package br.com.jean.connectivitydata.adapter;

import android.content.Context;
import android.telephony.CellSignalStrength;
import android.telephony.TelephonyManager;
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
        String sinalMovel = "Intensidade do sinal móvel: " + connectivityStattement.getMovel() + " dBm";
        String latitude = "Latitude: " + connectivityStattement.getLatitude();
        String longitude = "Longitude: " + connectivityStattement.getLongitude();

        holder.tvSinalWifi.setText(sinalWifi);
        holder.tvSinalMovel.setText(sinalMovel);
        holder.tvLatitude.setText(latitude);
        holder.tvLongitude.setText(longitude);
        holder.tvTipoRede.setText(convertNetworkType(connectivityStattement.getNetworkType()));
        holder.tvLevel.setText(convertLevel(connectivityStattement.getLevel()));
    }

    @Override
    public int getItemCount() {
        return connectivityStattements.size();
    }

    public void update() {
        connectivityStattements.clear();
        connectivityStattements.addAll(connectivityStattementsDao.getAll());
        notifyDataSetChanged();
    }

    private String convertNetworkType(int type) {
        String r = "Tipo de conexão móvel: ";

        switch (type) {
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_IDEN:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return r + "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return r + "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:
                return r + "4G";
            case TelephonyManager.NETWORK_TYPE_NR:
                return r + "5G";
            default:
                return r + "Desconhecido";
        }
    }

    private String convertLevel(int level) {
        if (level == CellSignalStrength.SIGNAL_STRENGTH_GOOD)
            return "Qualidade sinal: Bom (3)";
        if (level == CellSignalStrength.SIGNAL_STRENGTH_GREAT)
            return "Qualidade sinal: Ótimo (4)";
        if (level == CellSignalStrength.SIGNAL_STRENGTH_MODERATE)
            return "Qualidade sinal: Moderado (2)";
        if (level == CellSignalStrength.SIGNAL_STRENGTH_POOR)
            return "Qualidade sinal: Ruim (1)";

        return "Qualidade sinal: Desconhecido/Nenhum (0)";
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvSinalWifi, tvSinalMovel, tvLatitude, tvLongitude, tvTipoRede, tvLevel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvSinalWifi = itemView.findViewById(R.id.tvSinalWifi);
            tvSinalMovel = itemView.findViewById(R.id.tvSinalMovel);
            tvLatitude = itemView.findViewById(R.id.tvLatitude);
            tvLongitude = itemView.findViewById(R.id.tvLongitude);
            tvTipoRede = itemView.findViewById(R.id.tvTipoRede);
            tvLevel = itemView.findViewById(R.id.tvLevel);
        }
    }
}