"""
Classification de publications par domaine et prédiction de tendances.

- classify_domain : TF-IDF + Logistic Regression, entraîné dynamiquement sur le corpus existant.
  Retourne le top-3 des domaines prédits avec scores de confiance.

- predict_trends : régression linéaire sur les comptages mensuels par domaine,
  retourne un score de tendance (croissance/décroissance) pour chaque domaine.
"""
from __future__ import annotations

import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import cross_val_score
from collections import Counter, defaultdict


def classify_domain(
    text: str,
    corpus: list[dict],
    top_n: int = 3,
) -> dict:
    """
    corpus : [{"id": int, "text": str, "domain": str}, ...]
    Retourne {"predictions": [{"domain": str, "confidence": float}], "metrics": {...}}
    """
    if not text or not text.strip() or len(corpus) < 5:
        return {"predictions": [], "metrics": {"accuracy_cv": 0, "n_samples": len(corpus)}}

    texts = [item["text"] for item in corpus]
    labels = [item["domain"] for item in corpus]

    le = LabelEncoder()
    y = le.fit_transform(labels)

    vectorizer = TfidfVectorizer(
        stop_words="english",
        max_features=3000,
        ngram_range=(1, 2),
        sublinear_tf=True,
    )
    X = vectorizer.fit_transform(texts)

    clf = LogisticRegression(
        max_iter=1000,
        C=1.0,
        solver="lbfgs",
    )
    clf.fit(X, y)

    n_classes = len(le.classes_)
    cv_folds = min(5, min(Counter(y).values()), len(corpus))
    if cv_folds >= 2:
        cv_scores = cross_val_score(clf, X, y, cv=cv_folds, scoring="accuracy")
        accuracy_cv = float(np.mean(cv_scores))
    else:
        accuracy_cv = 0.0

    x_new = vectorizer.transform([text])
    probas = clf.predict_proba(x_new)[0]

    top_indices = np.argsort(probas)[::-1][:top_n]
    predictions = []
    for idx in top_indices:
        predictions.append({
            "domain": le.inverse_transform([idx])[0],
            "confidence": round(float(probas[idx]), 4),
        })

    return {
        "predictions": predictions,
        "metrics": {
            "accuracy_cv": round(accuracy_cv, 4),
            "n_samples": len(corpus),
            "n_classes": n_classes,
            "model": "TF-IDF + Logistic Regression",
        },
    }


def predict_trends(
    publications: list[dict],
) -> list[dict]:
    """
    publications : [{"domain": str, "date": str (YYYY-MM or ISO)}, ...]
    Retourne une liste de domaines avec score de tendance (slope),
    triée par tendance décroissante.
    """
    if not publications:
        return []

    monthly: dict[str, dict[str, int]] = defaultdict(lambda: defaultdict(int))
    all_months: set[str] = set()

    for pub in publications:
        domain = pub.get("domain", "")
        date_str = pub.get("date", "")
        if not domain or not date_str:
            continue
        month = date_str[:7]  # YYYY-MM
        monthly[domain][month] += 1
        all_months.add(month)

    if not all_months:
        return []

    sorted_months = sorted(all_months)
    month_to_idx = {m: i for i, m in enumerate(sorted_months)}

    results = []
    for domain, counts in monthly.items():
        xs = []
        ys = []
        for month, count in counts.items():
            xs.append(month_to_idx[month])
            ys.append(count)

        total = sum(ys)
        if len(xs) < 2:
            slope = 0.0
        else:
            x_arr = np.array(xs, dtype=float)
            y_arr = np.array(ys, dtype=float)
            x_mean = np.mean(x_arr)
            y_mean = np.mean(y_arr)
            numerator = np.sum((x_arr - x_mean) * (y_arr - y_mean))
            denominator = np.sum((x_arr - x_mean) ** 2)
            slope = float(numerator / denominator) if denominator != 0 else 0.0

        if slope > 0.05:
            trend = "rising"
        elif slope < -0.05:
            trend = "declining"
        else:
            trend = "stable"

        results.append({
            "domain": domain,
            "total_publications": total,
            "trend_slope": round(slope, 4),
            "trend": trend,
            "monthly_counts": {m: counts.get(m, 0) for m in sorted_months},
        })

    results.sort(key=lambda x: x["trend_slope"], reverse=True)
    return results
