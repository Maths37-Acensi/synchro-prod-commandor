INSERT INTO navette (id, idCluster, numero, horoCalcul, horoCommande, horoExpedition,
                     horoReception, codeStatut, numBL, idConteneurType, raisonCommande, emplacement,
                     version, erreur, dateMailCommande, idFournisseur, dateReceptionAttendue, navetteType)
SELECT n_prod.id,
       n_prod.idCluster,
       n_prod.numero,
       n_prod.horoCalcul,
       n_prod.horoCommande,
       n_prod.horoExpedition,
       n_prod.horoReception,
       n_prod.codeStatut,
       n_prod.numBL,
       n_prod.idConteneurType,
       n_prod.raisonCommande,
       n_prod.emplacement,
       n_prod.version,
       n_prod.erreur,
       n_prod.dateMailCommande,
       f_test.id,
       n_prod.dateReceptionAttendue,
       n_prod.navetteType
FROM navette_tmp n_prod
         inner join fournisseur_tmp f_prod on n_prod.idFournisseur = f_prod.id
         inner join fournisseur f_test on f_test.code = f_prod.code
         LEFT OUTER JOIN navette n_test
                         ON n_prod.idCluster = n_test.idCluster and n_prod.idConteneurType = n_test.idConteneurType and
                            n_test.idFournisseur = f_test.id
WHERE n_test.id IS NULL;