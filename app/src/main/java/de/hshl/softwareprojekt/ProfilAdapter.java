package de.hshl.softwareprojekt;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ProfilAdapter extends BaseAdapter {

    Context context;
    private ArrayList<Bitmap> imageList;
    private ArrayList<String> idList;
    private LayoutInflater layoutInflater;

    public ProfilAdapter(Context context, ArrayList<Bitmap> imageList, ArrayList<String> idList) {
        this.context = context;
        this.imageList = imageList;
        this.idList = idList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return imageList.size();
    }

    @Override
    public Bitmap getItem(int position) {
        return imageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(idList.get(position));
    }

    public String getItemDes(int position) {
        return idList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder = null;
        if(v==null){
            holder = new ViewHolder();
            v = layoutInflater.inflate(R.layout.adapter_profil, null);
            holder.imageHold = v.findViewById(R.id.picHolder);
            v.setTag(holder);
        }else{
            holder = (ViewHolder) v.getTag();
        }
        Bitmap item = getItem(position);
        String id = getItemDes(position);
        holder.imageHold.setImageBitmap(item);
        holder.imageHold.setContentDescription(id);
        return v;
    }
}

class ViewHolder{
    ImageView imageHold;
}
