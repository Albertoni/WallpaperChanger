package wallpaperchanger;

import java.io.File;
import java.util.HashMap;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef.UINT_PTR;
import com.sun.jna.win32.*;

interface SPI extends StdCallLibrary {

    //from MSDN article
    long SPI_SETDESKWALLPAPER = 20;
    long SPIF_UPDATEINIFILE = 0x01;
    long SPIF_SENDWININICHANGE = 0x02;

    SPI INSTANCE = Native.load("user32", SPI.class, new HashMap<String, Object>() {
        {
            put(OPTION_TYPE_MAPPER, W32APITypeMapper.UNICODE);
            put(OPTION_FUNCTION_MAPPER, W32APIFunctionMapper.UNICODE);
        }
    });

    boolean SystemParametersInfo(
            UINT_PTR uiAction,
            UINT_PTR uiParam,
            String pvParam,
            UINT_PTR fWinIni
    );
}

class WindowsInteraction implements OSInteraction {
    @Override
    public Exception changeWallpaper(File file) {
        // TODO: Investigate need to save at a more permanent location
        boolean success = SPI.INSTANCE.SystemParametersInfo(
                new UINT_PTR(SPI.SPI_SETDESKWALLPAPER),
                new UINT_PTR(0),
                file.getAbsolutePath(),
                new UINT_PTR(SPI.SPIF_UPDATEINIFILE | SPI.SPIF_SENDWININICHANGE));

        if(!success) {
            return new Exception("Error setting wallpaper");
        } else {
            return null;
        }
    }
}
