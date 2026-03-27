use pg_commandorv1;

TRUNCATE TABLE log_application;
TRUNCATE TABLE log_connexion;
TRUNCATE TABLE log_kanban;
TRUNCATE TABLE log_modif_param;
TRUNCATE TABLE navette;
TRUNCATE TABLE navette_calcul_histo;
TRUNCATE TABLE navette_contenu;
TRUNCATE TABLE navette_histo;
TRUNCATE TABLE plan_sap;
TRUNCATE TABLE plan_sap_histo;
TRUNCATE TABLE rtcis_reservation_stock;

UPDATE sys_mail_notif SET value = 'pg@acensi.fr';
UPDATE secu_utilisateur SET mail = 'pg@acensi.fr';

CREATE TABLE LIGNE_SAP_tmp
(
    id            INT,
    idLigneTremie INT,
    libelle       VARCHAR(1000),
    codeSAP       VARCHAR(1000),
    codeRTCIS     VARCHAR(1000),
    active        INT,
    poPacking     VARCHAR(1000),
    qteFlacon     INT,
    horodate      DATETIME
);

CREATE TABLE GCAS_PARAMETRAGE_tmp
(
    id              INT,
    idFournisseur   INT,
    idMaterialType  INT,
    idContainerType INT,
    gcas            VARCHAR(1000),
    libelle         VARCHAR(1000),
    qteContainer    INT,
    dejaCommande    INT
);
