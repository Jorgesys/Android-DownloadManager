package com.jorgesys.downloadmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.CookieManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myDownloadManager";
    private static final int REQUEST_PERMISSIONS = 255;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            String[] PERMISSIONS = {android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(getApplicationContext(), PERMISSIONS)) {
                Log.i(TAG, "Requiring permissions!.");
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSIONS );
            } else {
                Log.i(TAG, "Permissions already set!.");
                downloadPDF();
            }
        } else {
            Log.i(TAG, "Permission request not necessary.");
            downloadPDF();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission Granted! ");
                    downloadPDF();
                } else {
                    Toast.makeText(getApplicationContext(), "The app was not allowed to write/read in your storage", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private String mimetype= "application/pdf";
    private String url = "http://www.africau.edu/images/default/sample.pdf";

    private void downloadPDF() {
        Log.i(TAG, "Download file: downloadPDF().");
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.allowScanningByMediaScanner();
        request.setMimeType(mimetype);
        String cookie = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("Cookie", cookie);
        //request.addRequestHeader("User-Agent", userAgent);
        request.setAllowedNetworkTypes(
                DownloadManager .Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE);

        request.setAllowedOverRoaming(true);
        request.setDescription("Downloading File...");
        request.setTitle("PDF File");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,  "pdf_sample.pdf");
        Log.i(TAG, "The file will be downloaded as : " + Environment.DIRECTORY_DOWNLOADS + "pdf_sample.pdf");
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadManager.enqueue(request);

    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

}
