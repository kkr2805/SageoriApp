package com.hgtech.sageoriapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PublishListAdapter extends ArrayAdapter<PublishItem>
                                implements Filterable {

    private class ListFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults() ;

            if (constraint == null || constraint.length() == 0) {
                results.values = publishItemList;
                results.count = publishItemList.size() ;
            } else {
                List<PublishItem> itemList = new ArrayList<PublishItem>() ;

                for (PublishItem item : publishItemList) {
                    if (item.MemberName.toUpperCase().contains(constraint.toString().toUpperCase()))
                    {
                        itemList.add(item);
                    }
                }

                results.values = itemList ;
                results.count = itemList.size() ;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // update listview by filtered data list.
            filteredItemList = (List<PublishItem>) results.values ;

            // notify
            if (results.count > 0) {
                notifyDataSetChanged() ;
            } else {
                notifyDataSetInvalidated() ;
            }
        }
    }

    List<PublishItem> publishItemList;
    List<PublishItem> filteredItemList = publishItemList;
    Filter listFilter;

    public PublishListAdapter(Context context, int resource, List<PublishItem> objects) {
        super(context, resource, objects);

        publishItemList = objects;
        filteredItemList = objects;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        PublishItem currentPublishItem = filteredItemList.get(position);

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

        TextView createdDate = (TextView)listItemView.findViewById(R.id.CreatedDate);
        createdDate.setText(new SimpleDateFormat("yyyy.MM.dd hh시 mm분").format(currentPublishItem.Date));

        return listItemView;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return filteredItemList.size() ;
    }

    @Override
    public Filter getFilter() {
        if (listFilter == null) {
            listFilter = new ListFilter() ;
        }

        return listFilter ;
    }


}
