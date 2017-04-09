package com.jy.jyjy;

import android.app.Application;
import android.content.Context;

import com.dl7.downloaderlib.DownloadConfig;
import com.dl7.downloaderlib.FileDownloader;
import com.jy.jyjy.api.RetrofitService;
import com.jy.jyjy.engine.DownloaderWrapper;
import com.jy.jyjy.local.table.DaoMaster;
import com.jy.jyjy.local.table.DaoSession;
import com.jy.jyjy.rxbus.RxBus;
import com.jy.jyjy.utils.PreferencesUtils;
import com.jy.jyjy.utils.ToastUtils;
import com.orhanobut.logger.Logger;
import com.squareup.leakcanary.LeakCanary;

import org.greenrobot.greendao.database.Database;

import java.io.File;

import static org.greenrobot.greendao.test.DbTest.DB_NAME;

/**
 * Created by zhengchengrong on 2017/4/9.
 */

public class AndroidApplication extends Application {

    private DaoSession mDaoSession;

    private static Context sContext;


    // 因为下载那边需要用，这里在外面实例化在通过 ApplicationModule 设置
    private RxBus mRxBus = new RxBus();

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();


        _initDatabase();
        _initInjector();
        _initConfig();
    }

    /**
     * 初始化数据库
     */
    private void _initDatabase() {

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), "jyjy.db",null);
        Database database = helper.getWritableDb();
        mDaoSession = new DaoMaster(database).newSession();
        //mDaoSession.getUserDao();
    }
    /**
     * 初始化注射器
     */
    private void _initInjector() {


        // 这里不做注入操作，只提供一些全局单例数据
//        sAppComponent = DaggerApplicationComponent.builder()
//                .applicationModule(new ApplicationModule(this, mDaoSession, mRxBus))
//                .build();
    }

    /**
     * 初始化配置
     */
    private void _initConfig() {
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this); //开发阶段检测内存泄漏
            Logger.init("LogTAG");
        }

          RetrofitService.init();
          ToastUtils.init(getContext());
           DownloaderWrapper.init(mRxBus, mDaoSession.getVideoInfoDao());
          FileDownloader.init(getApplicationContext());
          DownloadConfig config = new DownloadConfig.Builder()
                .setDownloadDir(PreferencesUtils.getSavePath(getApplicationContext()) + File.separator + "video/").build();
          FileDownloader.setConfig(config);
    }




    public static Context getContext() {
        return sContext;
    }

}
