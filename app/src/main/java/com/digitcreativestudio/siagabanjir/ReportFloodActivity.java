package com.digitcreativestudio.siagabanjir;

/**
 * Created by Afifatul on 8/2/2015.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.digitcreativestudio.siagabanjir.utils.JSONParser;
import com.digitcreativestudio.siagabanjir.utils.MyLocationListener;
import com.digitcreativestudio.siagabanjir.utils.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportFloodActivity extends ActionBarActivity{

    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;
    Location location;

    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    final static int SELECTED_IMAGE_ACTIVITY_REQUEST_CODE = 2;
    static String Path;
    Uri imageUri                      = null;
    public  static ImageView showImg  = null;
    ReportFloodActivity CameraActivity = null;
    int serverResponseCode = 0;
    ProgressDialog dialog = null;
  //  String upLoadServerUri = "http://api.vhiefa.net76.net/siagabanjir/upload_photos.php";
    String upLoadServerUri = "http://demo.edusarana.com/sis/ws/upload_photos.php";
    String photo_url, id_user;
    int status_upload_img;
    String deskripsi, latitude="", longitude="";
    JSONParser jsonParser = new JSONParser();
    EditText inputDesc;
    Button photo, btnLaporBanjir;
    //private static String url_lapor_banjir = "http://api.vhiefa.net76.net/siagabanjir/lapor_banjir.php";
    private static String url_lapor_banjir = "http://demo.edusarana.com/sis/ws/lapor_banjir.php";
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    SessionManager session;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_flood);
        Path = null;
        CameraActivity = this;
        showImg = (ImageView) findViewById(R.id.showImg);
        photo = (Button) findViewById(R.id.photo);
        inputDesc = (EditText) findViewById(R.id.inputDesc);
        btnLaporBanjir = (Button) findViewById(R.id.btnUpload);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        session = new SessionManager(getApplicationContext());


        if (session.isLoggedIn() == true){
            HashMap<String, String> user = session.getUserDetails();
            id_user = user.get(SessionManager.KEY_ID);
            String nama = user.get(SessionManager.KEY_NAME);
            btnLogin.setText(nama);
        }

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);   //default

        criteria.setCostAllowed(false);
        // get the best provider depending on the criteria
        provider = locationManager.getBestProvider(criteria, false);

        // the last known location of this provider
        location = locationManager.getLastKnownLocation(provider);

        mylistener = new MyLocationListener(ReportFloodActivity.this);

        if (location != null) {
            mylistener.onLocationChanged(location);
            latitude = String.valueOf(mylistener.getLatitude());
            longitude = String.valueOf(mylistener.getLongitude());
        /*    Toast.makeText(
                    getApplicationContext(),
                    "Lat : "+latitude+
                            "Long : "+longitude,
                    Toast.LENGTH_LONG).show(); */
        } else {
            // leads to the settings because there is no last known location
            showSettingsAlert(provider);
        }
        // location updates: at least 10 meter and 3 minutes change
        locationManager.requestLocationUpdates(provider, 1000*60*3, 10, mylistener);

        photo.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                selectImage();
            }
        });

        btnLaporBanjir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deskripsi = inputDesc.getText().toString();
                if (Path==null){
                    Toast.makeText(
                            getApplicationContext(),
                            "Harap masukan gambar!",
                            Toast.LENGTH_LONG).show();
                }
                else if (deskripsi.isEmpty()){
                    Toast.makeText(
                            getApplicationContext(),
                            "Harap masukan deskripsi gambar!",
                            Toast.LENGTH_LONG).show();
                }
                else if ((latitude.equals(""))||(longitude.equals(""))){
                    Toast.makeText(
                            getApplicationContext(),
                            "Lokasi Anda sedang dicari! Tunggu Sebentar dan Coba lagi!",
                            Toast.LENGTH_LONG).show();
                }

                else if (id_user==null){
                    Toast.makeText(
                            getApplicationContext(),
                            "Silahkan login terlebih dulu",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    new ReportFloodActivityAsyncTask().execute();
                }
            }
        });

        ActionBar bar = getSupportActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#C62828")));

    }


    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ReportFloodActivity.this);
        builder.setTitle("Insert Picture!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    // Define the file-name to save photo taken by Camera activity
                    String fileName = "photo.jpg";
                    // Create parameters for Intent with filename
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, fileName);
                    values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
                    // imageUri is the current activity attribute, define and save it for later usage  
                    imageUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);// EXTERNAL_CONTENT_URI : style URI for the "primary" external storage volume.
                    Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                    startActivityForResult( intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            SELECTED_IMAGE_ACTIVITY_REQUEST_CODE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data)
    {
        if ( requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if ( resultCode == RESULT_OK) {
                String imageId = convertImageUriToFile( imageUri,CameraActivity);
                new LoadImagesFromSDCardAsyncTask().execute(""+imageId);
            }  else {
                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == SELECTED_IMAGE_ACTIVITY_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                imageUri = data.getData();
                String imageId = convertImageUriToFile( imageUri,CameraActivity);
                new LoadImagesFromSDCardAsyncTask().execute(""+imageId);
            }else {
                Toast.makeText(this, " Picture was not taken ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /************ Convert Image Uri path to physical path a.k.a get path **************/
    public static String convertImageUriToFile ( Uri imageUri, Activity activity )  {
        Cursor cursor = null;
        int imageID = 0;
        int thumbID = 0;
        try {
            /*********** Which columns values want to get *******/
            String [] proj={
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID
            };
            cursor = activity.managedQuery(
                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name) 
            );
            //  Get Query Data
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);

            int size = cursor.getCount();
            if (size == 0) { // If size is 0, there are no images on the SD Card. 
               // imageDetails.setText("No Image");
            }
            else
            {

                if (cursor.moveToFirst()) {
                    imageID     = cursor.getInt(columnIndex);
                    thumbID     = cursor.getInt(columnIndexThumb);
                    Path = cursor.getString(file_ColumnIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return ""+thumbID; // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )
    }


    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ReportFloodActivity.this);

        alertDialog.setTitle(provider + " setting");

        alertDialog
                .setMessage(provider + " belum aktif, silahkan aktifkan terlebih dulu.");

        alertDialog.setPositiveButton("Setting",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        ReportFloodActivity.this.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Batal",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    public class LoadImagesFromSDCardAsyncTask  extends AsyncTask<String, Void, Void> {
        private ProgressDialog Dialog = new ProgressDialog(ReportFloodActivity.this);
        Bitmap mBitmap;
        protected void onPreExecute() {
            Dialog.setMessage(" Loading image...");
            Dialog.show();
        }
        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            Uri uri = null;
            try {
                /**  Uri.withAppendedPath Method Description Parameters
                 *    baseUri  Uri to append path segment to pathSegment  encoded path segment to append
                 *    Returns a new Uri based on baseUri with the given segment appended to the path
                 */
                uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + urls[0]);
                /**************  Decode an input stream into a bitmap. *********/
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                if (bitmap != null) {
                    /********* Creates a new bitmap, scaled from an existing bitmap. ***********/
                    newBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                    bitmap.recycle();
                    if (newBitmap != null) {
                        mBitmap = newBitmap;
                    }
                }
            } catch (IOException e) {
                // Error fetching image, try to recover
                runOnUiThread(new Runnable() {
                    public void run() {
                        //    messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(ReportFloodActivity.this, "Erorr.. Try again :) ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                //cancel(true);
            }
            return null;
        }

        protected void onPostExecute(Void unused) {
            Dialog.dismiss();
            if(mBitmap != null)
            {
                // Set Image to ImageView  
                showImg.setImageBitmap(mBitmap);
            }
        }
    }

    class ReportFloodActivityAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ReportFloodActivity.this);
            dialog.setMessage("Lapor Banjir...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        //void postToDatabase(){
        protected String doInBackground(String... args) {

            uploadPhoto(Path);
            if (status_upload_img ==1) {

                String namaFile_img = Path.substring(Path.lastIndexOf("/")+1);
                //photo_url = "http://api.vhiefa.net76.net/siagabanjir/photos/"+namaFile_img;
                photo_url = "http://demo.edusarana.com/sis/ws/photos/"+namaFile_img;

                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id_user", id_user));
                params.add(new BasicNameValuePair("deskripsi", deskripsi));
                params.add(new BasicNameValuePair("photo_url", photo_url));
                params.add(new BasicNameValuePair("latitude", latitude));
                params.add(new BasicNameValuePair("longitude", longitude));

                JSONObject json = jsonParser.makeHttpRequest(url_lapor_banjir, "POST", params);

                Log.d("Create Response", json.toString());

                // check for success tag
                try {
                    int success = json.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // successfully created Post
                        Path=null;


                        // closing this screen
                        finish();
                    } else {
                        return "gagal_database";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "gagal_koneksi_or_exception";
                }
            }
            else{

                return "photo_gagal";

            }

            return "sukses";

        }

        public int uploadPhoto(String sourceFileUri) {

            String fileName = sourceFileUri;

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);

            if (!sourceFile.isFile()) {
                Log.e("uploadPhoto", "Source File not exist :" +Path);
                return 0;
            }
            else{
                try {
                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST"); // Set HTTP method to POST
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + fileName + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.i("uploadPhoto", "HTTP Response is : "+ serverResponseMessage + ": " + serverResponseCode);

                    if(serverResponseCode == 200){ //File Upload Completed

                        status_upload_img =1;
                    }
                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                } catch (MalformedURLException ex) {
                    dialog.dismiss();
                    ex.printStackTrace();
                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {

                    dialog.dismiss();
                    e.printStackTrace();
                    Log.e("Upload Exception", "Exception :"+ e.getMessage(), e);
                }
                return serverResponseCode;
            }
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if (result.equalsIgnoreCase("gagal_database")){
                Toast.makeText(ReportFloodActivity.this, "Terjadi masalah! Silahkan cek koneksi Anda!",  Toast.LENGTH_SHORT).show();
            }
            else if (result.equalsIgnoreCase("gagal_koneksi_or_exception")){
                Toast.makeText(ReportFloodActivity.this, "Terjadi masalah! Silahkan cek koneksi Anda!",  Toast.LENGTH_SHORT).show();
            }
            else if (result.equalsIgnoreCase("photo_gagal")){
                Toast.makeText(ReportFloodActivity.this, "Terjadi masalah! Silahkan cek koneksi Anda!",  Toast.LENGTH_SHORT).show();
            }
            else if (result.equalsIgnoreCase("sukses")){
                Toast.makeText(ReportFloodActivity.this, "Sukses!",  Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (session.isLoggedIn() == true){

            HashMap<String, String> user = session.getUserDetails();
            id_user = user.get(SessionManager.KEY_ID);
            String nama = user.get(SessionManager.KEY_NAME);
            btnLogin.setText(nama);
        }
    }
}


