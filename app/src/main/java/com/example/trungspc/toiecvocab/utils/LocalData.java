package com.example.trungspc.toiecvocab.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jaison on 18/06/17.
 */

public class LocalData {

    private static final String APP_SHARED_PREFS = "Setting";

    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    private static final String reminderStatus = "reminderStatus";
    private static final String HOUR = "HOUR";
    private static final String MIN = "MIN";
    private final String TIME_REMINDER = "time_reminder";
    private final String TOPIC_REMINDER = "topic_reminder";

    public LocalData(Context context) {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    // Settings Page Set Reminder

    public boolean getReminderStatus() {
        return appSharedPrefs.getBoolean(reminderStatus, false);
    }

    public void setReminderStatus(boolean status) {
        prefsEditor.putBoolean(reminderStatus, status);
        prefsEditor.commit();
    }

    // Settings Page Reminder Time (Hour)

    public int getHour() {
        return appSharedPrefs.getInt(HOUR, 8);
    }

    public void setHour(int h) {
        prefsEditor.putInt(HOUR, h);
        prefsEditor.commit();
    }

    // Settings Page Reminder Time (Minutes)

    public int getMin() {
        return appSharedPrefs.getInt(MIN, 0);
    }

    public void setMin(int m) {
        prefsEditor.putInt(MIN, m);
        prefsEditor.commit();
    }

    public void clearReminder() {
        prefsEditor.remove(HOUR).remove(MIN).commit();
    }

    public void reset() {
        prefsEditor.clear();
        prefsEditor.commit();
    }

}
