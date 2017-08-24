package com.example.t_robop.matomemo;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;

/**
 * Created by taiga on 2017/08/23.
 */

public class UIAnimation {
    /****************************************************
     * 指定したビューのアルファ値をアニメーションさせながら変化させる
     *
     * @param view Viewオブジェクト
     * @param toAlpha 変化させるアルファ値(0.0～1.0)
     * @param duration アニメーション時間(単位：ミリ秒)
     ****************************************************/
    public static void startAlpha(View view, float toAlpha, long duration) {
        UIAnimation.startAlpha(view, toAlpha, duration, 0, null);
    }

    /****************************************************
     * 指定したビューのアルファ値をアニメーションさせながら変化させる
     *
     * @param view Viewオブジェクト
     * @param toAlpha 変化させるアルファ値(0.0～1.0)
     * @param duration アニメーション時間(単位：ミリ秒)
     * @param startDelay アニメーションの開始待ち(単位：ミリ秒)
     ****************************************************/
    public static void startAlpha(View view, float toAlpha,
                                  long duration, long startDelay) {
        UIAnimation.startAlpha(view, toAlpha, duration, startDelay, null);
    }

    /****************************************************
     * 指定したビューのアルファ値をアニメーションさせながら変化させる
     *
     * @param view Viewオブジェクト
     * @param toAlpha 変化させるアルファ値(0.0～1.0)
     * @param duration アニメーション時間(単位：ミリ秒)
     * @param startDelay アニメーションの開始待ち(単位：ミリ秒)
     * @param listener アニメーションリスナー
     ****************************************************/
    public static void startAlpha(View view, float toAlpha,
                                  long duration, long startDelay, Animator.AnimatorListener listener) {
        ObjectAnimator animation
                = ObjectAnimator.ofFloat(view, "alpha", toAlpha);
        animation.setStartDelay(startDelay);
        animation.setDuration(duration);
        animation.addListener(listener);
        animation.start();
    }
}
