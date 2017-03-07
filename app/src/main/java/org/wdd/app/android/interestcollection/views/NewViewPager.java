package org.wdd.app.android.interestcollection.views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by richard on 3/7/17.
 */

public class NewViewPager extends ViewPager {

    private boolean canSwitch = true;

    public NewViewPager(Context context) {
        super(context);
    }

    public NewViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCanSwitch(boolean canSwitch) {
        this.canSwitch = canSwitch;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!canSwitch) return false;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!canSwitch) return false;
        return super.onTouchEvent(ev);
    }
}
