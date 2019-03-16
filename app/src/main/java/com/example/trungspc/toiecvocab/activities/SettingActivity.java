package com.example.trungspc.toiecvocab.activities;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.trungspc.toiecvocab.R;
import com.example.trungspc.toiecvocab.backgrounds.AlarmReceiver;
import com.example.trungspc.toiecvocab.backgrounds.NotificationScheduler;
import com.example.trungspc.toiecvocab.databases.DatabaseManager;
import com.example.trungspc.toiecvocab.databases.models.TopicModel;
import com.example.trungspc.toiecvocab.utils.LocalData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";
    public final String TOPIC_REMINDER = "topic_reminder";

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_save)
    ImageView ivSave;
    @BindView(R.id.sw_reminder)
    Switch swReminder;
    @BindView(R.id.tv_pick_timer)
    TextView tvPickTimer;
    @BindView(R.id.sw_review)
    Switch swReview;
    @BindView(R.id.lv_topics)
    ListView lvTopics;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String time;
    private LocalData localData;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences("Setting", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        localData = new LocalData(this);

        List<TopicModel> topicModels = DatabaseManager.getInstance(this).getListTopic();
        List<String> topicName = new ArrayList<>();
        for (TopicModel topicModel : topicModels) {
            topicName.add(topicModel.getName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_list_review, topicName);
        lvTopics.setAdapter(arrayAdapter);

        if (localData.getReminderStatus()) {
            swReminder.setChecked(true);
            String timeString = localData.getHour() + ":" + localData.getMin();
            tvPickTimer.setText(timeString);
        } else {
            tvPickTimer.setVisibility(View.GONE);
        }

        Set<String> set = sharedPreferences.getStringSet(TOPIC_REMINDER, null);
        if (set != null) {
            swReview.setChecked(true);
            List<String> listChecked = new ArrayList<>(set);
            for (String position : listChecked) {
                lvTopics.setItemChecked(Integer.parseInt(position), true);
            }
        } else {
            lvTopics.setVisibility(View.GONE);
        }

        swReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tvPickTimer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                if (buttonView.isShown()) {
                    if (isChecked)
                        setTimeReminder();
                }
            }
        });

        swReview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    lvTopics.setVisibility(View.VISIBLE);
                } else {
                    lvTopics.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setTimeReminder() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time = String.format("%02d:%02d", hourOfDay, minute);
                tvPickTimer.setText(time);
            }
        }, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }


    @OnClick({R.id.iv_back, R.id.iv_save, R.id.sw_reminder, R.id.tv_pick_timer, R.id.sw_review})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_save:
                saveSetting();
                break;
            case R.id.sw_reminder:
                break;
            case R.id.tv_pick_timer:
                setTimeReminder();
                break;
            case R.id.sw_review:
                break;
        }
    }

    private void saveSetting() {
        localData.setReminderStatus(swReminder.isChecked());
        if (swReminder.isChecked()) {
            localData.setHour(Integer.parseInt(time.substring(0, 2)));
            localData.setMin(Integer.parseInt(time.substring(3, 5)));
            NotificationScheduler.setReminder(SettingActivity.this, AlarmReceiver.class, localData.getHour(), localData.getMin());
        } else {
            NotificationScheduler.cancelReminder(SettingActivity.this, AlarmReceiver.class);
            localData.clearReminder();
        }

        if (swReview.isChecked()) {
            //get checked list
            Log.d(TAG, "saveSetting: " + lvTopics.getCheckedItemPositions());
            SparseBooleanArray sparseBooleanArray = lvTopics.getCheckedItemPositions();

            //add checked position to set
            Set<String> set = new HashSet<>();
            for (int i = 0; i < lvTopics.getAdapter().getCount(); i++) {
                if (sparseBooleanArray.get(i)) {
                    set.add(i + "");
                }
            }

            //save set to SharedPreference
            if (set.size() > 0) {
                editor.putStringSet(TOPIC_REMINDER, set);
            } else {
                editor.putStringSet(TOPIC_REMINDER, null);
            }
        } else {
            editor.putStringSet(TOPIC_REMINDER, null);
        }

        editor.commit();
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
