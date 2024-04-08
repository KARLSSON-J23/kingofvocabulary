package com.example.kingofvocabulary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class show_grade_Activity extends AppCompatActivity {
    private int score, exam_length;
    private String[] questionAry, ansAry, SystemAnsEng;
    private TextView textView_grade;
    private ListView listView_outcome;
    private String[] outcome;
    private boolean[] isCorrect;
    private Button btn_exitOutCome;
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.show_grade_layout);

        Intent intent = this.getIntent();
        Bundle recBundle = intent.getExtras();

        score = recBundle.getInt("score");
        exam_length = recBundle.getInt("exam_length");
        questionAry = recBundle.getStringArray("SystemAns");
        ansAry = recBundle.getStringArray("UserAns");
        SystemAnsEng = recBundle.getStringArray("SystemAnsEng");
        isCorrect = recBundle.getBooleanArray("isCorrect");

        textView_grade = findViewById(R.id.textView_grade_ID);
        listView_outcome = findViewById(R.id.listView_outcome_ID);
        btn_exitOutCome = findViewById(R.id.btn_exitOutCome_ID);

        textView_grade.setText("得分 : " + score + "/" + exam_length);

        MyAdapter myAdapter = new MyAdapter(this);

        listView_outcome.setAdapter(myAdapter);
        btn_exitOutCome.setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.btn_exitOutCome_ID:
                {
                    MainActivity.re_save = true;
                    Intent intent = new Intent();
                    intent.setClass(show_grade_Activity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            }
        }
    };
    public class MyAdapter extends BaseAdapter
    {
        private LayoutInflater layoutInflater;
        public MyAdapter(Context c) {
            layoutInflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return MainActivity.countExam;
        }

        @Override
        public Object getItem(int i) {
            return questionAry[i];
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = layoutInflater.inflate(R.layout.outcome_layout, null);

            ImageView imageView = view.findViewById(R.id.img_stateIcon_ID);
            TextView txt_Ans = view.findViewById(R.id.txt_Ans_ID);
            TextView txt_userAns = view.findViewById(R.id.txt_userAns_ID);
            TextView text_sysEngAns = view.findViewById(R.id.text_sysEngAns_ID);

            if(isCorrect[i])
            {
                imageView.setImageResource(R.drawable.correct);
            }
            else
            {
                imageView.setImageResource(R.drawable.wrong);
            }


            txt_Ans.setText(questionAry[i]);
            txt_userAns.setText(ansAry[i]);
            text_sysEngAns.setText(SystemAnsEng[i]);


            return view;
        }
    }
}
