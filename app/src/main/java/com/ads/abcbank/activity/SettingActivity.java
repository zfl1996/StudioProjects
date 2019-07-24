package com.ads.abcbank.activity;

import android.os.Bundle;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.RegisterBean;
import com.ads.abcbank.utils.ToastUtil;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseActivity;
import com.ads.abcbank.view.KeyboardWindow;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class SettingActivity extends BaseActivity implements View.OnClickListener {
    private LinearLayout llRoot;
    private EditText etCmd;
    private EditText etPreset;
    private EditText etPlaylist;
    private EditText etTabImg;
    private EditText etTabPreset;
    private EditText etPdf;
    private EditText etFile;
    private EditText etDownloadspeed;
    private EditText storeId;
    private TextView tvSubmit;
    private TextView back;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initViews();
        initDatas();
    }

    private KeyboardWindow keyboardWindow;

    private void initViews() {
        llRoot = findViewById(R.id.ll_root);
        etCmd = findViewById(R.id.et_cmd);
        etPreset = findViewById(R.id.et_preset);
        etPlaylist = findViewById(R.id.et_playlist);
        etTabImg = findViewById(R.id.et_tab_img);
        etTabPreset = findViewById(R.id.et_tab_preset);
        etPdf = findViewById(R.id.et_pdf);
        etFile = findViewById(R.id.et_file);
        etDownloadspeed = findViewById(R.id.et_downloadspeed);
        tvSubmit = findViewById(R.id.tv_submit);
        back = findViewById(R.id.back);
        storeId = findViewById(R.id.storeId);
        {
            etCmd.setEnabled(false);
            etPreset.setEnabled(false);
            etPlaylist.setEnabled(false);
            etTabImg.setEnabled(false);
            etTabPreset.setEnabled(false);
            etPdf.setEnabled(false);
            etDownloadspeed.setEnabled(false);
        }
        addListener(etCmd, true);
        addListener(etPreset, true);
        addListener(etPlaylist, true);
        addListener(etTabImg, true);
        addListener(etTabPreset, true);
        addListener(etPdf, true);
        addListener(etFile, true);
        addListener(etDownloadspeed, true);
        addListener(storeId, true);
        String beanStr = Utils.get(this, Utils.KEY_REGISTER_BEAN, "").toString();
        if (!TextUtils.isEmpty(beanStr)) {
            RegisterBean bean = JSON.parseObject(beanStr, RegisterBean.class);
            storeId.setText(bean.data.storeId);
        }
    }

    @SuppressWarnings("ALL")
    private void addListener(EditText editText, boolean isNum) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (keyboardWindow != null) {
                        keyboardWindow.dismiss();
                    }
                    keyboardWindow = new KeyboardWindow(SettingActivity.this, editText, isNum);
                    keyboardWindow.show();
                    editText.requestFocus();
                }

                int inType = editText.getInputType();
                editText.setInputType(InputType.TYPE_NULL);
                editText.onTouchEvent(event);
                editText.setInputType(inType);
                CharSequence text = editText.getText();
                if (text instanceof Spannable) {
                    Spannable spanText = (Spannable) text;
                    Selection.setSelection(spanText, text.length());
                }
                return false;
            }
        });
        editText.setMovementMethod(ScrollingMovementMethod.getInstance());
        editText.setSelection(editText.getText().length(), editText.getText().length());
    }

    private void initDatas() {
        tvSubmit.setOnClickListener(this);
        back.setOnClickListener(this);
        etCmd.setText(Utils.get(this, Utils.KEY_TIME_CMD, Utils.KEY_TIME_CMD_TIME + "").toString());
        etPlaylist.setText(Utils.get(this, Utils.KEY_TIME_PLAYLIST, Utils.KEY_TIME_PLAYLIST_TIME + "").toString());
        etPreset.setText(Utils.get(this, Utils.KEY_TIME_PRESET, Utils.KEY_TIME_PRESET_TIME + "").toString());
        etTabImg.setText(Utils.get(this, Utils.KEY_TIME_TAB_IMG, Utils.KEY_TIME_IMG_DEFAULT + "").toString());
        etTabPreset.setText(Utils.get(this, Utils.KEY_TIME_TAB_PRESET, Utils.KEY_TIME_PRESET_DEFAULT + "").toString());
        etPdf.setText(Utils.get(this, Utils.KEY_TIME_TAB_PDF, Utils.KEY_TIME_IMG_DEFAULT + "").toString());
        etFile.setText(Utils.get(this, Utils.KEY_TIME_FILE, Utils.KEY_TIME_FILE_DEFAULT + "").toString());
        etDownloadspeed.setText(Utils.get(this, Utils.KEY_SPEED_DOWNLOAD, Utils.KEY_DOWNLOAD_SIZE + "").toString());
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
                if (!TextUtils.isEmpty(etDownloadspeed.getText().toString())) {
                    Utils.put(this, Utils.KEY_SPEED_DOWNLOAD, etDownloadspeed.getText().toString());
                } else {
                    ToastUtil.showToast(this, "数据不能为空");
                }
                if (TextUtils.isEmpty(storeId.getText().toString())) {
                    ToastUtil.showToast(this, "数据不能为空");
                    return;
                }
                String beanStr = Utils.get(this, Utils.KEY_REGISTER_BEAN, "").toString();
                if (!TextUtils.isEmpty(beanStr)) {
                    RegisterBean bean = JSON.parseObject(beanStr, RegisterBean.class);
                    bean.data.storeId = storeId.getText().toString();
                    Utils.put(this, Utils.KEY_REGISTER_BEAN, JSONObject.toJSONString(bean));
                }
                finish();
                break;
            default:
                break;
        }
    }


}
