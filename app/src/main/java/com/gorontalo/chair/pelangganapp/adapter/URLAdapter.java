package com.gorontalo.chair.pelangganapp.adapter;

public class URLAdapter {
//        private String URL = "http://192.168.43.163/kurir-app/webservices/";
//        private String URL = "http://192.168.1.20/kurir-app/webservices/";
    private String URL = "https://luwukbike.com/webservices/";

//        private String URL_PHOTO = "http://192.168.43.163/kurir-app/admin-control/assets/images/photo/";
//        private String URL_PHOTO = "http://192.168.1.20/kurir-app/admin-control/assets/images/photo/";
    private String URL_PHOTO = "https://luwukbike.com/admin-control/assets/images/photo/";

    public String loginPelanggan(){
        return URL = URL+"ws-login-pelanggan.php";
    }

    public String registerPelanggan(){
        return URL = URL+"ws-register-pelanggan.php";
    }

    public String updateLokasiPelanggan(){
        return URL = URL+"ws-update-lokasi-pelanggan.php";
    }

    public String updateStatusPelanggan(){
        return URL = URL+"ws-update-status-pelanggan.php";
    }

    public String uploadPhotoProfile(){
        return URL = URL+"ws-upload-photo.php";
    }

    public String getDataOutletRandom(){
        return URL = URL+"ws-getoutlet-random.php";
    }

    public String getDataOutletRandom2(){
        return URL = URL+"ws-getoutlet-random-2.php";
    }

    public String getDataOutletDetail(){
        return URL = URL+"ws-getoutlet-id.php";
    }

    public String getDataBarangRandom(){
        return URL = URL+"ws-getoutletbarang-random.php";
    }

    public String getDataBarangOutlet(){
        return URL = URL+"ws-getoutletbarang.php";
    }

    public String tambahPekerjaan(){
        return URL = URL+"ws-tambah-pekerjaan.php";
    }

    public String getPhotoBarang(){
        return URL = URL_PHOTO+"barang/";
    }

    public String getPhotoOutlet(){
        return URL = URL_PHOTO+"outlet/";
    }

    public String getPhotoProfilePelanggan(){
        return URL = URL_PHOTO+"profile-pelanggan/";
    }

    public String getPhotoProfileKurir(){
        return URL = URL_PHOTO+"profile-kurir/";
    }

    public String getPekerjaanKurirDetail(){
        return URL = URL+"ws-pekerjaan-kurir-detail.php";
    }

    public String getStatusPekerjaan(){
        return URL = URL+"ws-get-status-pekerjaan.php";
    }

    public String getBiayaKurir(){
        return URL = URL+"ws-get-biaya.php";
    }

    public String getSaldoPelanggan(){
        return URL = URL+"ws-get-saldo-pelanggan.php";
    }

    public String simpanPenilaianKurir(){
        return URL = URL+"ws-simpan-penilaian.php";
    }

    public String getRiwayatPekerjaan(){
        return URL = URL+"ws-pekerjaan-pelanggan.php";
    }

    public String batalkanPekerjaan(){
        return URL = URL+"ws-batal-pekerjaan.php";
    }
}
