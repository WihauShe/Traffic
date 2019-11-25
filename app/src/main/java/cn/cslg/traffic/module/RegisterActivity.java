package cn.cslg.traffic.module;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import cn.cslg.traffic.R;


public class RegisterActivity extends Activity {
    private ContentResolver resolver;
    private Spinner images,sex;
    private boolean isInitial=true;
    private ImageView headimg;
    private EditText account,password,age,email;
    private Button cancel,save,reset;
    private boolean flag=false;
    private int userId;
    private int imgId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        resolver = getContentResolver();
        findViews();
        setListeners();
        //dbDao = new DBDao(this);

        //flag = intent.getBooleanExtra("update",false);
        //judge if it is updating

    }
    private void findViews(){
        images = findViewById(R.id.images);
        headimg = findViewById(R.id.headimg);
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);
        age = findViewById(R.id.age);
        sex = findViewById(R.id.sex);
        email = findViewById(R.id.email);
        cancel = findViewById(R.id.cancel);
        save = findViewById(R.id.save);
        reset = findViewById(R.id.reset);
    }
    private void setListeners(){
        //bind the spinner with listener
        images.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(isInitial){
                    isInitial = false;
                    return;
                }
                headimg.setImageResource(DBData.imgs[position]);
                imgId = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                imgId = 0;
            }
        });
        email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
        //cancel button
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //save button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useraccount = account.getText().toString();
                String userpass = password.getText().toString();
                int userage = Integer.parseInt(age.getText().toString());
                String usersex = sex.getSelectedItem().toString();
                String useremail = email.getText().toString();
                Uri uri = Uri.parse("content://cn.cslg.traffic.dao.MyProvider/user");
                ContentValues values = new ContentValues();
                values.put("headimg",imgId);
                values.put("account",useraccount);
                values.put("password",userpass);
                values.put("age",userage);
                values.put("sex",usersex);
                values.put("email",useremail);
                resolver.insert(uri,values);
                Toast.makeText(RegisterActivity.this,"注册成功",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        //reset button
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account.setText("");
                password.setText("");
                age.setText("");
                sex.setSelection(0);
                email.setText("");
            }
        });
    }

}
