package com.gorontalo.chair.pelangganapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gorontalo.chair.pelangganapp.adapter.KoneksiAdapter;
import com.gorontalo.chair.pelangganapp.adapter.RVRiwayatPekerjaanAdapter;
import com.gorontalo.chair.pelangganapp.adapter.SessionAdapter;
import com.gorontalo.chair.pelangganapp.adapter.URLAdapter;
import com.gorontalo.chair.pelangganapp.adapter.VolleyAdapter;
import com.gorontalo.chair.pelangganapp.model.RiwayatPekerjaanModel;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

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

public class RiwayatPekerjaanActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_PEKERJAAN = "pekerjaan";
    private static final String TAG_PEKERJAAN_BARANG = "pekerjaan_barang";

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;
    private Boolean isInternetPresent = false;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private List<RiwayatPekerjaanModel> riwayatPekerjaanModelList;
    private TextView txtEmpty;

    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_pekerjaan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionAdapter = new SessionAdapter(getApplicationContext());
        koneksiAdapter = new KoneksiAdapter(getApplicationContext());
        mRecyclerView = (RecyclerView) findViewById(R.id.rvRiwayatPekerjaan);

        txtEmpty = (TextView) findViewById(R.id.empty_view);

        riwayatPekerjaanModelList = new ArrayList<>();
        adapter = new RVRiwayatPekerjaanAdapter(getApplicationContext(), riwayatPekerjaanModelList);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        Dexter.withActivity(this)
                .withPermissions(
                        android.Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (isInternetPresent = koneksiAdapter.isConnectingToInternet()) {
                                getData(sessionAdapter.getID());
                            }else{
                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(RiwayatPekerjaanActivity.this)
                                                .text("No Connection !")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                .actionLabel("Refresh")
                                                .actionListener(new ActionClickListener() {
                                                    @Override
                                                    public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                                        refresh();
                                                    }
                                                })
                                        , RiwayatPekerjaanActivity.this);
                            }

                        }

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

    public void onBackPressed(){
        startActivity(new Intent(RiwayatPekerjaanActivity.this, MainActivity.class));
    }

    public void getData(final String id_pelanggan) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getRiwayatPekerjaan(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {

                        riwayatPekerjaanModelList.clear();

                        JSONArray pekerjaan = jObj.getJSONArray(TAG_PEKERJAAN);
                        for (int i = 0; i < pekerjaan.length(); i++) {
                            try {
                                JSONObject jsonObject = pekerjaan.getJSONObject(i);

                                RiwayatPekerjaanModel pekerjaanModel = new RiwayatPekerjaanModel();
                                pekerjaanModel.setIdPekerjaan(jsonObject.getString("id"));
                                pekerjaanModel.setIdKurir(jsonObject.getString("id_kurir"));
                                pekerjaanModel.setNamaKurir(jsonObject.getString("nama_kurir"));
                                pekerjaanModel.setHpKurir(jsonObject.getString("hp_kurir"));
                                pekerjaanModel.setPhotoKurir(jsonObject.getString("photo_kurir"));
                                pekerjaanModel.setRatingKurir(jsonObject.getString("rating_kurir"));
                                pekerjaanModel.setMetode(jsonObject.getString("metodebayar"));
                                pekerjaanModel.setBiaya(jsonObject.getString("biaya"));
                                pekerjaanModel.setTotal(jsonObject.getString("total"));
                                pekerjaanModel.setTanggal(jsonObject.getString("tanggal"));
                                pekerjaanModel.setStatus(jsonObject.getString("status_pekerjaan"));
                                pekerjaanModel.setNamaPelanggan(jsonObject.getString("nama_pelanggan"));
                                pekerjaanModel.setCatatan(jsonObject.getString("catatan_pekerjaan"));

                                pekerjaanModel.setLatPelanggan(Double.parseDouble(jsonObject.getString("lat_pekerjaan")));
                                pekerjaanModel.setLongPelanggan(Double.parseDouble(jsonObject.getString("long_pekerjaan")));
                                pekerjaanModel.setJarak(Double.parseDouble(jsonObject.getString("jarak")));


                                JSONArray pekerjaan_barang = jsonObject.getJSONArray(TAG_PEKERJAAN_BARANG);

                                String id_outlet = "";
                                String nama_outlet = "";
                                double lat_outlet = 0;
                                double long_outlet = 0;

                                for (int j = 0; j < pekerjaan_barang.length(); j++) {
                                    JSONObject jsonObject2 = pekerjaan_barang.getJSONObject(j);

                                    id_outlet = jsonObject2.getString("id_outlet");
                                    nama_outlet = jsonObject2.getString("nama_outlet");
                                    lat_outlet = Double.parseDouble(jsonObject2.getString("lat_outlet"));
                                    long_outlet = Double.parseDouble(jsonObject2.getString("long_outlet"));
                                }

                                pekerjaanModel.setIdOutlet(id_outlet);
                                pekerjaanModel.setNamaOutlet(nama_outlet);
                                pekerjaanModel.setLatOutlet(lat_outlet);
                                pekerjaanModel.setLongOutlet(long_outlet);

                                riwayatPekerjaanModelList.add(pekerjaanModel);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }

                        mRecyclerView.setVisibility(View.VISIBLE);
                        txtEmpty.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_PEKERJAAN), Toast.LENGTH_LONG).show();
                        mRecyclerView.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
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
                params.put("id_pelanggan", id_pelanggan);

                return params;
            }

        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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
