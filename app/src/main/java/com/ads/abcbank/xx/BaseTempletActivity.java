package com.ads.abcbank.xx;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ads.abcbank.view.BaseActivity;

public abstract class BaseTempletActivity extends AppCompatActivity {

    protected final String TAG = BaseActivity.class.getSimpleName();

    protected ProgressDialog mProgressDialog;
    protected AppCompatActivity activity;
    protected Toast toast = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getLayoutResourceId());

        initCtrls(savedInstanceState);
    }

    protected abstract void initCtrls(Bundle savedInstanceState);
    protected abstract int getLayoutResourceId();
}
