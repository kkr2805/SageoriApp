package com.hgtech.sageoriapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MemberSpinnerAdapter extends ArrayAdapter<MemberItem> {
    List<MemberItem> memberItemList;

    public MemberSpinnerAdapter(Context context, int resource, List<MemberItem> objects) {
        super(context, resource, objects);

        memberItemList = objects;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View spinnerItem = convertView;
        MemberItem currentMemberItem = memberItemList.get(position);

        if(spinnerItem == null){
            spinnerItem = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_spinner_item, parent, false);
        }

        TextView nameTextView = (TextView) spinnerItem.findViewById(android.R.id.text1);
        nameTextView.setText(currentMemberItem.Name);


        return spinnerItem;
    }
}
