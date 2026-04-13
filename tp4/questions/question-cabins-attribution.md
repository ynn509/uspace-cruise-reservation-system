# Scénarios de tests pour l'attribution de cabines

### 1. Élaborer la stratégie d'essais pour la fonctionnalité d'attribution de cabines

Pour faciliter la correction, suivez le gabarit ci-dessous.

- Expliquer pourquoi chaque test est dans sa catégorie.
- Expliquer pourquoi une catégorie est vide si c'est le cas.

**TESTS FONCTIONS:**

- **Tri par date de reservation**: Fonction qui trie une liste de Booking sans dépendance externe.
- **Tri par nombre de voyageurs puis par date en cas d'égalité**: Fonction avec double critère (nb voyageurs → date).
- **Attribution des cabines par catégorie**: DefaultCabinAttributionEngine est totalement isolé et ne fait que de la 
logique interne.
- **Validation du critère d’attribution**: CabinAttributionCriteriaValidator ne dépend que de fonctions.
- **Mise à jour du tableau de cabines après la réponse API**: mise à jour du state. On vérifie que cabins contient 
ce que l’API mockée retourne.
- **Gestion d’une réponse vide**: On vérifie que si l’API retourne [], le tableau du composant reste vide.
- **Gestion d’une erreur API**: On vérifie que le composant ne plante pas et que cabins reste vide.
- **Sélection du critère dans le menu déroulant**: mise à jour d’une variable interne. On vérifie que changer le "select" 
met bien criteria à "travelers" ou "bookingDateTime".

**TESTS COMPOSANT:**

- **Attribution selon bookingDateTime**: Ce test vérifie le service complet (CabinAttributionService) en simulant des 
réservations et en observant le résultat final. C’est un test composant parce qu’il couvre l’orchestration entre le tri
, l’engine d’attribution et l’assembler sans dépendre d’infrastructure réelle.
- **Attribution selon travelers**: Même logique que ci-dessus, mais avec un tri basé sur le nombre de voyageurs. 
C’est un test composant car il valide l’orchestration interne du service avec mocks.
- **Lecture de cabins.json à chaque requête**: Ce test s’assure que le service recharge les cabines à chaque appel, 
- donc pas de cache interne. C’est un test composant car il isole CabinRegistry en mockant tout le reste.
- **Gestion des erreurs (service + resource)**: Ce test valide que le composant API gère correctement les erreurs 
métier provenant du service.


**TESTS MULTI-COMPOSANTS (Cucumber):**

Pour cette fonctionnalité, les scénarios d’intégration sont déjà bien couverts par les tests Composant, 
qui vérifient l’orchestration complète entre le tri, l’engine d’attribution, le validator et l’assembler, ainsi que 
par les tests End-to-End, qui valident le comportement réel depuis l’interface jusqu’à l’API. Comme la logique est assez
simple et déjà testée en profondeur, ajouter des tests Cucumber n’apporterait pas vraiment plus de valeur et rendrait 
seulement la solution plus lourde sans raison.


**TESTS END-TO-END (Cypress):**

- L’utilisateur sélectionne le critère “bookingDateTime”, clique sur Get Cabin Attributions, 
et le tableau des cabines s’affiche avec le bon nombre d’entrées.
- L’utilisateur sélectionne le critère “travelers”, lance la requête, et les cabines apparaissent dans l’ordre prévu.
