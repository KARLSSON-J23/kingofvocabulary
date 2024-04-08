package com.example.kingofvocabulary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.AndroidException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {


    static  ArrayList<LinkedHashMap<String,String>> word_hashMapInAL = new ArrayList<>();
    /***
     *  word_hashMapInAL --> 主要資料存放
     *
     *  格式
     *  ArrayList<LinkedHashMap <String, String>>
     *      "en" -> 英文單字 ex: apple
     *      "ch" -> 中文意思 ex: 蘋果
     *
     */

    Set<String> wordSet = new HashSet<>();
    /**
     * wordSet 確認是否有重複單字用
     *
     * if(wordSet.add("要加入的單字"))
     *
     * if裡的式子成立的話(此單字還未被加入)就會執行
     */
    VL_RecyclerViewAdapter recyclerViewAdapter = new VL_RecyclerViewAdapter(word_hashMapInAL);

    int count;//項目計數，不建議用此變數，建議使用 word_hashMapInAL.size()
    static int countExam = 0;
    static boolean re_save = false;//判斷是否需重新啟動程式
    /**
     * 功能按鈕宣告區，如登入登出等按鈕
     */
    private Button btn_layout_dictionary, btn_layout_list, btn_layout_exam, btn_translate,
                    btn_addWordList, btn_switchLanguage,btn_setting, btn_addList, btn_login,
                    btn_upload, btn_startExam, btn_enCountExam, btn_deCountExam,btn_TTS;

    private LinearLayout layout_dictionary,layout_list, layout_exam, layout_main;
    /**
     * 主頁三畫面，字典單字表、測驗
     */
    public static TextView translate_output,translate_input, textView_languageFrom, textView_languageTo, textView_User, textView_countExam;
    /**
     * TextView 宣告區
     */
    private EditText editText_search;//翻譯搜尋框
    private RecyclerView recyclerView;//單字表recyclerView 宣告
    private String String_Language_From = "en", String_Language_To = "ch",string_nowInputLanguage = "en";
    /**翻譯前後設定儲存值*/

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    /** FireBase 驗證宣告*/

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://kingofvocabulary-default-rtdb.asia-southeast1.firebasedatabase.app/");
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    /**Firebase 即時資料庫宣告(主頁暫未用到)*/
    private boolean isLogin;//判段是否以已登入

    //翻譯圖片
    private ImageView img_translate;

    //文字轉語音
    static  TextToSpeech textToSpeech ;

    @Override
    public void onStart()//頁面初始化執行
    {
        super.onStart();

        /*
        宣告Firebase 中的用戶驗證
         */
        FirebaseUser currentUser = mAuth.getCurrentUser();
        /**
         * 先判斷是否需重啟程式，
         */
        if(re_save) {
              saveData();
              re_save = false;

              Toast toast = Toast.makeText(MainActivity.this,"請重新啟動程式以載入存檔",Toast.LENGTH_SHORT);


              saveData();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            intent.putExtra("REBOOT","reboot");
            PendingIntent restartIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, restartIntent);
            android.os.Process.killProcess(android.os.Process.myPid());


             // Intent intent = new Intent();
             // intent.setClass(MainActivity.this, reload.class);
             // startActivity(intent);
             // finish();


        }
        /**
         * 判斷APP中是否已經有登入的帳號，如已經登入，便將isLogin的值設為True
         */
        if(currentUser != null)
        {
            currentUser.reload();
            System.out.println("已登入, Email: " + currentUser.getEmail());
            isLogin = true;
            reloadUser();


        }
        else
        {
            isLogin = false;
            reloadUser();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * 宣告UI物件
         */
        //頁面切換按鈕
        btn_layout_dictionary = findViewById(R.id.btn_dictionary_ID);
        btn_layout_list = findViewById(R.id.btn_list_ID);
        btn_layout_exam = findViewById(R.id.btn_exam_ID);
        btn_login = findViewById(R.id.btn_login_ID);

        //功能按鈕
        btn_translate = findViewById(R.id.btn_translate_ID);
        btn_addWordList = findViewById(R.id.btn_addWordToList_ID);
        btn_switchLanguage = findViewById(R.id.btn_switchLanguage_ID);
        btn_setting = findViewById(R.id.btn_setting_ID);
        btn_addList = findViewById(R.id.btn_addList_ID);
        btn_upload = findViewById(R.id.btn_upload_ID);
        btn_startExam = findViewById(R.id.btn_startExam_ID);
        btn_enCountExam = findViewById(R.id.btn_enCountExam_ID);
        btn_deCountExam = findViewById(R.id.btn_deCountExam_ID);
        btn_TTS = findViewById(R.id.btn_TTS_ID);

        //Layout
        layout_dictionary = findViewById(R.id.Layout_dictionary_ID);
        layout_list = findViewById(R.id.Layout_wordList_ID);
        layout_exam = findViewById(R.id.Layout_exam_ID);
        layout_main = findViewById(R.id.Layout_main_ID);

        //recyclerView
        recyclerView = findViewById(R.id.RecyclerView_wordList_ID);

        //TextView
        translate_output = findViewById(R.id.textView_translateOutput);
        translate_input = findViewById(R.id.text_inputTranslate_ID);
        textView_languageFrom = findViewById(R.id.textView_LanguageFrom_ID);
        textView_languageTo = findViewById(R.id.textViewLanguageTo_ID);
        textView_User = findViewById(R.id.textView_User_ID);
        textView_countExam = findViewById(R.id.textView_countExam_ID);

        //Edit
        editText_search =findViewById(R.id.edit_translateInput);

        //ImageView
        img_translate = findViewById(R.id.img_translate_ID);

        /***
         * 設定按鈕監聽器
         */
        //頁面及登入按鈕
        btn_layout_dictionary.setOnClickListener(btn_LayoutListener);
        btn_layout_list.setOnClickListener(btn_LayoutListener);
        btn_layout_exam.setOnClickListener(btn_LayoutListener);
        btn_login.setOnClickListener(btn_LayoutListener);

        //各功能按鈕
        btn_translate.setOnClickListener(btn_Function_Listener);
        btn_switchLanguage.setOnClickListener(btn_Function_Listener);
        btn_addWordList.setOnClickListener(btn_Function_Listener);
        btn_setting.setOnClickListener(btn_Function_Listener);
        btn_addList.setOnClickListener(btn_Function_Listener);
        btn_upload.setOnClickListener(btn_Function_Listener);
        btn_startExam.setOnClickListener(btn_Function_Listener);
        btn_enCountExam.setOnClickListener(btn_Function_Listener);
        btn_deCountExam.setOnClickListener(btn_Function_Listener);
        btn_TTS.setOnClickListener(btn_Function_Listener);




        //翻譯輸入輸出長按監聽器
        translate_input.setOnLongClickListener(onLongClickListener);
        translate_output.setOnLongClickListener(onLongClickListener);
        //initializationScreen();

        //判斷是否首次使用，並載入本機存檔
        setUpListModels();
        setTitle(getText(R.string.BTN_dictionary));

        //recycleView Adapter設定
        recyclerView.setAdapter(recyclerViewAdapter);

        //itemTouchHelper 監聽器宣告
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);




        //設定目前資料
        Data.setData(word_hashMapInAL);
        Data.setDataSet(wordSet);

        //初始化文字轉語音
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(i == TextToSpeech.SUCCESS)//初始化成功
                {
                    //語音設定為英語
                     textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        //提升recycleView 繪圖效率
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


    }
    LinkedHashMap<String,String> deleteVocabulary = null;
    String deleteSet = null;



    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        /**
         * 處理項目滑動
         * @param viewHolder
         * @param direction
         */

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction)
            {
                //當項目被左滑
                case ItemTouchHelper.LEFT:
                {
                    //將被滑動項目暫存，並移除被滑動項目
                    deleteVocabulary = word_hashMapInAL.get(position);
                    deleteSet = word_hashMapInAL.get(position).get("en");
                    wordSet.remove(word_hashMapInAL.get(position).get("en"));
                    recyclerViewAdapter.notifyItemRemoved(viewHolder.getPosition());
                    word_hashMapInAL.remove(viewHolder.getPosition());

                    //更新頁面
                    setLayout_List();
                    setLayout_Dictionary();
                    setLayout_List();

                    //wordSet.remove(word_hashMapInAL.get(position).get("en"));

                    //儲存檔案
                    saveData();

                    //底部顯示已刪除及復原按鈕
                    Snackbar.make(recyclerView, "已刪除", Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //當取消時便將暫存資料放回
                                    word_hashMapInAL.add(position,deleteVocabulary);
                                    recyclerViewAdapter.notifyItemInserted(position);
                                    wordSet.add(deleteSet);
                                    //儲存檔案
                                    saveData();
                                    Toast toast = Toast.makeText(MainActivity.this,"已復原刪除",Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }).show();
                }
                break;
            }

        }
    };


    private  void saveData()//儲存資料
    {

        SharedPreferences sharedPreferencesData = getSharedPreferences("Data",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferencesData.edit();

        Gson gson = new Gson();
        String data = gson.toJson(word_hashMapInAL.toArray()).toString();
        editor.putString("Data",data);
        editor.commit();
        Data.setData(word_hashMapInAL);
    }


    private boolean getData()//從本地取得資料
    {
        int total;
        boolean ans = false;
        SharedPreferences sharedPreferences = getSharedPreferences("Data",Context.MODE_PRIVATE);

        String data = sharedPreferences.getString("Data","未讀取到資料");

        /***
         * 將字串分割為陣列
         *
         * 格式
         * {"en":"dentist","ch":"牙醫"},{"en":"Apple","ch":"蘋果"},{"en":"Banana","ch":"香蕉"}
         *先透過 , 分割出每組單字，後再依每組分割
         */

        if(!data.equals("未讀取到資料") &&  data.length() >= 3) {
            ans = true;

            data = data.substring(2, data.length() - 2);

            String[] aryData = data.split("\\},\\{");
            total = aryData.length;
            //System.out.println(data);
            String[] aryDataEn = new String[aryData.length];//英文陣列
            String[] aryDataCh = new String[aryData.length];//中文陣列
            ArrayList<String> englishList = new ArrayList<>();//英文串列
            ArrayList<String> chineseList = new ArrayList<>();//中文串列

            /***
             * 依中英分類放入陣列
             */
            for (int i = 0; i < aryData.length; i++) {

                aryDataEn[i] = aryData[i].split(",")[0].split(":")[1];
                aryDataEn[i] = aryDataEn[i].substring(1, aryDataEn[i].length() - 1);

                aryDataCh[i] = aryData[i].split(",")[1].split(":")[1];
                aryDataCh[i] = aryDataCh[i].substring(1, aryDataCh[i].length() - 1);
                word_hashMapInAL.add(new LinkedHashMap<String, String>());
                englishList.add("");
                chineseList.add("");
                //System.out.println(aryDataEn[i] + " " + aryDataCh[i]);
            }

            for (int i = total - 1; i >= 0; i--) {
                wordSet.add(aryDataEn[i]);
                word_hashMapInAL.get(i).put("en", aryDataEn[i]);
                word_hashMapInAL.get(i).put("ch", aryDataCh[i]);
                englishList.add(i, aryDataEn[i]);
                chineseList.add(i, aryDataCh[i]);
            }
            count = total;


        }

        //從Firebase 中取得單字表需要的圖片網址，並放入Map
        for(int i = 0; i <word_hashMapInAL.size(); i ++)
        {
            String word_English = word_hashMapInAL.get(i).get("en");
            DatabaseReference databaseReference =  firebaseDatabase.getReference("img/img_" + word_English);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.getValue() != null)
                    {
                        //URL url = null;//URL url = new URL(srcUrl);
                        VL_RecyclerViewAdapter.imgUrlMap.put(word_English,snapshot.getValue(String.class));
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        //imgURL.sendQuest(word_English);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        return ans;
      // for(int i = 0; i < total; i ++)//初始測試資料
      // {
      //     System.out.println(word_hashMapInAL.get(i).get("en"));
      //     System.out.println(word_hashMapInAL.get(i).get("ch"));
      // }

        //System.out.println(data);
/*
       String[] data_en = {"Apple","Banana","Cup","Monkey","car","cow","water","hell"};
       String[] data_ch = {"蘋果","香蕉","杯子","猴子","汽車","母牛","水","地獄"};

        for(int i = 0; i < count; i ++)
        {
            word_hashMapInAL.add(new LinkedHashMap<>());
            word_hashMapInAL.get(i).put("en",data_en[i]);
            word_hashMapInAL.get(i).put("ch",data_ch[i]);
           //System.out.println("add data " + i + ". " + word_hashMapInAL.get(i).get("en") + ":" + word_hashMapInAL.get(i).get("ch"));
           //System.out.println( );

        }
 */
    }
    //重新載入使用者資訊
    private void reloadUser()
    {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            btn_login.setText(R.string.String_signOut);
            textView_User.setText("使用者: " + user.getEmail().toString().split("@")[0]);

        }else{
            btn_login.setText(R.string.String_Login);
            textView_User.setText("未登入");

        }

    }
    //將新單字加入單字表並保存
    private void add(String en,String ch)
    {
        SharedPreferences sharedPreferences = getSharedPreferences("Data",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        Gson gson = new Gson();
        String data = gson.toJson(word_hashMapInAL.toArray()).toString();
        System.out.println(data);

        editor.putString("Data",data);
        editor.commit();

        //listModel.add(0,new ListModel(ch,en));
        recyclerViewAdapter.notifyItemInserted(0);
        recyclerViewAdapter.notifyDataSetChanged();
        //recyclerView.setLayoutManager(new LinearLayoutManager(this));




    }

    //初始設定及載入資料
    private void setUpListModels()
    {
        //判斷是否首次使用
        SharedPreferences sharedPreferences = getSharedPreferences("fistTime",Context.MODE_PRIVATE);
        String firstTime = sharedPreferences.getString("isFistTime","YES");

        if(firstTime.equals("NO"))//否
        {
           if(getData())
               for (int i = 0; i < word_hashMapInAL.size(); i ++) {
                wordSet.add(word_hashMapInAL.get(i).get("en"));
           }


        }
        else if(firstTime.equals("YES"))//是
        {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("isFistTime","NO");
            add("English", "英語");
            editor.commit();
        }
        else
        {
            System.out.println("ERROR");
        }

    }
    //將畫面設為字典
    public void setLayout_Dictionary()
    {
        setTitle(getText(R.string.BTN_dictionary));
        layout_dictionary.setVisibility(View.VISIBLE);
        layout_list.setVisibility(View.INVISIBLE);
        layout_exam.setVisibility(View.INVISIBLE);

        layout_dictionary.getLayoutParams().width = layout_main.getWidth();
    }
    //將畫面設為單字表
    public void setLayout_List()
    {
        setTitle(getText(R.string.BTN_vocabulary_list));
        layout_dictionary.setVisibility(View.INVISIBLE);
        layout_list.setVisibility(View.VISIBLE);
        layout_exam.setVisibility(View.INVISIBLE);

        layout_list.getLayoutParams().width = layout_main.getWidth();
    }
    //將畫面設為測驗
    public void setLayout_exam()
    {
        setTitle(getText(R.string.BTN_exam));
        layout_dictionary.setVisibility(View.INVISIBLE);
        layout_list.setVisibility(View.INVISIBLE);
        layout_exam.setVisibility(View.VISIBLE);

        layout_exam.getLayoutParams().width = layout_main.getWidth();
        if(word_hashMapInAL.size() < 5 || word_hashMapInAL == null)
        {
            textView_countExam.setText("當單字表超過5個單字即可啟用測驗功能");
            textView_countExam.setTextSize(20);

            btn_enCountExam.setVisibility(View.INVISIBLE);
            btn_deCountExam.setVisibility(View.INVISIBLE);
            btn_startExam.setVisibility(View.INVISIBLE);
        }
        else
        {
            int count = word_hashMapInAL.size();

            textView_countExam.setText("5");
            textView_countExam.setTextSize(50);

            btn_enCountExam.setVisibility(View.VISIBLE);
            btn_deCountExam.setVisibility(View.VISIBLE);
            btn_startExam.setVisibility(View.VISIBLE);
        }
    }
    //介面切換按鈕
    View.OnClickListener btn_LayoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId())
            {

                case R.id.btn_dictionary_ID:
                {
                    //將畫面設為字典
                    setLayout_Dictionary();
                }
                break;
                case R.id.btn_list_ID:
                {
                    //將畫面設為單字表
                    setLayout_List();

                }
                break;
                case R.id.btn_exam_ID:
                {
                    //將畫面設為測驗
                    setLayout_exam();
                }
                break;
                case R.id.btn_login_ID:
                {
                    //判斷是否已經登入
                    re_save = true;
                    if(isLogin)//是
                    {
                        //將使用者登出
                        FirebaseAuth.getInstance().signOut();
                        Toast toast = Toast.makeText(MainActivity.this, "已登出" , Toast.LENGTH_SHORT);
                        //登入狀態設為False
                        isLogin = false;
                        btn_login.setText(R.string.String_Login);
                        toast.show();
                    }

                    //切換至登入頁面
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this,loginActivity.class);
                    startActivity(intent);
                   // setContentView(R.layout.login);

                }
                break;



            }
        }
    };
    //移除單字
    public void remove ()
    {
        //listModel.remove(0);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    //功能按鈕監聽器
    View.OnClickListener btn_Function_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.btn_translate_ID://翻譯按鈕
                {

                    //img_translate.setImageResource(R.drawable.wrong);
                    //先判斷是否正在翻譯
                    if(!editText_search.getText().toString().isEmpty() && !translate_output.getText().toString().equals("翻譯中..."))
                    {
                        //圖片設為搜尋中
                        img_translate.setImageResource(R.drawable.search);
                        //判斷輸入資料為中文還是英文
                        if(String_Language_From.equals("ch"))
                        {
                            string_nowInputLanguage = "ch";
                        }
                        else
                        {
                            string_nowInputLanguage = "en";
                        }
                        String input;

                        //將輸入資料去除首尾空白並全部轉成小寫
                        input = editText_search.getText().toString().toLowerCase().trim();
                        //editText_search.setText("");

                        translate_input.setText(input);
                        translate_output.setText("翻譯中...");

                        //取得Firebase資料庫中的單字
                        DatabaseReference databaseReference = firebaseDatabase.getReference("vocabularyMap/"+input);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override//判斷資料庫是否存在單字
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                //存在
                                if(snapshot.getValue() != null)
                                {
                                    //抓取單字
                                    String value= snapshot.getValue().toString();
                                    System.out.println("資料庫中存在單字:" + input);
                                    translate_output.setText(value);


                                }
                                else//不存在
                                {
                                    System.out.println("資料庫中不存在單字:" + input);
                                    try {
                                        //透過翻譯API翻譯輸入
                                        translateAI(String_Language_From,String_Language_To,input);
                                        if(string_nowInputLanguage.equals("en"))
                                        {
                                            String outcome = "";
                                            do {
                                                outcome = translate_output.getText().toString();
                                            }while (outcome.equals("翻譯中..."));

                                            databaseReference.setValue(translate_output.getText());
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        //設定翻譯圖片

                        getFirebaseImgData(translate_input.getText().toString());





                    }



                }
                break;
                case R.id.btn_switchLanguage_ID://切換語言
                {
                    String temp = translate_input.getText().toString();
                    if(String_Language_From.equals("en"))
                    {
                        String_Language_From = "ch";
                        String_Language_To = "en";

                        textView_languageFrom.setText("中文");
                        textView_languageTo.setText("English");
                    }
                    else
                    {
                        String_Language_From = "en";
                        String_Language_To = "ch";

                        textView_languageFrom.setText("English");
                        textView_languageTo.setText("中文");
                    }
                }
                break;
                case R.id.btn_addWordToList_ID://加入收藏
                {
                    //如果不正在翻譯
                    if(!translate_output.getText().toString().equals("翻譯中..."))
                    {
                        String String_en, String_ch;

                        //判斷輸入語言
                        if(string_nowInputLanguage.equals("en"))
                        {
                            String_en = translate_input.getText().toString();
                            String_ch = translate_output.getText().toString();
                        }
                        else
                        {
                            String_en = translate_output.getText().toString();
                            String_ch = translate_input.getText().toString();

                        }
                        //如果單字表中不存在該單字即新增
                        if(wordSet.add(String_en) && !translate_input.getText().equals("English"))
                        {

                            word_hashMapInAL.add(0,new LinkedHashMap<>());
                            word_hashMapInAL.get(0).put("en",String_en.toLowerCase(Locale.ROOT));
                            word_hashMapInAL.get(0).put("ch",String_ch);


                            System.out.println("size: " + word_hashMapInAL.size());
                            System.out.println("English:" + word_hashMapInAL.get(0).get("en"));
                            System.out.println("中文: " + word_hashMapInAL.get(0).get("ch"));




                            add(String_en,String_ch);
                            Toast toast = Toast.makeText(MainActivity.this,"新增成功",Toast.LENGTH_SHORT);
                            toast.show();

                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"已存在單字",Toast.LENGTH_SHORT).show();
                        }
                        recyclerViewAdapter.notifyDataSetChanged();
                    }
                }
                break;
                case R.id.btn_TTS_ID://TTS按鈕
                {
                    String text = translate_input.getText().toString();
                    textToSpeech.stop();
                    textToSpeech.speak(text,TextToSpeech.QUEUE_FLUSH,null);


                }
                break;
                case R.id.btn_addList_ID://加入新條
                {

                    String en,ch;
                    en = word_hashMapInAL.size()+"";
                    ch = "中文";

                    word_hashMapInAL.add(0,new LinkedHashMap<>());
                    word_hashMapInAL.get(0).put("en",word_hashMapInAL.size()+"");
                    word_hashMapInAL.get(0).put("ch","中文");
                    add(word_hashMapInAL.size()+"","中文");
                    count ++;
                    recyclerViewAdapter.notifyItemInserted(0);
                    recyclerViewAdapter.notifyDataSetChanged();

                }
                break;
                case R.id.btn_upload_ID://資料庫頁面(上傳下載資料庫檔案)
                {
                    if(isLogin) {
                        saveData();
                        Data.setData(word_hashMapInAL);
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, com.example.kingofvocabulary.database.class);
                        startActivity(intent);

                    }else {
                        Toast toast = Toast.makeText(MainActivity.this,"請先登入帳號", Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }
                break;
                case R.id.btn_startExam_ID://開始測驗
                {
                    countExam = Integer.parseInt(textView_countExam.getText().toString());
                   Intent intent = new Intent();
                   intent.setClass(MainActivity.this, exam.class);
                   startActivity(intent);

                }
                break;
                case R.id.btn_enCountExam_ID://+題數
                {
                    int count = Integer.parseInt(textView_countExam.getText().toString());
                    if( count + 1 <= word_hashMapInAL.size())
                    {
                        textView_countExam.setText((count + 1) + "");
                    }

                }
                break;
                case R.id.btn_deCountExam_ID://-題數
                {
                    int count = Integer.parseInt(textView_countExam.getText().toString());
                    if( count - 1 < word_hashMapInAL.size() && count > 5)
                    {
                        textView_countExam.setText((count - 1) + "");
                    }

                }
                break;


            }

        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if(id == R.id.Item_database_ID)
        {
            if(isLogin) {
                saveData();
                Data.setData(word_hashMapInAL);
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, com.example.kingofvocabulary.database.class);
                startActivity(intent);

            }else {
                Toast toast = Toast.makeText(MainActivity.this,"請先登入帳號", Toast.LENGTH_SHORT);
                toast.show();
            }
        }

        return super.onOptionsItemSelected(item);
    }
    public static String getEnglishInput()
    {
        return translate_output.getText().toString();
    }
    //取得Firebase中照片網址
    public void getFirebaseImgData(String word)
    {

        String output = "";
        //設定資料庫位置
        firebaseDatabase = FirebaseDatabase.getInstance("https://kingofvocabulary-default-rtdb.asia-southeast1.firebasedatabase.app/");
        //取得資料庫路徑
        DatabaseReference databaseReference = firebaseDatabase.getReference("/img/img_"+word);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String value = snapshot.getValue(String.class);

                //如果資料庫中不存在網址
                if(value == null || value.equals("輸出為空"))
                {
                    System.out.println("firebase中不存在網址");

                    //透過API抓取圖片
                    String srcUrl = imgURL.sendQuest(translate_input.getText().toString());
                    VL_RecyclerViewAdapter.imgUrlMap.put(word,value);
                }
                else
                {
                    //設定資料庫取得的圖片
                    VL_RecyclerViewAdapter.imgUrlMap.put(word,value);
                    System.out.println("資料庫中圖片網址:" + value );

                }
                //與圖片網址連線並顯示
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(value);//URL url = new URL(srcUrl);
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.connect();
                            InputStream inputStream = conn.getInputStream();
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                            //URL url = new URL("https://www.petmd.com/sites/default/files/2021-01/cute-small-dog-lying-on-floor-beside-bowl-of-food.jpg");//URL url = new URL(srcUrl);

                            img_translate.setImageBitmap(bitmap);

                        }
                        catch (Exception e)
                        {

                        }

                    }
                }).start();




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("連線失敗");
            }
        });

    }
    //當項目被長按時，複製到剪貼簿
    View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {

            switch (view.getId())
            {
                case R.id.textView_translateOutput:
                case R.id.text_inputTranslate_ID:
                {
                    ClipboardManager clipboardManager = (ClipboardManager) view.getContext().getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("vocabulary",translate_input.getText());
                    clipboardManager.setPrimaryClip(clipData);
                    Toast toast = Toast.makeText(view.getContext(),"已複製到剪貼簿",Toast.LENGTH_SHORT);
                    toast.show();
                    Vibrator myVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
                    myVibrator.vibrate(100);
                }
                break;
            }
            return false;
        }
    };


    //非google官方翻譯API
    private void translateAI(String from, String to, String context) throws IOException {
        if(!context.isEmpty())
        {
            OkHttpClient client = new OkHttpClient();
            //傳送API請求
            MediaType mediaType = MediaType.parse("application/json");
            String value = "{\r\n    \"texts\": [\r\n        \"" + context + "\"\r         \r     ],\r     \"tls\": [\r         \"en\"\r     ],\r     \"sl\": \"zh-tw\"\r }";
            if(from.equals("en"))
            {
                value = "{\r\n    \"texts\": [\r\n        \"" + context + "\"\r         \r     ],\r     \"tls\": [\r         \"zh-tw\"\r     ],\r     \"sl\": \"en\"\r }";;
            }

            //取得結果
            RequestBody body = RequestBody.create(mediaType, value);
            Request request = new Request.Builder()
                    .url("https://ai-translate.p.rapidapi.com/translates")
                    .post(body)
                    .addHeader("content-type", "application/json")
                    .addHeader("X-RapidAPI-Key", "f165b7871amsha687f4a243bd2f6p1146d9jsnaa4cb4499a75")
                    .addHeader("X-RapidAPI-Host", "ai-translate.p.rapidapi.com")
                    .build();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response response = client.newCall(request).execute();
                        String res = response.body().string().toString();
                        System.out.println(res);
                        translate_output.setText(transOutCome(res));

                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                }
            }).start();
        }
    }

    //轉換文字格式
    private static String transOutCome(String res)
    {
        int start = 5;
        int count = 0;
        String out = "";
        boolean enable = false;
        for(int i = 0; i < res.length(); i ++)
        {

            if (res.charAt(i) == '"')
            {
                count ++;
            }
            if(enable)
                out += res.charAt(i);
            if(count == start)
            {
                enable = true;
                if(res.charAt(i + 1)== '"')
                    enable = false;
            }
        }
        return out;
    }


}
