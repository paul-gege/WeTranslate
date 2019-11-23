package com.example.wetranslate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class OcrTranslate extends AppCompatActivity {

    SurfaceView cameraView;
    TextView textView;
    String end_lang;
    CameraSource cameraSource;
    TextRecognizer textRecognizer;
    Button stopCamera;
    private RequestQueue mQueue;
    boolean isCameraStopped = false;
    final int PERMISSION_REQUEST_CAMERA = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_translate);

        end_lang = this.getIntent().getStringExtra("end_lang");
        mQueue = Volley.newRequestQueue(this);

        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        textView = (TextView) findViewById(R.id.text_view);
        stopCamera = (Button) findViewById(R.id.stop);

        stopCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCameraStopped) {
                    Intent intent = new Intent(getBaseContext(), OcrTranslate.class); // your activity
                    startActivity(intent);
                    stopCamera.setText("Capture Image");
                } else {
                    stopCamera.setText("Translate Text");
                    cameraSource.stop();
                    jsonParse();
                }
            }
        });

        textRecognizer = new TextRecognizer.Builder(this).build();
        if (!textRecognizer.isOperational()) {
            Log.w("OcrActivity", "Detector dependencies are not yet available");
        }
        cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                try {
                    if (isCameraPermissionGranted()) {
                        cameraSource.start(cameraView.getHolder());
                    } else {
                        requestForPermission();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
            }
        });

        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {
                final SparseArray<TextBlock> items = detections.getDetectedItems();
                if (items.size() != 0) {
                    textView.post(new Runnable() {
                        @Override
                        public void run() {
                            StringBuilder stringBuilder = new StringBuilder();
                            for (int i = 0; i < items.size(); ++i) {
                                TextBlock item = items.valueAt(i);
                                stringBuilder.append(item.getValue());
                                stringBuilder.append("\n");
                            }
                            textView.setText(stringBuilder.toString());

                        }
                    });
                }
            }
        });
    }

    private boolean isCameraPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void requestForPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != PERMISSION_REQUEST_CAMERA) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isCameraPermissionGranted()) {
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                finish();
            }
        }
    }

    private void jsonParse() {
        String start_url = "https://translate.yandex.net/api/v1.5/tr.json/translate";
        String key = "?key=trnsl.1.1.20191002T223910Z.414f92f154ef1226.3b0efcfc0e39cf01694e6f90f3634a8eb308f010";
        String text = "&text=" + textView.getText().toString();
        String lang = "&lang=" + "en-" + end_lang;

        String url = start_url + key + text + lang;
        Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray text = response.getJSONArray("text");
                    String translated = "";
                    for(int i = 0; i < text.length(); i++){
                        translated += text.getString(i);
                    }

                    textView.setText(translated);

                } catch (JSONException e) {
                    Toast.makeText(OcrTranslate.this, "Error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(OcrTranslate.this, "Error", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }
}
