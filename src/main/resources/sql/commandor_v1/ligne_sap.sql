UPDATE ligne_sap
SET
    poPacking = ls_prod.poPacking,
    qteFlacon = ls_prod.qteFlacon,
    horodate = ls_prod.horodate
FROM ligne_sap ls_test
INNER JOIN ligne_sap_tmp ls_prod ON ls_test.codeSAP = ls_prod.codeSAP
WHERE
    ls_test.poPacking != ls_prod.poPacking
   OR ls_test.qteFlacon != ls_prod.qteFlacon;
