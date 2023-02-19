/*
 * (C) Copyright 2019.  Eugene Zrazhevsky and others.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * Contributors:
 * Eugene Zrazhevsky <eugene.zrazhevsky@gmail.com>
 */

package com.github.benchdoos.weblocopener.update;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benchdoos.weblocopener.update.impl.UnixUpdater;
import com.github.benchdoos.weblocopener.update.impl.WindowsUpdater;
import com.github.benchdoos.weblocopenercore.domain.version.ApplicationVersion;
import com.github.benchdoos.weblocopenercore.domain.version.Beta;
import com.github.benchdoos.weblocopenercore.domain.version.UpdateInfo;
import com.github.benchdoos.weblocopenercore.service.settings.impl.InstallBetaUpdateSettings;
import com.github.benchdoos.weblocopenercore.service.settings.impl.LatestUpdateCheckSettings;
import com.github.benchdoos.weblocopenercore.utils.VersionUtils;
import com.github.benchdoos.weblocopenercore.utils.system.OS;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class UpdaterHelper {
    private static final String REPOSITORY_NAME = "benchdoos/weblocopener";
    private static final Pattern BETA_FROM_RELEASE_TITLE_PATTERN = Pattern.compile("\\(beta\\.(\\d+)\\)");

    public ApplicationVersion getLatestVersion(Updater updater) {
        final ApplicationVersion latestReleaseAppVersion = updater.getLatestReleaseAppVersion();

        new LatestUpdateCheckSettings().save(new Date());

        if (Boolean.TRUE.equals(new InstallBetaUpdateSettings().getValue())) {
            final ApplicationVersion latestBetaAppVersion = updater.getLatestBetaAppVersion();

            if (latestBetaAppVersion != null) {
                log.debug("Comparing latest beta version: {} and latest release version: {}",
                    latestBetaAppVersion.getVersion(),
                    latestReleaseAppVersion.getVersion());
                if (VersionUtils.versionCompare(latestBetaAppVersion, latestReleaseAppVersion)
                    == VersionUtils.VersionCompare.FIRST_VERSION_IS_NEWER) {
                    log.debug("Beta version is newer");
                    return latestBetaAppVersion;
                }
            }
        }
        return latestReleaseAppVersion;
    }

    public ApplicationVersion getLatestReleaseVersion(String setupName) {
        try {
            log.debug("Requesting new application version from github");
            final GitHub github = GitHub.connectAnonymously();
            final GHRepository repository = github.getRepository(REPOSITORY_NAME);
            final GHRelease latestRelease = repository.getLatestRelease();
            return getApplicationVersion(latestRelease, setupName);

        } catch (IOException e) {
            log.warn("Can not get release application version", e);
            return null;
        }
    }

    public static Updater getUpdaterForCurrentOperatingSystem() {
        if (OS.isWindows()) {
            return new WindowsUpdater();
        } else if (OS.isUnix()) {
            return new UnixUpdater();
        } else return new UnixUpdater();
    }

    public ApplicationVersion getLatestBetaVersion(String setupName) {
        try {
            final GitHub gitHub = GitHub.connectAnonymously();
            final GHRepository repository = gitHub.getRepository(REPOSITORY_NAME);
            final PagedIterable<GHRelease> ghReleases = repository.listReleases();
            return getLatestBetaAppVersion(ghReleases, setupName);
        } catch (IOException e) {
            log.warn("Can not get latest beta application version", e);
            return null;
        }
    }

    private ApplicationVersion getLatestBetaAppVersion(PagedIterable<GHRelease> releases, String setupName) throws IOException {
        ApplicationVersion latestBeta = null;
        for (GHRelease release : releases) {
            if (release.isPrerelease()) {
                latestBeta = getApplicationVersion(release, setupName);
                break;
            }
        }
        return latestBeta;
    }

    private ApplicationVersion getApplicationVersion(GHRelease latestRelease, String setupName) throws IOException {
        final ApplicationVersion version = new ApplicationVersion();
        version.setUpdateTitle(latestRelease.getName());
        version.setLegacyUpdateInfo(latestRelease.getBody());
        version.setVersion(latestRelease.getTagName());
        version.setBeta(tryGetBetaFromName(version.getUpdateTitle(), new Beta(latestRelease.isPrerelease() ? 1 : 0)));
        latestRelease.listAssets().forEach(asset -> {
            if (asset.getName().equalsIgnoreCase(setupName)) {
                version.setDownloadUrl(asset.getBrowserDownloadUrl());
                version.setSize(asset.getSize());
            }
            String updateInfoName = "update-info.json";
            if (asset.getName().equalsIgnoreCase(updateInfoName)) {
                try {
                    log.debug("Getting update info: {}", updateInfoName);
                    final String updateInfoJsonUrl = asset.getBrowserDownloadUrl();
                    final UpdateInfo updateInfoFromUrl = getUpdateInfoFromUrl(new URL(updateInfoJsonUrl));
                    version.setVersionInfo(updateInfoFromUrl);
                } catch (final Exception e) {
                    log.error("Could not get update info", e);
                }
            }
        });
        return version;
    }

    public UpdateInfo getUpdateInfoFromUrl(final URL url) throws IOException {
        final String jsonSrc = IOUtils.toString(url, StandardCharsets.UTF_8);
        final ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonSrc, UpdateInfo.class);
    }

    private Beta tryGetBetaFromName(String updateTitle, Beta beta) {
        try {
            final Matcher matcher = BETA_FROM_RELEASE_TITLE_PATTERN.matcher(updateTitle);
            if (matcher.find()) {
                int betaVersion = Integer.parseInt(matcher.group(1));
                return new Beta(betaVersion);
            }

        } catch (Exception ignore) {
        }
        return beta;
    }
}
