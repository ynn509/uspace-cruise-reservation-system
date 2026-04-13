#language: fr

Fonctionnalité: Obtenir le manifeste des navettes d'urgence

  Scénario: cas nominal avec voyageurs et équipage, priorité RESCUE_SHIP
    Etant donné une croisière avec 255 voyageurs et 3 membres d'équipage
    Quand on récupère le manifeste des navettes d'urgence
    Alors le code de statut 200 est obtenu
    Et le manifeste contient 5 navettes de sauvetage et 1 navette standard
    Et les navettes de sauvetage sont prioritaires puis les navettes standard
    Et aucune navette vide n'est renvoyée
    Et le coût total des navettes est de 800000

  Scénario: manifeste vide retourne un coût nul
    Quand on récupère le manifeste des navettes d'urgence
    Alors le code de statut 200 est obtenu
    Et le manifeste est vide

  Scénario: croisière inexistante retourne 404
    Quand on récupère le manifeste des navettes d'urgence de la croisière "UNKNOWN_CRUISE"
    Alors le code de statut 404 est obtenu
    Et l'erreur "CRUISE_NOT_FOUND" est obtenue
