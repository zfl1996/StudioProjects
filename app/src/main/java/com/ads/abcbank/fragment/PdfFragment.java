package com.ads.abcbank.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.utils.ActivityManager;
import com.ads.abcbank.utils.Utils;
import com.ads.abcbank.view.BaseTempFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfFragment extends BaseTempFragment {
    private View view;
    private ImageView content;
    private static PlaylistBodyBean bean;
    private static final String STATE_CURRENT_PAGE_INDEX = "current_page_index";

    //    private static final String FILENAME = "sample.pdf";
    private static final String FILENAME = "1.pdf";

    private ParcelFileDescriptor mFileDescriptor;

    private PdfRenderer mPdfRenderer;

    private PdfRenderer.Page mCurrentPage;


    private Integer pageNumber = 0;
    private Integer pageTotal = -1;

    public PdfFragment() {
    }

    @Override
    protected View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_pdf, null);
        getViews();
        return view;
    }

    private void getViews() {
        content = view.findViewById(R.id.content);

    }

    @Override
    public void initData() {
    }

    /**
     * Sets up a {@link android.graphics.pdf.PdfRenderer} and related resources.
     */
    private void openRenderer(Context context) throws IOException {
        File file = new File(context.getCacheDir(), FILENAME);
        if (!file.exists()) {
            InputStream asset = context.getAssets().open(FILENAME);
            FileOutputStream output = new FileOutputStream(file);
            final byte[] buffer = new byte[1024];
            int size;
            while ((size = asset.read(buffer)) != -1) {
                output.write(buffer, 0, size);
            }
            asset.close();
            output.close();
        }
        mFileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
        if (mFileDescriptor != null) {
            mPdfRenderer = new PdfRenderer(mFileDescriptor);
            pageTotal = mPdfRenderer.getPageCount();
            handler.postDelayed(runnable, delayTime);
        }
    }

    private void closeRenderer() throws IOException {
        if (null != mCurrentPage) {
            try {
                mCurrentPage.close();
            } catch (Exception e) {
            }
        }
        if (mPdfRenderer != null) {
            try {
                mPdfRenderer.close();
            } catch (Exception e) {
            }
        }
        if (mFileDescriptor != null) {
            try {
                mFileDescriptor.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * Shows the specified page of PDF to the screen.
     *
     * @param index The page index.
     */

    private void showPage(int index) {
        if (mPdfRenderer.getPageCount() <= index) {
            return;
        }
        if (null != mCurrentPage) {
            try {
                mCurrentPage.close();
            } catch (Exception e) {
                try {
                    handler.removeCallbacks(runnable);
                    openRenderer(getActivity());
                    showPage(index);
                } catch (Exception e1) {
                }
            }
        }
        pageNumber = index;
        mCurrentPage = mPdfRenderer.openPage(index);
        Bitmap bitmap = Bitmap.createBitmap(mCurrentPage.getWidth(), mCurrentPage.getHeight(),
                Bitmap.Config.ARGB_8888);
        mCurrentPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        content.setImageBitmap(bitmap);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (pageTotal > 0) {
                handler.postDelayed(runnable, delayTime);
            } else {
                pageNumber = 0;
                if (getActivity() != null) {
                    try {
                        openRenderer(getActivity());
                        showPage(pageNumber);
                    } catch (Exception e) {
                    }
                }
            }
        } else {
            if (handler != null && runnable != null && content != null && pageTotal > 0) {
                handler.removeCallbacks(runnable);
            }
        }
    }

    private long delayTime = 5000;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                delayTime = Integer.
                        parseInt(Utils.
                                get(getActivity(), Utils.KEY_TIME_TAB_PDF, "5")
                                .toString()) * 1000;
            } catch (Exception e) {
            }
            if (pageNumber < pageTotal - 1) {
                showPage(pageNumber + 1);
                handler.postDelayed(runnable, delayTime);
            } else {
                pageNumber = 0;
                try {
                    closeRenderer();
                } catch (Exception e) {
                }
                pageTotal = -1;
                if (tempView != null && ActivityManager.getInstance().getTopActivity() == tempView.getContext())
                    tempView.nextPlay();
                else
                    handler.postDelayed(runnable, delayTime);
            }
        }
    };

    @Override
    public void setBean(PlaylistBodyBean bean) {
        this.bean = bean;
        initData();
        showQRs(bean);
    }

    @Override
    public PlaylistBodyBean getBean() {
        return bean;
    }

}