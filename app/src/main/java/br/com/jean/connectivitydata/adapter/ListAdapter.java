package br.com.jean.connectivitydata.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.jean.connectivitydata.R;
import br.com.jean.connectivitydata.models.ConnectivityStattement;
import br.com.jean.connectivitydata.models.dao.ConnectivityStattementsDao;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<ConnectivityStattement> connectivityStattements = new ArrayList<>();
    private Context context;
    private ConnectivityStattementsDao connectivityStattementsDao = new ConnectivityStattementsDao();

    public ListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_get_connectivity_item_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConnectivityStattement connectivityStattement = connectivityStattements.get(position);
        String sinalWifi = "Intensidade do sinal wi-fi: " + connectivityStattement.getWifi() + "%";
        String sinalMovel = "Intensidade do sinal móvel: " + connectivityStattement.getMovel() + "%";
        String latitude = "Latitude: " + connectivityStattement.getLatitude();
        String longitude = "Longitude: " + connectivityStattement.getLongitude();

        holder.tvSinalWifi.setText(sinalWifi);
        holder.tvSinalMovel.setText(sinalMovel);
        holder.tvLatitude.setText(latitude);
        holder.tvLongitude.setText(longitude);
    }

    @Override
    public int getItemCount() {
        return connectivityStattements.size();
    }

    public void atualizar() {
        connectivityStattements.clear();
        connectivityStattements.addAll(connectivityStattementsDao.getAll());
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSinalWifi, tvSinalMovel, tvLatitude, tvLongitude;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSinalWifi = itemView.findViewById(R.id.tvSinalWifi);
            tvSinalMovel = itemView.findViewById(R.id.tvSinalMovel);
            tvLatitude = itemView.findViewById(R.id.tvLatitude);
            tvLongitude = itemView.findViewById(R.id.tvLongitude);
        }
    }
}