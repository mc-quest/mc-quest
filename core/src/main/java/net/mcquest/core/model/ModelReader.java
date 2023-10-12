package net.mcquest.core.model;

import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.reader.blockbench.BBModelReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

public class ModelReader {
    private static team.unnamed.hephaestus.reader.ModelReader reader = BBModelReader.blockbench();

    public static Model read(InputStream inputStream) {
        try {
            return reader.read(inputStream);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
