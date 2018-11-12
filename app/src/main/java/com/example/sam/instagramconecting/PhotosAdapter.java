package com.example.sam.instagramconecting;

import java.util.ArrayList;

import com.example.parsinginstagramtest.MainActivity.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


// Adapter Class to be used with GridView to display Retrieved Instagram pictures

public class PhotosAdapter extends BaseAdapter {

private Context mContext;
	
	private ImageLoader mImageLoader;
	private static AnimateFirstDisplayListener mAnimator;
	
	private ArrayList<String> mPhotoList;
	
	private int mWidth;
	private int mHeight;
	
	public PhotosAdapter(Context context) {
		mContext = context;
		
    //initialize Imageloader object parameters
		mImageLoader = ImageLoader.getInstance();
		Helper.initImageLoaderOptions(mContext,mImageLoader) ;
 
	}
	
	public void setData(ArrayList<String> data) {
		mPhotoList = data;
	}
	
	public void setLayoutParam(int width, int height) {
		mWidth 	= width;
		mHeight = height;
	}
	
	@Override
	public int getCount() {
		return (mPhotoList == null) ? 0 : mPhotoList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageIv;
		
		if (convertView == null) {
			imageIv = new ImageView(mContext);
			
			imageIv.setLayoutParams(new GridView.LayoutParams(mWidth, mHeight));
            imageIv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageIv.setPadding(0, 0, 0, 0); 
		} else {
			imageIv = (ImageView) convertView;
		}
		
		mImageLoader.displayImage(mPhotoList.get(position), imageIv, mAnimator);
		
		imageIv.setOnClickListener( new OnImageClickListener(position){} );
		
		return imageIv;
	}

	
	
	class OnImageClickListener implements View.OnClickListener {

		int _position;

		// constructor
		public OnImageClickListener(int position) {
			this._position = position;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			showImageAlert(_position);

		}

	}
	
	
	//Show pop up Alert with clicked image  
	public void showImageAlert(int position){
		
        ImageView image = new ImageView(mContext);
		mImageLoader.displayImage(mPhotoList.get(position), image, mAnimator);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
              
        builder.setNegativeButton("OK", new OnClickListener() {                     
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                    }
                }).
                setView(image);
         builder.create().getWindow().setLayout(500,800);
         builder.show();
 
		
	}
	
	


	
 
}
