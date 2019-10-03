package com.hgtech.sageoriapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ReturnActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.return_layout);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("회수관리");
        setSupportActionBar(toolbar);
    }
}
