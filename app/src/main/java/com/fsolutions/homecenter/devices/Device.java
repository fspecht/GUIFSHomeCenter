package com.fsolutions.homecenter.devices;

import java.io.Serializable;

/**
 * Created by specht on 09/03/17.
 */

public abstract class Device implements Serializable{
    private String IPAddress;
    private String id;
    private String internalConfig;

    private boolean connected;

    public abstract String getType();
    public abstract String getTitle();
    public abstract String getIcon();
    public abstract void loadInteralConfig(String[] paramenters);
    public abstract String getDeviceIdentity();

    /**
     *
     * @param paramenters
     */
    public void load(String[] paramenters){
        IPAddress = paramenters[0];
        id = paramenters[3];
        internalConfig = "";

        for(String p : paramenters){
            if(!internalConfig.isEmpty()){
                internalConfig += ";";
            }

            internalConfig += p;
        }

        loadInteralConfig(paramenters);

        setConnected(true);
    }

    public String getId(){
        return id;
    }

    public String getIPAddress(){
        return IPAddress;
    }

    public String getInteralConfig(){
        return internalConfig;
    }

    public String getURL(){
        return "http://" + getIPAddress();
    }

    public String getStatusURL(){
        return getURL() + "/deviceStatus";
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
