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

import com.github.benchdoos.weblocopener.preferences.PreferencesManager;
import com.github.benchdoos.weblocopener.utils.Internal;
import com.github.benchdoos.weblocopener.utils.system.OperatingSystem;
import com.github.benchdoos.weblocopener.utils.version.ApplicationVersion;
import com.github.benchdoos.weblocopener.utils.version.Beta;
import lombok.extern.log4j.Log4j2;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class UpdaterManager {
    private static final String REPOSITORY_NAME = "benchdoos/weblocopener";
    private static final Pattern BETA_FROM_RELEASE_TITLE_PATTERN = Pattern.compile("\\(beta\\.(\\d+)\\)");

    static ApplicationVersion getLatestVersion(Updater updater) {
        final ApplicationVersion latestReleaseAppVersion = updater.getLatestReleaseAppVersion();
        PreferencesManager.setLatestUpdateCheck(new Date());

        if (PreferencesManager.isBetaUpdateInstalling()) {
            final ApplicationVersion latestBetaAppVersion = updater.getLatestBetaAppVersion();

            if (latestBetaAppVersion != null) {
                log.debug("Comparing latest beta version: {} and latest release version: {}", latestBetaAppVersion, latestReleaseAppVersion);
                if (Internal.versionCompare(latestBetaAppVersion, latestReleaseAppVersion)
                        == Internal.VersionCompare.SERVER_VERSION_IS_NEWER) {
                    return latestBetaAppVersion;
                }
            }
        }
        return latestReleaseAppVersion;
    }

    static ApplicationVersion getLatestReleaseVersion(String setupName) {
        try {
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
        if (OperatingSystem.isWindows()) {
            return new WindowsUpdater();
        } else if (OperatingSystem.isUnix()) {
            return new UnixUpdater();
        } else return new UnixUpdater();
    }

    static ApplicationVersion getLatestBetaVersion(String setupName) {
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

    private static ApplicationVersion getLatestBetaAppVersion(PagedIterable<GHRelease> releases, String setupName) throws IOException {
        ApplicationVersion latestBeta = null;
        for (GHRelease release : releases) {
            if (release.isPrerelease()) {
                latestBeta = getApplicationVersion(release, setupName);
                break;
            }
        }
        return latestBeta;
    }

    private static ApplicationVersion getApplicationVersion(GHRelease latestRelease, String setupName) throws IOException {
        ApplicationVersion version = new ApplicationVersion();
        version.setUpdateTitle(latestRelease.getName());
        version.setUpdateInfo(latestRelease.getBody());
        version.setVersion(latestRelease.getTagName());
        version.setBeta(tryGetBetaFromName(version.getUpdateTitle(), new Beta(latestRelease.isPrerelease() ? 1 : 0)));
        latestRelease.getAssets().forEach(asset -> {
            if (asset.getName().equalsIgnoreCase(setupName)) {
                version.setDownloadUrl(asset.getBrowserDownloadUrl());
                version.setSize(asset.getSize());
            }
        });
        return version;
    }

    private static Beta tryGetBetaFromName(String updateTitle, Beta beta) {
        try {
            Matcher matcher = BETA_FROM_RELEASE_TITLE_PATTERN.matcher(updateTitle);
            if (matcher.find()) {
                int betaVersion = Integer.parseInt(matcher.group(1));
                return new Beta(betaVersion);
            }

        } catch (Exception ignore) {
        }
        return beta;
    }
}
