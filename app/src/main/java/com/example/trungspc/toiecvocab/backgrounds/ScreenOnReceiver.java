package com.example.trungspc.toiecvocab.backgrounds;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.trungspc.toiecvocab.activities.ReviewActivity;
import com.example.trungspc.toiecvocab.activities.SettingActivity;

import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class ScreenOnReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        SharedPreferences sharedPreferences = context.getSharedPreferences("Setting", MODE_PRIVATE);
        Map<String, ?> sharedPreferencesAll = sharedPreferences.getAll();
//        for (Map.Entry entry : sharedPreferencesAll.entrySet()){
//            Log.d("Entry key", entry.getKey().toString());
//            Log.d("Entry value", entry.getValue().toString());
//        }
        boolean reviewEnabled = sharedPreferences.getBoolean(SettingActivity.WORD_REVIEWER, false);
        Log.d("ScreenOnReceiver", "sharedPreferences.WORD_REVIEWER " + reviewEnabled);
        if (reviewEnabled) {
            if (action != null) {
                if (action.equals(Intent.ACTION_SCREEN_ON) || action.equals(Intent.ACTION_USER_PRESENT)){
                    Log.d("ScreenOnReceiver", "onReceive");
                    Intent intent1 = new Intent(context, ReviewActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent1);
                }
            }
        }

    }
}
