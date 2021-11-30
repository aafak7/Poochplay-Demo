package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AllTrackerDataActivity extends AppCompatActivity {


    private List<String> lstDevicesName = new ArrayList<>();
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tracker_data);

        lstDevicesName=getIntent().getStringArrayListExtra("list");

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        listView = findViewById(R.id.listView);
        ListAdapter adapter = new ArrayAdapter<>(AllTrackerDataActivity.this, android.R.layout.simple_list_item_1, lstDevicesName);
        listView.setAdapter(adapter);


    }
}