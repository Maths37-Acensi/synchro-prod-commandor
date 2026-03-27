package com.petg.blois.domain.v2;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArretLigne {
    private Long id;
    private LocalDateTime horoDebPrev;
    private LocalDateTime horoFinPrev;
    private Long idLigne;
    private Long estSaisieManuelle;
    private String raison;
    private Long estArretTeamNeeded;
}
