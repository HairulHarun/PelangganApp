package com.gorontalo.chair.pelangganapp.adapter;

import android.content.Context;
import android.os.AsyncTask;
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

import com.gorontalo.chair.pelangganapp.R;
import com.gorontalo.chair.pelangganapp.model.BarangModel;
import com.gorontalo.chair.pelangganapp.model.KeranjangModel;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

public class RVBarangAdapter extends RecyclerView.Adapter<RVBarangAdapter.ViewHolder> {
    private Context context;
    private List<BarangModel> list;
    private int lastPosition = -1;
    private KeranjangModel keranjangModel;

    public RVBarangAdapter(Context context, List<BarangModel> list){
        super();

        this.list = list;
        this.context = context;
    }

    @Override
    public RVBarangAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_barang, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        final BarangModel barangModel= list.get(position);

        holder.txtCardId.setText(barangModel.getId());
        holder.txtCardNama.setText(barangModel.getBarang());
        holder.txtCardHarga.setText(barangModel.getHarga());
        holder.txtCardStatus.setText(barangModel.getStatus());

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(holder.txtValue.getText().toString());
                if (qty != 0){
                    if (qty == 1){
                        holder.txtValue.setText("0");
                        hapusData(barangModel.getId(), barangModel.getBarang(), "0", barangModel.getHarga());
                    }else{
                        holder.txtValue.setText(String.valueOf(qty-1));
                        updateData(barangModel.getId(), barangModel.getBarang(), String.valueOf(qty-1), barangModel.getHarga());
                    }
                }
            }
        });

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(holder.txtValue.getText().toString());
                if (qty == 0){
                    holder.txtValue.setText(String.valueOf(qty+1));
                    simpanData(barangModel.getId(), barangModel.getBarang(), String.valueOf(qty+1), barangModel.getHarga());
                }else{
                    holder.txtValue.setText(String.valueOf(qty+1));
                    updateData(barangModel.getId(), barangModel.getBarang(), String.valueOf(qty+1), barangModel.getHarga());
                }
            }
        });

        Picasso.with(context)
                .load(new URLAdapter().getPhotoBarang()+barangModel.getPhoto())
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into(holder.imgBarang);
        setAnimation(holder.itemView, position);
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
        public LinearLayout layout;
        public ImageView imgBarang, btnMinus, btnPlus;
        public TextView txtCardId, txtCardNama, txtCardHarga, txtCardStatus, txtValue;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
            imgBarang = (ImageView) itemView.findViewById(R.id.imgBarang);
            btnMinus = (ImageView) itemView.findViewById(R.id.btnMinus);
            btnPlus = (ImageView) itemView.findViewById(R.id.btnPlus);
            txtCardId = (TextView) itemView.findViewById(R.id.txtCardIdBarang);
            txtCardNama = (TextView) itemView.findViewById(R.id.txtCardNama);
            txtCardHarga = (TextView) itemView.findViewById(R.id.txtCardHarga);
            txtCardStatus = (TextView) itemView.findViewById(R.id.txtCardStatus);
            txtValue = (TextView) itemView.findViewById(R.id.txtCardValue);
        }

    }

    private void simpanData(String id_barang, String nama, String jumlah, String harga){
        class SaveTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                keranjangModel = new KeranjangModel();
                keranjangModel.setId_barang(id_barang);
                keranjangModel.setNama_barang(nama);
                keranjangModel.setJumlah(Integer.parseInt(jumlah));
                keranjangModel.setHarga(Integer.parseInt(harga));

                DatabaseClientAdapter.getInstance(context).getAppDatabase().keranjangDAO().insert(keranjangModel);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }

    private void updateData(String id_barang, String nama, String jumlah, String harga){
        class UpdateTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClientAdapter.getInstance(context).getAppDatabase()
                        .keranjangDAO()
                        .updateById(id_barang, jumlah);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }

        UpdateTask ut = new UpdateTask();
        ut.execute();
    }

    private void hapusData(String id_barang, String nama, String jumlah, String harga){
        class DeleteTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {

                DatabaseClientAdapter.getInstance(context).getAppDatabase()
                        .keranjangDAO()
                        .deleteById(id_barang);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();
    }
}
