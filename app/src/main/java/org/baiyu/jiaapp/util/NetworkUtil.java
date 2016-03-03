package org.baiyu.jiaapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Created by baiyu on 2015-9-17.
 * 手机联网状态
 */
public class NetworkUtil {

    public enum NetworkType {
        NETWORKTYPE_WIFI,
        NETWORKTYPE_2G,
        NETWORKTYPE_3G,
        NETWORKTYPE_4G,
        NETWORKTYPE_UNKOWN,//未知
        NETWORKTYPE_NO//未联网
    }

    public enum Operators {
        China_Mobile,//移动
        China_Unicom,//联通
        China_Telecom,//电信
        China_Unkown//未知
    }

    public static NetworkType GetNetworkType(Context context) {
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return NetworkType.NETWORKTYPE_WIFI;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                String _strSubTypeName = networkInfo.getSubtypeName();

                // TD-SCDMA   networkType is 17
                int networkType = networkInfo.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: { //api<8 : replace by 11
                        return NetworkType.NETWORKTYPE_2G;
                    }
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP: {  //api<13 : replace by 15
                        return NetworkType.NETWORKTYPE_3G;
                    }
                    case TelephonyManager.NETWORK_TYPE_LTE: {   //api<11 : replace by 13
                        return NetworkType.NETWORKTYPE_4G;
                    }
                    default:
                        if (_strSubTypeName.equalsIgnoreCase("TD-SCDMA") || _strSubTypeName.equalsIgnoreCase("WCDMA") || _strSubTypeName.equalsIgnoreCase("CDMA2000")) {
                            return NetworkType.NETWORKTYPE_3G;
                        } else {
                            return NetworkType.NETWORKTYPE_UNKOWN;
                        }
                }
            }
        }
        return NetworkType.NETWORKTYPE_NO;
    }


    public static Operators getOperators(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        String imsi = "";
        if (networkInfo != null && networkInfo.isAvailable()) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);
            imsi = telephonyManager.getSubscriberId();
        }
        if (!TextUtils.isEmpty(imsi)) {
            if (imsi.startsWith("46000") || imsi.startsWith("46002")) {
                return Operators.China_Mobile;
            } else if (imsi.startsWith("46001")) {
                return Operators.China_Unicom;
            } else if (imsi.startsWith("46003")) {
                return Operators.China_Telecom;
            }
        }
        return Operators.China_Unkown;
    }
}
