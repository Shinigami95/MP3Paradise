package frags;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.ListView;
import android.widget.Toast;

import com.api.mp3paradise.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import DB.DoHTTPRequest;
import DB.DownloadFileFromURL;
import adapters.DownloadArrayAdapter;
import model.DownloadCancion;

public class DownloadFragment extends Fragment implements DoHTTPRequest.AsyncResponse{

    private OnDownloadFragmentInteractionListener mListener;

    private String user;
    private ListView lvDownload;

    public static final int REQUEST_INIT = 300;

    public DownloadFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_download, container, false);
        lvDownload = (ListView) v.findViewById(R.id.lv_descargas);
        lvDownload.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.showContextMenu();
            }
        });
        registerForContextMenu(lvDownload);
        return v;
    }

    //Menu contextual que aparece al hacer click largo en un centro del list view (opciones eliminar y modificar)
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_download, menu);
    }

    //Detectar si se ha pulsado eliminar en el menu contextual
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.it_download:
                Log.d("MENUITEM","it_download");
                View v = adapterDC.getView(info.position,null,lvDownload);
                int idCancion = (Integer) v.getTag();
                DoHTTPRequest doHTTP = new DoHTTPRequest(this,getActivity(),-1);
                doHTTP.prepComandGetDownloadPath(idCancion,user);
                doHTTP.execute();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof OnDownloadFragmentInteractionListener) {
            mListener = (OnDownloadFragmentInteractionListener) getActivity();
            mListener.onDownloadFragmentInteraction(this,REQUEST_INIT,null);
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnInfoCentroFragmentInteractionListener");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDownloadFragmentInteractionListener) {
            mListener = (OnDownloadFragmentInteractionListener) context;
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
        user = u;
        DoHTTPRequest doHTTP = new DoHTTPRequest(this,getActivity(),-1);
        doHTTP.prepComandGetDownloadList();
        doHTTP.execute();
    }

    ArrayList<DownloadCancion> arrayDC;
    DownloadArrayAdapter adapterDC;
    @Override
    public void processFinish(String output, int mReqId) {
        try{
            Log.d("DOWNLOAD",output);
            if(mReqId==DoHTTPRequest.GET_DOWNLOAD_LIST){
                JSONObject json = new JSONObject(output);
                String status = json.getString("status");
                if(status.equals("ok")){
                    JSONArray jsonArray = json.getJSONArray("canciones");
                    JSONObject jsonObj;
                    arrayDC = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObj = jsonArray.getJSONObject(i);
                        int idCan = Integer.parseInt(jsonObj.getString("id"));
                        String nombreCan = jsonObj.getString("nombre");
                        String duracionCan = jsonObj.getString("duracion");
                        DownloadCancion dc = new DownloadCancion(idCan,nombreCan,duracionCan);
                        arrayDC.add(dc);
                    }
                    adapterDC = new DownloadArrayAdapter(getActivity(),arrayDC);
                    lvDownload.setAdapter(adapterDC);
                } else {
                    Toast.makeText(getContext(),"Error while downloading the song",Toast.LENGTH_LONG).show();
                }
            } else if(mReqId==DoHTTPRequest.GET_DOWNLOAD_PATH){
                JSONObject json = new JSONObject(output);
                String status = json.getString("status");
                if(status.equals("ok")){
                    String nombreFich = json.getString("path");
                    Log.d("DOWNLOAD","A DESCARGAR");
                    DownloadFileFromURL dffurl = new DownloadFileFromURL(getActivity(),nombreFich);
                    dffurl.execute();
                } else {
                    Toast.makeText(getContext(),"Error while downloading the song",Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    public interface OnDownloadFragmentInteractionListener {
        void onDownloadFragmentInteraction(DownloadFragment frag, int mreqid, String[] args);
    }
}
