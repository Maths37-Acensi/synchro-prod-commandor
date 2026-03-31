package com.petg.blois.domain.v2;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConteneurConsomme {
    private Long id;
    private Long idligne;
    private Long idcomposant;
    private LocalDateTime horodepose;
    private Long qtecomposants;
    private String sscc;
    private Long estperime;
}
