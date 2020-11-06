package com.example.stepcounterversion1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class History extends AppCompatActivity {
    private String TAG = "History";
    private ListView lv;

/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        lv = (ListView) findViewById(R.id.lv);
        //get data of shared preferences
        Intent intent1 = this.getIntent();
        String history = "";
        history = intent1.getStringExtra("data");
        //transform data into string[]
        String[] HistoryRecord = new String[100];
        List<String> list = new ArrayList<String>();
        if(history.length()!=0) {
            //reduce "{" and "}"
            String temp1 = history.replace("{"," ");
            String temp2 = temp1.replace("}"," ");
            String[] historys = temp2.split(",");
            int i = 0;
            while(i<historys.length){
                //build history record
                String time = historys[i].split("=")[0];
                String step = historys[i].split("=")[1];
                HistoryRecord[i] = "Date: "+time+"     Number Of Steps: "+step;
                Log.w(TAG,HistoryRecord[i]);
                i++;
            }
            Log.w(TAG,HistoryRecord[0]);
            Log.w(TAG,HistoryRecord[1]);
            //remove all null string
            for(String s: HistoryRecord){
                if(s!=null&&s.length()>0){
                    list.add(s);
                }
            }
            lv.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, list));
        }
    }

    private String getTime(){
        Date date = new Date();
        String time = date.toLocaleString();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String sim = dateFormat.format(date);
        return sim;
    }
}