package com.example.sam.instagramconecting;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private InstagramSession mInstagramSession;
    private Instagram mInstagram;

    private ProgressBar mLoadingPb;
    private GridView mGridView;


    // Keys obtaned during Instagram Developper Registration
    private static final String CLIENT_ID = "dde2900cbd6241f6b1afd0b0fe6ed4e2";
    private static final String CLIENT_SECRET = "fca11e7b90aa493782a2ed71fe9f87b3";
    private static final String REDIRECT_URI = "http://www.test123.ca";





    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        //Login to Instagram API and get the current Session

        mInstagram  		= new Instagram(this, CLIENT_ID, CLIENT_SECRET, REDIRECT_URI);
        mInstagramSession	= mInstagram.getSession();

        // if LOGED to instagram then RETRIEVE and DISPLAY  user informations, profile picture AND Recent posted Pictures

        if (mInstagramSession.isActive()) {
            setContentView(R.layout.activity_photos_adapter);

            InstagramUser instagramUser = mInstagramSession.getUser();

            mLoadingPb 	= (ProgressBar) findViewById(R.id.pb_loading);
            mGridView	= (GridView) findViewById(R.id.gridView);

            ((TextView) findViewById(R.id.tv_name)).setText(instagramUser.fullName);
            ((TextView) findViewById(R.id.tv_username)).setText(instagramUser.username);

            ((Button) findViewById(R.id.btn_logout)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mInstagramSession.reset();

                    startActivity(new Intent(MainActivity.this, MainActivity.class));

                    finish();
                }
            });

            ImageView userIv = (ImageView) findViewById(R.id.iv_user);

            ImageLoader imageLoader = ImageLoader.getInstance();
            Helper.initImageLoaderOptions(this, imageLoader);
            AnimateFirstDisplayListener animate  = new AnimateFirstDisplayListener();
            imageLoader.displayImage(instagramUser.profilPicture, userIv, animate);

            //execute Asyntax Class to get Recent Posted Picture

            new DownloadTask().execute();
        }


        // If not Loged to instagram, initiate Authentification

        else {
            setContentView(R.layout.activity_main);

            ((Button) findViewById(R.id.btn_connect)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mInstagram.authorize(mAuthListener);
                }
            });
        }

    }//End of OnCreate()




    // Instagram Authentification Class
    private Instagram.InstagramAuthListener mAuthListener = new Instagram.InstagramAuthListener() {

        @Override
        public void onError(String error) {
            showToast(error);
        }

        @Override
        public void onCancel() {
            showToast("OK. Maybe later?");

        }

        @Override
        public void onSuccess(InstagramUser arg0) {
            finish();
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        }
    };



    //Class to Display NEW images with a "fade in" animation

    public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }





    // Asynchronious Class that sends and Retrieve data Requests from Instagram API using JSON

    public class DownloadTask extends AsyncTask<URL, Integer, Long> {
        ArrayList<String> photoList;

        protected void onCancelled() {

        }

        protected void onPreExecute() {

        }

        @SuppressWarnings("deprecation")
        protected Long doInBackground(URL... urls) {
            long result = 0;

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>(1);

                params.add(new BasicNameValuePair("count", "10"));

                InstagramRequest request  = new InstagramRequest(mInstagramSession.getAccessToken());
                String           response = request.createRequest("GET", "/users/self/media/recent", params); // here is where we filter response by Calling Recent Media only

                if (!response.equals("")) {
                    JSONObject jsonObj  = (JSONObject) new JSONTokener(response).nextValue();
                    JSONArray jsonData	= jsonObj.getJSONArray("data");
                    //Log.d("SAM CHECKING JSON RESPONSE", jsonData.toString());

                    int length = jsonData.length();

                    if (length > 0) {
                        photoList = new ArrayList<String>();

                        //Extracting urls of low Resolution images from API JSON response
                        for (int i = 0; i < length; i++) {
                            JSONObject jsonPhoto = jsonData.getJSONObject(i).getJSONObject("images").getJSONObject("low_resolution");

                            photoList.add(jsonPhoto.getString("url"));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onProgressUpdate(Integer... progress) {
        }


        //Handling doInBacground Result

        protected void onPostExecute(Long result) {
            mLoadingPb.setVisibility(View.GONE);

            if (photoList == null) {
                Toast.makeText(getApplicationContext(), "No Available Photos", Toast.LENGTH_LONG).show();
            } else {

                // Display Retrieved user images on a GreedView

                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);

                int width 	= (int) Math.ceil((double) dm.widthPixels / 2);
                width   = width - 50;
                int height	= width;

                PhotosAdapter adapter = new PhotosAdapter(MainActivity.this);
                adapter.setData(photoList);
                adapter.setLayoutParam(width, height);

                mGridView.setAdapter(adapter);
            }
        }

    }



    //Display a Toast
    private void showToast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
}
