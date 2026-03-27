package com.petg.blois.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.petg.blois.exception.ServiceException;
import com.petg.blois.util.PGUtils;
import com.petg.blois.util.ReflectionUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service pour la gestion de la sérialisation et désérialisation JSON.
 * Il fournit des méthodes utilitaires permettant de convertir des objets en JSON et de les manipuler,
 * en tenant compte des différentes configurations de tolérance aux erreurs.
 *
 * @author F.LUTZ
 */
@Service
@RequiredArgsConstructor
public class JsonService {
    private final JsonMapper jsonMapper;

    /**
     * Sérialise un objet en une chaîne JSON. Pour qu'il puisse être converti en JsonNode.
     * Exemple d'utilisation : @see src.test.java.fr.maif.datain.core.service.JsonServiceTest#writeValueAsStringTest
     *
     * @param bean L'objet à sérialiser.
     * @return La représentation JSON de l'objet.
     */
    public String toJson(Object bean) {
        try {
            return jsonMapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Convertit une chaîne JSON en un arbre JSON (JsonNode).
     *
     * @param json La chaîne JSON à convertir.
     * @return L'arbre JSON correspondant.
     */
    public JsonNode readTree(String json) {
        try {
            return jsonMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Recherche un attribut spécifique dans un JsonNode, y compris dans les objets imbriqués, en suivant un chemin de clés.
     *
     * @param node JsonNode source
     * @param keys Table de chemin des clés séparées par des points (ex: "user.address.city")
     * @return Le JsonNode correspondant à l'attribut recherché, ou null si non trouvé
     */
    public <J extends JsonNode> Optional<J> findFirstNode(@Nullable J node, String keys) {
        J currentNode = node;

        for (String key : keys.split("\\.")) {
            if (currentNode == null) {
                return Optional.empty();
            } else if (currentNode.has(key)) {
                currentNode = (J) currentNode.get(key);
            } else {
                Iterator<String> it = currentNode.fieldNames();
                boolean find = false;
                while (it.hasNext()) {
                    String fieldName = it.next();
                    if (currentNode.get(fieldName).isObject()) {
                        Optional<JsonNode> opt = findFirstNode(currentNode.get(fieldName), key);
                        if (opt.isPresent()) {
                            currentNode = (J) opt.get();
                            find = true;
                            break;
                        }
                    }
                }
                if (!find) {
                    return Optional.empty();
                }
            }
        }

        return Optional.ofNullable(currentNode);
    }

    /**
     * Recherche un attribut spécifique dans un JsonNode, y compris dans les objets imbriqués, en suivant un chemin de clés.
     *
     * @param node JsonNode source
     * @param keys Table de chemin des clés séparées par des points (ex: "user.address.city"). On s'arrête à l'avant-dernier.
     * @return Le JsonNode correspondant à l'attribut recherché, ou null si non trouvé
     */
    public <J extends JsonNode> Optional<Pair<J, String>> findFirstParentNode(@Nullable J node, String keys) {
        J currentNode = node;
        String[] keyTabs = keys.split("\\.");
        int i = 0;

        while (currentNode != null && i < keyTabs.length - 1) {
            currentNode = findFirstNode(currentNode, keyTabs[i])
                    .orElse(null);
            i++;
        }

        return currentNode == null ? Optional.empty() : Optional.of(Pair.of(currentNode, keyTabs[i]));
    }

    /**
     * Convertit une chaîne JSON en une liste d'objets du type spécifié. La chaine JSON peut être un objet ou une liste d'objet.
     * Les elements de la Liste sont convertie dans l'objet qui a été defini
     * Exemple d'utilisation : @see src.test.java.fr.maif.datain.core.service.JsonServiceTest#readValuestest
     *
     * @param json           La chaîne JSON à convertir.
     * @param clazz          Classe des objets de la liste.
     * @param jsonOptionsOpt Option de lecture du json.
     * @param <T>            Le type générique des objets de la liste à retourner.
     * @return Une liste d'objets du type spécifié. Retourne une liste vide si le JSON est null.
     */
    public final <T> List<T> findAll(@Nullable String json, Class<T> clazz, JsonOptions... jsonOptionsOpt) {
        JsonOptions options = PGUtils.findFirstParameter(jsonOptionsOpt)
                .orElse(JsonOptions.builder().build());

        if (StringUtils.isEmpty(json)) {
            return new ArrayList<>();
        }

        if (ReflectionUtils.findAllPropertyDescriptors(clazz).isEmpty()) {
            return findAll(readTree(json), clazz, jsonOptionsOpt);
        }

        try {
            if (json.startsWith("[")) {
                JavaType javaType = options.getJavaType() == null ? jsonMapper.getTypeFactory().constructCollectionType(List.class, clazz) :
                        options.getJavaType();
                return jsonMapper.readValue(json, javaType);
            } else {
                return Collections.singletonList(jsonMapper.readValue(json, clazz));
            }
        } catch (JsonProcessingException e) {
            throw new ServiceException(e);
        }
    }

    /**
     * Désérialise une chaîne JSON en un objet.
     * {@link #findAll(String, Class, JsonOptions...)}
     *
     * @param json           La chaîne JSON à convertir.
     * @param clazz          Classe des objets de la liste.
     * @param jsonOptionsOpt Option de lecture du json.
     * @param <T>            Le type générique des objets de la liste à retourner.
     * @return Le 1er objet de la liste.
     */
    public final <T> Optional<T> findFirst(@Nullable String json, Class<T> clazz, JsonOptions... jsonOptionsOpt) {
        return findAll(json, clazz, jsonOptionsOpt).stream()
                .findFirst();
    }

    /**
     * Parse la chaîne JSON pour récupérer le nœud spécifié par le paramètre keys puis appelle la méthode {@link #findAll(JsonNode, Class, JsonOptions...)}
     *
     * @param json           La chaîne JSON à convertir.
     * @param keys           Table de chemin des clés séparées par des points (ex: "user.address.city")
     * @param clazz          Classe des objets de la liste.
     * @param jsonOptionsOpt Option de lecture du json.
     * @param <T>            Le type générique des objets de la liste à retourner.
     * @return Une liste d'objets du type spécifié. Retourne une liste vide si le JSON est null.
     */
    public final <T> List<T> findAll(@Nullable String json, String keys, Class<T> clazz, JsonOptions... jsonOptionsOpt) {
        if (StringUtils.isEmpty(json)) {
            return new ArrayList<>();
        }

        JsonNode rootNode = readTree(json);
        return findFirstNode(rootNode, keys)
                .map(node -> findAll(node, clazz, jsonOptionsOpt))
                .orElse(new ArrayList<>());
    }

    /**
     * Désérialise une chaîne JSON en un objet.
     * {@link #findAll(String, String, Class, JsonOptions...)}
     *
     * @param json           La chaîne JSON à convertir.
     * @param keys           Table de chemin des clés séparées par des points (ex: "user.address.city")
     * @param clazz          Classe des objets de la liste.
     * @param jsonOptionsOpt Option de lecture du json.
     * @param <T>            Le type générique des objets de la liste à retourner.
     * @return Le 1er objet de la liste.
     */
    public final <T> Optional<T> findFirst(@Nullable String json, String keys, Class<T> clazz, JsonOptions... jsonOptionsOpt) {
        return findAll(json, keys, clazz, jsonOptionsOpt).stream()
                .findFirst();
    }

    /**
     * Mappe une liste de JsonNode vers une liste d'objets de type T.
     * Similaire à {@link #findAll(String, Class, JsonOptions...)} avec un JsonNode.
     *
     * @param jsonNode       JsonNode à mapper (peut être une liste)
     * @param clazz          Classe cible pour la conversion
     * @param jsonOptionsOpt Option de mapping du json.
     * @param <T>            Type générique des objets retournés
     * @return Une liste d'instances de T contenant les données des JsonNode
     */
    public final <T> List<T> findAll(@Nullable JsonNode jsonNode, Class<T> clazz, JsonOptions... jsonOptionsOpt) {
        List<T> results = new ArrayList<>();

        if (jsonNode == null) {
            return new ArrayList<>();
        }

        if (jsonNode.isArray()) {
            for (Iterator<JsonNode> it = jsonNode.elements(); it.hasNext(); ) {
                JsonNode currentNode = it.next();
                findFirst(currentNode, clazz, jsonOptionsOpt)
                        .ifPresent(results::add);
            }
        } else {
            findFirst(jsonNode, clazz, jsonOptionsOpt)
                    .ifPresent(results::add);
        }

        return results;
    }

    /**
     * Map jsonNode to t object
     *
     * @param node           JsonNode to map
     * @param clazz          Classe cible pour la conversion
     * @param jsonOptionsOpt Mapping options
     * @param <T>            generic type of clazz
     * @return jsonNode store in T object
     */
    public final <T> Optional<T> findFirst(@Nullable JsonNode node, Class<T> clazz, JsonOptions... jsonOptionsOpt) {
        JsonOptions jsonOptions = PGUtils.findFirstParameter(jsonOptionsOpt)
                .orElse(JsonOptions.builder().build());
        if (node == null) {
            return Optional.empty();
        } else if (ReflectionUtils.findAllPropertyDescriptors(clazz).isEmpty()) {
            // Simple object like String
            if (node.isValueNode()) {
                return ReflectionUtils.parse(node.asText(), clazz);
            } else {
                return ReflectionUtils.parse(node.toString(), clazz);
            }
        } else {
            return findFirst(node.toString(), clazz, jsonOptions);
        }
    }

    /**
     * Méthode qui permet de modifier le contenu d'un objet JSON.
     *
     * @param json        Donnée au format JSON.
     * @param objectToAdd Objet à ajouter dans `json`
     * @param keys        Clés permettant d'accéder à l'objet à ajouter / modifier (on sépare les attributs par un `.`)
     * @return Le json modifié.
     */
    public final String addOrReplaceObject(String json, Object objectToAdd, String keys) {
        ObjectNode copy = readTree(json).deepCopy();
        findFirstParentNode(copy, keys)
                .ifPresent(pair -> {
                    ObjectNode parentNode = pair.getLeft();
                    String attributKey = pair.getRight();

                    if (parentNode.has(attributKey)) {
                        parentNode.remove(attributKey);
                    }

                    parentNode.set(attributKey, readTree(toJson(objectToAdd)));
                });
        return toJson(copy);
    }
}
