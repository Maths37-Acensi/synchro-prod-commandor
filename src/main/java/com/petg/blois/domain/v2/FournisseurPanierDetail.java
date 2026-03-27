package com.petg.blois.domain.v2;

import lombok.Data;

@Data
public class FournisseurPanierDetail {
    private Long id;
    private Long idcomposant;
    private Long idfournisseurpanier;
    private Long numero;
    private Long quantite;
    private Long idligne;
}
