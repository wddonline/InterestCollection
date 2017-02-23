package org.wdd.app.android.interestcollection.ui.gallery.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.base.BaseActivity;
import org.wdd.app.android.interestcollection.views.photo_view.PhotoView;

public class ImageBrowserActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    public static void show(Activity activity, View view, String[]urls) {
        Bundle bundle = ActivityOptionsCompat.makeScaleUpAnimation(view, (int)(view.getWidth() / 2f), (int)(view.getHeight() / 2f), 0, 0).toBundle();
        Intent intent = new Intent(activity, ImageBrowserActivity.class);
        intent.putExtra("urls", urls);
        ActivityCompat.startActivity(activity, intent, bundle);
    }

    public static void show(Activity activity, View view, String[]urls, int initPosition) {
        Bundle bundle = ActivityOptionsCompat.makeScaleUpAnimation(view, (int)(view.getWidth() / 2f), (int)(view.getHeight() / 2f), 0, 0).toBundle();
        Intent intent = new Intent(activity, ImageBrowserActivity.class);
        intent.putExtra("urls", urls);
        intent.putExtra("init_position", initPosition);
        ActivityCompat.startActivity(activity, intent, bundle);
    }

    public static void show(Activity activity, String[] urls) {
        Intent intent = new Intent(activity, ImageBrowserActivity.class);
        intent.putExtra("urls", urls);
        activity.startActivity(intent);
    }

    public static void show(Context context, String[] urls) {
        Intent intent = new Intent(context, ImageBrowserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("urls", urls);
        context.startActivity(intent);
    }

    private ViewPager mViewPager;
    private TextView mTextView;

    private String[] mUrls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_browser);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        initData();
        initViews();
    }

    private void initData() {
        mUrls = getIntent().getStringArrayExtra("urls");
    }

    private void initViews() {
        int initPosition = getIntent().getIntExtra("init_position", 0);
        mViewPager = (ViewPager) findViewById(R.id.activity_image_browser_viewpager);
        mTextView = (TextView) findViewById(R.id.activity_image_browser_textview);
        mViewPager.addOnPageChangeListener(this);
        ImageAdapter adapter = new ImageAdapter();
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(initPosition);
        mTextView.setText((initPosition + 1) + "/" + mUrls.length);
    }

    @Override
    public void onBackPressed() {
        ActivityCompat.finishAfterTransition(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mTextView.setText((position + 1) + "/" + mUrls.length);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class ImageAdapter extends PagerAdapter {

        private PhotoView[] mPhotoViews;

        public ImageAdapter() {
            mPhotoViews = new PhotoView[mUrls.length];
            for (int i = 0; i < mPhotoViews.length; i++) {
                mPhotoViews[i] = new PhotoView(getBaseContext());
                mPhotoViews[i].enable();
                mPhotoViews[i].enableRotate();
                mPhotoViews[i].setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = mPhotoViews[position];
            photoView.setImageUrl(mUrls[position]);
            container.addView(photoView);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mPhotoViews[position]);
        }

        @Override
        public int getCount() {
            return mUrls.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
