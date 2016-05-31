package com.simoncherry.averagefaceclient.Fragment;

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
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.simoncherry.averagefaceclient.Adapter.DirectoryAdapter;
import com.simoncherry.averagefaceclient.Module.DirectoryBean;
import com.simoncherry.averagefaceclient.Module.ImagePathBean;
import com.simoncherry.averagefaceclient.R;
import com.simoncherry.averagefaceclient.Util.ImageLoader;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import okhttp3.Call;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CloudDirFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CloudDirFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CloudDirFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    //=============================================================================================
    private String dirUrl = "http://192.168.1.102:8128/AverageFaceServer/DirectoryServlet";
    private String fileUrl = "http://192.168.1.102:8128/AverageFaceServer/faceset/";
    String reqStr = "null";
    String selectDir = "root";
    private ImageLoader mImageLoader;

    private ListView list_dir;
    private DirectoryAdapter adapter_dir;
    private List<DirectoryBean> bean_dir;
    private TextView tv_show_test;
    private GridView gv_img;
    private PtrClassicFrameLayout ptr;

    public CloudDirFragment() {
        // Required empty public constructor
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            if(msg.what == 0x123){
                // 设置show组件显示服务器响应
                tv_show_test.setText(reqStr);
                Gson gson = new Gson();
                //DirectoryBean bean = gson.fromJson(reqStr, DirectoryBean.class);
                //DirectoryBean[] arrayBean = gson.fromJson(reqStr, new TypeToken<DirectoryBean>(){}.getType());
                DirectoryBean[] arrayBean = gson.fromJson(reqStr, DirectoryBean[].class);

                bean_dir = new ArrayList<>();
                setDirectoryAdapter(bean_dir);
                for(int i=0; i<arrayBean.length; i++){
                    addDirectoryList(R.drawable.ic_folder_shared_grey600_48dp,
                            arrayBean[i].getFileName(), arrayBean[i].getFileDate(), arrayBean[i].getFileCount());
                }

            }else if(msg.what == 0x456){
                Gson gson = new Gson();
                ImagePathBean[] arrayBean = gson.fromJson(reqStr, ImagePathBean[].class);
                String imagePath[] = new String[arrayBean.length];
                for(int i=0; i<arrayBean.length; i++){
                    imagePath[i] = fileUrl + selectDir + "//" + arrayBean[i].getImgPath();
                }

                gv_img.setVisibility(View.VISIBLE);
                gv_img.setAdapter(new ListImgItemAdaper(getActivity(), 0,
                        imagePath));
                mListener.setWhetherInFolder(true);
            }
        }
    };
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CloudDirFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CloudDirFragment newInstance(String param1, String param2) {
        CloudDirFragment fragment = new CloudDirFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cloud_dir, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        list_dir = (ListView) getActivity().findViewById(R.id.list_cloud_dir);
        list_dir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list_dir.setVisibility(View.GONE);
                ptr.setVisibility(View.GONE);
                selectDir = bean_dir.get(position).getFileName();
                mListener.setWhichFolder(selectDir);

                OkHttpUtils.get().url(dirUrl)
                        .addParams("request", "imglist")
                        .addParams("data", selectDir)
                        .addParams("type", "faceset")
                        .build().execute(new StringCallback(){
                    @Override
                    public void onError(Call call, Exception e) {
                    }
                    @Override
                    public void onResponse(String response){
                        reqStr = response;
                        handler.sendEmptyMessage(0x456);
                    }
                });
            }
        });

        tv_show_test = (TextView) getActivity().findViewById(R.id.tv_show_test);
        gv_img = (GridView) getActivity().findViewById(R.id.gv_img);
        ptr = (PtrClassicFrameLayout) getActivity().findViewById(R.id.ptr_frame);
        ptr.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refreshFolder(selectDir);
                ptr.refreshComplete();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                // 默认实现，根据实际情况做改动
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }
        });

        OkHttpUtils.get().url(dirUrl)
                .addParams("request", "getdir")
                .addParams("data", "null")
                .addParams("type", "faceset")
                .build().execute(new StringCallback(){
                    @Override
                    public void onError(Call call, Exception e) {
                    }
                    @Override
                    public void onResponse(String response){
                        reqStr = response;
                        handler.sendEmptyMessage(0x123);
                    }
                });
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void setWhetherInFolder(Boolean isInFolder);
        void setWhichFolder(String folder);
    }

    private void setDirectoryAdapter(List<DirectoryBean> list){
        adapter_dir = new DirectoryAdapter(getContext(), list);
        list_dir.setAdapter(adapter_dir);
    }

    private void addDirectoryList(int imgID, String name, Long date, int count){
        DirectoryBean bean = new DirectoryBean();
        bean.setImgID(imgID);
        bean.setFileName(name);
        bean.setFileDate(date);
        bean.setFileCount(count);
        bean_dir.add(bean);
        adapter_dir.notifyDataSetChanged();
    }

    private class ListImgItemAdaper extends ArrayAdapter<String>
    {

        public ListImgItemAdaper(Context context, int resource, String[] datas)
        {
            super(getActivity(), 0, datas);
            Log.e("TAG", "ListImgItemAdaper");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            if (convertView == null)
            {
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.item_fragment_list_imgs, parent, false);
            }
            ImageView imageview = (ImageView) convertView
                    .findViewById(R.id.id_img);
            imageview.setImageResource(R.drawable.pictures_no);
            mImageLoader.loadImage(getItem(position), imageview, true);
            return convertView;
        }

    }

    public void backUpperLevel(){
        selectDir = "root";
        gv_img.setVisibility(View.GONE);
        ptr.setVisibility(View.VISIBLE);
        list_dir.setVisibility(View.VISIBLE);
    }

    public void refreshFolder(String folder){
        if(folder.equals("root")){
            OkHttpUtils.get().url(dirUrl)
                    .addParams("request", "getdir")
                    .addParams("data", "null")
                    .addParams("type", "faceset")
                    .build().execute(new StringCallback(){
                @Override
                public void onError(Call call, Exception e) {
                }
                @Override
                public void onResponse(String response){
                    reqStr = response;
                    handler.sendEmptyMessage(0x123);
                }
            });
        }else{
            OkHttpUtils.get().url(dirUrl)
                    .addParams("request", "imglist")
                    .addParams("data", selectDir)
                    .addParams("type", "faceset")
                    .build().execute(new StringCallback(){
                @Override
                public void onError(Call call, Exception e) {
                }
                @Override
                public void onResponse(String response){
                    reqStr = response;
                    handler.sendEmptyMessage(0x456);
                }
            });
        }
    }
}
