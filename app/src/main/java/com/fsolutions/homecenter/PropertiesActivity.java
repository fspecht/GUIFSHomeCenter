package com.fsolutions.homecenter;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.fsolutions.homecenter.devices.Device;
import com.fsolutions.homecenter.devices.DeviceManager;
import com.fsolutions.homecenter.devices.RGBLight;

import java.io.IOException;

public class PropertiesActivity extends AppCompatActivity {

    private RGBLight currentDevice;
    TextView color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_properties);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            currentDevice = (RGBLight) bundle.get("dev");
        }

        if(!currentDevice.isConnected()){
            DeviceManager.getInstance().reloadDevice(currentDevice);
        }

        final TextView tvDeviceName = (TextView) findViewById(R.id.device_name);
        final TextView tvDeviceID = (TextView) findViewById(R.id.device_id);
        final TextView tvDeviceIP = (TextView) findViewById(R.id.device_ip);

        final Switch change_color_status = (Switch) findViewById((R.id.change_color_status));
        final Switch buzzer_status = (Switch) findViewById((R.id.buzzer_status));
        final Switch mic_status = (Switch) findViewById((R.id.mic_status));

        final SeekBar redBar = (SeekBar) findViewById(R.id.seekBar_red);
        final SeekBar greenBar = (SeekBar) findViewById(R.id.seekBar_green);
        final SeekBar blueBar = (SeekBar) findViewById(R.id.seekBar_blue);

        tvDeviceName.setText(tvDeviceName.getText() + currentDevice.getTitle());
        tvDeviceID.setText(tvDeviceID.getText() + currentDevice.getId());
        tvDeviceIP.setText(tvDeviceIP.getText() + currentDevice.getIPAddress());

        redBar.setProgress(currentDevice.getColorRed());
        greenBar.setProgress(currentDevice.getColorGreen());
        blueBar.setProgress(currentDevice.getColorBlue());

        color = (TextView) findViewById(R.id.backgroundView);
        color.setBackgroundColor(Color.argb(128, currentDevice.getColorRed(), currentDevice.getColorGreen(), currentDevice.getColorBlue()));

        SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                StringBuffer url = new StringBuffer();
                url.append(currentDevice.getChangeColorURL()).append("?");
                url.append("r=").append(redBar.getProgress()).append("&");
                url.append("g=").append(greenBar.getProgress()).append("&");
                url.append("b=").append(blueBar.getProgress());

                HomeCenterUtils.getURL(url.toString());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeColor();
            }
        };

        CompoundButton.OnCheckedChangeListener switchListener =
                new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                StringBuffer url = new StringBuffer();
                url.append(currentDevice.getChangeStatusURL()).append("?");
                url.append("changeColor=").append(change_color_status.isChecked()?1:0).append("&");
                url.append("micStatus=").append(mic_status.isChecked()?1:0).append("&");
                url.append("buzzerStatus=").append(buzzer_status.isChecked()?1:0);

                HomeCenterUtils.getURL(url.toString());
            }
        };

//      Set Listener
        change_color_status.setOnCheckedChangeListener(switchListener);
        mic_status.setOnCheckedChangeListener(switchListener);
        buzzer_status.setOnCheckedChangeListener(switchListener);

        redBar.setOnSeekBarChangeListener(listener);
        greenBar.setOnSeekBarChangeListener(listener);
        blueBar.setOnSeekBarChangeListener(listener);
    }

    private void changeColor(){
        SeekBar redBar = (SeekBar) findViewById(R.id.seekBar_red);
        SeekBar greenBar = (SeekBar) findViewById(R.id.seekBar_green);
        SeekBar blueBar = (SeekBar) findViewById(R.id.seekBar_blue);

        color.setBackgroundColor(Color.argb(255, redBar.getProgress(), greenBar.getProgress(), blueBar.getProgress()));
    }
}
