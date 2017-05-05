package DB;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Created by shind on 02/05/2017.
 */

public class DoHTTPRequest extends AsyncTask<String, Void, String> {

public interface AsyncResponse {
    void processFinish(String output, int mReqId);
}
    public AsyncResponse delegate=null;

    private Context mContext;
    private int mReqId;
    private String param = "";
    private ProgressBar mProgressBar = null;
    private int mProgressBarId;
    private HttpURLConnection urlConnection = null;
    private String errorMessage = "";

    public static final int LOG_IN = 100;
    public static final int REGISTER = 101;
    public static final int GET_LISTAS_USU = 102;
    public static final int GET_DOWNLOAD_LIST = 103;
    public static final int ADD_LISTA = 104;
    public static final int DELETE_LISTA = 105;
    public static final int ADD_CANCION_LISTA = 106;
    public static final int DELETE_CANCION_LISTA = 107;
    public static final int GET_CANCIONES_LISTA = 108;


    public DoHTTPRequest(AsyncResponse deleg, Context context, int progressBarId) {
        delegate = deleg;
        mContext = context;
        mProgressBarId = progressBarId;
        errorMessage = "";
    }

    public void prepComandLogin(String user, String pass){
        try {
            mReqId = LOG_IN;
            param = "func=login";
            param += "&user=" + URLEncoder.encode(user, "UTF-8");
            param += "&pass=" + URLEncoder.encode(pass, "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandRegister(String user, String pass){
        try {
            mReqId = REGISTER;
            param = "func=register";
            param += "&user=" + URLEncoder.encode(user, "UTF-8");
            param += "&pass=" + URLEncoder.encode(pass, "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandGetListasUsu(String user){
        try {
            mReqId = GET_LISTAS_USU;
            param = "func=get_listas_usu";
            param += "&user=" + URLEncoder.encode(user, "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandAddLista(String user, String nombreLista){
        try {
            mReqId = ADD_LISTA;
            param = "func=add_lista";
            param += "&user=" + URLEncoder.encode(user, "UTF-8");
            param += "&nombre_lista=" + URLEncoder.encode(nombreLista, "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandDeleteLista(int idLista){
        try {
            mReqId = DELETE_LISTA;
            param = "func=delete_lista";
            param += "&id_lista=" + URLEncoder.encode(idLista+"", "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandGetCancionesLista(int idLista){
        try {
            mReqId = GET_CANCIONES_LISTA;
            param = "func=get_canciones_lista";
            param += "&id_lista=" + URLEncoder.encode(idLista+"", "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandAddCancionLista(int idLista, String nombre, String path, String duracion){
        try {
            mReqId = ADD_CANCION_LISTA;
            param = "func=add_cancion_lista";
            param += "&id_lista=" + URLEncoder.encode(idLista+"", "UTF-8");
            param += "&nombre=" + URLEncoder.encode(nombre+"", "UTF-8");
            param += "&path=" + URLEncoder.encode(path+"", "UTF-8");
            param += "&duracion=" + URLEncoder.encode(duracion+"", "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandDeleteCancionLista(int idCancion){
        try {
            mReqId = DELETE_CANCION_LISTA;
            param = "func=delete_cancion_lista";
            param += "&id_cancion=" + URLEncoder.encode(idCancion+"", "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (mProgressBarId != -1) {
            mProgressBar = (ProgressBar) ((Activity) mContext).findViewById(mProgressBarId);
            mProgressBar.setVisibility(ProgressBar.VISIBLE);
        }
    }

    @Override
    protected String doInBackground(String... params) {

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (!(netInfo != null && netInfo.isConnected())) {
            errorMessage = "No Internet Connection";
            Log.d("DoHHTTPRequest","ERROR - "+errorMessage);
            return errorMessage;
        }

        String targetURLstr = "http://galan.ehu.eus/jperez134/WEB/mp3paradise/indexmp3paradise.php";
        InputStream inputStream;
        try {
            URL targetURL = new URL(targetURLstr);
            urlConnection = (HttpURLConnection) targetURL.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setRequestProperty("Accept-Language", Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry());
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setDoOutput(true);

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(param);
            out.close();

            int statusCode = urlConnection.getResponseCode();
            Log.d("DoHTTPRequest","Status: "+statusCode);
            if (statusCode == 200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line;
                String result = "";
                while((line = bufferedReader.readLine()) != null){
                    result += line;
                }
                inputStream.close();
                String response = result;
                return response;
            }
            else{
                errorMessage = "Error al conectar con el servidor: "+statusCode;
                urlConnection.disconnect();
                return errorMessage;
            }
        } catch (Exception e) {
            errorMessage = "Error al conectar a Internet";
            return errorMessage;
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
    }

    @Override
    protected void onPostExecute(final String result) {
        delegate.processFinish(result, mReqId);
        if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
    }

    @Override
    protected void onCancelled() {
        if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
    }

}