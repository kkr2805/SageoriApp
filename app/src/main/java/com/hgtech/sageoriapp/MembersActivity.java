package com.hgtech.sageoriapp;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.gson.*;

public class MembersActivity extends AppCompatActivity {

    private List<MemberItem> dataList;
    private MemberListAdapter listAdapter;

    private class CallbackGetMembers implements Callback<List<MemberItem>> {
        @Override
        public void onResponse(Call<List<MemberItem>> call, Response<List<MemberItem>> response){
            if(response.isSuccessful()){
                dataList = response.body();
                listAdapter.clear();
                listAdapter.addAll(dataList);
                listAdapter.notifyDataSetChanged();

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


        //String[] arrName = {"송효주", "김두현", "송지현"};
        //String[] arrPhone = {"010-9278-2806", "010-2030-2805", "010-8609-2806"};

        //ArrayList<MemberItem> dataList = new ArrayList();
        //for(int k = 0; k < 30; k++){
        //    MemberItem member = new MemberItem();

        //    member.No = k + 1;
        //    member.Name = arrName[k % arrName.length];
        //    member.HP = arrPhone[k % arrPhone.length];
        //    
        //    dataList.add(member);
        //}
        
        ListView listView = (ListView)findViewById(R.id.listview);
        dataList = new ArrayList<MemberItem>();
        listAdapter = new MemberListAdapter(this, 0, dataList);
        listView.setAdapter(listAdapter);

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new GsonDateFormatAdapter());

		Retrofit client = new Retrofit.Builder()
									.baseUrl("http://192.168.1.26:3000/")		
									.addConverterFactory(GsonConverterFactory.create(builder.create()))
									.build();
		SageoriAPI api = client.create(SageoriAPI.class);
		Call<List<MemberItem>> callMembers = api.getMembers();
		callMembers.enqueue(new CallbackGetMembers());

        //ListView listView = (ListView)findViewById(R.id.listview);
        //MemberListAdapter adapter = new MemberListAdapter(this, 0, dataList);
        //listView.setAdapter(adapter);

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
                showNoteDialog(false,-1);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        return true;
    }

    private void showNoteDialog(final boolean shouldUpdate, /*final Note note,*/ final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.member_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MembersActivity.this);
        alertDialogBuilderUserInput.setView(view);

        //final EditText inputNote = view.findViewById(R.id.note);
       // TextView dialogTitle = view.findViewById(R.id.dialog_title);
        //dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));

       // if (shouldUpdate && note != null) {
       //     inputNote.setText(note.getNote());
       // }
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

                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeAdapter(Date.class, new GsonDateFormatAdapter());

                    Retrofit client = new Retrofit.Builder()
                                                .baseUrl("http://192.168.1.26:3000/")		
                                                .addConverterFactory(GsonConverterFactory.create(builder.create()))
                                                .build();
                    final SageoriAPI api = client.create(SageoriAPI.class);
                    Call<SageoriResult> callPost = api.createMember(postData);
                    callPost.enqueue(new Callback<SageoriResult>(){
                        @Override
                        public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                            if(response.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "등록되었습니다.", Toast.LENGTH_SHORT).show();

                                // 리스트 최신내용으로 업데이트
                                Call<List<MemberItem>> callMembers = api.getMembers();
                                callMembers.enqueue(new CallbackGetMembers());
                                
                                return;
                            }
                        }

                        @Override
                        public void onFailure(Call<SageoriResult> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "등록실패", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    });

                    alertDialog.dismiss();
            }
        });
    }
}
