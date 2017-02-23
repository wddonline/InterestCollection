package org.wdd.app.android.interestcollection.cache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.text.TextUtils;

import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 图片缓存
 * @author richard wang
 *
 */
public class ImageCache implements com.android.volley.toolbox.ImageLoader.ImageCache {

	private static ImageCache cache;
	
	public static ImageCache getInstance() {
		if(cache == null) {
			synchronized (ImageCache.class) {
				if(cache == null) {
					cache = new ImageCache();
				}
			}
		}
		return cache;
	}

	private final int INITIAL_CAPACITY = 16;// LinkedHashMap初始容量
	private final int MAX_SIZE = 30;
	private final float LOAD_FACTOR = 0.75f;// LinkedHashMap加载因子
	private final boolean ACCESS_ORDER = true;// LinkedHashMap排序模式


	private static LruCache<String, Bitmap> mStrongRefs;// 硬引用缓存
	private static LinkedHashMap<String, SoftReference<Bitmap>> mSoftRefs;// 软引用缓存

	private ImageCache() {
		// 获取单个进程可用内存的最大值
		final int avaliableSize = (int) Runtime.getRuntime().maxMemory();
		// 设置为可用内存的1/4（按Byte计算）
		final int useableSize = avaliableSize / 4;
		mStrongRefs = new LruCache<String, Bitmap>(useableSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				if (value != null) {
					// 计算存储bitmap所占用的字节数
					return value.getRowBytes() * value.getHeight();
				} else {
					return 0;
				}
			}

			@Override
			protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
				if (evicted) {
					if (oldValue != null) {
						// 当硬引用缓存容量已满时，会使用LRU算法将最近没有被使用的图片转入软引用缓存
						mSoftRefs.put(key, new SoftReference<Bitmap>(oldValue));
					}
				}
			}
		};

        /*
        * 第一个参数：初始容量（默认16） 第二个参数：加载因子（默认0.75）
		* 第三个参数：排序模式（true：按访问次数排序；false：按插入顺序排序）
		*/
		mSoftRefs = new LinkedHashMap<String, SoftReference<Bitmap>>(INITIAL_CAPACITY, LOAD_FACTOR, ACCESS_ORDER) {

			@Override
			protected boolean removeEldestEntry(Entry<String, SoftReference<Bitmap>> eldest) {
				if (size() > MAX_SIZE) {
					Bitmap bitmap = eldest.getValue().get();
					if (bitmap != null) {
						bitmap.recycle();
					}
					return true;
				}
				return false;
			}
		};
	}

	/**
	 * 从缓存中获取Bitmap
	 * 
	 * @param url
	 * @return bitmap
	 */
	@Override
	public Bitmap getBitmap(String url) {
		if(TextUtils.isEmpty(url)) {
			return null;
		}
		Bitmap bitmap = mStrongRefs.get(url);
		if (bitmap != null) {
			// 找到该Bitmap之后，将其移到LinkedHashMap的最前面，保证它在LRU算法中将被最后删除。
			mStrongRefs.remove(url);
			mStrongRefs.put(url, bitmap);
			return bitmap;
		}

		SoftReference<Bitmap> ref = mSoftRefs.get(url);
		if (ref != null) {
			bitmap = ref.get();
			if (bitmap != null) {
				// 找到该Bitmap之后，将它移到硬引用缓存。并从软引用缓存中删除。
				mStrongRefs.put(url, bitmap);
				mSoftRefs.remove(url);
				return bitmap;
			} else {
				mSoftRefs.remove(url);
			}
		}
		return null;
	}

	/**
	 * 添加Bitmap到内存缓存
	 * 
	 * @param url
	 * @param bitmap
	 */
	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		if (bitmap == null) {
			return;
		}
		mStrongRefs.put(url, bitmap);
	}

	public void clear() {
		clearLruCache();
		clearSoftCache();
	}

	private void clearLruCache() {
		synchronized (this) {
			Map<String, Bitmap> snapshot = mStrongRefs.snapshot();
			Set<String> keys = snapshot.keySet();
			Iterator<String> it = keys.iterator();
			String key;
			Bitmap bitmap;
			while (it.hasNext()) {
				key = it.next();
				bitmap = snapshot.get(key);
				bitmap.recycle();
				if (bitmap != null) {
					bitmap.recycle();
				}
			}
			keys.clear();
			snapshot.clear();
			mStrongRefs.evictAll();
		}
	}

	private void clearSoftCache() {
		synchronized (this) {
			Set<String> keys = mSoftRefs.keySet();
			String[] arr = new String[keys.size()];
			keys.toArray(arr);
			Bitmap bitmap;
			for (String key : arr) {
				bitmap = mSoftRefs.get(key).get();
				if (bitmap != null) {
					bitmap.recycle();
				}
			}
			keys.clear();
			mSoftRefs.clear();
		}
	}

	public void removeBitmap(String key) {
		synchronized (this) {
			Bitmap bitmap = mStrongRefs.remove(key);
			if (bitmap != null) bitmap.recycle();
			SoftReference<Bitmap> reference = mSoftRefs.remove(key);
			bitmap = reference.get();
			if (reference != null && bitmap != null) {
				bitmap.recycle();
			}
		}
	}

}
