package com.watchdog.ipc.scheduler;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.watchdog.ipc.Logger;
import com.watchdog.ipc.WatchDogDispatcher;

/**
 * https://www.jb51.net/article/116575.htm
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP) //这里的标识注明只有在Android 5.0及以上才可以用
public class JobSchedulerService extends JobService {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.i("[JobSchedulerService] onCreate - Thread ID = " + Thread.currentThread().getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.i("[JobSchedulerService] onDestroy - Thread ID = " + Thread.currentThread().getId());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.i("[JobSchedulerService] onStartCommand - startId = " + startId + ", Thread ID = " + Thread.currentThread().getId());
        WatchDogDispatcher.getInstance().scheduleJob(getApplicationContext());
        return START_NOT_STICKY;
    }

    @Override
    public boolean onStartJob(final JobParameters params) {
        Logger.i("[JobSchedulerService] onStartJob - Thread ID = " + Thread.currentThread().getName()+"   Job ID = "+params.getJobId());
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            jobFinished(params, true);
            WatchDogDispatcher.getInstance().scheduleJob(getApplicationContext());
        }else {
            jobFinished(params, false);
        }
        //返回false表示执行完毕，返回true表示需要开发者自己调用jobFinished方法通知系统已执行完成
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Logger.i("[JobSchedulerService] onStopJob - Thread ID = " + Thread.currentThread().getId()+"   Job ID = "+params.getJobId());
        //停止，不是结束。jobFinished不会直接触发onStopJob
        //必须在“onStartJob之后，jobFinished之前”取消任务，才会在jobFinished之后触发onStopJob
        return false;
    }
}
