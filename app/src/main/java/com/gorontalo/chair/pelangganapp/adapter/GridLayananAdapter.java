package com.gorontalo.chair.pelangganapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gorontalo.chair.pelangganapp.R;
import com.gorontalo.chair.pelangganapp.model.LayananModel;

public class GridLayananAdapter extends BaseAdapter {
    private final Context mContext;
    private final String[] mobileValues;

    public GridLayananAdapter(Context context, String[] mobileValues) {
        this.mContext = context;
        this.mobileValues = mobileValues;
    }

    @Override
    public int getCount() {
        return mobileValues.length;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(mContext);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.card_layanan, null);

            // set value into textview
            TextView textView = (TextView) gridView.findViewById(R.id.txtLayanan);
            textView.setText(mobileValues[position]);

            // set image based on selected text
            ImageView imageView = (ImageView) gridView.findViewById(R.id.imgLayanan);

            String mobile = mobileValues[position];

            if (mobile.equals("lb-mobil")) {
                imageView.setImageResource(R.drawable.logo);
            } else if (mobile.equals("lb-motor")) {
                imageView.setImageResource(R.drawable.logo);
            } else if (mobile.equals("lb-food")) {
                imageView.setImageResource(R.drawable.logo);
            } else if (mobile.equals("lb-laundry")) {
                imageView.setImageResource(R.drawable.logo);
            } else if (mobile.equals("lb-delivery")) {
                imageView.setImageResource(R.drawable.logo);
            } else {
                imageView.setImageResource(R.drawable.logo);
            }

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    private int getResourseId(String pVariableName, String pResourcename, String pPackageName) throws RuntimeException {
        try {
            return mContext.getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
        } catch (Exception e) {
            throw new RuntimeException("Error getting Resource ID.", e);
        }
    }
}
