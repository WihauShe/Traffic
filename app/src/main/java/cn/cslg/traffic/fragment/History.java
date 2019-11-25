package cn.cslg.traffic.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.cslg.traffic.R;
import cn.cslg.traffic.module.RouteActivity;
import cn.cslg.traffic.module.StationActivity;
import cn.cslg.traffic.module.TransferActivity;


public class History extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private View view;
    private LinearLayout historyPage;
    private int flag=0;
    private ContentResolver resolver;
    private int userId=0;
    private TextView historyUser;
    private ListView historyList;
    private SimpleCursorAdapter adapter;
    private int pos;
    private boolean isFirst=true;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.history_layout,container,false);
        resolver = getActivity().getContentResolver();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        findViews();
        initData();
        setListeners();
    }

    private void findViews(){
        historyPage = view.findViewById(R.id.history_page);
        historyUser = view.findViewById(R.id.history_user);
        historyList = view.findViewById(R.id.history_list);
    }
    private void initData(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logInfo", Context.MODE_PRIVATE);
        boolean isLogin  = sharedPreferences.getBoolean("isLogin",false);
        if(isLogin) {
            historyUser.setVisibility(View.GONE);
            userId = sharedPreferences.getInt("userId", 0);
            if(isFirst){
                getLoaderManager().initLoader(1,null,this);
                isFirst = false;
            }else
                getLoaderManager().restartLoader(1,null,this);

        }

    }
    private void setListeners(){
        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pos = position;
                showPopupMenu(historyList);
            }
        });
    }

    private void showPopupMenu(View v){
        PopupMenu popupMenu = new PopupMenu(getContext(),v);
        getActivity().getMenuInflater().inflate(R.menu.history,popupMenu.getMenu());
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Uri uri = Uri.parse("content://cn.cslg.traffic.dao.MyProvider/history");
                String selection = "user_id = "+userId;
                Cursor cursor = resolver.query(uri,null,selection,null,null);
                cursor.moveToPosition(pos);
                int id = cursor.getInt(0);
                switch (item.getItemId()){
                    case R.id.selecct_menu:
                        int type = cursor.getInt(1);
                        switch (type){
                            case 1:
                                Intent intent1 = new Intent(getActivity(), StationActivity.class);
                                intent1.putExtra("city",cursor.getString(2));
                                intent1.putExtra("station",cursor.getString(3));
                                startActivity(intent1);
                                break;
                            case 2:
                                Intent intent2 = new Intent(getActivity(), RouteActivity.class);
                                intent2.putExtra("city",cursor.getString(2));
                                intent2.putExtra("route",cursor.getString(4));
                                startActivity(intent2);
                                break;
                            case 3:
                                Intent intent3 = new Intent(getActivity(), TransferActivity.class);
                                intent3.putExtra("city",cursor.getString(2));
                                intent3.putExtra("start",cursor.getString(5));
                                intent3.putExtra("end",cursor.getString(6));
                                startActivity(intent3);
                                break;
                            default:break;
                        }
                        break;
                    case R.id.delete_menu:
                        resolver.delete(uri,"_id = ?",new String[]{Integer.toString(id)});
                        break;
                    case R.id.clear_menu:
                        resolver.delete(uri,"user_id = ?",new String[]{Integer.toString(userId)});
                        break;
                }
                cursor.close();
                return false;
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.back_menu){
            switch (flag){
                case 0: historyPage.setBackground(getResources().getDrawable(R.drawable.back4,null));break;
                case 1: historyPage.setBackground(getResources().getDrawable(R.drawable.back5,null));break;
                case 2: historyPage.setBackground(getResources().getDrawable(R.drawable.back6,null));break;
            }
            if(flag==2)
                flag = 0;
            else
                flag++;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri uri = Uri.parse("content://cn.cslg.traffic.dao.MyProvider/history");
        String selection = "user_id = "+userId;
        return new CursorLoader(getContext(),uri,null,selection,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        adapter = new SimpleCursorAdapter(getContext(),R.layout.history_list_item,data,
                new String[]{"type","city","station","route","start_station","end_station"},
                new int[]{R.id.history_item_type,R.id.history_item_city,R.id.history_item_station,R.id.history_item_route,
                R.id.history_item_start,R.id.history_item_end},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                switch(cursor.getInt(columnIndex)){
                    case 1:((TextView)view).setText("站点 ");return true;
                    case 2:((TextView)view).setText("路线 ");return true;
                    case 3:((TextView)view).setText("换乘 ");return true;
                }
                if(cursor.getInt(cursor.getColumnIndex("type"))==3&&cursor.getColumnName(columnIndex).equals("start_station")) {
                    ((TextView) view).setText(cursor.getString(columnIndex) + ">>");
                    return true;
                }
                return false;
            }
        });
        historyList.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
