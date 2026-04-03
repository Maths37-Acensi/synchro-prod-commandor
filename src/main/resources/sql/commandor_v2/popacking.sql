INSERT INTO popacking (idLigne, idProduitFini, horoDebPrev, horoFinPrev, horoDebAPI, horoFinAPI, numPo,
                       quantiteProduite, quantitePrevue, dureeChangeOver, ordre)
SELECT pp_prod.idLigne,
       pf_test.id,
       pp_prod.horoDebPrev,
       pp_prod.horoFinPrev,
       pp_prod.horoDebAPI,
       pp_prod.horoFinAPI,
       pp_prod.numPo,
       pp_prod.quantiteProduite,
       pp_prod.quantitePrevue,
       pp_prod.dureeChangeOver,
       pp_prod.ordre
FROM popacking_tmp pp_prod
         INNER JOIN produitfini_tmp pf_prod ON pp_prod.idProduitFini = pf_prod.id
         INNER JOIN produitfini pf_test ON pf_prod.gcas = pf_test.gcas
         LEFT OUTER JOIN popacking pp_test ON pp_prod.numPo = pp_test.numPo AND pp_prod.ordre = pp_test.ordre
WHERE pp_test.id IS NULL;
