package cn.cslg.traffic.fragment;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.cslg.traffic.R;
import cn.cslg.traffic.module.RegisterActivity;
import cn.cslg.traffic.module.DBData;
import cn.cslg.traffic.module.LoginActivity;

import java.util.Date;

public class Person extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private View view;
    private LinearLayout personPage;
    private int flag=0;
    private LinearLayout personLogin,personInfo;
    private Button loginBtn,registerBtn,logoffBtn;
    private ImageView userImg;
    private TextView userAccount,userAge,userSex,userEmail;
    private int userId;
    private boolean isFirst=true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.person_layout,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        findViews();
        init();
        setListners();
    }

    private void findViews(){
        personPage = view.findViewById(R.id.person_page);
        personLogin = view.findViewById(R.id.person_login);
        personInfo = view.findViewById(R.id.person_info);
        loginBtn = view.findViewById(R.id.show_login);
        registerBtn = view.findViewById(R.id.show_register);
        logoffBtn = view.findViewById(R.id.logoff_btn);
        userImg = view.findViewById(R.id.user_img);
        userAccount = view.findViewById(R.id.user_account);
        userAge = view.findViewById(R.id.user_age);
        userSex = view.findViewById(R.id.user_sex);
        userEmail = view.findViewById(R.id.user_email);
    }
    private void init(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logInfo", Context.MODE_PRIVATE);
        long lastLogin  = sharedPreferences.getLong("lastLogin",0);
        Date date = new Date();
        int days = (int) ((date.getTime() - lastLogin) / (1000*3600*24));
        boolean isLogin = sharedPreferences.getBoolean("isLogin",false);
        if(lastLogin!=0&&days<7&&isLogin) {
            setData();
        }else {
            personInfo.setVisibility(View.GONE);
        }
    }
    private void setListners(){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent,0);
            }
        });
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RegisterActivity.class);
                startActivity(intent);
            }
        });
        logoffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("isLogin",false);
                editor.apply();
                personLogin.setVisibility(View.VISIBLE);
                personInfo.setVisibility(View.GONE);
                TextView historyUser = getActivity().findViewById(R.id.history_user);
                ListView historyList = getActivity().findViewById(R.id.history_list);
                historyUser.setVisibility(View.VISIBLE);
                historyList.setVisibility(View.GONE);
            }
        });
    }
    private void setData(){
        personLogin.setVisibility(View.GONE);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logInfo", Context.MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId",0);
        if(isFirst){
            getLoaderManager().initLoader(2,null,this);
            isFirst = false;
        }else
            getLoaderManager().restartLoader(2,null,this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.back_menu){
            switch (flag){
                case 0: personPage.setBackground(getResources().getDrawable(R.drawable.back5,null));break;
                case 1: personPage.setBackground(getResources().getDrawable(R.drawable.back4,null));break;
                case 2: personPage.setBackground(getResources().getDrawable(R.drawable.back3,null));break;
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
        if(requestCode==0&&resultCode==0){
            setData();
            ViewPager viewPager = getActivity().findViewById(R.id.vp);
            viewPager.setCurrentItem(0);
        }
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        Uri uri = Uri.parse("content://cn.cslg.traffic.dao.MyProvider/user");
        return new CursorLoader(getContext(),uri,null,"_id = ?",new String[]{Integer.toString(userId)},null);
    }

    @Override
    public void onLoadFinished(@NonNull android.support.v4.content.Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        userImg.setImageResource(DBData.imgs[cursor.getInt(cursor.getColumnIndex("headimg"))]);
        userAccount.setText(cursor.getString(cursor.getColumnIndex("account")));
        userAge.setText(cursor.getString(cursor.getColumnIndex("age")));
        userSex.setText(cursor.getString(cursor.getColumnIndex("sex")));
        userEmail.setText(cursor.getString(cursor.getColumnIndex("email")));
    }

    @Override
    public void onLoaderReset(@NonNull android.support.v4.content.Loader<Cursor> loader) {

    }

}
