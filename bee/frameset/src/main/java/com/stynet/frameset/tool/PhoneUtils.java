package com.stynet.frameset.tool;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by xx shuiDianBing, 2019/01/14-18:25:18:25.Refer to the website: nullptr
 * Android开发之获取SIM卡信息和手机号码 https://blog.csdn.net/u013184970/article/details/79773058
 **/

public final class PhoneUtils {
    /**
     * 获取sim卡iccid
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static final String getIccid(Context context){
        String iccid = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getSimSerialNumber();
        return iccid;
    }

    /**
     * 获取电话号码
     * @param context
     * @return
     */
    @SuppressLint("MissingPermission")
    public static final String getNativePhoneNumber(Context context){
        String tel = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number();
        return tel;
    }
    public static final String getProvidersName(Context context){
        String networkOperator = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkOperator();
        //IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
        if(networkOperator.equals("46000")|| networkOperator.equals("46002"))
            return "中国移动";
        else if(networkOperator.equals("46001"))
            return "中国联通";
        else if(networkOperator.equals("46003"))
            return "中国电信";
        return null;
    }
    @SuppressLint("MissingPermission")
    public static final String getPhoneInfo(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if(null == tm)return null;
        StringBuffer sb = new StringBuffer();
        sb.append("\nLine1Number = " + tm.getLine1Number());
        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());//移动运营商编号
        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());//移动运营商名称
        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
        sb.append("\nSimOperator = " + tm.getSimOperator());
        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
        return sb.toString();
    }
}
