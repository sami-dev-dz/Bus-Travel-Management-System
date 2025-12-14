# 🚌 Bus Travel Management System

## Description du Projet

Ce projet est une application de bureau (Desktop Application) développée en **Java Swing** visant à automatiser la gestion complète des voyages et des réservations pour une compagnie de transport par bus.

Le système est conçu pour remplacer les processus manuels et offrir une plateforme centralisée pour l'administration des ressources et des transactions.

### 🎯 Fonctionnalités Clés

* **Gestion des Voyages :** Création et modification des voyages, association d'un bus et de multiples destinations (avec ordre).
* **Gestion des Réservations :** Enregistrement des Passagers, création, modification, et annulation des réservations, avec vérification de la disponibilité des places et du voyage.
* **Gestion du Parc :** Administration des Bus (immatriculation, capacité, marque, modèle).
* **Gestion des Utilisateurs & Rôles :**
    * **Administrateur** : Gestion complète du système (Bus, Destinations, Voyages, Comptes Réceptionnistes).
    * **Réceptionniste** : Opérations de Réservation et gestion des Passagers.
* **Sécurité :** Authentification sécurisée avec hachage des mots de passe (BCrypt).

## 🛠️ Technologies Utilisées

| Catégorie | Technologie | Rôle |
| :--- | :--- | :--- |
| **Langage Principal** | Java (JDK) | Programmation Orientée Objet (POO). |
| **Interface Utilisateur** | Java Swing | Construction de l'interface graphique (GUI). |
| **Base de Données** | SQLite | Base de données embarquée légère et portable. |
| **Accès aux Données** | JDBC | Connectivité standard pour interagir avec SQLite. |
| **Sécurité** | BCrypt (Hachage) | Sécurisation des mots de passe utilisateurs. |

## 📐 Architecture du Logiciel

Le projet utilise une architecture en couches bien définie, centrée sur le patron de conception **DAO (Data Access Object)**, garantissant la clarté et la maintenabilité du code. 

1.  **Model (Entités) :** Classes Java représentant les données métier (`Bus.java`, `Passager.java`, `Reservation.java`, etc.).
2.  **DAO (Data Access Object) :** Couche responsable de toutes les opérations de persistance (CRUD) vers la base de données SQLite via JDBC. (Exemple : `BusDAO.java`, `ReservationDAO.java`).
3.  **Panel/View :** Couche de présentation réalisée en Java Swing pour l'interaction utilisateur.

## ⚙️ Démarrage et Installation

### Prérequis

* Java Development Kit (JDK) 8 ou supérieur.
* Driver JDBC pour SQLite (nécessaire dans le classpath).
* IDE (IntelliJ IDEA, Eclipse).

### Étapes

1.  **Clonage du dépôt :**
    ```bash
    git clone [https://github.com/sami-dev-dz/Bus-Travel-Management-System.git](https://github.com/sami-dev-dz/Bus-Travel-Management-System.git)
    cd Bus-Travel-Management-System
    ```
2.  **Configuration de la Base de Données :** Le fichier de base de données SQLite doit être configuré pour être accessible via la classe de connexion (`DBConnection` dans le code).
3.  **Lancement :** Exécutez la classe principale du projet dans votre IDE.

---

### 📝 Conception et Documentation

Ce projet est soutenu par une documentation complète incluant :

* **Diagrammes UML** : Diagramme de Cas d'Utilisation, Diagramme de Classes. 
* **Modèles Relationnels** : Schéma des tables SQL pour la base de données SQLite.
* **Diagrammes de Séquence** : Modélisation des processus clés (Authentification, Création de Voyage, Création de Réservation).

## 🤝 Contribution

Pour toute suggestion ou amélioration, n'hésitez pas à ouvrir une *issue* ou à soumettre une *Pull Request*.

