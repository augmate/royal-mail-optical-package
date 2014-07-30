package com.augmate.sdk.voice;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by Darien on 7/30/2014.
 */
public class AnimThread extends Thread {


    private ImageView myImage;
    private Animation myAnim;

    public AnimThread(ImageView myImage, Animation myAnim) {
        this.myImage = myImage;
        this.myAnim = myAnim;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            if(myAnim.hasEnded())
                myAnim.start();
        }
    }
}
