package com.cxgame.datatest;

import android.content.Context;

import androidx.annotation.NonNull;

import com.cxgame.datatest.util.DataUtil;

import java.util.HashMap;

public class CommonData extends HashMap<String, Object> {

    public CommonData(Context context) {
        super();
        put("AndroidId", DataUtil.getAndroidId(context));
        put("IMEI", DataUtil.getIMEI(context));
        put("IMSI", DataUtil.getIMSI(context));
        put("OAID", DataUtil.getOAID());
        put("Manufacturer", DataUtil.getManufacturer());
        put("PhoneNumber", DataUtil.getPhoneNumber(context));
        put("UUID", DataUtil.getUUID(context));
        put("MacAddress", DataUtil.getMacAddress(context));
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder retVal = new StringBuilder();
        for (Entry<String, Object> entry : entrySet()) {
            retVal.append(entry.getKey()).append("=").append(entry.getValue().toString()).append("\n");
        }
        return retVal.toString();
    }
}
