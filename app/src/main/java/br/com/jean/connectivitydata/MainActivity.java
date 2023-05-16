package br.com.jean.connectivitydata;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btIniciar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btIniciar = findViewById(R.id.btIniciar);

        btIniciar.setOnClickListener(v -> {
            Intent intent = new Intent(this, GetConnectivityDataActivity.class);
            startActivity(intent);
        });
    }
}