"""
Moteur sémantique partagé : encodage via sentence-transformers, cache d'embeddings
indexé par (id, hash_du_texte) pour ne ré-encoder que ce qui a changé.

Réutilisé par : semantic search, recommandations, profil chercheur, Q&A (RAG-lite).
"""
from __future__ import annotations

import hashlib
import threading
from typing import Iterable

import numpy as np

_MODEL_NAME = "sentence-transformers/all-MiniLM-L6-v2"  # ~80 MB, 384 dims, multilingue OK
_model = None
_model_lock = threading.Lock()
_cache: dict[tuple[int, str], np.ndarray] = {}


def _hash(text: str) -> str:
    return hashlib.sha1((text or "").encode("utf-8")).hexdigest()[:16]


def get_model():
    """Lazy-load : le modèle (~80 MB) ne se télécharge/charge qu'au 1er appel."""
    global _model
    if _model is not None:
        return _model
    with _model_lock:
        if _model is None:
            from sentence_transformers import SentenceTransformer
            _model = SentenceTransformer(_MODEL_NAME)
    return _model


def encode(text: str) -> np.ndarray:
    if not text or not text.strip():
        return np.zeros(384, dtype=np.float32)
    vec = get_model().encode([text], convert_to_numpy=True, normalize_embeddings=True)[0]
    return vec.astype(np.float32)


def encode_corpus(corpus: Iterable[dict]) -> tuple[list[int], np.ndarray]:
    """
    corpus : itérable de dict {"id": int, "text": str}
    Retourne (ids, matrix [N x 384]) avec embeddings normalisés (cosine = produit scalaire).
    Cache par (id, hash_texte) pour ne ré-encoder que ce qui a changé.
    """
    ids: list[int] = []
    to_encode: list[str] = []
    cached_vecs: dict[int, np.ndarray] = {}

    for item in corpus:
        pid = int(item["id"])
        text = item.get("text", "") or ""
        key = (pid, _hash(text))
        ids.append(pid)
        if key in _cache:
            cached_vecs[pid] = _cache[key]
        else:
            to_encode.append((pid, text, key))

    if to_encode:
        texts = [t for _, t, _ in to_encode]
        new_vecs = get_model().encode(texts, convert_to_numpy=True,
                                       normalize_embeddings=True, batch_size=32)
        for (pid, _, key), vec in zip(to_encode, new_vecs):
            v = vec.astype(np.float32)
            _cache[key] = v
            cached_vecs[pid] = v

    matrix = np.stack([cached_vecs[pid] for pid in ids], axis=0)
    return ids, matrix


def cosine_similarities(query_vec: np.ndarray, matrix: np.ndarray) -> np.ndarray:
    """Vecteurs déjà normalisés → produit scalaire = cosine similarity."""
    if matrix.size == 0:
        return np.array([])
    return matrix @ query_vec
