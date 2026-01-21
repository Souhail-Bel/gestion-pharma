drop database if exists Pharmacie;
create database Pharmacie;
use Pharmacie;
create table EMPLOYE (
            id INT primary key AUTO_INCREMENT,
            nom VARCHAR(50),
            prenom VARCHAR(50),
            username VARCHAR(50) UNIQUE,
            password VARCHAR(255),
            role ENUM('ADMIN','EMPLOYE')
        );
insert into EMPLOYE (nom, prenom, username, password, role) values
('akrem', 'medimagh', 'lost4onin', 'akrem123', 'ADMIN'),
('souhail', 'belhassen', 'he', 'he123', 'EMPLOYE'),
('elleuch', 'ahmed', 'nest', 'nest123', 'EMPLOYE'),
('bhouri', 'adam', 'hyper', 'hyper123', 'EMPLOYE'),
('gadhgadhi', 'aziz', 'gadh', 'gadh123', 'EMPLOYE');
create table produit(
    id INT primary key AUTO_INCREMENT,
    nom VARCHAR(100),
    prixVente double,
    seuilMinimal INT
);
insert into produit (id,nom, prixVente, seuilMinimal) values
(1,'Paracetamol', 1.5, 10),
(2,'Ibuprofen', 2.0, 15),
(3,'Aspirin', 1.2, 20),
(4,'Amoxicillin', 3.0, 5),
(5,'Cough Syrup', 4.5, 8);

create table stock(
    produit_id INT primary key,
    quantiteDisponible INT,
    FOREIGN KEY (produit_id) REFERENCES produit(id)
);
insert into stock (produit_id,quantiteDisponible) values
(1, 50),
(2, 30),
(3, 20),
(4, 10),
(5, 15);
create table fournisseur(
    id INT primary key AUTO_INCREMENT,
    nom VARCHAR(100),
    telephone VARCHAR(100),
    email VARCHAR(100)
);
insert into fournisseur (nom, telephone, email) values
('PharmaSupply Co.', '123-456-7890', 'fournisseur@ieee.org');
create table CommandeFournisseur(
    id INT primary key AUTO_INCREMENT,
    fournisseur_id INT,
    dateCommande DATE,
    statut ENUM('EN_ATTENTE','RECU','ANNULE'),
    FOREIGN KEY (fournisseur_id) REFERENCES fournisseur(id)
);
insert into CommandeFournisseur (id,fournisseur_id, dateCommande, statut) values
(0,1, '2024-06-01', 'EN_ATTENTE');

create table LigneCommandeFournisseur(
    commande_id INT,
    produit_id INT,
    quantite INT,
    prixAchat double,
    PRIMARY KEY (commande_id, produit_id),
    FOREIGN KEY (commande_id) REFERENCES CommandeFournisseur(id),
    FOREIGN KEY (produit_id) REFERENCES produit(id)
);
insert into LigneCommandeFournisseur (commande_id, produit_id, quantite, prixAchat) values
(1, 1, 100, 1.0),
(1, 2, 200, 1.5);
create table client(
    id INT primary key AUTO_INCREMENT,
    nom VARCHAR(100),
    prenom VARCHAR(100),
    telephone VARCHAR(100)
);
insert into client (id,nom, prenom, telephone) values
(1,'Doe', 'John', '555-1234'),
(2,'Smith', 'Jane', '555-5678');
create table vente(
    id INT primary key AUTO_INCREMENT,
    client_id INT,
    employe_id INT,
    dateVente DATE,
    total double,
    FOREIGN KEY (client_id) REFERENCES client(id),
    FOREIGN KEY (employe_id) REFERENCES EMPLOYE(id)
);
insert into vente (client_id, employe_id, dateVente, total) values
(1, 2, '2024-06-10', 15.0),
(2, 3, '2024-06-11', 25.0);
create table LigneVente(
    id INT primary key AUTO_INCREMENT,
    vente_id INT,
    produit_id INT,
    quantite INT,
    prixUnitaire double,
    FOREIGN KEY (vente_id) REFERENCES vente(id),
    FOREIGN KEY (produit_id) REFERENCES produit(id)
);
insert into LigneVente (vente_id, produit_id, quantite, prixUnitaire) values
(1, 1, 5, 1.5),
(1, 2, 3, 2.0),
(2, 3, 10, 1.2);

