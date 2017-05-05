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
import java.util.zip.Inflater;

import model.ListCanciones;

/**
 * Created by Jorge on 05/05/2017.
 */

public class ListasArrayAdapter extends BaseAdapter {

    private ArrayList<ListCanciones> listaListCanciones;
    private Context context;
    private LayoutInflater inflater;

    public ListasArrayAdapter(Activity activity, ArrayList<ListCanciones> lista){
        context = activity;
        listaListCanciones = lista;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return listaListCanciones.size();
    }

    @Override
    public Object getItem(int position) {
        return listaListCanciones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listaListCanciones.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;
        rowView = inflater.inflate(context.getResources().getLayout(R.layout.lv_listas_canciones_item),null);

        TextView tvNombreLista = (TextView) rowView.findViewById(R.id.tv_nombre_lista);
        TextView tvNumCancionesLista = (TextView) rowView.findViewById(R.id.tv_num_canciones);

        tvNombreLista.setText(listaListCanciones.get(position).name);
        tvNumCancionesLista.setText(listaListCanciones.get(position).numCanciones+"");
        return rowView;
    }
}
