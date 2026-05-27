"""
Classification de tendances des domaines de recherche.

Pipeline ML complet :
  1. Extraction de features temporelles par domaine
  2. Labélisation automatique par règles (ground truth)
  3. Augmentation des données par bootstrap
  4. Entraînement Random Forest Classifier
  5. Évaluation : accuracy, precision, recall, f1, matrice de confusion, feature importance

Modèle : Random Forest (scikit-learn)
Features : 8 caractéristiques temporelles par domaine
Classes : rising (en hausse), stable, declining (en baisse)
"""
from __future__ import annotations

import numpy as np
from collections import defaultdict
from datetime import datetime, timedelta
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import StandardScaler, LabelEncoder
from sklearn.model_selection import cross_val_score, cross_val_predict
from sklearn.metrics import classification_report, confusion_matrix


FEATURE_NAMES = [
    "total_publications",
    "recent_6m_count",
    "older_count",
    "recent_ratio",
    "monthly_avg_rate",
    "recent_monthly_rate",
    "momentum",
    "recency_score",
]


def _extract_features(monthly_counts: dict[str, int], all_months: list[str], total: int) -> list[float]:
    """Extraire 8 features à partir des comptages mensuels d'un domaine."""
    n_months = len(all_months)
    if n_months == 0 or total == 0:
        return [0.0] * 8

    mid = max(1, n_months // 2)
    recent_months = all_months[mid:]
    older_months = all_months[:mid]

    recent_count = sum(monthly_counts.get(m, 0) for m in recent_months)
    older_count = sum(monthly_counts.get(m, 0) for m in older_months)
    recent_ratio = recent_count / total if total > 0 else 0
    monthly_avg = total / n_months if n_months > 0 else 0
    recent_monthly_rate = recent_count / len(recent_months) if recent_months else 0
    older_monthly_rate = older_count / len(older_months) if older_months else 0
    momentum = (recent_monthly_rate / older_monthly_rate) if older_monthly_rate > 0 else (2.0 if recent_count > 0 else 0.0)

    last_pub_month = max((m for m in all_months if monthly_counts.get(m, 0) > 0), default=all_months[-1])
    last_idx = all_months.index(last_pub_month)
    recency_score = (last_idx + 1) / n_months

    return [
        float(total),
        float(recent_count),
        float(older_count),
        round(recent_ratio, 4),
        round(monthly_avg, 4),
        round(recent_monthly_rate, 4),
        round(momentum, 4),
        round(recency_score, 4),
    ]


def _generate_label(features: list[float]) -> str:
    """Labéliser un domaine par règles basées sur ses features."""
    momentum = features[6]
    recent_ratio = features[3]
    recency = features[7]

    score = 0.0
    if momentum > 1.3:
        score += 2
    elif momentum > 1.0:
        score += 1
    elif momentum < 0.7:
        score -= 2
    elif momentum < 1.0:
        score -= 1

    if recent_ratio > 0.6:
        score += 1.5
    elif recent_ratio < 0.35:
        score -= 1.5

    if recency > 0.85:
        score += 1
    elif recency < 0.5:
        score -= 1

    if score >= 1.5:
        return "rising"
    elif score <= -1.5:
        return "declining"
    return "stable"


def _bootstrap_augment(X: np.ndarray, y: np.ndarray, n_augmented: int = 50) -> tuple[np.ndarray, np.ndarray]:
    """Augmenter le dataset par bootstrap avec bruit gaussien."""
    rng = np.random.RandomState(42)
    X_aug = [X]
    y_aug = [y]

    for _ in range(n_augmented):
        indices = rng.choice(len(X), size=len(X), replace=True)
        X_sample = X[indices].copy()
        noise = rng.normal(0, 0.1, size=X_sample.shape) * np.abs(X_sample)
        X_sample += noise
        X_sample = np.clip(X_sample, 0, None)
        X_aug.append(X_sample)
        y_aug.append(y[indices])

    return np.vstack(X_aug), np.concatenate(y_aug)


def classify_trends(publications: list[dict]) -> dict:
    """
    publications : [{"domain": str, "date": str (ISO or YYYY-MM)}, ...]

    Retourne {
        "predictions": [{"domain", "trend", "confidence", "features": {...}}],
        "model_info": {"accuracy_cv", "classification_report", "confusion_matrix", "feature_importance", ...}
    }
    """
    if not publications:
        return {"predictions": [], "model_info": {}}

    monthly: dict[str, dict[str, int]] = defaultdict(lambda: defaultdict(int))
    domain_totals: dict[str, int] = defaultdict(int)

    for pub in publications:
        domain = pub.get("domain", "")
        date_str = pub.get("date", "")
        if not domain or not date_str:
            continue
        month = date_str[:7]
        monthly[domain][month] += 1
        domain_totals[domain] += 1

    if len(monthly) < 3:
        return {"predictions": [], "model_info": {"error": "Not enough domains (need >= 3)"}}

    all_months_set: set[str] = set()
    for counts in monthly.values():
        all_months_set.update(counts.keys())
    all_months = sorted(all_months_set)

    domains = list(monthly.keys())
    X_raw = []
    labels = []
    for domain in domains:
        feats = _extract_features(monthly[domain], all_months, domain_totals[domain])
        X_raw.append(feats)
        labels.append(_generate_label(feats))

    X_original = np.array(X_raw, dtype=np.float64)
    le = LabelEncoder()
    y_original = le.fit_transform(labels)

    X_aug, y_aug = _bootstrap_augment(X_original, y_original, n_augmented=60)

    scaler = StandardScaler()
    X_scaled = scaler.fit_transform(X_aug)

    clf = RandomForestClassifier(
        n_estimators=100,
        max_depth=5,
        min_samples_split=3,
        min_samples_leaf=2,
        random_state=42,
        class_weight="balanced",
    )
    clf.fit(X_scaled, y_aug)

    cv_folds = min(5, len(X_aug))
    if cv_folds >= 2:
        cv_scores = cross_val_score(clf, X_scaled, y_aug, cv=cv_folds, scoring="accuracy")
        accuracy_cv = float(np.mean(cv_scores))
        y_pred_cv = cross_val_predict(clf, X_scaled, y_aug, cv=cv_folds)
        cm = confusion_matrix(y_aug, y_pred_cv).tolist()
        cr = classification_report(y_aug, y_pred_cv, target_names=le.classes_, output_dict=True)
    else:
        accuracy_cv = 0.0
        cm = []
        cr = {}

    importances = clf.feature_importances_
    feature_importance = [
        {"feature": FEATURE_NAMES[i], "importance": round(float(importances[i]), 4)}
        for i in np.argsort(importances)[::-1]
    ]

    X_orig_scaled = scaler.transform(X_original)
    probas = clf.predict_proba(X_orig_scaled)
    predictions_raw = clf.predict(X_orig_scaled)

    predictions = []
    for i, domain in enumerate(domains):
        pred_label = le.inverse_transform([predictions_raw[i]])[0]
        confidence = float(np.max(probas[i]))

        class_probas = {}
        for j, cls in enumerate(le.classes_):
            class_probas[cls] = round(float(probas[i][j]), 4)

        feat_dict = {FEATURE_NAMES[k]: X_raw[i][k] for k in range(len(FEATURE_NAMES))}

        predictions.append({
            "domain": domain,
            "trend": pred_label,
            "confidence": round(confidence, 4),
            "probabilities": class_probas,
            "features": feat_dict,
        })

    predictions.sort(key=lambda x: x["confidence"], reverse=True)

    per_class_metrics = {}
    for cls in le.classes_:
        if cls in cr:
            per_class_metrics[cls] = {
                "precision": round(cr[cls]["precision"], 4),
                "recall": round(cr[cls]["recall"], 4),
                "f1_score": round(cr[cls]["f1-score"], 4),
            }

    model_info = {
        "model": "Random Forest Classifier",
        "n_estimators": 100,
        "accuracy_cv": round(accuracy_cv, 4),
        "n_original_samples": len(domains),
        "n_augmented_samples": len(X_aug),
        "n_features": len(FEATURE_NAMES),
        "n_classes": len(le.classes_),
        "classes": le.classes_.tolist(),
        "feature_importance": feature_importance,
        "confusion_matrix": cm,
        "per_class_metrics": per_class_metrics,
    }

    return {"predictions": predictions, "model_info": model_info}
