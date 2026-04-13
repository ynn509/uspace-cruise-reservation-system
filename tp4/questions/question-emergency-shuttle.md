# Stratégie d'essais : Obtenir le manifeste des navettes d'urgence

## TESTS FONCTIONS:
[Liste des tests unitaires]
Affecter voyageurs et équipage remplit d'abord les RESCUE_SHIP puis les STANDARD_SHUTTLE : Priorité et remplissage dans l'allocator avec config mockée, aucun I/O, donc unitaire.
Affecter plus de passagers que la capacité d'un RESCUE_SHIP ouvre une nouvelle navette seulement après remplissage complet : Règle « dernière navette pleine avant ouverture » testée en isolation, donc unitaire.
Limiter le nombre de RESCUE_SHIP bascule l'excédent vers les STANDARD_SHUTTLE : Contrôle du quota par type via specs stub, donc unitaire.
Inclure voyageurs et équipage réserve bien des places : Vérifie l'embarquement complet des occupants sans dépendance externe, donc unitaire.
Calculer le coût total ne somme que les navettes utilisées : Calcul sur objets métier seuls, donc unitaire.
Exclure les navettes vides du manifeste : Filtre des navettes sans occupants au niveau domaine, donc unitaire.

## TESTS COMPOSANT:
[Liste des tests d'intégration]
GET /cruises/{cruiseId}/emergencyShuttles (croisière existante) retourne 200 avec manifest priorisé et coût attendu : Parcourt ressource JAX-RS + service + config + repo mémoire, vérifie HTTP et JSON, donc composant.
GET /cruises/{cruiseId}/emergencyShuttles (croisière sans occupants) retourne 200 et manifeste vide : Traverse API/service/repo et valide la réponse vide, donc composant.
GET /cruises/{cruiseId}/emergencyShuttles (croisière inexistante) retourne 404 CRUISE_NOT_FOUND : Vérifie mapper d'exception via l'appel HTTP complet, donc composant.

## TESTS MULTI-COMPOSANTS (Cucumber):
[Liste des tests comportementaux]
Scénario : croisière avec assez de passagers pour plusieurs navettes, priorité RESCUE_SHIP puis STANDARD_SHUTTLE : Démarre l'appli complète (API + services + repos + allocator) et vérifie ordre et remplissage via REST.
Scénario : pas de navettes vides dans le manifeste : Flux complet appli, contrôle que chaque navette retournée a des occupants.
Scénario : manifeste vide → bouton rouge (réponse API vide) : App complète, récupère un manifeste vide pour valider le comportement UI attendu.
Scénario : croisière inexistante → 404 : App complète, vérifie code/erreur via API + services + repos + mapper.
