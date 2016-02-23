package com.example.solution_color;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Picture;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.library.bitmap_utilities.BitMap_Helpers;

import java.io.File;

public class MainActivity extends AppCompatActivity  {

    private ImageView camera, background;

    private final int TAKE_PICTURE = 1;

    private final String PREF_FILE_NAME = "Preferences";
    private final String DEFAULT_PATH   = "";

    private String Path_To_Picture = DEFAULT_PATH;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        camera = (ImageView) findViewById(R.id.camera);
        background = (ImageView) findViewById(R.id.background);
        getPref(); // get preferences if there are any

        if (!Path_To_Picture.equals(DEFAULT_PATH)) { //if the current path is not ""
            changeBackgroundImage(Camera_Helpers.loadAndScaleImage(Path_To_Picture,
                    background.getWidth(), background.getHeight()));
        }

        setContentView(R.layout.activity_main);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Bitmap currentBg, sketchedBG, coloredBG;
        switch (item.getItemId()) {

            case R.id.action_settings:
                Intent myIntent = new Intent(this, SettingsActivity.class);
                startActivity(myIntent);
                break;
            case R.id.action_share:
                //do share stuff
                break;
            case R.id.colorize:
                currentBg = BitMap_Helpers.copyBitmap(background.getDrawable());
                sketchedBG    = BitMap_Helpers.thresholdBmp(currentBg, 50);
                coloredBG     = BitMap_Helpers.colorBmp(currentBg, 125);
                BitMap_Helpers.merge(coloredBG,sketchedBG); //colored BG merged w/ sketch
                changeBackgroundImage(coloredBG); // change to merged picture

                break;
            case R.id.black_and_white:
                currentBg = BitMap_Helpers.copyBitmap(background.getDrawable());
                sketchedBG     = BitMap_Helpers.thresholdBmp(currentBg, 50);
                changeBackgroundImage(sketchedBG);


                break;
            case R.id.restore:
                removeSavedPhoto();
                background.setImageResource(R.drawable.gutters);
                Path_To_Picture = "";
                changeBackgroundImage(null);


                break;
            default:
                break;
        }
        return true;
    }

    public void takePhoto(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File outFile = Environment.getExternalStorageDirectory();
        Uri  output  = Uri.fromFile(outFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,output);

        startActivityForResult(intent, TAKE_PICTURE);
    }

    /**
     * Checks for if the path is not the default path
     * if it is not the default path then the photo is removed
     */
    private void removeSavedPhoto() {
        if (!Path_To_Picture.equals(DEFAULT_PATH)) {
            Camera_Helpers.delSavedImage(Path_To_Picture);
            Path_To_Picture = DEFAULT_PATH;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
           switch (resultCode) {
               case RESULT_OK:
                   removeSavedPhoto();
                   Path_To_Picture = data.getData().getPath();
                   //File picture = new File(data.getData().getPath());
                   int targetW  = background.getWidth();
                   int targetH  = background.getHeight();

                   // get dimensions of bitmap
                   BitmapFactory.Options bmOps = new BitmapFactory.Options();
                   bmOps.inJustDecodeBounds = true;
                   BitmapFactory.decodeFile(Path_To_Picture);
                   int photoW = bmOps.outWidth;
                   int photoH = bmOps.outHeight;

                   // Determine how much to scale down the image
                   int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

                   // Decode the image file into a Bitmap sized to fill the View
                   bmOps.inJustDecodeBounds = false;
                   bmOps.inSampleSize = scaleFactor;
                   //bmOps.inPurgeable = true;

                   Bitmap bitmap = BitmapFactory.decodeFile(Path_To_Picture, bmOps);
                   background.setImageBitmap(bitmap);
                   Camera_Helpers.saveProcessedImage(bitmap, Path_To_Picture);
                   savePref();
                   break;

               case RESULT_CANCELED:
                   // do nothing?
                   break;

               default:
                   break;
           }
        }
    }

    private void changeBackgroundImage(Bitmap photo) {
        if (photo != null) background.setImageBitmap(photo);
        background.setScaleType(ImageView.ScaleType.FIT_CENTER);
        background.setScaleType(ImageView.ScaleType.FIT_XY);
    }


    public void getPref() {
        SharedPreferences settings = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);

        Path_To_Picture = settings.getString("Current Picture Path", DEFAULT_PATH);
    }

    public void savePref() {
        SharedPreferences settings = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();

        editor.putString("Current Picture Path", Path_To_Picture);
        editor.commit();
    }
}

