package wallpaperchanger;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

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

    // TODO: Linux / Win - https://stackoverflow.com/questions/19779980/is-it-possible-to-change-the-desktop-background-with-java-for-different-operatin?noredirect=1&lq=1

    public static void main(String[] arg) {
        // Get OSType
        OSType osType = getOS();
        OSInteraction OSInterface = getOSInteraction(osType);
        // Get list of URLs
        // MVP: How about just the one URL
        URL wallpaperUrl = getURLs();

        // register tray icon
        registerTrayIcon(WallpaperChanger.class.getClassLoader().getResource("icon.png"));


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

    private static void registerTrayIcon(URL resource) {
        final TrayIcon trayIcon;

        final MouseListener mouseListener = new MouseListener() {

            public void mouseClicked(MouseEvent e) {
                System.out.println("Tray Icon - Mouse clicked!");
            }

            public void mouseEntered(MouseEvent e) {
                System.out.println("Tray Icon - Mouse entered!");
            }

            public void mouseExited(MouseEvent e) {
                System.out.println("Tray Icon - Mouse exited!");
            }

            public void mousePressed(MouseEvent e) {
                System.out.println("Tray Icon - Mouse pressed!");
            }

            public void mouseReleased(MouseEvent e) {
                System.out.println("Tray Icon - Mouse released!");
            }
        };

        final ActionListener exitListener = e -> {
            System.out.println("Exiting...");
            System.exit(0);
        };

        try {
            if(SystemTray.isSupported()) {
                PopupMenu popup = new PopupMenu();
                MenuItem defaultItem = new MenuItem("Exit");
                defaultItem.addActionListener(exitListener);
                popup.add(defaultItem);

                trayIcon = new TrayIcon(ImageIO.read(resource), "Wallpaper Changer", popup);

                final ActionListener actionListener = e -> trayIcon.displayMessage("Action Event",
                        "An Action Event Has Been Performed!",
                        TrayIcon.MessageType.INFO);

                trayIcon.setImageAutoSize(true);
                trayIcon.addActionListener(actionListener);
                trayIcon.addMouseListener(mouseListener);
                SystemTray tray = SystemTray.getSystemTray();
                tray.add(trayIcon);


            } else {
                // This should never happen, unless you're not running in graphical mode
                // In which case how do you even have wallpapers
                throw new UnsupportedOperationException();
            }
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
