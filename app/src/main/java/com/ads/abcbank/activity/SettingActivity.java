package com.ads.abcbank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.service.DownloadService;
import com.ads.abcbank.utils.ToastUtil;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseActivity;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout llRoot;
    private EditText etCmd;
    private EditText etPreset;
    private EditText etPlaylist;
    private EditText etTabImg;
    private EditText etTabPreset;
    private EditText etPdf;
    private EditText etFile;
    private EditText et_downloadspeed;
    private TextView tvSubmit;
    private TextView back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initViews();
        initDatas();
    }


    private void initViews() {
        llRoot = (LinearLayout) findViewById(R.id.ll_root);
        etCmd = (EditText) findViewById(R.id.et_cmd);
        etPreset = (EditText) findViewById(R.id.et_preset);
        etPlaylist = (EditText) findViewById(R.id.et_playlist);
        etTabImg = (EditText) findViewById(R.id.et_tab_img);
        etTabPreset = (EditText) findViewById(R.id.et_tab_preset);
        etPdf = (EditText) findViewById(R.id.et_pdf);
        etFile = (EditText) findViewById(R.id.et_file);
        et_downloadspeed = (EditText) findViewById(R.id.et_downloadspeed);
        tvSubmit = (TextView) findViewById(R.id.tv_submit);
        back = (TextView) findViewById(R.id.back);
    }

    private void initDatas() {
        tvSubmit.setOnClickListener(this);
        back.setOnClickListener(this);
        etCmd.setText(Utils.get(this, Utils.KEY_TIME_CMD, "5").toString());
        etPlaylist.setText(Utils.get(this, Utils.KEY_TIME_PLAYLIST, "20").toString());
        etPreset.setText(Utils.get(this, Utils.KEY_TIME_PRESET, "30").toString());
        etTabImg.setText(Utils.get(this, Utils.KEY_TIME_TAB_IMG, "5").toString());
        etTabPreset.setText(Utils.get(this, Utils.KEY_TIME_TAB_PRESET, "5").toString());
        etPdf.setText(Utils.get(this, Utils.KEY_TIME_TAB_PDF, "5").toString());
        etFile.setText(Utils.get(this, Utils.KEY_TIME_FILE, "30").toString());
        et_downloadspeed.setText(Utils.get(this, Utils.KEY_SPEED_DOWNLOAD, "50").toString());
        etCmd.setSelection(etCmd.getText().toString().length());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.tv_submit:
                if (!TextUtils.isEmpty(etCmd.getText().toString())) {
                    Utils.put(this, Utils.KEY_TIME_CMD, etCmd.getText().toString());
                } else {
                    ToastUtil.showToast(this, "数据不能为空");
                }
                if (!TextUtils.isEmpty(etPlaylist.getText().toString())) {
                    Utils.put(this, Utils.KEY_TIME_PLAYLIST, etPlaylist.getText().toString());
                } else {
                    ToastUtil.showToast(this, "数据不能为空");
                }
                if (!TextUtils.isEmpty(etPreset.getText().toString())) {
                    Utils.put(this, Utils.KEY_TIME_PRESET, etPreset.getText().toString());
                } else {
                    ToastUtil.showToast(this, "数据不能为空");
                }
                if (!TextUtils.isEmpty(etTabPreset.getText().toString())) {
                    Utils.put(this, Utils.KEY_TIME_TAB_PRESET, etTabPreset.getText().toString());
                } else {
                    ToastUtil.showToast(this, "数据不能为空");
                }
                if (!TextUtils.isEmpty(etTabImg.getText().toString())) {
                    Utils.put(this, Utils.KEY_TIME_TAB_IMG, etTabImg.getText().toString());
                } else {
                    ToastUtil.showToast(this, "数据不能为空");
                }
                if (!TextUtils.isEmpty(etPdf.getText().toString())) {
                    Utils.put(this, Utils.KEY_TIME_TAB_PDF, etPdf.getText().toString());
                } else {
                    ToastUtil.showToast(this, "数据不能为空");
                }
                if (!TextUtils.isEmpty(etFile.getText().toString())) {
                    Utils.put(this, Utils.KEY_TIME_FILE, etFile.getText().toString());
                } else {
                    ToastUtil.showToast(this, "数据不能为空");
                }
                if (!TextUtils.isEmpty(et_downloadspeed.getText().toString())) {
                    Utils.put(this, Utils.KEY_SPEED_DOWNLOAD, et_downloadspeed.getText().toString());
                } else {
                    ToastUtil.showToast(this, "数据不能为空");
                }
                finish();
                break;
        }
    }


}
