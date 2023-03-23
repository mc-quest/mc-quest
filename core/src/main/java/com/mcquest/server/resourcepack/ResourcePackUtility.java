package com.mcquest.server.resourcepack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mcquest.server.util.ResourceUtility;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.Material;
import team.unnamed.creative.base.*;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.*;
import team.unnamed.creative.texture.Texture;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.Callable;

public class ResourcePackUtility {
    public static final int COOLDOWN_DIVISIONS = 16;
    private static final Key WOODEN_AXE = Key.key("item/wooden_axe");
    private static final String ITEM_NAMESPACE = "item";
    private static final String TEXTURE_DATA_PREFIX = "data:image/png;base64,";

    public static void writeItemOverrides(FileTree tree, Material material, List<ItemOverride> overrides) {
        Key materialKey = Key.key(material.key().namespace(), "item/" + material.key().value());
        Model model = Model.builder()
                .key(materialKey)
                .parent(Key.key("minecraft", "item/handheld"))
                .textures(ModelTexture.builder()
                        .layers(materialKey)
                        .build())
                .overrides(overrides)
                .build();
        tree.write(model);
    }

    private static void applyCooldownTexture(BufferedImage texture) {

    }

    public static int grayAndDarken(int rgb) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = (rgb & 0xFF);
        int gray = (r + g + b) / 3;
        // Darken.
        gray *= 0.25;
        int ret = 0xFF000000 & rgb;
        ret |= gray;
        ret |= gray << 8;
        ret |= gray << 16;
        return ret;
    }

    public static void writeItemModel(FileTree tree, Callable<InputStream> bbmodel, int itemId,
                                      List<ItemOverride> overrides) {
        // TODO: might need to account for texture resolution
        JsonObject modelJson = ResourceUtility.readJson(bbmodel).getAsJsonObject();

        JsonObject displaysJson = modelJson.get("display").getAsJsonObject();
        Map<ItemTransform.Type, ItemTransform> displays = parseDisplays(displaysJson);

        JsonArray texturesJson = modelJson.get("textures").getAsJsonArray();
        List<Texture> textures = new ArrayList<>();
        Map<String, Key> textureMappings = new HashMap<>();
        parseTextures(texturesJson, textures, textureMappings, itemId);

        ModelTexture modelTexture = ModelTexture.builder()
                .variables(textureMappings)
                .build();

        JsonArray elementsJson = modelJson.get("elements").getAsJsonArray();
        List<Element> elements = parseElements(elementsJson);

        Key modelKey = Key.key(ITEM_NAMESPACE, String.valueOf(itemId));

        tree.write(Model.builder()
                .key(modelKey)
                .display(displays)
                .textures(modelTexture)
                .elements(elements)
                .build());

        for (Texture texture : textures) {
            tree.write(texture);
        }

        ItemOverride override = ItemOverride.of(modelKey, ItemPredicate.customModelData(itemId));
        overrides.add(override);
    }

    private static Vector3Float parseVec3(JsonArray vec) {
        float x = vec.get(0).getAsFloat();
        float y = vec.get(1).getAsFloat();
        float z = vec.get(2).getAsFloat();
        return new Vector3Float(x, y, z);
    }

    private static Vector4Float parseVec4(JsonArray vec) {
        float x = vec.get(0).getAsFloat();
        float y = vec.get(1).getAsFloat();
        float x2 = vec.get(2).getAsFloat();
        float y2 = vec.get(3).getAsFloat();
        return new Vector4Float(x, y, x2, y2);
    }

    private static Map<ItemTransform.Type, ItemTransform> parseDisplays(JsonObject displaysJson) {
        Map<ItemTransform.Type, ItemTransform> displays = new HashMap<>();
        for (ItemTransform.Type type : ItemTransform.Type.values()) {
            String typeKey = type.toString().toLowerCase();
            if (displaysJson.has(typeKey)) {
                JsonObject transformJson = displaysJson.get(typeKey).getAsJsonObject();
                ItemTransform transform = parseItemTransform(transformJson);
                displays.put(type, transform);
            }
        }
        return displays;
    }

    private static ItemTransform parseItemTransform(JsonObject transformJson) {
        ItemTransform.Builder builder = ItemTransform.builder();
        if (transformJson.has("translation")) {
            builder.translation(parseVec3(transformJson.get("translation").getAsJsonArray()));
        }
        if (transformJson.has("rotation")) {
            builder.rotation(parseVec3(transformJson.get("rotation").getAsJsonArray()));
        }
        if (transformJson.has("scale")) {
            builder.scale(parseVec3(transformJson.get("scale").getAsJsonArray()));
        }
        return builder.build();
    }

    private static void parseTextures(JsonArray texturesJson, List<Texture> textures,
                                      Map<String, Key> textureMappings, int itemId) {
        for (JsonElement textureJson : texturesJson) {
            parseTexture(textureJson.getAsJsonObject(), textures, textureMappings, itemId);
        }
    }

    private static void parseTexture(JsonObject textureJson, List<Texture> textures,
                                     Map<String, Key> textureMappings, int itemId) {
        int textureId = textureJson.get("id").getAsInt();
        String path = itemId + "/" + textureId;
        Key textureKey = Key.key(ITEM_NAMESPACE, path);
        String source = textureJson.get("source").getAsString();
        source = source.substring(TEXTURE_DATA_PREFIX.length());
        byte[] bytes = Base64.getDecoder().decode(source);
        Writable data = Writable.bytes(bytes);
        textures.add(Texture.of(textureKey, data));
        textureMappings.put(String.valueOf(textureId), textureKey);
    }

    private static List<Element> parseElements(JsonArray elementsJson) {
        List<Element> elements = new ArrayList<>();
        for (JsonElement elementJson : elementsJson) {
            Element element = parseElement(elementJson.getAsJsonObject());
            elements.add(element);
        }
        return elements;
    }

    private static Element parseElement(JsonObject elementJson) {
        Vector3Float from = parseVec3(elementJson.get("from").getAsJsonArray());
        Vector3Float to = parseVec3(elementJson.get("to").getAsJsonArray());
        JsonObject faces = elementJson.get("faces").getAsJsonObject();
        return Element.builder()
                .from(from)
                .to(to)
                .rotation(parseElementRotation(elementJson))
                .face(CubeFace.NORTH, parseElementFace(faces.get("north").getAsJsonObject()))
                .face(CubeFace.EAST, parseElementFace(faces.get("east").getAsJsonObject()))
                .face(CubeFace.SOUTH, parseElementFace(faces.get("south").getAsJsonObject()))
                .face(CubeFace.WEST, parseElementFace(faces.get("west").getAsJsonObject()))
                .face(CubeFace.UP, parseElementFace(faces.get("up").getAsJsonObject()))
                .face(CubeFace.DOWN, parseElementFace(faces.get("down").getAsJsonObject()))
                .build();
    }

    private static ElementRotation parseElementRotation(JsonObject element) {
        if (!element.has("rotation")) {
            return null;
        }
        boolean rescale = ElementRotation.DEFAULT_RESCALE;
        Vector3Float origin = parseVec3(element.get("origin").getAsJsonArray());
        JsonArray rotation = element.get("rotation").getAsJsonArray();
        float angle = rotation.get(0).getAsFloat();
        if (angle != 0f) {
            return ElementRotation.of(origin, Axis3D.X, angle, rescale);
        }
        angle = rotation.get(1).getAsFloat();
        if (angle != 0f) {
            return ElementRotation.of(origin, Axis3D.Y, angle, rescale);
        }
        angle = rotation.get(2).getAsFloat();
        return ElementRotation.of(origin, Axis3D.Z, angle, rescale);
    }

    private static ElementFace parseElementFace(JsonObject elementFace) {
        Vector4Float uv = parseVec4(elementFace.get("uv").getAsJsonArray());
        int textureId = elementFace.get("texture").getAsInt();
        return ElementFace.builder()
                .uv(uv)
                .texture("#" + textureId)
                .build();
    }
}
