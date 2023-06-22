package com.mcquest.server;

import com.mcquest.core.asset.Asset;
import com.mcquest.core.asset.AssetDirectory;

public class Assets {
    public static Asset asset(String path) {
        return new Asset(Assets.class.getClassLoader(), path);
    }

    public static AssetDirectory directory(String path) {
        return new AssetDirectory(Assets.class.getClassLoader(), path);
    }
}
