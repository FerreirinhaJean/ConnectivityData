package br.com.jean.connectivitydata.ui.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import br.com.jean.connectivitydata.R;
import br.com.jean.connectivitydata.models.dto.ConnectivityStattementDto;
import br.com.jean.connectivitydata.repositories.ConnectivityStattementRepository;
import br.com.jean.connectivitydata.services.ConnectivityService;

public class MainActivity extends AppCompatActivity {

    private Button btIniciar, btSincronizar;
    private ConnectivityStattementRepository connectivityStattementRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btIniciar = findViewById(R.id.btIniciar);
        btSincronizar = findViewById(R.id.btSincronizar);
        connectivityStattementRepository = new ConnectivityStattementRepository(this);

        btIniciar.setOnClickListener(v -> {
            Intent intent = new Intent(this, GetConnectivityDataActivity.class);
            startActivity(intent);
        });

        btSincronizar.setOnClickListener(v -> {
            List<ConnectivityStattementDto> data = connectivityStattementRepository.getAllConnectivityData();

            ConnectivityService service = new ConnectivityService();
            int batchSize = 500;
            int dataSize = data.size();
            int startIndex = 0;
            int endIndex = Math.min(startIndex + batchSize, dataSize);

            while (startIndex < dataSize) {
                List<ConnectivityStattementDto> batchData = data.subList(startIndex, endIndex);
                service.enviarObjetoAPI(batchData);

                startIndex = endIndex;
                endIndex = Math.min(startIndex + batchSize, dataSize);
            }
        });
    }
}