package com.example.kingofvocabulary;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;

import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

public class Data {
    private static ArrayList<LinkedHashMap<String, String>> data;
    private static Set<String> dataSet = new HashSet<>();
    public static ArrayList<String> showData = new ArrayList<>();

    public Data() {

    }

    public Data(ArrayList<LinkedHashMap<String, String>> dataIn) {
        data = dataIn;
    }

    public static ArrayList<LinkedHashMap<String, String>> getData()
    {
        return data;
    }
    public static Set<String> getDataSet()
    {
        return dataSet;
    }
    public static void setData(ArrayList<LinkedHashMap<String, String>> dataIn)
    {
       data = dataIn;
    }
    public static void setDataSet(Set<String> dataIn)
    {
       dataSet = dataIn;
    }
    public static void setShowData(ArrayList<String> dataIn)
    {
       showData = dataIn;
    }


    public static ArrayList<LinkedHashMap<String,String>> transData(String data)
    {
        ArrayList<LinkedHashMap<String, String>> temp = new ArrayList<>();
        Set<String> tempSet = new HashSet<>();
        data = data.substring(2,data.length()-2);

        String[] aryData = data.split("\\}, \\{");

        //System.out.println(data);
        String[] aryDataEn = new String[aryData.length];//英文陣列
        String[] aryDataCh = new String[aryData.length];//中文陣列
        ArrayList<String> englishList = new ArrayList<>();//英文串列
        ArrayList<String> chineseList = new ArrayList<>();//中文串列

        /**
         * 依中英分類放入陣列
         */
        for(int i = 0; i < aryData.length; i ++)
        {
           // System.out.println(aryData[i]);
        }


         for(int i = aryData.length - 1; i >= 0; i --)
         {
             aryDataEn[i] = aryData[i].split(", ")[0].split("=")[1];
            // aryDataEn[i] = aryDataEn[i].substring(1,aryDataEn[i].length() - 1);
             aryDataCh[i] = aryData[i].split(", ")[1].split("=")[1];
            // aryDataCh[i] = aryDataCh[i].substring(1,aryDataCh[i].length() - 1);
             temp.add(new LinkedHashMap<String,String>());
             englishList.add("");
             chineseList.add("");tempSet.add(aryDataEn[i]);
             //System.out.println(  aryDataEn[i] + " " + aryDataCh[i]);
         }
         for(int i = 0; i <  temp.size(); i ++)
         {

             temp.get(i).put("en",aryDataEn[i]);
             temp.get(i).put("ch",aryDataCh[i]);
             englishList.add(i,aryDataEn[i]);
             chineseList.add(i,aryDataCh[i]);
             showData.add(aryDataEn[i] + " " + aryDataCh[i]);
         }
         System.out.println(temp);

            return temp;
    }

}
