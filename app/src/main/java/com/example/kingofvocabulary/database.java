package com.example.kingofvocabulary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class database extends AppCompatActivity {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private TextView textView_UserName, textView_dataView;
    private Button btn_update, btn_download, btn_coverSave;
    private ListView listView;
    String data;
    FirebaseUser mAuth_user =mAuth.getCurrentUser();
    private  FirebaseDatabase database = FirebaseDatabase.getInstance("https://kingofvocabulary-default-rtdb.asia-southeast1.firebasedatabase.app/");
    ArrayAdapter<String> myAdapter;
    private ArrayList<String> dataAryList;
    protected void onCreate(Bundle savedInstanceState)
    {
        setTitle("資料備份");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database);


        textView_UserName = findViewById(R.id.textView_UserName_ID);
        textView_dataView = findViewById(R.id.textView_dataView_ID);

        listView =  findViewById(R.id.listView_show_ID);

        btn_update = findViewById(R.id.btn_upDate_ID);
        btn_download = findViewById(R.id.btn_download_ID);
        btn_coverSave = findViewById(R.id.btn_coverSave_ID);

        btn_download.setOnClickListener(onClickListener);
        btn_update.setOnClickListener(onClickListener);
        btn_coverSave.setOnClickListener(onClickListener);

        btn_coverSave.setVisibility(View.INVISIBLE);

       if(mAuth_user != null)
       {

           textView_UserName.setText(mAuth_user.getEmail());
            System.out.println(mAuth_user.getEmail());
            dataAryList = Data.showData;
            myAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataAryList);
            btn_coverSave.setVisibility(View.INVISIBLE);
           //
        }
       else {
           textView_UserName.setText("尚未登入");
       }


    }
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            switch (view.getId())
            {
                case R.id.btn_upDate_ID:{



                    DatabaseReference myRef = database.getReference("vocabulary/vocabulary_" + mAuth.getUid());
                    System.out.println(myRef.getKey());
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            myRef.setValue(Data.getData().toString());
                            toastShow("上傳成功");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(database.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                break;

                case R.id.btn_download_ID:{
                    DatabaseReference myRef = database.getReference("vocabulary/vocabulary_" + mAuth.getUid());
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            myAdapter.clear();

                             data = snapshot.getValue(String.class);
                             if(data != null)
                             {
                                 ArrayList<LinkedHashMap<String,String>> data_allhm ;
                                 //System.out.println(data);
                                 data_allhm = Data.transData(data);


                                 textView_dataView.setText(data_allhm.toString());


                                 listView.setAdapter(myAdapter);
                                 btn_coverSave.setVisibility(View.VISIBLE);
                             }
                             else {
                                 toastShow("尚未加入任何存檔");
                             }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(database.this, "Fail to add data " + error, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                break;
                case R.id.btn_coverSave_ID:{



                    ArrayList<LinkedHashMap<String,String>> data_allhm ;
                    //System.out.println(data);
                    data_allhm = Data.transData(data);
                    MainActivity.word_hashMapInAL = data_allhm;
                    MainActivity.re_save = true;
                    //MainActivity.recyclerViewAdapter.notifyDataSetChanged();

                    toastShow("已覆蓋存檔");
                    toastShow("請重新啟動程式以載入存檔");
                    Intent intent = new Intent();
                    intent.setClass(com.example.kingofvocabulary.database.this,MainActivity.class);
                   //startActivity(intent);
                   //finish();



                }
                break;

            }
        }
    };

    public void writeNewUser(String userId, String name, String email) {
        User user = new User(name, email);


    }
    private void toastShow(String str)
    {
        Toast toast = Toast.makeText(database.this,str,Toast.LENGTH_SHORT);
        toast.show();
    }
    public class User {

        public String username;
        public String email;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }

    }


}

