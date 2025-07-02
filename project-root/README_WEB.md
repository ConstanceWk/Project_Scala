# 📚 Système de Gestion de Bibliothèque - Interface Web

Ce projet est une transformation de l'application console LibraryApp en une interface web moderne avec API REST.

## 🚀 Fonctionnalités

L'interface web offre les mêmes fonctionnalités que l'application console originale :

- **Recherche de livres** par titre
- **Emprunt de livres** avec validation de disponibilité
- **Retour de livres** avec mise à jour de l'état
- **Recommandations personnalisées** basées sur l'historique d'emprunt
- **Consultation des livres disponibles**
- **Statistiques en temps réel** (nombre de livres, utilisateurs, transactions)

## 🏗️ Architecture

### Backend (Scala)
- **LibraryServer.scala** : Serveur HTTP avec API REST (Akka HTTP)
- **LibraryCatalog.scala** : Logique métier (identique à l'original)
- **Models** : Book, User, Transaction (identiques à l'original)
- **JsonIO** : Persistance des données (identique à l'original)

### Frontend (HTML/CSS/JavaScript)
- **index.html** : Interface utilisateur moderne et responsive
- **app.js** : Logique client et communication avec l'API
- **Design moderne** : CSS3 avec gradients, animations et interface responsive

## 🔧 Utilisation

### Démarrage du serveur

```bash
cd project-root
sbt run
```

Le serveur se lance sur http://localhost:8080

### Interface utilisateur

1. **Identification** : Entrez votre ID utilisateur
2. **Recherche** : Tapez un titre de livre dans la barre de recherche
3. **Actions disponibles** :
   - Cliquer sur "Livres Disponibles"
   - Cliquer sur "Mes Recommandations" (nécessite un ID utilisateur)
   - Cliquer sur "Tous les Livres"
4. **Emprunts/Retours** : Utilisez les boutons sur chaque livre

### API REST

L'API REST est disponible sur `/api` avec les endpoints suivants :

- `GET /api/books` - Liste tous les livres
- `GET /api/books/available` - Liste les livres disponibles
- `POST /api/books/search` - Recherche de livres
- `POST /api/books/loan` - Emprunter un livre
- `POST /api/books/return` - Retourner un livre
- `GET /api/users/{userId}/recommendations` - Recommandations
- `GET /api/users` - Liste des utilisateurs
- `GET /api/transactions` - Liste des transactions

## 📊 Avantages de l'interface web

### Par rapport à l'application console :

1. **Interface moderne** : Design attrayant et professionnel
2. **Facilité d'utilisation** : Navigation intuitive avec boutons et formulaires
3. **Temps réel** : Mise à jour automatique des statistiques
4. **Accessibilité** : Utilisable depuis n'importe quel navigateur
5. **Responsive** : S'adapte aux mobiles et tablettes
6. **Recherche en temps réel** : Recherche au fur et à mesure de la saisie
7. **Feedback visuel** : Alertes de succès/erreur, animations
8. **Multi-utilisateur** : Plusieurs utilisateurs peuvent accéder simultanément

### Fonctionnalités améliorées :

- **Statistiques en temps réel** : Compteurs visuels en haut de page
- **Recherche intelligente** : Recherche automatique après 3 caractères
- **Interface graphique** : Cartes de livres avec informations détaillées
- **Gestion des erreurs** : Messages d'erreur clairs et informatifs
- **Performance** : Chargement asynchrone des données

## 🔄 Comparaison Console vs Web

| Fonctionnalité | Console | Web |
|----------------|---------|-----|
| Recherche | Saisie manuelle | Temps réel + suggestions |
| Navigation | Menu textuel | Interface graphique |
| Affichage | Texte simple | Cartes visuelles avec statut |
| Statistiques | Aucune | Dashboard en temps réel |
| Multi-utilisateur | Non | Oui (concurrent) |
| Accessibilité | Terminal uniquement | Navigateur web |
| Responsive | Non | Oui (mobile/desktop) |

## 🎯 Points clés de l'implémentation

1. **Conservation de la logique métier** : LibraryCatalog reste identique
2. **API REST complète** : Tous les endpoints nécessaires
3. **Gestion d'état** : Rechargement automatique après modifications
4. **Validation côté client** : Vérifications avant envoi des requêtes
5. **Design moderne** : Interface professionnelle avec animations

L'interface web transforme l'expérience utilisateur tout en conservant la robustesse et la fiabilité de la logique métier originale.
