package com.github.benchdoos.weblocopener.updater.update;

/**
 * Created by Eugene Zrazhevsky on 03.11.2016.
 */
public class AppVersion {
    private String version;
    private String downloadUrl;
    private long size;
    private String updateTitle;
    private String updateInfo;

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
        this.version = version.replace("v", "");
    }

    public long getSize() {
        return size;
    }


    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "AppVersion: {" + version + "\ndownload:[" + downloadUrl + "], size: " + getKilobyteFromByte() + "kb}" +
                "\n" + updateInfo;
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
