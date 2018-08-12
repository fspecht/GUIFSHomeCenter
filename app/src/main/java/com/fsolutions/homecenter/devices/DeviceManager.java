package com.fsolutions.homecenter.devices;

import android.app.Activity;
import android.util.Log;

import com.fsolutions.homecenter.HomeCenterUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created by specht on 11/03/17.
 */

public class DeviceManager {
    private static final String APP_CONFIG_NAME = "APP";

    private static DeviceManager instance;

    private HashMap<Integer, Device> devices;
    private HashMap<String, DeviceDefinition> devicesDefinition;

    public DeviceManager(){
        devices = new HashMap<Integer, Device>();

        devicesDefinition = new HashMap<String, DeviceDefinition>();
        devicesDefinition.put(RGBLight.MODULE_NAME+RGBLight.VERSION, RGBLight.getDefinition());
    }

    /**
     * Return Singleton for Device Manager
     * @return
     */
    public static DeviceManager getInstance(){
        if(instance==null){
            instance = new DeviceManager();
        }

        return instance;
    }

    /**
     * Find Devides
     * Make a Broadcast at Local Network
     * @return parameter from Device
     */
    public List<String> findDevices(Activity activity){
        String localIP = HomeCenterUtils.getLocalIpAddress(activity);
        return findDevices(localIP);
    }

    /**
     * Find Devides
     * Make a Broadcast at Local Network
     * @return parameter from Device
     */
    public List<String> findDevices(String localIP){
        List<String> deviceList = HomeCenterUtils.getDeviceList(localIP);

        return deviceList;
    }

    /**
     * Load Devices
     *
     * @param devicesParametern
     */
    public void loadDevices(List<String> devicesParametern){
        for (String param: devicesParametern){
            Device dev = loadDevice(param);
            if(dev != null){
                devices.put(dev.getId().hashCode(), dev);

            }
        }
    }

    /**
     * Load Devices
     *
     * @param config
     */
    public void loadDevices(Properties config){
        int cnt = 0;
        while(config.containsKey(APP_CONFIG_NAME + "_" + cnt)){
            String prop = config.getProperty(APP_CONFIG_NAME + "_" + cnt);
            Device dev = loadDevice(prop);

            if(dev != null){
                devices.put(dev.getId().hashCode(), dev);
            }
            cnt++;
        }
    }

    /**
     * load Device
     *
     * @param parameters
     *  Example: "192.168.0.15;RGB_LIGHT;1;1000;120;150;244;1;1;1;0"
     * @return
     */
    private Device loadDevice(String parameters){
        String[] param = parameters.split(";");

        if(param.length < 10){
            return null;
        }

        String deviceType = param[1].trim();
        String deviceID = param[2].trim();

        DeviceDefinition devDefinition = devicesDefinition.get(deviceType + deviceID);

        if(devDefinition==null) {
            Log.w("DeviceManager", "Invalid device " + param[0]);
            return null;
        }

        try {
            Class<?> myClass = Class.forName(devDefinition.getDataclass().getName());
            Constructor<?> constructor = myClass.getConstructors()[0];
            Object object = constructor.newInstance(new Object[]{});

            if (!(object instanceof Device)) {
//                TODO:Exception
            }

            Device d = (Device) object;
            d.load(param);

            return d;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Save Devices Config
     * @param p
     */
    public void saveDevices(Properties p){
        p.clear();

        HashMap<Integer, Device> devices = DeviceManager.getInstance().getDevices();
        int cnt = 0;
        for(Device d : devices.values()){
            p.setProperty(APP_CONFIG_NAME + "_" + cnt, d.getInteralConfig());
            cnt++;
        }
    }

    /**
     *
     */
    public void refreshAllDevices(){
        for (Device d : devices.values() ) {
            try {
                refreshDevice(d);
            } catch (IOException e) {
                d.setConnected(false);
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param device
     */
    public void refreshDevice(Device device) throws IOException {
        String url = device.getStatusURL();
        String parameters = HomeCenterUtils.getURL(url);
        String[] param = parameters.split(";");
        device.load(param);
    }

    /**
     * Reload Device
     * @param device
     */
    public void reloadDevice(Device device){
        List<String> devices = findDevices(device.getIPAddress());
        for(String devParameter: devices){
            if(devParameter.contains(device.getDeviceIdentity())){
                device = loadDevice(devParameter);
            }
        }
    }

    /**
     * Get Device List
     * @return
     */
    public HashMap<Integer, Device> getDevices(){
        return devices;
    }

    /**
     *
     * @param key
     * @return
     */
    public Device getDevice(int key){
        return devices.get(key);
    }

    /**
     *
     * @param key
     * @return
     */
    public DeviceDefinition getDeviceDefinition(String key){
        return devicesDefinition.get(key);
    }
}