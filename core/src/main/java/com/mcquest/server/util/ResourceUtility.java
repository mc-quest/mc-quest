package com.mcquest.server.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.reader.ModelReader;
import team.unnamed.hephaestus.reader.blockbench.BBModelReader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Utilities for reading project resources.
 */
public class ResourceUtility {
    private static final Gson GSON = new GsonBuilder().serializeNulls().create();
    private static final ModelReader modelReader = BBModelReader.blockbench();

    private static ClassLoader classLoader() {
        return ResourceUtility.class.getClassLoader();
    }

    public static String jsonStringify(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T parseJson(String json, Class<T> classOfT) {
        return GSON.fromJson(json, classOfT);
    }

    /**
     * Returns an InputStream for the resource with the given path. The caller
     * is responsible for closing the stream.
     */
    public static InputStream getStream(@NotNull String path) {
        return classLoader().getResourceAsStream(path);
    }

    public static Callable<InputStream> streamSupplier(@NotNull String path) {
        return () -> getStream(path);
    }

    public static JsonElement readJson(@NotNull String path) {
        return readJson(streamSupplier(path));
    }

    public static JsonElement readJson(@NotNull Callable<InputStream> streamSupplier) {
        try {
            InputStream stream = streamSupplier.call();
            Reader reader = new InputStreamReader(stream);
            JsonElement json = JsonParser.parseReader(reader);
            stream.close();
            return json;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Model readModel(String path) {
        try {
            InputStream inputStream = getStream(path);
            Model model = modelReader.read(inputStream);
            inputStream.close();
            return model;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Image readImage(String path) {
        try {
            InputStream inputStream = getStream(path);
            Image image = ImageIO.read(inputStream);
            inputStream.close();
            return image;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deserializes a JSON resource.
     *
     * @param path     the path of the resource file, relative to the
     *                 resources directory
     * @param classOfT the class of the object being deserialized
     * @param <T>      the type of the object being deserialized
     * @return the deserialized object
     */
    public static <T> T deserializeJson(@NotNull String path,
                                        @NotNull Class<T> classOfT) {
        try {
            InputStream inputStream = getStream(path);
            InputStreamReader reader = new InputStreamReader(inputStream);
            T object = GSON.fromJson(reader, classOfT);
            inputStream.close();
            return object;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extracts all resources inside a directory of resources to a directory on
     * the file system.
     *
     * @param srcPath  the path to the directory of resources, relative
     *                 to the resources directory
     * @param destPath the path to where the resources will be
     *                 extracted
     */
    public static void extractResources(@NotNull String srcPath,
                                        @NotNull String destPath) {
        try {
            URI resourcesUri = classLoader().getResource(srcPath).toURI();
            FileSystem fileSystem = null;

            // Only create a new FileSystem if running from a jar.
            if (resourcesUri.getScheme().equals("jar")) {
                fileSystem = FileSystems.newFileSystem(resourcesUri, Map.of("create", "true"));
            }

            Path resources = Paths.get(resourcesUri);
            Path destination = Path.of(destPath);

            Files.walkFileTree(resources, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path currentDestination = destination.resolve(resources.relativize(dir).toString());
                    Files.createDirectories(currentDestination);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Path to = destination.resolve(resources.relativize(file).toString());
                    Files.copy(file, to, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });

            if (fileSystem != null) {
                fileSystem.close();
            }
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the resources in a directory given by a path.
     */
    public static List<String> getResources(@NotNull String path) {
        try {
            URI resourcesUri = ResourceUtility.class.getResource("/" + path).toURI();

            // Only create a new FileSystem if running from a jar.
            FileSystem fileSystem = null;
            if (resourcesUri.getScheme().equals("jar")) {
                fileSystem = FileSystems.newFileSystem(resourcesUri, Collections.emptyMap());
            }

            Path resources = Paths.get(resourcesUri);

            List<String> results = new ArrayList<>();
            Files.walkFileTree(resources, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    String resourcePath = path + "/" + resources.relativize(file);
                    results.add(resourcePath);
                    return FileVisitResult.CONTINUE;
                }
            });

            if (fileSystem != null) {
                fileSystem.close();
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
