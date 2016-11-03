package update;

import java.util.Date;

/**
 * Created by Eugene Zrazhevsky on 03.11.2016.
 */
public class AppVersion {
    private String version;
    private String downloadUrl;
    private int size;
    private Date updateDate;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version.replace("v", "");
    }

    public int getSize() {
        return size;
    }


    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "AppVersion: {" + version + " " + updateDate.toString() + "\ndownload:[" + downloadUrl + "], size: " + size / 1024 + "kb}";
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
