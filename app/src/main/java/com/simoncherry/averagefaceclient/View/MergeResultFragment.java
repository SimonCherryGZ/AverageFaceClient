package com.simoncherry.averagefaceclient.View;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simoncherry.averagefaceclient.R;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.BitmapCallback;

import okhttp3.Call;

public class MergeResultFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    // ============================================================================================
    //private String fileUrl = "http://192.168.1.102:8128/AverageFaceServer/";
    private String fileUrl = "http://192.168.1.103:8128/AverageFaceServer/";

    private TextView tv_process_msg;
    private ImageView img_result;

    public MergeResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        tv_process_msg = (TextView) getActivity().findViewById(R.id.tv_process_msg);
        img_result = (ImageView) getActivity().findViewById(R.id.img_result);

        registerMessageReceiver();
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

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {
    }

    public MessageReceiver mMessageReceiver;
    public static String ACTION_INTENT_RECEIVER = "com.simoncherry.averagefaceclient.Receiver.MyReceiver";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_INTENT_RECEIVER);
        getActivity().registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_INTENT_RECEIVER)) {
                //tv_process_msg.setText(intent.getStringExtra("message"));
                String temp = intent.getStringExtra("message");
                if(temp.contains("output")){
                    //tv_process_msg.setText("地址：" + fileUrl + temp);

                    OkHttpUtils.get().url(fileUrl + temp).build()
                            .execute(new BitmapCallback()
                            {
                                @Override
                                public void onError(Call call, Exception e) {
                                    tv_process_msg.setText("onError:" + e.getMessage());
                                }
                                @Override
                                public void onResponse(Bitmap bitmap)
                                {
                                    img_result.setImageBitmap(bitmap);
                                }
                            });
                }
                else{
                    tv_process_msg.setText(intent.getStringExtra("message"));
                }
            }
        }

    }
}
