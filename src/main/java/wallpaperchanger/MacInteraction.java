package wallpaperchanger;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

class MacInteraction implements OSInteraction {
    @Override
    public Exception changeWallpaper(File file) throws Exception {
        String fileLocation = file.getAbsolutePath().replace("\"", "\\\"");
        String[] args = {
                "osascript",
                "-e", "tell application \"Finder\"",
                "-e", "set desktop picture to POSIX file \"" + fileLocation + "\"",
                "-e", "end tell"
        };
        Runtime runtime = Runtime.getRuntime();
        Process p = runtime.exec(args);
        int errorCode = p.waitFor();
        if (errorCode != 0) {
            return new RuntimeException("Couldn't change wallpaper, error returned!");
        }

        return null;
    }
}
