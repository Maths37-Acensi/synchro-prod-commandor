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
    horoDebPrev datetime,
    horoFinPrev datetime,
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

use pg_commandorv2_logs;

TRUNCATE TABLE UserAction;
TRUNCATE TABLE log4j_common;
TRUNCATE TABLE log_application;
TRUNCATE TABLE log_compute;
