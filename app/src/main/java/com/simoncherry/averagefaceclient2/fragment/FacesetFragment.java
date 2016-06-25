package com.simoncherry.averagefaceclient2.fragment;


import android.content.Context;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
import com.simoncherry.averagefaceclient2.application.MyApplication;
import com.simoncherry.averagefaceclient2.base.GridScrollStateBean;
import com.simoncherry.averagefaceclient2.base.LayerBean;
import com.simoncherry.averagefaceclient2.base.ListScrollStateBean;
import com.simoncherry.averagefaceclient2.bean.DirectoryBean;
import com.simoncherry.averagefaceclient2.event.onChangeDirectoryEvent;
import com.simoncherry.averagefaceclient2.event.onRefreshEvent;
import com.simoncherry.averagefaceclient2.presenter.FacesetPresenter;
import com.simoncherry.averagefaceclient2.presenter.impl.FacesetPresenterImpl;
import com.simoncherry.averagefaceclient2.util.ImageLoader;
import com.simoncherry.averagefaceclient2.view.FacesetView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
    // TODO
    private String type = MyApplication.TAG_FACESET;
    private int scrolledX = 0;
    private int scrolledY = 0;
    //


    public interface OnFragmentInteractionListener {
        void setInDiretory(Boolean isInDir);
    }

    private FacesetPresenter presenter;

    public FacesetFragment() {
        // Required empty public constructor
    }

    public FacesetFragment newInstance(String type){
        FacesetFragment myFacesetFragment = new FacesetFragment();

        Bundle args = new Bundle();
        args.putString("type", type);
        myFacesetFragment.setArguments(args);

        return myFacesetFragment;
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
        // TODO
        //presenter = new FacesetPresenterImpl(this);
        //
        //mListener.setInDiretory(false);
        EventBus.getDefault().register(this);
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
//                if(isInDirectory){
//                    // TODO
//                    //presenter.getFacesetPhoto(currentDirectory);
//                    presenter.getFacesetPhoto(LayerBean.getDirectory());
//                }else{
//                    presenter.getFacesetDirectory();
//                }
                if(LayerBean.getLayer() == 0){
                    presenter.getFacesetDirectory();
                }else if(LayerBean.getLayer() == 1){
                    presenter.getFacesetPhoto(LayerBean.getDirectory());
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

        // TODO
        type = getArguments().getString("type");
        presenter = new FacesetPresenterImpl(this, type);
        //

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        showLoadingScene();
        // TODO
        if(LayerBean.getLayer() == 0) {
            presenter.getFacesetDirectory();
        }else if(LayerBean.getLayer() == 1){
            presenter.getFacesetPhoto(LayerBean.getDirectory());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
//        Logger.e("set X", String.valueOf(scrolledX));
//        Logger.e("set Y", String.valueOf(scrolledY));
//        ListScrollStateBean.setScrolledX(scrolledX);
//        ListScrollStateBean.setScrolledY(scrolledY);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
                // TODO
                LayerBean.setLayer(1);
                LayerBean.setDirectory(path);
                EventBus.getDefault().post(new onChangeDirectoryEvent());
                //
                presenter.getFacesetPhoto(path);
                //mListener.setInDiretory(true);
            }
        });

        list_dir.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    int index = list_dir.getFirstVisiblePosition();
                    View v = list_dir.getChildAt(0);
                    int top = (v == null) ? 0 : v.getTop();
                    //Logger.t("set index").e(String.valueOf(index));
                    //Logger.t("set top").e(String.valueOf(top));
                    ListScrollStateBean.setIndex(index);
                    ListScrollStateBean.setTop(top);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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
                // TODO
                LayerBean.setLayer(2);
                EventBus.getDefault().post(new onChangeDirectoryEvent());
                //
            }
        });

        gv_img.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    int index = gv_img.getFirstVisiblePosition();
                    View v = gv_img.getChildAt(0);
                    int top = (v == null) ? 0 : v.getTop();
                    //Logger.t("set index").e(String.valueOf(index));
                    //Logger.t("set top").e(String.valueOf(top));
                    GridScrollStateBean.setIndex(index);
                    GridScrollStateBean.setTop(top);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
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
        // TODO
        LayerBean.setLayer(0);
        EventBus.getDefault().post(new onChangeDirectoryEvent());

        int index = ListScrollStateBean.getIndex();
        int top = ListScrollStateBean.getTop();
        //Logger.t("get index").e(String.valueOf(index));
        //Logger.t("get top").e(String.valueOf(top));
        list_dir.setSelectionFromTop(index, top);
        //
    }

    @Override
    public void showFacesetPhoto(String[] imagePath) {
        //ListAdapter adapter = new ListImgItemAdaper(getActivity(), 0, imagePath);
        adapter_photo = new ListImgItemAdaper(getActivity(), 0, imagePath);
        gv_img.setAdapter(adapter_photo);
        ptrFrame.refreshComplete();
        // TODO
        LayerBean.setLayer(1);
        EventBus.getDefault().post(new onChangeDirectoryEvent());

        int index = GridScrollStateBean.getIndex();
        int top = GridScrollStateBean.getTop();
        //Logger.t("get index").e(String.valueOf(index));
        //Logger.t("get top").e(String.valueOf(top));
        gv_img.setSelection(index);
        //
    }

    public void resumeFaceSet(){
        if(mPhotoDraweeView != null){
            mPhotoDraweeView.setVisibility(View.GONE);
        }
        if(gv_img != null){
            gv_img.setVisibility(View.VISIBLE);
            int index = GridScrollStateBean.getIndex();
            gv_img.setSelection(index);
        }
        // TODO
        LayerBean.setLayer(1);
        EventBus.getDefault().post(new onChangeDirectoryEvent());
        //
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(onRefreshEvent event){
        Logger.e("onEvent");
        if(LayerBean.getLayer() == 0) {
            presenter.getFacesetDirectory();
        }else if(LayerBean.getLayer() == 1){
            presenter.getFacesetPhoto(LayerBean.getDirectory());
        }else if(LayerBean.getLayer() == 2){
            resumeFaceSet();
        }
    }
}
