package com.example.feedpost.ImageActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.feedpost.R;
import com.google.rpc.context.AttributeContext;

import java.lang.reflect.Field;

public class ImageActivity extends AppCompatActivity {
    // widgets
    private ImageView selectedPic ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        initializeWidgets() ;
        setSelectedPost() ;
    }

    // 1 .
    private void initializeWidgets(){
        selectedPic = findViewById(R.id.selectedPost) ;
    }
    // 2 .
    private void setSelectedPost(){
        TextView see = findViewById(R.id.seePassedText) ;
        Intent i = getIntent() ;
        Bitmap bitmap = i.getParcelableExtra("selectedPost") ;
        selectedPic.setImageBitmap(bitmap);
    }

    public void finishCurrentIntent(View view) {
        finish() ;
    }
}