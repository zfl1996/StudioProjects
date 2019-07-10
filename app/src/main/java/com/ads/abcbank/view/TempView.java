package com.ads.abcbank.view;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ads.abcbank.R;
import com.ads.abcbank.activity.Temp2Activity;
import com.ads.abcbank.activity.Temp3Activity;
import com.ads.abcbank.activity.Temp5Activity;
import com.ads.abcbank.bean.DownloadBean;
import com.ads.abcbank.bean.PlaylistBodyBean;
import com.ads.abcbank.bean.PresetBean;
import com.ads.abcbank.fragment.ImageFragment;
import com.ads.abcbank.fragment.PdfFragment;
import com.ads.abcbank.fragment.Tab1Fragment;
import com.ads.abcbank.fragment.Tab2Fragment;
import com.ads.abcbank.fragment.Tab3Fragment;
import com.ads.abcbank.fragment.TxtFragment;
import com.ads.abcbank.fragment.VideoFragment;
import com.ads.abcbank.fragment.WebFragment;
import com.ads.abcbank.service.DownloadService;
import com.ads.abcbank.utils.Logger;
import com.ads.abcbank.utils.Utils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * @date 2019/5/19
 */

public class TempView extends LinearLayout {
    private Context context;
    private String type;
    private ViewPager viewpager;
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<PlaylistBodyBean> playlistBean;
    private ImageView image;
    private boolean needUpdate;
    private WillPagerAdapter willPagerAdapter;

    public boolean isNeedUpdate() {
        return needUpdate;
    }

    public void setNeedUpdate(boolean needUpdate) {
        this.needUpdate = needUpdate;
        if (needUpdate) {
            String json = Utils.get(context, Utils.KEY_PLAY_LIST, "").toString();
            try {
                if (!TextUtils.isEmpty(json)) {
                    playlistBean = JSON.parseArray(json, PlaylistBodyBean.class);
                }
            } catch (Exception e) {
                Logger.e(e.toString());
            }
        }
    }

    public ImageView getImage() {
        return image;
    }

    public TempView(Context context) {
        this(context, null);
    }

    public TempView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TempView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initView();
    }

    private synchronized void initView() {
        if (image == null || viewpager == null) {
            View view = LayoutInflater.from(context).inflate(R.layout.view_temp, null);
            image = view.findViewById(R.id.image);
            viewpager = view.findViewById(R.id.viewpager);
            Utils.loadImage(image, "");
            addView(view);
        }
        String json = Utils.get(context, Utils.KEY_PLAY_LIST, "").toString();
        if (!TextUtils.isEmpty(json)) {
            try {
                playlistBean = JSON.parseArray(json, PlaylistBodyBean.class);
            } catch (Exception e) {
                Logger.e("解析播放列表出错" + json);
            }
        }
    }

    public void setImageSrc(int src) {
        image.setImageResource(src);
    }

    public synchronized void setType(String type) {
        errFileSum = 0;
        presetSum = 0;
        this.type = type;
        if (image == null || viewpager == null) {
            initView();
        }
        int listType = addTempViewList();
        if (listType == 0) {
            Activity activity = (Activity) getContext();
            if (activity != null) {
                if (activity instanceof Temp2Activity || activity instanceof Temp3Activity
                        || activity instanceof Temp5Activity) {
                    Tab1Fragment tab1Fragment = new Tab1Fragment();
                    Tab2Fragment tab2Fragment = new Tab2Fragment();
                    Tab3Fragment tab3Fragment = new Tab3Fragment();
                    String json = Utils.get(context, Utils.KEY_PRESET, "").toString();
                    if (TextUtils.isEmpty(json)) {
                        return;
                    }
                    PresetBean bean = JSON.parseObject(json, PresetBean.class);
                    tab1Fragment.setBean(bean.data.saveRate);
                    tab2Fragment.setBean(bean.data.loanRate);
                    tab3Fragment.setBean(bean.data.buyInAndOutForeignExchange);
                    if (bean.data.saveRate.enable) {
                        tab1Fragment.setTempView(this);
                        fragmentList.add(tab1Fragment);
                        presetSum++;
                    }
                    if (bean.data.loanRate.enable) {
                        tab2Fragment.setTempView(this);
                        fragmentList.add(tab2Fragment);
                        presetSum++;
                    }
                    if (bean.data.buyInAndOutForeignExchange.enable) {
                        tab3Fragment.setTempView(this);
                        fragmentList.add(tab3Fragment);
                        presetSum++;
                    }
                }
            }
        }
        if (fragmentList.size() == 0) {
            ImageFragment imageFragment = new ImageFragment();
            imageFragment.setTempView(this);
            fragmentList.add(imageFragment);
        }
        reSetAdapter();
    }

    private int errFileSum = 0;
    private int presetSum = 0;

    private void reSetAdapter() {
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

    private String getPlayListStr(List<PlaylistBodyBean> bodyBeans) {
        if (bodyBeans == null) {
            return "";
        }
        StringBuffer playListStr = new StringBuffer();
        for (int i = 0; i < bodyBeans.size(); i++) {
            PlaylistBodyBean bodyBean = bodyBeans.get(i);
            if (Utils.isInPlayTime(bodyBean)) {
                String contentTypeMiddle = Utils.getContentTypeMiddle(context);
                String contentTypeEnd = Utils.getContentTypeEnd(context);
                if ("*".equals(contentTypeEnd)) {
                    if (bodyBean.contentType.substring(1, 2).equals(contentTypeMiddle) &&
                            type.contains(bodyBean.contentType.substring(0, 1))) {
                        String suffix = bodyBean.name.substring(bodyBean.name.lastIndexOf(".") + 1).toLowerCase();
                        String downloadFileFath = "";
                        switch (suffix) {
                            case "mp4":
                            case "mkv":
                            case "wmv":
                            case "avi":
                            case "rmvb":
                                downloadFileFath = DownloadService.downloadVideoPath + bodyBean.name;
                                break;
                            case "jpg":
                            case "png":
                            case "bmp":
                            case "jpeg":
                                downloadFileFath = DownloadService.downloadImagePath + bodyBean.name;
                                break;
                            case "pdf":
                                downloadFileFath = DownloadService.downloadFilePath + bodyBean.name;
                                break;
                            default:
                                downloadFileFath = DownloadService.downloadPath + bodyBean.name;
                                break;
                        }
                        if (isDownloadFinished(bodyBean)) {
                            int currentItem = 0;
                            if (viewpager != null) {
                                currentItem = viewpager.getCurrentItem();
                            }
                            playListStr.append("{\tID:" + bodyBean.id + "\t");
                            playListStr.append("name:" + bodyBean.name + "\t");
                            playListStr.append("contentType:" + bodyBean.contentType + "\t");
                            playListStr.append("xelUrl:" + downloadFileFath + "\t");
                            playListStr.append("playTime:" + bodyBean.playDate + "-" + bodyBean.stopDate + "\t");
                            playListStr.append("isUrg:" + (bodyBean.isUrg == null ? "0" : bodyBean.isUrg) + "\t");
                            playListStr.append("playStatus:" + (i == currentItem ? "play" : "pause") + "\t}\t\n");
                        } else {
                        }
                    }
                } else {
                    if (bodyBean.contentType.endsWith(contentTypeEnd) &&
                            bodyBean.contentType.substring(1, 2).equals(contentTypeMiddle) &&
                            type.contains(bodyBean.contentType.substring(0, 1))) {
                        String suffix = bodyBean.name.substring(bodyBean.name.lastIndexOf(".") + 1).toLowerCase();
                        String downloadFileFath = "";
                        switch (suffix) {
                            case "mp4":
                            case "mkv":
                            case "wmv":
                            case "avi":
                            case "rmvb":
                                downloadFileFath = DownloadService.downloadVideoPath + bodyBean.name;
                                break;
                            case "jpg":
                            case "png":
                            case "bmp":
                            case "jpeg":
                                downloadFileFath = DownloadService.downloadImagePath + bodyBean.name;
                                break;
                            case "pdf":
                                downloadFileFath = DownloadService.downloadFilePath + bodyBean.name;
                                break;
                            default:
                                downloadFileFath = DownloadService.downloadPath + bodyBean.name;
                                break;
                        }

                        if (isDownloadFinished(bodyBean)) {
                            int currentItem = 0;
                            if (viewpager != null) {
                                currentItem = viewpager.getCurrentItem();
                            }
                            playListStr.append("{\tID:" + bodyBean.id + "\t");
                            playListStr.append("name:" + bodyBean.name + "\t");
                            playListStr.append("contentType:" + bodyBean.contentType + "\t");
                            playListStr.append("xelUrl:" + downloadFileFath + "\t");
                            playListStr.append("playTime:" + bodyBean.playDate + "-" + bodyBean.stopDate + "\t");
                            playListStr.append("isUrg:" + (bodyBean.isUrg == null ? "0" : bodyBean.isUrg) + "\t");
                            playListStr.append("playStatus:" + (i == currentItem ? "play" : "pause") + "\t}\t\n");

                        } else {
                        }
                    }
                }
            } else {
            }
        }
        return playListStr.toString();
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
                        fragment.setTempView(this);
                        //TODO 此处需添加文件是否已下载完成的判断
                        if (isDownloadFinished(bodyBean)) {
                            fragmentList.add(fragment);
                        } else {
                            errFileSum++;
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
                        fragment.setTempView(this);
                        //TODO 此处需添加文件是否已下载完成的判断
                        if (isDownloadFinished(bodyBean)) {
                            fragmentList.add(fragment);
                        } else {
                            errFileSum++;
                        }
                    }
                }
            } else {
                errFileSum++;
            }
        }
    }

    private boolean isDownloadFinished(PlaylistBodyBean bodyBean) {
        for (int j = 0; j < getDownloadItems().size(); j++) {
            if (getDownloadItems().get(j).id.equals(bodyBean.id)) {
                try {
                    if ("finish".equals(getDownloadItems().get(j).status) || "COMPLETED".equals(getDownloadItems().get(j).status)) {
                        if (Utils.getFileExistType(bodyBean.name) > 0 && Utils.checkMd5(context, bodyBean)) {
                            return true;
                        }
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }

    private synchronized List<DownloadBean> getDownloadItems() {
        String jsonFinish = Utils.get(context, Utils.KEY_PLAY_LIST_DOWNLOAD_FINISH, "").toString();
        List<DownloadBean> finished = new ArrayList<>();
        List<DownloadBean> hadDeleted = new ArrayList<>();
        if (!TextUtils.isEmpty(jsonFinish)) {
            finished = JSON.parseArray(jsonFinish, DownloadBean.class);
            for (int i = 0; i < finished.size(); i++) {
                if (Utils.getFileExistType(finished.get(i).name) == 0) {
                    hadDeleted.add(finished.get(i));
                }
            }
            if (hadDeleted.size() > 0) {
                finished.removeAll(hadDeleted);
                Utils.put(context, Utils.KEY_PLAY_LIST_DOWNLOAD_FINISH, JSONArray.toJSONString(finished));
            }
        }
        return finished;
    }

    private synchronized int addTempViewList() {
        if (playlistBean == null) {
            return -1;
        }
        fragmentList.clear();

        List<PlaylistBodyBean> hotLists = new ArrayList<>();
        List<PlaylistBodyBean> normalLists = new ArrayList<>();
        for (int i = 0; i < playlistBean.size(); i++) {
            PlaylistBodyBean bodyBean = playlistBean.get(i);
            if ("1".equals(bodyBean.isUrg)) {
                hotLists.add(bodyBean);
            }
        }
        addPlayList(hotLists);
        if (fragmentList.size() > 0) {
            errFileSum += playlistBean.size() - fragmentList.size();
            try {
                willPagerAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Logger.e(e.toString());
            }
            return 1;
        }
        for (int i = 0; i < playlistBean.size(); i++) {
            if (!"1".equals(playlistBean.get(i).isUrg)) {
                normalLists.add(playlistBean.get(i));
            }
        }
        addPlayList(normalLists);
        Logger.d("当前播放列表状态:", getPlayListStr(playlistBean));
        return 0;
    }

    private long lastUpdaTime;

    public void fileHadDel() {
        setType(type);
    }

    public synchronized void nextPlay() {
        if ((System.currentTimeMillis() - lastUpdaTime) < 2000) {
            return;
        }
        lastUpdaTime = System.currentTimeMillis();
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
                Logger.e("出错，播放列表记录：" + json);
                nextPlay();
            }
        } else {

            int current = viewpager.getCurrentItem();
            int next;
            if (playlistBean != null && playlistBean.size() + presetSum > fragmentList.size() + errFileSum) {
                setType(type);
                return;
            }
            if (fragmentList.size() == 1 && fragmentList.get(0) instanceof VideoFragment) {
                if (!(willPagerAdapter.getRegisteredFragment(0) instanceof VideoFragment)) {
                    willPagerAdapter.setRegisteredFragment(0, fragmentList.get(0));
                    willPagerAdapter.notifyDataSetChanged();
//                    reSetAdapter();
                }
                ((VideoFragment) willPagerAdapter.getRegisteredFragment(0)).replayCurrent();
                return;
            }
            if (current < fragmentList.size() - 1) {
                next = current + 1;
            } else {
                next = 0;
            }
            if (fragmentList.get(next) instanceof BaseTempFragment) {
                if (Utils.isInPlayTime(((BaseTempFragment) fragmentList.get(next)).getBean())) {
                    viewpager.setCurrentItem(next);
                } else {
                    fragmentList.clear();
                    String json = Utils.get(context, Utils.KEY_PLAY_LIST, "").toString();
                    if (!TextUtils.isEmpty(json)) {
                        playlistBean = JSON.parseArray(json, PlaylistBodyBean.class);
                    }
                    setType(type);
                }
            } else {
                viewpager.setCurrentItem(next);
            }
            Logger.d("当前播放列表状态:", getPlayListStr(playlistBean));
//            if (errFileSum > 0) {
//                setType(type);
//            }
        }
    }

    //    public class WillPagerAdapter extends FragmentPagerAdapter {
    public class WillPagerAdapter extends MyFragmentPagerAdapter {
        // SparseArray是Hashmap的改良品，其核心是折半查找函数（binarySearch）
        SparseArray<WeakReference<Fragment>> registeredFragments = new SparseArray<WeakReference<Fragment>>();
        private List<Fragment> mList;


        public WillPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public WillPagerAdapter(FragmentManager fm, List<Fragment> list) {
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
            Fragment fragment = null;
            try {
                fragment = new Fragment();
                if (mList.get(position) instanceof BaseTempFragment) {
                    fragment = BaseTempFragment.newInstance((BaseTempFragment) mList.get(position));
                } else if (mList.get(position) instanceof BaseTabFragment) {
                    fragment = BaseTabFragment.newInstance((BaseTabFragment) mList.get(position));
                }
            } catch (Exception e) {
                Logger.e(e.toString());
            }
            return fragment;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // TODO Auto-generated method stub
            // 得到缓存的fragment
            Fragment fragment = (Fragment) super.instantiateItem(container,
                    position);
            if (mList != null && playlistBean != null && mList.size() + errFileSum < playlistBean.size() + presetSum) {
                setType(type);
            }
            if (fragment instanceof BaseTempFragment) {
                PlaylistBodyBean fBean = ((BaseTempFragment) fragment).getBean();
                if (fBean != null && !Utils.isInPlayTime(fBean)) {
                    setType(type);
                } else if (fBean != null && Utils.getFileExistType(fBean.name) == 0) {
                    setType(type);
                }
                if (fBean == null && mList.get(position) instanceof BaseTempFragment) {
                    PlaylistBodyBean bodyBean = ((BaseTempFragment) mList.get(position)).getBean();
                    if (bodyBean != null) {
                        ((BaseTempFragment) fragment).setBean(bodyBean);
                    }
                } else if (fBean != null && mList.get(position) instanceof BaseTempFragment) {
                    PlaylistBodyBean bodyBean = ((BaseTempFragment) mList.get(position)).getBean();
                    if (bodyBean != null && fBean.id != bodyBean.id) {
                        ((BaseTempFragment) fragment).setBean(bodyBean);
                    }
                    if (!fragment.getClass().equals(mList.get(position).getClass())) {
                        willPagerAdapter.setRegisteredFragment(position, mList.get(position));
                        willPagerAdapter.notifyDataSetChanged();
                        return getRegisteredFragment(position);
//
//                        reSetAdapter();
                    }

                }
            } else if (playlistBean != null && position < playlistBean.size()) {
                willPagerAdapter.setRegisteredFragment(position, mList.get(position));
                willPagerAdapter.notifyDataSetChanged();
                return getRegisteredFragment(position);
//                reSetAdapter();
            }
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

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position).get();
        }

        public void setRegisteredFragment(int position, Fragment fragment) {
            WeakReference<Fragment> weak = new WeakReference<Fragment>(fragment);
            registeredFragments.put(position, weak);
        }
    }

    public synchronized void updatePreset() {
        presetSum = 0;
        try {
            Activity activity = (Activity) getContext();
            if (activity != null) {
                if (activity instanceof Temp2Activity || activity instanceof Temp3Activity
                        || activity instanceof Temp5Activity) {

                    Fragment oldTab1Fragment = null;
                    Fragment oldTab2Fragment = null;
                    Fragment oldTab3Fragment = null;
                    int tabSum = 0;
                    for (int i = 0; i < fragmentList.size(); i++) {
                        if (fragmentList.get(i) instanceof Tab1Fragment) {
                            oldTab1Fragment = fragmentList.get(i);
                            tabSum++;
                        } else if (fragmentList.get(i) instanceof Tab2Fragment) {
                            oldTab2Fragment = fragmentList.get(i);
                            tabSum++;
                        } else if (fragmentList.get(i) instanceof Tab3Fragment) {
                            oldTab3Fragment = fragmentList.get(i);
                            tabSum++;
                        }
                    }
                    if (tabSum > 0 && viewpager.getCurrentItem() >= fragmentList.size() - tabSum) {
                        viewpager.setCurrentItem(0);
                    }
                    if (oldTab1Fragment != null) {
                        fragmentList.remove(oldTab1Fragment);
                    }
                    if (oldTab2Fragment != null) {
                        fragmentList.remove(oldTab2Fragment);
                    }
                    if (oldTab3Fragment != null) {
                        fragmentList.remove(oldTab3Fragment);
                    }
                    Tab1Fragment tab1Fragment = new Tab1Fragment();
                    Tab2Fragment tab2Fragment = new Tab2Fragment();
                    Tab3Fragment tab3Fragment = new Tab3Fragment();
                    String json = Utils.get(context, Utils.KEY_PRESET, "").toString();
                    if (TextUtils.isEmpty(json)) {
                        return;
                    }
                    PresetBean bean = JSON.parseObject(json, PresetBean.class);
                    tab1Fragment.setBean(bean.data.saveRate);
                    tab2Fragment.setBean(bean.data.loanRate);
                    tab3Fragment.setBean(bean.data.buyInAndOutForeignExchange);
                    if (bean.data.saveRate.enable) {
                        tab1Fragment.setTempView(this);
                        fragmentList.add(tab1Fragment);
                        presetSum++;
                    }
                    if (bean.data.loanRate.enable) {
                        tab2Fragment.setTempView(this);
                        fragmentList.add(tab2Fragment);
                        presetSum++;
                    }
                    if (bean.data.buyInAndOutForeignExchange.enable) {
                        tab3Fragment.setTempView(this);
                        fragmentList.add(tab3Fragment);
                        presetSum++;
                    }
                }
            }
        } catch (Exception e) {
            Logger.e(e.toString());
        }
    }

}
