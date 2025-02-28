package com.example.comics.bottomnav.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.comics.Model.HomeCategory;
import com.example.comics.Model.PopularModel;
import com.example.comics.Model.ViewAllModel;
import com.example.comics.R;
import com.example.comics.adapters.HomeAdapter;
import com.example.comics.adapters.PopularAdapters;
import com.example.comics.adapters.ViewAllAdapters;
import com.example.comics.databinding.FragmentMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment {
    ScrollView scrollView;
    ProgressBar progressBar;
    private FragmentMainBinding binding;
    FirebaseFirestore db;
    private Uri filePath;
    private ImageButton nextButton, allCategoryBtn;
    RecyclerView popularRec; // Убираем категории, оставляем только популярные
    PopularAdapters popularAdapters;
    List<PopularModel> popularModelList;
    EditText search_box; // Убираем поиск
    public String phone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        db = FirebaseFirestore.getInstance();

        // Убираем элементы поиска и категории
        popularRec = view.findViewById(R.id.pop_rec);
        scrollView = view.findViewById(R.id.scroll_view);
        progressBar = view.findViewById(R.id.progressbar);

        phone = requireActivity().getIntent().getStringExtra("phone");
        loadUserInfo();

        progressBar.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.GONE);

        // Настроим только популярные товары
        popularRec.setLayoutManager(new GridLayoutManager(getContext(), 2));
        popularModelList = new ArrayList<>();
        popularAdapters = new PopularAdapters(getActivity(), popularModelList);
        popularRec.setAdapter(popularAdapters);

        // Загружаем данные о популярных товарах
        db.collection("PaintCollection")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            PopularModel popularModel = document.toObject(PopularModel.class);
                            popularModelList.add(popularModel);
                            popularAdapters.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Error" + task.getException(), Toast.LENGTH_LONG).show();
                    }
                });

        return view;
    }

    private void loadUserInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(phone)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String profileImage = snapshot.child("profileImage").getValue().toString();
                            if (!profileImage.isEmpty()) {
                                Glide.with(getContext())
                                        .load(profileImage)
                                        .placeholder(R.drawable.loggg)
                                        .skipMemoryCache(true)
                                        .into(binding.avatarIv);
                            } else {
                                Toast.makeText(getContext(), "Загрузите свое фото!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Обработка ошибок базы данных
                    }
                });
    }

    // Метод для загрузки изображения в Firebase
    private void uploadImage() {
        if (filePath != null) {
            FirebaseStorage.getInstance().getReference().child("Product Images/" + phone)
                    .putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(getContext(), "Фото загружено успешно", Toast.LENGTH_SHORT).show();
                        FirebaseStorage.getInstance().getReference().child("Product Images/" + phone).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    FirebaseDatabase.getInstance().getReference().child("Users").child(phone)
                                            .child("profileImage").setValue(uri.toString())
                                            .addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Glide.with(getContext())
                                                            .load(uri)
                                                            .placeholder(R.drawable.down_splash_citek)
                                                            .skipMemoryCache(true)
                                                            .into(binding.avatarIv);
                                                }
                                            });
                                });
                    });
        }
    }
}
