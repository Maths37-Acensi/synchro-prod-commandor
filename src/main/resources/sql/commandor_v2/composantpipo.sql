INSERT INTO composantpipo (idComposantOrigine, idComposantSubstitut, horoDeb, horoFin)
SELECT c_ori_test.id,
       c_sub_test.id,
       cp_prod.horoDeb,
       cp_prod.horoFin
FROM composantpipo_tmp cp_prod
         INNER JOIN composant_tmp c_ori_prod ON cp_prod.idComposantOrigine = c_ori_prod.id
         INNER JOIN composant_tmp c_sub_prod ON cp_prod.idComposantSubstitut = c_sub_prod.id
         INNER JOIN composant c_ori_test ON c_ori_prod.gcas = c_ori_test.gcas
         INNER JOIN composant c_sub_test ON c_sub_prod.gcas = c_sub_test.gcas
         LEFT OUTER JOIN composantpipo cp_test
                         ON c_ori_test.id = cp_test.idComposantOrigine AND c_sub_test.id = cp_test.idComposantSubstitut
WHERE cp_test.id IS NULL;
