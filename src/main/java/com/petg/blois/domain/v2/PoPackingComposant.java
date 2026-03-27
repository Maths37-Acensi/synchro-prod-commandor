package com.petg.blois.domain.v2;

import lombok.Data;

@Data
public class PoPackingComposant {
    private Long id;
    private Long idpopacking;
    private Long idcomposant;
    private Long quantiteconsoprevue;
    private Long quantitebesoincomplementaire;
}
