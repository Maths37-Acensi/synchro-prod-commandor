package com.petg.blois.domain.v2;

import lombok.Data;

@Data
public class Composant {
    private Long id;
    private String gcas;
    private String libelle;
    private Long idconteneurtype;
    private Long idcomposanttype;
    private Long qteparconteneur;
    private Long idfournisseur;
    private Long stocksecu;
    private Long stocksecucommanderpar;
    private Long idfournisseurstocksecu;
}
