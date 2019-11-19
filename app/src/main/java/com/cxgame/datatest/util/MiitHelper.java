package com.cxgame.datatest.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bun.miitmdid.core.ErrorCode;
import com.bun.miitmdid.core.IIdentifierListener;
import com.bun.miitmdid.core.MdidSdk;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.supplier.IdSupplier;


public class MiitHelper implements IIdentifierListener {

    private static final String TAG = "CXGame-MiitHelper";

    private AppIdsUpdater _listener;
    public MiitHelper(AppIdsUpdater callback){
        _listener=callback;
    }

    private static boolean isSupported;

    private static String oaId;

    public static boolean isSupported() {
        return isSupported;
    }

    private static void setIsSupported(boolean isSupported) {
        MiitHelper.isSupported = isSupported;
    }

    public static String getOaId() {
        return oaId;
    }

    private static void setOaId(String oaId) {
        MiitHelper.oaId = oaId;
    }

    public void getDeviceIds(Context cxt){
        long timeb=System.currentTimeMillis();
        int nres = CallFromReflect(cxt);
//        int nres=DirectCall(cxt);
        long timee=System.currentTimeMillis();
        long offset=timee-timeb;
        if(nres == ErrorCode.INIT_ERROR_DEVICE_NOSUPPORT){//不支持的设备
            Log.d(TAG, "不支持的设备");
        }else if( nres == ErrorCode.INIT_ERROR_LOAD_CONFIGFILE){//加载配置文件出错
            Log.d(TAG, "加载配置文件出错");

        }else if(nres == ErrorCode.INIT_ERROR_MANUFACTURER_NOSUPPORT){//不支持的设备厂商
            Log.d(TAG, "不支持的设备厂商");

        }else if(nres == ErrorCode.INIT_ERROR_RESULT_DELAY){//获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程
            Log.d(TAG, "获取接口是异步的，结果会在回调中返回，回调执行的回调可能在工作线程");

        }else if(nres == ErrorCode.INIT_HELPER_CALL_ERROR){//反射调用出错
            Log.d(TAG, "反射调用出错");

        }
        Log.d(getClass().getSimpleName(),"return value: "+String.valueOf(nres));

    }


    /*
     * 通过反射调用，解决android 9以后的类加载升级，导至找不到so中的方法
     *
     * */
    private int CallFromReflect(Context cxt){
        return MdidSdkHelper.InitSdk(cxt,true,this);
    }

    /*
     * 直接java调用，如果这样调用，在android 9以前没有题，在android 9以后会抛找不到so方法的异常
     * 解决办法是和JLibrary.InitEntry(cxt)，分开调用，比如在A类中调用JLibrary.InitEntry(cxt)，在B类中调用MdidSdk的方法
     * A和B不能存在直接和间接依赖关系，否则也会报错
     *
     * */
    private int DirectCall(Context cxt){
        MdidSdk sdk = new MdidSdk();
        return sdk.InitSdk(cxt,this);
    }

    /**
     * 由于MiitHelper.getDeviceIds方法中使用CallFromReflect而不是DirectCall，
     * 这个回调在有些机型的调用时间会长，导致 OAID 会延迟一点时间获取到
     */
    @Override
    public void OnSupport(boolean isSupport, IdSupplier _supplier) {
        if(_supplier==null) {
            return;
        }
        /* String oaid=_supplier.getOAID();
        String vaid=_supplier.getVAID();
        String aaid=_supplier.getAAID();
//        String udid=_supplier.getUDID();  // udid不对开发者开放，可以使用oaid来做为维一标识
        StringBuilder builder=new StringBuilder();
        builder.append("support: ").append(isSupport?"true":"false").append("\n");
//        builder.append("UDID: ").append(udid).append("\n");
        builder.append("OAID: ").append(oaid).append("\n");
        builder.append("VAID: ").append(vaid).append("\n");
        builder.append("AAID: ").append(aaid).append("\n");
        String idstext=builder.toString(); */


        setIsSupported(_supplier.isSupported());


        String oaId = _supplier.getOAID();
        setOaId(oaId);
        _supplier.shutDown();   // 关闭接口
        if(_listener!=null){
            _listener.OnIdsAvalid(oaId);
        }
    }
    public interface AppIdsUpdater{
        void OnIdsAvalid(@NonNull String ids);
    }
}
