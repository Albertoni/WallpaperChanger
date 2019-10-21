package wallpaperchanger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import org.json.*;

public class WallpaperChanger {
    public enum OS {
        WINDOWS,
        LINUX,
        UNIX,
        MAC,
        OTHER
    }

    public static void main(String[] arg) {
        // Get OS
        OS os = getOS();
        // Get list of URLs
        // MVP: How about just the one URL
        URL file = getURLs();
        System.out.println(file);
        // register tray icon

        // Register timer to run

        // download file

        // set wallpaper

        // delete file?
    }

    private static OS getOS() {
        OS os;

        try {
            String osName = System.getProperty("os.name");
            if (osName == null) {
                throw new IOException("os.name not found");
            }
            osName = osName.toLowerCase(Locale.ENGLISH);
            if (osName.contains("windows")) {
                os = OS.WINDOWS;
            } else if (osName.contains("linux")) {
                os = OS.LINUX;
            } else if (osName.contains("mac os")) {
                os = OS.MAC;
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
                os = OS.UNIX;
            } else {
                os = OS.OTHER;
            }

        } catch (Exception ex) {
            os = OS.OTHER;
        }

        return os;
    }

    private static URL getURLs() {
        try {
            // return new File(MyClass.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
            File jsonFile = new File(WallpaperChanger.class.getProtectionDomain().getCodeSource().getLocation().toURI() + "config.json");
            String json = new String(Files.readAllBytes(jsonFile.toPath()), StandardCharsets.UTF_8);
            JSONObject obj = new JSONObject(json);
            JSONArray arr = obj.getJSONArray("urls");
            // TODO: parse things
            String str = arr.getJSONObject(0).getString("url");
            return new URL(str);
        } catch (URISyntaxException e) {
            System.out.println("Bad URI - Check folder and jar name for weird characters");
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            System.out.println("Config file not found.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Couldn't read the config file.");
            e.printStackTrace();
        }
        return null;
    }
}
