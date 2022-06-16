package com.mcquest.server.util;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResourceLoader {
    private static final Gson gson = new Gson();

    public static InputStream getResourceAsStream(String resourcePath) {
        return ResourceLoader.class.getClassLoader().getResourceAsStream(resourcePath);
    }

    /**
     * Deserializes a JSON resource.
     *
     * @param resourcePath the path of the resource file, relative to the
     *                     resources directory
     * @param classOfT     the class of the object being deserialized
     * @param <T>          the type of the object being deserialized
     * @return the deserialized object
     */
    public static <T> T deserializeJsonResource(String resourcePath, Class<T> classOfT) {
        InputStream inputStream = getResourceAsStream(resourcePath);
        InputStreamReader reader = new InputStreamReader(inputStream);
        return gson.fromJson(reader, classOfT);
    }

    /**
     * Extracts all resources inside a directory of resources to a directory on
     * the file system.
     *
     * @param resourcesPath   the path to the directory of resources, relative
     *                        to the resources directory
     * @param destinationPath the relative path to where the resources will be
     *                        extracted
     */
    public static void extractResources(String resourcesPath, String destinationPath) {
        try {
            URI resourcesUri = ResourceLoader.class.getResource("/" + resourcesPath).toURI();
            FileSystem fileSystem = null;

            // Only create a new FileSystem if running from a jar.
            if (resourcesUri.getScheme().equals("jar")) {
                fileSystem = FileSystems.newFileSystem(resourcesUri, Map.of("create", "true"));
            }

            Path resources = Paths.get(resourcesUri);
            Path destination = Path.of(destinationPath);

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
    public static List<String> getResources(String resourcesPath) {
        try {
            URI resourcesUri = ResourceLoader.class.getResource("/" + resourcesPath).toURI();

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
                    String resourcePath = resourcesPath + "/" + resources.relativize(file);
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
