package com.github.benchdoos.weblocopener.update;

import com.github.benchdoos.weblocopener.base.BaseUnitTest;
import com.github.benchdoos.weblocopener.utils.UpdateHelperUtil;
import com.github.benchdoos.weblocopenercore.domain.version.UpdateInfo;
import org.apache.commons.collections.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdaterHelperTest extends BaseUnitTest {

  @Test
  void getUpdateInfoFromUrl() throws MalformedURLException {

    final URL url = getClass().getResource("/json/update-info.json");

    final UpdateInfo updateInfo = assertDoesNotThrow(() -> UpdateHelperUtil.getUpdateInfoFromUrl(url));
    assertNotNull(updateInfo);
    assertNotNull(updateInfo.features());
    assertTrue(CollectionUtils.isNotEmpty(updateInfo.features()));
    assertEquals(2, updateInfo.features().size());
    assertHasExpectedBody(updateInfo.features());

    assertTrue(CollectionUtils.isNotEmpty(updateInfo.improvements()));
    assertEquals(1, updateInfo.improvements().size());
    assertHasExpectedBody(updateInfo.improvements());

    assertTrue(CollectionUtils.isNotEmpty(updateInfo.fixes()));
    assertEquals(3, updateInfo.fixes().size());
    assertHasExpectedBody(updateInfo.fixes());

  }

  private void assertHasExpectedBody(final List<UpdateInfo.Modification> modifications) {
    for (UpdateInfo.Modification info : modifications) {
      final Map<String, String> descriptionMap = info.description();
      assertHasLanguageMessage(descriptionMap, "ru");
      assertHasLanguageMessage(descriptionMap, "en");
    }
  }

  private void assertHasLanguageMessage(final Map<String, String> descriptionMap, final String languageName) {
    assertTrue(descriptionMap.containsKey(languageName));
    assertNotNull(descriptionMap.get(languageName));
    assertTrue(StringUtils.isNotBlank(descriptionMap.get(languageName)));
  }
}