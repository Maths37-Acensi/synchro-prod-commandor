INSERT INTO composant (gcas, libelle, idconteneurtype, idcomposanttype, qteparconteneur, idfournisseur, stocksecu,
                       stocksecucommanderpar, idfournisseurstocksecu)
SELECT c_prod.gcas,
       c_prod.libelle,
       c_prod.idconteneurtype,
       c_prod.idcomposanttype,
       c_prod.qteparconteneur,
       c_prod.idfournisseur,
       c_prod.stocksecu,
       c_prod.stocksecucommanderpar,
       c_prod.idfournisseurstocksecu
FROM composant_tmp c_prod
         LEFT OUTER JOIN composant c_test ON c_prod.gcas = c_test.gcas
WHERE c_test.id IS NULL;
