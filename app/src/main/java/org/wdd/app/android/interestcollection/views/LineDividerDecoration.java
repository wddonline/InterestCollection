package org.wdd.app.android.interestcollection.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by richard on 9/15/15.
 */
public class LineDividerDecoration extends RecyclerView.ItemDecoration{

    /*
    * RecyclerView的布局方向，默认先赋值
    * 为纵向布局
    * RecyclerView 布局可横向，也可纵向
    * 横向和纵向对应的分割想画法不一样
    * */
    private int mOrientation = LinearLayoutManager.VERTICAL ;

    /**
     * item之间分割线的size，默认为1
     */
    private int mItemSize = 1;

    /**
     * 绘制item分割线的画笔，和设置其属性
     * 来绘制个性分割线
     */
    private Paint mPaint ;
    private int mDividerColor;
    private int leftOffset = 0;
    private int rightOffset = 0;

    public LineDividerDecoration(Context context) {
        this(context, LinearLayoutManager.VERTICAL, Color.parseColor("#cccccc"));
    }

    public LineDividerDecoration(Context context, int orientation) {
        this(context, orientation, Color.parseColor("#cccccc"));
    }

    public LineDividerDecoration(Context context, int orientation, int dividerColor) {
        this.mOrientation = orientation;
        this.mDividerColor = dividerColor;
        if(orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL){
            throw new IllegalArgumentException("请传入正确的参数") ;
        }
        mItemSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mItemSize, context.getResources().getDisplayMetrics());
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG) ;
        mPaint.setColor(mDividerColor);
         /*设置填充*/
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == LinearLayoutManager.VERTICAL){
            drawVertical(c,parent) ;
        }else {
            drawHorizontal(c,parent) ;
        }
    }

    /**
     * 绘制纵向 item 分割线
     * @param canvas
     * @param parent
     */
    private void drawVertical(Canvas canvas,RecyclerView parent){
        final int left = parent.getPaddingLeft() + leftOffset ;
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight() - rightOffset;
        final int childSize = parent.getChildCount() ;
        View child;
        RecyclerView.LayoutParams layoutParams;
        for(int i = 0 ; i < childSize ; i ++){
            child = parent.getChildAt( i ) ;
            layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin ;
            final int bottom = top + mItemSize ;
            canvas.drawRect(left,top,right,bottom,mPaint);
        }
    }

    /**
     * 绘制横向 item 分割线
     * @param canvas
     * @param parent
     */
    private void drawHorizontal(Canvas canvas,RecyclerView parent){
        final int top = parent.getPaddingTop() ;
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom() ;
        final int childSize = parent.getChildCount() ;
        View child;
        RecyclerView.LayoutParams layoutParams;
        for(int i = 0 ; i < childSize - 1; i++){
            child = parent.getChildAt( i ) ;
            layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin ;
            final int right = left + mItemSize ;
            canvas.drawRect(left,top,right,bottom,mPaint);
        }
    }

    /**
     * 设置item分割线的size
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == LinearLayoutManager.VERTICAL){
            outRect.set(0, 0, 0, mItemSize);
        }else {
            outRect.set(0, 0, mItemSize, 0);
        }
    }

    public void setLeftOffset(int leftOffset) {
        this.leftOffset = leftOffset;
    }

    public void setRightOffset(int rightOffset) {
        this.rightOffset = rightOffset;
    }
}