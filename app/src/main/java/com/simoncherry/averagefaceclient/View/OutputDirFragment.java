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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.simoncherry.averagefaceclient.Adapter.DirectoryAdapter;
import com.simoncherry.averagefaceclient.Bean.DirectoryBean;
import com.simoncherry.averagefaceclient.Model.OutputDirPresenterImple;
import com.simoncherry.averagefaceclient.R;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import okhttp3.Call;

public class OutputDirFragment extends Fragment{

    //private String dirUrl = "http://192.168.1.102:8128/AverageFaceServer/DirectoryServlet";
    private String dirUrl = "http://192.168.1.103:8128/AverageFaceServer/DirectoryServlet";

    private OutputDirPresenterImple outputDirPresenterImple;
    private ListView list_dir;
    private DirectoryAdapter adapter_dir;
    private List<DirectoryBean> bean_dir;
    private GridView gv_img;
    private ListAdapter adapter_img;
    private PtrClassicFrameLayout ptr;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void setInDir(Boolean isInDir);
        void setWhichDir(String dir);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x123){
                String res = msg.getData().getString("res");
                outputDirPresenterImple.getFacesetDir(res);
                adapter_dir = outputDirPresenterImple.getmListViewAdapter();
                list_dir.setAdapter(adapter_dir);
                //adapter_dir.notifyDataSetChanged();

            }else if(msg.what == 0x456){
                String res = msg.getData().getString("res");
                String path = msg.getData().getString("dat");
                Log.v("refresh", "path: " + path);
                outputDirPresenterImple.getImageList(res, path);
                adapter_img = outputDirPresenterImple.getmGridViewAdapter();
                gv_img.setAdapter(adapter_img);
                //adapter_img.notifyAll();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        outputDirPresenterImple = new OutputDirPresenterImple(this);
        mListener.setInDir(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cloud_dir, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        initViews();
        //outputDirPresenterImple = new UserDirPresenterImple(this);
        outputDirPresenterImple.queryFacesetDir(dirUrl, "getdir", "null", "output", new MyStringCallBack(0x123, "null"));
        //mListener.setInDir(false);
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

    private void initViews(){
        //list_dir = (ListView) getActivity().findViewById(R.id.list_cloud_dir);
        //gv_img = (GridView) getActivity().findViewById(R.id.gv_img);
        ptr = (PtrClassicFrameLayout) getActivity().findViewById(R.id.ptr_frame);

        list_dir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list_dir.setVisibility(View.GONE);
                ptr.setVisibility(View.GONE);
                gv_img.setVisibility(View.VISIBLE);
                bean_dir = outputDirPresenterImple.getmBean();
                String path = bean_dir.get(position).getFileName();
                mListener.setWhichDir(path);
                outputDirPresenterImple.queryFacesetDir(dirUrl, "imglist", path, "output", new MyStringCallBack(0x456, path));
                mListener.setInDir(true);
            }
        });

        ptr.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                outputDirPresenterImple.refreshDir("root", new MyStringCallBack(0x123, "null"));
                ptr.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                // 默认实现，根据实际情况做改动
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });
    }

    public void refreshDir(String dir){
        Log.v("refresh", "dir: " + dir);
        if(dir.equals("root")) {
            outputDirPresenterImple.refreshDir(dir, new MyStringCallBack(0x123, "null"));
        }else{
            outputDirPresenterImple.refreshDir(dir, new MyStringCallBack(0x456, dir));
        }
    }

    public void backUpperLevel(){
        gv_img.setVisibility(View.GONE);
        ptr.setVisibility(View.VISIBLE);
        list_dir.setVisibility(View.VISIBLE);
        mListener.setInDir(false);
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
        }
        @Override
        public void onResponse(String response) {
            Log.v("refresh", "response: " + response);
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