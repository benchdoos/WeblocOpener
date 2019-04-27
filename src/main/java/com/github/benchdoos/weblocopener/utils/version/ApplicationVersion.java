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

import java.util.StringJoiner;

/**
 * Created by Eugene Zrazhevsky on 03.11.2016.
 */
public class ApplicationVersion {
    private String version;
    private String downloadUrl;
    private long size;
    private String updateTitle;
    private String updateInfo;
    private Beta beta = new Beta(0);

    public Beta getBeta() {
        return beta;
    }

    public boolean isBeta() {
        return beta.isBeta();
    }

    public void setBeta(Beta beta) {
        this.beta = beta;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getUpdateTitle() {
        return updateTitle;
    }

    public void setUpdateTitle(String updateTitle) {
        this.updateTitle = updateTitle;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        if (version != null) {
            version = version.replace("v", ""); // this may fix old versions "v" start
        }
        this.version = version;
    }

    public long getSize() {
        return size;
    }


    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ApplicationVersion.class.getSimpleName() + "[", "]")
                .add("version='" + version + "'")
                .add("downloadUrl='" + downloadUrl + "'")
                .add("size=" + size)
                .add("updateTitle='" + updateTitle + "'")
                .add("updateInfo='" + updateInfo + "'")
                .add("beta=" + beta)
                .toString();
    }

    private long getKilobyteFromByte() {
        return size / 1024;
    }

    public String getUpdateInfo() {
        return updateInfo;
    }

    public void setUpdateInfo(String updateInfo) {
        this.updateInfo = updateInfo;
    }

}
