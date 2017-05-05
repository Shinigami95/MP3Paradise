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

import model.Cancion;
import model.ListCanciones;

/**
 * Created by Jorge on 05/05/2017.
 */

public class CancionesArrayAdapter  extends BaseAdapter {

    private ArrayList<Cancion> listaCanciones;
    private Context context;
    private LayoutInflater inflater;

    public CancionesArrayAdapter(Activity activity, ArrayList<Cancion> lista){
        context = activity;
        listaCanciones = lista;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listaCanciones.size();
    }

    @Override
    public Object getItem(int position) {
        return listaCanciones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listaCanciones.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = inflater.inflate(context.getResources().getLayout(R.layout.lv_canciones_item),null);

        TextView tvDisplayname = (TextView) rowView.findViewById(R.id.displayname);
        TextView tvDuration = (TextView) rowView.findViewById(R.id.duration);

        tvDisplayname.setText(listaCanciones.get(position).nombre);
        tvDuration.setText(listaCanciones.get(position).duracion+"");
        rowView.setTag(listaCanciones.get(position).path);
        return rowView;
    }
}
