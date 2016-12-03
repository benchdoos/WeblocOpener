package com.doos.commons.core;

import com.doos.commons.ApplicationConstants;
import com.doos.commons.registry.RegistryCanNotReadInfoException;
import com.doos.commons.registry.RegistryCanNotWriteInfoException;
import com.doos.commons.registry.RegistryManager;

/**
 * Created by Eugene Zrazhevsky on 20.11.2016.
 *
 * {@code SettingsManager} manages settings of application. It uses registry of Windows.
 */
public class SettingsManager {

    //You can not call this, method can be deleted
    public static void loadInfo() throws RegistryCanNotReadInfoException, RegistryCanNotWriteInfoException {
        RegistryManager.getAppNameValue();
        RegistryManager.getAppVersionValue();
        RegistryManager.getInstallLocationValue();
        RegistryManager.getURLUpdateValue();

        //Fixes registry after update (if needed)
        if (!RegistryManager.getAppVersionValue().equals(ApplicationConstants.APP_VERSION)) {
            RegistryManager.setAppVersionValue(ApplicationConstants.APP_VERSION);
        }
    }

}
