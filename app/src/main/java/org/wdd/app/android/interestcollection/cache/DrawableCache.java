package org.wdd.app.android.interestcollection.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import com.android.volley.toolbox.DrawableLoader;

import org.wdd.app.android.interestcollection.utils.LogUtils;

import pl.droidsonroids.gif.GifDrawable;

/**
 * 图片缓存
 * @author richard wang
 *
 */
public class DrawableCache implements DrawableLoader.DrawableCache {

	private final String TAG = DrawableCache.class.getName();

	private static DrawableCache cache;

	public static DrawableCache getInstance() {
		if(cache == null) {
			synchronized (DrawableCache.class) {
				if(cache == null) {
					cache = new DrawableCache();
				}
			}
		}
		return cache;
	}

	private LruCache<String, Drawable> mLruCache;

	private DrawableCache() {
		// 获取单个进程可用内存的最大值
		final int avaliableSize = (int) Runtime.getRuntime().maxMemory();
		// 设置为可用内存的1/4（按Byte计算）
		final int useableSize = avaliableSize / 20;
		LogUtils.e(TAG, useableSize / 1024 / 1024f + "m");
		mLruCache = new LruCache<String, Drawable>(useableSize) {
			@Override
			protected int sizeOf(String key, Drawable value) {
				if (value != null) {
					if (value instanceof GifDrawable) {
						GifDrawable drawable = (GifDrawable) value;
						if (drawable.getBuffer() != null) {
							// 计算存储bitmap所占用的字节数
							Bitmap bitmap = drawable.getBuffer();
							return bitmap.getRowBytes() * bitmap.getHeight();
						} else {
							mLruCache.remove(key);
						}
					} else if (value instanceof BitmapDrawable) {
						BitmapDrawable drawable = (BitmapDrawable) value;
						if (drawable.getBitmap() != null) {
							// 计算存储bitmap所占用的字节数
							Bitmap bitmap = drawable.getBitmap();
							return bitmap.getRowBytes() * bitmap.getHeight();
						} else {
							mLruCache.remove(key);
						}
					}

				} else {
					mLruCache.remove(key);

				}
				return 0;
			}

			@Override
			protected void entryRemoved(boolean evicted, String key, Drawable oldValue, Drawable newValue) {
				if (evicted) {
					if (oldValue != null) {
						if (oldValue != null) {
							if (oldValue instanceof GifDrawable) {
								((GifDrawable)oldValue).recycle();
							} else if(oldValue instanceof BitmapDrawable) {
								((BitmapDrawable)oldValue).getBitmap().recycle();
							}
						}
					}
				}
			}
		};
	}

	/**
	 * 从缓存中获取Bitmap
	 *
	 * @param url
	 * @return drawable
	 */
	@Override
	public Drawable getDrawable(String url) {
		if(TextUtils.isEmpty(url)) {
			return null;
		}
		Drawable drawable = mLruCache.get(url);
		if (drawable != null) {
			return drawable;
		}
		return null;
	}

	/**
	 * 添加Drawable到内存缓存
	 *
	 * @param url
	 * @param drawable
	 */
	@Override
	public void putDrawable(String url, Drawable drawable) {
		if (drawable == null) {
			return;
		}
		mLruCache.put(url, drawable);
	}

	public void clear() {
		mLruCache.evictAll();
	}

	public void removeDrawable(String key) {
		mLruCache.remove(key);
	}

}
