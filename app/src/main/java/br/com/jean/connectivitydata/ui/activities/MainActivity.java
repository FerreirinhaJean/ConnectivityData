package br.com.jean.connectivitydata.ui.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import br.com.jean.connectivitydata.R;
import br.com.jean.connectivitydata.models.Callback;
import br.com.jean.connectivitydata.models.ConnectivityStattement;
import br.com.jean.connectivitydata.models.dto.ConnectivityStattementDto;
import br.com.jean.connectivitydata.repositories.ConnectivityStattementRepository;
import br.com.jean.connectivitydata.services.ConnectivityService;

public class MainActivity extends AppCompatActivity {

    private Button btIniciar;
    private ConnectivityStattementRepository connectivityStattementRepository;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btIniciar = findViewById(R.id.btIniciar);
        progressDialog = new ProgressDialog(this);

        connectivityStattementRepository = new ConnectivityStattementRepository(this);

        btIniciar.setOnClickListener(v -> {
            Intent intent = new Intent(this, GetConnectivityDataActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menuSinzronizar: {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Esta ação irá sincronizar os dados com o servidor")
                        .setPositiveButton("Confirmar", (dialog, which) -> {
                            progressDialog.setMessage("Sinzronizando com o servidor...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();

                            new Thread(() -> {
                                List<ConnectivityStattement> data = connectivityStattementRepository.getAllConnectivityData();
                                ConnectivityService service = new ConnectivityService();

                                int batchSize = 500;
                                int dataSize = data.size();
                                int startIndex = 0;
                                int endIndex = Math.min(startIndex + batchSize, dataSize);

                                while (startIndex < dataSize) {
                                    List<ConnectivityStattement> batchData = data.subList(startIndex, endIndex);
                                    service.enviarObjetoAPI(batchData, connectivityStattementRepository, new Callback<String>() {
                                        @Override
                                        public void onSuccess(String result) {
                                            runOnUiThread(() -> {
                                                progressDialog.dismiss();
                                                showToast(result);
                                            });
                                        }

                                        @Override
                                        public void onFailure(String errorMessage) {
                                            runOnUiThread(() -> {
                                                progressDialog.dismiss();
                                                showToast(errorMessage);
                                            });
                                        }
                                    });
                                    startIndex = endIndex;
                                    endIndex = Math.min(startIndex + batchSize, dataSize);
                                }
                            }).start();
                        }).setNegativeButton("Cancelar", (dialog, which) -> {
                        });

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
            case R.id.limparDados: {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Esta ação irá apagar todos os registros gerados")
                        .setPositiveButton("Confirmar", (dialog, which) -> {
                            connectivityStattementRepository.deleteAllConnectivityRecords();
                            showToast("Dados apagados com sucesso!");
                        }).setNegativeButton("Cancelar", (dialog, which) -> {
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}