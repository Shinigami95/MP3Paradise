package DB;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Jorge on 05/05/2017.
 */

// Fuente: http://www.androidhive.info/2012/04/android-downloading-file-by-showing-progress-bar/
public class DownloadFileFromURL extends AsyncTask<String, String, String> {

    private final String serverURL = "http://galan.ehu.eus/jperez134/WEB/mp3paradise/canciones/";
    private Activity context;
    private String nombreFich;
    private ProgressDialog pd;
    private String filePath;

    public DownloadFileFromURL(Activity activity, String nF){
        context = activity;
        nombreFich = nF;
    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     * */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(context);
        pd.setMessage("Downloading file. Please wait...");
        pd.setIndeterminate(false);
        pd.setMax(100);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCancelable(true);
        pd.show();
        Log.d("DownloadFileFromURL","onPreExecute");
    }

    /**
     * Downloading file in background thread
     * */
    @Override
    protected String doInBackground(String... f_url) {
        Log.d("DownloadFileFromURL","doInBackground");
        int count;
        try {
            URL url = new URL(serverURL+nombreFich.replace(" ","%20"));
            Log.d("DownloadFileFromURL",url.toString());
            URLConnection conection = url.openConnection();
            conection.connect();
            // getting file length
            int lenghtOfFile = conection.getContentLength();

            // input stream to read file - with 8k buffer
            InputStream input = new BufferedInputStream(url.openStream(), 8192);

            // Output stream to write file
            filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)+"/"+nombreFich;
            OutputStream output = new FileOutputStream(filePath);
            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                // publishing the progress....
                // After this onProgressUpdate will be called
                publishProgress(""+(int)((total*100)/lenghtOfFile));

                // writing data to file
                output.write(data, 0, count);
            }

            // flushing output
            output.flush();

            // closing streams
            output.close();
            input.close();

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Updating progress bar
     * */
    protected void onProgressUpdate(String... progress) {
        // setting progress percentage
        pd.setProgress(Integer.parseInt(progress[0]));
    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     * **/
    @Override
    protected void onPostExecute(String file_url) {
        // dismiss the dialog after the file was downloaded
        pd.dismiss();

        // Displaying downloaded image into image view
        // Reading image path from sdcard
        MediaScannerConnection.scanFile(
                context,
                new String[]{filePath},
                null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Log.d("REGISTERES_MEDIA",
                                "file " + path + " was scanned seccessfully: " + uri);
                    }
                });

        String notTitle = nombreFich;
        String notMsg = filePath;
        NotificationCompat.Builder elconstructor =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setContentTitle(notTitle)
                        .setContentText(notMsg)
                        .setAutoCancel(true)
                        .setTicker(notTitle);
        NotificationManager elnotificationmanager = (NotificationManager)
                context.getSystemService(context.getApplicationContext().NOTIFICATION_SERVICE);
        elnotificationmanager.notify(1, elconstructor.build());

    }

}