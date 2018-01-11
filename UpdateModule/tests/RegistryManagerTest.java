
import com.github.benchdoos.weblocopener.commons.core.ApplicationConstants;
import com.github.benchdoos.weblocopener.commons.registry.RegistryManager;
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
        assertEquals("Checking com.benchdoos.com.benchdoos.commons.core.commons.registry APP_ROOT_HKEY",
                     expected,
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

    @Test
    public void testCreateRegistryEntry() throws Exception {
        RegistryManager.createRegistryEntry("AutoUpdateEnabled", Boolean.toString(true));
        boolean isActive = RegistryManager.isAutoUpdateActive();
        assertEquals(isActive, true);
    }

    @Test
    public void testGetCurrentVersion() throws Exception {
        String expected = ApplicationConstants.APP_VERSION;
        String result = RegistryManager.getAppVersionValue();
        assertEquals(expected, result);
    }

    @Test
    public void testSetCurrentVersion() throws Exception {
        String expected = ApplicationConstants.APP_VERSION;
        RegistryManager.setAppVersionValue(expected);
        String result = RegistryManager.getAppVersionValue();
        assertEquals(expected, result);
    }
}