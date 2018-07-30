package com.example.ga.rps.view;

import android.content.Context;
import android.support.animation.DynamicAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.ga.rps.R;
import com.example.ga.rps.data.HandType;
import com.example.ga.rps.util.ScreenUtils;

import java.util.Random;

/**
 * Created by GA on 2018. 4. 14..
 */

public class HandView extends FrameLayout {
    //from hand type, arm type, to hand type, center x, center y
    private LottieAnimationView handView;

    public HandView(@NonNull Context context) {
        this(context, null);
    }

    public HandView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HandView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.hand, this, true);

        handView = findViewById(R.id.hand_animation);
    }

    public void setHandType(int armType, HandType fromHandType, HandType toHandType) {
        ImageView arm = findViewById(R.id.arm);
        arm.setImageResource(getArmResId(armType));
        handView.setAnimation(String.format("Hand%sTo%s.json", fromHandType.getAttr(), toHandType.getAttr()));
    }

    private int getArmResId(int armType) {
        switch (armType) {
            case 1:
                return R.drawable.arm1;
            case 2:
                return R.drawable.arm2;
            case 3:
                return R.drawable.arm3;
            case 4:
                return R.drawable.arm4;
            case 5:
                return R.drawable.arm5;
            case 6:
                return R.drawable.arm6;
            case 7:
                return R.drawable.arm7;
            case 8:
                return R.drawable.arm8;
            default:
            case 0:
                return R.drawable.arm9;
        }
    }

    public HandView setPosition(float x, float y) {
        //로띠 여백 제거
        setTranslationX(x - ScreenUtils.dpToPx(25) + (new Random()).nextInt(200) - 100);
        setTranslationY(Math.max(Math.min(ScreenUtils.getScreenSize().heightPixels - 400, y - 300), 100));
        return this;
    }

    public HandView setRotation(float offset, float toX, float toY) {
        setPivotX(ScreenUtils.dpToPx(25));
        setPivotY(150);
        setRotation(offset + (float) Math.toDegrees(Math.atan(toX / toY)));
        return this;
    }

    public void showWithAnimation() {
        setVisibility(View.VISIBLE);
        SpringAnimation xAxisAnimation = new SpringAnimation(this, DynamicAnimation.TRANSLATION_X, this.getX());
        xAxisAnimation.setSpring(new SpringForce(this.getX()).setStiffness(SpringForce.STIFFNESS_LOW));
        if (this.getRotation() < 180) {
            xAxisAnimation.setStartValue(0.0f);
        } else {
            xAxisAnimation.setStartValue(ScreenUtils.getScreenSize().widthPixels);
        }

        SpringAnimation yAxisAnimation = new SpringAnimation(this, DynamicAnimation.TRANSLATION_Y, this.getY());
        yAxisAnimation.setSpring(new SpringForce(this.getY()).setStiffness(SpringForce.STIFFNESS_LOW));
        if (this.getRotation() < 90 || this.getRotation() > 270) {
            yAxisAnimation.setStartValue(ScreenUtils.getScreenSize().heightPixels);
        } else {
            yAxisAnimation.setStartValue(0.0f);
        }

        xAxisAnimation.start();
        yAxisAnimation.start();
    }

    public void startHandGame() {

        Animation animation;
        if (this.getRotation() < 180) {
            if (this.getRotation() < 90 || this.getRotation() > 270) {
                animation = AnimationUtils.loadAnimation(getContext(), R.anim.translate_left_bottom);
            } else {
                animation = AnimationUtils.loadAnimation(getContext(), R.anim.translate_left_top);
            }
        } else {
            if (this.getRotation() < 90 || this.getRotation() > 270) {
                animation = AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_bottom);
            } else {
                animation = AnimationUtils.loadAnimation(getContext(), R.anim.translate_right_top);
            }
        }

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                handView.playAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        startAnimation(animation);
    }
}
