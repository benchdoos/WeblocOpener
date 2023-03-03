package com.github.benchdoos.weblocopener.service.impl;

import com.github.benchdoos.weblocopener.domain.ExtendedModificationInfo;
import com.github.benchdoos.weblocopener.service.HtmlService;
import com.github.benchdoos.weblocopenercore.domain.version.UpdateInfo;
import com.github.benchdoos.weblocopenercore.service.settings.impl.LocaleSettings;
import com.github.benchdoos.weblocopenercore.service.translation.Translation;
import j2html.TagCreator;
import j2html.tags.specialized.HtmlTag;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

@Log4j2
public class DefaultHtmlService implements Serializable, HtmlService {
  @Override
  public HtmlTag prepareUpdateInfoHtmlPage(final UpdateInfo updateInfo) {
    final List<ExtendedModificationInfo> modifications = prepareRawList(updateInfo);

    final Locale locale = Translation.getSelectedLocale();
    return TagCreator.html(
            TagCreator.body(
                TagCreator.table(
                    TagCreator.each(
                        modifications,
                        value -> {
                          final ExtendedModificationInfo.ModificationType modificationType =
                              value.type();

                          final Map<String, String> description =
                              value.modification().description();
                          final String srcMessage =
                              description.get(locale.getLanguage().toLowerCase());
                          final String message;
                          if (StringUtils.isNotBlank(srcMessage)) {
                            message = srcMessage;
                          } else {
                            message =
                                description.get(
                                    LocaleSettings.getDefaultLocale().getLanguage().toLowerCase());
                          }
                          final String tdStyle =
                              "border-radius: 5px; padding: 5px 10px 5px 5px; width: 120px; color: white; "
                                  + "font-weight: bold; text-align:right;";
                          final String typeValue;
                          final String backgroundColor;
                          final String bundle = "UpdateDialog";
                          switch (modificationType) {
                            case WARNING -> {
                              typeValue = Translation.get(bundle, "warningType");
                              backgroundColor = "background-color:#b56219;";
                            }
                            case IMPROVEMENT -> {
                              typeValue = Translation.get(bundle, "improvementType");
                              backgroundColor = "background-color:#4f73a5;";
                            }
                            case BUGFIX -> {
                              typeValue = Translation.get(bundle, "fixType");
                              backgroundColor = "background-color:#d36767;";
                            }
                            default -> {
                              typeValue = Translation.get(bundle, "featureType");
                              backgroundColor = "background-color:#439443;";
                            }
                          }
                          return TagCreator.tr(
                              TagCreator.td(TagCreator.span(typeValue))
                                  .withStyle(tdStyle + backgroundColor),
                              TagCreator.td(TagCreator.rawHtml(message)));
                        }))))
        .attr("lang", locale.getLanguage());
  }

  @NotNull
  private static List<ExtendedModificationInfo> prepareRawList(final UpdateInfo updateInfo) {
    final List<ExtendedModificationInfo> modifications = new ArrayList<>();

    if (CollectionUtils.isNotEmpty(updateInfo.warnings())) {
      updateInfo
          .warnings()
          .forEach(
              i ->
                  modifications.add(
                      new ExtendedModificationInfo(
                          ExtendedModificationInfo.ModificationType.WARNING, i)));
    }

    if (CollectionUtils.isNotEmpty(updateInfo.features())) {
      modifications.addAll(
          updateInfo.features().stream()
              .map(
                  i ->
                      new ExtendedModificationInfo(
                          ExtendedModificationInfo.ModificationType.FEATURE, i))
              .toList());
    }

    if (CollectionUtils.isNotEmpty(updateInfo.improvements())) {
      modifications.addAll(
          updateInfo.improvements().stream()
              .map(
                  i ->
                      new ExtendedModificationInfo(
                          ExtendedModificationInfo.ModificationType.IMPROVEMENT, i))
              .toList());
    }

    if (CollectionUtils.isNotEmpty(updateInfo.fixes())) {
      modifications.addAll(
          updateInfo.fixes().stream()
              .map(
                  i ->
                      new ExtendedModificationInfo(
                          ExtendedModificationInfo.ModificationType.BUGFIX, i))
              .toList());
    }

    log.debug("Formed modification list: {}", modifications);
    return modifications;
  }
}
