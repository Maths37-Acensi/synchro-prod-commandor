INSERT INTO conteneurconsomme (id, idLigne, idComposant, horoDepose, qteComposants, SSCC, estPerime)
SELECT cc_prod.id,
       cc_prod.idLigne,
       c_test.id,
       cc_prod.horoDepose,
       cc_prod.qteComposants,
       cc_prod.SSCC,
       cc_prod.estPerime
FROM conteneurconsomme_tmp cc_prod
         INNER JOIN composant_tmp c_prod ON cc_prod.idComposant = c_prod.id
         INNER JOIN composant c_test ON c_prod.gcas = c_test.gcas
         LEFT OUTER JOIN conteneurconsomme cc_test
                         ON cc_test.idLigne = cc_prod.idLigne
WHERE cc_test.id IS NULL;