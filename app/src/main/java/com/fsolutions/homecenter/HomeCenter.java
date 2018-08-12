package com.fsolutions.homecenter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.fsolutions.homecenter.devices.Device;
import com.fsolutions.homecenter.devices.DeviceDefinition;
import com.fsolutions.homecenter.devices.DeviceManager;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;


public class HomeCenter extends Activity implements View.OnClickListener{

    private static final String CONFIG_NAME = "homeCenterConfig";
    private List<Button> buttonList = new ArrayList<Button>();
    private Properties appConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTitle("Home Center");
        setContentView(R.layout.activity_home_center);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(mToolbar);

        loadConfigFile();
        DeviceManager.getInstance().loadDevices(appConfig);
        DeviceManager.getInstance().refreshAllDevices();
        createDeviceButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflator = getMenuInflater();
        mMenuInflator.inflate(R.menu.my_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if( item.getItemId() == R.id.action_refresh) {
            ProgressDialog dialog = ProgressDialog.show(this, "",
                    "Loading. Please wait...", true);

            List<String> devicesList = DeviceManager.getInstance().findDevices(this);
            DeviceManager.getInstance().loadDevices(devicesList);
            createDeviceButton();

            dialog.cancel();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        DeviceManager.getInstance().saveDevices(appConfig);
        saveConfig();
        super.onStop();
    }

    /**
     * Create Device Buttons
     */
    private void createDeviceButton(){
        HashMap<Integer, Device> devices = DeviceManager.getInstance().getDevices();
        buttonList.clear();

        LinearLayout ll = (LinearLayout) findViewById(R.id.verticalLayout);
        ll.removeAllViews();

        for(Integer devkey : devices.keySet()) {
            Device dev = devices.get(devkey);

            Button myButton = new Button(this);
            myButton.setText(dev.getTitle());
            myButton.setId(devkey);
            buttonList.add(myButton);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.addView(myButton, lp);

            myButton.setOnClickListener(this);
        }
    }

    /**
     * Load App Config
     */
    private void loadConfigFile(){
        InputStream inputSteam = null;
        appConfig = new Properties();

        try {
            inputSteam = openFileInput(CONFIG_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(inputSteam != null) {
            try {
                appConfig.load(inputSteam);
//                TODO: Clear Config for Test
//                appConfig.clear();
                inputSteam.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Save App Config
     */
    private void saveConfig(){
        try {
            FileOutputStream fos = openFileOutput(CONFIG_NAME, Context.MODE_PRIVATE);
            appConfig.store(fos, "");
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    private void clearConfig(){
        appConfig.clear();
        saveConfig();
    }

    @Override
    public void onClick(View v) {

        Device dev = DeviceManager.getInstance().getDevice(v.getId());
        String key = dev.getDeviceIdentity();

        DeviceDefinition definition = DeviceManager.getInstance().getDeviceDefinition(key);

        Intent it = new Intent(this, definition.getActivityclass());
        it.putExtra("device_id", v.getId());
        it.putExtra("dev", dev);
        startActivity(it);
    }
}