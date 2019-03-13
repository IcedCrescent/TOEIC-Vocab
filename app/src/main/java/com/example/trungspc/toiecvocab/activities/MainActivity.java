package com.example.trungspc.toiecvocab.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.trungspc.toiecvocab.R;
import com.example.trungspc.toiecvocab.adapters.ToiecExpandableListViewAdapter;
import com.example.trungspc.toiecvocab.databases.DatabaseManager;
import com.example.trungspc.toiecvocab.databases.models.CategoryModel;
import com.example.trungspc.toiecvocab.databases.models.TopicModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ExpandableListView elvCategory;
    ToiecExpandableListViewAdapter adapter;
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

        elvCategory = findViewById(R.id.elv_category);

        topicModelList = DatabaseManager.getInstance(this).getListTopic();
        List<CategoryModel> categoryModelList = DatabaseManager.getInstance(this).getCategoryModel(topicModelList);
        HashMap<String, List<TopicModel>> hashMap = DatabaseManager.getInstance(this).getHashMapTopic(topicModelList, categoryModelList);

        adapter = new ToiecExpandableListViewAdapter(categoryModelList, hashMap, this);
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
}
