package com.hgtech.sageoriapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ExpandableScoreListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private int type;

        private TextView nameTextView;

        public ViewHolder(View itemView, int type) {
            super(itemView);

            this.type = type;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        private void bindView(final ScoreItem currentScoreItem){

            final View listItemView = this.itemView;

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

            listItemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                Log.d("Tag", "ViewHolder setOnClickListener");
                int exchangeListCount = 0;
                int position = ExpandableScoreListAdapter.this.getPosition(currentScoreItem);

                if(currentScoreItem.exchageItemList != null)
                    exchangeListCount = currentScoreItem.exchageItemList.size();

                if(exchangeListCount > 0) {

                    currentScoreItem.exchageItemList = null;
                    ExpandableScoreListAdapter.this.notifyItemRangeRemoved(position + 1, exchangeListCount);

                } else {

                    showExchangeList(listItemView.getContext(), currentScoreItem, position);

                }


                }
            });

            listItemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {

                    showActionsDialog(listItemView.getContext(), currentScoreItem);
                    return false;

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
            exchangeView.setText(String.valueOf(currentScoreItem.Exchange));

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

        private void bindView(final ExchageItem currentExchangeItem){

            final View itemView = this.itemView;

            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {

                    showActionsDialog(itemView.getContext(), currentExchangeItem);
                    return false;

                }
            });

            TextView dateView = (TextView) itemView.findViewById(R.id.createdDate);
            DateFormat format = new SimpleDateFormat("(YY.MkM.dd HH시 mm분)", Locale.KOREA);
            dateView.setText(format.format(currentExchangeItem.Date));

            TextView labelView = (TextView) itemView.findViewById(R.id.exchangeLabel);
            labelView.setText("차감");

            TextView totalView = (TextView) itemView.findViewById(R.id.exchange);
            totalView.setText(String.valueOf(currentExchangeItem.ExchageValue));

        }

        @Override
        public void onClick(View view) {
            // Context context = view.getContext();
            // article.getName()
        }

        @Override
        public boolean onLongClick(View view) {
            // Handle long click
            // Return true to indicate the click was handled
            return true;
        }
    }

    private class CallbackGetExchanges implements Callback<List<ExchageItem>> {

        private Context context;
        private boolean bShouldUpdate;
        private ScoreItem scoreItem;
        private int position;

        CallbackGetExchanges(boolean bShouldUpdate, ScoreItem scoreItem, int position) {
            this.bShouldUpdate = bShouldUpdate;
            this.scoreItem = scoreItem;
            this.position = position;
        }

        @Override
        public void onResponse(Call<List<ExchageItem>> call, Response<List<ExchageItem>> response){
            Log.d("Tag", "onResponse");

            if(response.isSuccessful()){
                Log.d("Tag", "onResponse success");
                List<ExchageItem> exchageItemList = response.body();

                scoreItem.exchageItemList = exchageItemList;
                if(exchageItemList != null && exchageItemList.size() > 0) {
                    Log.d("Tag", "onResponse exchage list size: " + Integer.toString(exchageItemList.size()));
                    Log.d("Tag", "onResponse notifyItemRangeInserted: " + Integer.toString(position + 1) + ", " +
                            Integer.toString(position + 1 + exchageItemList.size()));
                    ExpandableScoreListAdapter.this.notifyItemRangeInserted(position + 1, exchageItemList.size());
                    //ExpandableScoreListAdapter.this.notifyDataSetChanged();
                }


                Log.d("Tag", "데이터 조회 완료");
                //Toast.makeText(context, "데이터 조회 완료.", Toast.LENGTH_SHORT).show();
            }

            Log.d("Tag", "onResponse end");
            ExpandableScoreListAdapter.this.showProgressbar(context,false);
        }

        @Override
        public void onFailure(Call<List<ExchageItem>> call, Throwable t) {
            Log.d("Tag", "onFailure");

            String strErrMessage = new String("데이터 조회 실패." );
            strErrMessage += t.getMessage();
            //Toast.makeText(context, strErrMessage, Toast.LENGTH_SHORT).show();
            ExpandableScoreListAdapter.this.showProgressbar(context,false);
        }
    }

    private class CallbackGetMembers implements Callback<List<MemberItem>> {

        private Context context;
        private boolean bShouldUpdate;
        private int memberID;

        CallbackGetMembers(Context context) {
            this.context = context;
            this.bShouldUpdate = false;
            this.memberID = -1;
        }

        CallbackGetMembers(Context context, boolean bShouldUpdate, int memberID) {
            this.context = context;
            this.bShouldUpdate = bShouldUpdate;
            this.memberID = memberID;
        }

        @Override
        public void onResponse(Call<List<MemberItem>> call, Response<List<MemberItem>> response){
            if(response.isSuccessful()){
                memberList = response.body();

                if(memberSpinner != null && memberSpinnerAdapter != null)
                {
                    memberSpinnerAdapter.clear();
                    memberSpinnerAdapter.addAll(memberList);
                    memberSpinnerAdapter.notifyDataSetChanged();

                    if(this.bShouldUpdate == true) {
                        MemberItem item = new MemberItem();
                        item.ID = this.memberID;
                        int index = memberList.indexOf(item);
                        memberSpinner.setSelection(index);
                    }
                }
                Log.d("Tag", "데이터 조회 완료");
            }

            ExpandableScoreListAdapter.this.showProgressbar(context,false);
        }

        @Override
        public void onFailure(Call<List<MemberItem>> call, Throwable t) {
            String strErrMessage = new String("데이터 조회 실패." );
            strErrMessage += t.getMessage();
            ExpandableScoreListAdapter.this.showProgressbar(context,false);
        }
    }

    static final int SCORE_ITEM = 0;
    static final int EXCHANGE_ITEM = 1;

    private List<ScoreItem> scoreItemList;

    private Spinner memberSpinner;
    private List<MemberItem> memberList;
    private MemberSpinnerAdapter memberSpinnerAdapter;

    private ProgressDialog progressDialog;

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

        Log.d("Tag", "onCreateViewHolder " + Integer.toString(type));

        switch (type) {
            case SCORE_ITEM:
                view = inflater.inflate(R.layout.score_item, parent, false);
                return new ViewHolder(view, type);

            case EXCHANGE_ITEM:
                view = inflater.inflate(R.layout.exchange_item, parent, false);
                return new ViewHolder(view, type);
        }
        return null;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int type = getItemViewType(position);
        ViewHolder viewHolder = (ViewHolder)holder;

        switch (type) {
            case SCORE_ITEM:
                ScoreItem scoreItem = getScoreItem(position);
                viewHolder.bindView(scoreItem);

                Log.d("Tag", "onBindViewHoler " + Integer.toString(position));

                break;
            case EXCHANGE_ITEM:
                ExchageItem exchageItem = getExchangeItem(position);
                viewHolder.bindView(exchageItem);

                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d("Tag", "getItemViewType position: " + Integer.toString(position));

        int k = 0;
        for(ScoreItem item: scoreItemList)
        {
            if(k == position)
                return SCORE_ITEM;

            int subItemCount = 0;

            if(item.exchageItemList != null)
                subItemCount = item.exchageItemList.size();

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
            int subItemCount = 0;

            if(item.exchageItemList != null)
                subItemCount = item.exchageItemList.size();
            k = k + 1 + subItemCount;
        }

        Log.d("Tag", "getItemCount count: " + Integer.toString(k));

        return k;
    }

    private ScoreItem getScoreItem(int position) {

        int k = 0;
        for(ScoreItem item: scoreItemList)
        {
            if(position == k)
                return item;

            int subItemCount = 0;

            if(item.exchageItemList != null)
                subItemCount = item.exchageItemList.size();

            k = k + 1 + subItemCount;
        }

        return null;
    }

    private ExchageItem getExchangeItem(int position) {

        int k = 0;
        for(ScoreItem item: scoreItemList)
        {
            int subItemCount = 0;

            if(item.exchageItemList != null) {
                subItemCount = item.exchageItemList.size();

                if(position < k + 1 + subItemCount) {
                    return item.exchageItemList.get(position - 1 - k);
                }
            }

            k = k + 1 + subItemCount;
        }

        return null;
    }

    private int getPosition(ScoreItem scoreItem) {

        int k = 0;
        for(ScoreItem item: scoreItemList)
        {
            int subItemCount = 0;

            if(item.exchageItemList != null) {
                subItemCount = item.exchageItemList.size();
            }

            if(item == scoreItem)
                return k;

            k = k + 1 + subItemCount;
        }

        return -1;
    }

    private int getPosition(ExchageItem exchageItem) {

        int k = 0;
        for(ScoreItem item: scoreItemList)
        {
            int subItemCount = 0;

            if(item.exchageItemList != null) {
                subItemCount = item.exchageItemList.size();

                for (ExchageItem subItem : item.exchageItemList) {
                    if(subItem == exchageItem)
                        return k;

                    k++;
                }

            }

            k++;
        }

        return -1;
    }

    private ScoreItem getParentScoreItem(ExchageItem exchageItem) {

        int n = 0;
        int k = 0;
        for(ScoreItem item: scoreItemList)
        {
            int subItemCount = 0;

            if(item.exchageItemList != null) {
                subItemCount = item.exchageItemList.size();

                for (ExchageItem subItem : item.exchageItemList) {
                    if(subItem == exchageItem)
                        return item;

                    k++;
                }

            }

            k++;
            n = n + 1 + subItemCount;
        }

        return null;
    }

    public void showProgressbar(Context context, boolean bShow) {
        if(bShow) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("잠시만 기다리세요.");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setProgress(0);
            progressDialog.setMax(100);
            progressDialog.show();

        } else {
            if(progressDialog != null)
                progressDialog.dismiss();
        }
    }

    private void showExchangeList(final Context context, ScoreItem scoreItem, int position){

        Log.d("Tag", "showExchangeList");

        SageoriAPI api = SageoriClient.getAPI();
        Call<List<ExchageItem>> callExchanges = api.getExchageItems(scoreItem.MemberID);
        CallbackGetExchanges callbackExchanges;
        //if(shouldUpdate) {
        //    callbackMembers = new ExpandableScoreListAdapter.CallbackGetMembers(true, exchageItem.MemberID);
        //}else{
        callbackExchanges = new ExpandableScoreListAdapter.CallbackGetExchanges(false, scoreItem, position);
        //}
        callExchanges.enqueue(callbackExchanges);

    }

    private void showActionsDialog(final Context context, final ScoreItem scoreItem) {
        CharSequence actionMenu[] = new CharSequence[]{ "차감등록하기" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("작업을 선택하세요.");

        builder.setItems(actionMenu, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {

                showInsertExchangeDialog(context, scoreItem);

            }

        });

        builder.show();
    }

    private void showActionsDialog(final Context context, final ExchageItem exchageItem) {
        CharSequence actionMenu[] = new CharSequence[]{ "차감수정하기", "차감삭제하기" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("작업을 선택하세요.");

        builder.setItems(actionMenu, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){

                    showUpdateExchangeDialog(context, exchageItem);

                } else if (which == 1) {

                    showProgressbar( context, true);
                    final SageoriAPI api = SageoriClient.getAPI();

                    HashMap<String, String> postData = new HashMap<String, String>();
                    postData.put("ID", Integer.toString(exchageItem.ID));

                    Call<SageoriResult> callDelete = api.deleteExchangeItem(postData);
                    callDelete.enqueue(new Callback<SageoriResult>(){
                        @Override
                        public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                            if(response.isSuccessful() && response.body().isSuccess()){
                                Toast.makeText(context, "삭제되었습니다", Toast.LENGTH_SHORT).show();

                                int position = getPosition(exchageItem);
                                ScoreItem parent = getParentScoreItem(exchageItem);
                                int parentPosition = getPosition(parent);

                                int subposition = 0;
                                for (ExchageItem item: parent.exchageItemList){
                                    if(item == exchageItem)
                                        break;

                                    subposition++;
                                }

                                parent.exchageItemList.remove(subposition);
                                ExpandableScoreListAdapter.this.notifyItemRemoved(position);
                                ExpandableScoreListAdapter.this.notifyItemChanged(parentPosition);
                                ExpandableScoreListAdapter.this.notifyDataSetChanged();

                                showProgressbar(context, false);

                                return;
                            }
                        }

                        @Override
                        public void onFailure(Call<SageoriResult> call, Throwable t) {
                            Toast.makeText(context, "회수정보 삭제실패", Toast.LENGTH_SHORT).show();
                            showProgressbar(context, false);
                            return;
                        }
                    });

                }


            }

        });

        builder.show();
    }

    private void showInsertExchangeDialog(final Context context, final ScoreItem scoreItem) {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View view = layoutInflaterAndroid.inflate(R.layout.exchange_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(view);

        // member spinner
        memberSpinner = (Spinner)view.findViewById(R.id.spinnerMember);
        memberList = new ArrayList<>();
        memberSpinnerAdapter = new MemberSpinnerAdapter(view.getContext(), android.R.layout.simple_spinner_item, memberList);
        memberSpinner.setAdapter(memberSpinnerAdapter);

        EditText editExchange = (EditText)view.findViewById(R.id.editTextExchange);

        TextView dialogTitle = (TextView)view.findViewById(R.id.dialogTitle);
        dialogTitle.setText("차감등록");

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("저장", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogBox, int id) {
                    }
                })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        // Retrofit API
        SageoriAPI api = SageoriClient.getAPI();
        Call<List<MemberItem>> callMembers = api.getMembers();
        ExpandableScoreListAdapter.CallbackGetMembers callbackMembers;
        callbackMembers = new ExpandableScoreListAdapter.CallbackGetMembers(context, true, scoreItem.MemberID);
        callMembers.enqueue(callbackMembers);

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editExchange = (EditText)alertDialog.findViewById(R.id.editTextExchange);

                String strExchange = editExchange.getText().toString();

                if(memberSpinner.getSelectedItemPosition() < 0) {
                    Toast.makeText(context, "회원이름을 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(strExchange.length() == 0) {
                    Toast.makeText(context, "회수를 입력하세요.", Toast.LENGTH_SHORT).show();
                    editExchange.requestFocus();
                    return;
                }

                int memberSpinnerPos = memberSpinner.getSelectedItemPosition();

                HashMap<String, String> postData = new HashMap<String, String>();

                String memberID = Integer.toString(memberList.get(memberSpinnerPos).ID);

                postData.put("MemberID", memberID);
                postData.put("Exchange", strExchange);

                showProgressbar(context, true);
                final SageoriAPI api = SageoriClient.getAPI();


                Call<SageoriResult> callPost = api.createExchageItem(postData);
                callPost.enqueue(new Callback<SageoriResult>(){
                    @Override
                    public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                        if(response.isSuccessful() && response.body().isSuccess()){
                            Toast.makeText(context, "등록되었습니다", Toast.LENGTH_SHORT).show();

                            int position = getPosition(scoreItem);

                            int exchangeListCount = 0;
                            if(scoreItem.exchageItemList != null)
                                exchangeListCount = scoreItem.exchageItemList.size();

                            scoreItem.exchageItemList = null;

                            if(exchangeListCount > 0)
                                ExpandableScoreListAdapter.this.notifyItemRangeRemoved(position + 1, exchangeListCount);

                            showExchangeList(context, scoreItem, position);

                            showProgressbar(context,false);
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<SageoriResult> call, Throwable t) {
                        Toast.makeText(context, "지급정보 등록실패", Toast.LENGTH_SHORT).show();
                        showProgressbar(context,false);
                        return;
                    }
                });


                alertDialog.dismiss();
            }
        });

    }

    private void showUpdateExchangeDialog(final Context context, final ExchageItem exchageItem) {

        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);
        View view = layoutInflaterAndroid.inflate(R.layout.exchange_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(context);
        alertDialogBuilderUserInput.setView(view);

        // member spinner
        memberSpinner = (Spinner)view.findViewById(R.id.spinnerMember);
        memberList = new ArrayList<>();
        memberSpinnerAdapter = new MemberSpinnerAdapter(view.getContext(), android.R.layout.simple_spinner_item, memberList);
        memberSpinner.setAdapter(memberSpinnerAdapter);

        EditText editExchange = (EditText)view.findViewById(R.id.editTextExchange);

        editExchange.setText(Integer.toString(exchageItem.ExchageValue));

        TextView dialogTitle = (TextView)view.findViewById(R.id.dialogTitle);
        dialogTitle.setText("차감수정");

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogBox, int id) {
                    }
                })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        // Retrofit API
        SageoriAPI api = SageoriClient.getAPI();
        Call<List<MemberItem>> callMembers = api.getMembers();
        ExpandableScoreListAdapter.CallbackGetMembers callbackMembers;
        callbackMembers = new ExpandableScoreListAdapter.CallbackGetMembers(context, true, exchageItem.MemberID);
        callMembers.enqueue(callbackMembers);

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editExchange = (EditText)alertDialog.findViewById(R.id.editTextExchange);

                final String strExchange = editExchange.getText().toString();

                if(memberSpinner.getSelectedItemPosition() < 0) {
                    Toast.makeText(context, "회원이름을 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(strExchange.length() == 0) {
                    Toast.makeText(context, "회수를 입력하세요.", Toast.LENGTH_SHORT).show();
                    editExchange.requestFocus();
                    return;
                }

                int memberSpinnerPos = memberSpinner.getSelectedItemPosition();

                HashMap<String, String> postData = new HashMap<String, String>();

                String memberID = Integer.toString(memberList.get(memberSpinnerPos).ID);

                postData.put("ID", Integer.toString(exchageItem.ID));
                postData.put("MemberID", memberID);
                postData.put("Exchange", strExchange);

                showProgressbar(context, true);
                final SageoriAPI api = SageoriClient.getAPI();

                String ID = Integer.toString(exchageItem.ID);
                postData.put("ID", ID);
                Call<SageoriResult> callUpdate = api.updateExchageItem(postData);
                callUpdate.enqueue(new Callback<SageoriResult>(){
                    @Override
                    public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                        if(response.isSuccessful() && response.body().isSuccess()){
                            Toast.makeText(context, "수정되었습니다", Toast.LENGTH_SHORT).show();

                            exchageItem.ExchageValue = Integer.parseInt(strExchange);
                            int position = getPosition(exchageItem);

                            ScoreItem parent = getParentScoreItem(exchageItem);
                            int parentPosition = getPosition(parent);

                            ExpandableScoreListAdapter.this.notifyItemChanged(position);
                            ExpandableScoreListAdapter.this.notifyItemChanged(parentPosition);
                            ExpandableScoreListAdapter.this.notifyDataSetChanged();

                            showProgressbar(context,false);
                            return;
                        } else {
                            Toast.makeText(context, "지급정보 수정실패", Toast.LENGTH_SHORT).show();
                            showProgressbar(context,false);
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<SageoriResult> call, Throwable t) {
                        Toast.makeText(context, "지급정보 수정실패", Toast.LENGTH_SHORT).show();
                        showProgressbar(context, false);
                        return;
                    }
                });


                alertDialog.dismiss();
            }
        });
    }
}
