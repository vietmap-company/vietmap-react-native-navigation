//package com.basicapp;
//
//import android.app.Application;
//import android.content.Context;
//import android.content.res.Resources;
//
//import com.facebook.react.ReactNativeHost;
//import com.facebook.react.ReactPackage;
//import com.facebook.react.shell.MainPackageConfig;
//import com.facebook.react.shell.MainReactPackage;
//import java.util.Arrays;
//import java.util.ArrayList;
//
//
//public class PackageList {
//    private Application application;
//    private ReactNativeHost reactNativeHost;
//    private MainPackageConfig mConfig;
//
//    public PackageList(Application application) {
//        this(application, null);
//    }
//
//    public PackageList(Application application, MainPackageConfig config) {
//        this.application = application;
//        this.reactNativeHost = null;
//        this.mConfig = config;
//    }
//
//    private Resources getResources() {
//        return this.application.getResources();
//    }
//
//    private Context getApplicationContext() {
//        return this.application.getApplicationContext();
//    }
//
//    public ArrayList<ReactPackage> getPackages() {
//        return new ArrayList<>(Arrays.asList(
//                new MainReactPackage(mConfig)
//        ));
//    }
//}