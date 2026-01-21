DROP DATABASE IF EXISTS Pharmacie;
CREATE DATABASE Pharmacie;
USE Pharmacie;



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
    dateCommande DATE,
    statut VARCHAR(50),
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
('PharmaSupply Co.', '123-456-7890', 'fournisseur@ieee.org');



INSERT INTO COMMANDE_FOURNISSEUR (id, fournisseur_id, dateCommande, statut) VALUES
(1, 1, '2024-06-01', 'EN_ATTENTE');



INSERT INTO LIGNE_COMMANDE_FOURNISSEUR (commande_id, produit_id, quantite, prixAchat) VALUES
(1, 1, 100, 1.0),
(1, 2, 200, 1.5);



INSERT INTO CLIENT (nom, prenom, telephone) VALUES
('Doe', 'John', '20521234'),
('Smith', 'Jane', '95525678');



INSERT INTO VENTE (client_id, employe_id, dateVente, total) VALUES
(1, 2, '2024-06-10 10:00:00', 15.0),
(2, 3, '2024-06-11 11:30:00', 25.0);



INSERT INTO LIGNE_VENTE (vente_id, produit_id, quantite, prixUnitaire) VALUES
(1, 1, 5, 1.5),
(1, 2, 3, 2.0),
(2, 3, 10, 1.2);
