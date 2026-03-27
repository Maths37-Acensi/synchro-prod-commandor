package com.petg.blois.domain.v2;

import lombok.Data;

@Data
public class LigneComposantFournisseur {
    private Long id;
    private Long idlignecomposant;
    private Boolean archive;
    private Long idfournisseur;
}
