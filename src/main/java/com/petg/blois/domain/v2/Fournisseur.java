package com.petg.blois.domain.v2;

import lombok.Data;

@Data
public class Fournisseur {
    private Long id;
    private String libelle;
    private Long actif;
    private String code;
    private Long delaiexpedition;
    private Long delaicommande;
    private Long delaialerte;
    private String emailscommande;
}
