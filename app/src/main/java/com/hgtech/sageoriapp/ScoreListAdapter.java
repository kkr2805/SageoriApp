package com.hgtech.sageoriapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ScoreListAdapter extends ArrayAdapter<ScoreItem> {

    List<ScoreItem> scoreItemList;

    public ScoreListAdapter(Context context, int resource, List<ScoreItem> objects) {
        super(context, resource, objects);

        scoreItemList = objects;
    }

    @NotNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        ScoreItem currentScoreItem = scoreItemList.get(position);

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.score_item, parent, false);
        }

        TextView memberNameView = (TextView) listItemView.findViewById(R.id.member_name);
        memberNameView.setText(currentScoreItem.MemberName);

        TextView scoreLabelView = (TextView) listItemView.findViewById(R.id.scoreLabel);
        scoreLabelView.setText("점수");

        TextView scoreView = (TextView)listItemView.findViewById(R.id.score);
        scoreView.setText(NumberFormatter.getNumberString(currentScoreItem.Score));

        TextView exchangeLabelView = (TextView) listItemView.findViewById(R.id.exchangeLabel);
        exchangeLabelView.setText("차감");

        TextView exchangeView = (TextView) listItemView.findViewById(R.id.exchange);
        exchangeLabelView.setText(NumberFormatter.getNumberString((currentScoreItem.Exchange)));

        TextView returnLabel = (TextView)listItemView.findViewById(R.id.returnLabel);
        returnLabel.setText("회수");

        TextView returnValueLabel = (TextView) listItemView.findViewById(R.id.returnValue);
        returnValueLabel.setText(NumberFormatter.getNumberString(currentScoreItem.ReturnValue));

        TextView publishLabelView = (TextView) listItemView.findViewById(R.id.publishLabel);
        publishLabelView.setText("지급");

        TextView publishView = (TextView) listItemView.findViewById(R.id.publish);
        publishView.setText(NumberFormatter.getNumberString(currentScoreItem.Publish));

        TextView totalLabel = (TextView)listItemView.findViewById(R.id.totalLabel);
        totalLabel.setText("이월");

        TextView totalView = (TextView) listItemView.findViewById(R.id.total);
        totalView.setText(NumberFormatter.getNumberString(currentScoreItem.getRemains()));

        return listItemView;
    }

}
