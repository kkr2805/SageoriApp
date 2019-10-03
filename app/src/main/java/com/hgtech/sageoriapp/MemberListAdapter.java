package com.hgtech.sageoriapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MemberListAdapter extends ArrayAdapter<MemberItem>{

    List<MemberItem> memberItemList;

    public MemberListAdapter(Context context, int resource, List<MemberItem> objects) {
        super(context, resource, objects);

        memberItemList = objects;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        MemberItem currentMemberItem = memberItemList.get(position);

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.member_item, parent, false);
        }

        TextView noTextView = (TextView) listItemView.findViewById(R.id.no);
        noTextView.setText(String.valueOf(currentMemberItem.No));

        TextView nameTextView = (TextView) listItemView.findViewById(R.id.name);
        nameTextView.setText(currentMemberItem.Name);

        TextView ageTextView = (TextView) listItemView.findViewById(R.id.HP);
        ageTextView.setText(currentMemberItem.HP);


        return listItemView;
    }
}
