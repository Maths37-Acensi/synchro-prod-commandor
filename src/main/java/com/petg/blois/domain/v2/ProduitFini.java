package com.petg.blois.domain.v2;

import lombok.Data;

@Data
public class ProduitFini {
    private Long id;
    private String gcas;
    private String libelle;
    private Long idproduitfinigamme;
    private Long idtechnobulk;
}
