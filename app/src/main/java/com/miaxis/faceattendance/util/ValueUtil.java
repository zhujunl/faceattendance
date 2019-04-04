package com.miaxis.faceattendance.util;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueUtil {

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    public static final String DEFAULT_UPLOAD_URL = "";
    public static final String DEFAULT_PASSWORD = "666666";
    public static final float DEFAULT_QUALITY_SCORE = 75f;
    public static final float DEFAULT_VERIFY_SCORE = 0.8f;
    public static final int DEFAULT_RECORD_CLEAR_THRESHOLD = 500;

    public static final int GET_CARD_ID = 0;
    public static final int NO_CARD     = 134;
    public static final int mFingerDataSize = 512;

    public static final int PAGESIZE = 16;

    public static final String[] FOLK = { "汉", "蒙古", "回", "藏", "维吾尔", "苗", "彝", "壮", "布依", "朝鲜",
            "满", "侗", "瑶", "白", "土家", "哈尼", "哈萨克", "傣", "黎", "傈僳", "佤", "畲",
            "高山", "拉祜", "水", "东乡", "纳西", "景颇", "柯尔克孜", "土", "达斡尔", "仫佬", "羌",
            "布朗", "撒拉", "毛南", "仡佬", "锡伯", "阿昌", "普米", "塔吉克", "怒", "乌孜别克",
            "俄罗斯", "鄂温克", "德昂", "保安", "裕固", "京", "塔塔尔", "独龙", "鄂伦春", "赫哲",
            "门巴", "珞巴", "基诺", "", "", "穿青人", "家人", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "", "", "其他", "外国血统", "",
            "" };

    public static String unicode2String(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length / 2; i++) {
            int a = bytes[2 * i + 1];
            if (a < 0) {
                a = a + 256;
            }
            int b = bytes[2 * i];
            if (b < 0) {
                b = b + 256;
            }
            int c = (a << 8) | b;
            sb.append((char) c);
        }
        return sb.toString();
    }

    public static String fingerPositionCovert(byte finger) {
        switch ((int) finger) {
            case 11:
                return "右手拇指";
            case 12:
                return "右手食指";
            case 13:
                return "右手中指";
            case 14:
                return "右手环指";
            case 15:
                return "右手小指";
            case 16:
                return "左手拇指";
            case 17:
                return "左手食指";
            case 18:
                return "左手中指";
            case 19:
                return "左手环指";
            case 20:
                return "左手小指";
            case 97:
                return "右手不确定指位";
            case 98:
                return "左手不确定指位";
            case 99:
                return "其他不确定指位";
            default:
                return "其他不确定指位";
        }
    }

    public static String getCurVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean isHttpFormat(String str) {
        Pattern pattern = Pattern.compile("^(http://)([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}(:)(\\d{4})(/)[^\\s]{1,}");
        Matcher match = pattern.matcher(str);
        return match.matches();
    }

    /**
     * 获取IP
     *
     * @param application
     * @return
     */
    public static String getIP(Application application) {
        String ip = "0.0.0.0";
        ConnectivityManager connectivityManager = (ConnectivityManager) application.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null) {
            int type = info.getType();
            if (type == ConnectivityManager.TYPE_ETHERNET) {
                ip = getEtherNetIP();
            } else if (type == ConnectivityManager.TYPE_WIFI) {
                ip = getWifiIP(application);
            }
        }
        return ip;
    }

    /**
     * 获取有线地址
     *
     * @return
     */
    public static String getEtherNetIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()
                            && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("asd", ex.toString());
        }
        return "0.0.0.0";
    }

    /**
     * 获取wifiIP地址
     *
     * @param application
     * @return
     */
    public static String getWifiIP(Application application) {
        android.net.wifi.WifiManager wifi = (android.net.wifi.WifiManager) application.getSystemService(android.content.Context.WIFI_SERVICE);
        WifiInfo wifiinfo = wifi.getConnectionInfo();
        int intaddr = wifiinfo.getIpAddress();
        byte[] byteaddr = new byte[] { (byte) (intaddr & 0xff),
                (byte) (intaddr >> 8 & 0xff), (byte) (intaddr >> 16 & 0xff),
                (byte) (intaddr >> 24 & 0xff) };
        InetAddress addr = null;
        try {
            addr = InetAddress.getByAddress(byteaddr);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        String mobileIp = addr.getHostAddress();
        return mobileIp;
    }

    public static boolean isValidDate(String str) {
        boolean convertSuccess = true;
        try {
            ValueUtil.simpleDateFormat.parse(str);
        } catch (ParseException e) {
             e.printStackTrace();
            convertSuccess = false;
        }
        return convertSuccess;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    public static int getStateBarHeight(Context context){
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
