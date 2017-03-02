package org.wdd.app.android.interestcollection.ui.shares.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.shares.model.ShareDetail;
import org.wdd.app.android.interestcollection.views.GifNetworkImageView;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 2/24/17.
 */

public class ShareDetailAdapter extends BaseAdapter {

    private final int TYPE_TEXT = 0;
    private final int TYPE_IMAGE_NORMAL = 1;
    private final int TYPE_IMAGE_GIF = 2;

    private LayoutInflater mInflater;
    private List<ShareDetailAdapter.Item> mData;

    public ShareDetailAdapter(Context context, List<ShareDetail.Node> nodes) {
        mInflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
        for (ShareDetail.Node node : nodes) {
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

    public void refreshData(List<ShareDetail.Node> nodes) {
        mData.clear();
        for (ShareDetail.Node node : nodes) {
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
        ShareDetailAdapter.Item item = mData.get(position);
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
                imageViewHolder.bindData(item);
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
                gifViewHolder.bindData(item);
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

        public void bindData(Item item) {
            imageView.setImageUrl(item.data);
        }
    }

    private class GifViewHolder {

        GifNetworkImageView gifView;

        GifViewHolder(View itemView) {
            gifView = (GifNetworkImageView) itemView.findViewById(R.id.item_images_detail_gif_img);
        }

        public void bindData(Item item) {
            gifView.setImageUrl(item.data);
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
