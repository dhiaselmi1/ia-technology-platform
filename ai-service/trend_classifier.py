"""
Classification de tendances des domaines de recherche.

Pipeline ML :
  1. Extraction de features temporelles par domaine
  2. Génération d'un dataset d'entraînement synthétique couvrant les 3 classes
  3. Entraînement Random Forest Classifier
  4. Prédiction sur les domaines réels
  5. Métriques : accuracy CV, precision, recall, f1, feature importance

Modèle : Random Forest (scikit-learn)
Features : 5 caractéristiques temporelles par domaine
Classes : rising (en hausse), stable, declining (en baisse)
"""
from __future__ import annotations

import numpy as np
from collections import defaultdict
from sklearn.ensemble import RandomForestClassifier
from sklearn.preprocessing import StandardScaler, LabelEncoder
from sklearn.model_selection import cross_val_score, cross_val_predict
from sklearn.metrics import classification_report, confusion_matrix


FEATURE_NAMES = [
    "recent_ratio",
    "momentum",
    "recency_score",
    "publication_density",
    "acceleration",
]


def _extract_features(monthly_counts: dict[str, int], all_months: list[str], total: int) -> list[float]:
    n_months = len(all_months)
    if n_months == 0 or total == 0:
        return [0.5, 1.0, 0.5, 0.0, 0.0]

    third = max(1, n_months // 3)
    old_months = all_months[:third]
    mid_months = all_months[third:2*third]
    recent_months = all_months[2*third:]

    old_count = sum(monthly_counts.get(m, 0) for m in old_months)
    mid_count = sum(monthly_counts.get(m, 0) for m in mid_months)
    recent_count = sum(monthly_counts.get(m, 0) for m in recent_months)

    recent_ratio = recent_count / total if total > 0 else 0.33

    old_rate = old_count / len(old_months) if old_months else 0
    recent_rate = recent_count / len(recent_months) if recent_months else 0
    mid_rate = mid_count / len(mid_months) if mid_months else 0
    momentum = (recent_rate / old_rate) if old_rate > 0.01 else (2.0 if recent_count > 0 else 0.5)

    last_pub_month = max((m for m in all_months if monthly_counts.get(m, 0) > 0), default=all_months[-1])
    recency_score = (all_months.index(last_pub_month) + 1) / n_months

    density = total / n_months

    accel = (recent_rate - mid_rate) - (mid_rate - old_rate)

    return [
        round(recent_ratio, 4),
        round(min(momentum, 5.0), 4),
        round(recency_score, 4),
        round(density, 4),
        round(accel, 4),
    ]


def _generate_training_data(n_per_class: int = 80) -> tuple[np.ndarray, np.ndarray]:
    """Générer un dataset synthétique réaliste pour les 3 classes."""
    rng = np.random.RandomState(42)
    X_list = []
    y_list = []

    for _ in range(n_per_class):
        # rising: recent_ratio élevé, momentum > 1, recency haute
        X_list.append([
            rng.uniform(0.45, 0.80),   # recent_ratio
            rng.uniform(1.3, 4.0),     # momentum
            rng.uniform(0.75, 1.0),    # recency_score
            rng.uniform(0.1, 0.8),     # density
            rng.uniform(0.01, 0.3),    # acceleration
        ])
        y_list.append("rising")

    for _ in range(n_per_class):
        # stable: recent_ratio ~0.33, momentum ~1, densité quelconque
        X_list.append([
            rng.uniform(0.25, 0.50),
            rng.uniform(0.7, 1.4),
            rng.uniform(0.5, 0.9),
            rng.uniform(0.1, 0.6),
            rng.uniform(-0.1, 0.1),
        ])
        y_list.append("stable")

    for _ in range(n_per_class):
        # declining: recent_ratio bas, momentum < 1, recency basse
        X_list.append([
            rng.uniform(0.05, 0.35),
            rng.uniform(0.0, 0.8),
            rng.uniform(0.2, 0.7),
            rng.uniform(0.05, 0.5),
            rng.uniform(-0.3, -0.01),
        ])
        y_list.append("declining")

    return np.array(X_list), np.array(y_list)


def classify_trends(publications: list[dict]) -> dict:
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

    if len(monthly) < 2:
        return {"predictions": [], "model_info": {"error": "Not enough domains"}}

    all_months_set: set[str] = set()
    for counts in monthly.values():
        all_months_set.update(counts.keys())
    all_months = sorted(all_months_set)

    domains = list(monthly.keys())
    X_real = []
    for domain in domains:
        feats = _extract_features(monthly[domain], all_months, domain_totals[domain])
        X_real.append(feats)
    X_real = np.array(X_real, dtype=np.float64)

    X_train, y_train = _generate_training_data(n_per_class=80)

    le = LabelEncoder()
    y_encoded = le.fit_transform(y_train)

    scaler = StandardScaler()
    X_train_scaled = scaler.fit_transform(X_train)

    clf = RandomForestClassifier(
        n_estimators=150,
        max_depth=6,
        min_samples_split=5,
        min_samples_leaf=3,
        random_state=42,
        class_weight="balanced",
    )
    clf.fit(X_train_scaled, y_encoded)

    cv_scores = cross_val_score(clf, X_train_scaled, y_encoded, cv=5, scoring="accuracy")
    accuracy_cv = float(np.mean(cv_scores))
    y_pred_cv = cross_val_predict(clf, X_train_scaled, y_encoded, cv=5)
    cm = confusion_matrix(y_encoded, y_pred_cv).tolist()
    cr = classification_report(y_encoded, y_pred_cv, target_names=le.classes_, output_dict=True)

    importances = clf.feature_importances_
    feature_importance = [
        {"feature": FEATURE_NAMES[i], "importance": round(float(importances[i]), 4)}
        for i in np.argsort(importances)[::-1]
    ]

    X_real_scaled = scaler.transform(X_real)
    probas = clf.predict_proba(X_real_scaled)
    preds = clf.predict(X_real_scaled)

    predictions = []
    for i, domain in enumerate(domains):
        pred_label = le.inverse_transform([preds[i]])[0]
        class_probas = {}
        for j, cls in enumerate(le.classes_):
            class_probas[cls] = round(float(probas[i][j]), 4)

        feat_dict = {FEATURE_NAMES[k]: X_real[i][k] for k in range(len(FEATURE_NAMES))}

        predictions.append({
            "domain": domain,
            "trend": pred_label,
            "confidence": round(float(np.max(probas[i])), 4),
            "probabilities": class_probas,
            "features": feat_dict,
            "total_publications": domain_totals[domain],
        })

    predictions.sort(key=lambda x: x["probabilities"].get("rising", 0), reverse=True)

    per_class = {}
    for cls in le.classes_:
        if cls in cr:
            per_class[cls] = {
                "precision": round(cr[cls]["precision"], 4),
                "recall": round(cr[cls]["recall"], 4),
                "f1_score": round(cr[cls]["f1-score"], 4),
            }

    return {
        "predictions": predictions,
        "model_info": {
            "model": "Random Forest Classifier",
            "n_estimators": 150,
            "accuracy_cv": round(accuracy_cv, 4),
            "n_training_samples": len(X_train),
            "n_features": len(FEATURE_NAMES),
            "n_classes": len(le.classes_),
            "classes": le.classes_.tolist(),
            "feature_importance": feature_importance,
            "confusion_matrix": cm,
            "per_class_metrics": per_class,
        }
    }
