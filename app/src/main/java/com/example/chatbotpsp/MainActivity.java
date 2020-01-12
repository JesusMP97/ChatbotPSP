package com.example.chatbotpsp;

import androidx.appcompat.app.AppCompatActivity;
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button btSend;
    ImageButton btVoice;
    EditText etInput;
    TextView tvTest;
    pl.droidsonroids.gif.GifImageView gif;
    ChatterBot bot;
    ChatterBotSession botSession;
    RecyclerView recyclerView;
    AdapterMultiType adapter;
    String lngFrom = "es", lngTo = "en";
    String str = "";
    String translation = "";
    ArrayList<String> result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        initEvents();
        initBot();
    }

    private void initComponents() {
        btSend = findViewById(R.id.btSend);
        btVoice = findViewById(R.id.btVoice);
        etInput = findViewById(R.id.etInput);
        recyclerView = findViewById(R.id.recyclerView);
        gif = findViewById(R.id.gif);
        tvTest = findViewById(R.id.tvTest);

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
                adapter.mensajes.add(new Mensaje(etInput.getText().toString(), true));
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(adapter.mensajes.size() - 1);
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

    public void doTheChat(){
        gif.setVisibility(View.GONE);
        tvTest.setText(tvTest.getText() + "User: " + translation + "\n");
        new Chat().execute();
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

    // POST
    // https://www.bing.com/ttranslatev3

    // HEADERS
    // HEADER NAME: Content-type / application/x-www-form-urlencoded
    // User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36

    // BODY
    // fromLang=es
    // text=Soy programador
    // to=en
    private void chat(String msg) {
        try {
            String response = botSession.think(msg);
            new TranslateToEs(response).execute();
        }catch(Exception e){
            Log.v("xyz", "Error: " + e.getMessage());
        }
    }

    private void showBotResponse(){
        adapter.mensajes.add(new Mensaje(translation, false));
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(adapter.mensajes.size() - 1);
        //btSend.setClickable(true);
        gif.setVisibility(View.GONE);
        new TextToSpeechActivity(adapter.mensajes.get(adapter.mensajes.size()-1).mensaje, this);
    }

    /*public static String getTextFromUrl(String src) {
        StringBuffer out = new StringBuffer();
        try {
            URL url = new URL(src);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = in.readLine()) != null) {
                out.append(line + "\n");
            }
            in.close();
        } catch (IOException e) {
        }
        return out.toString();
    }*/

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

