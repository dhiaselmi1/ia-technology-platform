from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity
import numpy as np


def get_recommendations(target_text: str, corpus: list[dict], top_n: int = 5) -> list[dict]:
    if not corpus or not target_text.strip():
        return []

    texts = [target_text] + [item.get("text", "") for item in corpus]

    vectorizer = TfidfVectorizer(stop_words='english', max_features=5000)
    tfidf_matrix = vectorizer.fit_transform(texts)

    similarities = cosine_similarity(tfidf_matrix[0:1], tfidf_matrix[1:]).flatten()
    top_indices = np.argsort(similarities)[::-1][:top_n]

    return [
        {**corpus[i], "similarity_score": round(float(similarities[i]), 4)}
        for i in top_indices
        if similarities[i] > 0.1
    ]
