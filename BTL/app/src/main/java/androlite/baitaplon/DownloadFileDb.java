package androlite.baitaplon;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Zenphone on 4/13/2017.
 */

public class DownloadFileDb extends AppCompatActivity {
    private ProgressDialog pDialog;
    private static final int BUFFER_SIZE =8192 ;
    public static final int progress_bar_type = 0;
    private Setting db_setting;
    private DataDictionary db;
    private String file_url;
    DownloadFileDb(String fire_url)
    {
        this.file_url=fire_url;
    }
    public void Complete(){
        new DownloadFileFromURL().execute(file_url);
    }
    public static void unzip(String zipFile, String location) throws IOException {
        int size;
        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            if ( !location.endsWith("/") ) {
                location += "/";
            }
            File f = new File(location);
            if(!f.isDirectory()) {
                f.mkdirs();
            }
            ZipInputStream zin = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFile), BUFFER_SIZE));
            try {
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = location + ze.getName();
                    File unzipFile = new File(path);

                    if (ze.isDirectory()) {
                        if(!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {
                        // check for and create parent directories if they don't exist
                        File parentDir = unzipFile.getParentFile();
                        if ( null != parentDir ) {
                            if ( !parentDir.isDirectory() ) {
                                parentDir.mkdirs();
                            }
                        }

                        // unzip the file
                        FileOutputStream out = new FileOutputStream(unzipFile, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ( (size = zin.read(buffer, 0, BUFFER_SIZE)) != -1 ) {
                                fout.write(buffer, 0, size);
                            }

                            zin.closeEntry();
                        }
                        finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            }
            finally {
                zin.close();
            }
        }
        catch (Exception e) {
            Log.e("AAA", "Unzip exception", e);
        }
    }
    public class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(progress_bar_type);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;

            try {
                Log.d("AAA","1");
                URL url = new URL(f_url[0]);
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();
                Log.d("AAA","2");
                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                Log.d("AAA","3");
                // Output stream
                OutputStream output = new FileOutputStream("/data/data/androlite.baitaplon/ditionary.zip");
                Log.d("AAA","4");
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
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            dismissDialog(progress_bar_type);
            Log.d("AAA","DONE");


            try {
                unzip("/data/data/androlite.baitaplon/ditionary.zip","/data/data/androlite.baitaplon/");
                DataDictionary db1 = new DataDictionary(getApplication(),"anh_viet.db");
                DataDictionary db2 = new DataDictionary(getApplication(),"viet_anh.db");

                db1.copyDataBaseFromDownload("anh_viet.db");
                db2.copyDataBaseFromDownload("viet_anh.db");

                db_setting.updatefisttime();

            } catch (IOException e) {
                e.printStackTrace();
            }
            // setting downloaded into image view

//
//            try {
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            try {
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }



        }

    }
    public class DirectoryCleaner {
        private final File mFile;

        public DirectoryCleaner(File file) {
            mFile = file;
        }

        public void clean() {
            if (null == mFile || !mFile.exists() || !mFile.isDirectory()) return;
            for (File file : mFile.listFiles()) {
                delete(file);
            }
        }

        private void delete(File file) {
            if (file.isDirectory()) {
                for (File child : file.listFiles()) {
                    delete(child);
                }
            }
            file.delete();

        }
    }
    public static void deleteFiles(String path) {

        File file = new File(path);

        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(deleteCmd);
            } catch (IOException e) { }
        }
    }
    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }
}
