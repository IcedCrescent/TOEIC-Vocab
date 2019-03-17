package com.example.trungspc.toiecvocab.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.trungspc.toiecvocab.R;
import com.example.trungspc.toiecvocab.adapters.ToeicExpandableListViewAdapter;
import com.example.trungspc.toiecvocab.databases.DatabaseManager;
import com.example.trungspc.toiecvocab.databases.models.CategoryModel;
import com.example.trungspc.toiecvocab.databases.models.TopicModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String REMINDER_CHANNEL_ID = "1";
    ExpandableListView elvCategory;
    ToeicExpandableListViewAdapter adapter;
    List<TopicModel> topicModelList;

    @Override
    protected void onStart() {
        super.onStart();
        adapter.refreshList(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creating an existing notification channel with its original values performs no operation,
        // so it's safe to call this code when starting an app
        createNotificationChannel();

        elvCategory = findViewById(R.id.elv_category);

        topicModelList = DatabaseManager.getInstance(this).getListTopic();
        List<CategoryModel> categoryModelList = DatabaseManager.getInstance(this).getCategoryModel(topicModelList);
        HashMap<String, List<TopicModel>> hashMap = DatabaseManager.getInstance(this).getHashMapTopic(topicModelList, categoryModelList);

        adapter = new ToeicExpandableListViewAdapter(categoryModelList, hashMap, this);
        elvCategory.setAdapter(adapter);

        elvCategory.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                TopicModel topicModel = topicModelList.get(groupPosition * 5 + childPosition); //for each group position (category), there are 5 child position after it (topic)

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                String lastTime = simpleDateFormat.format(Calendar.getInstance().getTime());

                DatabaseManager.getInstance(MainActivity.this).updateLastTime(topicModel, lastTime);

                Intent intent = new Intent(MainActivity.this, StudyActivity.class);
                intent.putExtra("topic", topicModel); //object passed through intent must implement Serializable
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting_menu_button, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_setting:
                //switch intent
                Intent intent = new Intent(this,SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.study_reminder);
            String description = getString(R.string.reminder_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(REMINDER_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 1000}); // delay 100ms, vibrate for 1 sec
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
