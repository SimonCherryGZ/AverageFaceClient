package com.simoncherry.averagefaceclient.View;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.orhanobut.logger.Logger;
import com.simoncherry.averagefaceclient.Adapter.DirectoryAdapter;
import com.simoncherry.averagefaceclient.Bean.DirectoryBean;
import com.simoncherry.averagefaceclient.Model.UserDirPresenterImple;
import com.simoncherry.averagefaceclient.R;
import com.zhy.http.okhttp.callback.StringCallback;

import net.steamcrafted.loadtoast.LoadToast;

import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;
import okhttp3.Call;

public class UserDirFragment extends Fragment{

    //private String dirUrl = "http://192.168.1.102:8128/AverageFaceServer/DirectoryServlet";
    private String dirUrl = "http://192.168.1.103:8128/AverageFaceServer/DirectoryServlet";
    private String currentDir = "root";
    private boolean isInDir = false;

    private UserDirPresenterImple userDirPresenterImple;
    private ListView list_dir;
    private DirectoryAdapter adapter_dir;
    private List<DirectoryBean> bean_dir;
    private GridView gv_img;
    private ListAdapter adapter_img;
    private PtrFrameLayout ptrFrame;
    private ImageView img_loading;
    private LoadToast lt;
    private ViewGroup layout_container;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void setInDir(Boolean isInDir);
        void setWhichDir(String dir);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO
            if(msg.what == 0x123){
                initViews(1);

                String res = msg.getData().getString("res");
                userDirPresenterImple.getFacesetDir(res);
                adapter_dir = userDirPresenterImple.getmListViewAdapter();
                list_dir.setAdapter(adapter_dir);
                ptrFrame.refreshComplete();

            }else if(msg.what == 0x456){
                initViews(2);

                String res = msg.getData().getString("res");
                String path = msg.getData().getString("dat");
                Logger.e("handler 0x456 refresh path", path);
                userDirPresenterImple.getImageList(res, path);
                adapter_img = userDirPresenterImple.getmGridViewAdapter();
                gv_img.setAdapter(adapter_img);
                ptrFrame.refreshComplete();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDirPresenterImple = new UserDirPresenterImple(this);
        mListener.setInDir(false);
        isInDir = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_cloud_dir, container, false);
        // TODO
        View view = inflater.inflate(R.layout.fragment_cloud_dir, container, false);
        layout_container = (ViewGroup) view.findViewById(R.id.layout_container);
        ptrFrame = (PtrFrameLayout) view.findViewById(R.id.ptr_frame);
//        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
//            @Override
//            public void onRefreshBegin(PtrFrameLayout frame) {
//                userDirPresenterImple.refreshDir("root", new MyStringCallBack(0x123, "null"));
//                //ptrFrame.refreshComplete();
//                // TODO
//                if(img_loading != null) {
//                    img_loading.setImageResource(R.drawable.loading_wait);
//                    img_loading.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
//                // 默认实现，根据实际情况做改动
//                //return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
//                View view = img_loading;
//                if(img_loading != null){
//                    view = img_loading;
//                }else if(list_dir != null){
//                    view = list_dir;
//                }else if(gv_img != null){
//                    view = gv_img;
//                }
//                return PtrDefaultHandler.checkContentCanBePulledDown(frame, view, header);
//            }
//        });
        // header
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
        initViews(0);  // TODO
        userDirPresenterImple.queryFacesetDir(dirUrl, "getdir", "null", "faceset", new MyStringCallBack(0x123, "null"));
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
//        list_dir = (ListView) getActivity().findViewById(R.id.list_cloud_dir);
//        img_loading = (ImageView) getActivity().findViewById(R.id.img_loading);
//        gv_img = (GridView) getActivity().findViewById(R.id.gv_img);
//        ptrFrame = (PtrClassicFrameLayout) getActivity().findViewById(R.id.ptr_frame);
//
//        list_dir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                list_dir.setVisibility(View.GONE);
//                ptrFrame.setVisibility(View.GONE);
//                gv_img.setVisibility(View.VISIBLE);
//                bean_dir = userDirPresenterImple.getmBean();
//                String path = bean_dir.get(position).getFileName();
//                mListener.setWhichDir(path);
//                userDirPresenterImple.queryFacesetDir(dirUrl, "imglist", path, "faceset", new MyStringCallBack(0x456, path));
//                mListener.setInDir(true);
//            }
//        });
//
//        img_loading.setVisibility(View.VISIBLE);

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
                    // TODO
                    //ptrFrame.setVisibility(View.GONE);
                    //list_dir.setVisibility(View.GONE);
                    //gv_img.setVisibility(View.VISIBLE);
                    bean_dir = userDirPresenterImple.getmBean();
                    String path = bean_dir.get(position).getFileName();
                    // TODO
                    isInDir = true;
                    currentDir = path;
                    mListener.setWhichDir(path);
                    mListener.setInDir(true);
                    userDirPresenterImple.queryFacesetDir(dirUrl, "imglist", path, "faceset", new MyStringCallBack(0x456, path));
                }
            });
        }else if(which == 2){
            View gridview_faceset_layout = inflater.inflate(R.layout.layout_gridview_faceset, null);
            gv_img = (GridView) gridview_faceset_layout.findViewById(R.id.gridview_faceset);
            layout_container.removeAllViews();
            layout_container.addView(gridview_faceset_layout);
            img_loading = null;
            list_dir = null;
        }

        //ptrFrame = (PtrClassicFrameLayout) getActivity().findViewById(R.id.ptr_frame);
        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                // TODO
                if(isInDir) {
                    userDirPresenterImple.refreshDir(currentDir, new MyStringCallBack(0x456, currentDir));
                }else {
                    userDirPresenterImple.refreshDir("root", new MyStringCallBack(0x123, "null"));
                }
                // TODO
                if(img_loading != null) {
                    img_loading.setImageResource(R.drawable.loading_wait);
                    img_loading.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
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
            userDirPresenterImple.refreshDir(dir, new MyStringCallBack(0x123, "null"));
        }else{
            userDirPresenterImple.refreshDir(dir, new MyStringCallBack(0x456, dir));
        }
    }

    public void backUpperLevel(){
        // TODO
//        gv_img.setVisibility(View.GONE);
//        list_dir.setVisibility(View.VISIBLE);
        ptrFrame.setVisibility(View.VISIBLE);
        mListener.setInDir(false);
        isInDir = false;
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
            //img_loading.setImageResource(R.drawable.loading_failed);
            //img_loading.setVisibility(View.VISIBLE);
            // TODO
            if(img_loading == null){
//                final LayoutInflater inflater = LayoutInflater.from(getActivity());
//                ViewGroup layout_img_loading = (ViewGroup) inflater.inflate(R.layout.layout_img_loading, null);
//                img_loading = (ImageView) layout_img_loading.findViewById(R.id.img_loading);
//                layout_container.removeAllViews();
//                layout_container.addView(layout_img_loading);
                initViews(0);
            }

            img_loading.setImageResource(R.drawable.loading_failed);
            img_loading.setVisibility(View.VISIBLE);

            ptrFrame.refreshComplete();
        }
        @Override
        public void onResponse(String response) {
            Logger.t("onResponse").e(response);
            //img_loading.setVisibility(View.GONE);
            // TODO

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