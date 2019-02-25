package com.example.vaish.gosafe;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.BeanManager;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.Callback;
import com.punchthrough.bean.sdk.message.DeviceInfo;
import com.punchthrough.bean.sdk.message.ScratchBank;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Bean b1;
    List<Bean> beans;
    LocationManager l;
    String la,lo;
    double lat,lon;
    Button save;
    EditText num, num2,num3;
    String str, str2, str3;
    LocationListener c = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lat=location.getLatitude();
            la= String.valueOf(lat);
            lon=location.getLongitude();
            lo= String.valueOf(lon);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        l = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        l.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, c);
        save = (Button) findViewById(R.id.button);
        num=(EditText) findViewById(R.id.editText);
        num2=(EditText) findViewById(R.id.editText2);
        num3=(EditText) findViewById(R.id.editText3);
        beans = new ArrayList<>();

        BeanDiscoveryListener listener = new BeanDiscoveryListener() {
            @Override
            public void onBeanDiscovered(Bean bean, int rssi) {
                beans.add(bean);
                Toast.makeText(MainActivity.this, "BEAN ADDED", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDiscoveryComplete() {
                Integer i = 0;
                for (Bean bean : beans) {
                    if (i == 0) {
                        b1 = bean;
                        i++;
                    }
                    System.out.println(bean.getDevice().getName());   // "Bean"              (example)
                    System.out.println(bean.getDevice().getAddress());    // "B4:99:4C:1E:BC:75" (example)
                }
                System.out.println("Discovery complete");
                System.out.println(b1.getDevice().getName());   // "Bean"              (example)
                System.out.println(b1.getDevice().getAddress());
                BeanListener beanListener = new BeanListener() {
                    @Override
                    public void onConnected() {
                        Toast.makeText(MainActivity.this, "Connected to bean", Toast.LENGTH_SHORT).show();
                        b1.readDeviceInfo(new Callback<DeviceInfo>() {
                            @Override
                            public void onResult(DeviceInfo deviceInfo) {
                                System.out.println(deviceInfo.hardwareVersion());
                                System.out.println(deviceInfo.firmwareVersion());
                                System.out.println(deviceInfo.softwareVersion());
                            }
                        });
                    }

                    @Override
                    public void onConnectionFailed() {
                        Toast.makeText(MainActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDisconnected() {
                        Toast.makeText(MainActivity.this, "Disconnected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSerialMessageReceived(byte[] data) {
                        Toast.makeText(MainActivity.this, "Serial Message Received", Toast.LENGTH_SHORT).show();
                        Toast.makeText(MainActivity.this,""+data, Toast.LENGTH_SHORT).show();
                        sendSMSMessage();
                        try {
                            TimeUnit.MINUTES.sleep(5);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onScratchValueChanged(ScratchBank bank, byte[] value) {
                        Toast.makeText(MainActivity.this, "Scratch Value Changed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(BeanError error) {
                        Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onReadRemoteRssi(int rssi) {
                        Toast.makeText(MainActivity.this, "Read Remote Rssi", Toast.LENGTH_SHORT).show();
                    }
                };
                b1.connect(getApplicationContext(),beanListener);
            }
        };

        BeanManager.getInstance().startDiscovery(listener);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str = num.getText().toString();
                str2= num2.getText().toString();
                str3=num3.getText().toString();
                Toast.makeText(MainActivity.this," Number Saved!",Toast.LENGTH_SHORT).show();

            }
        });


    }
    protected void sendSMSMessage() {
        Log.i("Send SMS", "");
        String phoneNo = str;
        String phoneNo2= str2;
        String phoneNo3 = str3;
        String message = new String("Help me! I am in danger! My location is : Lattitude: "+la+" Longitude:"+lo);
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo,null,message,null,null);
            smsManager.sendTextMessage(phoneNo2,null,message,null,null);
            smsManager.sendTextMessage(phoneNo3,null,message,null,null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
