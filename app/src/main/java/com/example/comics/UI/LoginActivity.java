package com.example.comics.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.comics.Model.Users;
import com.example.comics.Prevalent.Prevalent;
import com.example.comics.R;
import com.example.comics.UI.Admin.AdminCategoryActivity;
import com.example.comics.UI.Users.HomeActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity {

    private ImageButton loginButton, swipeButton;
    private EditText loginPhoneInput, loginPasswordInput;
    private ProgressDialog loadingBar;
    private String parentDbName = "Users";
    private CheckBox checkBoxRememberMe;
    private TextView AdminLink, NotAdminLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        swipeButton = (ImageButton)findViewById(R.id.swipe_btn);
        loginButton = (ImageButton)findViewById(R.id.register_button);


        loginButton = (ImageButton)findViewById(R.id.register_button);
        loginPasswordInput = (EditText) findViewById(R.id.register_password_input);
        loginPhoneInput = (EditText) findViewById(R.id.register_phone_input);
        loadingBar = new ProgressDialog(this);
        checkBoxRememberMe = (CheckBox) findViewById(R.id.register_checkbox);
        Paper.init(this);
        swipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(homeIntent);
            }
        });

        System.out.println("create and check");
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

    }

    private void loginUser() {
        String phone = loginPhoneInput.getText().toString();
        String password = loginPasswordInput.getText().toString();
        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Введите номер телефона", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
        } else {
            loadingBar.setTitle("Вход в приложение");
            loadingBar.setMessage("Пожалуйста, подождите...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ValidateUser(phone, password);
        }
    }

    private void ValidateUser(String phone, String password) {
        if(checkBoxRememberMe.isChecked()){
            Paper.book().write(Prevalent.UserPhoneKey, phone);
            Paper.book().write(Prevalent.UserPasswordKey, password);
        }

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String phone = loginPhoneInput.getText().toString();

                if (snapshot.child(parentDbName).child(phone).exists()){

                    Users usersData = snapshot.child(parentDbName).child(phone).getValue(Users.class);

                    if (usersData.getPhone().equals(phone))
                    {
                        if (usersData.getPassword().equals(password)){

                            if (parentDbName.equals("Users")){
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                                Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                homeIntent.putExtra("phone", phone);
                                System.out.println("login" + phone);
                                startActivity(homeIntent);

                            } else if(parentDbName.equals("Admins")){
                                loadingBar.dismiss();
                                Toast.makeText(LoginActivity.this, "Успешный вход!", Toast.LENGTH_SHORT).show();
                                Intent homeAdminIntent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                homeAdminIntent.putExtra("phone", phone);
                                startActivity(homeAdminIntent);

                            }

                        }
                        else{
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Неверный пароль", Toast.LENGTH_SHORT).show();
                        }
                    }


                }
                else {
                    loadingBar.dismiss();
                    Toast.makeText(LoginActivity.this, "Aккаунт с номером " + phone + " не существует", Toast.LENGTH_SHORT).show();
                    Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}