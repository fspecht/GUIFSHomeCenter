package com.fsolutions.homecenter;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.StrictMode;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by specht on 07/03/17.
 */
public class HomeCenterUtils {

    private static final String WEB_PROTOCOL = "http://";
    private static final String DEVICE_ID = "/deviceStatus";

    /**
     * Get Local IP
     * @param activity
     * @return
     */
    public static String getLocalIpAddress(Activity activity) {
        WifiManager wm = (WifiManager) activity.getSystemService(Activity.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        return ip;
    }

    public static List<String> getURL(List<String> url_){
        List<Thread> threads = new ArrayList<Thread>();
        final List<String> responseValues = new ArrayList<String>();


        for(final String url : url_){
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    responseValues.add(getURL(url));
                }
            };

            Thread t = new Thread(r);
            threads.add(t);
            t.start();

        }

        boolean threadEnd = true;
        while (threadEnd){
            threadEnd = false;
            try {
                for(Thread t : threads){
                    if(t.isAlive()){
                        threadEnd=true;
                        break;
                    }
                }
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        List<String> result = new ArrayList<String>();
        for(String l : responseValues){
            if(l!=null && !l.isEmpty()){
                result.add(l);
            }
        }

        return result;
    }

    public static String getURL(String url_){

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url_).newBuilder();
        String url = urlBuilder.build().toString();

        Response resp = null;
        String result = null;
        try {
            final Request req = new Request.Builder().url(url).get().build();
            resp = client.newCall(req).execute();
            final int code = resp.code();
            if(code==200){
                System.out.println(code);
                result = resp.body().string();
            }

        }
        catch (final Throwable th) {
            th.printStackTrace();
        }
        finally {
            if (resp != null) {
                try {
                    resp.body().close();
                }
                catch (final Throwable th) {
                    System.out.println(th.getMessage());
                }
            }
        }

        return result;
    }

    public static List<String> getDeviceList(String localIP){
        List<String> devices = new ArrayList<String>();
        int subNetworkIndex = localIP.lastIndexOf(".");
        String subNetz =  localIP.substring(0, subNetworkIndex+1);
        List<String> urls = new ArrayList<String>();

        for (int ipRange = 2; ipRange < 20; ipRange++) {
            String ipAddress = subNetz + ipRange;

            String url = WEB_PROTOCOL + ipAddress + DEVICE_ID;
            urls.add(url);
        }

        devices = getURL(urls);

        return devices;
    }
}
