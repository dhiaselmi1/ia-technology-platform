from sklearn.feature_extraction.text import TfidfVectorizer
import numpy as np


def extract_keywords(text: str, n: int = 10) -> list[str]:
    if not text or not text.strip():
        return []

    vectorizer = TfidfVectorizer(
        stop_words='english',
        max_features=1000,
        ngram_range=(1, 2)
    )

    try:
        tfidf_matrix = vectorizer.fit_transform([text])
        feature_names = vectorizer.get_feature_names_out()
        scores = tfidf_matrix.toarray()[0]
        top_indices = np.argsort(scores)[::-1][:n]
        return [feature_names[i] for i in top_indices if scores[i] > 0]
    except Exception:
        return []
