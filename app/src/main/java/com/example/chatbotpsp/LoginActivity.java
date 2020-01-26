package com.example.chatbotpsp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "xyz";
    Button btLogin, btRegister;
    TextInputEditText etUser, etPassword;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        startChatActivity();//TEST
        init();
    }

    private void init() {
        initComponents();
        initEvents();
    }

    private void initEvents() {
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initLogin();
            }
        });

        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerMenu();
            }
        });
    }

    private void initComponents() {
        btLogin = findViewById(R.id.btSign);
        btRegister = findViewById(R.id.btRegister);
        etUser = findViewById(R.id.tietUser);
        etPassword = findViewById(R.id.tietPassword);
    }

    private void initLogin() {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithEmailAndPassword(etUser.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                    startChatActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, task.getException().toString());
                }
            }
        });
    }

    private void startChatActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void registerUser(String user, String password) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(user, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    Toast.makeText(LoginActivity.this, "Registro completado, ya puedes iniciar sesion", Toast.LENGTH_SHORT).show();
                } else {
                    Log.v(TAG, task.getException().toString());
                    Toast.makeText(LoginActivity.this, "Error, " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void registerMenu() {
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.register_dialog, null);
        final EditText registerUsername, registerPassword;

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Registrarse");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);
        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        registerUsername = alertLayout.findViewById(R.id.registerUsername);
        registerPassword = alertLayout.findViewById(R.id.registerPassword);

        alert.setPositiveButton("Registrar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String user = registerUsername.getText().toString();
                String pass = registerPassword.getText().toString();
                registerUser(user, pass);
            }
        });
        AlertDialog dialog = alert.create();
        dialog.show();
    }


}
