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
package org.qiyi.video.svg.local;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wangallen on 2018/1/8.
 */

public class LocalServiceHub implements ILocalServiceHub {

    private static LocalServiceHub sInstance;

    public static LocalServiceHub getInstance() {
        if (null == sInstance) {
            synchronized (LocalServiceHub.class) {
                if (null == sInstance) {
                    sInstance = new LocalServiceHub();
                }
            }
        }
        return sInstance;
    }

    private Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    private LocalServiceHub() {
    }


    @Override
    public Object getLocalService(String module) {
        return serviceMap.get(module);
    }

    @Override
    public void registerService(String module, Object serviceImpl) {
        serviceMap.put(module, serviceImpl);
    }

    @Override
    public void unregisterService(String module) {
        serviceMap.remove(module);
    }
}
