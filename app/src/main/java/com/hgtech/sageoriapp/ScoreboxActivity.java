package com.hgtech.sageoriapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScoreboxActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView listView;
    private List<ScoreItem> dataList;
    private ExpandableScoreListAdapter listAdapter;

    private Spinner memberSpinner;
    private List<MemberItem> memberList;
    private MemberSpinnerAdapter memberSpinnerAdapter;

    private SwipeRefreshLayout swipeLayout;
    private ProgressDialog progressDialog;

    private EditText passwordEditText;

    private class CallbackGetScoreItems implements Callback<List<ScoreItem>> {
        @Override
        public void onResponse(Call<List<ScoreItem>> call, Response<List<ScoreItem>> response){
            if(response.isSuccessful()){
                dataList = response.body();
                listAdapter = new ExpandableScoreListAdapter(dataList);
                listView.setAdapter(listAdapter);
                listAdapter.notifyDataSetChanged();

                Log.d("Tag", "데이터 조회 완료 " + Integer.toString(dataList.size()));
                Toast.makeText(getApplicationContext(), "데이터 조회 완료.", Toast.LENGTH_SHORT).show();
            }

            ScoreboxActivity.this.showProgressbar(false);
        }

        @Override
        public void onFailure(Call<List<ScoreItem>> call, Throwable t) {
            String strErrMessage = new String("데이터 조회 실패." );
            strErrMessage += t.getMessage();
            Toast.makeText(getApplicationContext(), strErrMessage, Toast.LENGTH_SHORT).show();
            ScoreboxActivity.this.showProgressbar(false);
        }
    }

    private class CallbackGetMembers implements Callback<List<MemberItem>> {

        private boolean bShouldUpdate;
        private int memberID;

        CallbackGetMembers() {
            this.bShouldUpdate = false;
            this.memberID = -1;
        }

        CallbackGetMembers(boolean bShouldUpdate, int memberID) {
            this.bShouldUpdate = bShouldUpdate;
            this.memberID = memberID;
        }

        @Override
        public void onResponse(Call<List<MemberItem>> call, Response<List<MemberItem>> response){
            if(response.isSuccessful()){
                memberList = response.body();

                memberSpinnerAdapter.clear();
                memberSpinnerAdapter.addAll(memberList);
                memberSpinnerAdapter.notifyDataSetChanged();

                if(this.bShouldUpdate == true) {
                    MemberItem item = new MemberItem();
                    item.ID = this.memberID;
                    int index = memberList.indexOf(item);
                    memberSpinner.setSelection(index);
                }

                Log.d("Tag", "데이터 조회 완료");
                Toast.makeText(getApplicationContext(), "데이터 조회 완료.", Toast.LENGTH_SHORT).show();
            }

            ScoreboxActivity.this.showProgressbar(false);
        }

        @Override
        public void onFailure(Call<List<MemberItem>> call, Throwable t) {
            String strErrMessage = new String("데이터 조회 실패." );
            strErrMessage += t.getMessage();
            Toast.makeText(getApplicationContext(), strErrMessage, Toast.LENGTH_SHORT).show();
            ScoreboxActivity.this.showProgressbar(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scorebox_layout);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("점수관리");
        setSupportActionBar(toolbar);

        // SwipeRefreshLayout 설정
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(this);

        // ListView 설정
        listView = (RecyclerView)findViewById(R.id.listview);
        listView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        dataList = new ArrayList<ScoreItem>();

        listAdapter = new ExpandableScoreListAdapter(dataList);
        listView.setAdapter(listAdapter);
        /*listView.setOnLongClickListener(new AdapterView.OnLongClickListener(){
            @Override
            public boolean onLongClick(View view) {
                showActionsDialog(position);
                return false;
            }
        });*/

        showProgressbar(true);

        // Retrofit API
        SageoriAPI api = SageoriClient.getAPI();
        Call<List<ScoreItem>> callPublishItems = api.getScoreItems();
        callPublishItems.enqueue(new ScoreboxActivity.CallbackGetScoreItems());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.scorebox_menu, menu);
        MenuItem loginItem = (MenuItem)menu.findItem(R.id.login);
        loginItem.setTitle("로그아웃");

        MenuItem newItemButton = (MenuItem)menu.findItem(R.id.new_item);
        newItemButton.setTitle("차감등록");
        newItemButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showConfirmAdminDialog();
                return false;
            }
        });

        //MenuItem searchDateButton = (MenuItem)menu.findItem(R.id.search);
        //searchDateButton.setTitle("날짜검색");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return true;
    }

    private void showConfirmAdminDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.confirm_admin_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(ScoreboxActivity.this);
        alertDialogBuilderUserInput.setView(view);

        passwordEditText = (EditText)view.findViewById(R.id.editTextPassword);

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
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



        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strPassword = passwordEditText.getText().toString();

                if(strPassword.isEmpty()){
                    Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                HashMap<String, String> postData = new HashMap<>();
                postData.put("password", strPassword);

                // Retrofit API
                SageoriAPI api = SageoriClient.getAPI();
                Call<SageoriResult> callConfirmAdmin = api.confirmAdmin(postData);
                callConfirmAdmin.enqueue(new Callback<SageoriResult>(){
                    @Override
                    public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                        if(response.isSuccessful() && response.body().isSuccess()){
                            showExchangeDialog(false, -1);
                            return;
                        }else{

                            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<SageoriResult> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        showProgressbar(false);
                        return;
                    }
                });
                alertDialog.dismiss();
            }
        });
    }

    private void showExchangeDialog(final boolean shouldUpdate, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.exchange_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(ScoreboxActivity.this);
        alertDialogBuilderUserInput.setView(view);

        // member spinner
        memberSpinner = (Spinner)view.findViewById(R.id.spinnerMember);
        memberList = new ArrayList<>();
        memberSpinnerAdapter = new MemberSpinnerAdapter(view.getContext(), android.R.layout.simple_spinner_item, memberList);
        memberSpinner.setAdapter(memberSpinnerAdapter);

        EditText editExchage = (EditText)view.findViewById(R.id.editTextExchange);

        if(shouldUpdate) {
            editExchage.setText(Integer.toString(dataList.get(position).Exchange));
        }

        TextView dialogTitle = (TextView)view.findViewById(R.id.dialogTitle);
        dialogTitle.setText(!shouldUpdate ? "차감등록" : "차감수정");

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "수정" : "저장", new DialogInterface.OnClickListener() {
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
        ScoreboxActivity.CallbackGetMembers callbackMembers;
        if(shouldUpdate) {
            callbackMembers = new ScoreboxActivity.CallbackGetMembers(true, dataList.get(position).MemberID);
        }else{
            callbackMembers = new ScoreboxActivity.CallbackGetMembers();
        }
        callMembers.enqueue(callbackMembers);

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText exchangeEdit = (EditText)alertDialog.findViewById(R.id.editTextExchange);

                String strExchange = exchangeEdit.getText().toString();

                if(memberSpinner.getSelectedItemPosition() < 0) {
                    Toast.makeText(getApplicationContext(), "회원이름을 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(strExchange.length() == 0) {
                    Toast.makeText(getApplicationContext(), "차감을 입력하세요.", Toast.LENGTH_SHORT).show();
                    exchangeEdit.requestFocus();
                    return;
                }

                int memberSpinnerPos = memberSpinner.getSelectedItemPosition();

                HashMap<String, String> postData = new HashMap<String, String>();

                String memberID = Integer.toString(memberList.get(memberSpinnerPos).ID);
                String exchage = strExchange;

                postData.put("MemberID", memberID);
                postData.put("Exchange", exchage);


                showProgressbar(true);
                final SageoriAPI api = SageoriClient.getAPI();

                if(shouldUpdate) {

                } else {
                    Call<SageoriResult> callPost = api.createExchageItem(postData);
                    callPost.enqueue(new Callback<SageoriResult>(){
                        @Override
                        public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                            if(response.isSuccessful() && response.body().isSuccess()){
                                Toast.makeText(getApplicationContext(), "등록되었습니다", Toast.LENGTH_SHORT).show();

                                // 리스트 최신내용으로 업데이트
                                Call<List<ScoreItem>> callScoreItems = api.getScoreItems();
                                callScoreItems.enqueue(new ScoreboxActivity.CallbackGetScoreItems());

                                showProgressbar(false);
                                return;
                            }
                        }

                        @Override
                        public void onFailure(Call<SageoriResult> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "지급정보 등록실패", Toast.LENGTH_SHORT).show();
                            showProgressbar(false);
                            return;
                        }
                    });
                }

                alertDialog.dismiss();
            }
        });
    }



    @Override
    public void onRefresh() {
        // Retrofit API

        showProgressbar(true);
        SageoriAPI api = SageoriClient.getAPI();
        Call<List<ScoreItem>> callScoreItems = api.getScoreItems();
        callScoreItems.enqueue(new ScoreboxActivity.CallbackGetScoreItems());

        swipeLayout.setRefreshing(false);
    }

    public void showProgressbar(boolean bShow) {
        if(bShow) {
            progressDialog = new ProgressDialog(this);
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

    public void showActionsDialog(int position) {
        CharSequence actionMenu[] = new CharSequence[]{"차감등록하기"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("작업을 선택하세요.");
        builder.setItems(actionMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 1) {
                    //showExchangeDialog(position);
                }
            }
        });
        builder.show();
    }
}
