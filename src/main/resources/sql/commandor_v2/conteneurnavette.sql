INSERT INTO conteneurnavette (id, idForcageCommande, idLigne, idNavette, slotNavette, horoConsoEstimee, qteComposants,
                              codeStatut, idComposant, ordreConso, pourAmorcage, SSCC, version)
SELECT NEXT VALUE FOR seq_conteneurs_navettes,
       NULL,
       cn_prod.idLigne,
       cn_prod.idNavette,
       cn_prod.slotNavette,
       cn_prod.horoConsoEstimee,
       cn_prod.qteComposants,
       cn_prod.codeStatut,
       c_test.id,
       cn_prod.ordreConso,
       cn_prod.pourAmorcage,
       cn_prod.SSCC,
       cn_prod.version
FROM conteneurnavette_tmp cn_prod
         INNER JOIN composant_tmp c_prod ON cn_prod.idComposant = c_prod.id
         INNER JOIN composant c_test ON c_prod.gcas = c_test.gcas
         LEFT OUTER JOIN conteneurnavette cn_test
                         ON cn_test.idLigne = cn_prod.idLigne and cn_test.idNavette = cn_test.idNavette and
                            cn_test.idCOmposant = c_test.id
WHERE cn_test.id IS NULL;
