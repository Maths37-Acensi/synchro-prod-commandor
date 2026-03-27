INSERT INTO produitfini (gcas, libelle, idProduitFiniGamme, idTechnoBulk)
SELECT pf_prod.gcas,
       pf_prod.libelle,
       pf_prod.idProduitFiniGamme,
       pf_prod.idTechnoBulk
FROM produitfini_tmp pf_prod
         LEFT OUTER JOIN produitfini pf_test ON pf_prod.gcas = pf_test.gcas
WHERE pf_test.id IS NULL;
