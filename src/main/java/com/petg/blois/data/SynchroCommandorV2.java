package com.petg.blois.data;

import com.petg.blois.domain.v2.*;
import lombok.Getter;
import org.apache.logging.log4j.util.Strings;

import static com.petg.blois.data.SynchroType.FULL;

@Getter
public enum SynchroCommandorV2 {
    COMPOSANT(Composant.class, FULL),
    LIGNECOMPOSANT(LigneComposant.class, FULL),
    PRODUITFINI(ProduitFini.class, FULL),
    POPACKING(PoPacking.class, FULL),
    POPACKINGCOMPOSANT(PoPackingComposant.class, FULL),
    ARRETLIGNE(ArretLigne.class, FULL),
    COMPOSANTPIPO(ComposantPIPO.class, FULL),
    FOURNISSEURPANIER(FournisseurPanier.class, FULL),
    FOURNISSEURPANIERDETAIL(FournisseurPanierDetail.class, FULL),
    LIGNECOMPOSANTFOURNISSEUR(LigneComposantFournisseur.class, FULL);

    final Class<?> clazz;
    final SynchroType synchroType;
    final String dateColumn;

    SynchroCommandorV2(Class<?> clazz, SynchroType synchroType) {
        this.clazz = clazz;
        this.synchroType = synchroType;
        this.dateColumn = Strings.EMPTY;
    }

    SynchroCommandorV2(Class<?> clazz, SynchroType synchroType, String dateColumn) {
        this.clazz = clazz;
        this.synchroType = synchroType;
        this.dateColumn = dateColumn;
    }
}
