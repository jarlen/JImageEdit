package cn.jarlen.imgedit;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class StartUpActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    public static final int PERMISSIONS = 7;

    private final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private final int WAIT_TIME = 1500; //2秒等待时间

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);

        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(this, PERMISSIONS, permissions).build());
            return;
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goMain();
            }
        }, WAIT_TIME);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        goMain();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        finish();
    }

    private void goMain() {
        startActivity(new Intent(StartUpActivity.this, MainActivity.class));
        finish();
    }

}