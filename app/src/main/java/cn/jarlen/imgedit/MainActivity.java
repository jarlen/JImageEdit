package cn.jarlen.imgedit;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

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

    public static final int PERMISSIONS = 7;

    String picturePath;

    MainFuncBean funcBean;

    private RecyclerView mainList;
    private MainFuncAdapter funcAdapter;

    private AdView mAdView;

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

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onMainFuncItem(MainFuncBean bean) {
        this.funcBean = bean;
        if (bean.getFuncType() == MainFuncBean.MAIN_FUNC_TYPE_PUZZLE) {
            Utils.navigate2Func(this, funcBean, picturePath);
            return;
        }
        showSelectImage(bean);
    }

    private void showSelectImage(MainFuncBean bean) {
        getPictureFromPhoto();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_OK) {
//            return;
//        }
//
//        if (requestCode == PHOTO_PICKED_WITH_DATA) {
//
//            if (data == null) {
//                Toast.makeText(this, "选取图片失败 ", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            Uri originalUri = data.getData();
//
//            if (originalUri == null || originalUri.getPath() == null) {
//                Toast.makeText(this, "选取图片失败 ", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            String[] filePathColumn = {MediaStore.MediaColumns.DATA};
//
//            Cursor cursor = getContentResolver().query(originalUri,
//                    filePathColumn, null, null, null);
//
//            String picturePath = null;
//
//            if (cursor != null) {
//                if (cursor.moveToFirst()) {
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    picturePath = cursor.getString(columnIndex);
//                } else {
//                    Toast.makeText(this, "选取图片失败 ", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//            } else {
//                picturePath = originalUri.getPath();
//            }
//            Utils.navigate2Func(this, funcBean, picturePath);
//        } else if (requestCode == PHOTO_PICKED_WITH_DATA_AFTER_KIKAT) {
//            String picturePath = FileUtils.getPath(this, data.getData());
//            Utils.navigate2Func(this, funcBean, picturePath);
//        }
//    }

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
        Intent photoIntent = new Intent(Intent.ACTION_PICK);
        photoIntent.setType("image/*");

        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() != RESULT_OK) {
                    return;
                }
                String picturePath = FileUtils.getPath(MainActivity.this, result.getData().getData());
                Utils.navigate2Func(MainActivity.this, funcBean, picturePath);
            }
        }).launch(photoIntent);
    }
}
