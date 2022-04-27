// IDisplayTalkShow.aidl
package org.qiyi.video.svg.event;

// Declare any non-default types here with import statements

interface IDisplayTalkShow {
    String getShowName();

    String[]getHostNames();

    void startTalkShow(int userId);
}
