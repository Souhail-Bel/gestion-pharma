DROP DATABASE IF EXISTS Pharmacie;
CREATE DATABASE Pharmacie;
USE Pharmacie;


DROP USER IF EXISTS 'pharma_user'@'localhost';
CREATE USER 'pharma_user'@'localhost' IDENTIFIED BY 'securepass';
GRANT ALL PRIVILEGES ON Pharmacie.* TO 'pharma_user'@'localhost';
FLUSH PRIVILEGES;



CREATE TABLE EMPLOYE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50),
    prenom VARCHAR(50),
    username VARCHAR(50) UNIQUE,
    password VARCHAR(255),
    role ENUM('ADMIN','EMPLOYE')
);



CREATE TABLE PRODUIT (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100),
    prixVente DOUBLE,
    seuilMinimal INT
);


CREATE TABLE STOCK (
    produit_id INT PRIMARY KEY,
    quantiteDisponible INT,
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(id)
);



CREATE TABLE FOURNISSEUR (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100),
    telephone VARCHAR(100),
    email VARCHAR(100),
    adresse VARCHAR(255) DEFAULT ''
);


CREATE TABLE COMMANDE_FOURNISSEUR (
    id INT PRIMARY KEY AUTO_INCREMENT,
    fournisseur_id INT,
    dateCommande DATETIME,
    statut ENUM('CREATED', 'MODIFIED', 'CANCELED', 'RECEIVED'),
    FOREIGN KEY (fournisseur_id) REFERENCES FOURNISSEUR(id)
);


CREATE TABLE LIGNE_COMMANDE_FOURNISSEUR (
    commande_id INT,
    produit_id INT,
    quantite INT,
    prixAchat DOUBLE,
    PRIMARY KEY (commande_id, produit_id),
    FOREIGN KEY (commande_id) REFERENCES COMMANDE_FOURNISSEUR(id),
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(id)
);



CREATE TABLE CLIENT (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100),
    prenom VARCHAR(100),
    telephone VARCHAR(100)
);


CREATE TABLE VENTE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    client_id INT,
    employe_id INT,
    dateVente DATETIME,
    total DOUBLE,
    FOREIGN KEY (client_id) REFERENCES CLIENT(id),
    FOREIGN KEY (employe_id) REFERENCES EMPLOYE(id)
);

CREATE TABLE LIGNE_VENTE (
    id INT PRIMARY KEY AUTO_INCREMENT,
    vente_id INT,
    produit_id INT,
    quantite INT,
    prixUnitaire DOUBLE,
    FOREIGN KEY (vente_id) REFERENCES VENTE(id),
    FOREIGN KEY (produit_id) REFERENCES PRODUIT(id)
);




INSERT INTO EMPLOYE (nom, prenom, username, password, role) VALUES
('admin', 'admin', 'admin', SHA2('admin', 256), 'ADMIN'),
('akrem', 'medimagh', 'lost4onin', SHA2('akrem123', 256), 'ADMIN'),
('souhail', 'belhassen', 'he', SHA2('he123', 256), 'EMPLOYE'),
('elleuch', 'ahmed', 'nest', SHA2('nest123', 256), 'EMPLOYE'),
('bhouri', 'adam', 'hyper', SHA2('hyper123', 256), 'EMPLOYE'),
('gadhgadhi', 'aziz', 'gadh', SHA2('gadh123', 256), 'EMPLOYE');



INSERT INTO PRODUIT (nom, prixVente, seuilMinimal) VALUES
('Paracetamol', 1.5, 10),
('Ibuprofen', 2.0, 15),
('Aspirin', 1.2, 20),
('Amoxicillin', 3.0, 5),
('Cough Syrup', 4.5, 8);



INSERT INTO STOCK (produit_id, quantiteDisponible) VALUES
(1, 50), (2, 30), (3, 20), (4, 10), (5, 15);


INSERT INTO FOURNISSEUR (nom, telephone, email) VALUES
('PharmaSupply Co.', '20202020', 'fournisseur@ieee.org');
INSERT INTO FOURNISSEUR (nom, telephone, email, adresse) VALUES
('Goodies :D', '95959595', 'walter.white@insat.ucar.tn', 'Land of Freedom');


INSERT INTO COMMANDE_FOURNISSEUR (fournisseur_id, dateCommande, statut) VALUES
(1, '2025-06-01', 'CREATED'),
(2, '2026-12-13', 'CANCELED');



INSERT INTO LIGNE_COMMANDE_FOURNISSEUR (commande_id, produit_id, quantite, prixAchat) VALUES
(1, 1, 100, 1.0),
(1, 2, 200, 1.5),
(2, 3, 2000, 0.5);



INSERT INTO CLIENT (nom, prenom, telephone) VALUES
('Mohsen', 'Ahmed', '20521234'),
('Torvalds', 'Linus', '42069690');



INSERT INTO VENTE (client_id, employe_id, dateVente, total) VALUES
(1, 2, '2024-06-10 10:00:00', 13.5),
(2, 4, '2024-06-11 11:30:00', 12.0);



INSERT INTO LIGNE_VENTE (vente_id, produit_id, quantite, prixUnitaire) VALUES
(1, 1, 5, 1.5),
(1, 2, 3, 2.0),
(2, 3, 10, 1.2);
