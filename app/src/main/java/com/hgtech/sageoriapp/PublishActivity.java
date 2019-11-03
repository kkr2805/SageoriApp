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

    File photoFile;
    private ImageView publishImageView;
    private PreViewImageDialog previewImageDialog;

    SearchView searchView;

    SearchParams searchParams;
    EditText editDate;
    DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener(){
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            editDate.setText(year + "." + (monthOfYear + 1) + "." + dayOfMonth);
        }
    };

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
        switch(item.getItemId())
        {
            case R.id.searchDate:
                //calendarView.setVisibility(View.VISIBLE);
                break;
        }
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
                dispatchTakePictureIntent();
            }
        });

        // galleryButton
        Button galleryButton = (Button)view.findViewById(R.id.buttonGallery);
        galleryButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dispatchGalleryPictureIntent();
            }
        });

        publishImageView = (ImageView) view.findViewById(R.id.imageViewPublish);
        publishImageView.setImageBitmap(null);
        previewImageDialog = null;

        publishImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviewImageDialog();
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

            Call<ResponseBody> call = api.getImageFile(SageoriClient.GetImageURL(dataList.get(position).ImageFile));
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            // display the image data in a ImageView or save it
                            Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                            publishImageView.setImageBitmap(bmp);

                            previewImageDialog = new PreViewImageDialog(PublishActivity.this, bmp);
                        } else {
                            // TODO
                        }
                    } else {
                        // TODO
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    // TODO
                }
            });
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

        // date picker
        editDate = (EditText)view.findViewById(R.id.editTextDate);
        editDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Date today = new Date();
                Calendar calendar = Calendar.getInstance(Locale.KOREA);
                calendar.setTime(today);

                // DatePickerDialog
                DatePickerDialog dialog = new DatePickerDialog(PublishActivity.this, datePickerListener
                        , calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH));

                dialog.show();
            }

        });

        editDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.onTouchEvent(event);
                InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null){
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return true;
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
        callbackMembers = new CallbackGetMembers();
        callMembers.enqueue(callbackMembers);
        Call<List<Integer>> callMachines = api.getMachines();
        CallbackGetMachines callbackMachines;
        callbackMachines = new CallbackGetMachines();
        callMachines.enqueue(callbackMachines);

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox checkBoxMachine = (CheckBox) alertDialog.findViewById(R.id.checkBoxMachine);
                CheckBox checkBoxMember = (CheckBox) alertDialog.findViewById(R.id.checkBoxMember);
                CheckBox checkBoxDate = (CheckBox) alertDialog.findViewById(R.id.checkBoxDate);

                String strDate = editDate.getText().toString();

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

                int machineSpinnerPos = machineSpinner.getSelectedItemPosition();
                int memberSpinnerPos = memberSpinner.getSelectedItemPosition();

                HashMap<String, String> getData = new HashMap<String, String>();

                if(checkBoxMachine.isChecked())
                    getData.put("MachineID", Integer.toString(machineList.get(machineSpinnerPos)));

                if(checkBoxMember.isChecked())
                    getData.put("MemberID", Integer.toString(memberList.get(memberSpinnerPos).ID));

                if(checkBoxDate.isChecked())
                    getData.put("Date", strDate);

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
                    previewImageDialog = null;

                    SageoriAPI api = SageoriClient.getAPI();
                    SageoriClient.GetImageURL(dataList.get(position).ImageFile);
                    Call<ResponseBody> call = api.getImageFile(SageoriClient.GetImageURL(dataList.get(position).ImageFile));
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null) {
                                    // display the image data in a ImageView or save it
                                    Bitmap bmp = BitmapFactory.decodeStream(response.body().byteStream());
                                    previewImageDialog = new PreViewImageDialog(PublishActivity.this, bmp);
                                    showPreviewImageDialog();
                                } else {
                                    // TODO
                                }
                            } else {
                                previewImageDialog = null;
                                showPreviewImageDialog();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            // TODO
                        }
                    });
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

    private void showPreviewImageDialog(){
        if(previewImageDialog == null)
        {
            Toast.makeText(getApplicationContext(), "사진이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        previewImageDialog.show();
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
        Bitmap imageBitmap = null;

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");

            int compressionRatio = 25; //1 == originalImage, 2 = 50% compression, 4=25% compress
            try {
                imageBitmap = BitmapFactory.decodeFile (photoFile.getPath ());
                imageBitmap.compress (Bitmap.CompressFormat.JPEG, compressionRatio, new FileOutputStream(photoFile));
            }
            catch (Throwable t) {
                Log.e("ERROR", "Error compressing file." + t.toString ());
                t.printStackTrace ();
            }

            publishImageView.setImageBitmap(imageBitmap);
            previewImageDialog = new PreViewImageDialog(this, imageBitmap);

        }else if(requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK) {

            final Uri imageUri = data.getData();
            String imgDecodableString = getUriRealPath(this, imageUri);
            photoFile = new File(imgDecodableString);

            int compressionRatio = 25; //1 == originalImage, 2 = 50% compression, 4=25% compress
            try {
                imageBitmap = BitmapFactory.decodeFile (photoFile.getPath ());
                imageBitmap.compress (Bitmap.CompressFormat.JPEG, compressionRatio, new FileOutputStream(photoFile));
            }
            catch (Throwable t) {
                Log.e("ERROR", "Error compressing file." + t.toString ());
                t.printStackTrace ();
            }

            publishImageView.setImageBitmap(imageBitmap);
            previewImageDialog = new PreViewImageDialog(this, imageBitmap);

        }else if(requestCode == SEARCH_DIALOG && resultCode == RESULT_OK) {

            Bundle extraData = data.getExtras();
            searchParams = (SearchParams) extraData.get("searchParams");

        }
    }

    /* Get uri related content real local file path. */
    private String getUriRealPath(Context ctx, Uri uri)
    {
        String ret = "";

        if( isAboveKitKat() )
        {
            // Android OS above sdk version 19.
            ret = getUriRealPathAboveKitkat(ctx, uri);
        }else
        {
            // Android OS below sdk version 19
            ret = getImageRealPath(getContentResolver(), uri, null);
        }

        return ret;
    }

    private String getUriRealPathAboveKitkat(Context ctx, Uri uri)
    {
        String ret = "";

        if(ctx != null && uri != null) {

            if(isContentUri(uri))
            {
                if(isGooglePhotoDoc(uri.getAuthority()))
                {
                    ret = uri.getLastPathSegment();
                }else {
                    ret = getImageRealPath(getContentResolver(), uri, null);
                }
            }else if(isFileUri(uri)) {
                ret = uri.getPath();
            }else if(isDocumentUri(ctx, uri)){

                // Get uri related document id.
                String documentId = DocumentsContract.getDocumentId(uri);

                // Get uri authority.
                String uriAuthority = uri.getAuthority();

                if(isMediaDoc(uriAuthority))
                {
                    String idArr[] = documentId.split(":");
                    if(idArr.length == 2)
                    {
                        // First item is document type.
                        String docType = idArr[0];

                        // Second item is document real id.
                        String realDocId = idArr[1];

                        // Get content uri by document type.
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if("image".equals(docType))
                        {
                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        }else if("video".equals(docType))
                        {
                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        }else if("audio".equals(docType))
                        {
                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        // Get where clause with real document id.
                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                        ret = getImageRealPath(getContentResolver(), mediaContentUri, whereClause);
                    }

                }else if(isDownloadDoc(uriAuthority))
                {
                    // Build download uri.
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");

                    // Append download document id at uri end.
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));

                    ret = getImageRealPath(getContentResolver(), downloadUriAppendId, null);

                }else if(isExternalStoreDoc(uriAuthority))
                {
                    String idArr[] = documentId.split(":");
                    if(idArr.length == 2)
                    {
                        String type = idArr[0];
                        String realDocId = idArr[1];

                        if("primary".equalsIgnoreCase(type))
                        {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /* Check whether current android os version is bigger than kitkat or not. */
    private boolean isAboveKitKat()
    {
        boolean ret = false;
        ret = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        return ret;
    }

    /* Check whether this uri represent a document or not. */
    private boolean isDocumentUri(Context ctx, Uri uri)
    {
        boolean ret = false;
        if(ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri);
        }
        return ret;
    }

    /* Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  */
    private boolean isContentUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("content".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     * */
    private boolean isFileUri(Uri uri)
    {
        boolean ret = false;
        if(uri != null) {
            String uriSchema = uri.getScheme();
            if("file".equalsIgnoreCase(uriSchema))
            {
                ret = true;
            }
        }
        return ret;
    }


    /* Check whether this document is provided by ExternalStorageProvider. */
    private boolean isExternalStoreDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.externalstorage.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by DownloadsProvider. */
    private boolean isDownloadDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.downloads.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by MediaProvider. */
    private boolean isMediaDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.android.providers.media.documents".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by google photos. */
    private boolean isGooglePhotoDoc(String uriAuthority)
    {
        boolean ret = false;

        if("com.google.android.apps.photos.content".equals(uriAuthority))
        {
            ret = true;
        }

        return ret;
    }

    /* Return uri represented document file real local path.*/
    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause)
    {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if(cursor!=null)
        {
            boolean moveToFirst = cursor.moveToFirst();
            if(moveToFirst)
            {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if( uri==MediaStore.Images.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Images.Media.DATA;
                }else if( uri==MediaStore.Audio.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Audio.Media.DATA;
                }else if( uri==MediaStore.Video.Media.EXTERNAL_CONTENT_URI )
                {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        return ret;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        String currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    /*private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }*/

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            //...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.hgtech.sageoriapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    void dispatchGalleryPictureIntent()
    {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }
}
