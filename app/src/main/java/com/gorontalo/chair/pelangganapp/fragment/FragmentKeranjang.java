package com.gorontalo.chair.pelangganapp.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.gorontalo.chair.pelangganapp.InvoiceActivity;
import com.gorontalo.chair.pelangganapp.R;
import com.gorontalo.chair.pelangganapp.adapter.DatabaseClientAdapter;
import com.gorontalo.chair.pelangganapp.adapter.RVKeranjangAdapter;
import com.gorontalo.chair.pelangganapp.adapter.SessionAdapter;
import com.gorontalo.chair.pelangganapp.model.KeranjangModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentKeranjang extends BottomSheetDialogFragment {
    private RecyclerView recyclerView;
    private Spinner spinner;
    private TextView txtSubTotal, txtBiaya, txtTotal;
    private EditText txtCatatan;
    private Button btnKonfirmasi;

    private SessionAdapter sessionAdapter;

    private String LOKASI, OUTLET, TIME;
    private int TOTAL, JARAK;
    private double LATITUDE, LONGITUDE;

    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intent = getActivity().getIntent();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_keranjang, container, false);

        recyclerView = view.findViewById(R.id.rvFragmentKeranjang);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        spinner = view.findViewById(R.id.spinnerMetodeBayar);
        txtSubTotal= view.findViewById(R.id.txtFragmentTotal);
        txtBiaya = view.findViewById(R.id.txtFragmentBiayaKurir);
        txtTotal = view.findViewById(R.id.txtFragmentTotalAkhir);
        txtCatatan = view.findViewById(R.id.txtFragmentCatatan);
        btnKonfirmasi = view.findViewById(R.id.btnKonfirmasi);

        sessionAdapter = new SessionAdapter(getActivity().getApplicationContext());

        OUTLET = intent.getStringExtra("nama");
        LOKASI = intent.getStringExtra("lokasi");
        TIME = intent.getStringExtra("time");
        LATITUDE = intent.getDoubleExtra("latitude", 0);
        LONGITUDE = intent.getDoubleExtra("longitude", 0);
        JARAK = intent.getIntExtra("jarak", 0);

        List<String> categories = new ArrayList<String>();
        categories.add("TUNAI");
        categories.add("NON TUNAI");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(dataAdapter);

        getData();
        getTotal();
        getJumlah();

        return view;
    }

    private int biaya(){
        int biaya1 = 0, biaya2 = 0, biaya_akhir = 0;
        int jarak_sisa = 0;
        if (JARAK <= 5){
            biaya_akhir = Integer.parseInt(sessionAdapter.getBiaya1());
        }else{
            jarak_sisa = JARAK - 5;

            biaya1 = Integer.parseInt(sessionAdapter.getBiaya1());
            biaya2 = jarak_sisa * Integer.parseInt(sessionAdapter.getBiaya2());

            biaya_akhir = biaya1+biaya2;
        }

        return biaya_akhir;
    }

    private void getData() {
        class GetTasks extends AsyncTask<Void, Void, List<KeranjangModel>> {

            @Override
            protected List<KeranjangModel> doInBackground(Void... voids) {
                List<KeranjangModel> taskList = DatabaseClientAdapter
                        .getInstance(getActivity().getApplicationContext())
                        .getAppDatabase()
                        .keranjangDAO()
                        .getAll();

                return taskList;
            }

            @Override
            protected void onPostExecute(List<KeranjangModel> tasks) {
                super.onPostExecute(tasks);
                RVKeranjangAdapter adapter = new RVKeranjangAdapter(getActivity().getApplicationContext(), tasks);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    private void getTotal(){
        class GetTotal extends AsyncTask<Void, Void, Integer> {
            int total;

            @Override
            protected Integer doInBackground(Void... voids) {
                total = DatabaseClientAdapter.getInstance(getActivity().getApplicationContext()).getAppDatabase()
                        .keranjangDAO()
                        .getTotal();

                return total;
            }

            @Override
            protected void onPostExecute(Integer aVoid) {
                super.onPostExecute(aVoid);
                txtSubTotal.setText(konversiRupiah(aVoid));
                txtBiaya.setText(konversiRupiah(biaya()));
                txtTotal.setText(konversiRupiah(aVoid+biaya()));
                TOTAL = aVoid;
            }
        }

        GetTotal dt = new GetTotal();
        dt.execute();
    }

    private void getJumlah(){
        class GetJumlah extends AsyncTask<Void, Void, Integer> {
            int jumlah;

            @Override
            protected Integer doInBackground(Void... voids) {
                jumlah = DatabaseClientAdapter.getInstance(getActivity().getApplicationContext()).getAppDatabase()
                        .keranjangDAO()
                        .getJumlah();

                return jumlah;
            }

            @Override
            protected void onPostExecute(Integer aVoid) {
                super.onPostExecute(aVoid);

                if (aVoid > 0){
                    btnKonfirmasi.setVisibility(View.VISIBLE);
                    btnKonfirmasi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (spinner.getSelectedItem().toString().equals("NON TUNAI")){
                                int saldo = Integer.parseInt(sessionAdapter.getSaldo());

                                if (saldo > (TOTAL+biaya())){
                                    intent = new Intent(getActivity(), InvoiceActivity.class);
                                    intent.putExtra("outlet", OUTLET);
                                    intent.putExtra("lokasi", LOKASI);
                                    intent.putExtra("latitude", LATITUDE);
                                    intent.putExtra("longitude", LONGITUDE);
                                    intent.putExtra("jarak", JARAK);
                                    intent.putExtra("time", TIME);
                                    intent.putExtra("catatan", txtCatatan.getText().toString());
                                    intent.putExtra("metodebayar", spinner.getSelectedItem().toString());
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }else{
                                    Toast.makeText(getActivity().getApplicationContext(), "Saldo anda tidak cukup !", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                intent = new Intent(getActivity(), InvoiceActivity.class);
                                intent.putExtra("outlet", OUTLET);
                                intent.putExtra("lokasi", LOKASI);
                                intent.putExtra("latitude", LATITUDE);
                                intent.putExtra("longitude", LONGITUDE);
                                intent.putExtra("jarak", JARAK);
                                intent.putExtra("time", TIME);
                                intent.putExtra("catatan", txtCatatan.getText().toString());
                                intent.putExtra("metodebayar", spinner.getSelectedItem().toString());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        }

        GetJumlah dt = new GetJumlah();
        dt.execute();
    }

    private String konversiRupiah(double angka){
        String hasil = null;
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        hasil = formatRupiah.format(angka);
        return hasil;
    }
}
