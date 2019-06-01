package com.xingqige.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;

import com.xingqige.criminalintent.utils.PictureUtils;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

public class ImageViewFragment extends DialogFragment {

    /** Called when the activity is first created. */
    Bitmap bp = null;
    ImageView mImageView;
    float scaleWidth;
    float scaleHeight;

    int h;
    boolean num = false;

    private View mFragmentImageView;

    private File mPhotoFile;

    private final static String PHOTO_FILE = "photoFile";

    public static ImageViewFragment newInstance(File photoFile) {
        Bundle args = new Bundle();
        args.putSerializable(PHOTO_FILE, photoFile);
        ImageViewFragment fragment = new ImageViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPhotoFile = (File)getArguments().getSerializable(PHOTO_FILE);
    }

    private View setView() {
        DisplayMetrics dm=new DisplayMetrics();//创建矩阵
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        mImageView=(ImageView)mFragmentImageView.findViewById(R.id.image_view);

//        bp = BitmapFactory.decodeResource(getResources(),R.drawable.xiaoyua);
        bp = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());

        int width=bp.getWidth();
        int height=bp.getHeight();
        int w=dm.widthPixels; //得到屏幕的宽度
        int h=dm.heightPixels; //得到屏幕的高度
        scaleWidth=((float)w)/width;
        scaleHeight=((float)h)/height;
        mImageView.setImageBitmap(bp);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return mFragmentImageView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        mFragmentImageView = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_image_view, null);

        setView();

        return new AlertDialog.Builder(getActivity())
                .setView(mFragmentImageView)
//                .setPositiveButton(android.R.string.ok,
//                        new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                sendResult();
//                            }
//                        })
                .create();
    }

    private void sendResult(){
        if (getTargetFragment() == null){
            return;
        }

        Intent intent = new Intent();

        getTargetFragment().onActivityResult(getTargetRequestCode(),1, intent);
    }
}
