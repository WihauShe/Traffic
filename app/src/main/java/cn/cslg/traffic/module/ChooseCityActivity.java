package cn.cslg.traffic.module;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.cslg.traffic.R;
import cn.cslg.traffic.dao.Connection;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChooseCityActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    private AutoCompleteTextView cityList;
    private ContentResolver resolver;
    private CursorAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);
        findViews();
        setListeners();
        init();
    }

    private void findViews(){
        cityList = findViewById(R.id.city_input);
    }
    private void setListeners(){
        cityList.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    Intent data = getIntent();
                    data.putExtra("city",cityList.getText().toString());
                    setResult(1,data);
                    finish();
                }
                return true;
            }
        });
    }
    private void init(){
        resolver = getContentResolver();
        SharedPreferences sharedPreferences = getSharedPreferences("initCity",MODE_PRIVATE);
        boolean isFirst = sharedPreferences.getBoolean("isFirst",true);
        if(isFirst){
            CityTask cityTask = new CityTask();
            cityTask.execute();
        }
        adapter = new CursorAdapter(this,null,0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.city_list_item,null);
                ViewHolder holder = new ViewHolder();
                holder.name = view.findViewById(R.id.city_item);
                view.setTag(holder);
                return view;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                ViewHolder holder = (ViewHolder)view.getTag();
                String name = cursor.getString(cursor.getColumnIndex("name"));
                holder.name.setText(name);
            }

            @Override
            public CharSequence convertToString(Cursor cursor) {
                return cursor.getString(cursor.getColumnIndex("name"));
            }

            @Override
            public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
                getLoaderManager().restartLoader(0,null,ChooseCityActivity.this);
                return null;
            }

            final class ViewHolder{
                TextView name;
            }
        };
        cityList.setAdapter(adapter);
        getLoaderManager().initLoader(0,null,this);
    }

    class CityTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            String path="https://api.jisuapi.com/transit/city?appkey=6295111744d2b012";
            Connection connection  = new Connection();
            return connection.getDate("GET",path);
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            String msg = parseJson(o);
            if(!msg.equals("ok"))
                Toast.makeText(ChooseCityActivity.this,msg,Toast.LENGTH_SHORT).show();
            else{
                SharedPreferences sharedPreferences = getSharedPreferences("initCity",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isFirst",false);
                editor.apply();
            }
        }
        private String parseJson(Object o){
            String result= o.toString();
            try {
                JSONObject jsonObject=new JSONObject(result);
                int status=jsonObject.getInt("status");
                if(status==0){
                    JSONArray list=jsonObject.getJSONArray("result");
                    for(int i=0;i<list.length();i++){
                        JSONObject object = (JSONObject) list.get(i);
                        String cityid = object.getString("cityid");
                        String name = object.getString("name");
                        String code = object.getString("code");
                        ContentValues values = new ContentValues();
                        values.put("_id",cityid);
                        values.put("name",name);
                        values.put("code",code);
                        Uri uri = Uri.parse("content://cn.cslg.traffic.dao.MyProvider/city");
                        resolver.insert(uri,values);
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {"_id","name"};
        Uri uri = Uri.parse("content://cn.cslg.traffic.dao.MyProvider/city");
        String constraint = cityList.getText().toString();
        String selection = "name like '" + constraint +"%'";
        CursorLoader cursorLoader = new CursorLoader(this,uri,projection,selection,null,null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
