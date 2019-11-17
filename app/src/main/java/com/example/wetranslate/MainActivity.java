package com.example.wetranslate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner translate_to;
    Button translate_btn;
    Button view_favorites;
    Button add_favorites;
    Button ocr_translate;
    EditText input_trans;
    EditText result_trans;
    String end_lang;

    private DatabaseHelper my_db;

    private RequestQueue mQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        translate_btn = (Button) findViewById(R.id.translate_btn);
        input_trans = (EditText) findViewById(R.id.trans_input);
        result_trans = (EditText) findViewById(R.id.trans_result);
        my_db = new DatabaseHelper(this);


        view_favorites = (Button) findViewById(R.id.view_fav);
        view_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent  = new Intent(MainActivity.this, ViewFavorites.class);
                startActivity(intent);
            }
        });
        add_favorites = (Button) findViewById(R.id.add_fav);
        add_favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = input_trans.getText().toString();
                String result = result_trans.getText().toString();
                String language = end_lang;

                Favorites new_favorite = new Favorites(input,result,language);
                boolean inserted = my_db.insertData(new_favorite);

                if(inserted){
                    Toast.makeText(MainActivity.this, "Successfully Added", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error Adding", Toast.LENGTH_LONG).show();
                }
            }
        });

        ocr_translate = (Button) findViewById(R.id.ocr_translate);
        ocr_translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OcrTranslate.class);
                startActivityForResult(intent, 1);
            }
        });

        mQueue = Volley.newRequestQueue(this);

        translate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jsonParse();
            }
        });

        translate_to = (Spinner) findViewById(R.id.spinner_to);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.languages,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        translate_to.setAdapter(adapter);
        translate_to.setOnItemSelectedListener(this);
    }

    private void jsonParse() {
        String start_url = "https://translate.yandex.net/api/v1.5/tr.json/translate";
        String key = "?key=trnsl.1.1.20191002T223910Z.414f92f154ef1226.3b0efcfc0e39cf01694e6f90f3634a8eb308f010";
        String text = "&text=" + input_trans.getText().toString();
        String lang = "&lang=" + "en-" + end_lang;

        String url = start_url + key + text + lang;
        Toast.makeText(MainActivity.this, "Loading", Toast.LENGTH_SHORT).show();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray text = response.getJSONArray("text");
                    String translated = "";
                    for(int i = 0; i < text.length(); i++){
                        translated += text.getString(i);
                    }

                    result_trans.setText(translated);

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString().substring(0,2);
        end_lang = text;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
