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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReturnActivity extends AppCompatActivity
                        implements SwipeRefreshLayout.OnRefreshListener {

    private class CallbackGetReturnItems implements Callback<List<ReturnItem>> {
        @Override
        public void onResponse(Call<List<ReturnItem>> call, Response<List<ReturnItem>> response){
            if(response.isSuccessful()){
                dataList = response.body();
                listAdapter.clear();
                listAdapter.addAll(dataList);
                listAdapter.notifyDataSetChanged();

                Log.d("Tag", "데이터 조회 완료");
                Toast.makeText(getApplicationContext(), "데이터 조회 완료.", Toast.LENGTH_SHORT).show();
            }

            ReturnActivity.this.showProgressbar(false);
        }

        @Override
        public void onFailure(Call<List<ReturnItem>> call, Throwable t) {
            String strErrMessage = new String("데이터 조회 실패." );
            strErrMessage += t.getMessage();
            Toast.makeText(getApplicationContext(), strErrMessage, Toast.LENGTH_SHORT).show();
            ReturnActivity.this.showProgressbar(false);
        }
    }

    private class CallbackGetMachines implements Callback<List<Integer>> {

        private boolean bShouldUpdate;
        private int machineID1;
        private int machineID2;

        CallbackGetMachines() {
            this.bShouldUpdate = false;
            this.machineID1 = 0;
        }

        CallbackGetMachines(boolean bShouldUpdate, int machineID1, int machineID2) {
            this.bShouldUpdate = bShouldUpdate;
            this.machineID1 = machineID1;
            this.machineID2 = machineID2;
        }

        @Override
        public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response){
            if(response.isSuccessful()){
                machineList = response.body();

                if(machineSpinner1 != null && machineSpinner2 != null && machineListAdapter != null)
                {
                    machineListAdapter.clear();
                    machineListAdapter.addAll(machineList);
                    machineListAdapter.notifyDataSetChanged();

                    if(this.bShouldUpdate == true){
                        int index1 = machineList.indexOf(new Integer(this.machineID1));
                        int index2 = machineList.indexOf(new Integer(this.machineID2));
                        machineSpinner1.setSelection(index1);
                        machineSpinner2.setSelection(index2);
                    }
                }
                Log.d("Tag", "데이터 조회 완료");
                Toast.makeText(getApplicationContext(), "데이터 조회 완료.", Toast.LENGTH_SHORT).show();
            }

            ReturnActivity.this.showProgressbar(false);
        }

        @Override
        public void onFailure(Call<List<Integer>> call, Throwable t) {
            String strErrMessage = new String("데이터 조회 실패." );
            strErrMessage += t.getMessage();
            Toast.makeText(getApplicationContext(), strErrMessage, Toast.LENGTH_SHORT).show();
            ReturnActivity.this.showProgressbar(false);
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

            ReturnActivity.this.showProgressbar(false);
        }

        @Override
        public void onFailure(Call<List<MemberItem>> call, Throwable t) {
            String strErrMessage = new String("데이터 조회 실패." );
            strErrMessage += t.getMessage();
            Toast.makeText(getApplicationContext(), strErrMessage, Toast.LENGTH_SHORT).show();
            ReturnActivity.this.showProgressbar(false);
        }
    }

    private Spinner machineSpinner1;
    private Spinner machineSpinner2;
    private List<Integer> machineList;
    private ArrayAdapter<Integer> machineListAdapter;

    private Spinner memberSpinner;
    private List<MemberItem> memberList;
    private MemberSpinnerAdapter memberSpinnerAdapter;

    private List<ReturnItem> dataList;
    private ReturnListAdapter listAdapter;

    private SwipeRefreshLayout swipeLayout;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.return_layout);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("회수관리");
        setSupportActionBar(toolbar);

        // SwipeRefreshLayout 설정
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
        swipeLayout.setOnRefreshListener(this);

        // ListView 설정
        ListView listView = (ListView)findViewById(R.id.listview);

        dataList = new ArrayList<ReturnItem>();

        listAdapter = new ReturnListAdapter(this, 0, dataList);
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
        Call<List<ReturnItem>> callReturnItems = api.getReturnItems();
        callReturnItems.enqueue(new ReturnActivity.CallbackGetReturnItems());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.scorebox_menu, menu);
        MenuItem loginItem = (MenuItem)menu.findItem(R.id.login);
        loginItem.setTitle("로그아웃");

        MenuItem newItemButton = (MenuItem)menu.findItem(R.id.new_item);
        newItemButton.setTitle("회수등록");
        newItemButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showRetrunDialog(false,-1);
                return false;
            }
        });

        MenuItem searchDateButton = (MenuItem)menu.findItem(R.id.searchDate);
        searchDateButton.setTitle("날짜검색");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        return true;
    }

    private void showRetrunDialog(final boolean shouldUpdate, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.return_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(ReturnActivity.this);
        alertDialogBuilderUserInput.setView(view);

        machineList = new ArrayList<>();
        machineListAdapter = new ArrayAdapter<Integer>(view.getContext(), android.R.layout.simple_spinner_item, machineList);

        // machine spinner1
        machineSpinner1 = (Spinner)view.findViewById(R.id.spinnerMachine1);
        machineSpinner1.setAdapter(machineListAdapter);

        // machine spinner
        machineSpinner2 = (Spinner)view.findViewById(R.id.spinnerMachine2);
        machineSpinner2.setAdapter(machineListAdapter);

        // member spinner
        memberSpinner = (Spinner)view.findViewById(R.id.spinnerMember);
        memberList = new ArrayList<>();
        memberSpinnerAdapter = new MemberSpinnerAdapter(view.getContext(), android.R.layout.simple_spinner_item, memberList);
        memberSpinner.setAdapter(memberSpinnerAdapter);

        EditText editReturn = (EditText)view.findViewById(R.id.editTextReturn);
        EditText editService = (EditText)view.findViewById(R.id.editTextService);
        EditText editOnePone = (EditText)view.findViewById(R.id.editTextOnePone);


        if(shouldUpdate) {
            editReturn.setText(Integer.toString(dataList.get(position).Retrun));
            editService.setText(Integer.toString(dataList.get(position).Service));
            editOnePone.setText(Integer.toString(dataList.get(position).OnePone));
        }

        TextView dialogTitle = (TextView)view.findViewById(R.id.dialogTitle);
        dialogTitle.setText(!shouldUpdate ? "회수등록" : "회수수정");

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
        ReturnActivity.CallbackGetMembers callbackMembers;
        if(shouldUpdate) {
            callbackMembers = new ReturnActivity.CallbackGetMembers(true, dataList.get(position).MemberID);
        }else{
            callbackMembers = new ReturnActivity.CallbackGetMembers();
        }
        callMembers.enqueue(callbackMembers);
        Call<List<Integer>> callMachines = api.getMachines();
        ReturnActivity.CallbackGetMachines callbackMachines;
        if(shouldUpdate) {
            callbackMachines = new ReturnActivity.CallbackGetMachines(true, dataList.get(position).MachineID1, dataList.get(position).MachineID2);
        }else{
            callbackMachines = new ReturnActivity.CallbackGetMachines();
        }
        callMachines.enqueue(callbackMachines);

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editReturn = (EditText)alertDialog.findViewById(R.id.editTextReturn);
                EditText editService = (EditText)alertDialog.findViewById(R.id.editTextService);
                EditText editOnePone = (EditText)alertDialog.findViewById(R.id.editTextOnePone);

                String strReturn = editReturn.getText().toString();
                String strService = editService.getText().toString();
                String strOnePone = editOnePone.getText().toString();

                if(machineSpinner1.getSelectedItemPosition() < 0) {
                    Toast.makeText(getApplicationContext(), "기계번호1을 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(machineSpinner2.getSelectedItemPosition() < 0) {
                    Toast.makeText(getApplicationContext(), "기계번호2를 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(memberSpinner.getSelectedItemPosition() < 0) {
                    Toast.makeText(getApplicationContext(), "회원이름을 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(strReturn.length() == 0) {
                    Toast.makeText(getApplicationContext(), "회수를 입력하세요.", Toast.LENGTH_SHORT).show();
                    editReturn.requestFocus();
                    return;
                }

                int machineSpinnerPos1 = machineSpinner1.getSelectedItemPosition();
                int machineSpinnerPos2 = machineSpinner2.getSelectedItemPosition();
                int memberSpinnerPos = memberSpinner.getSelectedItemPosition();


                HashMap<String, String> postData = new HashMap<String, String>();


                String machineID1 = Integer.toString(machineList.get(machineSpinnerPos1));
                String machineID2 = Integer.toString(machineList.get(machineSpinnerPos2));
                String memberID = Integer.toString(memberList.get(memberSpinnerPos).ID);


                postData.put("MachineID1", machineID1);
                postData.put("MachineID2", machineID2);
                postData.put("MemberID", memberID);
                postData.put("Return", strReturn);
                postData.put("Service", strService.isEmpty() ? "0" : strService);
                postData.put("OnePone", strOnePone.isEmpty() ? "0" : strOnePone);


                showProgressbar(true);
                final SageoriAPI api = SageoriClient.getAPI();

                if(shouldUpdate) {
                    String ID = Integer.toString(dataList.get(position).ID);
                    postData.put("ID", ID);
                    Call<SageoriResult> callUpdate = api.updateReturnItem(postData);
                    callUpdate.enqueue(new Callback<SageoriResult>(){
                        @Override
                        public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                            if(response.isSuccessful() && response.body().isSuccess()){
                                Toast.makeText(getApplicationContext(), "수정되었습니다", Toast.LENGTH_SHORT).show();

                                // 리스트 최신내용으로 업데이트
                                Call<List<ReturnItem>> callReturnItems = api.getReturnItems();
                                callReturnItems.enqueue(new ReturnActivity.CallbackGetReturnItems());

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
                    Call<SageoriResult> callPost = api.createReturnItem(postData);
                    callPost.enqueue(new Callback<SageoriResult>(){
                        @Override
                        public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                            if(response.isSuccessful() && response.body().isSuccess()){
                                Toast.makeText(getApplicationContext(), "등록되었습니다", Toast.LENGTH_SHORT).show();

                                // 리스트 최신내용으로 업데이트
                                Call<List<ReturnItem>> callPublishItems = api.getReturnItems();
                                callPublishItems.enqueue(new ReturnActivity.CallbackGetReturnItems());

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
        CharSequence actionMenu[] = new CharSequence[]{"수정하기", "삭제하기"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("작업을 선택하세요.");
        builder.setItems(actionMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (which == 0) {
                    showRetrunDialog(true, position);
                } else if(which == 1) {

                    showProgressbar(true);
                    final SageoriAPI api = SageoriClient.getAPI();

                    HashMap<String, String> postData = new HashMap<String, String>();
                    postData.put("ID", Integer.toString(dataList.get(position).ID));

                    Call<SageoriResult> callDelete = api.deleteReturnItem(postData);
                    callDelete.enqueue(new Callback<SageoriResult>(){
                        @Override
                        public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                            if(response.isSuccessful() && response.body().isSuccess()){
                                Toast.makeText(getApplicationContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();

                                // 리스트 최신내용으로 업데이트
                                Call<List<ReturnItem>> callReturnItems = api.getReturnItems();
                                callReturnItems.enqueue(new ReturnActivity.CallbackGetReturnItems());

                                showProgressbar(false);
                                return;
                            }
                        }

                        @Override
                        public void onFailure(Call<SageoriResult> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "회수정보 삭제실패", Toast.LENGTH_SHORT).show();
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
        Call<List<ReturnItem>> callReturnItems = api.getReturnItems();
        callReturnItems.enqueue(new ReturnActivity.CallbackGetReturnItems());

        swipeLayout.setRefreshing(false);
    }
}
