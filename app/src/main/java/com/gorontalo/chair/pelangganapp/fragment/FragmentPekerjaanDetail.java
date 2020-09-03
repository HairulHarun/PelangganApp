package com.gorontalo.chair.pelangganapp.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gorontalo.chair.pelangganapp.PekerjaanActivity;
import com.gorontalo.chair.pelangganapp.R;
import com.gorontalo.chair.pelangganapp.adapter.KoneksiAdapter;
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

public class FragmentPekerjaanDetail extends BottomSheetDialogFragment {
    private static final String TAG = PekerjaanActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_PEKERJAAN_BARANG = "pekerjaan_barang";

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private List<PekerjaanBarangModel> pekerjaanBarangModelList;

    private TextView txtSubtotal, txtBiaya, txtTotal;
    int success, jumlah;

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;

    private Boolean isInternetPresent = false;

    private String ID_PEKERJAAN;
    private double JARAK;
    private int BIAYA, TOTAL;

    public FragmentPekerjaanDetail() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pekerjaan_detail, container, false);

        txtSubtotal= (TextView)view.findViewById(R.id.txtFragmentSubTotal);
        txtBiaya= (TextView)view.findViewById(R.id.txtFragmentBiaya);
        txtTotal= (TextView)view.findViewById(R.id.txtFragmentTotal);

        koneksiAdapter = new KoneksiAdapter(getActivity().getApplicationContext());
        sessionAdapter = new SessionAdapter(getActivity().getApplicationContext());

        Intent intent = getActivity().getIntent();
        ID_PEKERJAAN = intent.getStringExtra("id_pekerjaan");
        JARAK = intent.getDoubleExtra("jarak", 0);
        BIAYA = intent.getIntExtra("biaya", 0);
        TOTAL = intent.getIntExtra("total", 0);

        mRecyclerView = (RecyclerView)view.findViewById(R.id.rvFragmentPekerjaanDetail);

        pekerjaanBarangModelList = new ArrayList<>();
        adapter = new RVFragmentPekerjaanBarangAdapter(getActivity().getApplicationContext(), pekerjaanBarangModelList);

        mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);

        Dexter.withActivity(getActivity())
                .withPermissions(
                        android.Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            if (isInternetPresent = koneksiAdapter.isConnectingToInternet()) {
                                getData(ID_PEKERJAAN);
                            }else{
                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(getActivity())
                                                .text("No Connection !")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                .actionLabel("Refresh")
                                                .actionListener(new ActionClickListener() {
                                                    @Override
                                                    public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                                        refresh();
                                                    }
                                                })
                                        , getActivity());
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
                        Toast.makeText(getActivity().getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();

        return view;
    }

    private void getData(final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new URLAdapter().getPekerjaanKurirDetail(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {

                        pekerjaanBarangModelList.clear();

                        JSONArray pekerjaan = jObj.getJSONArray(TAG_PEKERJAAN_BARANG);

                        int subtotal = 0;

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
                                pekerjaanBarangModel.setStatusPekerjaan(jsonObject.getString("status_pekerjaan"));
                                pekerjaanBarangModel.setPhoto(jsonObject.getString("photo"));

                                subtotal += Integer.parseInt(jsonObject.getString("jumlah"));

                                pekerjaanBarangModelList.add(pekerjaanBarangModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        txtSubtotal.setText(konversiRupiah(subtotal));
                        txtBiaya.setText(konversiRupiah(BIAYA));
                        txtTotal.setText(konversiRupiah(TOTAL));
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), jObj.getString(TAG_PEKERJAAN_BARANG), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", error.toString());
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

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
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
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void refresh(){
        Intent intent = getActivity().getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    private String konversiRupiah(double angka){
        String hasil = null;
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        hasil = formatRupiah.format(angka);
        return hasil;
    }
}
