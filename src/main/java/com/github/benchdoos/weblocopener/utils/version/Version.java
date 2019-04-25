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

package com.github.benchdoos.weblocopener.utils.version;

import com.github.benchdoos.weblocopener.update.AppVersion;
import org.jetbrains.annotations.NotNull;

import java.util.StringJoiner;

public class Version implements Comparable<Version> {
    private final AppVersion appVersion;
    private int major;
    private int minor;
    private int micro;
    private int build;

    public Version(AppVersion appVersion) {
        this.appVersion = appVersion;
        init(this.appVersion);
    }


    // 1.2.3.5 > 1.2.3.4-beta
    public int compareTo(Version o) {
        if (this.major != o.major) {
            return Integer.compare(this.major, o.major);
        }
        if (this.minor != o.minor) {
            return Integer.compare(this.minor, o.minor);
        }
        if (this.micro != o.micro) {
            return Integer.compare(this.micro, o.micro);
        }
        if (this.build != o.build) {
            return Integer.compare(this.build, o.build);
        }
        return 0;
    }


    //version 1.9.1.132
    // major: 1
    // minor: 9
    // micro: 1
    // beta: (from version)
    // build: 132


    private void init(@NotNull AppVersion appVersion) {

        final String version = appVersion.getVersion();
        if (version == null) {
            return;
        }


        final String[] split = version.split("\\.");

        if (split.length == 4) {
            major = Integer.parseInt(fixOldVersionReg(split[0]));
            minor = Integer.parseInt(split[1]);
            micro = Integer.parseInt(split[2]);
            build = Integer.parseInt(split[3]);

        } else {
            for (int i = 0; i < split.length; i++) {
                if (i == 0) {
                    major = Integer.parseInt(fixOldVersionReg(split[i]));
                } else if (i == 1) {
                    minor = Integer.parseInt(split[i]);
                } else if (i == 2) {
                    micro = Integer.parseInt(split[i]);
                } else if (i == 3) {
                    build = Integer.parseInt(split[i]);
                }

                if (i > 4) {
                    break;
                }
            }
        }
    }

    private String fixOldVersionReg(String s) {
        return s.replace("v", "");
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Version.class.getSimpleName() + "[", "]")
                .add("major=" + major)
                .add("minor=" + minor)
                .add("micro=" + micro)
                .add("build=" + build)
                .toString();
    }
}