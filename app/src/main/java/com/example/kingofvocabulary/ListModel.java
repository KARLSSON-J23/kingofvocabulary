package com.example.kingofvocabulary;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ListModel {
    ArrayList<LinkedHashMap<String,String>> vocabularyList;
    LinkedHashMap<String,String> vocabularyDefinition;
    static int count = 0;
    int image;
    int p;

    public ListModel(ArrayList<LinkedHashMap<String,String>> vocabularyList) {
        this.vocabularyList = vocabularyList;
        p = count;
        count ++;


    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch (direction)
            {
                case ItemTouchHelper.LEFT:
                {


                }
                break;


            }
        }
    };
    public int getItemPosition__()
    {
        return p;
    }

    public String getVocabularyEnglish() {
        return vocabularyList.get(p).get("en");
    }

    public String getVocabularyChinese() {
        return vocabularyList.get(p).get("ch");
    }
/*
    public String getVocabularyDefinition() {
        return vocabularyDefinition;
    }
*/
    public int getImage() {
        return image;
    }
}
