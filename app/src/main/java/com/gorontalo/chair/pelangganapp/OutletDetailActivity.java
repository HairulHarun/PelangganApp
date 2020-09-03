package com.gorontalo.chair.pelangganapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gorontalo.chair.pelangganapp.adapter.DatabaseClientAdapter;
import com.gorontalo.chair.pelangganapp.adapter.KoneksiAdapter;
import com.gorontalo.chair.pelangganapp.adapter.RVBarangAdapter;
import com.gorontalo.chair.pelangganapp.adapter.URLAdapter;
import com.gorontalo.chair.pelangganapp.adapter.VolleyAdapter;
import com.gorontalo.chair.pelangganapp.fragment.FragmentKeranjang;
import com.gorontalo.chair.pelangganapp.model.BarangModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class OutletDetailActivity extends AppCompatActivity {
    private static final String TAG = OutletDetailActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_BARANG= "barang";

    private KoneksiAdapter koneksiAdapter;
    private Boolean isInternetPresent = false;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private List<BarangModel> barangModelList;

    private ImageView imgHeader;
    private TextView txtDeskripsi, txtPemilik, txtWaktu, txtHp, txtTime;

    String tag_json_obj = "json_obj_req";
    int success;

    private Intent intent;
    private String ID, NAMA, PEMILIK, HP, WAKTU, DESKRIPSI, PHOTO, TIME;
    private int JARAK;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentKeranjang bottomSheetFragment = new FragmentKeranjang();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        intent = getIntent();
        ID = intent.getStringExtra("id");
        NAMA = intent.getStringExtra("nama");
        PEMILIK = intent.getStringExtra("pemilik");
        HP = intent.getStringExtra("hp");
        WAKTU = intent.getStringExtra("waktu");
        DESKRIPSI = intent.getStringExtra("deskripsi");
        PHOTO = intent.getStringExtra("photo");
        JARAK = intent.getIntExtra("jarak", 0);
        TIME = intent.getStringExtra("time");

        toolbar.setTitle(NAMA);
        toolbar.setTitleTextColor(R.color.colorBlack);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imgHeader = findViewById(R.id.imgHeaderOutlet);
        txtPemilik = findViewById(R.id.txtDetailPemilik);
        txtDeskripsi= findViewById(R.id.txtDetailDeskripsi);
        txtWaktu= findViewById(R.id.txtDetailWaktu);
        txtHp= findViewById(R.id.txtDetailHp);
        txtTime= findViewById(R.id.txtDetailTime);

        txtPemilik.setText("Oleh : "+PEMILIK);
        txtDeskripsi.setText(DESKRIPSI);
        txtWaktu.setText(WAKTU);
        txtHp.setText(HP);
        txtTime.setText(String.valueOf(JARAK)+" km ( "+TIME+" menit)");

        Picasso.with(getApplicationContext())
                .load(new URLAdapter().getPhotoOutlet()+PHOTO)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .into(imgHeader);

        koneksiAdapter = new KoneksiAdapter(getApplicationContext());
        mRecyclerView = (RecyclerView)findViewById(R.id.rvBarang);

        barangModelList = new ArrayList<>();
        adapter = new RVBarangAdapter(getApplicationContext(), barangModelList);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        hapusData();

        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isInternetPresent = koneksiAdapter.isConnectingToInternet()) {
                                getDataBarang(ID);
                            }else{
                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(OutletDetailActivity.this)
                                                .text("No Connection !")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                .actionLabel("Refresh")
                                                .actionListener(new ActionClickListener() {
                                                    @Override
                                                    public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                                        refresh();
                                                    }
                                                })
                                        , OutletDetailActivity.this);
                            }

                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void getDataBarang(final String id) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getDataBarangOutlet(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        barangModelList.clear();
                        JSONArray barang = jObj.getJSONArray(TAG_BARANG);

                        for (int i = 0; i < barang.length(); i++) {
                            try {
                                JSONObject jsonObject = barang.getJSONObject(i);

                                BarangModel barangModel = new BarangModel();
                                barangModel.setId(jsonObject.getString("id"));
                                barangModel.setKategori(jsonObject.getString("nama_kategori"));
                                barangModel.setOutlet(jsonObject.getString("nama_outlet"));
                                barangModel.setBarang(jsonObject.getString("nama_outlet_barang"));
                                barangModel.setHarga(jsonObject.getString("harga"));
                                barangModel.setStatus(jsonObject.getString("status"));
                                barangModel.setDeskripsi(jsonObject.getString("deskripsi"));
                                barangModel.setPhoto(jsonObject.getString("barang_photo"));

                                barangModelList.add(barangModel);

                                adapter.notifyDataSetChanged();
                                progressDialog.dismiss();

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_BARANG), Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
                progressDialog.dismiss();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);

                return params;
            }

        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");
    }

    private void hapusData(){
        class DeleteTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClientAdapter.getInstance(getApplicationContext()).getAppDatabase()
                        .keranjangDAO()
                        .deleteAll();
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

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void refresh(){
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }
}
