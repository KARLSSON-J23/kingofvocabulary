package com.example.kingofvocabulary;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AIP_process {
    String url, header1, header2, value1,value2, output, search,type2Header1, headerFront, headerBack;
    public AIP_process(String url, String header1, String value1, String header2, String value2)
    {
        this.url = url;
        this.header1 = header1;
        this.header2 = header2;
        this.value1 = value1;
        this.value2 = value2;

    }
    public AIP_process(String url, String headerFront, String search, String headerBack, String value1, String header2, String value2)
    {
        this.url = url;
        this.header2 = header2;
        this.value1 = value1;
        this.value2 = value2;
        this.search = search;
        this.headerFront = headerFront;
        this.headerBack = headerBack;

    }
    String changeType(String str)
    {
        return  str;
    }
    String temp;
    String getValue(String value)
    {
        return null;
    }
    String processJson (String value)
    {
        String ans;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String ans = "";
                Gson gson = new Gson();

                JsonObject jsonObject = gson.fromJson(output,JsonObject.class);
                ans = jsonObject.get(value).getAsJsonArray().get(0).toString();
                ans = ans.split(",")[5];
                temp = ans;
            }
        }).start();
        ans = temp;
        temp ="";
        if(ans == null)
            ans = processJson (value);
        return ans;
    }
    String sendQuest()
    {
        output = "";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader(header1,value1)
                .addHeader(header2,value2)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    String res = response.body().string().toString();
                    //System.out.println(output);
                    output = res;

                    System.out.println("回傳" + output);

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }).start();
        System.out.println("完成請求");
        return output;
    }
    String sendQuest(String search)
    {
        output = "";
        OkHttpClient client = new OkHttpClient();
        this.search = search;
        type2Header1 = headerFront + search + headerBack;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader(type2Header1,value1)
                .addHeader(header2,value2)
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    String res = response.body().string().toString();
                    //System.out.println(output);
                    output = res;

                    System.out.println("回傳" + output);

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }).start();
        System.out.println("完成請求");
        return output;
    }
}
