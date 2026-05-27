from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from keyword_extractor import extract_keywords
from recommender import get_recommendations

app = FastAPI(title="IA-Technology AI Service", version="2.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8080", "http://localhost:4200"],
    allow_methods=["*"],
    allow_headers=["*"],
)


# ── Request / Response models ──────────────────────────────────

class KeywordsRequest(BaseModel):
    text: str
    n: int = 10


class Publication(BaseModel):
    id: int
    text: str


class RecommendRequest(BaseModel):
    target_text: str
    corpus: list[Publication]
    top_n: int = 5


class SemanticSearchRequest(BaseModel):
    query: str
    corpus: list[Publication]
    top_n: int = 10


class ResearcherProfileRequest(BaseModel):
    researcher_id: int
    researcher_texts: list[str]
    all_researchers: list[dict]  # [{id, name, texts: [str]}]
    top_n: int = 3


class AskRequest(BaseModel):
    question: str
    corpus: list[Publication]
    top_n: int = 5


class SummarizeRequest(BaseModel):
    text: str
    mode: str = "tldr"  # "tldr" or "vulgarize"


# ── Original endpoints ─────────────────────────────────────────

@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/keywords")
def keywords(req: KeywordsRequest):
    if not req.text.strip():
        raise HTTPException(status_code=400, detail="Text cannot be empty")
    return {"keywords": extract_keywords(req.text, req.n)}


@app.post("/recommend")
def recommend(req: RecommendRequest):
    corpus = [{"id": p.id, "text": p.text} for p in req.corpus]
    results = get_recommendations(req.target_text, corpus, req.top_n)
    return {"recommendations": results}


# ── Semantic search (sentence-transformers) ────────────────────

@app.post("/semantic-search")
def semantic_search(req: SemanticSearchRequest):
    if not req.query.strip():
        raise HTTPException(status_code=400, detail="Query cannot be empty")

    from semantic_engine import encode, encode_corpus, cosine_similarities
    import numpy as np

    query_vec = encode(req.query)
    corpus_dicts = [{"id": p.id, "text": p.text} for p in req.corpus]
    if not corpus_dicts:
        return {"results": []}

    ids, matrix = encode_corpus(corpus_dicts)
    scores = cosine_similarities(query_vec, matrix)
    top_idx = np.argsort(scores)[::-1][: req.top_n]

    results = []
    for i in top_idx:
        s = float(scores[i])
        if s > 0.15:
            results.append({"id": ids[i], "score": round(s, 4)})
    return {"results": results}


# ── Researcher profile (keywords + similar researchers) ────────

@app.post("/researcher-profile")
def researcher_profile(req: ResearcherProfileRequest):
    from semantic_engine import encode, cosine_similarities
    import numpy as np

    combined = " ".join(req.researcher_texts)
    if not combined.strip():
        return {"keywords": [], "similar": []}

    kws = extract_keywords(combined, 8)

    target_vec = encode(combined)

    similar = []
    for other in req.all_researchers:
        if other["id"] == req.researcher_id:
            continue
        other_text = " ".join(other.get("texts", []))
        if not other_text.strip():
            continue
        other_vec = encode(other_text)
        score = float(np.dot(target_vec, other_vec))
        if score > 0.2:
            similar.append({
                "id": other["id"],
                "name": other.get("name", ""),
                "domain": other.get("domain", ""),
                "score": round(score, 4),
            })

    similar.sort(key=lambda x: x["score"], reverse=True)
    return {"keywords": kws, "similar": similar[: req.top_n]}


# ── Q&A / RAG-lite ─────────────────────────────────────────────

@app.post("/ask")
def ask(req: AskRequest):
    if not req.question.strip():
        raise HTTPException(status_code=400, detail="Question cannot be empty")

    from semantic_engine import encode, encode_corpus, cosine_similarities
    import numpy as np

    query_vec = encode(req.question)
    corpus_dicts = [{"id": p.id, "text": p.text} for p in req.corpus]
    if not corpus_dicts:
        return {"results": []}

    ids, matrix = encode_corpus(corpus_dicts)
    scores = cosine_similarities(query_vec, matrix)
    top_idx = np.argsort(scores)[::-1][: req.top_n]

    results = []
    for i in top_idx:
        s = float(scores[i])
        if s < 0.2:
            continue
        text = corpus_dicts[i]["text"]
        sentences = [sent.strip() for sent in text.replace("\n", " ").split(".") if len(sent.strip()) > 20]
        best_sentence = ""
        if sentences:
            sent_vecs = [encode(sent) for sent in sentences]
            sent_scores = [float(np.dot(query_vec, sv)) for sv in sent_vecs]
            best_idx = int(np.argmax(sent_scores))
            best_sentence = sentences[best_idx] + "."
        results.append({
            "id": ids[i],
            "score": round(s, 4),
            "answer": best_sentence,
        })
    return {"results": results}


# ── Generative summarization (Groq) ───────────────────────────

@app.post("/summarize")
def summarize(req: SummarizeRequest):
    if not req.text.strip():
        raise HTTPException(status_code=400, detail="Text cannot be empty")

    try:
        from generative import summarize_tldr, vulgarize

        if req.mode == "vulgarize":
            result = vulgarize(req.text)
        else:
            result = summarize_tldr(req.text)

        return {"summary": result, "mode": req.mode}

    except RuntimeError as e:
        raise HTTPException(status_code=503, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Generation failed: {str(e)}")


# ── Classification & Prediction ──────────────────────────────

class ClassifyRequest(BaseModel):
    text: str
    corpus: list[dict]  # [{"id": int, "text": str, "domain": str}]
    top_n: int = 3


class TrendRequest(BaseModel):
    publications: list[dict]  # [{"domain": str, "date": str}]


@app.post("/classify")
def classify(req: ClassifyRequest):
    if not req.text.strip():
        raise HTTPException(status_code=400, detail="Text cannot be empty")
    from classifier import classify_domain
    return classify_domain(req.text, req.corpus, req.top_n)


@app.post("/predict-trends")
def predict_trends(req: TrendRequest):
    if not req.publications:
        raise HTTPException(status_code=400, detail="Publications list cannot be empty")
    from trend_classifier import classify_trends
    return classify_trends(req.publications)
