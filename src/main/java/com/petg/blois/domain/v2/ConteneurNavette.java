package com.petg.blois.domain.v2;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConteneurNavette {
    private Long id;
    private Long idforcagecommande;
    private Long idligne;
    private Long idnavette;
    private Long slotnavette;
    private LocalDateTime horoconsoestimee;
    private Long qtecomposants;
    private String codestatut;
    private Long idcomposant;
    private Long ordreconso;
    private Long pouramorcage;
    private String sscc;
    private Long version;
}
