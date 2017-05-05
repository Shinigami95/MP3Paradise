package dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.api.mp3paradise.R;

import java.util.ArrayList;

/**
 * Created by Jorge on 05/05/2017.
 */

public class SelectListDialog extends DialogFragment {

    public interface GestorSelectListDialog{
        void aceptarSelectList(String listName, int listId, int position);
        void cancelarSelectList();
        ArrayList<Integer> getListasIDS();
        ArrayList<String> getListasNAMES();
    }
    View view;
    GestorSelectListDialog gsld;

    ArrayList<Integer> listasIDS;
    ArrayList<String> listasNAMES;
    int pos;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        gsld = (GestorSelectListDialog) getTargetFragment();
        listasIDS = gsld.getListasIDS();
        listasNAMES = gsld.getListasNAMES();
        pos = getArguments().getInt("pos");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(getResources().getLayout(R.layout.dialog_select_lista),null);
        builder.setView(view);
        Spinner spin = (Spinner) view.findViewById(R.id.spin_lista);
        ArrayAdapter<String> spinAA = new ArrayAdapter<>(getActivity(),R.layout.spin_listas,listasNAMES);
        spin.setAdapter(spinAA);
        //boton positivo
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Spinner spin = (Spinner) view.findViewById(R.id.spin_lista);
                int itemPosition = spin.getSelectedItemPosition();
                String ln = listasNAMES.get(itemPosition);
                int lid = listasIDS.get(itemPosition);
                gsld.aceptarSelectList(ln,lid,pos);
            }
        });
        //boton negativo
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                gsld.cancelarSelectList();
            }
        });
        return builder.create();
    }
}
