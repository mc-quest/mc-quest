package com.mcquest.server.asset;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mcquest.server.util.JsonUtility;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.reader.ModelReader;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;

public class Asset {
    private final ClassLoader classLoader;
    private final String path;

    public Asset(ClassLoader classLoader, String path) {
        this.classLoader = classLoader;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex == -1) {
            return null;
        }
        return path.substring(dotIndex + 1);
    }

    public InputStream getStream() {
        return classLoader.getResourceAsStream(path);
    }

    public JsonElement readJson() {
        try (InputStreamReader reader = new InputStreamReader(getStream())) {
            return JsonParser.parseReader(reader);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public <T> T deserializeJson(@NotNull Class<T> classOfT) {
        try (InputStreamReader reader = new InputStreamReader(getStream())) {
            return JsonUtility.parse(reader, classOfT);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public BufferedImage readImage() {
        try (InputStream stream = getStream()) {
            return ImageIO.read(stream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Model readModel(ModelReader reader) {
        try (InputStream stream = getStream()) {
            return reader.read(stream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
