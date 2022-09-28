package com.example.devolucionmaterial.customview.behavior;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by EDGAR ARANA on 10/04/2017.
 */


public class Float extends FloatingActionButton.Behavior {
    public Float(Context context, AttributeSet attrs) {
    }

    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if(dyConsumed > 0) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams)child.getLayoutParams();
            int fab_bottomMargin = layoutParams.bottomMargin;
            child.animate().translationY((float)(child.getHeight() + fab_bottomMargin)).setInterpolator(new LinearInterpolator()).setDuration(150L);
        } else if(dyConsumed < 0) {
            child.animate().translationY(0.0F).setInterpolator(new LinearInterpolator()).setDuration(150L);
        }

    }

    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == 2;
    }
}
