package cn.jarlen.imgedit;

import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.jarlen.imgedit.adapter.MainFuncAdapter;
import cn.jarlen.imgedit.bean.MainFuncBean;
import cn.jarlen.imgedit.util.AdUtils;
import cn.jarlen.imgedit.util.FileUtils;
import cn.jarlen.imgedit.util.Utils;

public class MainActivity extends AppCompatActivity implements MainFuncAdapter.OnMainFuncItemListener {


    String picturePath;

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

        RelativeLayout adContainerView = findViewById(R.id.layout_ad);
        AdUtils.loadBanner(this, adContainerView);
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
