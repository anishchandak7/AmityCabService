package com.example.anish.amitycabservice;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


//TODO: This whole code is creating a listview according to our preferences, look ,and choice

public class CustomListView extends ArrayAdapter<String> {

    private Integer imgid;
    private String [] drivername;
    private String [] driverexp;
   // private String [] driverrating;
    private Activity context;
    private Long[] seats;
    String [] objectId;
    public CustomListView(Activity context, Integer imgid, String [] drivername, String [] driverexp, Long[] seats) {
        super(context, R.layout.listview_layout,drivername);

        this.context=context;
        this.drivername=drivername;
        this.driverexp=driverexp;
     //   this.driverrating=driverrating;
        this.imgid=imgid;
        this.seats=seats;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View r=convertView;
        ViewHolder viewHolder=null;
        if(r==null)
        {
            LayoutInflater layoutInflater=context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.listview_layout,null,true);
            viewHolder=new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else
        {
            viewHolder= (ViewHolder) r.getTag();

        }
      viewHolder.imageView.setImageResource(R.drawable.profile_pic);
        viewHolder.driver_name.setText(drivername[position]);
        viewHolder.driver_exp.setText(driverexp[position]);
viewHolder.seats_avail.setText("Available seats : "+seats[position]);
        return r;
    }

    class ViewHolder {

        ImageView imageView;
        TextView driver_name;
        TextView driver_exp;
        TextView seats_avail;

        ViewHolder(View v){
            imageView= v.findViewById(R.id.profileimage);
            driver_name= v.findViewById(R.id.drivername);
            driver_exp= v.findViewById(R.id.driverexp);
            seats_avail= v.findViewById(R.id.Seats_avail);
        }
    }

}
