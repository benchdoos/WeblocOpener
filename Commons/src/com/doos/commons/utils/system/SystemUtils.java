package com.doos.commons.utils.system;

import com.doos.commons.utils.Internal;
import org.apache.log4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

import static com.doos.commons.utils.Logging.getCurrentClassName;

/**
 * Created by Eugene Zrazhevsky on 03.12.2016.
 */
public class SystemUtils {
    static final String MINIMUM_WINDOWS_VERSION = "5.1"; //Windows XP
    private static final Logger log = Logger.getLogger(getCurrentClassName());
    private static final String CURRENT_OS_SYSTEM_NAME = System.getProperty("os.name").toLowerCase();
    private static final String CURRENT_OS_VERSION = System.getProperty("os.version");
    private static final OS[] SUPPORTED = new OS[]{OS.WINDOWS};

    public static String getRealSystemArch() {
        String arch = System.getenv("PROCESSOR_ARCHITECTURE");
        String wow64Arch = System.getenv("PROCESSOR_ARCHITEW6432");

        return arch.endsWith("64")
                || wow64Arch != null && wow64Arch.endsWith("64")
                ? "64" : "32";
    }

    public static void checkIfSystemIsSupported()
            throws UnsupportedOsSystemException, UnsupportedSystemVersionException {
        log.debug("Checking if system is supported.");
        log.info("\n" + getSystemParameters());
        if (isWindows()) {
            checkWindows();
        } else if (isMac()) {
            if (!isSupported(OS.MAC_OS)) {
                log.warn("Mac OS is not supported yet.");
                throw new UnsupportedOsSystemException();
            }
        } else if (isUnix()) {
            if (!isSupported(OS.UNIX)) {
                log.warn("UNIX is not supported yet.");
                throw new UnsupportedOsSystemException();
            }
        } else if (isSolaris()) {
            if (!isSupported(OS.SOLARIS)) {
                log.warn("Solaris is not supported yet.");
                throw new UnsupportedOsSystemException();
            }
        } else {
            log.warn(
                    System.getProperty("os.name") + " v" + System.getProperty("os.version") + " is not supported yet.");
            throw new UnsupportedOsSystemException();
        }
    }

    private static void checkWindows() throws UnsupportedOsSystemException, UnsupportedSystemVersionException {
        if (!isSupported(OS.WINDOWS)) {
            log.warn("Windows is not supported yet.");
            throw new UnsupportedOsSystemException();
        } else {
            if (Internal.versionCompare(CURRENT_OS_VERSION, MINIMUM_WINDOWS_VERSION) < 0) {
                log.warn("Windows " + CURRENT_OS_VERSION + " is not supported yet.");
                throw new UnsupportedSystemVersionException();
            }
        }
    }

    private static boolean isSupported(OS os) {
        for (OS aSUPPORTED : SUPPORTED) {
            if (os.equals(aSUPPORTED)) {
                return true;
            }
        }
        return false;
    }


    private static boolean isWindows() {
        return (CURRENT_OS_SYSTEM_NAME.contains("win"));
    }

    private static boolean isMac() {
        return (CURRENT_OS_SYSTEM_NAME.contains("mac"));
    }

    private static boolean isUnix() {
        return (CURRENT_OS_SYSTEM_NAME.contains("nix")
                || CURRENT_OS_SYSTEM_NAME.contains("nux")
                || CURRENT_OS_SYSTEM_NAME.contains("aix"));
    }

    private static boolean isSolaris() {
        return (CURRENT_OS_SYSTEM_NAME.contains("sunos"));
    }

    public static String getSystemParameters() {
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        HardwareAbstractionLayer hal = si.getHardware();
        int mb = 1024 * 1024;

        StringBuilder sb = new StringBuilder();

        sb.append("==========================System=========================").append("\n");
        sb.append("System:").append("\n");
        sb.append("\tOS: ").append(os.getManufacturer()).append(" ").append(os.getFamily())
                .append(" Version: ").append(os.getVersion()).append(" x" + getRealSystemArch()).append("\n");

        sb.append("Hardware:").append("\n");
        sb.append("\tCore: ").append(hal.getProcessor()).append(")").append("\n");
        sb.append("\tMemory: ").append(hal.getMemory().getAvailable() / mb).append(" (total:")
                .append(hal.getMemory().getTotal() / mb).append(") MB").append("\n");

        sb.append("Java:").append("\n");
        sb.append("\tJava version: ").append(System.getProperty("java.specification.version")).append("(").append
                (System.getProperty("java.version")).append(")").append("\n");
        sb.append("\t").append(System.getProperty("java.runtime.name")).append(" v").append(System.getProperty("java" +
                ".vm.version"))
                .append("\n");

        sb.append("User:").append("\n");
        sb.append("\tName: ").append(System.getProperty("user.name")).append(" Home: ")
                .append(System.getProperty("user.home")).append("\n");
        sb.append("\tTime zone: ").append(System.getProperty("user.timezone")).append(" (")
                .append(System.getProperty("user.country")).append(") language: ")
                .append(System.getProperty("user.language")).append("\n");

        sb.append("=========================================================").append("\n");

        return sb.toString();
    }

    enum OS {WINDOWS, MAC_OS, UNIX, SOLARIS}
}
