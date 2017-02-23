/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wdd.app.android.interestcollection.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;

public class PagerSlidingTabStrip extends HorizontalScrollView {

	// @formatter:off
	private static final int[] ATTRS = new int[] {
		android.R.attr.textSize,
		android.R.attr.textColor
    };
	// @formatter:on

	private LinearLayout.LayoutParams defaultTabLayoutParams;
	private LinearLayout.LayoutParams expandedTabLayoutParams;

	private final PageListener pageListener = new PageListener();
	private OnPageChangeListener delegatePageListener;

	private LinearLayout tabsContainer;
	private ViewPager pager;

	private int tabCount;

	private int currentPosition = 0;
	private float currentPositionOffset = 0f;

	private Paint rectPaint;
	private Paint dividerPaint;

	private boolean checkedTabWidths = false;

	private int indicatorColor = 0xFF0098f0;//指示器的颜色 来回滚动的条  原来#3560BF
	private int underlineColor = 0xFFcccccc;//tab 有一个下划线
	private int dividerColor = 0x1A000000;//竖向分割线的颜色

	private boolean shouldExpand = false;
	private boolean textEmphasized;
	
	private int scrollOffset = 52;
	private int indicatorWidth = -1;
	private int indicatorHeight = 2;
	private int underlineHeight = 0;
	private int dividerPadding = 4;//竖线 分割线的上下padding
	private int tabPadding = 10;//控制tab textview左右的padding 
	private int textLinePadding = 8;//控制tab textview和线的间距padding 
	private int dividerWidth = 0;

	
	private int tabTextSize = 20;
	private int tabTextColor = 0xFFFFFFFF;//选中的颜色值
	private int tabUnselectedTextColor = 0xFFFFFFFF;//没有选中的颜色值
	private Typeface tabTypeface = null;
	private int tabTypefaceStyle = Typeface.NORMAL;

	private int lastScrollX = 0;

	private int tabBackgroundResId = 0;

	private int screenWidth;
	
	private int screenHeight;

	private boolean isTwoTab = true;
	private int tab_num;
	
	public PagerSlidingTabStrip(Context context) {
		this(context, null);
	}

	public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		if(isInEditMode()) {
			return;
		}
		DisplayMetrics metric = new DisplayMetrics();
		((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(metric);
                screenWidth = metric.widthPixels;  // 屏幕宽度（像素）
                screenHeight = metric.heightPixels;  // 屏幕高度（像素）

		setFillViewport(true);
		setWillNotDraw(false);

		tabsContainer = new LinearLayout(context);
		tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
		tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		addView(tabsContainer);

		/*LayoutInflater lInflater=LayoutInflater.from(context);
		tabsContainer=(LinearLayout)lInflater.inflate(R.layout.mgd_tab_linear, null);
		addView(tabsContainer);*/

		DisplayMetrics dm = getResources().getDisplayMetrics();

		scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
		indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
		underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
		dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
		tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
		dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
		tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);
		textLinePadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textLinePadding, dm);

		// get system attrs (android:textSize and android:textColor)

		TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
		tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
		a.recycle();

		// get custom attrs

		a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);
		tabTextColor = a.getColor(R.styleable.PagerSlidingTabStrip_tabTextColor, tabTextColor);
		tabUnselectedTextColor = a.getColor(R.styleable.PagerSlidingTabStrip_tabUnselectedTextColor, tabUnselectedTextColor);
		textEmphasized = a.getBoolean(R.styleable.PagerSlidingTabStrip_indicatorColor, false);
		indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_indicatorColor, indicatorColor);
		underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_underlineColor, underlineColor);
		dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_dividerColor, dividerColor);
		indicatorWidth = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_indicatorWidth, indicatorWidth);
		indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_indicatorHeight, indicatorHeight);
		underlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_underlineHeight, underlineHeight);
		dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_dividerPadding, dividerPadding);
		tabPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_tabPaddingLeftRight, tabPadding);
		tabBackgroundResId = a.getResourceId(R.styleable.PagerSlidingTabStrip_tabBackground, tabBackgroundResId);
		shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_shouldExpand, shouldExpand);
		scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_scrollOffset, scrollOffset);

		a.recycle();

		rectPaint = new Paint();
		rectPaint.setAntiAlias(true);
		rectPaint.setStyle(Style.FILL);

		dividerPaint = new Paint();
		dividerPaint.setAntiAlias(true);
		dividerPaint.setStrokeWidth(dividerWidth);

		defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
	}

	public void setViewPager(ViewPager pager) {
		this.pager = pager;

		if (pager.getAdapter() == null) {
			throw new IllegalStateException("ViewPager does not have adapter getInstance.");
		}

		pager.setOnPageChangeListener(pageListener);

		notifyDataSetChanged();
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		this.delegatePageListener = listener;
	}

	public void notifyDataSetChanged() {

		tabsContainer.removeAllViews();

		tabCount = pager.getAdapter().getCount();

		for (int i = 0; i < tabCount; i++) {
			addTab(i, pager.getAdapter().getPageTitle(i).toString());
		}
		isTwoTab = tabCount <= 2;
		updateTabStyles();

		checkedTabWidths = false;

		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			@Override
			public void onGlobalLayout() {

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					getViewTreeObserver().removeGlobalOnLayoutListener(this);
				} else {
					getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}

				currentPosition = pager.getCurrentItem();
				scrollToChild(currentPosition, 0);
			}
		});

	}

	private void addTab(final int position, String title) {

		TextView tab = new TextView(getContext());
		tab.setText(title);
		tab.setFocusable(true);
		tab.setGravity(Gravity.CENTER);
		tab.setSingleLine();
		
		LinearLayout.LayoutParams mLayoutParamsss = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);// 三分之一
		
		LinearLayout ll=new LinearLayout(getContext());
		ll.setLayoutParams(mLayoutParamsss);// 三分之一
		ll.setGravity(Gravity.CENTER);

		ll.addView(tab);

		ll.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pager.setCurrentItem(position);
			}
		});

		tabsContainer.addView(ll);

	}

	/**
	 * 更新组件
	 */
	private void updateTabStyles() {

		for (int i = 0; i < tabCount; i++) {

			LinearLayout tab = (LinearLayout) tabsContainer.getChildAt(i);

			//tab.setLayoutParams(defaultTabLayoutParams);
			tab.setBackgroundResource(tabBackgroundResId);
			if (shouldExpand) {
				tab.setPadding(0, 0, 0, 0);
			} else {
				tab.setPadding(tabPadding, 0, tabPadding, textLinePadding);
			}
			((TextView)tab.getChildAt(1)).setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
			((TextView)tab.getChildAt(1)).setTypeface(tabTypeface, tabTypefaceStyle);
			((TextView)tab.getChildAt(1)).setTextColor(tabTextColor);
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		if (!shouldExpand || MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
			return;
		}

		int myWidth = getMeasuredWidth();
		int childWidth = 0;
		for (int i = 0; i < tabCount; i++) {
			childWidth += tabsContainer.getChildAt(i).getMeasuredWidth();
		}
		if (!checkedTabWidths && childWidth > 0 && myWidth > 0) {

			if (childWidth <= myWidth) {
				for (int i = 0; i < tabCount; i++) {
					tabsContainer.getChildAt(i).setLayoutParams(expandedTabLayoutParams);
				}
			}

			checkedTabWidths = true;
		}
	}

	private void scrollToChild(int position, int offset) {

		int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

		if (position > 0 || offset > 0) {
			newScrollX -= scrollOffset;
		}

		if (newScrollX != lastScrollX) {
			lastScrollX = newScrollX;
			scrollTo(newScrollX, 0);
		}

		updateTabColor(position);

	}

	private void updateTabColor(int position){
		TextView tab1 = (TextView) (((LinearLayout)tabsContainer.getChildAt(0)).getChildAt(1));
		TextView tab2 = (TextView) (((LinearLayout)tabsContainer.getChildAt(1)).getChildAt(1));
		TextView tab3 = null;
		
		if(!isTwoTab){
		    tab3 = (TextView) (((LinearLayout)tabsContainer.getChildAt(2)).getChildAt(1));
		}
		if (textEmphasized) {
			switch (position) {
				case 0:
					tab1.setTextColor(tabTextColor);
					TextPaint paint = tab1.getPaint();
					paint.setFakeBoldText(true);

					tab2.setTextColor(tabUnselectedTextColor);
					paint = tab2.getPaint();
					paint.setFakeBoldText(false);

					if (!isTwoTab) {
						tab3.setTextColor(tabUnselectedTextColor);
						paint = tab3.getPaint();
						paint.setFakeBoldText(false);
					}
					break;
				case 1:
					tab1.setTextColor(tabUnselectedTextColor);
					paint = tab1.getPaint();
					paint.setFakeBoldText(false);

					tab2.setTextColor(tabTextColor);
					paint = tab2.getPaint();
					paint.setFakeBoldText(true);

					if (!isTwoTab) {
						tab3.setTextColor(tabUnselectedTextColor);
						paint = tab3.getPaint();
						paint.setFakeBoldText(false);
					}

					break;
				case 2:
					tab1.setTextColor(tabUnselectedTextColor);
					paint = tab1.getPaint();
					paint.setFakeBoldText(false);

					tab2.setTextColor(tabUnselectedTextColor);
					paint = tab2.getPaint();
					paint.setFakeBoldText(false);

					if (!isTwoTab) {
						tab3.setTextColor(tabTextColor);
						paint = tab3.getPaint();
						paint.setFakeBoldText(true);
					}

					break;
				default:
					break;
			}
			tab1.invalidate();
			tab2.invalidate();
			if (!isTwoTab) {
				tab3.invalidate();
			}
		} else {
			switch (position) {
				case 0:
					tab1.setTextColor(tabTextColor);
					tab2.setTextColor(tabUnselectedTextColor);

					if (!isTwoTab) {
						tab3.setTextColor(tabUnselectedTextColor);
					}
					break;
				case 1:
					tab1.setTextColor(tabUnselectedTextColor);
					tab2.setTextColor(tabTextColor);
					if (!isTwoTab) {
						tab3.setTextColor(tabUnselectedTextColor);
					}

					break;
				case 2:
					tab1.setTextColor(tabUnselectedTextColor);
					tab2.setTextColor(tabUnselectedTextColor);

					if (!isTwoTab) {
						tab3.setTextColor(tabTextColor);
					}

					break;
				default:
					break;
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (isInEditMode() || tabCount == 0) {
			return;
		}

		final int height = getHeight();

		// draw indicator line

		rectPaint.setColor(indicatorColor);

		// default: line below current tab
		View currentTab = tabsContainer.getChildAt(currentPosition);
		float tabWidth = currentTab.getWidth();
		float lineLeft;
		float lineRight;
		if (indicatorWidth == -1) {
			lineLeft = currentTab.getLeft();
			lineRight = currentTab.getRight();
		} else {
			lineLeft = currentTab.getLeft() + (tabWidth - indicatorWidth) * 0.5f;
			lineRight = lineLeft + indicatorWidth;
		}

		// if there is an offset, start interpolating left and right coordinates between current and next tab
		if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

			View nextTab = tabsContainer.getChildAt(currentPosition + 1);
			tabWidth = nextTab.getWidth();
			float nextTabLeft;
			float nextTabRight;
			if (indicatorWidth == -1) {
				nextTabLeft = nextTab.getLeft();
				nextTabRight = nextTab.getRight();
			} else {
				nextTabLeft = nextTab.getLeft() + (tabWidth - indicatorWidth) * 0.5f;
				nextTabRight = nextTabLeft + indicatorWidth;
			}

			lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
			lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
		}

		canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, rectPaint);

		// draw underline

		rectPaint.setColor(underlineColor);
		canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

		// draw divider

//		dividerPaint.setColor(dividerColor);
//		for (int i = 0; i < tabCount - 1; i++) {
//			View tab = tabsContainer.getChildAt(i);
//			canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
//		}
	}

	public class PageListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			currentPosition = position;
			currentPositionOffset = positionOffset;

			scrollToChild(pager.getCurrentItem(), (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

			invalidate();

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				scrollToChild(pager.getCurrentItem(), 0);
			}

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrollStateChanged(state);
			}
		}

		@Override
		public void onPageSelected(int position) {
			if (delegatePageListener != null) {
				delegatePageListener.onPageSelected(position);
			}
		}

	}

	public void setIndicatorColor(int indicatorColor) {
		this.indicatorColor = indicatorColor;
		invalidate();
	}

	public void setIndicatorColorResource(int resId) {
		this.indicatorColor = getResources().getColor(resId);
		invalidate();
	}

	public int getIndicatorColor() {
		return this.indicatorColor;
	}

	public void setIndicatorHeight(int indicatorLineHeightPx) {
		this.indicatorHeight = indicatorLineHeightPx;
		invalidate();
	}

	public int getIndicatorHeight() {
		return indicatorHeight;
	}

	public void setUnderlineColor(int underlineColor) {
		this.underlineColor = underlineColor;
		invalidate();
	}

	public void setUnderlineColorResource(int resId) {
		this.underlineColor = getResources().getColor(resId);
		invalidate();
	}

	public int getUnderlineColor() {
		return underlineColor;
	}

	public void setDividerColor(int dividerColor) {
		this.dividerColor = dividerColor;
		invalidate();
	}

	public void setDividerColorResource(int resId) {
		this.dividerColor = getResources().getColor(resId);
		invalidate();
	}

	public int getDividerColor() {
		return dividerColor;
	}

	public void setUnderlineHeight(int underlineHeightPx) {
		this.underlineHeight = underlineHeightPx;
		invalidate();
	}

	public int getUnderlineHeight() {
		return underlineHeight;
	}

	public void setDividerPadding(int dividerPaddingPx) {
		this.dividerPadding = dividerPaddingPx;
		invalidate();
	}

	public int getDividerPadding() {
		return dividerPadding;
	}

	public void setScrollOffset(int scrollOffsetPx) {
		this.scrollOffset = scrollOffsetPx;
		invalidate();
	}

	public int getScrollOffset() {
		return scrollOffset;
	}

	public void setShouldExpand(boolean shouldExpand) {
		this.shouldExpand = shouldExpand;
		requestLayout();
	}

	public boolean getShuldExpand() {
		return shouldExpand;
	}

	public void setTextSize(int textSizePx) {
		this.tabTextSize = textSizePx;
		updateTabStyles();
	}

	public int getTextSize() {
		return tabTextSize;
	}

	public void setTextColor(int textColor) {
		this.tabTextColor = textColor;
		updateTabStyles();
	}

	public void setTextColorResource(int resId) {
		this.tabTextColor = getResources().getColor(resId);
		updateTabStyles();
	}

	public int getTextColor() {
		return tabTextColor;
	}

	public void setTypeface(Typeface typeface, int style) {
		this.tabTypeface = typeface;
		this.tabTypefaceStyle = style;
		updateTabStyles();
	}

	public void setTabBackground(int resId) {
		this.tabBackgroundResId = resId;
	}

	public int getTabBackground() {
		return tabBackgroundResId;
	}

	public void setTabPaddingLeftRight(int paddingPx) {
		this.tabPadding = paddingPx;
		updateTabStyles();
	}

	public int getTabPaddingLeftRight() {
		return tabPadding;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		currentPosition = savedState.currentPosition;
		requestLayout();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currentPosition = currentPosition;
		return savedState;
	}

	static class SavedState extends BaseSavedState {
		int currentPosition;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentPosition = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(currentPosition);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

	public boolean isTwoTab() {
	    return isTwoTab;
	}

	public void setTwoTab(boolean isTwoTab) {
	    this.isTwoTab = isTwoTab;
	}

}
