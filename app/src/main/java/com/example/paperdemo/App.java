package com.example.paperdemo;

import android.app.Application;

import com.ikangtai.cardpapersdk.util.Md5Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * desc
 *
 * @author xiongyl 2020/9/9 11:44
 */
public class App extends Application {
    private static App _instance;
    public static String saasAppId = "110005";
    public static String saasAppSecret = "5614dasdqwdeqw44e";
    @Override
    public void onCreate() {
        super.onCreate();
        _instance=this;
    }

    public static App getInstance() {
        return _instance;
    }
}
