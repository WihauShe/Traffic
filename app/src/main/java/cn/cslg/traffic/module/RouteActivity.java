package cn.cslg.traffic.module;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.cslg.traffic.R;
import cn.cslg.traffic.dao.Connection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RouteActivity extends Activity {
    private TextView transitno,price,maxPrice,runTime,startStation,endStation;
    private ListView stationList;
    private Map<String,String> data;
    private String[] stations;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        findViews();
        init();
    }

    private void findViews(){
        transitno = findViewById(R.id.route_transitno);
        price = findViewById(R.id.route_price);
        maxPrice = findViewById(R.id.route_maxprice);
        runTime = findViewById(R.id.route_time);
        startStation = findViewById(R.id.route_start);
        endStation = findViewById(R.id.route_end);
        stationList = findViewById(R.id.route_station);
    }

    private void init(){
        Intent intent = getIntent();
        String city = intent.getStringExtra("city");
        String route = intent.getStringExtra("route");
        RouteTask routeTask = new RouteTask();
        routeTask.execute(city,route);
    }
    class RouteTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            String path = "https://api.jisuapi.com/transit/line?appkey=6295111744d2b012";
            path += "&city="+params[0]+"&transitno="+params[1];
            Connection connection  = new Connection();
            return connection.getDate("GET",path);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            String msg = parseJson(o);
            if(!msg.equals("ok"))
                Toast.makeText(RouteActivity.this,msg,Toast.LENGTH_SHORT).show();
            transitno.setText(data.get("transitno"));
            price.setText(data.get("price"));
            maxPrice.setText(data.get("maxprice"));
            runTime.setText(data.get("time"));
            startStation.setText(data.get("startstation"));
            endStation.setText(data.get("endstation"));
            stationList.setAdapter(new ArrayAdapter<>(RouteActivity.this,android.R.layout.simple_list_item_1,stations));
        }

        private String parseJson(Object o){
            String result= o.toString();
            try {
                JSONObject jsonObject=new JSONObject(result);
                int status=jsonObject.getInt("status");
                if(status==0){
                    JSONArray tmp_list=jsonObject.getJSONArray("result");
                    JSONObject object = tmp_list.getJSONObject(0);
                    String tmp_transitno = object.getString("transitno");
                    String tmp_start = object.getString("startstation");
                    String tmp_end = object.getString("endstation");
                    String tmp_price = object.getString("price");
                    String tmp_maxprice = object.getString("maxprice");
                    String tmp_time = object.getString("timetable");
                    data = new HashMap<>();
                    data.put("transitno",tmp_transitno);
                    data.put("startstation",tmp_start);
                    data.put("endstation",tmp_end);
                    data.put("price",tmp_price);
                    data.put("maxprice",tmp_maxprice);
                    data.put("time",tmp_time);
                    JSONArray list = object.getJSONArray("list");
                    ArrayList<String> tmp_station = new ArrayList<>();
                    for(int i=0;i<list.length();i++){
                        JSONObject tmp_object = list.getJSONObject(i);
                        tmp_station.add(tmp_object.getString("station"));
                    }
                    stations = new String[tmp_station.size()];
                    tmp_station.toArray(stations);
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
