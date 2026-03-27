package com.petg.blois.domain.v1;

import lombok.Data;

@Data
public class GcasParametrage {
    private Integer id;
    private Integer idfournisseur;
    private Integer idmaterialtype;
    private Integer idcontainertype;
    private String gcas;
    private String libelle;
    private Integer qtecontainer;
    private Integer dejacommande;
}
