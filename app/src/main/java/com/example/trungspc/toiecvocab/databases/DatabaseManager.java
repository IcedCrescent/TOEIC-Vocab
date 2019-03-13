package com.example.trungspc.toiecvocab.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.example.trungspc.toiecvocab.databases.models.CategoryModel;
import com.example.trungspc.toiecvocab.databases.models.TopicModel;
import com.example.trungspc.toiecvocab.databases.models.WordModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Trung's PC on 3/1/2018.
 */

public class DatabaseManager {

    private static final String TAG = "DatabaseManager";

    private static final String TABLE_TOPIC = "tbl_topic";
    private static final String TABLE_WORD = "tbl_word";

    private SQLiteDatabase sqLiteDatabase;
    private AssetHelper assetHelper;

    private static DatabaseManager databaseManager;

    public DatabaseManager(Context context) {
        assetHelper = new AssetHelper(context);
    }

    public static DatabaseManager getInstance(Context context) {
        if (databaseManager == null) {
            databaseManager = new DatabaseManager(context);
        }
        return databaseManager;
    }

    public List<TopicModel> getListTopic() {
        List<TopicModel> lstTopicModels = new ArrayList<>();
        sqLiteDatabase = assetHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_TOPIC, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            //read data
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            String imageUrl = cursor.getString(3);
            String category = cursor.getString(4);
            String color = cursor.getString(5);
            String lastTime = cursor.getString(6);


            
            lstTopicModels.add(new TopicModel(id, name, imageUrl, category, color, lastTime));
            //next row
            cursor.moveToNext();
        }

        Log.d(TAG, "getListTopic: " + lstTopicModels);
        
        return lstTopicModels;
    }

    public List<CategoryModel> getCategoryModel(List<TopicModel> topicModelList) {
        List<CategoryModel> categoryModelList = new ArrayList<>();
        for (int i = 0; i < topicModelList.size(); i += 5) {
            categoryModelList.add(new CategoryModel(topicModelList.get(i).getName(), topicModelList.get(i).getColor()));
        }

        return categoryModelList;
    }

    public HashMap<String, List<TopicModel>> getHashMapTopic(List<TopicModel> topicModelList, List<CategoryModel> categoryModelList) {
        HashMap<String, List<TopicModel>> hashMap = new HashMap<>();
        for (int i = 0; i < categoryModelList.size(); i++) {
            int positionTopic = i * 5;
            hashMap.put(categoryModelList.get(i).getName(), topicModelList.subList(positionTopic, positionTopic + 5));
        }

        return hashMap;
    }

    public WordModel getRandomWord(int topicID, int previousID) {
        sqLiteDatabase = assetHelper.getReadableDatabase();

        Cursor cursor;

        int level = 0;

        do {
            //step 1: get level
            double random = Math.random() * 100; //get random number in the range of 0 - 100
            if (random < 5)
                level = 4;
            else if (random < 15)
                level = 3;
            else if (random < 30)
                level = 2;
            else if (random < 60)
                level = 1;
            else
                level = 0;

            //step 2: get word
            cursor = sqLiteDatabase.rawQuery("select * from " + TABLE_WORD + " where topic_id = " + topicID + " and level = " + level + " and id <> " + previousID + " order by random() limit 1", null);
        } while (cursor.getCount() == 0);

        cursor.moveToNext();
        int id = cursor.getInt(0);
        String origin = cursor.getString(1);
        String explanation = cursor.getString(2);
        String type = cursor.getString(3);
        String pronunciation = cursor.getString(4);
        String imageUrl = cursor.getString(5);
        String example = cursor.getString(6);
        String exampleTrans = cursor.getString(7);

        return new WordModel(id, origin, explanation, type, pronunciation, imageUrl, example, exampleTrans, topicID, level);
    }

    public void updateWordLevel(WordModel wordModel, boolean known) {
        sqLiteDatabase = assetHelper.getWritableDatabase();

        int level = wordModel.getLevel();
        if (known && level < 4) {
            level++;
        } else if (!known && level > 0){
            level--;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("level", level);
        sqLiteDatabase.update(TABLE_WORD, contentValues, "id = " + wordModel.getId(), null);
    }

    public void updateLastTime(TopicModel topicModel, String lastTime) {
        sqLiteDatabase = assetHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("last_time", lastTime);
        sqLiteDatabase.update(TABLE_TOPIC, contentValues, "id = " + topicModel.getId(), null);
    }

    public int getLNumOfMasterWordByTopicId(int topicID) {
        sqLiteDatabase = assetHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select level from " + TABLE_WORD + " where level = 4 and topic_id = " + topicID, null);
        return cursor.getCount();
    }

    public int getNumOfNewWordByTopicId(int topicID) {
        sqLiteDatabase = assetHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.rawQuery("select level from " + TABLE_WORD + " where level = 0 and topic_id = " + topicID, null);
        return cursor.getCount();
    }
}
