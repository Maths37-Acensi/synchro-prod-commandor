INSERT INTO fournisseurpanier (idFournisseur, jourHeureCommande, jourHeureReception, idCluster)
SELECT fp_prod.idFournisseur,
       fp_prod.jourHeureCommande,
       fp_prod.jourHeureReception,
       fp_prod.idCluster
FROM fournisseurpanier_tmp fp_prod
         LEFT OUTER JOIN fournisseurpanier fp_test
                         ON fp_prod.idFournisseur = fp_test.idFournisseur AND fp_prod.idCluster = fp_test.idCluster
WHERE fp_test.id IS NULL;
