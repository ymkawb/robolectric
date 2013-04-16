package org.robolectric.shadows;

import android.content.res.AssetManager;
import org.robolectric.AndroidManifest;
import org.robolectric.internal.HiddenApi;
import org.robolectric.internal.Implementation;
import org.robolectric.internal.Implements;
import org.robolectric.res.FsFile;

import java.io.IOException;
import java.io.InputStream;

import static org.robolectric.Robolectric.shadowOf;

@SuppressWarnings({"UnusedDeclaration"})
@Implements(AssetManager.class)
public final class ShadowAssetManager {
    static AssetManager bind(AssetManager assetManager, AndroidManifest androidManifest) {
        ShadowAssetManager shadowAssetManager = shadowOf(assetManager);
        if (shadowAssetManager.appManifest != null) throw new RuntimeException("ResourceLoader already set!");
        shadowAssetManager.appManifest = androidManifest;
        return assetManager;
    }

    private AndroidManifest appManifest;

    public final void __constructor__() {
    }

    @Implementation
    public final String[] list(String path) throws IOException {
        FsFile file = appManifest.getAssetsDirectory().join(path);
        if (file.isDirectory()) {
            return file.listFileNames();
        }
        return new String[0];
    }

    @Implementation
    public final InputStream open(String fileName) throws IOException {
        return appManifest.getAssetsDirectory().join(fileName).getInputStream();
    }

    @HiddenApi @Implementation
    public void setConfiguration(int mcc, int mnc, String locale,
                                              int orientation, int touchscreen, int density, int keyboard,
                                              int keyboardHidden, int navigation, int screenWidth, int screenHeight,
                                              int smallestScreenWidthDp, int screenWidthDp, int screenHeightDp,
                                              int screenLayout, int uiMode, int majorVersion) {
    }

    @HiddenApi @Implementation
    public void ensureStringBlocks() {
    }

    @HiddenApi @Implementation
    public final int createTheme() {
        return 1;
    }

    public FsFile getAssetsDirectory() {
        return appManifest.getAssetsDirectory();
    }
}
