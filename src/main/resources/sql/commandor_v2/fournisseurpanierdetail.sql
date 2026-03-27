INSERT INTO fournisseurpanierdetail (idComposant, idFournisseurPanier, numero, quantite, idLigne)
SELECT c_test.id,
       fp_test.id,
       fpd_prod.numero,
       fpd_prod.quantite,
       fpd_prod.idLigne
FROM fournisseurpanierdetail_tmp fpd_prod
         INNER JOIN composant_tmp c_prod ON fpd_prod.idComposant = c_prod.id
         INNER JOIN composant c_test ON c_prod.gcas = c_test.gcas
         INNER JOIN fournisseurpanier_tmp fp_prod ON fpd_prod.idFournisseurPanier = fp_prod.id
         INNER JOIN fournisseurpanier fp_test
                    ON fp_prod.idFournisseur = fp_test.idFournisseur AND fp_prod.idCluster = fp_test.idCluster
         LEFT OUTER JOIN fournisseurpanierdetail fpd_test
                         ON c_test.id = fpd_test.idComposant AND fp_test.id = fpd_test.idFournisseurPanier AND
                            fpd_prod.idLigne = fpd_test.idLigne
WHERE fpd_test.id IS NULL;
