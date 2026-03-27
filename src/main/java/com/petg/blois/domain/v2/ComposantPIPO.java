package com.petg.blois.domain.v2;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComposantPIPO {
    private Long id;
    private Long idcomposantorigine;
    private Long idcomposantsubstitut;
    private LocalDateTime horodeb;
    private LocalDateTime horofin;
}
