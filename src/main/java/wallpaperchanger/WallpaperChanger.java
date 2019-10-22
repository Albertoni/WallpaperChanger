package wallpaperchanger;

import java.awt.AWTException;
import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.json.*;

import javax.imageio.ImageIO;

public class WallpaperChanger {
    public enum OSType {
        WINDOWS,
        LINUX,
        UNIX,
        MAC,
        OTHER
    }

    // TODO: Put a message "Designed by rawpixel.com / Freepik" on the about window

    // TODO: Fix up all exceptions when we got the GUI down

    public static void main(String[] arg) {
        // Get OSType
        OSType osType = getOS();
        OSInteraction OSInterface = getOSInteraction(osType);
        // Get list of URLs
        // MVP: How about just the one URL
        URL wallpaperUrl = getURLs();

        // register tray icon
        try {
            OSInterface.registerTrayIcon(WallpaperChanger.class.getClassLoader().getResource("icon.jpg"));
        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }

        // Register timer to run
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // download file
                final File downloadedImage = downloadFile(wallpaperUrl);
                // set wallpaper
                try {
                    final Exception ex = OSInterface.changeWallpaper(downloadedImage);
                    if (ex != null) {
                        throw ex;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // delete file?
                if (downloadedImage == null) {
                    throw new RuntimeException("Downloaded image is null!");
                }
                downloadedImage.deleteOnExit();
            }
        }, 0, 30*60*1000);
    }

    private static OSInteraction getOSInteraction(OSType osType) {
        switch (osType) {
            case WINDOWS:
                return new WindowsInteraction();
            case LINUX:
                return new LinuxInteraction();
            case MAC:
                return new MacInteraction();
            default:
                throw new UnsupportedOperationException();
        }
    }

    private static OSType getOS() {
        final OSType osType;

        try {
            String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
            if (osName.contains("windows")) {
                osType = OSType.WINDOWS;
            } else if (osName.contains("linux")) {
                osType = OSType.LINUX;
            } else if (osName.contains("mac os")) {
                osType = OSType.MAC;
            } else if (osName.contains("sun os")
                    || osName.contains("sunos")
                    || osName.contains("solaris")
                    || osName.contains("hp-ux")
                    || osName.contains("aix")
                    || osName.contains("mpe/ix")
                    || osName.contains("freebsd")
                    || osName.contains("irix")
                    || osName.contains("digital unix")
                    || osName.contains("unix")) {
                osType = OSType.UNIX;
            } else {
                osType = OSType.OTHER;
            }
        } catch (NullPointerException ex) {
            throw new UnsupportedOperationException("os.name property not found!");
        }

        return osType;
    }

    private static URL getURLs() {
        try {
            // Todo: load from the folder, this should work
            // Todo: Actually, maybe a dynamic list?
            // return new File(MyClass.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            // File jsonFile = new File(WallpaperChanger.class.getProtectionDomain().getCodeSource().getLocation().toURI() + "config.json");

            // Get file
            URL x = WallpaperChanger.class.getClassLoader().getResource("config.json");
            assert x != null;
            File jsonFile = new File(x.getFile());

            // Read file as blob
            String json = new String(Files.readAllBytes(jsonFile.toPath()), StandardCharsets.UTF_8);

            // Parse the json data
            JSONObject obj = new JSONObject(json);
            JSONArray arr = obj.getJSONArray("urls");

            // Todo: get all files
            String str = arr.getJSONObject(0).getString("url");

            return new URL(str);
        /*} catch (URISyntaxException e) {
            System.out.println("Bad URI - Check folder and jar name for weird characters");
            e.printStackTrace();*/
        } catch (FileNotFoundException e) {
            System.out.println("Config file not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Couldn't read the config file.");
            e.printStackTrace();
        }
        return null;
    }

    private static File downloadFile(URL imageUrl) {
        try {
            File tempFile = File.createTempFile("tempWallpaper", "");
            ImageIO.write(ImageIO.read(imageUrl), "jpg", tempFile);

            return tempFile;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
