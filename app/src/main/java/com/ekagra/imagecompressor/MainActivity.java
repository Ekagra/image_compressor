package com.ekagra.imagecompressor;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    //bitmap
    Bitmap bm;

    //wids
    ImageView imageView;
    Button button;
    private Popup popup;
    private Context mContext = MainActivity.this;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        imageView = (ImageView) findViewById(R.id.image);
        button = (Button) findViewById(R.id.compress);
        popup = new Popup(mContext);

        start();

    }

    private void start(){

        Intent intent = getIntent();
        if(intent.hasExtra("image")){
            byte[] bytearray = intent.getByteArrayExtra("image");
            bm = BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);

            Glide.with(this).load(bm).
                    into(imageView);

            button.setText("compress this image");

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    storeImage(bm);
                    popup.makeText(mContext, "compressed", "your compressed photo is saved on " + filename(), 1).show();
                }
            });



    }else {
            button.setText("choose an image to compress");

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(checkPermissionsArray(com.ekagra.imagecompressor.Permissions.PERMISSIONS)){
                        Intent intent = new Intent(MainActivity.this, Gallery.class);
                        startActivity(intent);
                        Log.d(TAG, "permission granted");
                    }else{
                        verifyPermissions(Permissions.PERMISSIONS);
                        Log.d(TAG, "no permission");
                    }


                }
            });
        }

    }


    private static void storeImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/ekagra_compressed_image");
        myDir.mkdirs();


        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String fname = "compressed-"+timeStamp+".jpg";
        File file = new File (myDir, fname);
        if (file.exists ()) file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String filename(){
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        String fname = "compressed-"+timeStamp+".jpg";

        return "/ekagra_compressed_image/"+fname;
    }






    /**
     * verifiy all the permissions passed to the array
     * @param permissions
     */
    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                MainActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }



    /**
     * Check an array of permissions
     * @param permissions
     * @return
     */
    public boolean checkPermissionsArray(String[] permissions){
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for(int i = 0; i< permissions.length; i++){
            String check = permissions[i];
            if(!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }

    /**
     * Check a single permission is it has been verified
     * @param permission
     * @return
     */
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(MainActivity.this, permission);

        if(permissionRequest != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        }
        else{
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

}