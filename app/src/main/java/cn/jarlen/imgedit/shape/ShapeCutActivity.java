package cn.jarlen.imgedit.shape;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.base.BaseActivity;
import cn.jarlen.imgedit.base.OnAdapterItemClickListener;

public class ShapeCutActivity extends BaseActivity implements OnAdapterItemClickListener<Integer> {

    private ShapeCropView shapeCropView;

    private RecyclerView rvListShape;
    private ShapeListAdapter shapeListAdapter;

    @Override
    protected void onBindView(Bundle savedInstanceState) {
        setToolbarTitle("形状切图");
        shapeCropView = findViewById(R.id.view_shape_cut);
        rvListShape = findViewById(R.id.rv_list_shape);
        LinearLayoutManager shapeLinearLayoutManager = new LinearLayoutManager(this);
        shapeLinearLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvListShape.setLayoutManager(shapeLinearLayoutManager);
        shapeListAdapter = new ShapeListAdapter(this);
        shapeListAdapter.setAdapterItemClickListener(this);
        rvListShape.setAdapter(shapeListAdapter);

        findViewById(R.id.iv_toolbar_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = shapeCropView.getBitmap();
                saveImage(bitmap, "shape_");
            }
        });
        Bitmap bitmap = BitmapFactory.decodeFile(getImagePath());
        shapeCropView.setBackGroundBitMap(bitmap);
        List<Integer> shapeData = getShapeData();
        shapeListAdapter.addDataList(shapeData);
    }

    private List<Integer> getShapeData() {
        List<Integer> shapeData = new ArrayList<>();
        shapeData.add(R.drawable.shape_bear);
        shapeData.add(R.drawable.shape_butterfly);
        shapeData.add(R.drawable.shape_circle);
        shapeData.add(R.drawable.shape_clover);
        shapeData.add(R.drawable.shape_heart_arrow);
        shapeData.add(R.drawable.shape_qq);
        shapeData.add(R.drawable.shape_rabbit);
        shapeData.add(R.drawable.shape_star);
        return shapeData;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_shape_cut;
    }

    @Override
    public void onItemClick(Integer item, int position) {
        shapeCropView.setMaskResource(item);
    }
}
