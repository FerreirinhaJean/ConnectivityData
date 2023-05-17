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

        if (type == TelephonyManager.NETWORK_TYPE_LTE) {
            return r + "4G";
        }
        if (type == TelephonyManager.NETWORK_TYPE_GSM) {
            return r + "2G";
        }
        if (type == TelephonyManager.NETWORK_TYPE_CDMA) {
            return r + "3G";
        }
        if (type == TelephonyManager.NETWORK_TYPE_GPRS) {
            return r + "GPRS";
        }
        if (type == TelephonyManager.NETWORK_TYPE_EDGE) {
            return r + "EDGE";
        }
        if (type == TelephonyManager.NETWORK_TYPE_UMTS) {
            return r + "UMTS";
        }
        if (type == TelephonyManager.NETWORK_TYPE_EVDO_0) {
            return r + "EVDO_0";
        }
        if (type == TelephonyManager.NETWORK_TYPE_EVDO_A) {
            return r + "EVDO_A";
        }
        if (type == TelephonyManager.NETWORK_TYPE_1xRTT) {
            return r + "1xRTT";
        }
        if (type == TelephonyManager.NETWORK_TYPE_HSDPA) {
            return r + "HSDPA";
        }
        if (type == TelephonyManager.NETWORK_TYPE_HSUPA) {
            return r + "HSUPA";
        }
        if (type == TelephonyManager.NETWORK_TYPE_HSPA) {
            return r + "HSPA";
        }
        if (type == TelephonyManager.NETWORK_TYPE_IDEN) {
            return r + "IDEN";
        }
        if (type == TelephonyManager.NETWORK_TYPE_EVDO_B) {
            return r + "EVDO_B";
        }
        if (type == TelephonyManager.NETWORK_TYPE_EHRPD) {
            return r + "EHRPD";
        }
        if (type == TelephonyManager.NETWORK_TYPE_HSPAP) {
            return r + "HSPAP";
        }
        if (type == TelephonyManager.NETWORK_TYPE_TD_SCDMA) {
            return r + "SCDMA";
        }
        if (type == TelephonyManager.NETWORK_TYPE_IWLAN) {
            return r + "IWLAN";
        }
        if (type == TelephonyManager.NETWORK_TYPE_NR) {
            return r + "NR";
        }


        return r + "Desconhecido";
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