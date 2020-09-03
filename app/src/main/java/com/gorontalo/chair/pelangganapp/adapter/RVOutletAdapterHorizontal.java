package com.gorontalo.chair.pelangganapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gorontalo.chair.pelangganapp.OutletDetailActivity;
import com.gorontalo.chair.pelangganapp.R;
import com.gorontalo.chair.pelangganapp.model.OutletModel;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

public class RVOutletAdapterHorizontal extends RecyclerView.Adapter<RVOutletAdapterHorizontal.ViewHolder>{
    private Context context;
    private List<OutletModel> list;
    private double LATITUDE, LONGITUDE;
    private String LOKASI;

    private int lastPosition = -1;

    public RVOutletAdapterHorizontal(Context context, List<OutletModel> list, double latitude, double longitude, String lokasi){
        super();

        this.list = list;
        this.context = context;
        this.LATITUDE = latitude;
        this.LONGITUDE = longitude;
        this.LOKASI = lokasi;
    }

    @Override
    public RVOutletAdapterHorizontal.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_outlet_horizontal, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        final OutletModel outletModel = list.get(position);

        int calculate = calculateDistance(LATITUDE,
                LONGITUDE,
                outletModel.getLatitude(),
                outletModel.getLongitude(),
                "K");

        String time = String.valueOf(calculate*4);

        holder.txtCardId.setText(outletModel.getId());
        holder.txtCardNama.setText(outletModel.getNama());
        holder.txtCardPemilik.setText(outletModel.getPemilik());
        holder.txtCardHp.setText(outletModel.getHp());
        holder.txtCardWaktu.setText(outletModel.getWaktu());
        holder.txtCardTime.setText(time+" menit"+" ( "+calculate+" km )");
        holder.txtCardDeskripsi.setText(outletModel.getDeskripsi());
        holder.txtCardPhoto.setText(outletModel.getPhoto());

        Picasso.with(context)
                .load(new URLAdapter().getPhotoOutlet()+outletModel.getPhoto())
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into(holder.imgOutlet);
        setAnimation(holder.itemView, position);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, OutletDetailActivity.class);
                intent.putExtra("id", outletModel.getId());
                intent.putExtra("nama", outletModel.getNama());
                intent.putExtra("pemilik", outletModel.getPemilik());
                intent.putExtra("hp", outletModel.getHp());
                intent.putExtra("waktu", outletModel.getWaktu());
                intent.putExtra("deskripsi", outletModel.getDeskripsi());
                intent.putExtra("photo", outletModel.getPhoto());
                intent.putExtra("lokasi", LOKASI);
                intent.putExtra("latitude", LATITUDE);
                intent.putExtra("longitude", LONGITUDE);
                intent.putExtra("jarak", calculate);
                intent.putExtra("time", time);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    private int calculateDistance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        if (unit == "K") {
            dist = dist * 1.609344;
        } else if (unit == "M") {
            dist = dist * 0.8684;
        }

        int hasil = (int) Math.round(dist*2);

        return hasil;
    }

    private double deg2rad(double deg){
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad){
        return (rad * 180.0 / Math.PI);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));//to make duration random number between [0,501)
            viewToAnimate.startAnimation(anim);
        }

    }
    @Override
    public int getItemCount() {
        if(list!= null) {
            return list.size();
        }else{
            return 0;
        }

    }
    class ViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout layout;
        public ImageView imgOutlet;
        public TextView txtCardId, txtCardNama, txtCardTime, txtCardPemilik, txtCardHp, txtCardWaktu, txtCardDeskripsi, txtCardPhoto;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            imgOutlet = (ImageView) itemView.findViewById(R.id.imgOutlet);
            txtCardId = (TextView) itemView.findViewById(R.id.txtCardIdOutlet);
            txtCardNama = (TextView) itemView.findViewById(R.id.txtCardNamaOutlet);
            txtCardTime = (TextView) itemView.findViewById(R.id.txtCardTime);
            txtCardPemilik= (TextView) itemView.findViewById(R.id.txtCardPemilik);
            txtCardHp= (TextView) itemView.findViewById(R.id.txtCardHp);
            txtCardWaktu= (TextView) itemView.findViewById(R.id.txtCardWaktu);
            txtCardDeskripsi= (TextView) itemView.findViewById(R.id.txtCardDeskripsi);
            txtCardPhoto= (TextView) itemView.findViewById(R.id.txtCardPhoto);
        }

    }

}
