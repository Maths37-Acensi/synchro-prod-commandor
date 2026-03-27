package com.petg.blois.domain.v2;

import lombok.Data;

@Data
public class LigneComposant {
    private Long id;
    private Long idcomposant;
    private Long idligne;
    private Long pertes;
    private Long encoursmin;
    private Long encoursmax;
    private Long bbaanticiper;
    private String portesortie;
    private Long nbparminute;
    private String stagingzone;
    private Boolean archive;
    private Long bbacommanderenplus;
}
