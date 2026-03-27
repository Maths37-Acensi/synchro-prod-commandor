INSERT INTO lignecomposant (idComposant, idLigne, pertes, encoursMin, encoursMax, bbAAnticiper, porteSortie,
                            nbParMinute, stagingZone, archive, bbACommanderEnPlus)
SELECT c_test.id,
       lc_prod.idLigne,
       lc_prod.pertes,
       lc_prod.encoursMin,
       lc_prod.encoursMax,
       lc_prod.bbAAnticiper,
       lc_prod.porteSortie,
       lc_prod.nbParMinute,
       lc_prod.stagingZone,
       lc_prod.archive,
       lc_prod.bbACommanderEnPlus
FROM lignecomposant_tmp lc_prod
         INNER JOIN composant_tmp c_prod ON lc_prod.idComposant = c_prod.id
         INNER JOIN composant c_test ON c_prod.gcas = c_test.gcas
         LEFT OUTER JOIN lignecomposant lc_test ON lc_prod.idLigne = lc_test.idLigne AND c_test.id = lc_test.idComposant
WHERE lc_test.id IS NULL;
