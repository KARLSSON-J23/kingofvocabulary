package com.example.kingofvocabulary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class exam extends AppCompatActivity {

    private Button AnsResult;
    private TextView showExam,txtExam,txtScore;
    private EditText txtExamAns;
    private androidx.constraintlayout.widget.ConstraintLayout layout_inputAns;

    private LinearLayout layout_exam;
    private ImageView img_showExam;

    HashSet<String> wordSet = new HashSet<String>();
    ArrayList<LinkedHashMap<String,String>> wordList = new ArrayList<>();

    Set<String> strSet = new HashSet<>();
    String[] exam ;
    String[] examAnsArr;
    LinkedHashMap<Integer,String> examAns=new LinkedHashMap<Integer,String>();
    Random r2 = new Random();
    int rVoc;
    public int ex=0;
    int Score=0;
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.exam);

        AnsResult=findViewById(R.id.AnsResult);
        showExam=findViewById(R.id.showExam);
        txtExamAns=findViewById(R.id.txtExamAns);
        txtExam=findViewById(R.id.txtExam);

        layout_inputAns = findViewById(R.id.layout_inputAns_ID);

        layout_exam = findViewById(R.id.layout_exam_ID);

        img_showExam = findViewById(R.id.img_showExam_ID);



        layout_inputAns.setVisibility(View.VISIBLE);
        layout_exam.setVisibility(View.VISIBLE);

        exam = new String[MainActivity.countExam];
        examAnsArr=new String[MainActivity.countExam];

        for(int i = 0; i < MainActivity.word_hashMapInAL.size(); i ++)
        {
            wordList.add(new LinkedHashMap<>());
            wordList.get(i).put("en",MainActivity.word_hashMapInAL.get(i).get("en"));
            wordList.get(i).put("ch",MainActivity.word_hashMapInAL.get(i).get("ch"));


        }
        exam();

        txtExam.setText("第"+(ex+1)+"題： ");
        showExam.setText(exam[ex]+"   ");
        setTitle("單字測驗");
        changeImg();
        AnsResult.setOnClickListener(AnsResultListener);


    }
    private void changeImg()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//
                    URL url = new URL(VL_RecyclerViewAdapter.imgUrlMap.get(examAns.get(ex)));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    InputStream inputStream = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    img_showExam.setImageBitmap(bitmap);
                    System.out.println(exam[ex] + "換圖");
//
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {

            }
        }
    };
    private String[] saveAns = new String[MainActivity.countExam];
    private String[] ansEnglish = new String[MainActivity.countExam];
    private boolean[] isCorrect = new boolean[MainActivity.countExam];
    private final View.OnClickListener AnsResultListener=new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (ex!=exam.length){
                if(txtExamAns.getText().toString().isEmpty()){  //空值顯示Toast
                    Toast.makeText(exam.this, "請輸入答案", Toast.LENGTH_SHORT).show();
                }
                else{
                    examAnsArr[ex]=txtExamAns.getText().toString().trim().toLowerCase(Locale.ROOT); //答案存入examAnsArr[]
                    saveAns[ex] = txtExamAns.getText().toString().trim().toLowerCase(Locale.ROOT);
                    ex++;
                    if (ex!=exam.length){                     //顯示下一題
                        txtExam.setText("第"+(ex+1) + "/" + examAnsArr.length +"題： ");
                        showExam.setText(exam[ex]+"   ");
                        changeImg();

                    }else{

                        for (int i = 0; i < examAnsArr.length; i++) {   //對答案

                            if (examAnsArr[i].equals(examAns.get(i)))
                            {
                                Score++;
                                isCorrect[i] = true;
                            }
                            else
                            {
                                isCorrect[i] = false;
                            }
                        }
                       // txtScore.setVisibility(View.VISIBLE);
                       // layout_inputAns.setVisibility(View.INVISIBLE);
                       // btn_exit.setVisibility(View.VISIBLE);
                       // layout_grade.setVisibility(View.VISIBLE);
                       // layout_exam.setVisibility(View.INVISIBLE);
//
                       // txtScore.setText("得分： " + Score + "/" + exam.length);
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putStringArray("UserAns", saveAns);
                        bundle.putStringArray("SystemAns", exam);
                        bundle.putStringArray("SystemAnsEng", ansEnglish);
                        bundle.putInt("score", Score);
                        bundle.putInt("exam_length", examAnsArr.length);
                        bundle.putBooleanArray("isCorrect",isCorrect);




                        intent.putExtras(bundle);
                        intent.setClass(exam.this,show_grade_Activity.class);
                        startActivity(intent);
                        finish();

                    }

                }
            }else{
                Toast.makeText(exam.this, "測驗已結束", Toast.LENGTH_SHORT).show(); //題目出完 防止D能按下一提 顯示Toast
            }
            txtExamAns.setText("");


        }


    };
    public void exam(){

        for(int i = 0; i < exam.length; i ++)
        {
            do {
                rVoc = r2.nextInt(wordList.size());
            }while (!strSet.add(wordList.get(rVoc).get("ch")));
            exam[i] =wordList.get(rVoc).get("ch");
            examAns.put(i,wordList.get(rVoc).get("en"));
            ansEnglish[i] = wordList.get(rVoc).get("en");
        }
        for(int i=0;i<examAns.size();i++){
            System.out.println(examAns.get(i));
        }


    }


}
