package com.example.kingofvocabulary;

import android.content.Context;
import android.media.MediaSync;
import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class imgURL {

    static String output = "輸出為空";
    OkHttpClient client = new OkHttpClient();



    public static void main(String[] args) {



    }
    public static String parseJson(String jsonString) throws JSONException
    {
      Gson gson = new Gson();
      JSONObject jsonObject = gson.fromJson(jsonString,JSONObject.class);
        String value = jsonObject.getJSONArray("value").toString();
        System.out.println("value="+value);

        return  value;
    }
    static String url;
    public static String sendQuest(String search)
    {

        System.out.println("搜尋圖片中...");
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://bing-image-search1.p.rapidapi.com/images/search?q="+search)
                .get()
                .addHeader("X-RapidAPI-Key", "23029f78f0mshdd6ad9ded18f896p176974jsn4445126674ff")
                .addHeader("X-RapidAPI-Host", "bing-image-search1.p.rapidapi.com")
                .build();

        //OkHttpClient client = new OkHttpClient();
//
        //Request request = new Request.Builder()
        //        .url("https://contextualwebsearch-websearch-v1.p.rapidapi.com/api/Search/ImageSearchAPI?q=" + search + "&pageNumber=1&pageSize=1&autoCorrect=true")
        //        .get()
        //        .addHeader("X-RapidAPI-Key", "f165b7871amsha687f4a243bd2f6p1146d9jsnaa4cb4499a75")
        //        .addHeader("X-RapidAPI-Host", "contextualwebsearch-websearch-v1.p.rapidapi.com")
        //        .build();

        System.out.println("完成圖片請求...");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Response response = client.newCall(request).execute();
                    boolean out = false;

                        System.out.println("取得圖片網址...");
                        String value = response.body().string().toString();
                        value = value.split("thumbnailUrl")[1].split("datePublished")[0];
                        value = value.substring(4,value.length()-4);
                        System.out.println(value);
                        url = value;

                        VL_RecyclerViewAdapter.imgUrlMap.put(search, url);
                        VL_RecyclerViewAdapter.imgUrlMap.put(MainActivity.getEnglishInput(), url);
                        upLoadFirebaseImage(search);
                        upLoadFirebaseImage(MainActivity.getEnglishInput());


/*
                        try {
                            parseJson(value);
                        }catch (Exception e)
                        {
                            e.printStackTrace();
                        }

 */
                        //String res = response.body().string().split("value")[1].split(",")[0].split("url")[1].substring(3);
                        //String res = response.body().string().split(",")[5].substring(13);
                        //res = res.substring(0,res.length() - 1);
                        //System.out.println(res);
                        //output = res;
                        out = true;


                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

            }
        }).start();


        return output;


    }
    static void upLoadFirebaseImage(String search)
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://kingofvocabulary-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference databaseReference = firebaseDatabase.getReference("/img/img_"+search);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //System.out.println("Upload Image Input :" + search);
                databaseReference.setValue(url);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}

