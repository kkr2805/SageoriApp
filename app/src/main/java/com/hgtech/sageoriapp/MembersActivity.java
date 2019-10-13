package com.hgtech.sageoriapp;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.util.Log;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.Callback;
import retrofit2.Response;


public class MembersActivity extends AppCompatActivity
                            implements SwipeRefreshLayout.OnRefreshListener {

    private List<MemberItem> dataList;
    private MemberListAdapter listAdapter;

    SwipeRefreshLayout swipeLayout;

    private class CallbackGetMembers implements Callback<List<MemberItem>> {
        @Override
        public void onResponse(Call<List<MemberItem>> call, Response<List<MemberItem>> response){
            if(response.isSuccessful()){
                dataList = response.body();
                listAdapter.clear();
                listAdapter.addAll(dataList);
                listAdapter.notifyDataSetChanged();

                Log.d("Tag", "데이터 조회 완료");
                Toast.makeText(getApplicationContext(), "데이터 조회 완료.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<List<MemberItem>> call, Throwable t) {
                String strErrMessage = new String("데이터 조회 실패." );
                strErrMessage += t.getMessage();
                Toast.makeText(getApplicationContext(), strErrMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.members_layout);

        // Toolbar 설정
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("회원관리");
        setSupportActionBar(toolbar);
        
        // SwipeRefreshLayout 설정
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(this);
        
        // ListView 설정
        ListView listView = (ListView)findViewById(R.id.listview);
        dataList = new ArrayList<MemberItem>();
        listAdapter = new MemberListAdapter(this, 0, dataList);
        listView.setAdapter(listAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showActionsDialog(position);
                return false;
            }
        });


        // Retrofit API
		SageoriAPI api = SageoriClient.getAPI();
		Call<List<MemberItem>> callMembers = api.getMembers();
		callMembers.enqueue(new CallbackGetMembers());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.title_menu, menu);
        MenuItem loginItem = (MenuItem)menu.findItem(R.id.login);
        loginItem.setTitle("로그아웃");

        MenuItem newItemButton = (MenuItem)menu.findItem(R.id.new_item);
        newItemButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showMemberDialog(false,-1);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        return true;
    }

    private void showMemberDialog(final boolean shouldUpdate, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.member_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MembersActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText editName = view.findViewById(R.id.editTextName);
        final EditText editHP = view.findViewById(R.id.editTextHP);

        editHP.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

        if(shouldUpdate) {
            editName.setText(dataList.get(position).Name);
            editHP.setText(dataList.get(position).HP);
        }
        
        TextView dialogTitle = (TextView)view.findViewById(R.id.dialogTitle);
        dialogTitle.setText(!shouldUpdate ? "회원등록" : "회원수정");

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

                    EditText nameEdit = (EditText)alertDialog.findViewById(R.id.editTextName);
                    EditText hpEdit = (EditText)alertDialog.findViewById(R.id.editTextHP);

                    String strName = nameEdit.getText().toString();
                    String strHP = hpEdit.getText().toString();

                    if(strName.length() == 0) {
                        Toast.makeText(getApplicationContext(), "이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                        nameEdit.requestFocus();
                        return;
                    }

                    if(strHP.length() == 0) {
                        Toast.makeText(getApplicationContext(), "전화번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                        hpEdit.requestFocus();
                        return;
                    }
                    
                    HashMap<String, String> postData = new HashMap<String, String>();
                    postData.put("Name", strName);
                    postData.put("HP", strHP);
                    //postData.put("Name", "김두현");
                    //postData.put("HP", "010-9239-3945");

                    final SageoriAPI api = SageoriClient.getAPI();

                    if(shouldUpdate) {
                        postData.put("ID", Integer.toString(dataList.get(position).ID));
                        Call<SageoriResult> callUpdate = api.updateMember(postData);
                        callUpdate.enqueue(new Callback<SageoriResult>(){
                            @Override
                            public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                                if(response.isSuccessful() && response.body().isSuccess()){
                                    Toast.makeText(getApplicationContext(), "수정되었습니다", Toast.LENGTH_SHORT).show();

                                    // 리스트 최신내용으로 업데이트
                                    Call<List<MemberItem>> callMembers = api.getMembers();
                                    callMembers.enqueue(new CallbackGetMembers());

                                    return;
                                } else {
                                    Toast.makeText(getApplicationContext(), "회원정보 수정실패", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            @Override
                            public void onFailure(Call<SageoriResult> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "회원정보 수정실패", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });
                    } else {
                        Call<SageoriResult> callPost = api.createMember(postData);
                        callPost.enqueue(new Callback<SageoriResult>(){
                            @Override
                            public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                                if(response.isSuccessful() && response.body().isSuccess()){
                                    Toast.makeText(getApplicationContext(), "등록되었습니다", Toast.LENGTH_SHORT).show();

                                    // 리스트 최신내용으로 업데이트
                                    Call<List<MemberItem>> callMembers = api.getMembers();
                                    callMembers.enqueue(new CallbackGetMembers());

                                    return;
                                }
                            }

                            @Override
                            public void onFailure(Call<SageoriResult> call, Throwable t) {
                                Toast.makeText(getApplicationContext(), "회원정보 등록실패", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        });
                    }

                    alertDialog.dismiss();
            }
        });
    }

    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"수정하기", "삭제하기", "전화걸기"};
 
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("작업을 선택하세요.");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showMemberDialog(true, position);
                } else if(which == 1) {

                    final SageoriAPI api = SageoriClient.getAPI();

                    HashMap<String, String> postData = new HashMap<String, String>();
                    postData.put("ID", Integer.toString(dataList.get(position).ID));

                    Call<SageoriResult> callDelete = api.deleteMember(postData);
                    callDelete.enqueue(new Callback<SageoriResult>(){
                        @Override
                        public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                            if(response.isSuccessful() && response.body().isSuccess()){
                                Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();

                                // 리스트 최신내용으로 업데이트
                                Call<List<MemberItem>> callMembers = api.getMembers();
                                callMembers.enqueue(new CallbackGetMembers());

                                return;
                            }
                        }

                        @Override
                        public void onFailure(Call<SageoriResult> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "회원정보 삭제실패", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRefresh() {
        // Retrofit API
		SageoriAPI api = SageoriClient.getAPI();
		Call<List<MemberItem>> callMembers = api.getMembers();
		callMembers.enqueue(new CallbackGetMembers());

        swipeLayout.setRefreshing(false);
    }
}
