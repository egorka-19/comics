package com.example.comics;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.comics.Model.PopularModel;
import com.example.comics.Model.ViewAllModel;
import com.example.comics.UI.Users.HomeActivity;
import com.example.comics.UI.Users.add_response;
import com.example.comics.paint.PaintActivity;
import com.google.android.material.imageview.ShapeableImageView;

public class product_card extends AppCompatActivity {
    ImageView detailedImg;

    TextView price, description, name;

    ImageButton back;

    ViewAllModel viewAllModel = null;
    PopularModel popularModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_card);
        back = findViewById(R.id.btn_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                new Intent(product_card.this, HomeActivity.class);
            }
        });

        final Object object = getIntent().getSerializableExtra("detail");
        if (object instanceof ViewAllModel){
            viewAllModel = (ViewAllModel) object;
        }
        if (object instanceof PopularModel){
            popularModel = (PopularModel) object;
        }

        detailedImg = findViewById(R.id.iv_comic);
        name = findViewById(R.id.tv_title);

        if (viewAllModel != null){
            Glide.with(getApplicationContext()).load(viewAllModel.getImg_url()).into(detailedImg);
            name.setText(viewAllModel.getName());
        }
        if (popularModel != null){
            Glide.with(getApplicationContext()).load(popularModel.getImg_url()).into(detailedImg);
            name.setText(popularModel.getName());
        }
        ShapeableImageView imageView = findViewById(R.id.iv_comic);
        imageView.setShapeAppearanceModel(
                imageView.getShapeAppearanceModel()
                        .toBuilder()
                        .setAllCornerSizes(60f) // Размер скругления в пикселях
                        .build()
        );
        findViewById(R.id.menu_edit).setOnClickListener(v -> {
            Intent intent = new Intent(product_card.this, PaintActivity.class);
            startActivity(intent);
        });

    }
}