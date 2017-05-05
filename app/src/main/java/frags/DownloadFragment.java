package frags;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.api.mp3paradise.R;

public class DownloadFragment extends Fragment {

    private OnDownloadFragmentInteractionListener mListener;

    private String user;

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
        return v;
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

    }

    public interface OnDownloadFragmentInteractionListener {
        void onDownloadFragmentInteraction(DownloadFragment frag, int mreqid, String[] args);
    }
}
