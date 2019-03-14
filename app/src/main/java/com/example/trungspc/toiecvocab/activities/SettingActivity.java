package com.example.trungspc.toiecvocab.activities;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.trungspc.toiecvocab.R;
import com.example.trungspc.toiecvocab.backgrounds.ReminderService;
import com.example.trungspc.toiecvocab.backgrounds.ReviewService;
import com.example.trungspc.toiecvocab.databases.DatabaseManager;
import com.example.trungspc.toiecvocab.databases.models.TopicModel;

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
    public final static String TIME_REMINDER = "time_reminder";
    public final static String WORD_REVIEWER = "word_reviewer";

    private boolean reviewEnabled = false;
    private boolean remindEnabled = false;
    private boolean btnSaveClicked = false;
    private boolean btnUpdateClicked = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String time;
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
    @BindView(R.id.txtNoWordsToCheck)
    EditText txtNoWordsToCheck;
    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.linearLayoutSave)
    LinearLayout linearLayoutSave;
    @BindView(R.id.lblWordsToCheckSaved)
    TextView lblWordsToCheckSaved;
    @BindView(R.id.btnUpdate)
    Button btnUpdate;
    @BindView(R.id.linearLayoutUpdate)
    LinearLayout linearLayoutUpdate;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences("Setting", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        List<TopicModel> topicModels = DatabaseManager.getInstance(this).getListTopic();
        List<String> topicName = new ArrayList<>();
        for (TopicModel topicModel : topicModels) {
            topicName.add(topicModel.getName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.item_list_review, topicName);

        time = sharedPreferences.getString(TIME_REMINDER, null);
        if (time != null) {
            tvPickTimer.setText(time);
            swReminder.setChecked(true);
        } else {
            tvPickTimer.setVisibility(View.GONE);
        }

        boolean set = sharedPreferences.getBoolean(WORD_REVIEWER, false);
        if (set) {
            swReview.setChecked(true);
            linearLayoutSave.setVisibility(View.VISIBLE);
        } else {
            linearLayoutSave.setVisibility(View.GONE);
        }

        swReminder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    tvPickTimer.setVisibility(View.VISIBLE);
//                } else {
//                    tvPickTimer.setVisibility(View.GONE);
//                }
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
                    //let user choose number of words to review
                    linearLayoutSave.setVisibility(View.VISIBLE);
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
        if (swReminder.isChecked()) {
            editor.putString(TIME_REMINDER, time);
            //notif
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            Intent intent = new Intent(this, ReminderService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, 2)));
            calendar.set(Calendar.MINUTE, Integer.parseInt(time.substring(3, 5)));

            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        } else {
            editor.putString(TIME_REMINDER, null);
        }
//
        if (swReview.isChecked()) {
            //get checked list
            Log.d(TAG, "saveSetting: " + " review enabled");
            //save set to SharedPreference
            editor.putBoolean(WORD_REVIEWER, true);
        } else {
            editor.putBoolean(WORD_REVIEWER, false);
            Log.d(TAG, "saveSetting: " + " review disabled");
        }

        editor.commit();
        Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    @OnClick(R.id.btnSave)
    public void onViewClicked() {
        //check if a number is entered
        if (txtNoWordsToCheck.getText() == null || txtNoWordsToCheck.getText().toString().isEmpty()) {
            AlertDialog alertDialog = new AlertDialog.Builder(SettingActivity.this).create();
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("You must set number of words for review!");
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        } else {//a number was entered
            btnSaveClicked = true;
            //set save screen to invisible
            linearLayoutSave.setVisibility(View.GONE);
            //set update screen to visible
            linearLayoutUpdate.setVisibility(View.VISIBLE);

            //start review service
            Log.d(TAG, "Starting review service in the background");
            Intent intent = new Intent(SettingActivity.this, ReviewService.class);
            intent.setAction(ReviewService.ACTION_REVIEW);
            startService(intent);
        }

    }

    @OnClick(R.id.btnUpdate)
    public void onUpdateClicked() {
        btnUpdateClicked = true;
        //set update screen to invisible
        linearLayoutUpdate.setVisibility(View.GONE);
        //set save screen to visible
        linearLayoutSave.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if (reviewEnabled && !btnSaveClicked) {
                AlertDialog alertDialog = new AlertDialog.Builder(SettingActivity.this).create();
                alertDialog.setTitle("Warning");
                alertDialog.setMessage("You must set number of words for review!");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
        } else {
            super.onBackPressed();
        }

    }
}
