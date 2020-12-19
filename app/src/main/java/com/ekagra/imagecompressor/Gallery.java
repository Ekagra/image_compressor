package com.ekagra.imagecompressor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;


import com.bumptech.glide.Glide;
import com.theophrast.ui.widget.SquareImageView;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by User on 5/28/2017.
 */

public class Gallery extends AppCompatActivity {
    private static final String TAG = "GalleryFragment";

    //constants
    private static final int NUM_GRID_COLUMNS = 3;


    //widgets
    private GridView gridView;
    private SquareImageView galleryImage;
        private ImageView next;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;
    String total;

    //vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private Activity a;
    private String mSelectedImage;

    private Context c;
    Context context;


    Bitmap bm;
    private Context mContext = Gallery.this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_image);
        galleryImage = (SquareImageView) findViewById(R.id.galleryImageView);
        gridView = (GridView) findViewById(R.id.gridView);
        directorySpinner = (Spinner) findViewById(R.id.spinnerDirectory);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        next = (ImageView) findViewById(R.id.tvNext);
        mProgressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();
        this.c = context;

        init();

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryImage.buildDrawingCache();
                bm = galleryImage.getDrawingCache();
                new compress().execute(bm);

            }
        });

    }



    private void init(){
        FilePaths filePaths = new FilePaths();

        //check for other folders indide "/storage/emulated/0/pictures"
        if (FileSearch.getDirectoryPaths(filePaths.PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }
        directories.add(filePaths.CAMERA);

        ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < directories.size(); i++) {
            Log.d(TAG, "init: directory: " + directories.get(i));
            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index);
            directoryNames.add(string);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + directories.get(position));

                //setup our image grid for the directory chosen
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setupGridView(String selectedDirectory){
        Log.d(TAG, "setupGridView: directory chosen: " + selectedDirectory);
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);

        //set the grid column width
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use the grid adapter to adapter the images to gridview
        GalleryGridImageAdapter adapter = new GalleryGridImageAdapter(mContext, R.layout.gallery_grid_imageview, mAppend, imgURLs);
        gridView.setAdapter(adapter);

        //set the first image to be displayed when the activity fragment view is inflated
        try{
            setImage(imgURLs.get(0), galleryImage, mAppend);
            mSelectedImage = imgURLs.get(0);
        }catch (ArrayIndexOutOfBoundsException e){
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " +e.getMessage() );
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an image: " + imgURLs.get(position));

                setImage(imgURLs.get(position), galleryImage, mAppend);
                mSelectedImage = imgURLs.get(position);
            }
        });

    }


    private void setImage(String imgURL, SquareImageView image, String append){
        Log.d(TAG, "setImage: setting image " + append+imgURL);

        Activity a = (Activity) mContext;

        Glide.with(a).load(imgURL)
                .error(R.mipmap.dummy)
                .into(image);
    }





    public class compress extends AsyncTask<Bitmap, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {

            Log.d(TAG, "background process started.");


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100 ,stream);
            byte[] bytearray = stream.toByteArray();

            Log.d(TAG, "background process execution" + bytearray.length/1024 + " kb");

            if(bytearray.length/1024 <=10000 && bytearray.length/1024 >=800 ){

                Log.d(TAG, "onClick: navigating to the final share screen after compressing.");


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, baos);//Compression quality, here 100 means no compression, the storage of compressed data to baos
                int options = 90;
                while (baos.toByteArray().length / 1024 <= (baos.toByteArray().length / 1024)/15) {  //Loop if compressed picture is greater than 400kb, than to compression
                    baos.reset();//Reset baos is empty baos
                    bm.compress(Bitmap.CompressFormat.JPEG, options, baos);//The compression options%, storing the compressed data to the baos
                    options -= 10;//Every time reduced by 10
                }
                ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//The storage of compressed data in the baos to ByteArrayInputStream
                bm = BitmapFactory.decodeStream(isBm, null, null);//The ByteArrayInputStream data generation
                bytearray = baos.toByteArray();



                    Intent intent = new Intent(Gallery.this, MainActivity.class);
                    intent.putExtra("image", bytearray);
                    startActivity(intent);


            }



            else {
                Log.d(TAG, "came on else");

                if (bytearray.length / 1024 < 800) {


                        Intent intent = new Intent(Gallery.this, MainActivity.class);
                        intent.putExtra("image", bytearray);
                        startActivity(intent);


                }


                else if (bytearray.length / 1024 > 10000 && bytearray.length / 1024 < 20000) {


                    Log.d(TAG, "it came here");


                    Log.d(TAG, "onClick: navigating to the final share screen after compressing.");


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 100, baos);//Compression quality, here 100 means no compression, the storage of compressed data to baos
                    int options = 90;
                    while (baos.toByteArray().length / 1024 <= (baos.toByteArray().length / 1024) / 20) {  //Loop if compressed picture is greater than 400kb, than to compression
                        baos.reset();//Reset baos is empty baos
                        bm.compress(Bitmap.CompressFormat.JPEG, options, baos);//The compression options%, storing the compressed data to the baos
                        options -= 10;//Every time reduced by 10
                    }
                    ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//The storage of compressed data in the baos to ByteArrayInputStream
                    bm = BitmapFactory.decodeStream(isBm, null, null);//The ByteArrayInputStream data generation
                    bytearray = baos.toByteArray();




                        Intent intent = new Intent(Gallery.this, MainActivity.class);
                        intent.putExtra("image", bytearray);
                        startActivity(intent);


                }



                else if(bytearray.length / 1024 > 20000){

                    Intent intent = new Intent(Gallery.this, MainActivity.class);
                    intent.putExtra("image", bytearray);
                    startActivity(intent);

                }



            }



            return bm;
        }





        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);



        }
    }


}