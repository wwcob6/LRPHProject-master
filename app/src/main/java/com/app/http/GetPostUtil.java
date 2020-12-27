package com.app.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 林逸磊 on 2017/9/26.
 */

public class GetPostUtil {
    public static String sendGet1111(String url, String params) {
        String result = "";

        String urlName = url + "?" + params;
        try {
            URL realUrl = new URL(urlName);
            HttpURLConnection conn =(HttpURLConnection) realUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0(compatible;MSIE 6.0;Windows NT 5.1;SV1)");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();
            InputStream is = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "utf-8");
            BufferedReader in = new BufferedReader(isr);
            String line;
            while ((line = in.readLine()) != null) {
                result = line+"\n";
            }
            in.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }
}
