package com.example.kingofvocabulary;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.PowerManager;
import android.os.VibrationAttributes;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.spec.PSSParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;


public class VL_RecyclerViewAdapter extends RecyclerView.Adapter<VL_RecyclerViewAdapter.MyViewHolder> {
    Context context;
    //ArrayList<ListModel> wordListModels;
    ArrayList<LinkedHashMap<String,String>> vocabularyList;
    static HashMap<String,String> imgUrlMap;
    FirebaseDatabase database = FirebaseDatabase.getInstance("https://kingofvocabulary-default-rtdb.asia-southeast1.firebasedatabase.app/");
    int count = 0;
    private TextToSpeech textToSpeech ;
    public VL_RecyclerViewAdapter(ArrayList<LinkedHashMap<String, String>> vocabularyList) {
        this.vocabularyList = vocabularyList;
        imgUrlMap = new HashMap<>();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mAuth_user =mAuth.getCurrentUser();




        //DatabaseReference myRef = database.getReference("img/img_default");
        String vocabulary;
        DatabaseReference myRef;
        for(int i = 0; i < MainActivity.word_hashMapInAL.size(); i ++)
        {
            vocabulary = MainActivity.word_hashMapInAL.get(i).get("en");
            myRef = database.getReference("img/img_" + vocabulary);
            String finalVocabulary = vocabulary;
            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    String imgUrl = snapshot.getValue(String.class);
                    System.out.println(imgUrl+ "網址");

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        count ++;
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_view_row,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }
    String imgSrc;
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String word_English = vocabularyList.get(position).get("en");
        holder.textView_Chinese.setText(vocabularyList.get(position).get("en"));
        holder.textView_English.setText(vocabularyList.get(position).get("ch"));

        //System.out.println(word_English);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//
                    URL url = new URL(imgUrlMap.get(word_English));
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    InputStream inputStream = conn.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    holder.img_show.setImageBitmap(bitmap);
//
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();



       // holder.textView_index.setText((position + 1) + "" );

    }


    @Override
    public int getItemCount() {
        return vocabularyList.size();
    }


    public void deleteItem(int position)
    {
        notifyItemRemoved(0);

    }

    public Object getSystemService(String name) {
        return null;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{


        TextView textView_English, textView_Chinese, textView_index;
        ImageView img_show;


        AIP_process aip_process = new AIP_process("https://lexicala1.p.rapidapi.com/search?text=cat&page=1",
                                                        "X-RapidAPI-Key", "23029f78f0mshdd6ad9ded18f896p176974jsn4445126674ff",
                                                        "X-RapidAPI-Host","lexicala1.p.rapidapi.com");
        Button btn_delete;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            textView_Chinese = itemView.findViewById(R.id.TextView_Englis_ID);
            textView_English = itemView.findViewById(R.id.textView_chinese_ID);
            textView_index = itemView.findViewById(R.id.textView_index_ID);
            img_show = itemView.findViewById(R.id.img_show_ID);





            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String english_word = textView_Chinese.getText().toString();

                    MainActivity.textToSpeech.stop();
                    MainActivity.textToSpeech.speak(english_word,TextToSpeech.QUEUE_FLUSH,null);
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    ClipboardManager clipboardManager = (ClipboardManager) view.getContext().getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("vocabulary",textView_Chinese.getText());
                    clipboardManager.setPrimaryClip(clipData);
                    Toast toast = Toast.makeText(view.getContext(),"已複製到剪貼簿",Toast.LENGTH_SHORT);
                    toast.show();
                    Vibrator myVibrator = (Vibrator) view.getContext().getSystemService(Service.VIBRATOR_SERVICE);
                    myVibrator.vibrate(100);
                   // System.out.println(aip_process.processJson("results"));------------------------------------------------------------------------------------------------------------------------------------------------------------------------


                    return false;
                }
            });


        }

        @Override
        public void onClick(View view) {


        }
    }

}
