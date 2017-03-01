package org.wdd.app.android.interestcollection.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.toolbox.DrawableLoader;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import pl.droidsonroids.gif.GifDrawable;

/**
 * 图片缓存
 * @author richard wang
 *
 */
public class DrawableCache implements DrawableLoader.DrawableCache {

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
	private ReentrantReadWriteLock mLock;

	private DrawableCache() {
		mLock = new ReentrantReadWriteLock();
		// 获取单个进程可用内存的最大值
		final int avaliableSize = (int) Runtime.getRuntime().maxMemory();
		Log.e("#########", avaliableSize / 1024 / 1024 + "");
		// 设置为可用内存的1/4（按Byte计算）
		final int useableSize = avaliableSize / 3;
		mLruCache = new LruCache<String, Drawable>(useableSize) {
			@Override
			protected int sizeOf(String key, Drawable value) {
				if (value != null) {
					if (value instanceof GifDrawable) {
						GifDrawable drawable = (GifDrawable) value;
						if (drawable.getBuffer() != null) {
							// 计算存储bitmap所占用的字节数
							Bitmap bitmap = drawable.getBuffer();
							Log.e("####", "gif" + bitmap.getRowBytes() * bitmap.getHeight());
							return bitmap.getRowBytes() * bitmap.getHeight();
						} else {
							Log.e("####", "clear");
							mLruCache.remove(key);
							return 0;
						}
					} else if (value instanceof BitmapDrawable) {
						BitmapDrawable drawable = (BitmapDrawable) value;
						if (drawable.getBitmap() != null) {
							// 计算存储bitmap所占用的字节数
							Bitmap bitmap = drawable.getBitmap();
							Log.e("####", "img" + bitmap.getRowBytes() * bitmap.getHeight());
							return bitmap.getRowBytes() * bitmap.getHeight();
						} else {
							Log.e("####", "clear");
							mLruCache.remove(key);
							return 0;
						}
					} else {
						return 0;
					}

				} else {
					Log.e("####", "clear");
					mLruCache.remove(key);
					return 0;
				}
			}

			@Override
			protected void entryRemoved(boolean evicted, String key, Drawable oldValue, Drawable newValue) {
				if (evicted) {
					if (oldValue != null) {
						Log.e("####", "recycled");
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
		mLock.writeLock().lock();
		mLruCache.put(url, drawable);
		mLock.writeLock().unlock();
	}

	public void clear() {
		clearLruCache();
	}

	private void clearLruCache() {
		mLock.writeLock().lock();
		Map<String, Drawable> snapshot = mLruCache.snapshot();
		Set<String> keys = snapshot.keySet();
		Iterator<String> it = keys.iterator();
		String key;
		Drawable drawable;
		while (it.hasNext()) {
			key = it.next();
			drawable = snapshot.get(key);
			if (drawable != null) {
				Log.e("####", "recycled");
				if (drawable instanceof GifDrawable) {
					((GifDrawable)drawable).recycle();
				} else if(drawable instanceof BitmapDrawable) {
					((BitmapDrawable)drawable).getBitmap().recycle();
				}
			}
		}
		keys.clear();
		snapshot.clear();
		mLruCache.evictAll();
		mLock.writeLock().unlock();
	}

	public void removeDrawable(String key) {
		mLock.writeLock().lock();
		Drawable drawable = mLruCache.remove(key);
		if (drawable != null) {
			if (drawable instanceof GifDrawable) {
				((GifDrawable)drawable).recycle();
			} else if(drawable instanceof BitmapDrawable) {
				((BitmapDrawable)drawable).getBitmap().recycle();
			}
		}
		mLock.writeLock().unlock();
	}

}