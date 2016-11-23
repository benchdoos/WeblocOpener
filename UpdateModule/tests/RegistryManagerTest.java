
import com.doos.SettingsManager.ApplicationConstants;
import com.doos.SettingsManager.registry.RegistryManager;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Eugene Zrazhevsky on 05.11.2016.
 */
public class RegistryManagerTest {
    @Test
    public void checkForUpdates() throws Exception {
        final String expected = "C:\\Program Files (x86)\\WeblocOpener";

        String actual = RegistryManager.getInstallLocationValue();
        assertEquals("Checking com.doos.com.doos.SettingsManager.core.SettingsManager.registry APP_ROOT_HKEY", expected,
                     actual);
    }

    @Test
    public void checkSetterAutoUpdateActive() throws Exception {
        boolean expected = true;
        RegistryManager.setAutoUpdateActive(expected);

        boolean value = RegistryManager.isAutoUpdateActive();
        System.out.println(">" + value);
        assertEquals(expected, value);
    }

    @Test
    public void testIsAutoUpdate() throws Exception {
        boolean result = RegistryManager.isAutoUpdateActive();
        assertEquals(true, result);
    }

    @Ignore()
    @Test
    public void testCreateRegistryEntry() throws Exception {
        RegistryManager.createRegistryEntry("AutoUpdateEnabled", Boolean.toString(true));
        boolean isActive = RegistryManager.isAutoUpdateActive();
        assertEquals(isActive, true);
    }

    @Ignore()
    @Test
    public void testGetCurrentVersion() throws Exception {
        String expected = ApplicationConstants.APP_VERSION;
        String result = RegistryManager.getAppVersionValue();
        assertEquals(expected, result);
    }

    @Ignore()
    @Test
    public void testSetCurrentVersion() throws Exception {
        String expected = ApplicationConstants.APP_VERSION;
        RegistryManager.setAppVersionValue(expected);
        String result = RegistryManager.getAppVersionValue();
        assertEquals(expected, result);
    }
}