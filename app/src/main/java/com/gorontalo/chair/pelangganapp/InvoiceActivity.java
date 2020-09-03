package com.gorontalo.chair.pelangganapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.gorontalo.chair.pelangganapp.adapter.DatabaseClientAdapter;
import com.gorontalo.chair.pelangganapp.adapter.RVKeranjangAdapter;
import com.gorontalo.chair.pelangganapp.adapter.SessionAdapter;
import com.gorontalo.chair.pelangganapp.adapter.URLAdapter;
import com.gorontalo.chair.pelangganapp.adapter.VolleyAdapter;
import com.gorontalo.chair.pelangganapp.model.KeranjangModel;
import com.skyfishjy.library.RippleBackground;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class InvoiceActivity extends AppCompatActivity {
    private static final String TAG = InvoiceActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private RecyclerView recyclerView;
    private TextView txtLokasi, txtOutlet, txtJarak, txtSubtotal, txtBiayaKurir, txtTotal, txtCatatan, txtMetode;
    private Button btnPesan;
    private ImageView imgRipple;

    private Intent intent;

    private SessionAdapter sessionAdapter;

    private String IDPELANGGAN, OUTLET, LOKASI, TIME, CATATAN, METODEBAYAR;
    private double LATITUDE, LONGITUDE;
    private int JARAK, BIAYA;

    int success, TOTAL;

    private List<KeranjangModel> taskList;
    private AlertDialog alertDialog;
    private RippleBackground rippleBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionAdapter = new SessionAdapter(getApplicationContext());
        IDPELANGGAN = sessionAdapter.getID();

        txtLokasi = findViewById(R.id.txtInvoiceLokasi);
        txtOutlet= findViewById(R.id.txtInvoiceOutlet);
        txtJarak= findViewById(R.id.txtInvoiceJarak);
        txtCatatan= findViewById(R.id.txtInvoiceCatatan);
        txtMetode = findViewById(R.id.txtInvoiceMetode);
        txtSubtotal= findViewById(R.id.txtInvoiceSubTotal);
        txtBiayaKurir= findViewById(R.id.txtInvoiceBiayaKurir);
        txtTotal= findViewById(R.id.txtInvoiceTotal);
        btnPesan = findViewById(R.id.btnInvoicePesan);

        intent = getIntent();
        LOKASI = intent.getStringExtra("lokasi");
        OUTLET = intent.getStringExtra("outlet");
        TIME = intent.getStringExtra("time");
        CATATAN = intent.getStringExtra("catatan");
        METODEBAYAR = intent.getStringExtra("metodebayar");
        LATITUDE = intent.getDoubleExtra("latitude", 0);
        LONGITUDE = intent.getDoubleExtra("longitude", 0);
        JARAK = intent.getIntExtra("jarak", 0);

        getData();
        getTotal();

        txtLokasi.setText(LOKASI);
        txtOutlet.setText(OUTLET);
        txtCatatan.setText(CATATAN);
        txtMetode.setText(METODEBAYAR);
        txtJarak.setText(String.valueOf(JARAK)+" km ("+TIME+" menit)");

        recyclerView = findViewById(R.id.rvFragmentInvoice);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    }

    private void simpanPesanan() {
        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().tambahPekerjaan(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        String id_pekerjaan = jObj.getString("id_pekerjaan");
                        String id_kurir = jObj.getString("id_kurir");
                        String nama_kurir = jObj.getString("nama_kurir");
                        String hp_kurir = jObj.getString("hp_kurir");
                        String photo_kurir = jObj.getString("photo_kurir");
                        String rating_kurir = jObj.getString("rating_kurir");
                        String id_outlet = jObj.getString("id_outlet");
                        String nama_outlet = jObj.getString("nama_outlet");
                        String lat_outlet = jObj.getString("lat_outlet");
                        String long_outlet = jObj.getString("long_outlet");
                        String lat_pelanggan = jObj.getString("lat_pelanggan");
                        String long_pelanggan = jObj.getString("long_pelanggan");
                        String saldo_pelanggan = jObj.getString("saldo_pelanggan");

                        Intent intent = new Intent(InvoiceActivity.this, PekerjaanActivity.class);
                        intent.putExtra("id_pekerjaan", id_pekerjaan);
                        intent.putExtra("id_kurir", id_kurir);
                        intent.putExtra("nama_kurir", nama_kurir);
                        intent.putExtra("hp_kurir", hp_kurir);
                        intent.putExtra("photo_kurir", photo_kurir);
                        intent.putExtra("rating_kurir", rating_kurir);
                        intent.putExtra("id_outlet", id_outlet);
                        intent.putExtra("nama_outlet", nama_outlet);
                        intent.putExtra("lat_outlet", lat_outlet);
                        intent.putExtra("long_outlet", long_outlet);
                        intent.putExtra("lat_pelanggan", lat_pelanggan);
                        intent.putExtra("long_pelanggan", long_pelanggan);
                        intent.putExtra("jarak", JARAK);
                        intent.putExtra("biaya", biaya());
                        intent.putExtra("metodebayar", METODEBAYAR);
                        intent.putExtra("total", TOTAL);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        taskList.clear();
                        alertDialog.hide();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                alertDialog.hide();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Gson gson = new Gson();
                String data = gson.toJson(taskList);

                Map<String, String> params = new HashMap<String, String>();
                params.put("id_pelanggan", IDPELANGGAN);
                params.put("lat_pelanggan", String.valueOf(LATITUDE));
                params.put("long_pelanggan", String.valueOf(LONGITUDE));
                params.put("jarak", String.valueOf(JARAK));
                params.put("biaya", String.valueOf(biaya()));
                params.put("total", String.valueOf(TOTAL));
                params.put("metode", METODEBAYAR);
                params.put("catatan", CATATAN);
                params.put("pesanan", data);

                return params;
            }

        };

        strReq.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");
    }

    private void getData() {
        class GetTasks extends AsyncTask<Void, Void, List<KeranjangModel>> {

            @Override
            protected List<KeranjangModel> doInBackground(Void... voids) {
                taskList = DatabaseClientAdapter
                        .getInstance(getApplicationContext())
                        .getAppDatabase()
                        .keranjangDAO()
                        .getAll();

                return taskList;
            }

            @Override
            protected void onPostExecute(List<KeranjangModel> tasks) {
                super.onPostExecute(tasks);

                RVKeranjangAdapter adapter = new RVKeranjangAdapter(getApplicationContext(), tasks);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);

                btnPesan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                this.finish();
                            }

                            private void finish() {
                                simpanPesanan();
                            }
                        }, 3000);
                    }
                });
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
                total = DatabaseClientAdapter.getInstance(getApplicationContext()).getAppDatabase()
                        .keranjangDAO()
                        .getTotal();

                return total;
            }

            @Override
            protected void onPostExecute(Integer aVoid) {
                super.onPostExecute(aVoid);
                txtSubtotal.setText(konversiRupiah(aVoid));

                int biaya = biaya();
                int total = biaya+aVoid;
                txtBiayaKurir.setText(konversiRupiah(biaya));
                txtTotal.setText(konversiRupiah(total));
                TOTAL = total;
            }
        }

        GetTotal dt = new GetTotal();
        dt.execute();
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

    private String konversiRupiah(double angka){
        String hasil = null;
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        hasil = formatRupiah.format(angka);
        return hasil;
    }

    private void refresh(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(InvoiceActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_find_kurir, null);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();

        rippleBackground = (RippleBackground)view.findViewById(R.id.content);
        rippleBackground.startRippleAnimation();

    }

}
