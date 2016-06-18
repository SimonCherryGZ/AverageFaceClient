package com.simoncherry.averagefaceclient.Model;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.simoncherry.averagefaceclient.Adapter.DirectoryAdapter;
import com.simoncherry.averagefaceclient.Bean.DirectoryBean;
import com.simoncherry.averagefaceclient.Bean.ImagePathBean;
import com.simoncherry.averagefaceclient.Presenter.OutputDirPresenter;
import com.simoncherry.averagefaceclient.R;
import com.simoncherry.averagefaceclient.Util.ImageLoader;
import com.simoncherry.averagefaceclient.Application.MyApplication;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;

public class OutputDirPresenterImple implements OutputDirPresenter {

    //private String dirUrl = "http://192.168.1.102:8128/AverageFaceServer/DirectoryServlet";
    private String dirUrl = "http://192.168.1.103:8128/AverageFaceServer/DirectoryServlet";
    //private String fileUrl = "http://192.168.1.102:8128/AverageFaceServer/output/";
    private String fileUrl = "http://192.168.1.103:8128/AverageFaceServer/output/";

    private Fragment fragment;
    private ListView mListView;
    private DirectoryAdapter mListViewAdapter;
    private List<DirectoryBean> mBean;
    private GridView mGirdView;
    private ListAdapter mGridViewAdapter;
    private PtrClassicFrameLayout mPtrCFL;
    private ImageLoader mImageLoader;

    public OutputDirPresenterImple(Fragment fragment){
        this.fragment = fragment;
        mImageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
    }

    public ListView getmListView(){
        return mListView;
    }

    public void setmListView(ListView mListView) {
        this.mListView = mListView;
    }

    public DirectoryAdapter getmListViewAdapter() {
        return mListViewAdapter;
    }

    public void setmListViewAdapter(DirectoryAdapter mListViewAdapter) {
        this.mListViewAdapter = mListViewAdapter;
    }

    public List<DirectoryBean> getmBean() {
        return mBean;
    }

    public void setmBean(List<DirectoryBean> mBean) {
        this.mBean = mBean;
    }

    public GridView getmGirdView() {
        return mGirdView;
    }

    public void setmGirdView(GridView mGirdView) {
        this.mGirdView = mGirdView;
    }

    public ListAdapter getmGridViewAdapter() {
        return mGridViewAdapter;
    }

    public void setmGridViewAdapter(ListAdapter mGridViewAdapter) {
        this.mGridViewAdapter = mGridViewAdapter;
    }

    public PtrClassicFrameLayout getmPtrCFL() {
        return mPtrCFL;
    }

    public void setmPtrCFL(PtrClassicFrameLayout mPtrCFL) {
        this.mPtrCFL = mPtrCFL;
    }

    @Override
    public void queryFacesetDir(String url, String request, String data, String type, StringCallback callback) {
        OkHttpUtils.get().url(dirUrl)
                .addParams("request", request)
                .addParams("data", data)
                .addParams("type", type)
                .build().execute(callback);
    }

    @Override
    public void getFacesetDir(String reqStr){
        Gson gson = new Gson();
        DirectoryBean[] arrayBean = gson.fromJson(reqStr, DirectoryBean[].class);

        mBean = new ArrayList<>();
        setDirectoryAdapter(mBean);
        for(int i=0; i<arrayBean.length; i++){
            addDirectoryList(R.drawable.ic_folder_shared_grey600_48dp,
                    arrayBean[i].getFileName(), arrayBean[i].getFileDate(), arrayBean[i].getFileCount());
        }
    }

    @Override
    public void getImageList(String reqStr, String selectDir) {
        Gson gson = new Gson();
        ImagePathBean[] arrayBean = gson.fromJson(reqStr, ImagePathBean[].class);
        String imagePath[] = new String[arrayBean.length];
        for(int i=0; i<arrayBean.length; i++){
            // TODO "//" ?
            imagePath[i] = fileUrl + selectDir + "//" + arrayBean[i].getImgPath();
            //imagePath[i] = fileUrl + selectDir + "/" + arrayBean[i].getImgPath();
        }
        mGridViewAdapter = new ListImgItemAdaper(fragment.getActivity(), 0, imagePath);
    }

    @Override
    public void refreshDir(String path, StringCallback callback) {
        Log.v("refresh", "okhttp path: " + path);
        if(path.equals("root")) {
            OkHttpUtils.get().url(dirUrl)
                    .addParams("request", "getdir")
                    .addParams("data", "null")
                    .addParams("type", "output")
                    .build().execute(callback);
        }else{
            OkHttpUtils.get().url(dirUrl)
                    .addParams("request", "imglist")
                    .addParams("data", path)
                    .addParams("type", "output")
                    .build().execute(callback);
        }
    }

    private void setDirectoryAdapter(List<DirectoryBean> list){
        mListViewAdapter = new DirectoryAdapter(MyApplication.getContextObject(), list);
        //mListView.setAdapter(mListViewAdapter);
    }

    private void addDirectoryList(int imgID, String name, Long date, int count){
        DirectoryBean bean = new DirectoryBean();
        bean.setImgID(imgID);
        bean.setFileName(name);
        bean.setFileDate(date);
        bean.setFileCount(count);
        mBean.add(bean);
        mListViewAdapter.notifyDataSetChanged();
    }

    private class ListImgItemAdaper extends ArrayAdapter<String>{

        public ListImgItemAdaper(Context context, int resource, String[] datas){
            super(fragment.getActivity(), 0, datas);
            Log.e("TAG", "ListImgItemAdaper");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if (convertView == null){
                convertView = fragment.getActivity().getLayoutInflater().inflate(
                        R.layout.item_fragment_list_imgs, parent, false);
            }
            ImageView imageview = (ImageView) convertView
                    .findViewById(R.id.id_img);
            imageview.setImageResource(R.drawable.pictures_no);
            mImageLoader.loadImage(getItem(position), imageview, true);
            return convertView;
        }

    }

}