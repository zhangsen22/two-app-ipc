package com.watchdog.ipc.entry;

import android.os.Parcel;
import android.os.Parcelable;

public class AppInfo implements Parcelable {

    private String processName;
    private String className;
    private String packageName;
    private int versionCode;
    private String versionName;
    private int pid;
    private int processState;

    public AppInfo() {
    }

    protected AppInfo(Parcel in) {
        processName = in.readString();
        className = in.readString();
        packageName = in.readString();
        versionCode = in.readInt();
        versionName = in.readString();
        pid = in.readInt();
        processState = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(processName);
        dest.writeString(className);
        dest.writeString(packageName);
        dest.writeInt(versionCode);
        dest.writeString(versionName);
        dest.writeInt(pid);
        dest.writeInt(processState);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getProcessState() {
        return processState;
    }

    public void setProcessState(int processState) {
        this.processState = processState;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "processName='" + processName + '\'' +
                ", className='" + className + '\'' +
                ", packageName='" + packageName + '\'' +
                ", versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", pid=" + pid +
                ", processState=" + processState +
                '}';
    }
}
