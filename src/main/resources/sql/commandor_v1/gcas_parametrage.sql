INSERT INTO gcas_parametrage (idFournisseur, idMaterialType, idContainerType, gcas, libelle, qteContainer, dejaCommande)
SELECT gp_prod.idfournisseur,
       gp_prod.idMaterialType,
       gp_prod.idContainerType,
       gp_prod.gcas,
       gp_prod.libelle,
       gp_prod.qteContainer,
       gp_prod.dejaCommande
FROM gcas_parametrage_tmp gp_prod
         LEFT OUTER JOIN gcas_parametrage gp_test ON gp_prod.gcas = gp_test.gcas
WHERE gp_test.id IS NULL;
