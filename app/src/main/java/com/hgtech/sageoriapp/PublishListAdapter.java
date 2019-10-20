package com.hgtech.sageoriapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PublishListAdapter extends ArrayAdapter<PublishItem> {

    List<PublishItem> publishItemList;

    public PublishListAdapter(Context context, int resource, List<PublishItem> objects) {
        super(context, resource, objects);

        publishItemList = objects;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        PublishItem currentPublishItem = publishItemList.get(position);

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.publish_item, parent, false);
        }

        TextView machineIDView = (TextView) listItemView.findViewById(R.id.machine_id);
        machineIDView.setText(String.valueOf(currentPublishItem.MachineID));

        TextView creditLabel = (TextView)listItemView.findViewById(R.id.creditLabel);
        creditLabel.setText("Credit");

        TextView creditView = (TextView) listItemView.findViewById(R.id.credit);
        creditView.setText(String.valueOf(currentPublishItem.Credit));

        TextView bankLabel = (TextView)listItemView.findViewById(R.id.bankLabel);
        bankLabel.setText("Bank");

        TextView bankView = (TextView) listItemView.findViewById(R.id.bank);
        bankView.setText(String.valueOf(currentPublishItem.Bank));

        TextView totalLabel = (TextView)listItemView.findViewById(R.id.totalLabel);
        totalLabel.setText("합계");

        TextView totalView = (TextView) listItemView.findViewById(R.id.total);
        totalView.setText(String.valueOf(currentPublishItem.Credit + currentPublishItem.Bank));

        TextView memberNameView = (TextView) listItemView.findViewById(R.id.member_name);
        memberNameView.setText(currentPublishItem.MemberName);

        return listItemView;
    }

}
