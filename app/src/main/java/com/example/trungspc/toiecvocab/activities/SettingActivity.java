package com.example.trungspc.toiecvocab.activities;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.trungspc.toiecvocab.R;
import com.example.trungspc.toiecvocab.backgrounds.AlarmReceiver;
import com.example.trungspc.toiecvocab.backgrounds.NotificationScheduler;
import com.example.trungspc.toiecvocab.utils.CommonConst;
import com.example.trungspc.toiecvocab.utils.LocalData;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "SettingActivity";
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_topic)
    TextView tvTopic;
    @BindView(R.id.iv_save)
    ImageView ivSave;
    @BindView(R.id.view1)
    View view1;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.sw_reminder)
    Switch swReminder;
    @BindView(R.id.tv_enable_timer)
    TextView tvEnableTimer;
    @BindView(R.id.tv_pick_timer)
    TextView tvPickTimer;
    @BindView(R.id.view2)
    View view2;
    @BindView(R.id.tv_review)
    TextView tvReview;
    @BindView(R.id.sw_review)
    Switch swReview;
    @BindView(R.id.view3)
    View view3;
    @BindView(R.id.lblWordsToCheck)
    TextView lblWordsToCheck;
    @BindView(R.id.spinnerWordCount)
    Spinner spinnerWordCount;
    @BindView(R.id.linearLayoutChoose)
    LinearLayout linearLayoutChoose;
    @BindView(R.id.swAutoPlaySound)
    TextView swAutoPlaySound;
    @BindView(R.id.sw_play_sound)
    Switch swPlaySound;

    private boolean reviewEnabled = false;
    private boolean remindEnabled = false;
    private boolean btnSaveClicked = false;
    private boolean btnUpdateClicked = false;
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

        if (localData.getReminderStatus()) {
            swReminder.setChecked(true);
            String timeString = localData.getHour() + ":" + localData.getMin();
            tvPickTimer.setText(timeString);
        } else {
            tvPickTimer.setVisibility(View.GONE);
        }

        boolean set = sharedPreferences.getBoolean(CommonConst.WORD_REVIEWER, false);
        if (set) {
            swReview.setChecked(true);
            linearLayoutChoose.setVisibility(View.VISIBLE);
        } else {
            linearLayoutChoose.setVisibility(View.GONE);
        }

        boolean autoPlaySoundSet = sharedPreferences.getBoolean(CommonConst.PLAY_SOUND_AUTO, false);
        if (autoPlaySoundSet) {
            swPlaySound.setChecked(true);
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
                    linearLayoutChoose.setVisibility(View.VISIBLE);
                } else {
                    linearLayoutChoose.setVisibility(View.GONE);
                }
            }
        });

        swReview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linearLayoutChoose.setVisibility(View.VISIBLE);
                } else {
                    linearLayoutChoose.setVisibility(View.GONE);
                }
            }
        });

        swPlaySound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editor.putBoolean(CommonConst.PLAY_SOUND_AUTO, true);
                } else {
                    editor.putBoolean(CommonConst.PLAY_SOUND_AUTO, false);
                }
            }
        });

        //set value for spinner
        final ArrayList<Integer> integers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            integers.add(i);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, integers);
        spinnerWordCount.setAdapter(adapter);
        int prevSelection = sharedPreferences.getInt(CommonConst.WORD_REVIEW_COUNT, 0);
        spinnerWordCount.setSelection(prevSelection);

        //set value to SharedPreferences
        spinnerWordCount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editor.putInt(CommonConst.WORD_REVIEW_COUNT, position);
                spinnerWordCount.setSelection(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(SettingActivity.this, "You must specify the number of words for review", Toast.LENGTH_SHORT).show();
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
                saveSettings();
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

    private void saveSettings() {
        localData.setReminderStatus(swReminder.isChecked());
        if (swReminder.isChecked()) {
            if (time != null) {
                localData.setHour(Integer.parseInt(time.substring(0, 2)));
                localData.setMin(Integer.parseInt(time.substring(3, 5)));
            }
            NotificationScheduler.setReminder(SettingActivity.this, AlarmReceiver.class, localData.getHour(), localData.getMin());
        } else {
            NotificationScheduler.cancelReminder(SettingActivity.this, AlarmReceiver.class);
            localData.clearReminder();
        }

        if (swReview.isChecked()) {
            //get checked list
            Log.d(TAG, "saveSetting: " + " review enabled");
            //save set to SharedPreference
            editor.putBoolean(CommonConst.WORD_REVIEWER, true);
        } else {
            editor.putBoolean(CommonConst.WORD_REVIEWER, false);
            Log.d(TAG, "saveSetting: " + " review disabled");
        }

        editor.commit();
        Toast.makeText(this, "All changes saved!", Toast.LENGTH_SHORT).show();
        this.finish();
    }
}
