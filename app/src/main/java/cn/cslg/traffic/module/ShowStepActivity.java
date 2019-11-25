package cn.cslg.traffic.module;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import cn.cslg.traffic.R;

public class ShowStepActivity extends AppCompatActivity {
    private ListView stepList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        stepList = findViewById(R.id.step_list);
        Intent intent = getIntent();
        String[] steps = intent.getStringArrayExtra("steps");
        stepList.setAdapter(new ArrayAdapter<>(ShowStepActivity.this,android.R.layout.simple_list_item_1,steps));
    }
}
