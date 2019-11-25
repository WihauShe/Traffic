package cn.cslg.traffic.fragment;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.*;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.*;
import cn.cslg.traffic.module.ChooseCityActivity;
import cn.cslg.traffic.R;
import cn.cslg.traffic.module.RouteActivity;
import cn.cslg.traffic.module.StationActivity;

import static android.content.Context.BIND_AUTO_CREATE;

public class Home extends Fragment {
    private View view;
    private LinearLayout homePage;
    private int flag=0,sd;
    private EditText city,station,route;
    private Button station_btn,route_btn;
    private String cityName;
    private ContentResolver resolver;
    private SoundPool soundPool;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_layout,container,false);
        resolver = getActivity().getContentResolver();
        soundPool = new SoundPool(1, AudioManager.STREAM_SYSTEM,0);
        sd = soundPool.load(getContext(),R.raw.sound1,1);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        findViews();
        setListeners();
        init();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    private void findViews(){
        homePage = view.findViewById(R.id.home_page);
        city = view.findViewById(R.id.city);
        station = view.findViewById(R.id.station);
        route = view.findViewById(R.id.route);
        station_btn = view.findViewById(R.id.station_btn);
        route_btn = view.findViewById(R.id.route_btn);
    }
    private void setListeners(){
        city.setFocusable(false);
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChooseCityActivity.class);
                startActivityForResult(intent,0);
            }
        });
        station.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
        station_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(sd,1,1,1,0,1f);
                String tmp_station = station.getText().toString();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logInfo",Context.MODE_PRIVATE);
                boolean isLogin  = sharedPreferences.getBoolean("isLogin",false);
                if(isLogin){
                    int userId = sharedPreferences.getInt("userId",0);
                    ContentValues values = new ContentValues();
                    values.put("type",1);
                    values.put("city",cityName);
                    values.put("station",tmp_station);
                    values.put("user_id",userId);
                    Uri uri = Uri.parse("content://cn.cslg.traffic.dao.MyProvider/history");
                    resolver.insert(uri,values);
                }
                Intent intent = new Intent(getActivity(), StationActivity.class);
                intent.putExtra("city",cityName);
                intent.putExtra("station",tmp_station);
                startActivity(intent);
                station.setText("");
            }
        });

        route_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(sd,1,1,1,0,1f);
                String tmp_route = route.getText().toString();
                if(tmp_route.equals(""))
                    Toast.makeText(getContext(),"未知路线",Toast.LENGTH_SHORT).show();
                else {
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logInfo", Context.MODE_PRIVATE);
                    boolean isLogin = sharedPreferences.getBoolean("isLogin", false);
                    if (isLogin) {
                        int userId = sharedPreferences.getInt("userId", 0);
                        ContentValues values = new ContentValues();
                        values.put("type", 2);
                        values.put("city", cityName);
                        values.put("route", tmp_route);
                        values.put("user_id", userId);
                        Uri uri = Uri.parse("content://cn.cslg.traffic.dao.MyProvider/history");
                        resolver.insert(uri, values);
                    }
                    Intent intent = new Intent(getActivity(), RouteActivity.class);
                    intent.putExtra("city", cityName);
                    intent.putExtra("route", tmp_route);
                    startActivity(intent);
                    route.setText("");
                }
            }
        });
    }
    private void init(){
        SharedPreferences sp = getActivity().getSharedPreferences( "initCity",Context.MODE_PRIVATE);
        boolean isFirst = sp.getBoolean("isFirst",true);
        if(!isFirst){
            String tmp_city = sp.getString("lastCity","");
            city.setText(tmp_city);
            cityName = tmp_city;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.back_menu){
            switch (flag){
                case 0: homePage.setBackground(getResources().getDrawable(R.drawable.back1,null));break;
                case 1: homePage.setBackground(getResources().getDrawable(R.drawable.back2,null));break;
                case 2: homePage.setBackground(getResources().getDrawable(R.drawable.back3,null));break;
            }
            if(flag==2)
                flag = 0;
            else
                flag++;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0&&resultCode==1){
            String tmp_city = data.getStringExtra("city");
            cityName = tmp_city;
            city.setText(tmp_city);
            SharedPreferences sp = getActivity().getSharedPreferences( "initCity",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("lastCity",cityName);
            editor.apply();
        }
    }
}
