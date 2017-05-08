package frags;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.api.mp3paradise.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import DB.DoHTTPRequest;
import adapters.ListasArrayAdapter;
import dialogs.CreateNewListDialog;
import model.ListCanciones;

public class ListasFragment extends Fragment implements DoHTTPRequest.AsyncResponse,
        CreateNewListDialog.GestorCreateNewListDialog {

    private OnListasFragmentInteractionListener mListener;

    private String user;
    public static final int REQUEST_INIT = 300;

    private ArrayList<ListCanciones> arrayListas;
    private ListView lvListas;
    private ListasArrayAdapter laa;

    public ListasFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_listas, container, false);
        Button btNewList = (Button) v.findViewById(R.id.bt_new_list);
        btNewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewList(v);
            }
        });

        arrayListas = new ArrayList<>();
        laa = new ListasArrayAdapter(getActivity(),arrayListas);
        lvListas = (ListView) v.findViewById(R.id.lv_lists);
        lvListas.setAdapter(laa);
        registerForContextMenu(lvListas);
        return v;
    }

    //Menu contextual que aparece al hacer click largo en un centro del list view (opciones eliminar y modificar)
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_lista_canciones, menu);
    }

    //Detectar si se ha pulsado eliminar en el menu contextual
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.it_eliminar_lista:
                eliminarLista(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    int posAEliminar = -1;
    private void eliminarLista(int pos){
        if(posAEliminar==-1) {
            posAEliminar = pos;
            int idLista = arrayListas.get(pos).id;
            DoHTTPRequest doHTTP = new DoHTTPRequest(this, getActivity(), -1);
            doHTTP.prepComandDeleteLista(idLista);
            doHTTP.execute();
        }
    }

    private void createNewList(View v){
        CreateNewListDialog newDialog = new CreateNewListDialog();
        newDialog.setTargetFragment(this,0);
        newDialog.show(getFragmentManager(),"Add Lista");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof OnListasFragmentInteractionListener) {
            mListener = (OnListasFragmentInteractionListener) getActivity();
            mListener.onListasFragmentInteraction(this,REQUEST_INIT,null);
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnInfoCentroFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListasFragmentInteractionListener) {
            mListener = (OnListasFragmentInteractionListener) context;
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

    public void actualizarFragment(String u){
        Log.d("ListasFragment","actualizarFragment :"+u);
        user = u;
        DoHTTPRequest doHTTP = new DoHTTPRequest(this,getActivity(),-1);
        doHTTP.prepComandGetListasUsu(user);
        doHTTP.execute();
    }

    @Override
    public void aceptarCreateNewList(String listName) {
        DoHTTPRequest doHTTP = new DoHTTPRequest(this,getActivity(),-1);
        doHTTP.prepComandAddLista(user,listName);
        doHTTP.execute();
    }

    @Override
    public void cancelarCreateNewList() {

    }

    @Override
    public void processFinish(String output, int mReqId) {
        try {
            if (mReqId == DoHTTPRequest.ADD_LISTA) {
                Log.d("ADD_LISTA",output);
                JSONObject json = new JSONObject(output);
                String status = json.getString("status");
                if(status.equals("ok")){
                    json = json.getJSONObject("lista");
                    int idLista = Integer.parseInt(json.getString("id"));
                    String nombreLista = json.getString("nombre");
                    ListCanciones lc = new ListCanciones(idLista,nombreLista,0);
                    arrayListas.add(lc);
                    laa.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(),"Error while adding the list",Toast.LENGTH_LONG).show();
                    Log.d("ADD_LISTA","ERROR: "+output);
                }
            } else if (mReqId == DoHTTPRequest.GET_LISTAS_USU) {
                Log.d("GET_LISTAS_USU",output);
                JSONObject json = new JSONObject(output);
                String status = json.getString("status");
                if(status.equals("ok")){
                    JSONArray jsonArray = json.getJSONArray("listas");
                    JSONObject jsonObj;
                    arrayListas.clear();
                    ListCanciones lc;
                    for(int i=0; i<jsonArray.length(); i++){
                        jsonObj = jsonArray.getJSONObject(i);
                        int idLista = Integer.parseInt(jsonObj.getString("id"));
                        String nombreLista = jsonObj.getString("nombre");
                        int numCanciones = Integer.parseInt(jsonObj.getString("num_canciones"));
                        lc = new ListCanciones(idLista,nombreLista,numCanciones);
                        arrayListas.add(lc);
                    }
                    laa.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(),"Error while loading the list",Toast.LENGTH_LONG).show();
                    Log.d("GET_LISTAS_USU","ERROR: "+output);
                }
            } else if (mReqId == DoHTTPRequest.DELETE_LISTA) {
                Log.d("DELETE_LISTA",output);
                JSONObject json = new JSONObject(output);
                String status = json.getString("status");
                if(status.equals("ok")){
                    arrayListas.remove(posAEliminar);
                    laa.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(),"Error while deleting the list",Toast.LENGTH_LONG).show();
                    Log.d("DELETE_LISTA","ERROR: "+output);
                }
                posAEliminar = -1;
            }
        } catch(JSONException e){
            e.printStackTrace();
        }
    }

    public interface OnListasFragmentInteractionListener {
        void onListasFragmentInteraction(ListasFragment frag, int mreqid, String[] args);
    }
}
