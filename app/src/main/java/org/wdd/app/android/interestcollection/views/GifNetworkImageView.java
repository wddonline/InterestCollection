package org.wdd.app.android.interestcollection.views;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.DrawableLoader;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.app.InterestCollectionApplication;
import org.wdd.app.android.interestcollection.cache.DrawableCache;
import org.wdd.app.android.interestcollection.http.impl.VolleyTool;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by richard on 2/27/17.
 */

public class GifNetworkImageView extends GifImageView {

    /** The URL of the network image to load */
    private String mUrl;

    /**
     * Resource ID of the image to be used as a placeholder until the network image is loaded.
     */
    private int mDefaultImageId;

    /**
     * Resource ID of the image to be used if the network response fails.
     */
    private int mErrorImageId;

    /** Local copy of the ImageLoader. */
    private DrawableLoader mDrawableLoader;
    private NetworkImageView.ImageLoaderListener mLoaderListener;

    /** Current ImageContainer. (either in-flight or finished) */
    private DrawableLoader.DrawableContainer mDrawableContainer;

    public GifNetworkImageView(Context context) {
        this(context, null);
    }

    public GifNetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GifNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDrawableLoader = new DrawableLoader(VolleyTool.getInstance(InterestCollectionApplication.getInstance()).getRequestQueue(), DrawableCache.getInstance());
        setDefaultImageResId(R.drawable.default_img);
        setErrorImageResId(R.drawable.default_img);
    }

    /**
     * Sets URL of the image that should be loaded into this view. Note that calling this will
     * immediately either set the cached image (if available) or the default image specified by
     * {@link NetworkImageView#setDefaultImageResId(int)} on the view.
     *
     * NOTE: If applicable, {@link NetworkImageView#setDefaultImageResId(int)} and
     * {@link NetworkImageView#setErrorImageResId(int)} should be called prior to calling
     * this function.
     *
     * @param url The URL that should be loaded into this ImageView.
     * @param imageLoader ImageLoader that will be used to make the request.
     */
    public void setImageUrl(String url, DrawableLoader imageLoader) {
        mUrl = url;
        mDrawableLoader = imageLoader;
        // The URL has potentially changed. See if we need to load it.
        loadImageIfNecessary(false);
    }

    public void setImageUrl(String url) {
        mUrl = url;
        // The URL has potentially changed. See if we need to load it.
        loadImageIfNecessary(false);
    }

    /**
     * Gets the URL of the image that should be loaded into this view, or null if no URL has been set.
     * The image may or may not already be downloaded and set into the view.
     * @return the URL of the image to be set into the view, or null.
     */
    public String getImageURL()
    {
        return mUrl;
    }

    /**
     * Sets the default image resource ID to be used for this view until the attempt to load it
     * completes.
     */
    public void setDefaultImageResId(int defaultImage) {
        mDefaultImageId = defaultImage;
    }

    /**
     * Sets the error image resource ID to be used for this view in the event that the image
     * requested fails to load.
     */
    public void setErrorImageResId(int errorImage) {
        mErrorImageId = errorImage;
    }

    /**
     * Loads the image for the view if it isn't already loaded.
     * @param isInLayoutPass True if this was invoked from a layout pass, false otherwise.
     */
    void loadImageIfNecessary(final boolean isInLayoutPass) {
        int width = getWidth();
        int height = getHeight();

        boolean wrapWidth = false, wrapHeight = false;
        if (getLayoutParams() != null) {
            wrapWidth = getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT;
            wrapHeight = getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT;
        }

        // if the view's bounds aren't known yet, and this is not a wrap-content/wrap-content
        // view, hold off on loading the image.
        boolean isFullyWrapContent = wrapWidth && wrapHeight;
        if (width == 0 && height == 0 && !isFullyWrapContent) {
            return;
        }

        // if the URL to be loaded in this view is empty, cancel any old requests and clear the
        // currently loaded image.
        if (TextUtils.isEmpty(mUrl)) {
            if (mDrawableContainer != null) {
                mDrawableContainer.cancelRequest();
                mDrawableContainer = null;
            }
            setDefaultImageOrNull();
            return;
        }

        // if there was an old request in this view, check if it needs to be canceled.
        if (mDrawableContainer != null && mDrawableContainer.getRequestUrl() != null) {
            if (mDrawableContainer.getRequestUrl().equals(mUrl)) {
                // if the request is from the same URL, return.
                return;
            } else {
                // if there is a pre-existing request, cancel it if it's fetching a different URL.
                mDrawableContainer.cancelRequest();
                setDefaultImageOrNull();
            }
        }

        // Calculate the max image width / height to use while ignoring WRAP_CONTENT dimens.
        final int maxWidth = wrapWidth ? 0 : width;
        final int maxHeight = wrapHeight ? 0 : height;

        // The pre-existing content of this view didn't match the current URL. Load the new image
        // from the network.
        DrawableLoader.DrawableContainer newContainer = mDrawableLoader.get(mUrl,
                new DrawableLoader.DrawableListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mDrawableContainer = null;
                        if (mLoaderListener != null) mLoaderListener.onLoadError();
                        if (mErrorImageId != 0) {
                            setImageResource(mErrorImageId);
                        }
                    }

                    @Override
                    public void onResponse(final DrawableLoader.DrawableContainer response, boolean isImmediate) {
                        if (mLoaderListener != null) mLoaderListener.onLoadCompleted();
                        // If this was an immediate response that was delivered inside of a layout
                        // pass do not set the image immediately as it will trigger a requestLayout
                        // inside of a layout. Instead, defer setting the image by posting back to
                        // the main thread.
                        if (isImmediate && isInLayoutPass) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    onResponse(response, false);
                                }
                            });
                            return;
                        }
                        if (response.getDrawable() != null) {
                            setImageDrawable(response.getDrawable());
                        } else if (mDefaultImageId != 0) {
                            setImageResource(mDefaultImageId);
                        }
                    }
                }, maxWidth, maxHeight);

        // update the ImageContainer to be the new bitmap container.
        mDrawableContainer = newContainer;
    }

    private void setDefaultImageOrNull() {
        if(mDefaultImageId != 0) {
            setImageResource(mDefaultImageId);
        }
        else {
            setImageBitmap(null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        loadImageIfNecessary(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mDrawableContainer != null) {
            // If the view was bound to an image request, cancel it and clear
            // out the image from the view.
            mDrawableContainer.cancelRequest();
            setImageBitmap(null);
            // also clear out the container so we can reload the image if necessary.
            mDrawableContainer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        try {
            super.onDraw(canvas);
        } catch (IllegalStateException e) {
            mDrawableContainer = null;
            setImageResource(R.drawable.default_img);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return mUrl;
    }

    public void setImageLoaderListener(NetworkImageView.ImageLoaderListener loaderListener) {
        this.mLoaderListener = loaderListener;
    }

    public interface ImageLoaderListener {

        void onLoadError();
        void onLoadCompleted();

    }
}
