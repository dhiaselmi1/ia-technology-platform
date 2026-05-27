"""
Génération de texte via Groq (modèle Llama 3.1 8B Instant par défaut).
Deux modes :
  - tldr      : résumé technique en 2-3 phrases (anglais ou langue source)
  - vulgarize : explication simple en français pour le grand public

La clé API est lue depuis la variable d'environnement GROQ_API_KEY (chargée depuis .env).
"""
from __future__ import annotations

import os
from functools import lru_cache

from dotenv import load_dotenv

load_dotenv()


@lru_cache(maxsize=1)
def get_client():
    api_key = os.getenv("GROQ_API_KEY")
    if not api_key:
        raise RuntimeError("GROQ_API_KEY not set. Create ai-service/.env with the key.")
    from groq import Groq
    return Groq(api_key=api_key)


def _model() -> str:
    return os.getenv("GROQ_MODEL", "llama-3.1-8b-instant")


_TLDR_SYSTEM = (
    "You are a scientific summarization assistant. "
    "Given the abstract of a research paper, produce a TL;DR in 2 to 3 short sentences. "
    "Keep the technical vocabulary. No preamble, no markdown, just the sentences. "
    "Match the language of the input."
)

_VULGARIZE_SYSTEM = (
    "Tu es un vulgarisateur scientifique. "
    "On te donne le résumé d'un article de recherche. "
    "Réécris-le en français simple, accessible à un lycéen, en 3 à 4 phrases courtes. "
    "Évite les termes techniques sans les expliquer. Pas de markdown, juste un paragraphe."
)


def _generate(system_prompt: str, user_text: str, max_tokens: int = 220) -> str:
    if not user_text or not user_text.strip():
        return ""
    client = get_client()
    resp = client.chat.completions.create(
        model=_model(),
        messages=[
            {"role": "system", "content": system_prompt},
            {"role": "user", "content": user_text.strip()},
        ],
        temperature=0.3,
        max_tokens=max_tokens,
    )
    return (resp.choices[0].message.content or "").strip()


def summarize_tldr(text: str) -> str:
    return _generate(_TLDR_SYSTEM, text, max_tokens=180)


def vulgarize(text: str) -> str:
    return _generate(_VULGARIZE_SYSTEM, text, max_tokens=260)
