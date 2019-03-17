package com.example.trungspc.toiecvocab.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.trungspc.toiecvocab.R;
import com.example.trungspc.toiecvocab.databases.DatabaseManager;
import com.example.trungspc.toiecvocab.databases.models.CategoryModel;
import com.example.trungspc.toiecvocab.databases.models.TopicModel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Trung's PC on 3/4/2018.
 */

public class ToeicExpandableListViewAdapter extends BaseExpandableListAdapter {

    List<CategoryModel> categoryModelList;
    HashMap<String, List<TopicModel>> topicModelHashMap;
    Context context;
    int PROGRESS_PARTS = 12;

    public ToeicExpandableListViewAdapter(List<CategoryModel> categoryModelList, HashMap<String, List<TopicModel>> topicModelHashMap, Context context) {
        this.categoryModelList = categoryModelList;
        this.topicModelHashMap = topicModelHashMap;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return categoryModelList.size();
    }

    //index of children in the group
    @Override
    public int getChildrenCount(int groupPosition) {
        return topicModelHashMap.get(categoryModelList.get(groupPosition).getName()).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return topicModelHashMap.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return topicModelHashMap.get(categoryModelList.get(groupPosition).getName()).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        convertView = layoutInflater.inflate(R.layout.item_list_category, parent, false);

        CategoryModel categoryModel = categoryModelList.get(groupPosition); //CategoryModel categoryModel = (CategoryModel) getGroup(groupPosition);
        TextView tvCategory = convertView.findViewById(R.id.tv_category);
        TextView tvCategoryDes = convertView.findViewById(R.id.tv_category_des);
        ImageView ivArrow = convertView.findViewById(R.id.iv_arrow);
        CardView cvCategory = convertView.findViewById(R.id.cv_category);

        cvCategory.setBackgroundColor(Color.parseColor(categoryModel.getColor()));

        if (isExpanded) {
            ivArrow.setImageResource(R.drawable.ic_keyboard_arrow_up_white_24dp);
        } else {
            ivArrow.setImageResource(R.drawable.ic_keyboard_arrow_down_white_24dp);
        }
        tvCategory.setText(categoryModel.getName());

        String description = "";
        for (int i = 0; i < 5; i++) {
            description += topicModelHashMap.get(categoryModel.getName()).get(i).getName() + ((i != 4) ? ", " : "");
        }
        tvCategoryDes.setText(description);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        convertView = layoutInflater.inflate(R.layout.item_list_topic, parent, false);

        TopicModel topicModel = (TopicModel) getChild(groupPosition, childPosition);

        TextView tvTopic = convertView.findViewById(R.id.tv_name_topic);
        TextView tvLastTime = convertView.findViewById(R.id.tv_last_time);
        ProgressBar pbTopic = convertView.findViewById(R.id.pb_topic);

        pbTopic.setMax(PROGRESS_PARTS);
        pbTopic.setProgress(DatabaseManager.getInstance(context).getLNumOfMasterWordByTopicId(topicModel.getId()));
        pbTopic.setSecondaryProgress(PROGRESS_PARTS - DatabaseManager.getInstance(context).getNumOfNewWordByTopicId(topicModel.getId()));

        tvTopic.setText(topicModel.getName());
        if (topicModel.getLastTime() != null) {
            tvLastTime.setText(topicModel.getLastTime());
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true; //if return true, child divider will be shown
    }

    public void refreshList(Context context) {
        //1. change data
//        topicModelHashMap = DatabaseManager.getInstance()
        topicModelHashMap.clear();
        topicModelHashMap.putAll(DatabaseManager.getInstance(context).getHashMapTopic(DatabaseManager.getInstance(context).getListTopic(), categoryModelList));
        //2. refresh: add, remove, addAll, removeAll, clear,...
        notifyDataSetChanged();
    }
}