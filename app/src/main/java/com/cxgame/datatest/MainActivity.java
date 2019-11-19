package com.cxgame.datatest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.cxgame.datatest.util.easypermission.EasyPermission;
import com.cxgame.datatest.util.easypermission.GrantResult;
import com.cxgame.datatest.util.easypermission.NextAction;
import com.cxgame.datatest.util.easypermission.NextActionType;
import com.cxgame.datatest.util.easypermission.Permission;
import com.cxgame.datatest.util.easypermission.PermissionRequestListener;
import com.cxgame.datatest.util.easypermission.RequestPermissionRationalListener;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "CXGame-MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
    }


    public void getData(View view) {
        CommonData data = new CommonData(this);
        Log.d(TAG, "getData: " + data.toString());
        TextView textView = (TextView)findViewById(R.id.data_tv);
        textView.setText(data.toString());
    }


    public void reqPermission(View view) {
        requestPermission();
    }

    private void requestPermission() {
        EasyPermission.with(this)
                .addPermissions(Permission.READ_PHONE_STATE)
                .addRequestPermissionRationaleHandler(Permission.READ_PHONE_STATE, new RequestPermissionRationalListener() {
                    @Override
                    public void onRequestPermissionRational(String permission, final boolean requestPermissionRationaleResult, final NextAction nextAction) {
                        // 这里处理具体逻辑，如弹窗提示用户等,但是在处理完自定义逻辑后必须调用nextAction的next方法
                        nextAction.next(NextActionType.NEXT);
                    }
                })
                .request(new PermissionRequestListener() {
                    @Override
                    public void onGrant(final Map<String, GrantResult> result) {
                        //权限申请返回
                        Log.d(EasyPermission.TAG, "onGrant: " + result.get(Permission.READ_PHONE_STATE));
                        if (result.get(Permission.READ_PHONE_STATE) != GrantResult.GRANT) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("提示");
                            builder.setMessage("无法获取设备的IMEI、IMSI，程序无法正常运行");
                            // builder.setNegativeButton("知道了", null);
                            builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                                            !shouldShowRequestPermissionRationale(Permission.READ_PHONE_STATE)) {
                                        EasyPermission.openSettingPage(MainActivity.this);
                                    } else {
                                        requestPermission();
                                    }
                                }
                            });
                            builder.setCancelable(false);
                            builder.show();
                        }
                    }

                    @Override
                    public void onCancel(String stopPermission) {
                        //在addRequestPermissionRationaleHandler的处理函数里面调用了NextAction.next(NextActionType.STOP,就会中断申请过程，直接回调到这里来
                        Log.d(EasyPermission.TAG, "onCancel: ");
                    }
                });
    }

}
