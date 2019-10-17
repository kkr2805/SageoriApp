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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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

    private Spinner machineSpinner;
    private List<Integer> machineList;
    private ArrayAdapter<Integer> machineListAdapter;

    private Spinner memberSpinner;
    private List<MemberItem> memberList;
    private MemberSpinnerAdapter memberSpinnerAdapter;

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

    private class CallbackGetMachines implements Callback<List<Integer>> {

        private boolean bShouldUpdate;
        private int machineID;

        CallbackGetMachines() {
            this.bShouldUpdate = false; 
            this.machineID = 0;
        }

        CallbackGetMachines(boolean bShouldUpdate, int machineID) {
            this.bShouldUpdate = bShouldUpdate; 
            this.machineID = machineID;
        }

        @Override
        public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response){
            if(response.isSuccessful()){
                machineList = response.body();

                if(machineSpinner != null && machineListAdapter != null)
                {
                    machineListAdapter.clear();
                    machineListAdapter.addAll(machineList);
                    machineListAdapter.notifyDataSetChanged();

                    if(this.bShouldUpdate == true){
                        int index = machineList.indexOf(new Integer(this.machineID));
                        machineSpinner.setSelection(index);
                    }
                }
                Log.d("Tag", "데이터 조회 완료");
                Toast.makeText(getApplicationContext(), "데이터 조회 완료.", Toast.LENGTH_SHORT).show();
            }

            PublishActivity.this.showProgressbar(false);
        }

        @Override
        public void onFailure(Call<List<Integer>> call, Throwable t) {
            String strErrMessage = new String("데이터 조회 실패." );
            strErrMessage += t.getMessage();
            Toast.makeText(getApplicationContext(), strErrMessage, Toast.LENGTH_SHORT).show();
            PublishActivity.this.showProgressbar(false);
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
                Toast.makeText(getApplicationContext(), "데이터 조회 완료.", Toast.LENGTH_SHORT).show();
            }

            PublishActivity.this.showProgressbar(false);
        }

        @Override
        public void onFailure(Call<List<MemberItem>> call, Throwable t) {
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
        //listView.addHeaderView(headerView);




        dataList = new ArrayList<PublishItem>();

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


        // machine spinner
        machineSpinner = (Spinner)view.findViewById(R.id.spinnerMachine);
        machineList = new ArrayList<>();
        machineListAdapter = new ArrayAdapter<Integer>(view.getContext(), android.R.layout.simple_spinner_item, machineList);
        machineSpinner.setAdapter(machineListAdapter);

        // member spinner
        memberSpinner = (Spinner)view.findViewById(R.id.spinnerMember);
        memberList = new ArrayList<>();
        memberSpinnerAdapter = new MemberSpinnerAdapter(view.getContext(), android.R.layout.simple_spinner_item, memberList);
        memberSpinner.setAdapter(memberSpinnerAdapter);


        EditText editCredit = (EditText)view.findViewById(R.id.editTextCredit);
        EditText editBank = (EditText)view.findViewById(R.id.editTextBank);


        if(shouldUpdate) {
            editCredit.setText(Integer.toString(dataList.get(position).Credit));
            editBank.setText(Integer.toString(dataList.get(position).Bank));
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

        // Retrofit API
        SageoriAPI api = SageoriClient.getAPI();
        Call<List<MemberItem>> callMembers = api.getMembers();
        CallbackGetMembers callbackMembers;
        if(shouldUpdate) {
            callbackMembers = new CallbackGetMembers(true, dataList.get(position).MemberID);
        }else{
            callbackMembers = new CallbackGetMembers();
        }
        callMembers.enqueue(callbackMembers);
Call<List<Integer>> callMachines = api.getMachines();
        CallbackGetMachines callbackMachines;
        if(shouldUpdate) {
            callbackMachines = new CallbackGetMachines(true, dataList.get(position).MachineID);
        }else{
            callbackMachines = new CallbackGetMachines();
        }
        callMachines.enqueue(callbackMachines);

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText creditEdit = (EditText)alertDialog.findViewById(R.id.editTextCredit);
                EditText bankEdit = (EditText)alertDialog.findViewById(R.id.editTextBank);

                String strCredit = creditEdit.getText().toString();
                String strBank = bankEdit.getText().toString();

                if(machineSpinner.getSelectedItemPosition() < 0) {
                    Toast.makeText(getApplicationContext(), "기계번호를 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(memberSpinner.getSelectedItemPosition() < 0) {
                    Toast.makeText(getApplicationContext(), "회원이름을 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(strCredit.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Credit 을 입력하세요.", Toast.LENGTH_SHORT).show();
                    creditEdit.requestFocus();
                    return;
                }

                if(strBank.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Bank 를 입력하세요.", Toast.LENGTH_SHORT).show();
                    bankEdit.requestFocus();
                    return;
                }

                int machineSpinnerPos = machineSpinner.getSelectedItemPosition();
                int memberSpinnerPos = memberSpinner.getSelectedItemPosition();


                HashMap<String, String> postData = new HashMap<String, String>();
                postData.put("MachineID", Integer.toString(machineList.get(machineSpinnerPos)));
                postData.put("MemberID", Integer.toString(memberList.get(memberSpinnerPos).ID));
                postData.put("Credit", strCredit);
                postData.put("Bank", strBank);

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
