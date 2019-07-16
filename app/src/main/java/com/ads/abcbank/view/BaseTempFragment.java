package com.ads.abcbank.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.fragment.ImageFragment;
import com.ads.abcbank.fragment.PdfFragment;
import com.ads.abcbank.fragment.TxtFragment;
import com.ads.abcbank.fragment.VideoFragment;
import com.ads.abcbank.fragment.WebFragment;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.QRCodeUtil;
import com.ads.abcbank.utils.Utils;

import java.util.List;

public abstract class BaseTempFragment extends Fragment {
    public Activity mActivity;

    private LinearLayout llQr1;
    private LinearLayout llQr2;
    private LinearLayout llQr3;
    private LinearLayout llQr4;
    private LinearLayout llQr5;
    private LinearLayout llQr6;
    private LinearLayout llQr7;
    private LinearLayout llQr8;
    private LinearLayout llQr9;
    public static TempView tempView;
    public static TempView2 tempView2;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = getActivity();
    }

    //根部view
    private View rootView;
    protected Context context;
    private Boolean hasInitData = false;
    public boolean isVisiable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = getActivity();
        if (rootView == null) {
            rootView = initView(inflater);
        }
        llQr1 = rootView.findViewById(R.id.ll_qr1);
        llQr2 = rootView.findViewById(R.id.ll_qr2);
        llQr3 = rootView.findViewById(R.id.ll_qr3);
        llQr4 = rootView.findViewById(R.id.ll_qr4);
        llQr5 = rootView.findViewById(R.id.ll_qr5);
        llQr6 = rootView.findViewById(R.id.ll_qr6);
        llQr7 = rootView.findViewById(R.id.ll_qr7);
        llQr8 = rootView.findViewById(R.id.ll_qr8);
        llQr9 = rootView.findViewById(R.id.ll_qr9);
        if (bodyBean != null) {
            showQRs(bodyBean);
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isVisiable && !hasInitData) {
            initData();
            hasInitData = true;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null && rootView.getParent() != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    /**
     * 子类实现初始化View操作
     */
    protected abstract View initView(LayoutInflater inflater);

    /**
     * 子类实现初始化数据操作(子类自己调用)
     */
    public abstract void initData();

    /**
     * 子类实现赋值数据操作(子类自己调用)
     */
    public abstract void setBean(PlaylistBodyBean bean);

    public abstract PlaylistBodyBean getBean();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isVisiable = getUserVisibleHint();
        if (rootView != null && isVisiable && !hasInitData) {
            try {
                initData();
                hasInitData = true;
            } catch (Exception e) {
                Logger.e(e.toString());
            }
        }
    }

    private PlaylistBodyBean bodyBean;

    public void showQRs(PlaylistBodyBean bean) {
        this.bodyBean = bean;
        if (llQr1 == null){
            return;
        }
        if (bean != null && bean.QRCode != null && bean.QRCode.size() > 0) {
            llQr1.setVisibility(View.INVISIBLE);
            llQr2.setVisibility(View.INVISIBLE);
            llQr3.setVisibility(View.INVISIBLE);
            llQr4.setVisibility(View.INVISIBLE);
            llQr5.setVisibility(View.INVISIBLE);
            llQr6.setVisibility(View.INVISIBLE);
            llQr7.setVisibility(View.INVISIBLE);
            llQr8.setVisibility(View.INVISIBLE);
            llQr9.setVisibility(View.INVISIBLE);
            PlaylistBodyBean.QR qr = bean.QRCode.get(0);
            switch (qr.QRPosInDial) {
                case "1":
                    llQr1.setVisibility(View.VISIBLE);
                    addQRs(llQr1, bean.QRCode);
                    break;
                case "2":
                    llQr2.setVisibility(View.VISIBLE);
                    addQRs(llQr2, bean.QRCode);
                    break;
                case "3":
                    llQr3.setVisibility(View.VISIBLE);
                    addQRs(llQr3, bean.QRCode);
                    break;
                case "4":
                    llQr4.setVisibility(View.VISIBLE);
                    addQRs(llQr4, bean.QRCode);
                    break;
                case "5":
                    llQr5.setVisibility(View.VISIBLE);
                    addQRs(llQr5, bean.QRCode);
                    break;
                case "6":
                    llQr6.setVisibility(View.VISIBLE);
                    addQRs(llQr6, bean.QRCode);
                    break;
                case "7":
                    llQr7.setVisibility(View.VISIBLE);
                    addQRs(llQr7, bean.QRCode);
                    break;
                case "8":
                    llQr8.setVisibility(View.VISIBLE);
                    addQRs(llQr8, bean.QRCode);
                    break;
                case "9":
                    llQr9.setVisibility(View.VISIBLE);
                    addQRs(llQr9, bean.QRCode);
                    break;
                default:
                    break;
            }
        } else {
            llQr1.setVisibility(View.INVISIBLE);
            llQr2.setVisibility(View.INVISIBLE);
            llQr3.setVisibility(View.INVISIBLE);
            llQr4.setVisibility(View.INVISIBLE);
            llQr5.setVisibility(View.INVISIBLE);
            llQr6.setVisibility(View.INVISIBLE);
            llQr7.setVisibility(View.INVISIBLE);
            llQr8.setVisibility(View.INVISIBLE);
            llQr9.setVisibility(View.INVISIBLE);
        }
    }

    private void addQRs(LinearLayout qrLayout, List<PlaylistBodyBean.QR> qrs) {
        qrLayout.removeAllViews();
        for (int i = 0; i < qrs.size(); i++) {
            PlaylistBodyBean.QR qr = qrs.get(i);
            View view = LayoutInflater.from(context).inflate(R.layout.item_qr, null);
            ImageView iv = view.findViewById(R.id.iv);
            TextView tips = view.findViewById(R.id.tips);

            Bitmap logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.movie);
            if (Utils.getRegisterBean(context) != null) {
                Bitmap qrCodeBitmap = QRCodeUtil.createQRCodeBitmap(qr.QRLink.replace("$storeId", Utils.getRegisterBean(context).data.storeId)
                                .replace("$terminalId", Utils.getRegisterBean(context).terminalId), 130,
                        "UTF-8", "H", "0", Color.BLACK, Color.WHITE,
                        null, logoBitmap, 0.2F);
                iv.setImageBitmap(qrCodeBitmap);
                qrCodeBitmap.recycle();
            } else {
                Bitmap qrCodeBitmap = QRCodeUtil.createQRCodeBitmap(qr.QRLink, 130,
                        "UTF-8", "H", "0", Color.BLACK, Color.WHITE,
                        null, logoBitmap, 0.2F);
                iv.setImageBitmap(qrCodeBitmap);
                qrCodeBitmap.recycle();
            }
            logoBitmap.recycle();
            if (i==0) {
                iv.setImageResource(R.mipmap.abcqrcode);
            } else  if (i==1){
                iv.setImageResource(R.mipmap.abcqrcode1);
            }
            tips.setText(qr.QRTip);
            LinearLayout layout = new LinearLayout(context);
            layout.addView(view);

            qrLayout.addView(layout);
        }
    }

    public void setTempView(TempView tempView) {
        BaseTempFragment.tempView = tempView;
    }
    public void setTempView2(TempView2 tempView) {
        BaseTempFragment.tempView2 = tempView;
    }
    public static BaseTempFragment newInstance(BaseTempFragment baseTempFragment) {
        if (baseTempFragment instanceof ImageFragment) {
            ImageFragment fragment = new ImageFragment();
            fragment.setBean(baseTempFragment.getBean());
            return fragment;
        } else if (baseTempFragment instanceof WebFragment) {
            WebFragment fragment = new WebFragment();
            fragment.setBean(baseTempFragment.getBean());
            return fragment;
        } else if (baseTempFragment instanceof VideoFragment) {
            VideoFragment fragment = new VideoFragment();
            fragment.setBean(baseTempFragment.getBean());
            return fragment;
        } else if (baseTempFragment instanceof PdfFragment) {
            PdfFragment fragment = new PdfFragment();
            fragment.setBean(baseTempFragment.getBean());
            return fragment;
//        } else if (baseTempFragment instanceof TxtFragment) {
//            TxtFragment fragment = new TxtFragment();
//            fragment.setBean(baseTempFragment.getBean());
//            return fragment;
        }
        WebFragment fragment = new WebFragment();
        fragment.setBean(baseTempFragment.getBean());
        return fragment;
    }
}
