package com.gorontalo.chair.pelangganapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gorontalo.chair.pelangganapp.adapter.SessionAdapter;
import com.gorontalo.chair.pelangganapp.adapter.URLAdapter;
import com.gorontalo.chair.pelangganapp.adapter.VolleyAdapter;
import com.gorontalo.chair.pelangganapp.fragment.FragmentPekerjaanDetail;
import com.gorontalo.chair.pelangganapp.model.LocationsModel;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class PekerjaanActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "sukses";
    private static final String TAG_STATUS= "status";
    private static final String TAG_MESSAGE= "message";

    private HashMap<String, Marker> mMarkers = new HashMap<>();
    private GoogleMap gMap;
    private SupportMapFragment mapFragment;

    private SessionAdapter sessionAdapter;

    private static LatLng PELANGGAN = null;
    private static LatLng OUTLET = null;

    private Marker mPelanggan;
    private Marker mOutlet;

    private ImageView imgPekerjaanDetail;
    private CircleImageView circleImageView;
    private TextView txtNamaKurir, txtHpKurir, txtStatus, txtMetode, txtTotal;
    private RatingBar ratingBarKurir;
    private Button btnChat, btnPanggil, btnBatal;

    private Intent intent;
    private String IDPEKERJAAN, IDKURIR, NAMAKURIR, HPKURIR, PHOTOKURIR, RATINGKURIR, IDOUTLET, NAMAOUTLET, METODEBAYAR, TOTALBAYAR;
    private double LATOUTLET, LONGOUTLET, LATPELANGGAN, LONGPELANGGAN, JARAK;
    private int BIAYA;

    private AlertDialog alertDialog;
    private Timer timer;

    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pekerjaan);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        circleImageView = findViewById(R.id.imgPekerjaan);
        imgPekerjaanDetail = findViewById(R.id.imgPekerjaanDetail);
        txtNamaKurir = findViewById(R.id.txtPekerjaanNamaKurir);
        txtHpKurir = findViewById(R.id.txtPekerjaanHpKurir);
        txtStatus = findViewById(R.id.txtPekerjaanStatus);
        txtMetode = findViewById(R.id.txtPekerjaanMetodeBayar);
        txtTotal = findViewById(R.id.txtPekerjaanTotal);
        ratingBarKurir = findViewById(R.id.ratingBarKurir);
        btnChat = findViewById(R.id.btnPekerjaanChat);
        btnPanggil = findViewById(R.id.btnPekerjaanPanggil);
        btnBatal = findViewById(R.id.btnBatal);

        sessionAdapter = new SessionAdapter(getApplicationContext());

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        intent = getIntent();
        IDPEKERJAAN = intent.getStringExtra("id_pekerjaan");
        IDKURIR = intent.getStringExtra("id_kurir");
        NAMAKURIR = intent.getStringExtra("nama_kurir");
        HPKURIR = intent.getStringExtra("hp_kurir");
        PHOTOKURIR = intent.getStringExtra("photo_kurir");
        RATINGKURIR = intent.getStringExtra("rating_kurir");
        IDOUTLET = intent.getStringExtra("id_outlet");
        NAMAOUTLET = intent.getStringExtra("nama_outlet");
        LATOUTLET = Double.parseDouble(intent.getStringExtra("lat_outlet"));
        LONGOUTLET  = Double.parseDouble(intent.getStringExtra("long_outlet"));
        LATPELANGGAN = Double.parseDouble(intent.getStringExtra("lat_pelanggan"));
        LONGPELANGGAN = Double.parseDouble(intent.getStringExtra("long_pelanggan"));
        JARAK = intent.getDoubleExtra("jarak", 0);
        BIAYA = intent.getIntExtra("biaya", 0);
        METODEBAYAR = intent.getStringExtra("metodebayar");
        TOTALBAYAR = String.valueOf(intent.getIntExtra("total", 0));

        PELANGGAN = new LatLng(LATPELANGGAN, LONGPELANGGAN);
        OUTLET = new LatLng(LATOUTLET, LONGOUTLET);

        txtNamaKurir.setText(NAMAKURIR);
        txtHpKurir.setText(HPKURIR);
        txtMetode.setText(METODEBAYAR);
        txtTotal.setText(konversiRupiah(Double.parseDouble(TOTALBAYAR)));
        ratingBarKurir.setRating(Float.parseFloat(RATINGKURIR));

        imgPekerjaanDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentPekerjaanDetail bottomSheetFragment = new FragmentPekerjaanDetail();
                bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
            }
        });

        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogPembatalan();
            }
        });

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWhatsApp(HPKURIR);
            }
        });

        btnPanggil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", HPKURIR, null)));
            }
        });

        Picasso.with(PekerjaanActivity.this)
                .load(new URLAdapter().getPhotoProfileKurir()+PHOTOKURIR)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(circleImageView);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getStatus(IDPEKERJAAN);
            }
        }, 0, 10000);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            if (!sessionAdapter.getID().equals("")){
                gMap = googleMap;
                gMap.setMaxZoomPreference(16);
                loginToFirebase();

                mPelanggan= gMap.addMarker(new MarkerOptions().position(PELANGGAN).title(sessionAdapter.getName()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_pelanggan)));
                mPelanggan.setTag(0);
                mOutlet= gMap.addMarker(new MarkerOptions().position(OUTLET).title(NAMAOUTLET).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_outlet)));
                mOutlet.setTag(0);
            }
        }catch (NullPointerException e){

        }
    }

    public void onBackPressed(){
        startActivity(new Intent(PekerjaanActivity.this, RiwayatPekerjaanActivity.class));
    }

    private void loginToFirebase() {
        String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    try {
                        subscribeToUpdates2();
                    }catch (IllegalStateException | NullPointerException e){
                        Log.d("Main Activity", "Error Fragment");
                    }
                    Log.d(TAG, "firebase auth success");
                } else {
                    Log.d(TAG, "firebase auth failed");
                }
            }
        });
    }

    private void subscribeToUpdates2() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path_kurir));
        ref.child(IDKURIR).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try{
                    LocationsModel locations = dataSnapshot.getValue(LocationsModel.class);

                    String key = dataSnapshot.getKey();
                    double lat = locations.getLatitude();
                    double lng = locations.getLongitude();

                    LatLng location = new LatLng(lat, lng);
                    if (!mMarkers.containsKey(key)) {
                        mMarkers.put(key, gMap.addMarker(new MarkerOptions().position(location).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_kurir))));
                    } else {
                        mMarkers.get(key).setPosition(location);
                    }
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (Marker marker : mMarkers.values()) {
                        builder.include(marker.getPosition());
                    }
                    gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));

                }catch (NullPointerException e){

                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("tmz", "Failed to read value.", error.toException());
            }
        });
    }

    private void getStatus(final String id) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new URLAdapter().getStatusPekerjaan(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        String status = jObj.getString(TAG_STATUS);
                        if (status.equals("0")){
                            txtStatus.setText("MENUJU OUTLET");
                            txtStatus.setBackgroundResource(R.color.colorAccent);
                        }else if (status.equals("1")){
                            txtStatus.setText("MULAI BELANJA");
                            txtStatus.setBackgroundResource(R.color.colorYellow);
                        }else if (status.equals("2")){
                            txtStatus.setText("MENUJU PELANGGAN");
                            txtStatus.setBackgroundResource(R.color.colorPrimaryDark);
                            btnBatal.setVisibility(View.INVISIBLE);
                        }else if (status.equals("3")){
                            txtStatus.setText("SELESAI");
                            txtStatus.setBackgroundResource(R.color.colorPrimary);
                            btnBatal.setVisibility(View.VISIBLE);
                            showDialog();
                            timer.cancel();
                        }else if (status.equals("4")){
                            txtStatus.setText("DIBATALKAN");
                            timer.cancel();
                            startActivity(new Intent(PekerjaanActivity.this, RiwayatPekerjaanActivity.class));
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_STATUS), Toast.LENGTH_LONG).show();
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

    private void simpanPenilaian(final String id_pekerjaan, String rating, String catatan) {

        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().simpanPenilaianKurir(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response.toString());
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(PekerjaanActivity.this, MainActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
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
                params.put("id_pekerjaan", id_pekerjaan);
                params.put("rating", rating);
                params.put("catatan", catatan);

                return params;
            }

        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");
    }

    private void updateStatus(final String id, final String id_kurir, final String status, final String catatan) {

        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().batalkanPekerjaan(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        timer.cancel();
                        startActivity(new Intent(PekerjaanActivity.this, RiwayatPekerjaanActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(),jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Get Data Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("id_kurir", id_kurir);
                params.put("status", status);
                params.put("catatan", catatan);

                return params;
            }

        };

        // Adding request to request queue
        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PekerjaanActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_penilaian_kurir, null);
        TextView txtNama = view.findViewById(R.id.txtPenilaianNamaKurir);
        TextView txtHp = view.findViewById(R.id.txtPenilaianHpKurir);
        CircleImageView imgPhoto = view.findViewById(R.id.imgPenilaian);
        RatingBar ratingBar = view.findViewById(R.id.ratingBarPenilaianKurir);
        EditText txtCatatan = view.findViewById(R.id.txtPenilaianCatatan);
        Button btnSimpan = view.findViewById(R.id.btnPenilaianKirim);

        txtNama.setText(NAMAKURIR);
        txtHp.setText(HPKURIR);

        Picasso.with(PekerjaanActivity.this)
                .load(new URLAdapter().getPhotoProfileKurir()+PHOTOKURIR)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(imgPhoto);

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpanPenilaian(IDPEKERJAAN, String.valueOf(ratingBar.getRating()), txtCatatan.getText().toString());
            }
        });

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDialogPembatalan(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PekerjaanActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_pembatalan_pesanan, null);
        TextView txtNama = view.findViewById(R.id.txtPembatalanNamaKurir);
        TextView txtHp = view.findViewById(R.id.txtPembatalanHpKurir);
        CircleImageView imgPhoto = view.findViewById(R.id.imgPembatalan);
        EditText txtCatatan = view.findViewById(R.id.txtPembatalanCatatan);
        Button btnBatal = view.findViewById(R.id.btnPembatalan);

        txtNama.setText(NAMAKURIR);
        txtHp.setText(HPKURIR);

        Picasso.with(PekerjaanActivity.this)
                .load(new URLAdapter().getPhotoProfileKurir()+PHOTOKURIR)
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(imgPhoto);

        btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatus(IDPEKERJAAN, IDKURIR, "4", txtCatatan.getText().toString());
            }
        });

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

    public void openWhatsApp(String number){
        boolean isWhatsappInstalled = whatsappInstalledOrNot("com.whatsapp");
        if (isWhatsappInstalled) {
            String text = "Ping !";
            String finalNumber = "+62"+number.substring(1);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("http://api.whatsapp.com/send?phone="+finalNumber +"&text="+text));
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("market://details?id=com.whatsapp");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(goToMarket);
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
