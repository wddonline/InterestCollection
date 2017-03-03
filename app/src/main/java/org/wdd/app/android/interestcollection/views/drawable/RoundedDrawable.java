package org.wdd.app.android.interestcollection.views.drawable;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.util.Log;
import android.widget.ImageView.ScaleType;

/**
 * Created by richard on 9/20/16.
 */

public class RoundedDrawable extends Drawable {
    public static final String TAG = "RoundedDrawable";

    private final RectF mBounds = new RectF();

    private final RectF mDrawableRect = new RectF();
    private float mLeftTopRadius;
    private float mLeftBottomRadius;
    private float mRightTopRadius;
    private float mRightBottomRadius;

    private final RectF mBitmapRect = new RectF();
    private final BitmapShader mBitmapShader;
    private final Paint mBitmapPaint;
    //private final Paint mMaskPaint; //阴影层绘制
    private final int mBitmapWidth;
    private final int mBitmapHeight;

    private final RectF mBorderRect = new RectF();
    private final Paint mBorderPaint;
    private int mBorderWidth;
    private int mBorderColor;

    private ScaleType mScaleType = ScaleType.FIT_XY;

    private final Matrix mShaderMatrix = new Matrix();

    public RoundedDrawable(Bitmap bitmap, int border, int borderColor, float cornerRadius) {
        this(bitmap, border, borderColor, cornerRadius, cornerRadius, cornerRadius, cornerRadius);
    }

    public RoundedDrawable(Bitmap bitmap, int border, int borderColor, float leftTopRadius, float leftBottomRadius,
                            float rightTopRadius, float rightBottomRadius) {
        mBorderWidth = border;
        mBorderColor = borderColor;

        mBitmapWidth = bitmap.getWidth();
        mBitmapHeight = bitmap.getHeight();
        mBitmapRect.set(0, 0, mBitmapWidth, mBitmapHeight);

        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapShader.setLocalMatrix(mShaderMatrix);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        //mMaskPaint = new Paint();
        //mMaskPaint.setAntiAlias(true);
        //mMaskPaint.setColor(0x0C000000); // 5%的纯黑蒙层
        //mMaskPaint.setStyle(Paint.Style.FILL);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(border);

        mLeftTopRadius = leftTopRadius;
        mLeftBottomRadius = leftBottomRadius;
        mRightTopRadius = rightTopRadius;
        mRightBottomRadius = rightBottomRadius;
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType == null) { scaleType = ScaleType.FIT_XY; }
        if (mScaleType != scaleType) {
            mScaleType = scaleType;
            setMatrix();
        }
    }

    public ScaleType getScaleType() {
        return mScaleType;
    }

    private void setMatrix() {
        mBorderRect.set(mBounds);
        mDrawableRect.set(0 + mBorderWidth, 0 + mBorderWidth, mBorderRect.width() - mBorderWidth, mBorderRect.height() - mBorderWidth);

        float scale;
        float dx;
        float dy;

        switch(mScaleType) {
            case CENTER:
                mBorderRect.set(mBounds);
                mDrawableRect.set(0 + mBorderWidth, 0 + mBorderWidth, mBorderRect.width() - mBorderWidth, mBorderRect.height() - mBorderWidth);

                mShaderMatrix.set(null);
                mShaderMatrix.setTranslate((int) ((mDrawableRect.width() - mBitmapWidth) * 0.5f + 0.5f), (int) ((mDrawableRect.height() - mBitmapHeight) * 0.5f + 0.5f));
                break;
            case CENTER_CROP:
                mBorderRect.set(mBounds);
                mDrawableRect.set(0 + mBorderWidth, 0 + mBorderWidth, mBorderRect.width() - mBorderWidth, mBorderRect.height() - mBorderWidth);

                mShaderMatrix.set(null);

                dx = 0;
                dy = 0;

                if (mBitmapWidth * mDrawableRect.height() > mDrawableRect.width() * mBitmapHeight) {
                    scale = (float) mDrawableRect.height() / (float) mBitmapHeight;
                    dx = (mDrawableRect.width() - mBitmapWidth * scale) * 0.5f;
                } else {
                    scale = (float) mDrawableRect.width() / (float) mBitmapWidth;
                    dy = (mDrawableRect.height() - mBitmapHeight * scale) * 0.5f;
                }

                mShaderMatrix.setScale(scale, scale);
                mShaderMatrix.postTranslate((int) (dx + 0.5f) + mBorderWidth, (int) (dy + 0.5f) + mBorderWidth);
                break;
            case CENTER_INSIDE:
                mShaderMatrix.set(null);

                if (mBitmapWidth <= mBounds.width() && mBitmapHeight <= mBounds.height()) {
                    scale = 1.0f;
                } else {
                    scale = Math.min((float) mBounds.width() / (float) mBitmapWidth,
                            (float) mBounds.height() / (float) mBitmapHeight);
                }

                dx = (int) ((mBounds.width() - mBitmapWidth * scale) * 0.5f + 0.5f);
                dy = (int) ((mBounds.height() - mBitmapHeight * scale) * 0.5f + 0.5f);

                mShaderMatrix.setScale(scale, scale);
                mShaderMatrix.postTranslate(dx, dy);

                mBorderRect.set(mBitmapRect);
                mShaderMatrix.mapRect(mBorderRect);
                mDrawableRect.set(mBorderRect.left + mBorderWidth, mBorderRect.top + mBorderWidth, mBorderRect.right - mBorderWidth, mBorderRect.bottom - mBorderWidth);
                mShaderMatrix.setRectToRect(mBitmapRect, mDrawableRect, Matrix.ScaleToFit.FILL);
                break;
            case FIT_CENTER:
                mBorderRect.set(mBitmapRect);
                mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.CENTER);
                mShaderMatrix.mapRect(mBorderRect);
                mDrawableRect.set(mBorderRect.left + mBorderWidth, mBorderRect.top + mBorderWidth, mBorderRect.right - mBorderWidth, mBorderRect.bottom - mBorderWidth);
                mShaderMatrix.setRectToRect(mBitmapRect, mDrawableRect, Matrix.ScaleToFit.FILL);
                break;
            case FIT_END:
                mBorderRect.set(mBitmapRect);
                mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.END);
                mShaderMatrix.mapRect(mBorderRect);
                mDrawableRect.set(mBorderRect.left + mBorderWidth, mBorderRect.top + mBorderWidth, mBorderRect.right - mBorderWidth, mBorderRect.bottom - mBorderWidth);
                mShaderMatrix.setRectToRect(mBitmapRect, mDrawableRect, Matrix.ScaleToFit.FILL);
                break;
            case FIT_START:
                mBorderRect.set(mBitmapRect);
                mShaderMatrix.setRectToRect(mBitmapRect, mBounds, Matrix.ScaleToFit.START);
                mShaderMatrix.mapRect(mBorderRect);
                mDrawableRect.set(mBorderRect.left + mBorderWidth, mBorderRect.top + mBorderWidth, mBorderRect.right - mBorderWidth, mBorderRect.bottom - mBorderWidth);
                mShaderMatrix.setRectToRect(mBitmapRect, mDrawableRect, Matrix.ScaleToFit.FILL);
                break;
            case FIT_XY:
            default:
                mBorderRect.set(mBounds);
                mDrawableRect.set(0 + mBorderWidth, 0 + mBorderWidth, mBorderRect.width() - mBorderWidth, mBorderRect.height() - mBorderWidth);
                mShaderMatrix.set(null);
                mShaderMatrix.setRectToRect(mBitmapRect, mDrawableRect, Matrix.ScaleToFit.FILL);
                break;
        }
        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
//		Log.i(TAG, "onboundschange: w: " + bounds.width() + "h:" + bounds.height());
        super.onBoundsChange(bounds);

        mBounds.set(bounds);

//		if (USE_VIGNETTE) {
//			RadialGradient vignette = new RadialGradient(
//					mDrawableRect.centerX(), mDrawableRect.centerY() * 1.0f / 0.7f, mDrawableRect.centerX() * 1.3f,
//					new int[] { 0, 0, 0x7f000000 }, new float[] { 0.0f, 0.7f, 1.0f },
//					Shader.TileMode.CLAMP);
//
//			Matrix oval = new Matrix();
//			oval.setScale(1.0f, 0.7f);
//			vignette.setLocalMatrix(oval);
//
//			mBitmapPaint.setShader(
//					new ComposeShader(mBitmapShader, vignette, PorterDuff.Mode.SRC_OVER));
//		}

        setMatrix();
    }

    @Override
    public void draw(Canvas canvas) {
        if (mBorderWidth > 0) {
            drawRectangle(canvas, mBorderRect, mBorderPaint, 0);
            drawRectangle(canvas, mDrawableRect, mBitmapPaint, mBorderWidth);
        } else {
            drawRectangle(canvas, mDrawableRect, mBitmapPaint, 0);
        }
        // 绘制蒙层
        //canvas.drawRoundRect(mDrawableRect, mCornerRadius, mCornerRadius, mMaskPaint);
    }

    private void drawRectangle(Canvas canvas, RectF rectF, Paint paint, int offset) {
        canvas.translate(offset, offset);
        float left, top, right, bottom;
        Path path = new Path();
        path.moveTo(mLeftTopRadius, 0);
        path.lineTo(rectF.width() - mRightTopRadius, 0);

        left = rectF.width() - mRightTopRadius * 2;
        top = 0;
        right = rectF.width();
        bottom = mRightTopRadius * 2 ;
        path.arcTo(new RectF(left, top, right, bottom), 270, 90);

        path.lineTo(rectF.width(), rectF.height() - mRightBottomRadius);

        left = rectF.width() - mRightBottomRadius * 2;
        top = rectF.height() - mRightBottomRadius * 2;
        right = rectF.width();
        bottom = rectF.height();
        path.arcTo(new RectF(left, top, right, bottom), 0, 90);

        path.lineTo(mLeftBottomRadius, rectF.height());

        left = 0;
        top = rectF.height() - mLeftBottomRadius * 2;
        right = mLeftBottomRadius * 2;
        bottom = rectF.height();
        path.arcTo(new RectF(left, top, right, bottom), 90, 90);

        path.lineTo(0, mLeftTopRadius);

        left = 0;
        top = 0;
        right = mLeftTopRadius * 2;
        bottom = mLeftTopRadius * 2;
        path.arcTo(new RectF(left, top, right, bottom), 180, 90);

        path.close();
        canvas.drawPath(path, paint);
        canvas.save();
        canvas.restore();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mBitmapPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mBitmapPaint.setColorFilter(cf);
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmapWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmapHeight;
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap;
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (width > 0 && height > 0) {
            try {
                bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return null;
            }
            if (bitmap == null) {
                return null;
            }
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
        } else {
            bitmap = null;
        }

        return bitmap;
    }

    public static Drawable fromDrawable(Drawable drawable, float radius) {
        return fromDrawable(drawable, null, 0, 0, radius, radius, radius, radius);
    }

    public static Drawable fromDrawable(Drawable drawable, ScaleType scaleType, int border, int borderColor,
                                        float leftTopRadius, float leftBottomRadius, float rightTopRadius,
                                        float rightBottomRadius) {
        if (drawable != null) {
            if (drawable instanceof TransitionDrawable) {
                TransitionDrawable td = (TransitionDrawable) drawable;
                int num = td.getNumberOfLayers();

                Drawable[] drawableList = new Drawable[num];
                for (int i = 0; i < num; i++) {
                    Drawable d = td.getDrawable(i);
                    if (d instanceof ColorDrawable) {
                        drawableList[i] = d;
                    } else if (d instanceof RoundedDrawable) {
                        drawableList[i] = d;
                    } else {
                        Bitmap bm = drawableToBitmap(d);
                        if (bm != null) {

                            drawableList[i] = new RoundedDrawable(bm, border, borderColor, leftTopRadius,
                                    leftBottomRadius, rightTopRadius, rightBottomRadius);
                            if (scaleType != null) {
                                ((RoundedDrawable)drawableList[i]).setScaleType(scaleType);
                            }
                        }
                    }
                }
                try {
                    return new TransitionDrawable(drawableList);
                } catch(OutOfMemoryError e) {
                    e.printStackTrace();
                    return drawable;
                }
            }

            Bitmap bm = drawableToBitmap(drawable);
            if (bm != null) {
                RoundedDrawable roundDrawable;
                roundDrawable = new RoundedDrawable(bm, border, borderColor, leftTopRadius, leftBottomRadius, rightTopRadius, rightBottomRadius);
                if (scaleType != null) {
                    roundDrawable.setScaleType(scaleType);
                }
                return roundDrawable;
            } else {
                Log.w(TAG, "Failed to create bitmap from drawable!");
            }
        }
        return drawable;
    }

    public int getBorderWidth() {
        return mBorderWidth;
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderWidth(int width) {
        this.mBorderWidth = width;
        mBorderPaint.setStrokeWidth(mBorderWidth);
    }

    public void setBorderColor(int color) {
        this.mBorderColor = color;
        mBorderPaint.setColor(color);
    }

    public void setCornerRadius(int cornerRadius) {
        this.mRightBottomRadius = cornerRadius;
        this.mRightTopRadius = cornerRadius;
        this.mLeftBottomRadius = cornerRadius;
        this.mLeftTopRadius = cornerRadius;
    }

    public void setRightBottomRadius(int rightBottomRadius) {
        this.mRightBottomRadius = rightBottomRadius;
    }

    public void setRightTopRadius(int rightTopRadius) {
        this.mRightTopRadius = rightTopRadius;
    }

    public void setLeftBottomRadius(int leftBottomRadius) {
        this.mLeftBottomRadius = leftBottomRadius;
    }

    public void setLeftTopRadius(int leftTopRadius) {
        this.mLeftTopRadius = leftTopRadius;
    }
}
