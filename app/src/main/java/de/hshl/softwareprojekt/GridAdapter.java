package de.hshl.softwareprojekt;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridAdapter extends BaseAdapter {

    Context context;
    View view;
    LayoutInflater layoutInflater;

    int [] images = {
            R.drawable.major,
            R.drawable.hannah,
            R.drawable.hannah,
            R.drawable.hannah,
            R.drawable.hannah,
            R.drawable.hannah,
            R.drawable.hannah,
            R.drawable.hannah,
            R.drawable.hannah,
            R.drawable.hannah,
            R.drawable.hannah,
            R.drawable.hannah,
            R.drawable.hannah,
//

    };
    public GridAdapter(Context context) {
        this.context = context;

    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return images[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView imageView = new ImageView(context);
        imageView.setImageResource(images[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(400, 400));
        return imageView;


    }
}
