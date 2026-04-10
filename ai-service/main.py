from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from keyword_extractor import extract_keywords
from recommender import get_recommendations

app = FastAPI(title="IA-Technology AI Service", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8080", "http://localhost:4200"],
    allow_methods=["*"],
    allow_headers=["*"],
)


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
