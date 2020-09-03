package com.gorontalo.chair.pelangganapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.gorontalo.chair.pelangganapp.adapter.KoneksiAdapter;
import com.gorontalo.chair.pelangganapp.adapter.LocationAddressAdapter;
import com.gorontalo.chair.pelangganapp.adapter.RVFragmentPekerjaanBarangAdapter;
import com.gorontalo.chair.pelangganapp.adapter.SessionAdapter;
import com.gorontalo.chair.pelangganapp.adapter.URLAdapter;
import com.gorontalo.chair.pelangganapp.adapter.VolleyAdapter;
import com.gorontalo.chair.pelangganapp.model.PekerjaanBarangModel;
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
import java.text.NumberFormat;
import java.util.ArrayList;
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

public class DetailRiwayatPekerjaanActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = InvoiceActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_PEKERJAAN_BARANG = "pekerjaan_barang";

    private TextView txtLokasi, txtOutlet, txtJarak, txtSubtotal, txtBiayaKurir, txtTotal, txtCatatan, txtMetode;

    private Intent intent;

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;
    private Boolean isInternetPresent = false;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private List<PekerjaanBarangModel> pekerjaanBarangModelList;

    private String IDPEKERJAAN, OUTLET, TIME, CATATAN, METODEBAYAR, BIAYA, TOTAL;
    private double LAT_PELANGGAN, LONG_PELANGGAN, LAT_OUTLET, LONG_OUTLET, JARAK;

    String tag_json_obj = "json_obj_req";
    int success, jumlah;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_riwayat_pekerjaan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sessionAdapter = new SessionAdapter(getApplicationContext());
        koneksiAdapter = new KoneksiAdapter(getApplicationContext());

        txtLokasi = findViewById(R.id.txtDetailRiwayatLokasi);
        txtOutlet= findViewById(R.id.txtDetailRiwayatOutlet);
        txtJarak= findViewById(R.id.txtDetailRiwayatJarak);
        txtCatatan= findViewById(R.id.txtDetailRiwayatCatatan);
        txtMetode = findViewById(R.id.txtDetailRiwayatMetode);
        txtSubtotal= findViewById(R.id.txtDetailRiwayatSubTotal);
        txtBiayaKurir= findViewById(R.id.txtDetailRiwayatBiayaKurir);
        txtTotal= findViewById(R.id.txtDetailRiwayatTotal);

        mRecyclerView = (RecyclerView)findViewById(R.id.rvDetailRiwayat);
        pekerjaanBarangModelList = new ArrayList<>();

        intent = getIntent();
        IDPEKERJAAN = intent.getStringExtra("id_pekerjaan");
        OUTLET = intent.getStringExtra("nama_outlet");
        CATATAN = intent.getStringExtra("catatan");
        METODEBAYAR = intent.getStringExtra("metodebayar");
        BIAYA = intent.getStringExtra("biaya");
        TOTAL = intent.getStringExtra("total");
        LAT_OUTLET = intent.getDoubleExtra("lat_outlet", 0);
        LONG_OUTLET = intent.getDoubleExtra("long_outlet", 0);
        LAT_PELANGGAN= intent.getDoubleExtra("lat_pelanggan", 0);
        LONG_PELANGGAN= intent.getDoubleExtra("long_pelanggan", 0);
        JARAK = intent.getDoubleExtra("jarak", 0);

        double calculate = Math.ceil(calculateDistance(LAT_PELANGGAN, LONG_PELANGGAN, LAT_OUTLET, LONG_OUTLET, "M"));
        TIME = String.valueOf(calculate*8);

        LocationAddressAdapter locationAddress = new LocationAddressAdapter();
        locationAddress.getAddressFromLocation(LAT_OUTLET, LONG_OUTLET, getApplicationContext(), new GeocoderHandler());

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(DetailRiwayatPekerjaanActivity.this, DetailRiwayatPekerjaanActivity.this)
                .build();

    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            double lat = 0, longi = 0;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }

            txtLokasi.setText(locationAddress);
            initRV();
        }
    }

    private void initRV(){
        adapter = new RVFragmentPekerjaanBarangAdapter(getApplicationContext(), pekerjaanBarangModelList);
        mLayoutManager = new LinearLayoutManager(this);
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
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isInternetPresent = koneksiAdapter.isConnectingToInternet()) {
                                getData(IDPEKERJAAN);
                            }else{
                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(DetailRiwayatPekerjaanActivity.this)
                                                .text("No Connection !")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                .actionLabel("Refresh")
                                                .actionListener(new ActionClickListener() {
                                                    @Override
                                                    public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                                        refresh();
                                                    }
                                                })
                                        , DetailRiwayatPekerjaanActivity.this);
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

        txtOutlet.setText(OUTLET);
        txtCatatan.setText(CATATAN);
        txtMetode.setText(METODEBAYAR);
        txtJarak.setText(String.valueOf(JARAK)+" km ("+TIME+" menit)");
    }

    private void getData(final String id) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, new URLAdapter().getPekerjaanKurirDetail(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {

                        pekerjaanBarangModelList.clear();
                        jumlah = 0;

                        JSONArray pekerjaan = jObj.getJSONArray(TAG_PEKERJAAN_BARANG);

                        for (int i = 0; i < pekerjaan.length(); i++) {
                            try {
                                JSONObject jsonObject = pekerjaan.getJSONObject(i);

                                PekerjaanBarangModel pekerjaanBarangModel = new PekerjaanBarangModel();
                                pekerjaanBarangModel.setIdPekerjaanBarang(jsonObject.getString("id_pekerjaan_barang"));
                                pekerjaanBarangModel.setIdOutlet(jsonObject.getString("id_outlet"));
                                pekerjaanBarangModel.setNamaOutlet(jsonObject.getString("nama_outlet"));
                                pekerjaanBarangModel.setIdOutletBarang(jsonObject.getString("id_outlet_barang"));
                                pekerjaanBarangModel.setNamaOutletBarang(jsonObject.getString("nama_outlet_barang"));
                                pekerjaanBarangModel.setHarga(jsonObject.getString("harga"));
                                pekerjaanBarangModel.setQty(jsonObject.getString("qty"));
                                pekerjaanBarangModel.setJumlah(jsonObject.getString("jumlah"));
                                pekerjaanBarangModel.setPhoto(jsonObject.getString("photo"));

                                pekerjaanBarangModelList.add(pekerjaanBarangModel);

                                jumlah += Integer.parseInt(jsonObject.getString("jumlah"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }

                        txtSubtotal.setText(String.valueOf(jumlah));

                        txtBiayaKurir.setText(konversiRupiah(Double.parseDouble(BIAYA)));
                        txtTotal.setText(konversiRupiah(Double.parseDouble(TOTAL)));

                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_PEKERJAAN_BARANG), Toast.LENGTH_LONG).show();
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
                params.put("id_pekerjaan", id);

                return params;
            }

        };

        VolleyAdapter.getInstance().addToRequestQueue(stringRequest, "json_pekerjaan");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2, String unit) {
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
        return (dist);
    }

    private double deg2rad(double deg){
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad){
        return (rad * 180.0 / Math.PI);
    }

    private String konversiRupiah(double angka){
        String hasil = null;
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        hasil = formatRupiah.format(angka);
        return hasil;
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
