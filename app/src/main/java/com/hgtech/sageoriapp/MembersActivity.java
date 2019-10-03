package com.hgtech.sageoriapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MembersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.members_layout);

        // Toolbar 설정
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("회원관리");
        setSupportActionBar(toolbar);


        String[] arrName = {"송효주", "김두현", "송지현"};
        String[] arrPhone = {"010-9278-2806", "010-2030-2805", "010-8609-2806"};

        ArrayList<MemberItem> dataList = new ArrayList();
        for(int k = 0; k < 30; k++){
            MemberItem member = new MemberItem();

            member.No = k + 1;
            member.Name = arrName[k % arrName.length];
            member.HP = arrPhone[k % arrPhone.length];
            
            dataList.add(member);
        }
        
        ListView listView = (ListView)findViewById(R.id.listview);
        MemberListAdapter adapter = new MemberListAdapter(this, 0, dataList);
        listView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.title_menu, menu);
        MenuItem loginItem = (MenuItem)menu.findItem(R.id.login);
        loginItem.setTitle("로그아웃");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        return true;
    }
}
