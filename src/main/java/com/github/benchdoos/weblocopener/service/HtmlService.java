package com.github.benchdoos.weblocopener.service;

import com.github.benchdoos.weblocopener.domain.ExtendedModificationInfo;
import j2html.tags.specialized.HtmlTag;

import java.util.List;

public interface HtmlService {

  /**
   * Prepare html page by given update info
   *
   * @param modifications list of modifications
   * @return prepared html hierarchy
   */
  HtmlTag prepareUpdateInfoHtmlPage(List<ExtendedModificationInfo> modifications);
}
