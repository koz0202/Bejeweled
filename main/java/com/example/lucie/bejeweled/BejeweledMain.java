package com.example.lucie.bejeweled;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.MessageQueue;
import android.util.Log;
import android.widget.CheckBox;

import static android.content.ContentValues.TAG;

public class BejeweledMain extends Activity {

    static MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bejeweled_main);

    }


    @Override
    public void onPause()
    {
        if(mediaPlayer != null)
        {
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }catch(Exception e) {

            }
        }
        super.onPause();
    }


   public void bejSound(){


       Boolean chec = this.getIntent().getExtras().getBoolean("check");

       if(chec) {


           mediaPlayer = MediaPlayer.create(this, R.raw.ping);
           mediaPlayer.start();

           mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
               public void onCompletion(MediaPlayer mp) {

                   mp.stop();
                   mp.reset();
                   mp.release();
               };
           });

       }

   }

    public void getBack(){
        Intent intent = new Intent();
        setResult(1, intent);
        finish();
    }


}
