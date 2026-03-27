package com.petg.blois.domain.v2;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PoPacking {
    private Long id;
    private Long idligne;
    private Long idproduitfini;
    private LocalDateTime horodebprev;
    private LocalDateTime horofinprev;
    private LocalDateTime horodebaPI;
    private LocalDateTime horofinaPI;
    private String numpo;
    private Long quantiteproduite;
    private Long quantiteprevue;
    private Double dureechangeover;
    private Long ordre;
}
