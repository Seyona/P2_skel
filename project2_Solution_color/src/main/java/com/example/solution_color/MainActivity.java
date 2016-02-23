package com.example.solution_color;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.library.bitmap_utilities.BitMap_Helpers;
import com.library.bitmap_utilities.ManipBitmap;

public class MainActivity extends AppCompatActivity  {

    private ImageView camera, background;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        camera = (ImageView) findViewById(R.id.camera);
        background = (ImageView) findViewById(R.id.background);


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
                changePhoto(coloredBG); // change to merged picture

                break;
            case R.id.black_and_white:
                currentBg = BitMap_Helpers.copyBitmap(background.getDrawable());
                sketchedBG     = BitMap_Helpers.thresholdBmp(currentBg, 50);
                changePhoto(sketchedBG);


                break;
            case R.id.restore:
                background.setImageResource(R.drawable.gutters);
                changePhoto(null);


                break;
            default:
                break;
        }
        return true;
    }

    public void takePhoto(View view) {

    }

    private void changePhoto(Bitmap photo) {
        if (photo != null) background.setImageBitmap(photo);
        background.setScaleType(ImageView.ScaleType.FIT_CENTER);
        background.setScaleType(ImageView.ScaleType.FIT_XY);
    }
}

