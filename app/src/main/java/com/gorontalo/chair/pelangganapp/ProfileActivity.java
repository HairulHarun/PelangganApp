package com.gorontalo.chair.pelangganapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gorontalo.chair.pelangganapp.adapter.SessionAdapter;
import com.gorontalo.chair.pelangganapp.adapter.URLAdapter;
import com.gorontalo.chair.pelangganapp.adapter.VolleyAdapter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    private SessionAdapter sessionAdapter;

    private ImageView btnLogout, imgPhoto;
    private Button btnUpload;
    private TextView txtNama, txtSaldo, txtKtp, txtHp, txtSex, txtEmail, txtAlamat;

    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private String KEY_ID = "id";
    private String KEY_IMAGE = "image";
    private String KEY_STATUS = "status";

    private CircleImageView circleImageView;

    private ProgressDialog pDialog;
    String tag_json_obj = "json_obj_req";
    int success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionAdapter = new SessionAdapter(getApplicationContext());

        txtNama = (TextView) findViewById(R.id.txtProfileNama);
        txtSaldo = (TextView) findViewById(R.id.txtProfileSaldo);
        txtKtp = (TextView) findViewById(R.id.txtProfileKtp);
        txtHp = (TextView) findViewById(R.id.txtProfileHp);
        txtSex = (TextView) findViewById(R.id.txtProfileSex);
        txtEmail = (TextView) findViewById(R.id.txtProfileEmail);
        txtAlamat = (TextView) findViewById(R.id.txtProfileAlamat);
        btnLogout = (ImageView) findViewById(R.id.btnLogout);
        imgPhoto = (ImageView) findViewById(R.id.user_profile_photo);
        btnUpload = (Button) findViewById(R.id.btnUpload);

        circleImageView = findViewById(R.id.user_profile_photo);

        txtNama.setText(sessionAdapter.getName());
        txtSaldo.setText("Rp. "+sessionAdapter.getSaldo());
        txtKtp.setText(sessionAdapter.getKtp());
        txtHp.setText(sessionAdapter.getHp());
        txtSex.setText(sessionAdapter.getSex());
        txtEmail.setText(sessionAdapter.getEmail());
        txtAlamat.setText(sessionAdapter.getAlamat());

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateStatus(sessionAdapter.getID(), "No");
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

        imgPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage(sessionAdapter.getID(), getStringImage(bitmap), "pelanggan");
            }
        });

        Picasso.with(ProfileActivity.this)
                .load(new URLAdapter().getPhotoProfilePelanggan()+sessionAdapter.getPhoto())
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(circleImageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imgPhoto.setImageBitmap(bitmap);
                btnUpload.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage(final String id, final String image, final String status){
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);

        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().uploadPhotoProfile(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        loading.dismiss();
                        btnUpload.setVisibility(View.INVISIBLE);
                        Toast.makeText(ProfileActivity.this, s, Toast.LENGTH_LONG).show();

                        Picasso.with(ProfileActivity.this)
                                .load(new URLAdapter().getPhotoProfilePelanggan()+sessionAdapter.getPhoto())
                                .placeholder(R.mipmap.ic_launcher_round)
                                .error(R.mipmap.ic_launcher_round)
                                .memoryPolicy(MemoryPolicy.NO_CACHE)
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .into(circleImageView);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        try{
                            loading.dismiss();
                            Toast.makeText(ProfileActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }catch(NullPointerException e){
                            Toast.makeText(getApplicationContext(), "File Gambar Terlalu Besar", Toast.LENGTH_SHORT).show();
                        }
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new Hashtable<String, String>();

                params.put(KEY_ID, id);
                params.put(KEY_IMAGE, image);
                params.put(KEY_STATUS, status);

                return params;
            }
        };

        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");
    }

    private String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void updateStatus(final String id, final String status) {
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        pDialog.setMessage("Logout ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, new URLAdapter().updateStatusPelanggan(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e(TAG, "Data Response: " + response);
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        sessionAdapter.logoutUser();
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                        Toast.makeText(getApplicationContext(),jObj.getString(TAG_MESSAGE), Toast.LENGTH_SHORT).show();
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
                Log.e(TAG, "Get Data Errorrrr: " + error.getMessage());
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id", id);
                params.put("status", status);

                return params;
            }

        };

        // Adding request to request queue
        VolleyAdapter.getInstance().addToRequestQueue(strReq, "volley");
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
