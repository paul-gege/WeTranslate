package com.example.wetranslate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class ViewFavorites extends AppCompatActivity {

    private DatabaseHelper my_db;
    private Button back_btn;
    private ListView fav_list;
    private FavAdapter mFavAdapter;
    private ArrayList<Favorites> favArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_favorites);

        back_btn = (Button) findViewById(R.id.back_btn);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewFavorites.this, MainActivity.class);
                startActivity(intent);
            }
        });

        my_db = new DatabaseHelper(this);

        favArray = my_db.getAllData();

        mFavAdapter = new FavAdapter(this, favArray );
        fav_list = (ListView) findViewById(R.id.favorites_list);
        fav_list.setAdapter(mFavAdapter);

        fav_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }
}
