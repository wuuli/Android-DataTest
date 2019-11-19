package com.cxgame.datatest;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bun.miitmdid.core.JLibrary;
import com.cxgame.datatest.util.MiitHelper;

public class APPApplication extends Application {

    private static final String TAG = "APPApplication";

    @Override
	public void onCreate() {
		super.onCreate();

        // 移动智能终端补充设备标识体系统一调用 SDK 初始化
        JLibrary.InitEntry(this);

		// 获取OAID
        MiitHelper miitHelper = new MiitHelper(null);

        miitHelper.getDeviceIds(getApplicationContext());
	}

}
