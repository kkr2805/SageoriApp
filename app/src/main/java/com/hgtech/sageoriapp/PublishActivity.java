package com.hgtech.sageoriapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublishActivity extends AppCompatActivity
                            implements SwipeRefreshLayout.OnRefreshListener{

    private List<PublishItem> dataList;
    private PublishListAdapter listAdapter;

    private SwipeRefreshLayout swipeLayout;
    private ProgressDialog progressDialog;

    private class CallbackGetPublishItems implements Callback<List<PublishItem>> {
        @Override
        public void onResponse(Call<List<PublishItem>> call, Response<List<PublishItem>> response){
            if(response.isSuccessful()){
                dataList = response.body();
                listAdapter.clear();
                listAdapter.addAll(dataList);
                listAdapter.notifyDataSetChanged();

                Log.d("Tag", "데이터 조회 완료");
                Toast.makeText(getApplicationContext(), "데이터 조회 완료.", Toast.LENGTH_SHORT).show();
            }

            PublishActivity.this.showProgressbar(false);
        }

        @Override
        public void onFailure(Call<List<PublishItem>> call, Throwable t) {
            String strErrMessage = new String("데이터 조회 실패." );
            strErrMessage += t.getMessage();
            Toast.makeText(getApplicationContext(), strErrMessage, Toast.LENGTH_SHORT).show();
            PublishActivity.this.showProgressbar(false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_layout);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("지급관리");
        setSupportActionBar(toolbar);

        // SwipeRefreshLayout 설정
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(this);

        // ListView 설정
        ListView listView = (ListView)findViewById(R.id.listview);
        View headerView = getLayoutInflater().inflate(R.layout.publish_header, null, false);
        listView.addHeaderView(headerView);

        dataList = new ArrayList<PublishItem>();

        PublishItem item = new PublishItem();
        item.ID = 1;
        item.MachineID = 21;
        item.Credit = 300;
        item.BANK = 100;
        item.MemberID = 1;
        item.MemberName = "송효주";
        dataList.add(item);

        listAdapter = new PublishListAdapter(this, 0, dataList);
        listView.setAdapter(listAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showActionsDialog(position);
                return false;
            }
        });

        showProgressbar(true);

        // Retrofit API
        SageoriAPI api = SageoriClient.getAPI();
        Call<List<PublishItem>> callPublishItems = api.getPublishes();
        callPublishItems.enqueue(new PublishActivity.CallbackGetPublishItems());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.title_menu, menu);
        MenuItem loginItem = (MenuItem)menu.findItem(R.id.login);
        loginItem.setTitle("로그아웃");

        MenuItem newItemButton = (MenuItem)menu.findItem(R.id.new_item);
        newItemButton.setTitle("지급등록");
        newItemButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showPublishDialog(false,-1);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        return true;
    }

    private void showPublishDialog(final boolean shouldUpdate, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.publish_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(PublishActivity.this);
        alertDialogBuilderUserInput.setView(view);

        //final EditText editName = view.findViewById(R.id.editTextName);
        //final EditText editHP = view.findViewById(R.id.editTextHP);


        if(shouldUpdate) {
            //editName.setText(dataList.get(position).Name);
            //editHP.setText(dataList.get(position).HP);
        }

        TextView dialogTitle = (TextView)view.findViewById(R.id.dialogTitle);
        dialogTitle.setText(!shouldUpdate ? "지급등록" : "지급수정");

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

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //EditText nameEdit = (EditText)alertDialog.findViewById(R.id.editTextName);
                //EditText hpEdit = (EditText)alertDialog.findViewById(R.id.editTextHP);

                //String strName = nameEdit.getText().toString();
                //String strHP = hpEdit.getText().toString();

                //if(strName.length() == 0) {
                //    Toast.makeText(getApplicationContext(), "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                //    nameEdit.requestFocus();
                //    return;
                //}

                //if(strHP.length() == 0) {
                //    Toast.makeText(getApplicationContext(), "전화번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                //    hpEdit.requestFocus();
                //    return;
                //}

                HashMap<String, String> postData = new HashMap<String, String>();
                //postData.put("Name", strName);
                //postData.put("HP", strHP);
                //postData.put("Name", "김두현");
                //postData.put("HP", "010-9239-3945");

                showProgressbar(true);
                final SageoriAPI api = SageoriClient.getAPI();

                if(shouldUpdate) {
                    postData.put("ID", Integer.toString(dataList.get(position).ID));
                    Call<SageoriResult> callUpdate = api.updatePublishItem(postData);
                    callUpdate.enqueue(new Callback<SageoriResult>(){
                        @Override
                        public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                            if(response.isSuccessful() && response.body().isSuccess()){
                                Toast.makeText(getApplicationContext(), "수정되었습니다", Toast.LENGTH_SHORT).show();

                                // 리스트 최신내용으로 업데이트
                                Call<List<PublishItem>> callPublishItems = api.getPublishes();
                                callPublishItems.enqueue(new PublishActivity.CallbackGetPublishItems());

                                showProgressbar(false);
                                return;
                            } else {
                                Toast.makeText(getApplicationContext(), "지급정보 수정실패", Toast.LENGTH_SHORT).show();
                                showProgressbar(false);
                                return;
                            }
                        }

                        @Override
                        public void onFailure(Call<SageoriResult> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "지급정보 수정실패", Toast.LENGTH_SHORT).show();
                            showProgressbar(false);
                            return;
                        }
                    });
                } else {
                    Call<SageoriResult> callPost = api.createPublishItem(postData);
                    callPost.enqueue(new Callback<SageoriResult>(){
                        @Override
                        public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                            if(response.isSuccessful() && response.body().isSuccess()){
                                Toast.makeText(getApplicationContext(), "등록되었습니다", Toast.LENGTH_SHORT).show();

                                // 리스트 최신내용으로 업데이트
                                Call<List<PublishItem>> callPublishItems = api.getPublishes();
                                callPublishItems.enqueue(new PublishActivity.CallbackGetPublishItems());

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

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"수정하기", "삭제하기"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("작업을 선택하세요.");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showPublishDialog(true, position);
                } else if(which == 1) {

                    showProgressbar(true);
                    final SageoriAPI api = SageoriClient.getAPI();

                    HashMap<String, String> postData = new HashMap<String, String>();
                    postData.put("ID", Integer.toString(dataList.get(position).ID));

                    Call<SageoriResult> callDelete = api.deletePublishItem(postData);
                    callDelete.enqueue(new Callback<SageoriResult>(){
                        @Override
                        public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                            if(response.isSuccessful() && response.body().isSuccess()){
                                Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();

                                // 리스트 최신내용으로 업데이트
                                Call<List<PublishItem>> callPublishItems = api.getPublishes();
                                callPublishItems.enqueue(new PublishActivity.CallbackGetPublishItems());

                                showProgressbar(false);
                                return;
                            }
                        }

                        @Override
                        public void onFailure(Call<SageoriResult> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "회원정보 삭제실패", Toast.LENGTH_SHORT).show();
                            showProgressbar(false);
                            return;
                        }
                    });
                }
            }
        });
        builder.show();
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

    @Override
    public void onRefresh() {
        // Retrofit API

        showProgressbar(true);
        SageoriAPI api = SageoriClient.getAPI();
        Call<List<PublishItem>> callPublishItems = api.getPublishes();
        callPublishItems.enqueue(new PublishActivity.CallbackGetPublishItems());

        swipeLayout.setRefreshing(false);
    }
}
