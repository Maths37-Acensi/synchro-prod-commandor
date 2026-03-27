INSERT INTO popackingcomposant (idPoPacking, idComposant, quantiteConsoPrevue, quantiteBesoinComplementaire)
SELECT pp_test.id,
       c_test.id,
       ppc_prod.quantiteConsoPrevue,
       ppc_prod.quantiteBesoinComplementaire
FROM popackingcomposant_tmp ppc_prod
         INNER JOIN popacking_tmp pp_prod ON ppc_prod.idPoPacking = pp_prod.id
         INNER JOIN popacking pp_test ON pp_prod.numPo = pp_test.numPo AND pp_prod.ordre = pp_test.ordre
         INNER JOIN composant_tmp c_prod ON ppc_prod.idComposant = c_prod.id
         INNER JOIN composant c_test ON c_prod.gcas = c_test.gcas
         LEFT OUTER JOIN popackingcomposant ppc_test
                         ON pp_test.id = ppc_test.idPoPacking AND c_test.id = ppc_test.idComposant
WHERE ppc_test.id IS NULL;
