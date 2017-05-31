package org.wdd.app.android.interestcollection.ui.jokes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.app.InterestCollectionApplication;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJokeDetail;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by richard on 2/24/17.
 */

public class DirtyJokeDetailAdapter extends BaseAdapter {

    private final int TYPE_HEADER = 0;
    private final int TYPE_CONTENT = 1;
    private final int TYPE_IMAGE = 2;

    private LayoutInflater mInflater;

    private List<DirtyJokeDetail.Post> mData;

    public DirtyJokeDetailAdapter(Context context, List<DirtyJokeDetail.Post> posts) {
        mInflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
        if (InterestCollectionApplication.getInstance().getAppReviewStatus()) {
            Iterator<DirtyJokeDetail.Post> it = posts.iterator();
            DirtyJokeDetail.Post post;
            while (it.hasNext()) {
                post = it.next();
                if (post.type == DirtyJokeDetail.PostType.IMAGE || post.type == DirtyJokeDetail.PostType.HEADER) {
                    it.remove();
                }
            }
        }
        mData.addAll(posts);
    }

    public void refreshData(List<DirtyJokeDetail.Post> posts) {
        mData.clear();
        if (InterestCollectionApplication.getInstance().getAppReviewStatus()) {
            Iterator<DirtyJokeDetail.Post> it = posts.iterator();
            while (it.hasNext()) {
                if (it.next().type == DirtyJokeDetail.PostType.IMAGE || it.next().type == DirtyJokeDetail.PostType.HEADER) {
                    it.remove();
                }
            }
        }
        mData.addAll(posts);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        DirtyJokeDetail.Post post = mData.get(position);
        switch (post.type) {
            case HEADER:
                return TYPE_HEADER;
            case TEXT:
                return TYPE_CONTENT;
            case IMAGE:
                return TYPE_IMAGE;
        }
        return TYPE_CONTENT;
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
        DirtyJokeDetail.Post item = mData.get(position);
        switch (getItemViewType(position)) {
            case TYPE_HEADER:
                HeaderViewHolder headerViewHolder;
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_dirty_joke_detail_list_header, parent, false);
                    headerViewHolder = new HeaderViewHolder(convertView);
                    convertView.setTag(headerViewHolder);
                } else {
                    headerViewHolder = (HeaderViewHolder) convertView.getTag();
                }
                headerViewHolder.bindData(item);
                break;
            case TYPE_CONTENT:
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
            case TYPE_IMAGE:
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
        }
        return convertView;
    }

    private class HeaderViewHolder {

        TextView postView;

        public HeaderViewHolder(View itemView) {
            postView = (TextView) itemView.findViewById(R.id.item_dirty_joke_detail_list_text);
        }

        public void bindData(DirtyJokeDetail.Post post) {
            postView.setText(post.content);
        }
    }

    private class TextViewHolder {
        TextView postView;

        public TextViewHolder(View itemView) {
            postView = (TextView) itemView.findViewById(R.id.item_dirty_joke_detail_list_post_text);
        }

        public void bindData(DirtyJokeDetail.Post post) {
            postView.setText(post.content);
        }
    }

    private class ImageViewHolder {

        NetworkImageView imageView;

        public ImageViewHolder(View itemView) {
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_images_detail_image_img);
        }

        public void bindData(DirtyJokeDetail.Post post) {
            imageView.setImageUrl(post.content);
        }
    }

}
