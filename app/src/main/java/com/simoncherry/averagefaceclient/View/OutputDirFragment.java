package com.simoncherry.averagefaceclient.View;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.orhanobut.logger.Logger;
import com.simoncherry.averagefaceclient.Adapter.DirectoryAdapter;
import com.simoncherry.averagefaceclient.Bean.DirectoryBean;
import com.simoncherry.averagefaceclient.Model.OutputDirPresenterImple;
import com.simoncherry.averagefaceclient.R;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;
import me.relex.photodraweeview.PhotoDraweeView;
import okhttp3.Call;

public class OutputDirFragment extends Fragment{

    //private String dirUrl = "http://192.168.1.102:8128/AverageFaceServer/DirectoryServlet";
    private String dirUrl = "http://192.168.1.103:8128/AverageFaceServer/DirectoryServlet";
    private String currentDir = "root";
    private boolean isInDir = false;
    private boolean isViewDetail = false;

    private OutputDirPresenterImple outputDirPresenterImple;
    private ListView list_dir;
    private DirectoryAdapter adapter_dir;
    private List<DirectoryBean> bean_dir;
    private GridView gv_img;
    private ListAdapter adapter_img;
    private PtrFrameLayout ptrFrame;
    private ImageView img_loading;
    private ViewGroup layout_container;
    private PhotoDraweeView mPhotoDraweeView;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void setInDir(Boolean isInDir);
        void setWhichDir(String dir);
        void setViewDetail(Boolean viewDetail);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x123){
                initViews(1);

                String res = msg.getData().getString("res");
                outputDirPresenterImple.getFacesetDir(res);
                adapter_dir = outputDirPresenterImple.getmListViewAdapter();
                list_dir.setAdapter(adapter_dir);
                ptrFrame.refreshComplete();

            }else if(msg.what == 0x456){
                initViews(2);

                String res = msg.getData().getString("res");
                String path = msg.getData().getString("dat");
                Logger.e("handler 0x456 refresh path", path);
                outputDirPresenterImple.getImageList(res, path);
                adapter_img = outputDirPresenterImple.getmGridViewAdapter();
                gv_img.setAdapter(adapter_img);
                ptrFrame.refreshComplete();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        outputDirPresenterImple = new OutputDirPresenterImple(this);
        mListener.setInDir(false);
        isInDir = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_cloud_dir, container, false);
        View view = inflater.inflate(R.layout.fragment_cloud_dir, container, false);
        layout_container = (ViewGroup) view.findViewById(R.id.layout_container);
        ptrFrame = (PtrFrameLayout) view.findViewById(R.id.ptr_frame);
        final MaterialHeader header = new MaterialHeader(getContext());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, PtrLocalDisplay.dp2px(15), 0, PtrLocalDisplay.dp2px(10));
        header.setPtrFrameLayout(ptrFrame);
        ptrFrame.setHeaderView(header);
        ptrFrame.addPtrUIHandler(header);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initViews(0);
        outputDirPresenterImple.queryFacesetDir(dirUrl, "getdir", "null", "output", new MyStringCallBack(0x123, "null"));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initViews(final int which){
        //list_dir = (ListView) getActivity().findViewById(R.id.list_cloud_dir);
        //gv_img = (GridView) getActivity().findViewById(R.id.gv_img);
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        if(which == 0) {
            View img_loading_layout = inflater.inflate(R.layout.layout_img_loading, null);
            img_loading = (ImageView) img_loading_layout.findViewById(R.id.img_loading);
            layout_container.removeAllViews();
            layout_container.addView(img_loading_layout);
            list_dir = null;
            gv_img = null;
        }else if(which == 1){
            View listview_faceset_layout = inflater.inflate(R.layout.layout_listview_faceset, null);
            list_dir = (ListView) listview_faceset_layout.findViewById(R.id.listview_faceset);
            layout_container.removeAllViews();
            layout_container.addView(listview_faceset_layout);
            img_loading = null;
            gv_img = null;
            list_dir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    bean_dir = outputDirPresenterImple.getmBean();
                    String path = bean_dir.get(position).getFileName();
                    isInDir = true;
                    currentDir = path;
                    mListener.setWhichDir(path);
                    mListener.setInDir(true);
                    outputDirPresenterImple.queryFacesetDir(dirUrl, "imglist", path, "output", new MyStringCallBack(0x456, path));
                }
            });
        }else if(which == 2){
            View gridview_faceset_layout = inflater.inflate(R.layout.layout_gridview_faceset, null);
            gv_img = (GridView) gridview_faceset_layout.findViewById(R.id.gridview_faceset);
            mPhotoDraweeView = (PhotoDraweeView) gridview_faceset_layout.findViewById(R.id.mPhotoDraweeView);
            layout_container.removeAllViews();
            layout_container.addView(gridview_faceset_layout);
            img_loading = null;
            list_dir = null;
            gv_img.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String[] imagePathSet = outputDirPresenterImple.getImagePath();
                    String imagePath = imagePathSet[position];
                    Logger.t("clickGridView").e(String.valueOf(position));
                    Logger.t("getPath").e(imagePath);
                    gv_img.setVisibility(View.GONE);
                    mPhotoDraweeView.setVisibility(View.VISIBLE);
                    PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
                    controller.setUri(imagePath);
                    controller.setOldController(mPhotoDraweeView.getController());
                    controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
                        @Override
                        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                            super.onFinalImageSet(id, imageInfo, animatable);
                            if (imageInfo == null || mPhotoDraweeView == null) {
                                return;
                            }
                            mPhotoDraweeView.update(imageInfo.getWidth(), imageInfo.getHeight());
                        }
                    });
                    mPhotoDraweeView.setController(controller.build());
                    mListener.setViewDetail(true);
                    isViewDetail = true;
                }
            });
        }

        ptrFrame = (PtrClassicFrameLayout) getActivity().findViewById(R.id.ptr_frame);

//        list_dir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                list_dir.setVisibility(View.GONE);
//                ptrFrame.setVisibility(View.GONE);
//                gv_img.setVisibility(View.VISIBLE);
//                bean_dir = outputDirPresenterImple.getmBean();
//                String path = bean_dir.get(position).getFileName();
//                mListener.setWhichDir(path);
//                outputDirPresenterImple.queryFacesetDir(dirUrl, "imglist", path, "output", new MyStringCallBack(0x456, path));
//                mListener.setInDir(true);
//            }
//        });

        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                //outputDirPresenterImple.refreshDir("root", new MyStringCallBack(0x123, "null"));
                if(isInDir) {
                    outputDirPresenterImple.queryFacesetDir(dirUrl, "imglist", currentDir, "output", new MyStringCallBack(0x456, currentDir));
                }else {
                    outputDirPresenterImple.refreshDir("root", new MyStringCallBack(0x123, "null"));
                }
                if(img_loading != null) {
                    img_loading.setImageResource(R.drawable.loading_wait);
                    img_loading.setVisibility(View.VISIBLE);
                }
                //ptrFrame.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                if(isViewDetail){
                    return false;
                }
                // 默认实现，根据实际情况做改动
                //return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
                View view = img_loading;
                if(img_loading != null){
                    view = img_loading;
                }else if(list_dir != null){
                    view = list_dir;
                }else if(gv_img != null){
                    view = gv_img;
                }
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, view, header);
            }
        });
    }

    public void refreshDir(String dir){
        Logger.t("Refresh").e("Directory: " + dir);

        if(dir.equals("root")) {
            outputDirPresenterImple.refreshDir(dir, new MyStringCallBack(0x123, "null"));
        }else{
            outputDirPresenterImple.refreshDir(dir, new MyStringCallBack(0x456, dir));
        }
    }

    public void backUpperLevel(){
        //gv_img.setVisibility(View.GONE);
        //list_dir.setVisibility(View.VISIBLE);
        ptrFrame.setVisibility(View.VISIBLE);
        mListener.setInDir(false);
        isInDir = false;
    }

    public void resumeFaceSet(){
        if(mPhotoDraweeView != null){
            mPhotoDraweeView.setVisibility(View.GONE);
        }
        if(gv_img != null){
            gv_img.setVisibility(View.VISIBLE);
        }
        mListener.setViewDetail(false);
        isViewDetail = false;
    }

    private class MyStringCallBack extends StringCallback{
        int mCommand;
        String mData;

        private MyStringCallBack(int command, String data){
            mCommand = command;
            mData = data;
        }

        @Override
        public void onError(Call call, Exception e) {
            Logger.t("onError").e(e.getMessage());
            if(img_loading == null){
                initViews(0);
            }
            img_loading.setImageResource(R.drawable.loading_failed);
            img_loading.setVisibility(View.VISIBLE);

            ptrFrame.refreshComplete();
        }
        @Override
        public void onResponse(String response) {
            Logger.t("onResponse").e(response);

            Bundle bundle = new Bundle();
            bundle.putString("res", response);
            bundle.putString("dat", mData);
            Message msg = new Message();
            msg.setData(bundle);
            msg.what = mCommand;
            handler.sendMessage(msg);
        }
    }

}