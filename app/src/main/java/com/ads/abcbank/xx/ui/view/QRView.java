package com.ads.abcbank.xx.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.utils.QRCodeUtil;
import com.ads.abcbank.utils.Utils;

import java.util.List;

public class QRView extends LinearLayout {
    static String TAG = "SliderPlayer";

    Context context;
//    PlaylistBodyBean bodyBean;

    LinearLayout llQr1;
    LinearLayout llQr2;
    LinearLayout llQr3;
    LinearLayout llQr4;
    LinearLayout llQr5;
    LinearLayout llQr6;
    LinearLayout llQr7;
    LinearLayout llQr8;
    LinearLayout llQr9;

    public QRView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        initCtrls();
    }

    private void initCtrls() {
        View v = LayoutInflater.from(context).inflate(R.layout.widget_ui_slider_holder_qr, this, true);
        llQr1 = findViewById(R.id.ll_qr1);
        llQr2 = findViewById(R.id.ll_qr2);
        llQr3 = findViewById(R.id.ll_qr3);
        llQr4 = findViewById(R.id.ll_qr4);
        llQr5 = findViewById(R.id.ll_qr5);
        llQr6 = findViewById(R.id.ll_qr6);
        llQr7 = findViewById(R.id.ll_qr7);
        llQr8 = findViewById(R.id.ll_qr8);
        llQr9 = findViewById(R.id.ll_qr9);
    }

    public void showQRs(List<PlaylistBodyBean.QR> QRCode) {
//        this.bodyBean = bean;
        if (llQr1 == null){
            return;
        }
        if (QRCode != null && QRCode.size() > 0) {
            llQr1.setVisibility(View.GONE);
            llQr2.setVisibility(View.GONE);
            llQr3.setVisibility(View.GONE);
            llQr4.setVisibility(View.GONE);
            llQr5.setVisibility(View.GONE);
            llQr6.setVisibility(View.GONE);
            llQr7.setVisibility(View.GONE);
            llQr8.setVisibility(View.GONE);
            llQr9.setVisibility(View.GONE);
            PlaylistBodyBean.QR qr = QRCode.get(0);
            switch (qr.QRPosInDial) {
                case "1":
                    llQr1.setVisibility(View.VISIBLE);
                    addQRs(llQr1, QRCode);
                    break;
                case "2":
                    llQr2.setVisibility(View.VISIBLE);
                    addQRs(llQr2, QRCode);
                    break;
                case "3":
                    llQr3.setVisibility(View.VISIBLE);
                    addQRs(llQr3, QRCode);
                    break;
                case "4":
                    llQr4.setVisibility(View.VISIBLE);
                    addQRs(llQr4, QRCode);
                    break;
                case "5":
                    llQr5.setVisibility(View.VISIBLE);
                    addQRs(llQr5, QRCode);
                    break;
                case "6":
                    llQr6.setVisibility(View.VISIBLE);
                    addQRs(llQr6, QRCode);
                    break;
                case "7":
                    llQr7.setVisibility(View.VISIBLE);
                    addQRs(llQr7, QRCode);
                    break;
                case "8":
                    llQr8.setVisibility(View.VISIBLE);
                    addQRs(llQr8, QRCode);
                    break;
                case "9":
                    llQr9.setVisibility(View.VISIBLE);
                    addQRs(llQr9, QRCode);
                    break;
                default:
                    break;
            }
        } else {
            llQr1.setVisibility(View.GONE);
            llQr2.setVisibility(View.GONE);
            llQr3.setVisibility(View.GONE);
            llQr4.setVisibility(View.GONE);
            llQr5.setVisibility(View.GONE);
            llQr6.setVisibility(View.GONE);
            llQr7.setVisibility(View.GONE);
            llQr8.setVisibility(View.GONE);
            llQr9.setVisibility(View.GONE);
        }
    }

    private void addQRs(LinearLayout qrLayout, List<PlaylistBodyBean.QR> qrs) {
        qrLayout.removeAllViews();
        for (int i = 0; i < qrs.size(); i++) {
            PlaylistBodyBean.QR qr = qrs.get(i);
            View view = LayoutInflater.from(context).inflate(R.layout.item_qr, null);

            ImageView iv = view.findViewById(R.id.iv);
            TextView tips = view.findViewById(R.id.tips);

//            Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            Bitmap logoBitmap = null;
            if (Utils.getRegisterBean(context) != null) {
                Bitmap qrCodeBitmap = QRCodeUtil.createQRCodeBitmap(qr.QRLink.replace("$storeId", Utils.getRegisterBean(context).data.storeId)
                                .replace("$terminalId", Utils.getRegisterBean(context).terminalId), 130,
                        "UTF-8", "H", "0", Color.BLACK, Color.WHITE,
                        null, logoBitmap, 0.2F);
                iv.setImageBitmap(qrCodeBitmap);
            } else {
                Bitmap qrCodeBitmap = QRCodeUtil.createQRCodeBitmap(qr.QRLink, 130,
                        "UTF-8", "H", "0", Color.BLACK, Color.WHITE,
                        null, logoBitmap, 0.2F);
                iv.setImageBitmap(qrCodeBitmap);
            }
//            if (i==0) {
//                iv.setImageResource(R.mipmap.abcqrcode);
//            } else  if (i==1){
//                iv.setImageResource(R.mipmap.abcqrcode1);
//            }
            tips.setText(qr.QRTip);
//            LinearLayout layout = new LinearLayout(context);
//            layout.addView(view);

            qrLayout.addView(view);
        }
    }
}
