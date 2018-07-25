package com.icumister.icumisterapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class QuickAssistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quick_assist);
    }

    public void dialPolice(View view) {
        dial("tel:100");
    }

    public void dialNeighbor(View view) {
        dial("tel:0544351511");
    }

    public void dialHome(View view) {
        dial("tel:0395235555");
    }

    public void dial(String number) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(number));
        startActivity(intent);
    }
}
