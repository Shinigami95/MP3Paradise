package frags;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import adapters.MediaCursorAdapter;
import com.api.mp3paradise.R;

import java.util.Random;

public class CancionesFragment extends Fragment {

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
        return v;
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
            Cursor cursor = getContext().getContentResolver()
                    .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,null,null,null);

            if(cursor!= null){
                cursor.moveToFirst();
                adapter = new MediaCursorAdapter(getContext(),R.layout.lv_canciones_item,cursor);
                lvCanciones.setAdapter(adapter);
            }
        } else {
            throw new RuntimeException(getActivity().toString()
                    + " must implement OnInfoCentroFragmentInteractionListener");
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

    }

    public interface OnCancionesFragmentInteractionListener {
        void onCancionesFragmentInteraction(CancionesFragment frag, int mreqid, String[] args);
    }
}
