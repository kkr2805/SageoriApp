package com.hgtech.sageoriapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.File;
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

    private class CallbackGetMachines2 implements Callback<List<Integer>> {

        private boolean bShouldUpdate;
        private int machineID;

        CallbackGetMachines2() {
            this.bShouldUpdate = false;
            this.machineID = 0;
        }

        CallbackGetMachines2(boolean bShouldUpdate, int machineID) {
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

    static final int REQUEST_IMAGE_CAPTURE1 = 1;
    static final int REQUEST_TAKE_PHOTO1 = 1;
    static final int RESULT_LOAD_IMG1 = 2;

    static final int REQUEST_IMAGE_CAPTURE2 = 3;
    static final int REQUEST_TAKE_PHOTO2 = 3;
    static final int RESULT_LOAD_IMG2 = 4;

    private Spinner machineSpinner;
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

    SearchView searchView;

    ImagePresenter imagePresenter1;
    ImagePresenter imagePresenter2;

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

        // 검색조건
        searchParams = new SearchParams();

        imagePresenter1 = new ImagePresenter(this);
        imagePresenter2 = new ImagePresenter(this);

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
        getMenuInflater().inflate(R.menu.return_menu, menu);
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

        // 사진1 항목
        // cameraButton1
        Button cameraButton1 = (Button)view.findViewById(R.id.buttonCamera1);
        cameraButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePresenter1.dispatchTakePictureIntent(REQUEST_TAKE_PHOTO1);
            }
        });

        // galleryButton1
        Button galleryButton1 = (Button)view.findViewById(R.id.buttonGallery1);
        galleryButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                imagePresenter1.dispatchGalleryPictureIntent(RESULT_LOAD_IMG1);
            }
        });

        ImageView returnImageView1 = (ImageView) view.findViewById(R.id.imageViewPublish1);
        imagePresenter1.setImageView(returnImageView1);

        returnImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePresenter1.showPreviewImageDialog();
            }
        });

        // 사진2 항목
        // cameraButton2
        Button cameraButton2 = (Button)view.findViewById(R.id.buttonCamera2);
        cameraButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePresenter2.dispatchTakePictureIntent(REQUEST_TAKE_PHOTO2);
            }
        });

        // galleryButton2
        Button galleryButton2 = (Button)view.findViewById(R.id.buttonGallery2);
        galleryButton2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                imagePresenter2.dispatchGalleryPictureIntent(RESULT_LOAD_IMG2);
            }
        });

        ImageView returnImageView2 = (ImageView) view.findViewById(R.id.imageViewPublish2);
        imagePresenter2.setImageView(returnImageView2);

        returnImageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePresenter2.showPreviewImageDialog();
            }
        });

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
            imagePresenter1.requestImage(dataList.get(position).ImageFile1);
            imagePresenter2.requestImage(dataList.get(position).ImageFile2);

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


                HashMap<String, RequestBody> postData = new HashMap<String, RequestBody>();

                RequestBody machineID1 = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(machineList.get(machineSpinnerPos1)));
                RequestBody machineID2 = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(machineList.get(machineSpinnerPos2)));
                RequestBody memberID = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(memberList.get(memberSpinnerPos).ID));
                RequestBody returnBody = RequestBody.create(MediaType.parse("text/plain"), strReturn);
                RequestBody serviceBody = RequestBody.create(MediaType.parse("text/plain"), strService.isEmpty() ? "0" : strService);
                RequestBody onePoneBody = RequestBody.create(MediaType.parse("text/plain"), strOnePone.isEmpty() ? "0" : strOnePone);

                postData.put("MachineID1", machineID1);
                postData.put("MachineID2", machineID2);
                postData.put("MemberID", memberID);
                postData.put("Return", returnBody);
                postData.put("Service", serviceBody);
                postData.put("OnePone", onePoneBody);

                File photoFile1 = imagePresenter1.getPhotoFile();
                File photoFile2 = imagePresenter2.getPhotoFile();

                if(!shouldUpdate && photoFile1 == null){
                    Toast.makeText(getApplicationContext(), "사진1을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!shouldUpdate && Integer.toString(machineList.get(machineSpinnerPos2)).isEmpty() == false && photoFile1 == null){
                    Toast.makeText(getApplicationContext(), "사진2를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(photoFile1 != null){
                    RequestBody imageFile = RequestBody.create(MediaType.parse("image/*"), photoFile1);
                    postData.put("ReturnImageFile1\"; filename=\"photo1.png\" ", imageFile);
                }

                if(photoFile2 != null){
                    RequestBody imageFile = RequestBody.create(MediaType.parse("image/*"), photoFile2);
                    postData.put("ReturnImageFile2\"; filename=\"photo2.png\" ", imageFile);
                }

                showProgressbar(true);
                final SageoriAPI api = SageoriClient.getAPI();

                if(shouldUpdate) {
                    RequestBody ID = RequestBody.create(MediaType.parse("text/plain"), Integer.toString(dataList.get(position).ID));
                    postData.put("ID", ID);
                    Call<SageoriResult> callUpdate = api.updateReturnItem(postData);
                    callUpdate.enqueue(new Callback<SageoriResult>(){
                        @Override
                        public void onResponse(Call<SageoriResult> call, Response<SageoriResult> response){
                            imagePresenter1.reset();
                            imagePresenter2.reset();
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
                            imagePresenter1.reset();
                            imagePresenter2.reset();
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
                            imagePresenter1.reset();
                            imagePresenter2.reset();
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
                            imagePresenter1.reset();
                            imagePresenter2.reset();
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

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(ReturnActivity.this);
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
                DatePickerDialog dialog = new DatePickerDialog(ReturnActivity.this, new ReturnActivity.DatePickerListener(editDate)
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
                DatePickerDialog dialog = new DatePickerDialog(ReturnActivity.this, new ReturnActivity.DatePickerListener(editDateStart)
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
                DatePickerDialog dialog = new DatePickerDialog(ReturnActivity.this, new ReturnActivity.DatePickerListener(editDateEnd)
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
            else
                editDate.setText(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(new Date()));
        }else{
            editDate.setText(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(new Date()));
        }


        final CheckBox checkBoxDateStart = (CheckBox) view.findViewById(R.id.checkBoxDate1);
        if(searchParams.checkDateStart){
            checkBoxDateStart.setChecked(true);

            if(searchParams.createdDateStart != null)
                editDateStart.setText(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(searchParams.createdDateStart));
            else
                editDateStart.setText(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(new Date()));

            if(searchParams.createdDateEnd != null)
                editDateEnd.setText(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(searchParams.createdDateEnd));
            else
                editDateEnd.setText(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(new Date()));
        }else{
            editDateStart.setText(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(new Date()));
            editDateEnd.setText(new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(new Date()));
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
        ReturnActivity.CallbackGetMembers callbackMembers;
        callbackMembers = new ReturnActivity.CallbackGetMembers(true, searchParams.memberID);
        callMembers.enqueue(callbackMembers);
        Call<List<Integer>> callMachines = api.getMachines();
        ReturnActivity.CallbackGetMachines2 callbackMachines;
        callbackMachines = new ReturnActivity.CallbackGetMachines2(true, searchParams.machineID);
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
                    ReturnActivity.this.searchParams.checkMachineID = true;
                    ReturnActivity.this.searchParams.machineID = machineList.get(machineSpinnerPos);
                }else{
                    ReturnActivity.this.searchParams.checkMachineID = false;
                    ReturnActivity.this.searchParams.machineID = -1;
                }

                if(checkBoxMember.isChecked()) {
                    ReturnActivity.this.searchParams.checkMemberID = true;
                    ReturnActivity.this.searchParams.memberID = memberList.get(memberSpinnerPos).ID;
                }else{
                    ReturnActivity.this.searchParams.checkMemberID = false;
                    ReturnActivity.this.searchParams.memberID = -1;
                }

                if(checkBoxDate.isChecked()){
                    ReturnActivity.this.searchParams.checkDate = true;
                    try{
                        Log.d("TAG", strDate);
                        ReturnActivity.this.searchParams.createdDate = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).parse(strDate);
                    }catch(ParseException e){
                        Log.d("TAG", e.toString());
                        ReturnActivity.this.searchParams.createdDate = null;
                    }

                }else{
                    ReturnActivity.this.searchParams.checkDate = false;
                    ReturnActivity.this.searchParams.createdDate = null;
                }

                if(checkBoxDateStart.isChecked()){
                    ReturnActivity.this.searchParams.checkDateStart = checkBoxDateStart.isChecked();
                    try{
                        ReturnActivity.this.searchParams.createdDateStart = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).parse(strDateStart);
                        ReturnActivity.this.searchParams.createdDateEnd = new SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).parse(strDateEnd);
                    }catch(ParseException e){
                        ReturnActivity.this.searchParams.createdDateStart = null;
                    }
                }else{
                    ReturnActivity.this.searchParams.checkDateStart = checkBoxDateStart.isChecked();
                    ReturnActivity.this.searchParams.createdDateStart = null;
                    ReturnActivity.this.searchParams.createdDateEnd = null;
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

                Call<List<ReturnItem>> callPost = api.getReturnItems(getData);
                callPost.enqueue(new ReturnActivity.CallbackGetReturnItems());

                alertDialog.dismiss();
            }
        });
    }

    private void showActionsDialog(final int position) {
        CharSequence actionMenu[] = new CharSequence[]{"사진보기1", "사진보기2", "수정하기", "삭제하기"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("작업을 선택하세요.");
        builder.setItems(actionMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(which == 0) {
                    imagePresenter1.showPreviewImageDialog(dataList.get(position).ImageFile1);
                } else if(which == 1) {
                    imagePresenter2.showPreviewImageDialog(dataList.get(position).ImageFile2);
                } else if (which == 2) {
                    showRetrunDialog(true, position);
                } else if(which == 3) {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_IMAGE_CAPTURE1 && resultCode == RESULT_OK) {

            imagePresenter1.onActivityResult(REQUEST_IMAGE_CAPTURE1, data);

        }else if(requestCode == RESULT_LOAD_IMG1 && resultCode == RESULT_OK) {

            imagePresenter1.onActivityResult(RESULT_LOAD_IMG1, data);

        }else if (requestCode == REQUEST_IMAGE_CAPTURE2 && resultCode == RESULT_OK) {

            imagePresenter2.onActivityResult(REQUEST_IMAGE_CAPTURE2, data);

        }else if(requestCode == RESULT_LOAD_IMG2 && resultCode == RESULT_OK) {

            imagePresenter2.onActivityResult(RESULT_LOAD_IMG2, data);

        }

    }
}
