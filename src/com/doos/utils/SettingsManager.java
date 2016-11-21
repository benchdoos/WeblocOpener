package com.doos.utils;

import com.doos.ApplicationConstants;
import com.doos.utils.registry.RegistryCanNotReadInfoException;
import com.doos.utils.registry.RegistryCanNotWriteInfoException;
import com.doos.utils.registry.RegistryManager;

import java.util.Properties;

/**
 * Created by Eugene Zrazhevsky on 20.11.2016.
 */
public class SettingsManager {

    public static void updateInfo(Properties properties) {
        try {
            final String property = properties.getProperty(RegistryManager.KEY_AUTO_UPDATE);
            if (property != null) {
                RegistryManager.setAutoUpdateActive(Boolean.parseBoolean(property));
            } else {
                RegistryManager.setAutoUpdateActive(true);
            }
            RegistryManager.setAppVersionValue(ApplicationConstants.APP_VERSION);
        } catch (RegistryCanNotWriteInfoException e) {
            e.printStackTrace();
        }
    }

    public static Properties loadInfo() throws RegistryCanNotReadInfoException, RegistryCanNotWriteInfoException {
        Properties result = new Properties();
        result.setProperty(RegistryManager.KEY_AUTO_UPDATE,
                Boolean.toString(RegistryManager.isAutoUpdateActive()));

        result.setProperty(RegistryManager.KEY_CURRENT_VERSION, RegistryManager.getAppVersionValue());

        //Fixes registry after update (if needed)
        if (!RegistryManager.getAppVersionValue().equals(ApplicationConstants.APP_VERSION)) {
            RegistryManager.setAppVersionValue(ApplicationConstants.APP_VERSION);
        }
        result.setProperty(RegistryManager.KEY_INSTALL_LOCATION, RegistryManager.getInstallLocationValue());
        return result;
    }
}
