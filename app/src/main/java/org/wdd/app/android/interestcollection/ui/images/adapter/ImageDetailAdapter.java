package org.wdd.app.android.interestcollection.ui.images.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.images.model.ImageDetail;
import org.wdd.app.android.interestcollection.views.GifNetworkImageView;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by wangdd on 17-2-24.
 */

public class ImageDetailAdapter extends BaseAdapter {

    private final int TYPE_TEXT = 0;
    private final int TYPE_IMAGE_NORMAL = 1;
    private final int TYPE_IMAGE_GIF = 2;

    private LayoutInflater mInflater;
    private List<Item> mData;
    private List<SoftReference<GifDrawable>> mGifCache;

    public ImageDetailAdapter(Context context, List<ImageDetail.Node> nodes) {
        mInflater = LayoutInflater.from(context);
        mGifCache = new ArrayList<>();
        mData = new ArrayList<>();
        for (ImageDetail.Node node : nodes) {
            if (node.isImg) {
                if (node.data.endsWith(".gif")) {
                    mData.add(new Item(TYPE_IMAGE_GIF, node.data));
                } else {
                    mData.add(new Item(TYPE_IMAGE_NORMAL, node.data));
                }
            } else {
                mData.add(new Item(TYPE_TEXT, node.data));
            }
        }
    }

    public void refreshData(List<ImageDetail.Node> nodes) {
        mData.clear();
        for (ImageDetail.Node node : nodes) {
            if (node.isImg) {
                if (node.data.endsWith(".gif")) {
                    mData.add(new Item(TYPE_IMAGE_GIF, node.data));
                } else {
                    mData.add(new Item(TYPE_IMAGE_NORMAL, node.data));
                }
            } else {
                mData.add(new Item(TYPE_TEXT, node.data));
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Item item = mData.get(position);
        switch (item.type) {
            case TYPE_TEXT:
                TextViewHolder textViewHolder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_dirty_joke_detail_list_post, parent, false);
                    textViewHolder = new TextViewHolder(convertView);
                    convertView.setTag(textViewHolder);
                } else {
                    textViewHolder = (TextViewHolder) convertView.getTag();
                }
                textViewHolder.bindData(item);
                break;
            case TYPE_IMAGE_NORMAL:
                ImageViewHolder imageViewHolder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_images_detail_image, parent, false);
                    imageViewHolder = new ImageViewHolder(convertView);
                    convertView.setTag(imageViewHolder);
                } else {
                    imageViewHolder = (ImageViewHolder) convertView.getTag();
                }
                imageViewHolder.bindData(item.data);
                break;
            case TYPE_IMAGE_GIF:
                GifViewHolder gifViewHolder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_images_detail_gif, parent, false);
                    gifViewHolder = new GifViewHolder(convertView);
                    convertView.setTag(gifViewHolder);
                } else {
                    gifViewHolder = (GifViewHolder) convertView.getTag();
                }
                gifViewHolder.bindData(item.data);
                break;
        }
        return convertView;
    }

    private class TextViewHolder {

        TextView textView;

        TextViewHolder(View itemView) {
            textView = (TextView) itemView.findViewById(R.id.item_dirty_joke_detail_list_post_text);
        }

        public void bindData(Item item) {
            textView.setText(item.data);
        }
    }

    private class ImageViewHolder {

        NetworkImageView imageView;

        ImageViewHolder(View itemView) {
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_images_detail_image_img);
        }

        public void bindData(String imgUrl) {
            imageView.setImageUrl(imgUrl);
        }
    }

    private class GifViewHolder {

        GifNetworkImageView gifView;

        GifViewHolder(View itemView) {
            gifView = (GifNetworkImageView) itemView.findViewById(R.id.item_images_detail_gif_img);
        }

        public void bindData(String imgUrl) {
            gifView.setImageUrl(imgUrl);
        }
    }

    private class Item {

        int type;
        String data;

        public Item(int type, String data) {
            this.type = type;
            this.data = data;
        }
    }
}
