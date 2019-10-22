package wallpaperchanger;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

class WindowsInteraction implements OSInteraction {
    @Override
    public void registerTrayIcon(URL icon) throws IOException, AWTException {

    }

    @Override
    public Process changeWallpaper(File file) throws Exception {
        // TODO: Investigate need to save at a more permanent location

        return null;
    }
}
