package com.ads.abcbank.presenter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.TempView;

/**
 * @author ynet
 * @brief
 * @date 2019/5/4
 */

public class TempPresenter {
    private TempView tempView;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (Utils.mProgressDialog != null) {
                Utils.mProgressDialog.dismiss();
            }
            switch (msg.what) {
            }
        }
    };
}
