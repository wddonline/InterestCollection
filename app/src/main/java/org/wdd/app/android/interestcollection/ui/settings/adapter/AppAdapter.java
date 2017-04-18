package org.wdd.app.android.interestcollection.ui.settings.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.wdd.app.android.interestcollection.R;
import org.wdd.app.android.interestcollection.ui.base.AbstractCommonAdapter;
import org.wdd.app.android.interestcollection.ui.settings.model.AppModel;
import org.wdd.app.android.interestcollection.views.NetworkImageView;

import java.util.List;

/**
 * Created by richard on 4/14/17.
 */

public class AppAdapter extends AbstractCommonAdapter<AppModel> {

    private LayoutInflater mInflater;
    private OnItemClickedListener mListener;

    public AppAdapter(Context context, List<AppModel> data) {
        super(context, data);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_app_recommendation, parent, false);
        AppViewHolder holder = new AppViewHolder(view);
        return holder;
    }

    @Override
    protected void onBindDataViewHolder(RecyclerView.ViewHolder holder, AppModel item, int position) {
        AppViewHolder viewHolder = (AppViewHolder) holder;
        viewHolder.bindData(item);
    }

    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.mListener = listener;
    }

    private class AppViewHolder extends RecyclerView.ViewHolder {

        View clickView;
        NetworkImageView imageView;
        TextView nameView;
        TextView descView;
        TextView versionView;

        public AppViewHolder(View itemView) {
            super(itemView);
            clickView = itemView.findViewById(R.id.item_app_recommendation_click);
            imageView = (NetworkImageView) itemView.findViewById(R.id.item_app_recommendation_img);
            nameView = (TextView) itemView.findViewById(R.id.item_app_recommendation_name);
            descView = (TextView) itemView.findViewById(R.id.item_app_recommendation_desc);
            versionView = (TextView) itemView.findViewById(R.id.item_app_recommendation_version);
        }

        public void bindData(final AppModel item) {
            imageView.setImageUrl(item.imgUrl);
            nameView.setText(item.name);
            descView.setText(item.desc);
            versionView.setText(item.version);
            clickView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener == null) return;
                    mListener.onItemClicked(item);
                }
            });
        }
    }

    public interface OnItemClickedListener {

        void onItemClicked(AppModel app);

    }

}
