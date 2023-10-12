package net.mcquest.core.asset;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AssetDirectory {
    private final ClassLoader classLoader;
    private final String path;

    private AssetDirectory(ClassLoader classLoader, String path) {
        this.classLoader = classLoader;
        this.path = path;
    }

    public static AssetDirectory of(ClassLoader classLoader, String path) {
        return new AssetDirectory(classLoader, path);
    }

    public String getPath() {
        return path;
    }

    /**
     * Returns all assets in this directory, including assets that are within
     * subdirectories of this directory.
     */
    public List<Asset> getAssets() {
        try {
            URI dirUri = classLoader.getResource(path).toURI();

            // Only create a new FileSystem if running from a jar.
            FileSystem fileSystem = null;
            if (dirUri.getScheme().equals("jar")) {
                fileSystem = FileSystems.newFileSystem(dirUri, Collections.emptyMap());
            }

            List<Asset> assets = new ArrayList<>();
            Path dirRootPath = Paths.get(dirUri);
            Files.walkFileTree(dirRootPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    String assetPath = path + "/" + dirRootPath.relativize(file);
                    assets.add(Asset.of(classLoader, assetPath));
                    return FileVisitResult.CONTINUE;
                }
            });

            if (fileSystem != null) {
                fileSystem.close();
            }

            return assets;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Extracts all assets in this directory to the given destination,
     * including assets that are within subdirectories of this directory.
     */
    public void extractAssets(File destination) {
        try {
            URI dirUri = classLoader.getResource(path).toURI();
            FileSystem fileSystem = null;

            // Only create a new FileSystem if running from a jar.
            if (dirUri.getScheme().equals("jar")) {
                fileSystem = FileSystems.newFileSystem(dirUri, Map.of("create", "true"));
            }

            Path dirRootPath = Paths.get(dirUri);
            Path destRootPath = destination.toPath();

            Files.walkFileTree(dirRootPath, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(
                        Path dirPath, BasicFileAttributes attrs) throws IOException {
                    Path destPath = destRootPath.resolve(dirRootPath.relativize(dirPath));
                    Files.createDirectories(destPath);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(
                        Path assetPath, BasicFileAttributes attrs) throws IOException {
                    Path destPath = destRootPath.resolve(dirRootPath.relativize(assetPath));
                    Files.copy(assetPath, destPath, StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });

            if (fileSystem != null) {
                fileSystem.close();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
    }
}
