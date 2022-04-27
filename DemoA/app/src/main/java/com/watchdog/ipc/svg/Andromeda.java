/*
* Copyright (c) 2018-present, iQIYI, Inc. All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification,
* are permitted provided that the following conditions are met:
*
*        1. Redistributions of source code must retain the above copyright notice,
*        this list of conditions and the following disclaimer.
*
*        2. Redistributions in binary form must reproduce the above copyright notice,
*        this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
*
*        3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived
*        from this software without specific prior written permission.
*
*        THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
*        INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
*        IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
*        OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
*        OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
*        OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
*        EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*
*/
package com.watchdog.ipc.svg;

import android.content.Context;
import android.os.IBinder;
import android.text.TextUtils;

import com.watchdog.ipc.svg.remote.ConnectionManager;
import com.watchdog.ipc.svg.remote.IRemoteManager;
import com.watchdog.ipc.svg.remote.RemoteManager;
import com.watchdog.ipc.svg.transfer.RemoteTransfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by wangallen on 2018/1/8.
 */
public class Andromeda {

    private static final String TAG = "Andromeda";

    private static Andromeda sInstance;

    private static Context appContext;

    private static AtomicBoolean initFlag = new AtomicBoolean(false);

    public static void init(Context context) {
        if (initFlag.get() || context == null) {
            return;
        }
        appContext = context.getApplicationContext();
        RemoteTransfer.init(context.getApplicationContext());
        initFlag.set(true);
    }

    public static Andromeda getInstance() {
        if (null == sInstance) {
            synchronized (Andromeda.class) {
                if (null == sInstance) {
                    sInstance = new Andromeda();
                }
            }
        }
        return sInstance;
    }

    private Andromeda() {

    }

    public static Context getAppContext() {
        return appContext;
    }

    public static <T extends IBinder> void registerRemoteService(Class serviceClass, T stubBinder) {
        if (null == serviceClass || null == stubBinder) {
            return;
        }
        RemoteTransfer.getInstance().registerStubService(serviceClass.getCanonicalName(), stubBinder);
    }

    //考虑到混淆，不推荐使用这种方式
    @Deprecated
    public static <T extends IBinder> void registerRemoteService(String serviceCanonicalName, T stubBinder) {
        if (TextUtils.isEmpty(serviceCanonicalName) || null == stubBinder) {
            return;
        }
        RemoteTransfer.getInstance().registerStubService(serviceCanonicalName, stubBinder);
    }

    public static void unregisterRemoteService(Class serviceClass) {
        if (null == serviceClass) {
            return;
        }
        RemoteTransfer.getInstance().unregisterStubService(serviceClass.getCanonicalName());
    }

    @Deprecated
    public static void unregisterRemoteService(String serviceCanonicalName) {
        if (TextUtils.isEmpty(serviceCanonicalName)) {
            return;
        }
        RemoteTransfer.getInstance().unregisterStubService(serviceCanonicalName);
    }

    public static IRemoteManager with(Context context) {
        return RemoteManager.getInstance(context);
    }


    ////////////////end of non-static methods/////////////////////////////

    public static void unbind(Class<?> serviceClass) {
        unbind(serviceClass.getCanonicalName());
    }

    @Deprecated
    public static void unbind(String serviceCanonicalName) {
        if (TextUtils.isEmpty(serviceCanonicalName)) {
            return;
        }
        List<String> serviceNames = new ArrayList<>();
        serviceNames.add(serviceCanonicalName);
        ConnectionManager.getInstance().unbindAction(appContext, serviceNames);
    }

    public static void unbind(Set<Class<?>> serviceClasses) {
        if (null == serviceClasses || serviceClasses.size() < 1) {
            return;
        }
        List<String> serviceNames = new ArrayList<>();
        for (Class<?> clazz : serviceClasses) {
            serviceNames.add(clazz.getCanonicalName());
        }
        ConnectionManager.getInstance().unbindAction(appContext, serviceNames);
    }

    public static void subscribe(String name, EventListener listener) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        RemoteTransfer.getInstance().subscribeEvent(name, listener);
    }

    public static void unsubscribe(EventListener listener) {
        if (null == listener) {
            return;
        }
        RemoteTransfer.getInstance().unsubscribeEvent(listener);
    }

    public static void publish(Event event) {
        if (null == event) {
            return;
        }
        RemoteTransfer.getInstance().publish(event);
    }
}
