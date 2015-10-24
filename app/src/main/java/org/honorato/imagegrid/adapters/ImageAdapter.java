package org.honorato.imagegrid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.honorato.imagegrid.R;

import java.util.List;

/**
 * Created by jlh on 10/7/15.
 *
 * Heavily based on https://developer.android.com/training/material/lists-cards.html
 */
public class ImageAdapter extends ArrayAdapter<String> {

    List<String> mUrls;

    public ImageAdapter(Context ctx, List<String> urls) {
        super(ctx, R.layout.element_view, urls);
        this.mUrls = urls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.element_view, parent, false);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            int padding = (int) getContext().getResources().getDimension(R.dimen.image_padding);
            imageView.setPadding(padding, padding, padding, padding);
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(getContext())
                .load(this.getItem(position))
                .into(imageView);

        return imageView;
    }

    @Override
    public int getCount() {
        return mUrls.size();
    }

    @Override
    public String getItem(int position) {
        return mUrls.get(position);
    }


}
