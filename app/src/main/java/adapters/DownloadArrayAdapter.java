package adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.api.mp3paradise.R;

import java.util.ArrayList;

import model.DownloadCancion;

/**
 * Created by Jorge on 05/05/2017.
 */

public class DownloadArrayAdapter extends BaseAdapter {

    private ArrayList<DownloadCancion> listaDownload;
    private Context context;
    private LayoutInflater inflater;

    public DownloadArrayAdapter(Activity activity, ArrayList<DownloadCancion> lista){
        context = activity;
        listaDownload = lista;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listaDownload.size();
    }

    @Override
    public Object getItem(int position) {
        return listaDownload.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listaDownload.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = inflater.inflate(context.getResources().getLayout(R.layout.lv_canciones_item),null);

        TextView tvDisplayname = (TextView) rowView.findViewById(R.id.displayname);
        TextView tvDuration = (TextView) rowView.findViewById(R.id.duration);

        tvDisplayname.setText(listaDownload.get(position).nombre);
        tvDuration.setText(listaDownload.get(position).duracion+"");
        rowView.setTag(listaDownload.get(position).id);
        return rowView;
    }
}
