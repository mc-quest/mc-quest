package com.mcquest.core.resourcepack;

import com.google.common.collect.ListMultimap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mcquest.core.asset.Asset;
import com.mcquest.core.ui.Hotbar;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.base.*;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.*;
import team.unnamed.creative.texture.Texture;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

@ApiStatus.Internal
public class ResourcePackUtility {
    private static final String TEXTURE_DATA_PREFIX = "data:image/png;base64,";

    public static int gray(int rgb, double brightness) {
        int r = (rgb >> 16) & 0xFF;
        int g = (rgb >> 8) & 0xFF;
        int b = rgb & 0xFF;
        int gray = (r + g + b) / 3;
        gray *= brightness;
        int ret = 0xFF000000 & rgb;
        ret |= gray;
        ret |= gray << 8;
        ret |= gray << 16;
        return ret;
    }

    public static int writeIcon(FileTree tree, Asset icon, Key key, Material material,
                                ListMultimap<Material, ItemOverride> overrides) {
        return writeIcon(tree, icon, key, overrides.get(material));
    }

    public static int writeIcon(FileTree tree, Asset icon, Key key,
                                List<ItemOverride> overrides) {
        return writeIcon(tree,
                Writable.inputStream(icon::getStream), key, overrides);
    }

    private static int writeIcon(FileTree tree, Writable icon,
                                 Key key, List<ItemOverride> overrides) {
        int customModelData = overrides.size() + 1;

        Texture texture = Texture.of(key, icon);
        tree.write(texture);

        Model model = Model.builder()
                .key(key)
                .parent(Key.key("minecraft", "item/handheld"))
                .textures(ModelTexture.builder()
                        .layers(key)
                        .build())
                .build();
        tree.write(model);

        ItemPredicate itemPredicate = ItemPredicate.customModelData(customModelData);
        ItemOverride itemOverride = ItemOverride.of(key, itemPredicate);
        overrides.add(itemOverride);

        return customModelData;
    }

    public static void writeCooldownIcon(FileTree tree, Asset icon, Key key,
                                         int cooldownTexture, Material material,
                                         ListMultimap<Material, ItemOverride> overrides) {
        writeCooldownIcon(tree, icon, key, cooldownTexture, overrides.get(material));
    }

    public static void writeCooldownIcon(FileTree tree, Asset icon, Key key,
                                         int cooldownTexture,
                                         List<ItemOverride> overrides) {
        try {
            double thetaMax = ((double) cooldownTexture / Hotbar.COOLDOWN_TEXTURES) * 2.0 * Math.PI;
            BufferedImage image = icon.readImage();
            double cx = image.getWidth() / 2.0;
            double cy = image.getHeight() / 2.0;
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    double theta = Math.atan2(cy - y, x - cx) - Math.PI / 2.0;
                    if (theta < 0.0) {
                        theta = 2.0 * Math.PI + theta;
                    }
                    if (theta < thetaMax) {
                        int rgb = gray(image.getRGB(x, y), 0.25);
                        image.setRGB(x, y, rgb);
                    }
                }
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);
            writeIcon(tree, Writable.bytes(stream.toByteArray()), key, overrides);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void writeLockedIcon(FileTree tree, Asset icon, Key key,
                                       List<ItemOverride> overrides) {
        try {
            BufferedImage image = icon.readImage();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    int rgb = gray(image.getRGB(x, y), 1.0);
                    image.setRGB(x, y, rgb);
                }
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);
            writeIcon(tree, Writable.bytes(stream.toByteArray()), key, overrides);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void writeItemOverrides(FileTree tree,
                                          ListMultimap<Material, ItemOverride> overrides) {
        for (Material material : overrides.keySet()) {
            writeItemOverrides(tree, material, overrides.get(material));
        }
    }

    public static void writeItemOverrides(FileTree tree, Material material,
                                          List<ItemOverride> overrides) {
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

    public static int writeModel(FileTree tree, Asset bbmodel, Key key,
                                 Material material,
                                 ListMultimap<Material, ItemOverride> overrides) {
        return writeModel(tree, bbmodel, key, overrides.get(material));
    }

    public static int writeModel(FileTree tree, Asset bbmodel, Key key,
                                 List<ItemOverride> overrides) {
        int customModelData = overrides.size() + 1;

        JsonObject modelJson = bbmodel.readJson().getAsJsonObject();

        Map<ItemTransform.Type, ItemTransform> displays;
        if (modelJson.has("display")) {
            JsonObject displaysJson = modelJson.get("display").getAsJsonObject();
            displays = parseDisplays(displaysJson);
        } else {
            displays = Collections.emptyMap();
        }

        JsonArray texturesJson = modelJson.get("textures").getAsJsonArray();
        List<Texture> textures = new ArrayList<>();
        Map<String, Key> textureMappings = new HashMap<>();
        parseTextures(texturesJson, textures, textureMappings, key);

        ModelTexture modelTexture = ModelTexture.builder()
                .variables(textureMappings)
                .build();

        JsonObject resolution = modelJson.get("resolution").getAsJsonObject();
        int textureWidth = resolution.get("width").getAsInt();
        int textureHeight = resolution.get("height").getAsInt();

        JsonArray elementsJson = modelJson.get("elements").getAsJsonArray();
        List<Element> elements = parseElements(elementsJson, textureWidth, textureHeight);

        tree.write(Model.builder()
                .key(key)
                .display(displays)
                .textures(modelTexture)
                .elements(elements)
                .build());

        for (Texture texture : textures) {
            tree.write(texture);
        }

        ItemOverride override = ItemOverride.of(key, ItemPredicate.customModelData(customModelData));
        overrides.add(override);

        return customModelData;
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
                                      Map<String, Key> textureMappings, Key key) {
        for (JsonElement textureJson : texturesJson) {
            parseTexture(textureJson.getAsJsonObject(), textures, textureMappings, key);
        }
    }

    private static void parseTexture(JsonObject textureJson, List<Texture> textures,
                                     Map<String, Key> textureMappings, Key key) {
        int textureId = textureJson.get("id").getAsInt();
        String path = key.value() + "/" + textureId;
        Key textureKey = Key.key(Namespaces.ITEMS, path);
        String source = textureJson.get("source").getAsString();
        source = source.substring(TEXTURE_DATA_PREFIX.length());
        byte[] bytes = Base64.getDecoder().decode(source);
        Writable data = Writable.bytes(bytes);
        textures.add(Texture.of(textureKey, data));
        textureMappings.put(String.valueOf(textureId), textureKey);
    }

    private static List<Element> parseElements(JsonArray elementsJson, int textureWidth, int textureHeight) {
        List<Element> elements = new ArrayList<>();
        for (JsonElement elementJson : elementsJson) {
            Element element = parseElement(elementJson.getAsJsonObject(), textureWidth, textureHeight);
            elements.add(element);
        }
        return elements;
    }

    private static Element parseElement(JsonObject elementJson, int textureWidth, int textureHeight) {
        Vector3Float from = parseVec3(elementJson.get("from").getAsJsonArray());
        Vector3Float to = parseVec3(elementJson.get("to").getAsJsonArray());
        Element.Builder builder = Element.builder()
                .from(from)
                .to(to)
                .rotation(parseElementRotation(elementJson));

        JsonObject faces = elementJson.get("faces").getAsJsonObject();
        for (CubeFace type : CubeFace.values()) {
            JsonObject elementFace = faces.get(type.name().toLowerCase())
                    .getAsJsonObject();
            builder.face(type, parseElementFace(elementFace, textureWidth, textureHeight));
        }

        return builder.build();
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

    private static ElementFace parseElementFace(JsonObject elementFace,
                                                int textureWidth,
                                                int textureHeight) {
        Vector4Float uv = parseVec4(elementFace.get("uv").getAsJsonArray());
        uv = new Vector4Float(
                uv.x() / textureWidth,
                uv.y() / textureHeight,
                uv.x2() / textureWidth,
                uv.y2() / textureHeight
        );
        // TODO: texture property might not always exist
        int textureId = elementFace.get("texture").getAsInt();
        return ElementFace.builder()
                .uv(uv)
                .texture("#" + textureId)
                .build();
    }
}
