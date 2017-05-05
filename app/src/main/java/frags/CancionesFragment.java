package frags;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import DB.DoHTTPRequest;
import adapters.CancionesArrayAdapter;
import adapters.ListasArrayAdapter;
import adapters.MediaCursorAdapter;
import dialogs.SelectListDialog;
import model.Cancion;

import com.api.mp3paradise.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class CancionesFragment extends Fragment implements DoHTTPRequest.AsyncResponse,
        SelectListDialog.GestorSelectListDialog{

    private OnCancionesFragmentInteractionListener mListener;
    private ListView lvCanciones;
    private ListAdapter adapter;

    private String user;

    public static final int REQUEST_INIT = 300;
    public static final int REQUEST_SONG_PATH = 301;

    public CancionesFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_canciones, container, false);
        lvCanciones = (ListView) v.findViewById(R.id.lv_canciones);
        lvCanciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                playCancion(parent,view,position,id);
            }
        });
        registerForContextMenu(lvCanciones);
        return v;
    }

    private static final int MENU_ADD_CANCION_LISTA_STATE = 300;
    private static final int MENU_DELETE_CANCION_LISTA_STATE = 301;
    private int menuState = MENU_ADD_CANCION_LISTA_STATE;


    //Menu contextual que aparece al hacer click largo en un centro del list view (opciones eliminar y modificar)
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_canciones, menu);
        MenuItem itAddCancionLista = menu.findItem(R.id.it_add_a_lista);
        MenuItem itRemoveCancionLista = menu.findItem(R.id.it_quitar_de_lista);
        if(menuState==MENU_ADD_CANCION_LISTA_STATE){
            itAddCancionLista.setVisible(true);
            itRemoveCancionLista.setVisible(false);
        } else if(menuState==MENU_DELETE_CANCION_LISTA_STATE){
            itAddCancionLista.setVisible(false);
            itRemoveCancionLista.setVisible(true);
        }
    }

    private int posCancionAEliminar = -1;

    //Detectar si se ha pulsado eliminar en el menu contextual
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.it_add_a_lista:
                Log.d("MENUITEM","it_add_a_lista");
                SelectListDialog sld = new SelectListDialog();
                Bundle bund = new Bundle();
                bund.putInt("pos",info.position);
                sld.setArguments(bund);
                sld.setTargetFragment(this,0);
                sld.show(getFragmentManager(),"sel_list");

                return true;
            case R.id.it_quitar_de_lista:
                if(posCancionAEliminar==-1) {
                    Log.d("MENUITEM", "it_quitar_de_lista");
                    Cancion can = arrayCanciones.get(info.position);
                    int idCan = can.id;
                    posCancionAEliminar = info.position;

                    DoHTTPRequest doHTTP = new DoHTTPRequest(this, getActivity(), -1);
                    doHTTP.prepComandDeleteCancionLista(idCan);
                    doHTTP.execute();
                }
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void playCancion(AdapterView<?> parent, View view, int position, long id) {
        String path = (String) view.getTag();
        String[] args = {path};
        mListener.onCancionesFragmentInteraction(this,REQUEST_SONG_PATH,args);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof OnCancionesFragmentInteractionListener) {
            mListener = (OnCancionesFragmentInteractionListener) getActivity();
            mListener.onCancionesFragmentInteraction(this, REQUEST_INIT ,null);
            getLocalMusic();
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnInfoCentroFragmentInteractionListener");
        }
    }

    private void getLocalMusic(){
        Cursor cursor = getContext().getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,null,null,null);

        if(cursor!= null){
            cursor.moveToFirst();
            adapter = new MediaCursorAdapter(getContext(),R.layout.lv_canciones_item,cursor);
            lvCanciones.setAdapter(adapter);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCancionesFragmentInteractionListener) {
            mListener = (OnCancionesFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public String getCancionAleatoria(){
        Random rn = new Random();
        int pos = rn.nextInt(adapter.getCount());
        View v = adapter.getView(pos,null,lvCanciones);
        String path = (String) v.getTag();
        return path;
    }

    public void actualizarFragment(String u){
        user = u;
        DoHTTPRequest doHTTP = new DoHTTPRequest(this,getActivity(),-1);
        doHTTP.prepComandGetListasUsu(user);
        doHTTP.execute();
    }

    private ArrayList<Integer> listaSpinerIds;
    private ArrayList<String> listaSpinerNames;

    private ArrayList<Integer> listaIds;
    private ArrayList<String> listaNames;

    private ArrayList<Cancion> arrayCanciones;

    @Override
    public void processFinish(String output, int mReqId) {
        try {
            if (mReqId == DoHTTPRequest.GET_LISTAS_USU) {
                Log.d("GET_LISTAS_USU", output);
                JSONObject json = new JSONObject(output);
                String status = json.getString("status");
                if (status.equals("ok")) {
                    JSONArray jsonArray = json.getJSONArray("listas");
                    JSONObject jsonObj;
                    listaSpinerIds = new ArrayList<>();
                    listaSpinerNames = new ArrayList<>();
                    listaIds = new ArrayList<>();
                    listaNames = new ArrayList<>();

                    listaSpinerNames.add(getResources().getString(R.string.spin_all_canciones));
                    listaSpinerIds.add(-1);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObj = jsonArray.getJSONObject(i);
                        int idLista = Integer.parseInt(jsonObj.getString("id"));
                        String nombreLista = jsonObj.getString("nombre");
                        listaSpinerIds.add(idLista);
                        listaSpinerNames.add(nombreLista);
                        listaIds.add(idLista);
                        listaNames.add(nombreLista);
                    }
                    Spinner spin = (Spinner) getView().findViewById(R.id.sp_listas);
                    ArrayAdapter<String> spinAA = new ArrayAdapter<String>(getActivity(),R.layout.spin_listas,listaSpinerNames);
                    spin.setAdapter(spinAA);
                    spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            onSpinnerItemSelected(parent,view,position,id);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else {
                    //TODO error
                }
            } else if (mReqId == DoHTTPRequest.GET_CANCIONES_LISTA){
                Log.d("GET_CANCIONES_LISTA", output);
                JSONObject json = new JSONObject(output);
                String status = json.getString("status");
                if (status.equals("ok")) {
                    JSONArray jsonArray = json.getJSONArray("canciones");
                    JSONObject jsonObj;
                    arrayCanciones = new ArrayList<>();
                    Cancion can;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObj = jsonArray.getJSONObject(i);
                        int idCan = Integer.parseInt(jsonObj.getString("id"));
                        String nombreCan = jsonObj.getString("nombre");
                        String pathCan = jsonObj.getString("path");
                        String duracionCan = jsonObj.getString("duracion");

                        can = new Cancion(idCan,nombreCan,pathCan,duracionCan);

                        arrayCanciones.add(can);
                    }
                    adapter = new CancionesArrayAdapter(getActivity(),arrayCanciones);
                    lvCanciones.setAdapter(adapter);
                } else {
                    //TODO error
                    arrayCanciones = new ArrayList<>();
                    adapter = new CancionesArrayAdapter(getActivity(),arrayCanciones);
                    lvCanciones.setAdapter(adapter);
                }
            } else if (mReqId == DoHTTPRequest.GET_CANCIONES_LISTA){
                Log.d("GET_CANCIONES_LISTA", output);
                JSONObject json = new JSONObject(output);
                String status = json.getString("status");
                if (status.equals("ok")) {
                    JSONArray jsonArray = json.getJSONArray("canciones");
                    JSONObject jsonObj;
                    arrayCanciones = new ArrayList<>();
                    Cancion can;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObj = jsonArray.getJSONObject(i);
                        int idCan = Integer.parseInt(jsonObj.getString("id"));
                        String nombreCan = jsonObj.getString("nombre");
                        String pathCan = jsonObj.getString("path");
                        String duracionCan = jsonObj.getString("duracion");

                        can = new Cancion(idCan,nombreCan,pathCan,duracionCan);

                        arrayCanciones.add(can);
                    }
                    adapter = new CancionesArrayAdapter(getActivity(),arrayCanciones);
                    lvCanciones.setAdapter(adapter);
                } else {
                    //TODO error
                    arrayCanciones = new ArrayList<>();
                    adapter = new CancionesArrayAdapter(getActivity(),arrayCanciones);
                    lvCanciones.setAdapter(adapter);
                }
            }else if (mReqId == DoHTTPRequest.ADD_CANCION_LISTA){
                Log.d("GET_CANCIONES_LISTA", output);
                JSONObject json = new JSONObject(output);
                String status = json.getString("status");
                if (status.equals("ok")) {
                    //TODO add correcto
                } else {
                    //TODO error
                }
            }else if (mReqId == DoHTTPRequest.DELETE_CANCION_LISTA){
                if(adapter instanceof CancionesArrayAdapter){
                    arrayCanciones.remove(posCancionAEliminar);
                    ((CancionesArrayAdapter) adapter).notifyDataSetChanged();
                }
                posCancionAEliminar = -1;
                Log.d("GET_CANCIONES_LISTA", output);
                JSONObject json = new JSONObject(output);
                String status = json.getString("status");
                if (status.equals("ok")) {
                    //TODO add correcto
                } else {
                    //TODO error
                }
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public void onSpinnerItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //String listaName = listaSpinerNames.get(position);
        int listaId = listaSpinerIds.get(position);

        if(listaId==-1){
            menuState = MENU_ADD_CANCION_LISTA_STATE;
            getActivity().invalidateOptionsMenu();
            getLocalMusic();
            //TODO lvCanciones.createContextMenu();
        } else {
            menuState = MENU_DELETE_CANCION_LISTA_STATE;
            getActivity().invalidateOptionsMenu();
            DoHTTPRequest doHTTP = new DoHTTPRequest(this,getActivity(),-1);
            doHTTP.prepComandGetCancionesLista(listaId);
            doHTTP.execute();
        }
    }

    @Override
    public void aceptarSelectList(String listName, int listId, int position) {
        Log.d("LISTA SELECT",listName+" -> "+listId);
        View v = adapter.getView(position,null,lvCanciones);
        int idLista = listId;

        TextView tvNameCancion = (TextView) v.findViewById(R.id.displayname);
        String nombre = tvNameCancion.getText().toString();

        TextView tvDuracionCancion = (TextView) v.findViewById(R.id.duration);
        String duracion = tvDuracionCancion.getText().toString();

        String path = (String) v.getTag();

        DoHTTPRequest doHTTP = new DoHTTPRequest(this,getActivity(),-1);
        doHTTP.prepComandAddCancionLista(idLista,nombre,path,duracion);
        doHTTP.execute();
    }

    @Override
    public void cancelarSelectList() {

    }

    @Override
    public ArrayList<Integer> getListasIDS() {
        return listaIds;
    }

    @Override
    public ArrayList<String> getListasNAMES() {
        return listaNames;
    }

    public interface OnCancionesFragmentInteractionListener {
        void onCancionesFragmentInteraction(CancionesFragment frag, int mreqid, String[] args);
    }
}
