package com.petg.blois.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;

import java.util.*;

/**
 * MaifUtils
 *
 * @author F. LUTZ (97211P)
 */
@Slf4j
@UtilityClass
public final class PGUtils {
    /**
     * Permet d'utiliser les varargs en tant que paramètre optionnel tout en contrôlant qu'il n'y a pas plus d'un objet renseigné.
     * Si pas d'objet, on renseigne la valeur par défaut, sinon on prend le 1er élément (et donc l'unique) du tableau.
     *
     * @param varArgsParameter L'objet varargs attendu
     * @param <T>              Le type de l'objet attendu
     * @return Le paramètre renseigné s'il est présent, sinon {@link Optional#empty()}.
     */
    public <T> Optional<T> findFirstParameter(@Nullable T[] varArgsParameter) {
        if (varArgsParameter == null) {
            return Optional.empty();
        } else if (varArgsParameter.length > 1) {
            throw new IllegalArgumentException("Optional parameter attempted, informed more than 1 object !");
        }

        return varArgsParameter.length == 0 ? Optional.empty() : Optional.of(varArgsParameter[0]);
    }

    /**
     * Permet d'utiliser les varargs en tant que paramètre optionnel avec plusieurs valeurs possibles, tout en ayant la possibilité de mettre une liste par défaut.
     *
     * @param varArgsParameters Liste des arguments attendus
     * @param defaultParameters Liste par défaut
     * @param <T>               Le type de l'objet attendu
     * @return Le paramètre renseigné s'il est présent, sinon {@link Optional#empty()}.
     */
    public <T> List<T> findAllParameterOrDefault(T[] varArgsParameters, List<T> defaultParameters) {
        if (varArgsParameters.length == 0) {
            return defaultParameters;
        } else {
            return Arrays.asList(varArgsParameters);
        }
    }

    /**
     * Création du mapper jackson qui permet de parser / sérialiser le json. Son utilisation est faite notamment :
     * <ul>
     *     <li>Dans le JsonService</li>
     *     <li>Par Spring</li>
     *     <li>Par Liquibase</li>
     * </ul>
     *
     * @return Mapper json.
     */
    public JsonMapper createJsonMapper() {
        JsonMapper mapper = JsonMapper.builder()
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true)
                .configure(MapperFeature.ALLOW_COERCION_OF_SCALARS, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .addModule(new JavaTimeModule())
                .build();

        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper;
    }

    /**
     * Extension de la méthode {@link StringUtils#defaultIfEmpty(CharSequence, CharSequence)} mais pour tout type d'objets.
     *
     * @param value        Valeur à tester
     * @param defaultValue Valeur par défaut
     * @param <T>          Type de value.
     * @return Si value est renseignée, value, sinon defaultValue
     */
    @SuppressWarnings({"unchecked", "java:S3740"})
    public <T> T defaultIfEmpty(@Nullable T value, T defaultValue) {
        if (value instanceof CharSequence str && defaultValue instanceof CharSequence defaultStr) {
            return (T) StringUtils.defaultIfEmpty(str, defaultStr);
        }
        return Objects.isNull(value) ? defaultValue : value;
    }

    /**
     * Vérifie (sans importance sur l'ordre) que 2 collections sont identiques.<br/>
     * Les collections doivent être de même type, sinon ça n'a pas de sens de les comparer, d'où le type générique T.<br/>
     * C'est revu au goût du jour pour spécifier les types et comparer sur le hashcode de l'objet, et non l'objet.<br/>
     * On ne cherche pas à vérifier que l'objet est le même (pointeur sur le même espace mémoire), mais que le contenu de l'objet est le même (ses attributs sont identiques en valeur).
     *
     * @param expected Collection attendue de type T
     * @param actual   Collection à comparer de type T
     * @param <T>      Type générique de collection
     */
    public <T> boolean isEqualsCollections(final Collection<T> expected, final Collection<T> actual, boolean keepOrder) {
        // Comparaison de la taille des collections, doivent être identiques
        if (expected.size() != actual.size()) {
            return false;
        }

        // On fait un "group by" sur le hashcode des objets
        // Map<Integer, Pair<Object, Integer>> -> Map<"hashcode', Pair<T, "count">>
        LinkedHashMap<Integer, Pair<T, Integer>> expectedCardMap = getCardinalityMap(expected);
        LinkedHashMap<Integer, Pair<T, Integer>> actualCardMap = getCardinalityMap(actual);

        if (expectedCardMap.size() != actualCardMap.size()) {
            return false;
        }

        if (!expectedCardMap.keySet().stream()
                .map(key -> Objects.equals(getFreq(key, expectedCardMap), getFreq(key, actualCardMap)))
                .reduce((b1, b2) -> b1 && b2)
                .orElse(true)) {
            return false;
        }

        if (keepOrder) {
            List<Integer> expectedHashCodeList = expectedCardMap.sequencedKeySet().stream().toList();
            List<Integer> actualHashCodeList = actualCardMap.sequencedKeySet().stream().toList();
            for (int i = 0; i < expectedHashCodeList.size(); i++) {
                if (!Objects.equals(expectedHashCodeList.get(i), actualHashCodeList.get(i))) {
                    return false;
                }
            }
        }

        return true;
    }

    private <T> LinkedHashMap<Integer, Pair<T, Integer>> getCardinalityMap(final Collection<T> coll) {
        LinkedHashMap<Integer, Pair<T, Integer>> count = new LinkedHashMap<>();
        for (T obj : coll) {
            count.merge(obj.hashCode(), Pair.of(obj, 1), (p1, p2) -> Pair.of(p1.getLeft(), p1.getRight() + p2.getRight()));
        }
        return count;
    }

    private <T> int getFreq(final Integer hashcode, final Map<Integer, Pair<T, Integer>> freqMap) {
        Pair<T, Integer> count = freqMap.get(hashcode);
        return count == null ? 0 : count.getRight();
    }

    /**
     * Convert a camel case string to a snake case string.
     *
     * @param source : Camel case string to convert
     * @return A snake case string
     */
    public String camelToSnakeCase(String source) {
        // Regular Expression
        String regex = "([a-z])([A-Z]+)";

        // Replacement string
        String replacement = "$1_$2";

        // Replace the given regex with replacement string and convert it to lower case.
        return source.replaceAll(regex, replacement).toLowerCase();
    }

    /**
     * Convert a snake case string to a camel case string
     *
     * @param source                Snake case string to convert
     * @param capitalizeFirstLetter set to true if you want to capitalize first letter
     * @return A camel case string
     */
    public String snakeToCamelCase(String source, boolean capitalizeFirstLetter) {
        String target = source;

        if (capitalizeFirstLetter) {
            target = target.substring(0, 1).toUpperCase() + target.substring(1);
        }

        // Run a loop till string contains underscore
        while (target.contains("_")) {
            // Replace the first occurrence of letter that present after the underscore, to capitalize
            // form of next letter of underscore
            target = target.replaceFirst("_[a-z]", String.valueOf(Character.toUpperCase(target.charAt(target.indexOf('_') + 1))));
        }

        return target;
    }
}
