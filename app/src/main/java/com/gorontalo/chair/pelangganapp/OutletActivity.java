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
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.gorontalo.chair.pelangganapp.adapter.GPSTrackingAdapter;
import com.gorontalo.chair.pelangganapp.adapter.KoneksiAdapter;
import com.gorontalo.chair.pelangganapp.adapter.LocationAddressAdapter;
import com.gorontalo.chair.pelangganapp.adapter.RVOutletAdapter;
import com.gorontalo.chair.pelangganapp.adapter.RVOutletAdapterHorizontal;
import com.gorontalo.chair.pelangganapp.adapter.SessionAdapter;
import com.gorontalo.chair.pelangganapp.adapter.URLAdapter;
import com.gorontalo.chair.pelangganapp.adapter.VolleyAdapter;
import com.gorontalo.chair.pelangganapp.model.OutletModel;
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

import in.myinnos.imagesliderwithswipeslibrary.Animations.DescriptionAnimation;
import in.myinnos.imagesliderwithswipeslibrary.SliderLayout;
import in.myinnos.imagesliderwithswipeslibrary.SliderTypes.BaseSliderView;
import in.myinnos.imagesliderwithswipeslibrary.SliderTypes.TextSliderView;

public class OutletActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_OUTLET = "outlet";

    private KoneksiAdapter koneksiAdapter;
    private SessionAdapter sessionAdapter;
    private GPSTrackingAdapter gpsTrackingAdapter;
    private Boolean isInternetPresent = false;
    private RecyclerView mRecyclerViewHorisontal, mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManagerHorisontal, mLayoutManager;
    private RecyclerView.Adapter adapter, adapterHorisontal;
    private List<OutletModel> outletModelList, outletModelList2;
    private SearchView searchView;

    private LinearLayout layoutLokasi;
    private TextView txtLokasi;

    private SliderLayout sliderLayout;

    private GoogleApiClient mGoogleApiClient;
    private int PLACE_PICKER_REQUEST = 1;

    private double LATITUDE, LONGITUDE;

    String tag_json_obj = "json_obj_req";
    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outlet);

        gpsTrackingAdapter = new GPSTrackingAdapter(getApplicationContext());
        sessionAdapter = new SessionAdapter(getApplicationContext());
        koneksiAdapter = new KoneksiAdapter(getApplicationContext());
        sliderLayout = findViewById(R.id.slider);

        txtLokasi = findViewById(R.id.txtPilihLokasi);
        layoutLokasi = findViewById(R.id.layoutLokasi);

        searchView = findViewById(R.id.searchOutlet);

        mRecyclerViewHorisontal = (RecyclerView)findViewById(R.id.rvOutlet);
        mRecyclerView = (RecyclerView)findViewById(R.id.rvOutlet2);
        outletModelList = new ArrayList<>();
        outletModelList2 = new ArrayList<>();

        if (gpsTrackingAdapter.canGetLocation()){
            LATITUDE = gpsTrackingAdapter.getLatitude();
            LONGITUDE = gpsTrackingAdapter.getLongitude();
            LocationAddressAdapter locationAddress = new LocationAddressAdapter();
            locationAddress.getAddressFromLocation(LATITUDE, LONGITUDE, getApplicationContext(), new GeocoderHandler());
        }else{
            gpsTrackingAdapter.showSettingsAlert();
        }

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(OutletActivity.this, OutletActivity.this)
                .build();

        layoutLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(OutletActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        sliderLayout.stopAutoCycle();
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        int calculate = calculateDistance(LATITUDE,
                LONGITUDE,
                Double.parseDouble(String.valueOf(slider.getBundle().get("latitude"))),
                Double.parseDouble(String.valueOf(slider.getBundle().get("longitude"))),
                "K");

        String time = String.valueOf(calculate*4);

        Intent intent = new Intent(OutletActivity.this, OutletDetailActivity.class);
        intent.putExtra("id", String.valueOf(slider.getBundle().get("id")));
        intent.putExtra("nama", String.valueOf(slider.getBundle().get("nama")));
        intent.putExtra("pemilik", String.valueOf(slider.getBundle().get("pemilik")));
        intent.putExtra("hp", String.valueOf(slider.getBundle().get("hp")));
        intent.putExtra("waktu", String.valueOf(slider.getBundle().get("waktu")));
        intent.putExtra("deskripsi", String.valueOf(slider.getBundle().get("deskripsi")));
        intent.putExtra("photo", String.valueOf(slider.getBundle().get("photo")));
        intent.putExtra("lokasi", txtLokasi.getText());
        intent.putExtra("latitude", LATITUDE);
        intent.putExtra("longitude", LONGITUDE);
        intent.putExtra("jarak", calculate);
        intent.putExtra("time", time);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                StringBuilder stBuilder = new StringBuilder();

                String address = String.format("%s", place.getAddress());

                LATITUDE = place.getLatLng().latitude;
                LONGITUDE = place.getLatLng().longitude;

                stBuilder.append(address);

                txtLokasi.setText(stBuilder.toString());

                initRV();
                searchData();
            }
        }
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

    private void initRV(){
        adapterHorisontal = new RVOutletAdapterHorizontal(getApplicationContext(), outletModelList, LATITUDE, LONGITUDE, txtLokasi.getText().toString());
        mLayoutManagerHorisontal = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerViewHorisontal.setLayoutManager(mLayoutManagerHorisontal);
        mRecyclerViewHorisontal.setHasFixedSize(true);
        mRecyclerViewHorisontal.setAdapter(adapterHorisontal);

        adapter = new RVOutletAdapter(getApplicationContext(), outletModelList2, LATITUDE, LONGITUDE, txtLokasi.getText().toString());
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
                                getImage(sessionAdapter.getID());
                                getDataOutlet("");
                            }else{
                                SnackbarManager.show(
                                        com.nispok.snackbar.Snackbar.with(OutletActivity.this)
                                                .text("No Connection !")
                                                .duration(com.nispok.snackbar.Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                                                .actionLabel("Refresh")
                                                .actionListener(new ActionClickListener() {
                                                    @Override
                                                    public void onActionClicked(com.nispok.snackbar.Snackbar snackbar) {
                                                        refresh();
                                                    }
                                                })
                                        , OutletActivity.this);
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

            LATITUDE = gpsTrackingAdapter.getLatitude();
            LONGITUDE = gpsTrackingAdapter.getLongitude();

            initRV();
            searchData();
        }
    }

    private void searchData(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getDataOutlet(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void getImage(final String id) {

        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getDataOutletRandom(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {

                        outletModelList.clear();
                        JSONArray jsonArray = jObj.getJSONArray(TAG_OUTLET);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                OutletModel outletModel = new OutletModel();
                                outletModel.setId(obj.getString("id"));
                                outletModel.setNama(obj.getString("nama"));
                                outletModel.setPemilik(obj.getString("pemilik"));
                                outletModel.setHp(obj.getString("hp"));
                                outletModel.setWaktu(obj.getString("waktu"));
                                outletModel.setDeskripsi(obj.getString("deskripsi"));
                                outletModel.setPhoto(obj.getString("photo"));
                                outletModel.setLatitude(Double.parseDouble(obj.getString("latitude")));
                                outletModel.setLongitude(Double.parseDouble(obj.getString("longitude")));

                                outletModelList.add(outletModel);

                                TextSliderView textSliderView = new TextSliderView(OutletActivity.this);
                                textSliderView.description(obj.getString("nama"))
                                        .descriptionSize(14)
                                        .image(new URLAdapter().getPhotoOutlet()+obj.getString("photo"))
                                        .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                                        .setOnSliderClickListener(OutletActivity.this);

                                textSliderView.bundle(new Bundle());
                                textSliderView.getBundle().putString("id", obj.getString("id"));
                                textSliderView.getBundle().putString("nama", obj.getString("nama"));
                                textSliderView.getBundle().putString("pemilik", obj.getString("pemilik"));
                                textSliderView.getBundle().putString("hp", obj.getString("hp"));
                                textSliderView.getBundle().putString("waktu", obj.getString("waktu"));
                                textSliderView.getBundle().putString("deskripsi", obj.getString("deskripsi"));
                                textSliderView.getBundle().putString("photo", obj.getString("photo"));
                                textSliderView.getBundle().putString("latitude", obj.getString("latitude"));
                                textSliderView.getBundle().putString("longitude", obj.getString("longitude"));

                                sliderLayout.addSlider(textSliderView);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        adapterHorisontal.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getApplicationContext(),jObj.getString(TAG_OUTLET), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Data Errorrrr: " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);

                return params;
            }

        };

        // Adding request to request queue
        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");

        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Stack);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);

        sliderLayout.setPresetTransformer("Stack");

    }

    private void getDataOutlet(String param) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().getDataOutletRandom2(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {

                        outletModelList2.clear();

                        JSONArray pekerjaan = jObj.getJSONArray(TAG_OUTLET);

                        for (int i = 0; i < pekerjaan.length(); i++) {
                            try {
                                JSONObject jsonObject = pekerjaan.getJSONObject(i);

                                OutletModel outletModel = new OutletModel();
                                outletModel.setId(jsonObject.getString("id"));
                                outletModel.setNama(jsonObject.getString("nama"));
                                outletModel.setPemilik(jsonObject.getString("pemilik"));
                                outletModel.setHp(jsonObject.getString("hp"));
                                outletModel.setWaktu(jsonObject.getString("waktu"));
                                outletModel.setDeskripsi(jsonObject.getString("deskripsi"));
                                outletModel.setPhoto(jsonObject.getString("photo"));
                                outletModel.setLatitude(Double.parseDouble(jsonObject.getString("latitude")));
                                outletModel.setLongitude(Double.parseDouble(jsonObject.getString("longitude")));

                                outletModelList2.add(outletModel);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                            }
                        }
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_OUTLET), Toast.LENGTH_LONG).show();
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
                params.put("param", param);

                return params;
            }

        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");
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
