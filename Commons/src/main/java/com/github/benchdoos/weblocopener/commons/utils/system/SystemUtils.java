package com.github.benchdoos.weblocopener.commons.utils.system;

import com.github.benchdoos.weblocopener.commons.core.ApplicationConstants;
import com.github.benchdoos.weblocopener.commons.utils.Internal;
import org.apache.log4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import static com.github.benchdoos.weblocopener.commons.utils.Logging.getCurrentClassName;

/*import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;*/

/**
 * Created by Eugene Zrazhevsky on 03.12.2016.
 */
public class SystemUtils {
    static final String MINIMUM_WINDOWS_VERSION = "5.1"; //Windows XP
    private static final OS[] SUPPORTED = new OS[]{OS.WINDOWS};
    private static final Logger log = Logger.getLogger(getCurrentClassName());
    private static final String CURRENT_OS_VERSION = getOsVersion();
    public static final boolean IS_WINDOWS_XP = isWindows()
            && Internal.versionCompare(SystemUtils.CURRENT_OS_VERSION, "5.1") >= 0
            && Internal.versionCompare(SystemUtils.CURRENT_OS_VERSION, "6.0") < 0;
    private static final OS CURRENT_OS = getCurrentOS();

    /**
     * Returns current build version from {@link Manifest}. If build version is not available,
     * it will return application version only.
     *
     * @return build version
     */
    public static String getCurrentBuildVersion() {
        final InputStream mfStream = SystemUtils.class.getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
        Manifest mf = new Manifest();
        try {
            mf.read(mfStream);
        } catch (Exception e) {
            log.warn("Can not read MANIFEST.MF", e);
        }

        Attributes attributes = mf.getMainAttributes();
        /*for (int i = 0; i < attributes.size(); i++) {
            log.debug("attribute: " + attributes.entrySet());
        }*/

        String buildVersion = attributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
        return buildVersion != null ? buildVersion : ApplicationConstants.APP_VERSION;

    }

    private static String getApplicationInfo() {
        return "\n==========================Application=========================" + "\r\n" +
                "\n" + ApplicationConstants.WEBLOC_OPENER_APPLICATION_NAME + " v." + getCurrentBuildVersion() +
                "\n=========================================================" + "\r\n";
    }

    private static String getOsName() {
        return System.getProperty("os.name").toLowerCase();
    }

    private static String getOsVersion() {
        return System.getProperty("os.version");
    }

    public static String getRealSystemArch() {
        String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

        return arch.endsWith("64")
                || wow64Arch != null && wow64Arch.endsWith("64")
                ? "64" : "32";
    }

    public static void checkIfSystemIsSupported() throws UnsupportedOsSystemException, UnsupportedSystemVersionException {
        initSystem();
        if (isSupported(CURRENT_OS)) {
            if (CURRENT_OS == OS.WINDOWS) {
                checkWindows();
            }
        } else {
            log.warn(getOsName() + " v" + getOsVersion() + " is not supported yet.");
            throw new UnsupportedOsSystemException();
        }
    }

    private static void initSystem() {
        log.info("Initializing system...");
        getCurrentOS();
        log.info(getSystemParameters());
        log.info(getApplicationInfo());
    }

    private static void checkWindows() throws UnsupportedSystemVersionException {
        if (Internal.versionCompare(CURRENT_OS_VERSION, MINIMUM_WINDOWS_VERSION) < 0) {
            log.warn("Windows " + CURRENT_OS_VERSION + " is not supported yet.");
            throw new UnsupportedSystemVersionException();
        }
    }

    private static boolean isSupported(OS os) {
        for (OS supportedSystems : SUPPORTED) {
            if (os.equals(supportedSystems)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isWindows() {
        return (getOsName().contains("win"));
    }

    private static boolean isMac() {
        return (getOsName().contains("mac"));
    }

    private static boolean isUnix() {
        return (getOsName().contains("nix")
                || getOsName().contains("nux")
                || getOsName().contains("aix"));
    }

    private static boolean isSolaris() {
        return (getOsName().contains("sunos"));
    }

    private static String getSystemParameters() {
        try {
            SystemInfo si = new SystemInfo();
            OperatingSystem os = si.getOperatingSystem();
            HardwareAbstractionLayer hal = si.getHardware();
            int megabyte = 1024 * 1024;


            return "\n==========================System=========================" + "\r\n" +
                    "System:" + "\r\n" +
                    "\tOS: " + os.getManufacturer() + " " + os.getFamily() + " " +
                    os.getVersion() + " x" + getRealSystemArch() + "\r\n" +
                    "Hardware:" + "\r\n" +
                    "\tProcessors: " + Runtime.getRuntime().availableProcessors() + "\r\n" +
                    "\tTotal JVM memory: " + Runtime.getRuntime().maxMemory() / megabyte + " MB " +
                    "free:" + Runtime.getRuntime().freeMemory() / megabyte + " MB \r\n" +
                    "Java:" + "\r\n" +
                    "\tJava version: " + System.getProperty("java.specification.version") + "(" +
                    System.getProperty("java.version") + ")" + "\r\n" +
                    "\t" + System.getProperty("java.runtime.name") + " v" +
                    System.getProperty("java.vm.version") + "\r\n" +
                    "=========================================================" + "\r\n";
        } catch (Exception e) {
            log.warn("Could not show system parameters, proceeding", e);
            return "";
        }
    }

    private static OS getCurrentOS() {
        if (isWindows()) {
            return OS.WINDOWS;
        } else if (isMac()) {
            return OS.MAC_OS;
        } else if (isUnix()) {
            return OS.UNIX;
        } else if (isSolaris()) {
            return OS.SOLARIS;
        } else return OS.UNSUPPORTED;
    }

    enum OS {WINDOWS, MAC_OS, UNIX, SOLARIS, UNSUPPORTED}


}
