package com.hgtech.sageoriapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublishActivity extends AppCompatActivity
                            implements SwipeRefreshLayout.OnRefreshListener{

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int RESULT_LOAD_IMG = 2;
    static final int SEARCH_DIALOG = 3;

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

    ImagePresenter imagePresenter;

    SearchView searchView;

    SearchParams searchParams;
    TextView editDate;
    TextView editDateStart;
    TextView editDateEnd;

    private class DatePickerListener implements DatePickerDialog.OnDateSetListener {

        TextView editDate;

        public DatePickerListener(TextView editDate) {
            this.editDate = editDate;
        }

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){
            this.editDate.setText(year + "." + (monthOfYear + 1) + "." + dayOfMonth);
        }
    }

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

        // 검색조건
        searchParams = new SearchParams();

        imagePresenter = new ImagePresenter(this);

        // ListView 설정
        ListView listView = (ListView)findViewById(R.id.listview);
        //View headerView = getLayoutInflater().inflate(R.layout.publish_header, null, false);
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
        getMenuInflater().inflate(R.menu.publish_menu, menu);
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

        MenuItem searchDateButton = (MenuItem)menu.findItem(R.id.search);
        searchDateButton.setTitle("검색");
        searchDateButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                //intent.putExtra("searchParams", searchParams);
                //startActivityForResult(intent, SEARCH_DIALOG);

                showSearchDialog();

                return false;
            }
        });

        MenuItem searchItem = menu.findItem(R.id.actionSearch);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                listAdapter.getFilter().filter(s);
                return true;
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

        // cameraButton
        Button cameraButton = (Button)view.findViewById(R.id.buttonCamera);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePresenter.dispatchTakePictureIntent(REQUEST_TAKE_PHOTO);
            }
        });

        // galleryButton
        Button galleryButton = (Button)view.findViewById(R.id.buttonGallery);
        galleryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                imagePresenter.dispatchGalleryPictureIntent(RESULT_LOAD_IMG);
            }
        });

        ImageView publishImageView = (ImageView) view.findViewById(R.id.imageViewPublish);
        imagePresenter.setImageView(publishImageView);


        //previewImageDialog = null;

        publishImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePresenter.showPreviewImageDialog();
            }
        });

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
            imagePresenter.requestImage(dataList.get(position).ImageFile);

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


                HashMap<String, RequestBody> postData = new HashMap<String, RequestBody>();


                RequestBody machineID = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(machineList.get(machineSpinnerPos)));
                RequestBody memberID = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(memberList.get(memberSpinnerPos).ID));
                RequestBody credit = RequestBody.create(MediaType.parse("text/plain"), strCredit);
                RequestBody bank = RequestBody.create(MediaType.parse("text/plain"), strBank);

                postData.put("MachineID", machineID);
                postData.put("MemberID", memberID);
                postData.put("Credit", credit);
                postData.put("Bank", bank);

                File photoFile = imagePresenter.getPhotoFile();
                if(photoFile != null){
                    RequestBody imageFile = RequestBody.create(MediaType.parse("image/*"), photoFile);
                    postData.put("PublishImageFile\"; filename=\"pp.png\" ", imageFile);
                }

                showProgressbar(true);
                final SageoriAPI api = SageoriClient.getAPI();

                if(shouldUpdate) {
                    RequestBody ID = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(dataList.get(position).ID));
                    postData.put("ID", ID);
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

    private void showSearchDialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        final View view = layoutInflaterAndroid.inflate(R.layout.search_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(PublishActivity.this);
        alertDialogBuilderUserInput.setView(view);


        CheckBox checkBoxMachine = (CheckBox) view.findViewById(R.id.checkBoxMachine);
        if(searchParams.checkMachineID){
            checkBoxMachine.setChecked(true);
        }

        // machine spinner
        machineSpinner = (Spinner)view.findViewById(R.id.spinnerMachine);
        machineList = new ArrayList<>();
        machineListAdapter = new ArrayAdapter<Integer>(view.getContext(), android.R.layout.simple_spinner_item, machineList);
        machineSpinner.setAdapter(machineListAdapter);

        CheckBox checkBoxMember = (CheckBox) view.findViewById(R.id.checkBoxMember);
        if(searchParams.checkMemberID){
            checkBoxMember.setChecked(true);
        }

        // member spinner
        memberSpinner = (Spinner)view.findViewById(R.id.spinnerMember);
        memberList = new ArrayList<>();
        memberSpinnerAdapter = new MemberSpinnerAdapter(view.getContext(), android.R.layout.simple_spinner_item, memberList);
        memberSpinner.setAdapter(memberSpinnerAdapter);

        // date picker (날짜 검색)
        editDate = (TextView)view.findViewById(R.id.editTextDate);
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date today = new Date();
                Calendar calendar = Calendar.getInstance(Locale.KOREA);
                calendar.setTime(today);

                // DatePickerDialog
                DatePickerDialog dialog = new DatePickerDialog(PublishActivity.this, new DatePickerListener(editDate)
                        , calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH));

                dialog.show();
            }

        });

        // date picker (기간 검색)
        editDateStart = (TextView)view.findViewById(R.id.editTextDate1);
        editDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date today = new Date();
                Calendar calendar = Calendar.getInstance(Locale.KOREA);
                calendar.setTime(today);

                // DatePickerDialog
                DatePickerDialog dialog = new DatePickerDialog(PublishActivity.this, new DatePickerListener(editDateStart)
                        , calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH));

                dialog.show();
            }

        });

        editDateEnd = (TextView)view.findViewById(R.id.editTextDate2);
        editDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date today = new Date();
                Calendar calendar = Calendar.getInstance(Locale.KOREA);
                calendar.setTime(today);

                // DatePickerDialog
                DatePickerDialog dialog = new DatePickerDialog(PublishActivity.this, new DatePickerListener(editDateEnd)
                        , calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH));

                dialog.show();
            }

        });

        final CheckBox checkBoxDate = (CheckBox) view.findViewById(R.id.checkBoxDate);
        if(searchParams.checkDate) {
            checkBoxDate.setChecked(true);

            if(searchParams.createdDate != null)
                editDate.setText(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(searchParams.createdDate));
        }


        final CheckBox checkBoxDateStart = (CheckBox) view.findViewById(R.id.checkBoxDate1);
        if(searchParams.checkDateStart){
            checkBoxDateStart.setChecked(true);

            if(searchParams.createdDateStart != null)
                editDateStart.setText(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(searchParams.createdDateStart));

            if(searchParams.createdDateEnd != null)
                editDateEnd.setText(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(searchParams.createdDateEnd));
        }

        checkBoxDate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    checkBoxDateStart.setChecked(false);
            }
        });

        checkBoxDateStart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    checkBoxDate.setChecked(false);
            }
        });

        TextView dialogTitle = (TextView)view.findViewById(R.id.dialogTitle);
        dialogTitle.setText("검색");

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("검색", new DialogInterface.OnClickListener() {
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
        callbackMembers = new CallbackGetMembers(true, searchParams.memberID);
        callMembers.enqueue(callbackMembers);
        Call<List<Integer>> callMachines = api.getMachines();
        CallbackGetMachines callbackMachines;
        callbackMachines = new CallbackGetMachines(true, searchParams.machineID);
        callMachines.enqueue(callbackMachines);

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox checkBoxMachine = (CheckBox) alertDialog.findViewById(R.id.checkBoxMachine);
                CheckBox checkBoxMember = (CheckBox) alertDialog.findViewById(R.id.checkBoxMember);
                CheckBox checkBoxDate = (CheckBox) alertDialog.findViewById(R.id.checkBoxDate);

                int machineSpinnerPos = machineSpinner.getSelectedItemPosition();
                int memberSpinnerPos = memberSpinner.getSelectedItemPosition();

                String strDate = editDate.getText().toString();
                String strDateStart = editDateStart.getText().toString();
                String strDateEnd = editDateEnd.getText().toString();

                if(checkBoxMachine.isChecked() && machineSpinner.getSelectedItemPosition() < 0) {
                    Toast.makeText(getApplicationContext(), "기계번호를 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(checkBoxMember.isChecked() && memberSpinner.getSelectedItemPosition() < 0) {
                    Toast.makeText(getApplicationContext(), "회원이름을 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(checkBoxDate.isChecked() && strDate.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(checkBoxDateStart.isChecked() && (strDateStart.isEmpty() || strDateEnd.isEmpty())) {
                    Toast.makeText(getApplicationContext(), "날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
                    //checkBoxDateStart.setFocusable(true);
                    return;
                }

                // 검색상태를 보존한다.

                if(checkBoxMachine.isChecked()){
                    PublishActivity.this.searchParams.checkMachineID = true;
                    PublishActivity.this.searchParams.machineID = machineList.get(machineSpinnerPos);
                }else{
                    PublishActivity.this.searchParams.checkMachineID = false;
                    PublishActivity.this.searchParams.machineID = -1;
                }

                if(checkBoxMember.isChecked()) {
                    PublishActivity.this.searchParams.checkMemberID = true;
                    PublishActivity.this.searchParams.memberID = memberList.get(memberSpinnerPos).ID;
                }else{
                    PublishActivity.this.searchParams.checkMemberID = false;
                    PublishActivity.this.searchParams.memberID = -1;
                }

                if(checkBoxDate.isChecked()){
                    PublishActivity.this.searchParams.checkDate = true;
                    try{
                        Log.d("TAG", strDate);
                        PublishActivity.this.searchParams.createdDate = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).parse(strDate);
                    }catch(ParseException e){
                        Log.d("TAG", e.toString());
                        PublishActivity.this.searchParams.createdDate = null;
                    }

                }else{
                    PublishActivity.this.searchParams.checkDate = false;
                    PublishActivity.this.searchParams.createdDate = null;
                }

                if(checkBoxDateStart.isChecked()){
                    PublishActivity.this.searchParams.checkDateStart = checkBoxDateStart.isChecked();
                    try{
                        PublishActivity.this.searchParams.createdDateStart = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).parse(strDateStart);
                        PublishActivity.this.searchParams.createdDateEnd = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).parse(strDateEnd);
                    }catch(ParseException e){
                        PublishActivity.this.searchParams.createdDateStart = null;
                    }
                }else{
                    PublishActivity.this.searchParams.checkDateStart = checkBoxDateStart.isChecked();
                    PublishActivity.this.searchParams.createdDateStart = null;
                    PublishActivity.this.searchParams.createdDateEnd = null;
                }

                HashMap<String, String> getData = new HashMap<String, String>();

                if(checkBoxMachine.isChecked())
                    getData.put("MachineID", Integer.toString(machineList.get(machineSpinnerPos)));

                if(checkBoxMember.isChecked())
                    getData.put("MemberID", Integer.toString(memberList.get(memberSpinnerPos).ID));

                if(checkBoxDate.isChecked())
                    getData.put("Date", strDate);

                if(checkBoxDateStart.isChecked()){
                    getData.put("DateStart", strDateStart);
                    getData.put("DateEnd", strDateEnd);
                }


                showProgressbar(true);
                final SageoriAPI api = SageoriClient.getAPI();

                Call<List<PublishItem>> callPost = api.getPublishes(getData);
                callPost.enqueue(new CallbackGetPublishItems());

                alertDialog.dismiss();
            }
        });
    }

    private void showActionsDialog(final int position) {
        CharSequence actionMenu[] = new CharSequence[]{"사진보기", "수정하기", "삭제하기"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("작업을 선택하세요.");
        builder.setItems(actionMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    imagePresenter.showPreviewImageDialog(dataList.get(position).ImageFile);
                }
                else if (which == 1) {
                    showPublishDialog(true, position);
                } else if(which == 2) {

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
                            Toast.makeText(getApplicationContext(), "지급정보 삭제실패", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            imagePresenter.onActivityResult(REQUEST_IMAGE_CAPTURE, data);

        }else if(requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {

            imagePresenter.onActivityResult(RESULT_LOAD_IMG, data);

        }else if(requestCode == SEARCH_DIALOG && resultCode == RESULT_OK) {

            Bundle extraData = data.getExtras();
            searchParams = (SearchParams) extraData.get("searchParams");

        }
    }


}
