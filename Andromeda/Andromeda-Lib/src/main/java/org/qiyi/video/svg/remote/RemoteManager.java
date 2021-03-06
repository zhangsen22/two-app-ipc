package org.qiyi.video.svg.remote;

import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import org.qiyi.video.svg.bean.BinderBean;
import org.qiyi.video.svg.log.Logger;
import org.qiyi.video.svg.transfer.RemoteTransfer;
import org.qiyi.video.svg.utils.Utils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangallen on 2018/3/26.
 */

public class RemoteManager implements IRemoteManager {

    private Handler handler = new Handler(Looper.getMainLooper());

    private Context appContext;

    private List<String> commuStubServiceNames = new ArrayList<>();

    private static RemoteManager instance;

    public static RemoteManager getInstance(Context context) {
        if (null == instance) {
            synchronized (RemoteManager.class) {
                if (null == instance) {
                    instance = new RemoteManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public RemoteManager(Context context) {
        this.appContext = context;
    }

    @Override
    public IBinder getRemoteService(Class<?> serviceClass) {
        if (null == serviceClass) {
            return null;
        }
        return getRemoteService(serviceClass.getCanonicalName());
    }

    @Override
    public synchronized IBinder getRemoteService(String serviceCanonicalName) {
        Logger.d(this.toString() + "-->getRemoteService,serviceName:" + serviceCanonicalName);
        if (TextUtils.isEmpty(serviceCanonicalName)) {
            return null;
        }
        BinderBean binderBean = RemoteTransfer.getInstance().getRemoteServiceBean(serviceCanonicalName);
        if (binderBean == null) {
            Logger.e("Found no binder for "+serviceCanonicalName+"! Please check you have register implementation for it or proguard reasons!");
            return null;
        }
        String commuStubServiceName = ConnectionManager.getInstance().bindAction(appContext, binderBean.getProcessName());
        commuStubServiceNames.add(commuStubServiceName);
        return binderBean.getBinder();
    }

//    @Override
//    public void onDestroy() {
//        Logger.d(this.toString() + "-->onDestroy()");
//        ConnectionManager.getInstance().unbindAction(appContext, commuStubServiceNames);
//    }
}
