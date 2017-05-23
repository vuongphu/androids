package androlite.baitaplon;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Fragment fragment = null;
    private FragmentManager fragmentManager;
    private ProgressDialog pDialog;
    private static final int BUFFER_SIZE =8192 ;
    public static final int progress_bar_type = 0;
    private DataDictionary db;
    private static String file_url = "https://dictionnary.000webhostapp.com/data/ditionary.zip";
    private Setting db_setting;
    private boolean checkitn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        db_setting =new Setting(this);
//
//        Log.d("BBB",db_setting.getFisttime()+" !");
//        db_setting.updatefisttime();
//        Log.d("BBB",db_setting.getFisttime()+" @");
        checkitn=isNetworkAvailable(this);
        Log.d("BBB",checkitn+" @");
        Log.d("BBB",db_setting.getFisttime()+" @");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
//        Toast.makeText(this,checkitn+"    1", Toast.LENGTH_SHORT).show();
        if (db_setting.getFisttime()==0  )
        {
            if(checkitn ==true)
            {
                new DownloadFileFromURL().execute(file_url);
                Log.d("BBB",db_setting.getFisttime()+" @");
            }
            else{
                builder.setMessage("Please Connect Internet For Download");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TryDownload();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
//            new DownloadFileDb.DownloadFileFromURL.execute(file_url);
//            DownloadFileDb a =new DownloadFileDb(file_url);
//            a.Complete();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        displayView(0); // call search fragment.



    }
    public void TryDownload()
    {
        Boolean checkitn=isNetworkAvailable(this);
        if( checkitn ==true) {
            new DownloadFileFromURL().execute(file_url);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please Connect Internet For Download");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    TryDownload();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    public boolean isNetworkAvailable(Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
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
    class DownloadFileFromURL extends AsyncTask<String, String, String> {

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

            // Displaying downloaded image into image view
            // Reading image path from sdcard
//            String imagePath ="/data/data/com.example.gil.downloadfile/downloadedfile.jpg";

            try {
                unzip("/data/data/androlite.baitaplon/ditionary.zip","/data/data/androlite.baitaplon/");
                DataDictionary db1 = new DataDictionary(getApplication(),"anh_viet.db");
                DataDictionary db2 = new DataDictionary(getApplication(),"viet_anh.db");
                db1.copyDataBaseFromDownload("anh_viet.db");
                db2.copyDataBaseFromDownload("viet_anh.db");
                db_setting.updatefisttime();
                Log.d("AAA","DONE Change");
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
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading file. Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setCanceledOnTouchOutside(false);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
        public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_anhviet) {
            setTitle(R.string.label_anh_viet);
            // Handle the camera action
        } else if (id == R.id.nav_vietanh) {
            setTitle(R.string.label_viet_anh);

        } else if (id == R.id.nav_fav_av) {
            setTitle(R.string.label_fav_av);
//            fragment = new FragmentFav();
//
//            Log.d("GGG","AAA");

        } else if (id == R.id.nav_fav_va) {
            setTitle(R.string.label_fav_va);
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
        }

        displayView(0); // call search fragment.

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayView(0);
    }

    private void displayView(int position) {
        fragment = null;
        String fragmentTags = "";
        switch (position) {
            case 0:

                fragment = new SearchFragment();
                Bundle args = new Bundle();
                args.putString("DONE",getTitle().toString());
                fragment.setArguments(args);
                break;

            default:
                break;
        }

        if (fragment != null) {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, fragmentTags).commit();
        }
    }
}
