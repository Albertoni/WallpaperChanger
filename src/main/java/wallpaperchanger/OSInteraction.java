package wallpaperchanger;

import java.awt.AWTException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public interface OSInteraction {
    Exception changeWallpaper(File file) throws Exception;

    // TODO: Interface code?
}
