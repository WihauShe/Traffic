package cn.cslg.traffic.module;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.cslg.traffic.R;

import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    private ContentResolver resolver;
    private EditText userName,userPass;
    private Button loginBtn,cancelBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        resolver = getContentResolver();
        userName = findViewById(R.id.username);
        userPass = findViewById(R.id.userpass);
        loginBtn = findViewById(R.id.login_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        setListeners();
    }

    private void setListeners(){
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://cn.cslg.traffic.dao.MyProvider/user");
                String[] projection = new String[]{"_id","account","password"};
                String account = userName.getText().toString();
                String pass = userPass.getText().toString();
                String password = "";
                Cursor cursor = resolver.query(uri,projection,"account=?",new String[]{account},null);
                if(cursor.moveToFirst())
                    password = cursor.getString(cursor.getColumnIndex("password"));
                if(pass.equals(password)){
                    SharedPreferences sharedPreferences = getSharedPreferences("logInfo",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("isLogin",true);
                    editor.putInt("userId",cursor.getInt(cursor.getColumnIndex("_id")));
                    Date date = new Date();
                    editor.putLong("lastLogin",date.getTime());
                    editor.apply();
                    Intent intent = getIntent();
                    setResult(0,intent);
                    finish();
                }else
                    Toast.makeText(LoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                setResult(1,intent);
                finish();
            }
        });
    }
}
