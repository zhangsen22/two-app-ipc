package com.watchdog.ipc;

import static android.content.Context.JOB_SCHEDULER_SERVICE;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PersistableBundle;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import com.watchdog.ipc.entry.AppInfo;
import com.watchdog.ipc.entry.Message;
import com.watchdog.ipc.scheduler.JobSchedulerService;
import com.watchdog.ipc.services.AppRunningImpl;
import com.watchdog.ipc.services.BuyAppleImpl;
import com.watchdog.ipc.services.ClientDiedServiceImpl;
import com.watchdog.ipc.services.IMessageImpl;

public class WatchDogDispatcher {
    
    private Handler handler = new Handler(Looper.getMainLooper());

    private RemoteCallbackList<MessagereceiveListener> messagereceiveListenerList = new RemoteCallbackList<>();

    private RemoteCallbackList<IClientCallback> callbackList;

    protected static int DOWNLOAD_JOB_ID = 2;//定时下载apk

//    protected final static long minLatencyMillis = JobInfo.DEFAULT_INITIAL_BACKOFF_MILLIS;
    protected final static long minLatencyMillis = 5000;


    /**
     * 在service 中初始化
     */
    public void onCreate(Context context) {
        callbackList = new RemoteCallbackList<IClientCallback>() {
            @Override
            public void onCallbackDied(IClientCallback callback) {
                Logger.e( "onCallbackDied: ");
            }

            @Override
            public void onCallbackDied(IClientCallback callback, Object appinfo) {
                super.onCallbackDied(callback, appinfo);
                AppInfo appInfo = (AppInfo) appinfo;
                // 可以通过packagename判断是哪个client掉线了
                Logger.e( "onCallbackDied: "+callback+" cookie "+appinfo.toString());
                Logger.e( "当前剩余链接客户端的数量: "+callbackList.getRegisteredCallbackCount());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LaunchAppManager.getInstance().openPackage(context,appInfo.getPackageName());
                    }
                },1000);
//                try {
//                    callback.clientDiedCallBack();
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
            }

            @Override
            public void kill() {
                super.kill();
                Logger.e( "kill: ");
            }
        };
    }

    public RemoteCallbackList<IClientCallback> getCallbackList() {
        return callbackList;
    }

    //private
    private WatchDogDispatcher() {

    }

    private static class SingletonInstance{
        private final static WatchDogDispatcher S = new WatchDogDispatcher();
    }
    // 3 返回对象
    public static WatchDogDispatcher getInstance() {
        return SingletonInstance.S;
    }

    public Handler getHandler() {
        return handler;
    }

    public RemoteCallbackList<MessagereceiveListener> getMessagereceiveListenerList() {
        return messagereceiveListenerList;
    }

    private IServiceManager serviceManager = new IServiceManager.Stub() {
        @Override
        public IBinder getService(String serviceName) throws RemoteException {
            if(IMessageService.class.getSimpleName().equals(serviceName)){
                return IMessageImpl.getInstance().asBinder();
            }else if(IBuyApple.class.getSimpleName().equals(serviceName)){
                return BuyAppleImpl.getInstance().asBinder();
            } else if(IAppRunningListener.class.getSimpleName().equals(serviceName)){
                return AppRunningImpl.getInstance().asBinder();
            } else if(IClientDiedService.class.getSimpleName().equals(serviceName)){
                return ClientDiedServiceImpl.getInstance().asBinder();
            } else {
                return null;
            }
        }
    };

    public IServiceManager getServiceManager() {
        return serviceManager;
    }

    /**
     * 给其他进程发送消息
     * @param message
     */
    public void dispatcherMessage(Message message){
        int size = messagereceiveListenerList.beginBroadcast();
        for (int i = 0; i < size; i++) {
            try {
                messagereceiveListenerList.getBroadcastItem(i).onReceiveMessage(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        messagereceiveListenerList.finishBroadcast();
    }

    /**
     * 当CHEDULE JOB时执行
     * @param context
     */
    public void scheduleJob(Context context) {
        cancelJobs(context,DOWNLOAD_JOB_ID);
        //开始配置JobInfo
        JobInfo.Builder builder = new JobInfo.Builder(DOWNLOAD_JOB_ID, new ComponentName(context, JobSchedulerService.class));
//        JobInfo.Builder mJobBuilder = new JobInfo.Builder(DOWNLOAD_JOB_ID, new ComponentName(context.getPackageName(), JobSchedulerService.class.getName()));

        //设置任务周期执行，其周期为intervalMillis参数
        //你无法控制任务的执行时间，系统只保证在此时间间隔内，任务最多执行一次。
        //Android 7.0+ 增加了一项针对 JobScheduler 的新限制，最小间隔只能是下面设定的数字
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            builder.setMinimumLatency(minLatencyMillis); //执行的最小延迟时间
            builder.setOverrideDeadline(minLatencyMillis);  //执行的最长延时时间
            builder.setMinimumLatency(minLatencyMillis);
            builder.setBackoffCriteria(minLatencyMillis, JobInfo.BACKOFF_POLICY_LINEAR);//线性重试方案
        } else {
            builder.setPeriodic(minLatencyMillis);
        }
        //这个方法告诉系统当你的设备重启之后你的任务是否还要继续执行
        builder.setPersisted(true);
        //设置网络类型 - 设定工作需要的基本网络描述
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        //如果你需要对网络能力进行更精确的控制
        //builder.setRequiredNetwork()
        //告诉你的应用，只有当设备在充电时这个任务才会被执行。- 默认false
        builder.setRequiresCharging(false);
        //你的任务只有当用户没有在使用该设备且有一段时间没有使用时才会启动该任务。- 默认false
        builder.setRequiresDeviceIdle(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //指定要运行此作业，设备的电池电量不得过低。
            builder.setRequiresBatteryNotLow(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //指定要运行此作业，设备的可用存储空间不得过低
            builder.setRequiresStorageNotLow(false);
        }


        //设置额外参数
        PersistableBundle extras = new PersistableBundle();

        builder.setExtras(extras);

        // Schedule job
        JobInfo jobInfo = builder.build();
        Logger.i( "Scheduling job: " + builder+"  jobInfo: "+jobInfo.toString()+"  jobInfo.getId(): "+jobInfo.getId());
        JobScheduler mJobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        // 这里就将开始在service里边处理我们配置好的job
        int schedule = mJobScheduler.schedule(jobInfo);
        Logger.i( "schedule code: " + schedule);
        //mJobScheduler.schedule(builder.build())会返回一个int类型的数据
        //如果schedule方法失败了，它会返回一个小于0的错误码。否则它会返回我们在JobInfo.Builder中定义的标识id。
    }

    // 当用户点击取消所有时执行
    public void cancelAllJobs(Context context) {
        JobScheduler mJobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        mJobScheduler.cancelAll();
    }

    public void cancelJobs(Context context,int jobId) {
        JobScheduler mJobScheduler = (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);
        mJobScheduler.cancel(jobId);
    }
}
