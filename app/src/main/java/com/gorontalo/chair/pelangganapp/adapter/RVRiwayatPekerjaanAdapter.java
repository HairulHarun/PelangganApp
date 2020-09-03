package com.gorontalo.chair.pelangganapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gorontalo.chair.pelangganapp.DetailRiwayatPekerjaanActivity;
import com.gorontalo.chair.pelangganapp.PekerjaanActivity;
import com.gorontalo.chair.pelangganapp.R;
import com.gorontalo.chair.pelangganapp.model.RiwayatPekerjaanModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RVRiwayatPekerjaanAdapter extends RecyclerView.Adapter<RVRiwayatPekerjaanAdapter.ViewHolder> {
    private Context context;
    private List<RiwayatPekerjaanModel> list;

    private SessionAdapter sessionAdapter;

    private int lastPosition = -1;

    public RVRiwayatPekerjaanAdapter(Context context, List<RiwayatPekerjaanModel> list){
        super();

        this.list = list;
        this.context = context;

        sessionAdapter = new SessionAdapter(context);
    }

    @Override
    public RVRiwayatPekerjaanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_riwayat_pekerjaan, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        final RiwayatPekerjaanModel pekerjaanModel = list.get(position);

        holder.txtTanggal.setText(pekerjaanModel.getTanggal());
        holder.txtNamaPelanggan.setText(pekerjaanModel.getNamaPelanggan());
        holder.txtTotal.setText(konversiRupiah(Double.parseDouble(pekerjaanModel.getTotal())));
        holder.txtStatus.setText(pekerjaanModel.getStatus());
        holder.txtIdPekerjaan.setText(pekerjaanModel.getIdPekerjaan());
        holder.txtIdKurir.setText(pekerjaanModel.getIdKurir());
        holder.txtNamaKurir.setText(pekerjaanModel.getNamaKurir());
        holder.txtHpKurir.setText(pekerjaanModel.getHpKurir());
        holder.txtPhotoKurir.setText(pekerjaanModel.getPhotoKurir());
        holder.txtRatingKurir.setText(pekerjaanModel.getRatingKurir());
        holder.txtIdOutlet.setText(pekerjaanModel.getIdOutlet());
        holder.txtNamaOutlet.setText(pekerjaanModel.getNamaOutlet());
        holder.txtLatOutlet.setText(String.valueOf(pekerjaanModel.getLatOutlet()));
        holder.txtLongOutlet.setText(String.valueOf(pekerjaanModel.getLongOutlet()));
        holder.txtLatPekerjaan.setText(String.valueOf(pekerjaanModel.getLatPelanggan()));
        holder.txtLongPekerjaan.setText(String.valueOf(pekerjaanModel.getLongPelanggan()));
        holder.txtMetodeBayar.setText(pekerjaanModel.getMetode());
        holder.txtJarak.setText(String.valueOf(pekerjaanModel.getJarak()));

        if (pekerjaanModel.getStatus().equals("0")){
            holder.txtStatus.setBackgroundResource(R.color.colorAccent);
            holder.txtStatus.setText("MENUJU OUTLET");
        }else if (pekerjaanModel.getStatus().equals("1")){
            holder.txtStatus.setBackgroundResource(R.color.colorYellow);
            holder.txtStatus.setText("MULAI BELANJA");
        }else if (pekerjaanModel.getStatus().equals("2")){
            holder.txtStatus.setBackgroundResource(R.color.colorPrimaryDark);
            holder.txtStatus.setText("MENUJU PELANGGAN");
        }else if (pekerjaanModel.getStatus().equals("3")){
            holder.txtStatus.setBackgroundResource(R.color.colorPrimary);
            holder.txtStatus.setText("SELESAI");
        }else if (pekerjaanModel.getStatus().equals("4")){
            holder.txtStatus.setBackgroundResource(R.color.colorRed);
            holder.txtStatus.setText("DIBATALKAN");
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pekerjaanModel.getStatus().equals("3") || pekerjaanModel.getStatus().equals("4")){
                    Intent intent = new Intent(context, DetailRiwayatPekerjaanActivity.class);
                    intent.putExtra("id_pekerjaan", pekerjaanModel.getIdPekerjaan());
                    intent.putExtra("nama_outlet", pekerjaanModel.getNamaOutlet());
                    intent.putExtra("catatan", pekerjaanModel.getCatatan());
                    intent.putExtra("metodebayar", pekerjaanModel.getMetode());
                    intent.putExtra("lat_outlet", pekerjaanModel.getLatOutlet());
                    intent.putExtra("long_outlet", pekerjaanModel.getLongOutlet());
                    intent.putExtra("lat_pelanggan", pekerjaanModel.getLatPelanggan());
                    intent.putExtra("long_pelanggan", pekerjaanModel.getLongPelanggan());
                    intent.putExtra("jarak", pekerjaanModel.getJarak());
                    intent.putExtra("biaya", pekerjaanModel.getBiaya());
                    intent.putExtra("total", pekerjaanModel.getTotal());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{
                    Intent intent = new Intent(context, PekerjaanActivity.class);
                    intent.putExtra("id_pekerjaan", pekerjaanModel.getIdPekerjaan());
                    intent.putExtra("id_kurir", pekerjaanModel.getIdKurir());
                    intent.putExtra("nama_kurir", pekerjaanModel.getNamaKurir());
                    intent.putExtra("hp_kurir", pekerjaanModel.getHpKurir());
                    intent.putExtra("photo_kurir", pekerjaanModel.getPhotoKurir());
                    intent.putExtra("rating_kurir", pekerjaanModel.getRatingKurir());
                    intent.putExtra("id_outlet", pekerjaanModel.getIdOutlet());
                    intent.putExtra("nama_outlet", pekerjaanModel.getNamaOutlet());
                    intent.putExtra("lat_outlet", String.valueOf(pekerjaanModel.getLatOutlet()));
                    intent.putExtra("long_outlet", String.valueOf(pekerjaanModel.getLongOutlet()));
                    intent.putExtra("lat_pelanggan", String.valueOf(pekerjaanModel.getLatPelanggan()));
                    intent.putExtra("long_pelanggan", String.valueOf(pekerjaanModel.getLongPelanggan()));
                    intent.putExtra("jarak", pekerjaanModel.getJarak());
                    intent.putExtra("metodebayar", pekerjaanModel.getMetode());
                    intent.putExtra("total", Integer.parseInt(pekerjaanModel.getTotal()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        });
        setAnimation(holder.itemView, position);
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
        public TextView txtTanggal, txtNamaPelanggan, txtTotal, txtStatus, txtIdPekerjaan, txtIdKurir, txtNamaKurir,
                    txtHpKurir, txtPhotoKurir, txtRatingKurir, txtIdOutlet, txtNamaOutlet,  txtLatOutlet, txtLongOutlet,
                    txtLatPekerjaan, txtLongPekerjaan, txtMetodeBayar, txtJarak;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTanggal = (TextView) itemView.findViewById(R.id.txtCardTanggal);
            txtNamaPelanggan = (TextView) itemView.findViewById(R.id.txtCardNamaPelanggan);
            txtTotal = (TextView) itemView.findViewById(R.id.txtCardTotal);
            txtStatus = (TextView) itemView.findViewById(R.id.txtCardStatus);
            txtIdPekerjaan = (TextView) itemView.findViewById(R.id.txtCardIdPekerjaan);
            txtIdKurir = (TextView) itemView.findViewById(R.id.txtCardIdKurir);
            txtNamaKurir = (TextView) itemView.findViewById(R.id.txtCardNamaKurir);
            txtHpKurir = (TextView) itemView.findViewById(R.id.txtCardHpKurir);
            txtPhotoKurir = (TextView) itemView.findViewById(R.id.txtCardPhotoKurir);
            txtRatingKurir = (TextView) itemView.findViewById(R.id.txtCardRatingKurir);
            txtIdOutlet = (TextView) itemView.findViewById(R.id.txtCardIdOutlet);
            txtNamaOutlet = (TextView) itemView.findViewById(R.id.txtCardNamaOutlet);
            txtLatOutlet = (TextView) itemView.findViewById(R.id.txtCardLatOutlet);
            txtLongOutlet = (TextView) itemView.findViewById(R.id.txtCardLongOutlet);
            txtLatPekerjaan = (TextView) itemView.findViewById(R.id.txtCardLatPekerjaan);
            txtLongPekerjaan = (TextView) itemView.findViewById(R.id.txtCardLongPekerjaan);
            txtMetodeBayar = (TextView) itemView.findViewById(R.id.txtCardMetodeBayar);
            txtJarak = (TextView) itemView.findViewById(R.id.txtCardJarak);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.layoutCard);

        }

    }

    private String konversiRupiah(double angka){
        String hasil = null;
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        hasil = formatRupiah.format(angka);
        return hasil;
    }

}
