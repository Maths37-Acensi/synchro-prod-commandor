INSERT INTO lignecomposantfournisseur (idLigneComposant, archive, idFournisseur)
SELECT lc_test.id,
       lcf_prod.archive,
       lcf_prod.idFournisseur
FROM lignecomposantfournisseur_tmp lcf_prod
         INNER JOIN lignecomposant_tmp lc_prod ON lcf_prod.idLigneComposant = lc_prod.id
         INNER JOIN composant_tmp c_prod ON lc_prod.idComposant = c_prod.id
         INNER JOIN composant c_test ON c_prod.gcas = c_test.gcas
         INNER JOIN lignecomposant lc_test ON c_test.id = lc_test.idComposant AND lc_prod.idLigne = lc_test.idLigne
         LEFT OUTER JOIN lignecomposantfournisseur lcf_test
                         ON lc_test.id = lcf_test.idLigneComposant AND lcf_prod.idFournisseur = lcf_test.idFournisseur
WHERE lcf_test.id IS NULL;
