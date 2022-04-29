package com.watchdog.ipc.entry;

import android.os.Parcel;
import android.os.Parcelable;

public class AppInfo implements Parcelable {

    public String processName;
    public String className;
    public String packageName;
    public int versionCode;
    public String versionName;
    public int pid;
    public int processState;

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
}
