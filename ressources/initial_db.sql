CREATE TABLE IF NOT EXISTS Utilisateur (
    idUtilisateur INTEGER PRIMARY KEY AUTOINCREMENT, 
    nom TEXT NOT NULL, 
    prenom TEXT NOT NULL, 
    email TEXT UNIQUE NOT NULL, 
    motDePasse TEXT NOT NULL, 
    role TEXT NOT NULL CHECK (role IN ('Administrateur', 'Réceptionniste')) 
);

-- Insérer admin par défaut
INSERT OR IGNORE INTO Utilisateur (idUtilisateur, nom, prenom, email, motDePasse, role)
VALUES (
    1,
    'Al-Farouq',
    'Ahmed',
    'ahmed@gmail.com',
    '$2a$12$avl6u/b2kLGWMdx3rARLTOlxD23r1NF.IbC1q1JeHi6V7UCCvS7k2',
    'Administrateur'
);

CREATE TABLE IF NOT EXISTS Administrateur (
    idAdministrateur INTEGER PRIMARY KEY,
    FOREIGN KEY (idAdministrateur) REFERENCES Utilisateur(idUtilisateur)
        ON DELETE CASCADE 
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS Receptionniste (
    idReceptionniste INTEGER PRIMARY KEY, 
    FOREIGN KEY (idReceptionniste) REFERENCES Utilisateur(idUtilisateur)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS Bus (
    idBus INTEGER PRIMARY KEY AUTOINCREMENT, 
    immatriculation TEXT UNIQUE NOT NULL, 
    marque TEXT NOT NULL,
    modele TEXT NOT NULL,
    nbPlaces INTEGER NOT NULL CHECK (nbPlaces > 0),
    idAdministrateur INTEGER NOT NULL,
    FOREIGN KEY (idAdministrateur) REFERENCES Administrateur(idAdministrateur)
        ON DELETE RESTRICT 
        ON UPDATE CASCADE
);

INSERT OR IGNORE INTO Bus (idBus, immatriculation, marque, modele, nbPlaces)
VALUES (
    5,
    '8883 35 06',
    'mercedes',
    'ing',
    '12A'
);

CREATE TABLE IF NOT EXISTS Destination (
    idDestination INTEGER PRIMARY KEY AUTOINCREMENT,
    codeDestination TEXT UNIQUE NOT NULL, 
    nomVille TEXT NOT NULL,
    idAdministrateur INTEGER NOT NULL, 
    FOREIGN KEY (idAdministrateur) REFERENCES Administrateur(idAdministrateur)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS Voyage (
    idVoyage INTEGER PRIMARY KEY AUTOINCREMENT,
    code TEXT UNIQUE NOT NULL, 
    dateDepart DATE NOT NULL,
    heureDepart TEXT,
    duree INTEGER NOT NULL CHECK (duree > 0),
    idBus INTEGER NOT NULL, 
    idAdministrateur INTEGER NOT NULL, 
    FOREIGN KEY (idBus) REFERENCES Bus(idBus)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    FOREIGN KEY (idAdministrateur) REFERENCES Administrateur(idAdministrateur)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);


CREATE TABLE IF NOT EXISTS voyageDestination (
    idDestination INTEGER NOT NULL, 
    idVoyage INTEGER NOT NULL, 
    PRIMARY KEY (idDestination, idVoyage), 
    FOREIGN KEY (idDestination) REFERENCES Destination(idDestination)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (idVoyage) REFERENCES Voyage(idVoyage)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);
ALTER TABLE VoyageDestination ADD COLUMN ordre INTEGER DEFAULT 1;

CREATE TABLE IF NOT EXISTS Passager (
    idPassager INTEGER PRIMARY KEY AUTOINCREMENT,
    nom TEXT NOT NULL,
    prenom TEXT NOT NULL,
    adresse TEXT NOT NULL,
    telephone TEXT NOT NULL UNIQUE 
);

CREATE TABLE IF NOT EXISTS Reservation (
    idReservation INTEGER PRIMARY KEY AUTOINCREMENT,
    dateReservation DATE NOT NULL DEFAULT (date('now')), 
    statut TEXT NOT NULL DEFAULT 'En attente' CHECK (statut IN ('En attente', 'Confirmée', 'Annulée')),
    idVoyage INTEGER NOT NULL, 
    idPassager INTEGER NOT NULL, 
    idReceptionniste INTEGER NOT NULL, 
    FOREIGN KEY (idVoyage) REFERENCES Voyage(idVoyage)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    FOREIGN KEY (idPassager) REFERENCES Passager(idPassager)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (idReceptionniste) REFERENCES Receptionniste(idReceptionniste)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);
ALTER TABLE Reservation ADD COLUMN nbPlaces INTEGER NOT NULL DEFAULT 1 CHECK (nbPlaces > 0);


CREATE INDEX IF NOT EXISTS idx_utilisateur_email ON Utilisateur(email);
CREATE INDEX IF NOT EXISTS idx_utilisateur_role ON Utilisateur(role);
CREATE INDEX IF NOT EXISTS idx_bus_immatriculation ON Bus(immatriculation);
CREATE INDEX IF NOT EXISTS idx_destination_code ON Destination(codeDestination);
CREATE INDEX IF NOT EXISTS idx_voyage_code ON Voyage(code);
CREATE INDEX IF NOT EXISTS idx_voyage_date ON Voyage(dateDepart);
CREATE INDEX IF NOT EXISTS idx_reservation_voyage ON Reservation(idVoyage);
CREATE INDEX IF NOT EXISTS idx_reservation_passager ON Reservation(idPassager);
CREATE INDEX IF NOT EXISTS idx_passager_telephone ON Passager(telephone);
