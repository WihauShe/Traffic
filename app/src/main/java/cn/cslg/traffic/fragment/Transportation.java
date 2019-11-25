package cn.cslg.traffic.fragment;

import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.*;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.cslg.traffic.R;
import cn.cslg.traffic.module.TransferActivity;

public class Transportation extends Fragment {
    private View view;
    private LinearLayout transpotationPage;
    private int flag=0;
    private ContentResolver resolver;
    private EditText transferStart,transferEnd;
    private Button transferBtn;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.transpotation_layout,container,false);
        resolver = getActivity().getContentResolver();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        findViews();
        setListeners();
    }

    private void findViews(){
        transpotationPage = view.findViewById(R.id.transpotation_page);
        transferStart = view.findViewById(R.id.transfer_start);
        transferEnd = view.findViewById(R.id.transfer_end);
        transferBtn = view.findViewById(R.id.transfer_btn);
    }

    private void setListeners(){
        transferStart.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
        transferEnd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    return true;
                return false;
            }
        });
        transferBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText city = getActivity().findViewById(R.id.city);
                String cityName = city.getText().toString();
                String tmpStart = transferStart.getText().toString();
                String tmpEnd = transferEnd.getText().toString();
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("logInfo", Context.MODE_PRIVATE);
                boolean isLogin  = sharedPreferences.getBoolean("isLogin",false);
                if(isLogin){
                    int userId = sharedPreferences.getInt("userId",0);
                    ContentValues values = new ContentValues();
                    values.put("type",3);
                    values.put("city",cityName);
                    values.put("start_station",tmpStart);
                    values.put("end_station",tmpEnd);
                    values.put("user_id",userId);
                    Uri uri = Uri.parse("content://cn.cslg.traffic.dao.MyProvider/history");
                    resolver.insert(uri,values);
                }
                Intent intent = new Intent(getActivity(), TransferActivity.class);
                intent.putExtra("city",cityName);
                intent.putExtra("start",tmpStart);
                intent.putExtra("end",tmpEnd);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.back_menu){
            switch (flag){
                case 0: transpotationPage.setBackground(getResources().getDrawable(R.drawable.back4,null));break;
                case 1: transpotationPage.setBackground(getResources().getDrawable(R.drawable.back3,null));break;
                case 2: transpotationPage.setBackground(getResources().getDrawable(R.drawable.back2,null));break;
            }
            if(flag==2)
                flag = 0;
            else
                flag++;
        }
        return super.onOptionsItemSelected(item);
    }
}
