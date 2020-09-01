package cn.jarlen.imgedit;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.jarlen.imgedit.adapter.MainFuncAdapter;
import cn.jarlen.imgedit.bean.MainFuncBean;
import cn.jarlen.imgedit.util.FileUtils;
import cn.jarlen.imgedit.util.Utils;
import pub.devrel.easypermissions.EasyPermissions;
import pub.devrel.easypermissions.PermissionRequest;

public class MainActivity extends AppCompatActivity implements MainFuncAdapter.OnMainFuncItemListener, EasyPermissions.PermissionCallbacks {


    private final String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /* 用来标识请求gallery的activity */
    public static final int PHOTO_PICKED_WITH_DATA = 3021;
    public static final int PHOTO_PICKED_WITH_DATA_AFTER_KIKAT = 3022;

    public static final int TAKE_PHOTO_CODE = 8;
    public static final int REQUEST_TAKE_PHOTO_PERMISSION = 9;
    public static final int PERMISSIONS = 7;

    String picturePath;

    AlertDialog mAlertDialog;
    MainFuncBean funcBean;

    private RecyclerView mainList;
    private MainFuncAdapter funcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainList = findViewById(R.id.rv_list);
        funcAdapter = new MainFuncAdapter(this, this);
        mainList.setLayoutManager(new GridLayoutManager(this, 3));
        mainList.setAdapter(funcAdapter);

        List<MainFuncBean> funcBeanList = Utils.buildMainData(this);
        funcAdapter.addDataList(funcBeanList);

        if (!EasyPermissions.hasPermissions(MainActivity.this, permissions)) {
            EasyPermissions.requestPermissions(new PermissionRequest.Builder(MainActivity.this, PERMISSIONS, permissions).build());
        }
    }

    @Override
    public void onMainFuncItem(MainFuncBean bean) {
        this.funcBean = bean;
        showSelectImage(bean);
    }

    private void showSelectImage(MainFuncBean bean) {
        getPictureFromPhoto();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_PICKED_WITH_DATA) {

            if (data == null) {
                Toast.makeText(this, "选取图片失败 ", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri originalUri = data.getData();

            if (originalUri == null || originalUri.getPath() == null) {
                Toast.makeText(this, "选取图片失败 ", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] filePathColumn = {MediaStore.MediaColumns.DATA};

            Cursor cursor = getContentResolver().query(originalUri,
                    filePathColumn, null, null, null);

            String picturePath = null;

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                } else {
                    Toast.makeText(this, "选取图片失败 ", Toast.LENGTH_SHORT).show();
                    return;
                }

            } else {
                picturePath = originalUri.getPath();
            }
//            Toast.makeText(this, "选择成功 " + picturePath, Toast.LENGTH_SHORT).show();
            Utils.navigate2Func(this, funcBean, picturePath);
        } else if (requestCode == PHOTO_PICKED_WITH_DATA_AFTER_KIKAT) {
            String picturePath = FileUtils.getPath(this, data.getData());
//            Toast.makeText(this, "选择成功 " + picturePath, Toast.LENGTH_SHORT).show();
            Utils.navigate2Func(this, funcBean, picturePath);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "获取所需权限失败", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * 从相册中获取照片
     **/
    private void getPictureFromPhoto() {
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        Intent photoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoIntent.setType("image/*");

        if (isKitKat) {
            startActivityForResult(photoIntent,
                    PHOTO_PICKED_WITH_DATA_AFTER_KIKAT);
        } else {
            startActivityForResult(photoIntent,
                    PHOTO_PICKED_WITH_DATA);
        }
    }
}
