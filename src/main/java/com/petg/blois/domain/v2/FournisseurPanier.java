package com.petg.blois.domain.v2;

import lombok.Data;

@Data
public class FournisseurPanier {
    private Long id;
    private Long idfournisseur;
    private String jourheurecommande;
    private String jourheurereception;
    private Long idcluster;
}
