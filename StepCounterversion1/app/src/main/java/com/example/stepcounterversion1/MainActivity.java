package com.example.stepcounterversion1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private SensorManager SensorManager;
    private Sensor Acc;
    private Button button;
    private TextView text_one;
    private TextView text_two;
    private int step=0;
    private double CurrentV=0;
    private double LastV=0;
    private double InitialV=0;
    private boolean upmoving=true;
    private static final String TAG = "LOG";
    private NotificationManager mNotificationManager;
    private String CHANNEL_ID = "1";
    private String name = "test";
    private NotificationChannel mChannel;
    private Notification notification;
    private Timer timer;
    private Map historyRecord;
    public static SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get history data from shared preferences
        pref = getSharedPreferences("The Preference",MODE_PRIVATE);
        Log.w(TAG,"The Shared preference is "+pref.getAll()+"");
        Log.w(TAG,pref.getAll().toString());
        //get today steps record when apps initialize
        int LastStepRecorded = pref.getInt(getTime(),0);
        Log.w(TAG,"The last step is"+LastStepRecorded+"");
        //update today steps
        step = LastStepRecorded;
        Log.w(TAG,"The step is"+step+"");
        Log.w(TAG,"This is recorded data"+pref.getInt(getTime(),0));
        //Bind
        SensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Acc = SensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SensorManager.registerListener(this,Acc,SensorManager.SENSOR_DELAY_UI);
        bind();
        text_one.setText(" "+step+"");
        text_two.setText(" "+step*0.04+"");
        initNotification();
        //Save data every 1 minute
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SharedPreferences.Editor editor = getSharedPreferences("The Preference", MODE_PRIVATE).edit();
                editor.putInt(getTime(), step);
                editor.apply();
                Log.w(TAG, step+" at "+getTime());
            }
        },0,60000);//every min
    }

    private void bind(){
        button = (Button)findViewById(R.id.button);
        text_one = (TextView)findViewById(R.id.tv_step);
        text_two = (TextView)findViewById(R.id.tv_Calories);
        button.setOnClickListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        //This method is used to track the steps by comparing highest peak and lowest peak of sensor value
        float[] values = event.values;
        double Sensitivity = 3;
        CurrentV = Math.sqrt(values[0]*values[0]+values[1]*values[1]+values[2]*values[2]);
        //phone is up moving
        if (upmoving){
            if(CurrentV>LastV){
                LastV = CurrentV;
            }
            else{
                //reach peak
                if(Math.abs(CurrentV-LastV)>Sensitivity){
                    InitialV = CurrentV;
                    upmoving = false;
                }
            }
        }
        //phone is down moving
        if(!upmoving){
            if(CurrentV<LastV){
                LastV = CurrentV;
            }
            else{
                //reach peak
                if(Math.abs(CurrentV-LastV)>Sensitivity){
                    InitialV = CurrentV;
                        step++;
                        text_one.setText(" "+step+"");
                        text_two.setText(" "+step*0.04+"");
                        updatenotification();
                    upmoving = true;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                Intent activity2Intent = new Intent(getApplicationContext(), History.class);
                String history = pref.getAll().toString();
                activity2Intent.putExtra("data",history);
                startActivity(activity2Intent);
                break;
        }
    }

    private void initNotification() {
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this)
                    .setChannelId(CHANNEL_ID)
                    .setContentText("Today: "+step+" Steps")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setOngoing(true).build();
        } else {
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentText("Today: "+step+" Steps")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setOngoing(true);
            notification = builder.build();
        }
        mNotificationManager.notify(TAG,1, notification);
        Log.w(TAG, "This is notification");
    }

    private void updatenotification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this)
                    .setChannelId(CHANNEL_ID)
                    .setContentText("Today: "+step+" Steps")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setOngoing(true).build();
        }else{
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setContentText("Today: "+step+" Steps")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setOngoing(true);
            notification = builder.build();
        }
        mNotificationManager.notify(TAG,1, notification);
    }

    private String getTime(){
        Date date = new Date();
        String time = date.toLocaleString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String sim = dateFormat.format(date);
        return sim;
    }

}