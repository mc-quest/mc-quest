package com.mcquest.server.asset;

public class AssetTypeException extends RuntimeException {
    public AssetTypeException(String expected, String actual) {
        super(message(expected, actual));
    }

    private static String message(String expected, String actual) {
        return "Expected " + expected + " but was " + actual;
    }
}
