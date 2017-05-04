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
    public static final int GET_LISTA_CENTROS = 102;
    public static final int GET_INFO_CENTRO = 103;
    public static final int GET_CARRERAS_CENTRO = 104;
    public static final int ADD_CENTRO = 105;
    public static final int ADD_CARRERA = 106;
    public static final int DELETE_CENTRO = 107;
    public static final int DELETE_CARRERA = 108;
    public static final int MODIFICAR_CENTRO = 109;
    public static final int GET_INFO_USER = 110;
    public static final int ADD_FOTO_USER = 111;
    public static final int GET_FOTO = 112;

    public DoHTTPRequest(AsyncResponse deleg, Context context, int progressBarId) {

        delegate = deleg;
        mContext = context;
        mProgressBarId = progressBarId;
        errorMessage = "";

    }

    public void prepComandAddFotoUser(String nombre, String foto){
        try {
            mReqId = ADD_FOTO_USER;
            param = "func=addFotoUser";
            param += "&nombre=" + URLEncoder.encode(nombre, "UTF-8");
            param += "&foto=" + URLEncoder.encode(foto, "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandGetFoto(String path){
        try {
            mReqId = GET_FOTO;
            param = "func=getFoto";
            param += "&path=" + URLEncoder.encode(path, "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandLogin(String nombre, String pass){
        try {
            mReqId = LOG_IN;
            param = "func=login";
            param += "&nombre=" + URLEncoder.encode(nombre, "UTF-8");
            param += "&pass=" + URLEncoder.encode(pass, "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandGetInfoUser(String nombre){
        try {
            mReqId = GET_INFO_USER;
            param = "func=getInfoUser";
            param += "&nombre=" + URLEncoder.encode(nombre, "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandRegister(String nombre, String pass, String fechanacimiento, String email){
        try {
            mReqId = REGISTER;
            param = "func=register";
            param += "&nombre=" + URLEncoder.encode(nombre, "UTF-8");
            param += "&pass=" + URLEncoder.encode(pass, "UTF-8");
            param += "&fechanacimiento=" + URLEncoder.encode(fechanacimiento, "UTF-8");
            param += "&email=" + URLEncoder.encode(email, "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandGetListaCentros(){
        mReqId = GET_LISTA_CENTROS;
        param = "func=getListaCentros";
    }

    public void prepComandGetInfoCentro(String id){
        try {
            mReqId = GET_INFO_CENTRO;
            param = "func=getInfoCentro";
            param += "&id=" + URLEncoder.encode(id, "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandGetCarrerasCentro(String id){
        try{
            mReqId = GET_CARRERAS_CENTRO;
            param = "func=getCarrerasCentro";
            param += "&id=" + URLEncoder.encode(id, "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandAddCentro(String nombre, String siglas, String url, double lat, double lon){
        try {
            mReqId = ADD_CENTRO;
            param = "func=addCentro";
            param += "&nombre=" + URLEncoder.encode(nombre, "UTF-8");
            param += "&siglas=" + URLEncoder.encode(siglas, "UTF-8");
            param += "&url=" + URLEncoder.encode(url, "UTF-8");
            param += "&lat=" + URLEncoder.encode(lat+"", "UTF-8");
            param += "&lon=" + URLEncoder.encode(lon+"", "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandAddCarrera(String nombre, String centro, String fechamatricula){
        try {
            mReqId = ADD_CARRERA;
            param = "func=addCarrera";
            param += "&nombre=" + URLEncoder.encode(nombre, "UTF-8");
            param += "&centro=" + URLEncoder.encode(centro, "UTF-8");
            param += "&fechamatricula=" + URLEncoder.encode(fechamatricula, "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandDeleteCentro(String id){
        try {
            mReqId = DELETE_CENTRO;
            param = "func=deleteCentro";
            param += "&id=" + URLEncoder.encode(id+"", "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandDeleteCarrera(String id){
        try {
            mReqId = DELETE_CARRERA;
            param = "func=deleteCarrera";
            param += "&id=" + URLEncoder.encode(id, "UTF-8");
        } catch(UnsupportedEncodingException e){

        }
    }

    public void prepComandModificarCentro(String id, String nombre, String siglas, String url, double lat, double lon){
        try {
            mReqId = MODIFICAR_CENTRO;
            param = "func=modificarCentro";
            param += "&id=" + URLEncoder.encode(id, "UTF-8");
            param += "&nombre=" + URLEncoder.encode(nombre, "UTF-8");
            param += "&siglas=" + URLEncoder.encode(siglas, "UTF-8");
            param += "&url=" + URLEncoder.encode(url, "UTF-8");
            param += "&lat=" + URLEncoder.encode(lat+"", "UTF-8");
            param += "&lon=" + URLEncoder.encode(lon+""+"", "UTF-8");
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

        String targetURLstr = "http://galan.ehu.eus/jperez134/WEB/index_gcu.php";
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