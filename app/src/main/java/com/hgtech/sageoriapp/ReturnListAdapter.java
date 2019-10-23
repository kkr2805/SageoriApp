package com.hgtech.sageoriapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReturnListAdapter extends ArrayAdapter<ReturnItem> {
    List<ReturnItem> returnItemList;

    public ReturnListAdapter(Context context, int resource, List<ReturnItem> objects) {
        super(context, resource, objects);

        returnItemList = objects;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        ReturnItem currentReturnItem = returnItemList.get(position);

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.return_item, parent, false);
        }

        TextView machineIDView1 = (TextView) listItemView.findViewById(R.id.machine_id1);
        machineIDView1.setText(String.valueOf(currentReturnItem.MachineID1));

        TextView machineIDView2 = (TextView) listItemView.findViewById(R.id.machine_id2);
        machineIDView2.setText(String.valueOf(currentReturnItem.MachineID2));

        TextView returnLabel = (TextView)listItemView.findViewById(R.id.returnLabel);
        returnLabel.setText("회수");

        TextView returnView = (TextView) listItemView.findViewById(R.id.returnValue);
        returnView.setText(String.valueOf(currentReturnItem.Retrun));

        TextView serviceLabel = (TextView)listItemView.findViewById(R.id.serviceLabel);
        serviceLabel.setText("서비스");

        TextView serviceView = (TextView) listItemView.findViewById(R.id.service);
        serviceView.setText(String.valueOf(currentReturnItem.Service));

        TextView onePoneLabel = (TextView)listItemView.findViewById(R.id.onePoneLabel);
        onePoneLabel.setText("1 + 1");

        TextView onePoneView = (TextView) listItemView.findViewById(R.id.onePone);
        onePoneView.setText(String.valueOf(currentReturnItem.OnePone));

        TextView totalLabel = (TextView)listItemView.findViewById(R.id.totalLabel);
        totalLabel.setText("합계");

        TextView totalView = (TextView) listItemView.findViewById(R.id.total);
        totalView.setText(String.valueOf(currentReturnItem.Retrun + currentReturnItem.Service + currentReturnItem.OnePone));

        TextView memberNameView = (TextView) listItemView.findViewById(R.id.member_name);
        memberNameView.setText(currentReturnItem.MemberName);

        return listItemView;
    }
}
