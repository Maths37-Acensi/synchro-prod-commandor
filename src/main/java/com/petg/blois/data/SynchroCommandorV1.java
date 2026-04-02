package com.petg.blois.data;

import com.petg.blois.domain.v1.LigneSap;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

import static com.petg.blois.data.SynchroType.FULL;

@Getter
public enum SynchroCommandorV1 {
    //GCAS_PARAMETRAGE(GcasParametrage.class, DELTA_ID),
    LIGNE_SAP(LigneSap.class, FULL);/*,
    NAVETTE(Navette.class, DELTA_DATE, "horodate"),
    NAVETTE_CONTENU(NavetteContenu.class, DELTA_ID),
    PLAN_SAP(PlanSap.class, DELTA_DATE, "horodebut"),
    RTCIS_RESERVATION_STOCK(RtcisReservationStock.class, DELTA_DATE, "horodate"),
    TUBES_CALCULS_PARAMS(TubesCalculsParams.class, FULL)*/;

    final Class<?> clazz;
    final SynchroType synchroType;
    final String dateColumn;

    SynchroCommandorV1(Class<?> clazz, SynchroType synchroType) {
        this.clazz = clazz;
        this.synchroType = synchroType;
        this.dateColumn = Strings.EMPTY;
    }

    SynchroCommandorV1(Class<?> clazz, SynchroType synchroType, String dateColumn) {
        this.clazz = clazz;
        this.synchroType = synchroType;
        this.dateColumn = dateColumn;
    }
}
