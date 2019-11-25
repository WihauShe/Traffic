package cn.cslg.traffic.module;

import android.app.Activity;
import android.content.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import cn.cslg.traffic.R;
import cn.cslg.traffic.dao.Connection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StationActivity extends Activity{
    private ListView stationList;
    private Button stationBtn;
    private ArrayList<Map<String,String>> stations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station);
        findViews();
        init();
    }
    private void findViews(){
        stationList = findViewById(R.id.station_list);
        stationBtn = findViewById(R.id.station_btn);
    }
    private void init(){
        stations = new ArrayList<>();
        Intent intent = getIntent();
        String city = intent.getStringExtra("city");
        String station = intent.getStringExtra("station");
        StationTask stationTask = new StationTask();
        stationTask.execute(city,station);
    }
    class StationTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            String path="https://api.jisuapi.com/transit/station?appkey=6295111744d2b012";
            path += "&city="+params[0]+"&station="+params[1];
            Connection connection  = new Connection();
            return connection.getDate("GET",path);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            String msg = parseJson(o);
            if(!msg.equals("ok"))
                Toast.makeText(StationActivity.this,msg,Toast.LENGTH_SHORT).show();
            stationList.setAdapter(new SimpleAdapter(StationActivity.this,stations,R.layout.station_list_item,
                    new String[]{"transitno","time","startstation","endstation","price"},
                    new int[]{R.id.station_item_transitno,R.id.station_item_time,R.id.station_item_start,R.id.station_item_end,R.id.station_item_price}));
            stationBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        private String parseJson(Object o){
            String result= o.toString();
            try {
                JSONObject jsonObject=new JSONObject(result);
                int status=jsonObject.getInt("status");
                if(status==0){
                    JSONArray list=jsonObject.getJSONArray("result");
                    for(int i=0;i<list.length();i++){
                        Map<String,String> map = new HashMap<>();
                        JSONObject object = (JSONObject) list.get(i);
                        String tmp_transitno = object.getString("transitno");
                        String tmp_startstation = object.getString("startstation");
                        String tmp_endstation = object.getString("endstation");
                        String starttime = object.getString("starttime");
                        String endtime = object.getString("endtime");
                        String tmp_price = object.getString("price");
                        map.put("transitno",tmp_transitno);
                        map.put("startstation",tmp_startstation);
                        map.put("endstation",tmp_endstation);
                        map.put("time",starttime+"~"+endtime);
                        map.put("price",tmp_price);
                        stations.add(map);
                    }
                    return "ok";
                }else{
                    String msg=jsonObject.getString("msg");
                    return msg;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  null;
        }
    }
}
