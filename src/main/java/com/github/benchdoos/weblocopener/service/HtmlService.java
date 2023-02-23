package com.github.benchdoos.weblocopener.service;

import com.github.benchdoos.weblocopenercore.domain.version.UpdateInfo;
import j2html.tags.specialized.HtmlTag;

public interface HtmlService {

  /**
   * Prepare html page by given update info
   *
   * @param updateInfo update info
   * @return prepared html hierarchy
   */
  HtmlTag prepareUpdateInfoHtmlPage(final UpdateInfo updateInfo);
}
