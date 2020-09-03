package com.gorontalo.chair.pelangganapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.gorontalo.chair.pelangganapp.R;
import com.gorontalo.chair.pelangganapp.model.KeranjangModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RVKeranjangAdapter extends RecyclerView.Adapter<RVKeranjangAdapter.ViewHolder> {
    private Context context;
    private List<KeranjangModel> list;
    private int lastPosition = -1;
    private KeranjangModel keranjangModel;

    public RVKeranjangAdapter(Context context, List<KeranjangModel> list){
        super();

        this.list = list;
        this.context = context;
        keranjangModel = new KeranjangModel();
    }

    @Override
    public RVKeranjangAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_keranjang, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        final KeranjangModel keranjangModel = list.get(position);

        holder.txtCardNama.setText(keranjangModel.getNama_barang());
        holder.txtCardQty.setText(String.valueOf(keranjangModel.getJumlah()));
        holder.txtCardJumlah.setText(konversiRupiah(keranjangModel.getHarga()*keranjangModel.getJumlah()));

        setAnimation(holder.itemView, position);
    }

    private String konversiRupiah(double angka){
        String hasil = null;
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        hasil = formatRupiah.format(angka);
        return hasil;
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            anim.setDuration(new Random().nextInt(501));
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
        public TextView txtCardNama, txtCardQty, txtCardJumlah;

        public ViewHolder(View itemView) {
            super(itemView);
            txtCardNama = (TextView) itemView.findViewById(R.id.txtCardNama);
            txtCardQty= (TextView) itemView.findViewById(R.id.txtCardQty);
            txtCardJumlah= (TextView) itemView.findViewById(R.id.txtCardJumlah);
        }

    }
}
