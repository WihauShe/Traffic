package cn.cslg.traffic.module;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.List;
import java.util.Map;

public class TransferActivity extends AppCompatActivity {
    private ListView transferList;
    private List<Map<String,String>> transfers;
    private List<Map<String,String[]>>allSteps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        findViews();
        setListeners();
        init();
    }

    private void findViews(){
        transferList = findViewById(R.id.transfer_list);
    }
    private void setListeners(){
        transferList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map map = allSteps.get(position);
                String[] steps = (String[])map.get("steps");
                Intent intent = new Intent(TransferActivity.this, ShowStepActivity.class);
                intent.putExtra("steps",steps);
                startActivity(intent);
            }
        });
    }
    private void init(){
        transfers = new ArrayList<>();
        allSteps  = new ArrayList<>();
        Intent intent = getIntent();
        String city = intent.getStringExtra("city");
        String start  = intent.getStringExtra("start");
        String end = intent.getStringExtra("end");
        TransferTask transferTask = new TransferTask();
        transferTask.execute(city,start,end);
    }
    class TransferTask extends AsyncTask{
        @Override
        protected Object doInBackground(Object[] params) {
            String path = "https://api.jisuapi.com/transit/station2s?appkey=6295111744d2b012";
            path += "&city="+params[0]+"&start="+params[1]+"&end="+params[2];
            Connection connection  = new Connection();
            return connection.getDate("GET",path);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            String msg = "";
            if(o!=null)
                msg = parseJson(o);
            if(!msg.equals("ok"))
                Toast.makeText(TransferActivity.this,msg,Toast.LENGTH_SHORT).show();
            transferList.setAdapter(new SimpleAdapter(TransferActivity.this,transfers,R.layout.transfer_list_item,
                    new String[]{"totaltime","totaldistance","vehicles"},
                    new int[]{R.id.transfer_item_time,R.id.transfer_item_distance,R.id.transfer_item_vehicles}));
        }

        private String parseJson(Object o){
            String result= o.toString();
            try {
                JSONObject jsonObject=new JSONObject(result);
                int status=jsonObject.getInt("status");
                if(status==0){
                    JSONArray tmp_list=jsonObject.getJSONArray("result");
                    for(int i=0;i<tmp_list.length();i++){
                        JSONObject object = tmp_list.getJSONObject(i);
                        String totalTime = object.getString("totalduration");
                        String totalDistance = object.getString("totaldistance");
                        JSONArray tmpVehicle = object.getJSONArray("vehicles");
                        String vehicles = "";
                        for(int j=0;j<tmpVehicle.length();j++){
                            vehicles += tmpVehicle.getString(j);
                            if(j < tmpVehicle.length()-1)
                                vehicles += ">";
                        }
                        JSONArray tmpSteps = object.getJSONArray("steps");
                        String[] step = new String[tmpSteps.length()];
                        for(int k=0;k<tmpSteps.length();k++){
                            JSONObject tmpStep = tmpSteps.getJSONObject(k);
                            step[k] = tmpStep.getString("steptext");
                            if(tmpStep.has("endname")){
                                String endName = tmpStep.getString("endname");
                                if(!endName.equals("null"))
                                    step[k] += "至"+endName;
                                else
                                    step[k] += "到达终点";
                            }
                        }
                        HashMap<String,String> map1 = new HashMap<>();
                        map1.put("totaltime",totalTime);
                        map1.put("totaldistance",totalDistance);
                        map1.put("vehicles",vehicles);
                        HashMap<String,String[]> map2 = new HashMap<>();
                        map2.put("steps",step);
                        transfers.add(map1);
                        allSteps.add(map2);
                    }

                    return "ok";
                }else{
                    String msg=jsonObject.getString("msg");
                    return msg;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return  "ERROR";
        }
    }
}
