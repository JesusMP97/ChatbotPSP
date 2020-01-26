package com.example.chatbotpsp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatbotpsp.API.ChatterBot;
import com.example.chatbotpsp.API.ChatterBotFactory;
import com.example.chatbotpsp.API.ChatterBotSession;
import com.example.chatbotpsp.API.ChatterBotType;
import com.example.chatbotpsp.API.TextToSpeechActivity;
import com.example.chatbotpsp.API.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "xyz";
    ImageButton btVoice, btSend;
    EditText etInput;
    pl.droidsonroids.gif.GifImageView gif;
    ChatterBot bot;
    ChatterBotSession botSession;
    RecyclerView recyclerView;
    AdapterMultiType adapter;
    String lngFrom = "es", lngTo = "en";
    String str = "";
    String translation = "";
    ArrayList<String> result = null;
    Mensaje holderMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        initEvents();
        initBot();
        Toast.makeText(this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_SHORT).show();
    }

    private void initComponents() {
        btSend = findViewById(R.id.btSend);
        btVoice = findViewById(R.id.btVoice);
        etInput = findViewById(R.id.etInput);
        recyclerView = findViewById(R.id.recyclerView);
        gif = findViewById(R.id.gif);
        Toolbar toolbar = findViewById(R.id.toolbarChatbot);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Chatbot");

        adapter = new AdapterMultiType(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);

    }

    private void initEvents() {
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //btSend.setClickable(false);
                str = etInput.getText().toString();
                TranslateToEng translateTask = new TranslateToEng(str);
                Date currentDatetime = Calendar.getInstance().getTime();
                String time = currentDatetime.getHours()+":"+currentDatetime.getMinutes();
                holderMensaje = new Mensaje(time, etInput.getText().toString(), "placeholder",true);
                etInput.setText("");
                gif.setVisibility(View.VISIBLE);
                lngFrom = "es";
                lngTo = "en";
                translateTask.execute();
            }
        });

        btVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Di algo",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

                if(intent.resolveActivity(getPackageManager())!=null) {
                    startActivityForResult(intent, 5);
                }
                else {
                    Toast.makeText(v.getContext(),"Tu dispositivo no soporta STT", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initBot() {
        ChatterBotFactory factory = new ChatterBotFactory();

        try {
            bot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
        } catch (Exception e) {
            e.printStackTrace();
        }

        botSession = bot.createSession();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==5) {
            if(resultCode==RESULT_OK && data!=null) {
                result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                etInput.setText(result.get(0));
            }
        }
    }

    public void doTheChat(){ // La traduccion del mensaje del usuario llega
        holderMensaje.setSentenceEn(translation);
        adapter.mensajes.add(holderMensaje);
        saveMessage(holderMensaje);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(adapter.mensajes.size() - 1);
        gif.setVisibility(View.GONE);
        new Chat().execute();
    }

    private void chat(String msg) { // El mensaje del usuario traducido se envia al bot
        try {
            String response = botSession.think(msg);
            Date currentDatetime = Calendar.getInstance().getTime();
            String time = currentDatetime.getHours()+":"+currentDatetime.getMinutes();
            holderMensaje = new Mensaje(time, "placeholder", response,false);
            new TranslateToEs(response).execute();
        }catch(Exception e){
            Log.v("xyz", "Error: " + e.getMessage());
        }
    }

    private void showBotResponse(){ // La respuesta del bot se recibe
        holderMensaje.setSentenceEs(translation);
        adapter.mensajes.add(holderMensaje);
        saveMessage(holderMensaje);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(adapter.mensajes.size() - 1);
        //btSend.setClickable(true);
        gif.setVisibility(View.GONE);
        new TextToSpeechActivity(adapter.mensajes.get(adapter.mensajes.size()-1).sentenceEs, this);
    }

    public String decomposeJson(String json){
        String translationResult = "Could not get";
        try {
            JSONArray arr = new JSONArray(json);
            JSONObject jObj = arr.getJSONObject(0);
            translationResult = jObj.getString("translations");
            JSONArray arr2 = new JSONArray(translationResult);
            JSONObject jObj2 = arr2.getJSONObject(0);
            translationResult = jObj2.getString("text");
        } catch (JSONException e) {
            translationResult = e.getLocalizedMessage();
        }
        return translationResult;
    }

    private void saveMessage(Mensaje mensaje) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference referenciaItem = database.getReference(uid);
        referenciaItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v(TAG, "data changed: " + dataSnapshot.toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v(TAG, "error: " + databaseError.toException());
                Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        Date currentDate = Calendar.getInstance().getTime();
        Map<String, Object> map = new HashMap<>();
        String key = referenciaItem.child(getCurrentDate()).push().getKey();
        map.put(getCurrentDate() + "/" + key, mensaje.toMap());
        referenciaItem.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.v(TAG, "task succesfull");
                } else {
                    Log.v(TAG, task.getException().toString());
                    Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getCurrentDate(){
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        int currentYear = calendar.get(Calendar.YEAR);
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        String date = currentDay + "-" +  currentMonth + "-" + currentYear;
        return date;
    }

    private class Chat extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            chat(translation);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(adapter.mensajes.size() - 1);
        }
    }

    private class TranslateToEng extends AsyncTask<Void, Void, Void>{

        private final Map<String, String> headers;
        private final Map<String, String> vars;
        String s = "Error";

        private TranslateToEng(String message) {
            headers = new LinkedHashMap<String, String>();
            headers.put("Content-type","application/x-www-form-urlencoded");
            headers.put("User-Agent:","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");

            vars = new HashMap<String, String>();
            vars.put("fromLang", "es");
            vars.put("text",message);
            vars.put("to","en");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                s = Utils.performPostCall("https://www.bing.com/ttranslatev3", (HashMap) vars);
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("xyz", "Error: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            translation = decomposeJson(s);
            doTheChat();
        }
    }

    private class TranslateToEs extends AsyncTask<Void, Void, Void>{

        private final Map<String, String> headers;
        private final Map<String, String> vars;
        String s = "Error";

        private TranslateToEs(String message) {
            headers = new LinkedHashMap<String, String>();
            headers.put("Content-type","application/x-www-form-urlencoded");
            headers.put("User-Agent:","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");

            vars = new HashMap<String, String>();
            vars.put("fromLang", "en");
            vars.put("text",message);
            vars.put("to","es");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                s = Utils.performPostCall("https://www.bing.com/ttranslatev3", (HashMap) vars);
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("xyz", "Error: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            translation = decomposeJson(s);
            showBotResponse();
        }
    }

}

