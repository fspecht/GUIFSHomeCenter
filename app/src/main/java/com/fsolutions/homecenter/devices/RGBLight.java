package com.fsolutions.homecenter.devices;

import com.fsolutions.homecenter.PropertiesActivity;

/**
 * Created by specht on 07/03/17.
 */

public class RGBLight extends Device {

    public static final String VERSION = "1";
    public static final String MODULE_NAME = "RGB_LIGHT";

    private int[] rgb = new int[3];
    private boolean statusChangeColor = false;
    private boolean statusMicr = false;
    private boolean statusBuzzer = false;
    private long nextAlarm = 0;


    /**
     * Example
     *
     *      192.168.0.15;RGB_LIGHT;1000;1;120;150;244;1;1;1;0"
     *
     * @param parameters
     */
    @Override
    public void loadInteralConfig(String[] parameters){
        rgb[0] = Integer.parseInt(parameters[4]);
        rgb[1] = Integer.parseInt(parameters[5]);
        rgb[2] = Integer.parseInt(parameters[6]);
        statusChangeColor = parameters[7].equals("1")?true:false;
        statusBuzzer = parameters[8].equals("1")?true:false;
        statusMicr = parameters[9].equals("1")?true:false;
        nextAlarm = Long.parseLong(parameters[10]);
    }

    @Override
    public String getType() {
        return MODULE_NAME;
    }

    @Override
    public String getTitle() {
        return "RGB Light";
    }

    @Override
    public String getIcon() {
        return null;
    }

    @Override
    public String getDeviceIdentity(){
        return MODULE_NAME + VERSION;
    }

    /**
     *
     * @return
     */
    public static DeviceDefinition getDefinition(){
        final DeviceDefinition defition = new
                DeviceDefinition(MODULE_NAME,
                RGBLight.class,
                PropertiesActivity.class);

        return defition;
    }

    public int getColorRed(){
        return rgb[0];
    }

    public void setColorRed(int redColor){
        rgb[0] = redColor;
    }

    public int getColorGreen(){
        return rgb[1];
    }

    public void setColorReGreen(int greenColor){
        rgb[1] = greenColor;
    }

    public int getColorBlue(){
        return rgb[2];
    }

    public void setColorBlue(int blueColor){
        rgb[2] = blueColor;
    }

    public String getChangeStatusURL(){ return getURL() + "/changeStatus"; }

    public String getChangeColorURL(){ return getURL() + "/changeColor"; }
}