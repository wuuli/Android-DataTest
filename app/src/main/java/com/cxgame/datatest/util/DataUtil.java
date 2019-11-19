package com.cxgame.datatest.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.WindowManager;


import com.cxgame.datatest.APPApplication;
import com.cxgame.datatest.util.easypermission.EasyPermission;
import com.cxgame.datatest.util.easypermission.Permission;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class DataUtil {


    public static String getUUID(Context context) {
        String name = "uuid";
        SharedPreferences preferences = context.getSharedPreferences(name, 0);
        String uuid = preferences.getString(name, null);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            preferences.edit().putString(name, uuid).apply();
        }
        return uuid;
    }

    public static String getAndroidId (Context context) {
        return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getIMEI (Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = "";
        if (EasyPermission.isPermissionGrant(context, Permission.READ_PHONE_STATE)) {
            imei = tm.getDeviceId();  // todo 需要检查权限
        }

        if (TextUtils.isEmpty(imei)) {
            imei = Build.UNKNOWN;
        }
        return imei;
    }

    public static String getIMSI(Context context) {

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String imsi = "";
        if (EasyPermission.isPermissionGrant(context, Permission.READ_PHONE_STATE)) {
            imsi = tm.getSubscriberId(); // todo 需要检查权限
        }
        if (TextUtils.isEmpty(imsi)) {
            imsi = Build.UNKNOWN;
        }
        return imsi;
    }

    public static String getPhoneNumber(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNum = "";
        try {
            phoneNum = tm.getLine1Number();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(phoneNum)) {
            phoneNum = Build.UNKNOWN;
        }
        return phoneNum;
    }

    public static String getOAID() {
        if (MiitHelper.isSupported()) {
//            return "oaid-" + MiitHelper.getOaId();
            return MiitHelper.getOaId();
        }
        return Build.UNKNOWN;
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getMacAddress(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            String mac = getMacAddress();
            if (mac != null) {
                return mac;
            }
        }
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wm.getConnectionInfo();
        return wifiInfo.getMacAddress();
    }

    // 必须在连接wifi的时候才能获取
    // 可以看下NetworkInterface在Android FrameWork中怎么实现的
    private static String getMacAddress() {
        try {
            // 把当前机器上的访问网络的接口存入Enumeration集合中
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface netWork = interfaces.nextElement();
                if (!netWork.getName().equals("wlan0")) {
                    continue;
                }
                // 如果存在硬件地址并可以使用给定的当前权限访问，则返回该硬件地址（通常是 MAC）。
                byte[] by = netWork.getHardwareAddress();
                if (by == null || by.length == 0) {
                    return null;
                }
                StringBuilder builder = new StringBuilder();
                for (byte b : by) {
                    builder.append(String.format(":%02X", b));
                }
                builder.deleteCharAt(0);

                return builder.toString();
            }
        } catch (Exception ignored) {}

        return null;
    }

    public static String getCPUABI() {
        return Build.CPU_ABI;
    }

    public static String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getNetworkCarrier(String code) {
        switch (code) {
            case "46000":
            case "46002":
            case "46007":
                return "中国移动";
            case "46001":
            case "46006":
            case "46009":
                return "中国联通";
            case "46003":
            case "46005":
            case "46011":
                return "中国电信";
            default:
                return code;
        }
    }

    public static String getNetworkCode(Context context) {
        String imsi = getIMSI(context);
        if (imsi != null && !imsi.equals(Build.UNKNOWN) && imsi.length() > 4) {
            return imsi.substring(0, 5);
        }
        return Build.UNKNOWN;
    }

    public static String getModel() {
        String def = Build.UNKNOWN;
        String brand = Build.BRAND;
        String model = Build.MODEL;
        String brandU = brand.toUpperCase(Locale.getDefault());
        String modelU = model.toUpperCase(Locale.getDefault());
        if (modelU.contains(brandU)) return model;
        if (!brand.equals(def) && !model.equals(def)) return brand + " " + model;
        return brand;
    }

    public static String getResolution(Context context) {
        // 在service中也能得到高和宽
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        int width = size.x;
        int height = size.y;
        return width + "x" + height;
    }

    public static String getDump(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryIntent = context.registerReceiver(null, filter);
        if (batteryIntent != null) {
            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            return 100 * level / scale + "%";
        }
        return Build.UNKNOWN;
    }

    public static String getStatus(Context context) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryIntent = context.getApplicationContext().registerReceiver(null, filter);
        if (batteryIntent != null) {
            int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
            switch (status) {
                case BatteryManager.BATTERY_STATUS_UNKNOWN:
                    return Build.UNKNOWN;
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    return "charging";
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    return "uncharged";
                case BatteryManager.BATTERY_STATUS_FULL:
                    return "full";
            }
        }
        return Build.UNKNOWN;
    }

    public static String getNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if ((networkInfo != null) && (networkInfo.isConnected())) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return "WIFI";
            }
            if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                int mobileType = networkInfo.getSubtype();
                switch (mobileType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                    case TelephonyManager.NETWORK_TYPE_GSM:
                        return "2G";
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                        return "3G";
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return "4G";
                    case TelephonyManager.NETWORK_TYPE_NR:
                        return "5G";
                }
            }
        }
        return Build.UNKNOWN;
    }


    private static String getAppVersion(Context context) {
        try {
            return context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "unknown";
    }


}
