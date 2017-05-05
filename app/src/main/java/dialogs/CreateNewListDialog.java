package dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.api.mp3paradise.R;

public class CreateNewListDialog extends DialogFragment {

    public interface GestorCreateNewListDialog{
        void aceptarCreateNewList(String listName);
        void cancelarCreateNewList();
    }
    View view;
    GestorCreateNewListDialog gcnld;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        gcnld = (GestorCreateNewListDialog) getTargetFragment();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(getResources().getLayout(R.layout.dialog_new_list),null);
        builder.setView(view);
        //boton positivo
        builder.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText etListName = (EditText) view.findViewById(R.id.et_nombre_lista);
                String lName = etListName.getText().toString();
                if(!lName.equals("")) {
                    gcnld.aceptarCreateNewList(lName);
                } else {
                    Toast.makeText(getActivity(),getResources().getString(R.string.error_nombre_lista_vavio),Toast.LENGTH_LONG);
                }
            }
        });
        //boton negativo
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                gcnld.cancelarCreateNewList();
            }
        });
        return builder.create();
    }
}
