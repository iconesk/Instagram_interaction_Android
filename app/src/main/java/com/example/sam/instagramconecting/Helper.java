package com.example.sam.instagramconecting;

import com.example.parsinginstagramtest.MainActivity.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.content.Context;

// Helping Methodes Class

public class Helper {

	public static AnimateFirstDisplayListener mAnimator;

    // A methode to initiate imageLoader with options
	public static void initImageLoaderOptions(Context context, ImageLoader mImageLoader){

		DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.instagram_logo)
				.showImageOnFail(R.drawable.instagram_logo)
				.cacheInMemory(true)
				.cacheOnDisc(false)
				.considerExifParams(true)
				.build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)                		                       
				.writeDebugLogs()
				.defaultDisplayImageOptions(displayOptions)		        
				.build();

		mImageLoader.init(config);
		mAnimator  = new AnimateFirstDisplayListener();


	}
}
