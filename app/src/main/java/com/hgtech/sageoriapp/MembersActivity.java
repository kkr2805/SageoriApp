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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.gson.*;

public class MembersActivity extends AppCompatActivity {

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
        
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Date.class, new GsonDateFormatAdapter());

		Retrofit client = new Retrofit.Builder()
									.baseUrl("http://192.168.1.26:3000/")		
									.addConverterFactory(GsonConverterFactory.create(builder.create()))
									.build();
		SageoriAPI api = client.create(SageoriAPI.class);
		Call<List<MemberItem>> callMembers = api.getMembers();
		callMembers.enqueue(new Callback<List<MemberItem>>(){
			@Override
			public void onResponse(Call<List<MemberItem>> call, Response<List<MemberItem>> response){
				if(response.isSuccessful()){
					List<MemberItem> dataList = response.body();

					ListView listView = (ListView)findViewById(R.id.listview);
					MemberListAdapter adapter = new MemberListAdapter(MembersActivity.this, 0, dataList);
					listView.setAdapter(adapter);
				}
			}

			@Override
			public void onFailure(Call<List<MemberItem>> call, Throwable t) {
				
			}
		});

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
                // Show toast message when no text is entered
                //if (TextUtils.isEmpty(inputNote.getText().toString())) {
                //    Toast.makeText(MembersActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                 //   return;
                //} else {
                    alertDialog.dismiss();
                //}

                // check if user updating note
                if (shouldUpdate /*&& note != null*/) {
                    // update note by it's id
                    //updateNote(inputNote.getText().toString(), position);
                } else {
                    // create new note
                    //createNote(inputNote.getText().toString());
                }
            }
        });
    }
}
