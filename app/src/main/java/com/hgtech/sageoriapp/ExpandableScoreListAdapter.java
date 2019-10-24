package com.hgtech.sageoriapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class ExpandableScoreListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int SCORE_ITEM = 0;
    static final int EXCHANGE_ITEM = 1;

    private List<ScoreItem> scoreItemList;

    public ExpandableScoreListAdapter(List<ScoreItem> data) {
        this.scoreItemList = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {

        View view = null;
        Context context = parent.getContext();
        float dp = context.getResources().getDisplayMetrics().density;
        int subItemPaddingLeft = (int) (18 * dp);
        int subItemPaddingTopAndBottom = (int) (5 * dp);

        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        switch (type) {
            case SCORE_ITEM:
                view = inflater.inflate(R.layout.score_item, parent, false);
                return new RecyclerView.ViewHolder(view){

                };

            case EXCHANGE_ITEM:
                view = inflater.inflate(R.layout.score_item, parent, false);
                return new RecyclerView.ViewHolder(view){

                };
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int type = getItemViewType(position);

        switch (type) {
            case SCORE_ITEM:
                ScoreItem scoreItem = getScoreItem(position);
                bindScoreView(holder.itemView, scoreItem);

                break;
            case EXCHANGE_ITEM:
                ExchageItem exchageItem = getExchangeItem(position);
                bindExchageView(holder.itemView, exchageItem);

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {

        int k = 0;
        for(ScoreItem item: scoreItemList)
        {
            if(k == position)
                return SCORE_ITEM;

            int subItemCount = item.exchageItemList.size();
            if(position < k + 1 + subItemCount)
                return EXCHANGE_ITEM;

            k = k + 1 + subItemCount;
        }

        return SCORE_ITEM;
    }


    @Override
    public int getItemCount() {

        int k = 0;
        for(ScoreItem item: scoreItemList)
        {
            int subItemCount = item.exchageItemList.size();
            k = k + 1 + subItemCount;
        }

        return k;
    }

    private ScoreItem getScoreItem(int position) {

        int k = 0;
        for(ScoreItem item: scoreItemList)
        {
            if(position == k)
                return item;

            int subItemCount = item.exchageItemList.size();
            k = k + 1 + subItemCount;
        }

        return null;
    }

    private ExchageItem getExchangeItem(int position) {

        int k = 0;
        for(ScoreItem item: scoreItemList)
        {
            int subItemCount = item.exchageItemList.size();

            if(position < k + 1 + subItemCount) {
                return item.exchageItemList.get(position - 1 - k);
            }

            k = k + 1 + subItemCount;
        }

        return null;
    }

    private void bindScoreView(View convertView, ScoreItem currentScoreItem) {
        View listItemView = convertView;

        listItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (item.invisibleChildren == null) {
                    item.invisibleChildren = new ArrayList<Item>();
                    int count = 0;
                    int pos = data.indexOf(itemController.refferalItem);
                    while (data.size() > pos + 1 && data.get(pos + 1).type == CHILD) {
                        item.invisibleChildren.add(data.remove(pos + 1));
                        count++;
                    }
                    notifyItemRangeRemoved(pos + 1, count);
                    itemController.btn_expand_toggle.setImageResource(R.drawable.circle_plus);
                } else {
                    int pos = data.indexOf(itemController.refferalItem);
                    int index = pos + 1;
                    for (Item i : item.invisibleChildren) {
                        data.add(index, i);
                        index++;
                    }
                    notifyItemRangeInserted(pos + 1, index - pos - 1);
                    itemController.btn_expand_toggle.setImageResource(R.drawable.circle_minus);
                    item.invisibleChildren = null;
                }*/
            }
        });

        TextView memberNameView = (TextView) listItemView.findViewById(R.id.member_name);
        memberNameView.setText(currentScoreItem.MemberName);

        TextView scoreLabelView = (TextView) listItemView.findViewById(R.id.scoreLabel);
        scoreLabelView.setText("점수");

        TextView scoreView = (TextView)listItemView.findViewById(R.id.score);
        scoreView.setText(String.valueOf(currentScoreItem.Score));

        TextView exchangeLabelView = (TextView) listItemView.findViewById(R.id.exchangeLabel);
        exchangeLabelView.setText("차감");

        TextView exchangeView = (TextView) listItemView.findViewById(R.id.exchange);
        exchangeLabelView.setText(String.valueOf(currentScoreItem.Exchange));

        TextView returnLabel = (TextView)listItemView.findViewById(R.id.returnLabel);
        returnLabel.setText("회수");

        TextView returnValueLabel = (TextView) listItemView.findViewById(R.id.returnValue);
        returnValueLabel.setText(String.valueOf(currentScoreItem.ReturnValue));

        TextView publishLabelView = (TextView) listItemView.findViewById(R.id.publishLabel);
        publishLabelView.setText("지급");

        TextView publishView = (TextView) listItemView.findViewById(R.id.publish);
        publishView.setText(String.valueOf(currentScoreItem.Publish));

        TextView totalLabel = (TextView)listItemView.findViewById(R.id.totalLabel);
        totalLabel.setText("이월");

        TextView totalView = (TextView) listItemView.findViewById(R.id.total);
        totalView.setText(String.valueOf(currentScoreItem.getRemains()));
    }

    private void bindExchageView(View convertView, ExchageItem currentScoreItem) {


    }
}
