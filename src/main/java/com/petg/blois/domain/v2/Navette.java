package com.petg.blois.domain.v2;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Navette {
    private Long id;
    private Long idcluster;
    private Long numero;
    private LocalDateTime horocalcul;
    private LocalDateTime horocommande;
    private LocalDateTime horoexpedition;
    private LocalDateTime hororeception;
    private String codestatut;
    private String numbl;
    private Long idconteneurtype;
    private String raisoncommande;
    private String emplacement;
    private Long version;
    private String erreur;
    private LocalDateTime datemailcommande;
    private Long idfournisseur;
    private LocalDateTime datereceptionattendue;
    private String navettetype;
}
