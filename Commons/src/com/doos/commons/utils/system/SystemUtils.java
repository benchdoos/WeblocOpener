package com.doos.commons.utils.system;

import com.doos.commons.utils.Internal;

/**
 * Created by Eugene Zrazhevsky on 03.12.2016.
 */
public class SystemUtils {
    static final String MINIMUM_WINDOWS_VERSION = "5.1"; //Windows XP
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
        if (isWindows()) {
            checkWindows();
        } else if (isMac()) {
            if (!isSupported(OS.MAC_OS)) {
                throw new UnsupportedOsSystemException();
            }
        } else if (isUnix()) {
            if (!isSupported(OS.UNIX)) {
                throw new UnsupportedOsSystemException();
            }
        } else if (isSolaris()) {
            if (!isSupported(OS.SOLARIS)) {
                throw new UnsupportedOsSystemException();
            }
        } else {
            throw new UnsupportedOsSystemException();
        }
    }

    private static void checkWindows() throws UnsupportedOsSystemException, UnsupportedSystemVersionException {
        if (!isSupported(OS.WINDOWS)) {
            throw new UnsupportedOsSystemException();
        } else {
            if (Internal.versionCompare(CURRENT_OS_VERSION, MINIMUM_WINDOWS_VERSION) < 0) {
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

    enum OS {WINDOWS, MAC_OS, UNIX, SOLARIS}
}
