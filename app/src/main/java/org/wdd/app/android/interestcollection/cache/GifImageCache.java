package org.wdd.app.android.interestcollection.cache;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.toolbox.DrawableLoader;

import java.lang.ref.SoftReference;
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
public class GifImageCache implements DrawableLoader.DrawableCache {

	private static GifImageCache cache;

	public static GifImageCache getInstance() {
		if(cache == null) {
			synchronized (GifImageCache.class) {
				if(cache == null) {
					cache = new GifImageCache();
				}
			}
		}
		return cache;
	}

	private LruCache<String, SoftReference<Drawable>> mLruCache;// 硬引用缓存
	private ReentrantReadWriteLock mLock;

	private GifImageCache() {
		mLock = new ReentrantReadWriteLock();
		// 获取单个进程可用内存的最大值
		final int avaliableSize = (int) Runtime.getRuntime().maxMemory();
		// 设置为可用内存的1/4（按Byte计算）
		final int useableSize = avaliableSize / 5;
		mLruCache = new LruCache<String, SoftReference<Drawable>>(useableSize) {
			@Override
			protected int sizeOf(String key, SoftReference<Drawable> value) {
				Drawable d = value.get();
				if (d != null) {
					if (d instanceof GifDrawable) {
						GifDrawable drawable = (GifDrawable) d;
						if (drawable.getBuffer() != null) {
							// 计算存储bitmap所占用的字节数
							Bitmap bitmap = drawable.getBuffer();
							Log.e("####", "gif" + bitmap.getRowBytes() * bitmap.getHeight());
							return bitmap.getRowBytes() * bitmap.getHeight();
						} else {
							mLock.writeLock().lock();
							Log.e("####", "clear");
							mLruCache.remove(key);
							mLock.writeLock().unlock();
							return 0;
						}
					} else if (d instanceof BitmapDrawable) {
						BitmapDrawable drawable = (BitmapDrawable) d;
						if (drawable.getBitmap() != null) {
							// 计算存储bitmap所占用的字节数
							Bitmap bitmap = drawable.getBitmap();
							Log.e("####", "img" + bitmap.getRowBytes() * bitmap.getHeight());
							return bitmap.getRowBytes() * bitmap.getHeight();
						} else {
							mLock.writeLock().lock();
							Log.e("####", "clear");
							mLruCache.remove(key);
							mLock.writeLock().unlock();
							return 0;
						}
					} else {
						return 0;
					}

				} else {
					mLock.writeLock().lock();
					Log.e("####", "clear");
					mLruCache.remove(key);
					mLock.writeLock().unlock();
					return 0;
				}
			}

			@Override
			protected void entryRemoved(boolean evicted, String key, SoftReference<Drawable> oldValue, SoftReference<Drawable> newValue) {
				if (evicted) {
					if (oldValue != null) {
						mLock.writeLock().lock();
						Log.e("####", "recycled");
						Drawable drawable = oldValue.get();
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
		Drawable drawable = mLruCache.get(url).get();
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
		mLruCache.put(url, new SoftReference<>(drawable));
	}

	public void clear() {
		clearLruCache();
	}

	private void clearLruCache() {
		mLock.writeLock().lock();
		Map<String, SoftReference<Drawable>> snapshot = mLruCache.snapshot();
		Set<String> keys = snapshot.keySet();
		Iterator<String> it = keys.iterator();
		String key;
		Drawable drawable;
		while (it.hasNext()) {
			key = it.next();
			drawable = snapshot.get(key).get();
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
		Drawable drawable = mLruCache.remove(key).get();
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
