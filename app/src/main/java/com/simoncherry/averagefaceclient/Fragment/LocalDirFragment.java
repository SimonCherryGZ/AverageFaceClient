package com.simoncherry.averagefaceclient.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.simoncherry.averagefaceclient.Adapter.DirectoryAdapter;
import com.simoncherry.averagefaceclient.Module.DirectoryBean;
import com.simoncherry.averagefaceclient.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocalDirFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocalDirFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocalDirFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    //=============================================================================================
    private ListView list_dir;
    private DirectoryAdapter adapter_dir;
    private List<DirectoryBean> bean_dir;

    public LocalDirFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CloudDirFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocalDirFragment newInstance(String param1, String param2) {
        LocalDirFragment fragment = new LocalDirFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local_dir, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        list_dir = (ListView) getActivity().findViewById(R.id.list_local_dir);
        initDirectoryList();
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

    private void initDirectoryList(){
        bean_dir = new ArrayList<>();
        setDirectoryAdapter(bean_dir);
        Long date1 = 72471346813L;
        Long date2 = 62471346813L;
        addDirectoryList(R.drawable.ic_folder_shared_grey600_48dp, "魂斗罗大战葫芦娃", date1, 111);
        addDirectoryList(R.drawable.ic_folder_shared_grey600_48dp, "你老板大战我老顶", date2, 2788);
    }
}
