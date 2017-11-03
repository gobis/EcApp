package com.gw.ecapp;

import android.app.Activity;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HardwareAddress
{
    private static final int BUF = 8192;
    private static final String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
    public static final String NoMac = "00:00:00:00:00:00";
    private static final String REQ = "select vendor from oui where mac=?";
    private static final String TAG = "HardwareAddress";
    private WeakReference<Activity> mActivity;

    public HardwareAddress(Activity paramActivity) {}

    public static String getHardwareAddress(String paramString)
    {
     /*   String str1 = "00:00:00:00:00:00";
        String str2 = null;
        if (paramString != null) {
            str2 = str1;
        }
        try
        {
            Pattern localPattern = Pattern.compile(String.format("^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$", new Object[] { paramString.replace(".", "\\.") }));
            str2 = str1;
            BufferedReader localBufferedReader = new BufferedReader(new FileReader("/proc/net/arp"), 8192);
            do
            {
                str2 = str1;
                String str3 = localBufferedReader.readLine();
                paramString = str1;
                if (str3 == null) {
                    break;
                }
                str2 = str1;
                paramString = localPattern.matcher(str3);
                str2 = str1;
            } while (!paramString.matches());
            str2 = str1;
            paramString = paramString.group(1);
            str2 = paramString;
            localBufferedReader.close();
        }
        catch (IOException paramString)
        {
            Log.e("HardwareAddress", "Can't open/read file ARP: " + paramString.getMessage());
            return str2;
        }
        String str2 = str1;
        Log.e("HardwareAddress", "ip is null");
        paramString = str1;*/
        return paramString;
    }
}
