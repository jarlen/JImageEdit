package cn.jarlen.imgedit.text;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import cn.jarlen.imgedit.R;
import cn.jarlen.imgedit.filter.WMFilterOperateView;
import cn.jarlen.imgedit.filter.edit.OperateUtils;

public class TextAddActivity extends AppCompatActivity {

    WMFilterOperateView operateView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_add);
        operateView = findViewById(R.id.view_wm_filter_operate);
        findViewById(R.id.btn_color).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.btn_font).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.btn_add_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

}
