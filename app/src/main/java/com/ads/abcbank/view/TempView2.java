package com.ads.abcbank.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ads.abcbank.R;
import com.ads.abcbank.bean.DownloadBean;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.bean.PlaylistResultBean;
import com.ads.abcbank.fragment.ImageFragment;
import com.ads.abcbank.fragment.PdfFragment;
import com.ads.abcbank.fragment.TxtFragment;
import com.ads.abcbank.fragment.VideoFragment;
import com.ads.abcbank.fragment.WebFragment;
import com.ads.abcbank.service.DownloadService;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.alibaba.fastjson.JSON;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @date 2019/5/19
 */

public class TempView2 extends LinearLayout {
    private Context context;
    private String type;
    private ViewPager viewpager;
    private List<BaseTempFragment> fragmentList = new ArrayList<>();
    private List<PlaylistBodyBean> playlistBean;
    private ImageView image;
    private boolean needUpdate;
    private WillPagerAdapter willPagerAdapter;

    public boolean isNeedUpdate() {
        return needUpdate;
    }

    public void setNeedUpdate(boolean needUpdate) {
        this.needUpdate = needUpdate;
    }

    public ImageView getImage() {
        return image;
    }

    public TempView2(Context context) {
        this(context, null);
    }

    public TempView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TempView2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();
    }

    private synchronized void initView() {
        if (image == null || viewpager == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.view_temp2, null);
            image = view.findViewById(R.id.image);
            viewpager = view.findViewById(R.id.viewpager_temp);

            Utils.loadImage(image, "");
            addView(view);
        }
        String json = Utils.get(context, Utils.KEY_PLAY_LIST, "").toString();
        if (!TextUtils.isEmpty(json)) {
            playlistBean = JSON.parseArray(json, PlaylistBodyBean.class);
        }
    }

    public void setImageSrc(int src) {
        image.setImageResource(src);
    }

    public synchronized void setType(String type) {
        this.type = type;
        if (image == null || viewpager == null) {
            initView();
        }
        addTempViewList();
        if (fragmentList.size() == 0) {
            ImageFragment imageFragment = new ImageFragment();
            imageFragment.setTempView2(this);
            fragmentList.add(imageFragment);
        }
        willPagerAdapter = new WillPagerAdapter(((AppCompatActivity) context).getSupportFragmentManager(), fragmentList);
        if (viewpager != null) {
            viewpager.setAdapter(willPagerAdapter);
            viewpager.setCurrentItem(0);
            try {
                willPagerAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Logger.e(e.toString());
            }
        }
    }

    private void addPlayList(List<PlaylistBodyBean> bodyBeans) {
        for (int i = 0; i < bodyBeans.size(); i++) {
            PlaylistBodyBean bodyBean = bodyBeans.get(i);
            if (Utils.isInPlayTime(bodyBean)) {
                String contentTypeMiddle = Utils.getContentTypeMiddle(context);
                String contentTypeEnd = Utils.getContentTypeEnd(context);
                if ("*".equals(contentTypeEnd)) {
                    if (bodyBean.contentType.substring(1, 2).equals(contentTypeMiddle) &&
                            type.contains(bodyBean.contentType.substring(0, 1))) {
                        String suffix = bodyBean.name.substring(bodyBean.name.lastIndexOf(".") + 1).toLowerCase();
                        BaseTempFragment fragment;
                        switch (suffix) {
                            case "mp4":
                            case "mkv":
                            case "wmv":
                            case "avi":
                            case "rmvb":
                                fragment = new VideoFragment();
                                break;
                            case "jpg":
                            case "png":
                            case "bmp":
                            case "jpeg":
                                fragment = new ImageFragment();
                                break;
                            case "pdf":
                                fragment = new PdfFragment();
                                break;
//                            case "txt":
//                                fragment = new TxtFragment();
//                                break;
                            default:
                                fragment = new WebFragment();
                                break;
                        }
                        fragment.setBean(bodyBean);
                        fragment.setTempView2(this);
                        //TODO 此处需添加文件是否已下载完成的判断
                        if (isDownloadFinished(bodyBean)) {
                            fragmentList.add(fragment);
                        }
                    }
                } else {
                    if (bodyBean.contentType.endsWith(contentTypeEnd) &&
                            bodyBean.contentType.substring(1, 2).equals(contentTypeMiddle) &&
                            type.contains(bodyBean.contentType.substring(0, 1))) {
                        String suffix = bodyBean.name.substring(bodyBean.name.lastIndexOf(".") + 1).toLowerCase();
                        BaseTempFragment fragment;
                        switch (suffix) {
                            case "mp4":
                            case "mkv":
                            case "wmv":
                            case "avi":
                            case "rmvb":
                                fragment = new VideoFragment();
                                break;
                            case "jpg":
                            case "png":
                            case "bmp":
                            case "jpeg":
                                fragment = new ImageFragment();
                                break;
                            case "pdf":
                                fragment = new PdfFragment();
                                break;
//                            case "txt":
//                                fragment = new TxtFragment();
//                                break;
                            default:
                                fragment = new WebFragment();
                                break;
                        }
                        fragment.setBean(bodyBean);
                        fragment.setTempView2(this);
                        //TODO 此处需添加文件是否已下载完成的判断
                        if (isDownloadFinished(bodyBean)) {
                            fragmentList.add(fragment);
                        }
                    }
                }
            }
        }
    }

    private boolean isDownloadFinished(PlaylistBodyBean bodyBean) {
        for (int j = 0; j < getDownloadItems().size(); j++) {
            if (getDownloadItems().get(j).id.equals(bodyBean.id)) {
                try {
                    if ("finish".equals(getDownloadItems().get(j).status)||"COMPLETED".equals(getDownloadItems().get(j).status)) {
                        return true;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }

    private List<DownloadBean> getDownloadItems() {
        return DownloadService.getPlaylistBean().data.items;
    }

    private synchronized void addTempViewList() {
        if (playlistBean == null) {
            return;
        }
        fragmentList.clear();

        List<PlaylistBodyBean> hotLists = new ArrayList<>();
        List<PlaylistBodyBean> normalLists = new ArrayList<>();
        for (int i = 0; i < playlistBean.size(); i++) {
            if ("1".equals(playlistBean.get(i).isUrg)) {
                hotLists.add(playlistBean.get(i));
            }
        }
        addPlayList(hotLists);
        if (fragmentList.size() > 0) {
            try {
                willPagerAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Logger.e(e.toString());
            }
            return;
        }
        for (int i = 0; i < playlistBean.size(); i++) {
            if (!"1".equals(playlistBean.get(i).isUrg)) {
                normalLists.add(playlistBean.get(i));
            }
        }
        addPlayList(normalLists);
    }

    public void nextPlay() {
        if (isNeedUpdate()) {
            setNeedUpdate(false);
            fragmentList.clear();
            String json = Utils.get(context, Utils.KEY_PLAY_LIST, "").toString();
            try {
                if (!TextUtils.isEmpty(json)) {
                    playlistBean = JSON.parseArray(json, PlaylistBodyBean.class);
                }
                setType(type);
            } catch (Exception e) {
                Logger.e(e.toString());
                Logger.e("出错，播放列表记录："+json);
                nextPlay();
            }
        } else {
            int current = viewpager.getCurrentItem();
            int next;
            if (current < fragmentList.size() - 1) {
                next = current + 1;
            } else {
                next = 0;
            }
            if (Utils.isInPlayTime((fragmentList.get(next)).getBean())) {
                viewpager.setCurrentItem(next);
            } else {
                fragmentList.clear();
                String json = Utils.get(context, Utils.KEY_PLAY_LIST, "").toString();
                if (!TextUtils.isEmpty(json)) {
                    playlistBean = JSON.parseArray(json, PlaylistBodyBean.class);
                }
                setType(type);
            }
        }
    }

    public class WillPagerAdapter extends FragmentPagerAdapter {
        // SparseArray是Hashmap的改良品，其核心是折半查找函数（binarySearch）
        SparseArray<WeakReference<Fragment>> registeredFragments = new SparseArray<WeakReference<Fragment>>();
        private List<BaseTempFragment> mList;


        public WillPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public WillPagerAdapter(FragmentManager fm, List<BaseTempFragment> list) {
            this(fm);
            // TODO Auto-generated constructor stub
            mList = list;
        }

        /*
         * 生成新的 Fragment 对象。 .instantiateItem() 在大多数情况下，都将调用 getItem() 来生成新的对象
         */
        @Override
        public Fragment getItem(int position) {
            // TODO Auto-generated method stub
            BaseTempFragment fragment = null;
            try {
                fragment = BaseTempFragment.newInstance(mList.get(position));
            } catch (Exception e) {
                Logger.e(e.toString());
            }
            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            // 得到缓存的fragment
            BaseTempFragment fragment = (BaseTempFragment) super.instantiateItem(container,
                    position);
            WeakReference<Fragment> weak = new WeakReference<Fragment>(fragment);
            registeredFragments.put(position, weak);
            return fragment;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            registeredFragments.remove(position);

            super.destroyItem(container, position, object);

        }

        /**
         * 要求getItemPosition、FragmentStatePagerAdapter
         */
        public void remove(int position) {
            mList.remove(position);
            notifyDataSetChanged();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public BaseTempFragment getRegisteredFragment(int position) {
            return (BaseTempFragment) registeredFragments.get(position).get();
        }
    }
}
