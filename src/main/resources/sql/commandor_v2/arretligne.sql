INSERT INTO arretligne (horoDebPrev, horoFinPrev, idLigne, estSaisieManuelle, raison, estArretTeamNeeded)
SELECT al_prod.horoDebPrev,
       al_prod.horoFinPrev,
       al_prod.idLigne,
       al_prod.estSaisieManuelle,
       al_prod.raison,
       al_prod.estArretTeamNeeded
FROM arretligne_tmp al_prod
         LEFT OUTER JOIN arretligne al_test
                         ON al_prod.idLigne = al_test.idLigne AND al_prod.horoDebPrev = al_test.horoDebPrev
WHERE al_test.id IS NULL;
