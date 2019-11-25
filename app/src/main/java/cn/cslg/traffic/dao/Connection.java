package cn.cslg.traffic.dao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Connection {
    public Object getDate(String type,String path){
        try {
            URL url=new URL(path);
            HttpURLConnection conn= (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(type);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();
            int code=conn.getResponseCode();
            if(code==200){
                InputStream in=conn.getInputStream();
                BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                String str=reader.readLine();
                StringBuilder result=new StringBuilder();
                while (str!=null){
                    result.append(str);
                    str=reader.readLine();
                }
                in.close();
                conn.disconnect();
                return result;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
