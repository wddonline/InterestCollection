package org.wdd.app.android.interestcollection.ui.jokes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.jokes.model.DirtyJokeDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by richard on 2/24/17.
 */

public class DirtyJokeDetailAdapter extends BaseAdapter {

    private final int TYPE_POST = 0;
    private final int TYPE_COMMENT = 1;

    private LayoutInflater mInflater;

    private List<Object> mData;

    public DirtyJokeDetailAdapter(Context context, List<DirtyJokeDetail.Post> posts) {
        mInflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
        DirtyJokeDetail.Post post;
        for (int i = 0; i < posts.size(); i++) {
            post = posts.get(i);
            mData.add(post);
            if (post.comments != null) {
                for (int j = 0; j < post.comments.size(); j++) {
                    mData.add(post.comments.get(j));
                }
            }

        }
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Object item = mData.get(position);
        if (item instanceof DirtyJokeDetail.Comment) {
            return TYPE_COMMENT;
        }
        return TYPE_POST;
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
        Object item = mData.get(position);
        if (item instanceof DirtyJokeDetail.Post) {
            PostViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_dirty_joke_detail_list_post, parent, false);
                viewHolder = new PostViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (PostViewHolder) convertView.getTag();
            }
            DirtyJokeDetail.Post post = (DirtyJokeDetail.Post) item;
            viewHolder.bindData(post);
        } else {
            CommentViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_dirty_joke_detail_list_reply, parent, false);
                viewHolder = new CommentViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (CommentViewHolder) convertView.getTag();
            }
            DirtyJokeDetail.Comment comment = (DirtyJokeDetail.Comment) item;
            viewHolder.bindData(comment);
        }
        return convertView;
    }

    private class PostViewHolder {
        TextView postView;

        public PostViewHolder(View itemView) {
            postView = (TextView) itemView.findViewById(R.id.item_dirty_joke_detail_list_post_text);
        }

        public void bindData(DirtyJokeDetail.Post post) {
            postView.setText(post.content);
        }
    }

    private class CommentViewHolder {

        TextView typeView;
        TextView nameView;
        TextView commentView;

        public CommentViewHolder(View itemView) {
            typeView = (TextView) itemView.findViewById(R.id.item_dirty_joke_detail_list_reply_type);
            nameView = (TextView) itemView.findViewById(R.id.item_dirty_joke_detail_list_reply_name);
            commentView = (TextView) itemView.findViewById(R.id.item_dirty_joke_detail_list_reply_comment);
        }

        public void bindData(DirtyJokeDetail.Comment comment) {
            typeView.setText(comment.type);
            nameView.setText(comment.author);
            commentView.setText(comment.comment);
        }
    }

}
