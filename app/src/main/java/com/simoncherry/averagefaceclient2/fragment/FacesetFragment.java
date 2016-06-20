package com.simoncherry.averagefaceclient2.fragment;


import android.content.Context;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;
import com.orhanobut.logger.Logger;
import com.simoncherry.averagefaceclient2.R;
import com.simoncherry.averagefaceclient2.adapter.DirectoryAdapter;
import com.simoncherry.averagefaceclient2.bean.DirectoryBean;
import com.simoncherry.averagefaceclient2.presenter.FacesetPresenter;
import com.simoncherry.averagefaceclient2.presenter.impl.FacesetPresenterImpl;
import com.simoncherry.averagefaceclient2.util.ImageLoader;
import com.simoncherry.averagefaceclient2.view.FacesetView;

import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import me.relex.photodraweeview.PhotoDraweeView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacesetFragment extends Fragment implements FacesetView{

    private ListView list_dir;
    private DirectoryAdapter adapter_dir;
    private List<DirectoryBean> bean_dir;
    private GridView gv_img;
    private ListAdapter adapter_photo;
    private PtrFrameLayout ptrFrame;
    private ImageView img_loading;
    private ViewGroup layout_container;
    private PhotoDraweeView mPhotoDraweeView;
    private ImageLoader mImageLoader;

    private OnFragmentInteractionListener mListener;
    private boolean isInDirectory = false;
    private String currentDirectory = "";


    public interface OnFragmentInteractionListener {
        void setInDiretory(Boolean isInDir);
    }

    private FacesetPresenter presenter;

    public FacesetFragment() {
        // Required empty public constructor
    }

    public FacesetPresenter getPresenter(){
        return this.presenter;
    }

    public PhotoDraweeView getPhotoDraweeView(){
        return this.mPhotoDraweeView;
    }

    public boolean isInDirectory() {
        return isInDirectory;
    }

    public void setInDirectory(boolean inDirectory) {
        isInDirectory = inDirectory;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoader.getInstance(3, ImageLoader.Type.LIFO);
        presenter = new FacesetPresenterImpl(this);
        mListener.setInDiretory(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_faceset, null);
        layout_container = (ViewGroup) view.findViewById(R.id.layout_container);
        ptrFrame = (PtrFrameLayout) view.findViewById(R.id.ptr_frame);
        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                if(isInDirectory){
                    presenter.getFacesetPhoto(currentDirectory);
                }else{
                    presenter.getFacesetDirectory();
                }

                if(img_loading != null) {
                    img_loading.setImageResource(R.drawable.loading_wait);
                    img_loading.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                if(null != mPhotoDraweeView && mPhotoDraweeView.getVisibility() == View.VISIBLE){
                    return false;
                }

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
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showLoadingScene();
        presenter.getFacesetDirectory();
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
    public void showLoadingScene() {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        View img_loading_layout = inflater.inflate(R.layout.layout_imageview_loading, null);
        img_loading = (ImageView) img_loading_layout.findViewById(R.id.img_loading);
        layout_container.removeAllViews();
        layout_container.addView(img_loading_layout);
        list_dir = null;
        gv_img = null;
    }

    @Override
    public void loadFacesetDirectory() {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        View listview_faceset_layout = inflater.inflate(R.layout.layout_listview_faceset, null);
        list_dir = (ListView) listview_faceset_layout.findViewById(R.id.listview_faceset);
        layout_container.removeAllViews();
        layout_container.addView(listview_faceset_layout);
        img_loading = null;
        gv_img = null;
        list_dir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String path = presenter.getDirectoryBean().get(position).getFileName();
                currentDirectory = path;
                isInDirectory = true;
                presenter.getFacesetPhoto(path);
                mListener.setInDiretory(true);
            }
        });
    }

    @Override
    public void loadFacesetPhoto() {
        final LayoutInflater inflater = LayoutInflater.from(getActivity());
        View gridview_faceset_layout = inflater.inflate(R.layout.layout_gridview_faceset, null);
        gv_img = (GridView) gridview_faceset_layout.findViewById(R.id.gridview_faceset);
        mPhotoDraweeView = (PhotoDraweeView) gridview_faceset_layout.findViewById(R.id.mPhotoDraweeView);
        // TODO
        mPhotoDraweeView.setVisibility(View.GONE);
        //
        layout_container.removeAllViews();
        layout_container.addView(gridview_faceset_layout);
        img_loading = null;
        list_dir = null;
        gv_img.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] imagePathSet = presenter.getImagePath();
                String imagePath = imagePathSet[position];
                //Logger.t("clickGridView").e(String.valueOf(position));
                //Logger.t("getPath").e(imagePath);
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
            }
        });
    }

    @Override
    public void showFailedScene() {
        if(img_loading == null){
            showLoadingScene();
        }
        img_loading.setImageResource(R.drawable.loading_failed);
        img_loading.setVisibility(View.VISIBLE);

        ptrFrame.refreshComplete();
    }

    @Override
    public void showFacesetDirectory(DirectoryAdapter adapter) {
        list_dir.setAdapter(adapter);
        ptrFrame.refreshComplete();
    }

    @Override
    public void showFacesetPhoto(String[] imagePath) {
        //ListAdapter adapter = new ListImgItemAdaper(getActivity(), 0, imagePath);
        adapter_photo = new ListImgItemAdaper(getActivity(), 0, imagePath);
        gv_img.setAdapter(adapter_photo);
        ptrFrame.refreshComplete();
    }

    public void resumeFaceSet(){
        if(mPhotoDraweeView != null){
            mPhotoDraweeView.setVisibility(View.GONE);
        }
        if(gv_img != null){
            gv_img.setVisibility(View.VISIBLE);
        }
    }

    private class ListImgItemAdaper extends ArrayAdapter<String> {

        public ListImgItemAdaper(Context context, int resource, String[] datas){
            super(getActivity(), 0, datas);
            Logger.t("In ListImgItemAdaper").e("ok");
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if (convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(
                        R.layout.item_gridview_faceset, parent, false);
            }
            ImageView imageview = (ImageView) convertView
                    .findViewById(R.id.id_img);
            imageview.setImageResource(R.drawable.pictures_no);
            mImageLoader.loadImage(getItem(position), imageview, true);
            return convertView;
        }

    }
}
