use pg_commandorv2;

TRUNCATE TABLE ArretLigne;
TRUNCATE TABLE Composant;
TRUNCATE TABLE ComposantPIPO;
TRUNCATE TABLE ConteneurConsomme;
TRUNCATE TABLE ConteneurNavette;
TRUNCATE TABLE EncoursSaisi;
TRUNCATE TABLE ForcageCommande;
TRUNCATE TABLE FournisseurPanier;
TRUNCATE TABLE FournisseurPanierDetail;
TRUNCATE TABLE LigneComposant;
TRUNCATE TABLE LigneComposantFournisseur;
TRUNCATE TABLE Navette;
TRUNCATE TABLE Notification;
TRUNCATE TABLE OuvertureLigne;
TRUNCATE TABLE PoPacking;
TRUNCATE TABLE PoPackingComposant;
TRUNCATE TABLE ProduitFini;
TRUNCATE TABLE Session;

UPDATE User SET email = 'pg@acensi.fr';
UPDATE Fournisseur SET emailsCommande = 'pg@acensi.fr';

CREATE TABLE fournisseur_tmp
(
    id              bigint,
    libelle         varchar(255) ,
    actif           int NULL,
    code            varchar(50) ,
    delaiExpedition int DEFAULT 45  ,
    delaiCommande   int DEFAULT 180 ,
    delaiAlerte     int DEFAULT 180 ,
    emailsCommande  varchar(500)
);

CREATE TABLE Composant_tmp (
    id BIGINT,
    gcas VARCHAR(1000),
    libelle VARCHAR(1000),
    idConteneurType BIGINT,
    idComposantType BIGINT,
    qteParConteneur INT,
    idFournisseur BIGINT,
    stockSecu INT,
    stockSecuCommanderPar INT,
    idFournisseurStockSecu BIGINT
);

CREATE TABLE LigneComposant_tmp
(
    id                 BIGINT,
    idComposant        BIGINT,
    idLigne            BIGINT,
    pertes             FLOAT,
    encoursMin         INT,
    encoursMax         INT,
    bbAAnticiper       INT,
    porteSortie        VARCHAR(1000),
    nbParMinute        INT,
    stagingZone        VARCHAR(1000),
    archive            BIT,
    bbACommanderEnPlus INT
);

CREATE TABLE ProduitFini_tmp
(
    id                 BIGINT,
    gcas               VARCHAR(1000),
    libelle            VARCHAR(1000),
    idProduitFiniGamme INT,
    idTechnoBulk       INT
);

CREATE TABLE PoPacking_tmp
(
    id               BIGINT,
    idLigne          BIGINT,
    idProduitFini    BIGINT,
    horoDebPrev      DATETIME,
    horoFinPrev      DATETIME,
    horoDebAPI       DATETIME,
    horoFinAPI       DATETIME,
    numPo            VARCHAR(1000),
    quantiteProduite INT,
    quantitePrevue   INT,
    dureeChangeOver  DECIMAL(10, 3),
    ordre            INT
);

CREATE TABLE PoPackingComposant_tmp
(
    id                           BIGINT,
    idPoPacking                  BIGINT,
    idComposant                  BIGINT,
    quantiteConsoPrevue          INT,
    quantiteBesoinComplementaire INT
);

CREATE TABLE ArretLigne_tmp (
    id BIGINT,
    horoDebPrev DATETIME,
    horoFinPrev DATETIME,
    idLigne BIGINT,
    estSaisieManuelle INT,
    raison VARCHAR(1000),
    estArretTeamNeeded INT
);

CREATE TABLE ComposantPIPO_tmp
(
    id                   BIGINT,
    idComposantOrigine   BIGINT,
    idComposantSubstitut BIGINT,
    horoDeb              DATETIME,
    horoFin              DATETIME
);

CREATE TABLE LigneComposantFournisseur_tmp
(
    id               BIGINT,
    idLigneComposant BIGINT,
    archive          BIT,
    idFournisseur    BIGINT
);

CREATE TABLE FournisseurPanier_tmp
(
    id                 BIGINT,
    idFournisseur      BIGINT,
    jourHeureCommande  VARCHAR(1000),
    jourHeureReception VARCHAR(1000),
    idCluster          BIGINT
);

CREATE TABLE FournisseurPanierDetail_tmp
(
    id                  BIGINT,
    idComposant         BIGINT,
    idFournisseurPanier BIGINT,
    numero              INT,
    quantite            INT,
    idLigne             BIGINT
);

CREATE TABLE Navette_tmp (
    id BIGINT,
    idCluster BIGINT,
    numero INT,
    horoCalcul DATETIME,
    horoCommande DATETIME,
    horoExpedition DATETIME,
    horoReception DATETIME,
    codeStatut VARCHAR(20),
    numBL VARCHAR(20),
    idConteneurType BIGINT,
    raisonCommande VARCHAR(1000),
    emplacement VARCHAR(100),
    version INT DEFAULT 0,
    erreur nVARCHAR(4000),
    dateMailCommande DATETIME,
    idFournisseur BIGINT DEFAULT 1,
    dateReceptionAttendue DATETIME,
    navetteType VARCHAR(20) DEFAULT 'alpla'
);

CREATE TABLE ConteneurConsomme_tmp (
    id bigint,
    idLigne bigint,
    idComposant bigint,
    horoDepose datetime,
    qteComposants int,
    SSCC varchar(20),
    estPerime int DEFAULT 0
);

CREATE TABLE ConteneurNavette_tmp (
    id bigint,
    idForcageCommande bigint,
    idLigne bigint,
    idNavette bigint,
    slotNavette int,
    horoConsoEstimee datetime,
    qteComposants int,
    codeStatut varchar(10),
    idComposant bigint,
    ordreConso int,
    pourAmorcage int,
    SSCC varchar(20),
    version int DEFAULT 0
);

ALTER SEQUENCE seq_navettes RESTART WITH (SELECT MAX(id) + 10 FROM navettes);
ALTER SEQUENCE seq_conteneurs_navettes RESTART WITH (SELECT MAX(id) + 10 FROM conteneursnavettes);

use pg_commandorv2_logs;

TRUNCATE TABLE UserAction;
TRUNCATE TABLE log4j_common;
TRUNCATE TABLE log_application;
TRUNCATE TABLE log_compute;
