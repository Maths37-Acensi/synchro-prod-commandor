package com.petg.blois.domain.v1;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LigneSap {
    private Integer id;
    private Long idlignetremie;
    private String libelle;
    private String codesap;
    private String codertcis;
    private Long active;
    private String popacking;
    private Long qteflacon;
    private LocalDateTime horodate;
}
