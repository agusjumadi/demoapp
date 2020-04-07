package com.kreatifapp.android;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedWriter;
import java.io.FileWriter;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MyService extends Service {
    private Timer timer;
    private TimerTask timerTask;
    private Context ctx;
    public int countDuration = 60;
    public int savePhoto = 1;
    final static String CHAT_ID = "xxxxxxxxx"; 
    final static String BOT_URL = "https://api.telegram.org/<BOT_API>";
    //@Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    //private BroadcastReceiver mMessageReceiver = new ReceiverCall();
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = "capture";
            int value = 0;
            String val = "0";
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                action = bundle.getString("action");
                val =  bundle.getString("value");
            }
		value = Integer.parseInt(val);	
            appendLog("FIREBASE EVT ... "+action+"/"+val);
            //CapturePhoto();
            
            if(action.equals("duration"))
            {
                if(value > 0){
                    countDuration = value;
                    stoptimertask();
                    startTimer();
                }
            }
        	else if(action.equals("capture"))
            {
                appendLog("Response capturing...");
                CapturePhoto();
            }
            else if(action.equals("save"))
            {
                savePhoto = value;
            }
              
        }
    };
    
    /*@Override
    public void onCreate() {
        super.onCreate();
        //CapturePhoto();
        ctx = this;
        Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_LONG).show();
        //startService();
        startTimer();
    }*/
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        appendLog("Starting...");
        Toast.makeText(getApplicationContext(), "Service started", Toast.LENGTH_LONG).show();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver), new IntentFilter("myfirebasedata"));
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        appendLog("Destroy...");
        Log.i("EXIT", "ondestroy!");
	   LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        //Intent broadcastIntent = new Intent("ac.in.ActivityRecognition.RestartSensor");
        //sendBroadcast(broadcastIntent);
        stoptimertask();
    }
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();
        appendLog("Start "+countDuration+"s");
        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, countDuration * 1000); //
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
               // Log.i("in timer", "in timer ++++  " + (counter++));
                //Toast.makeText(getApplicationContext(), "Capturing photo", Toast.LENGTH_LONG).show();
                appendLog("Capturing...");
                CapturePhoto();
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    public void CapturePhoto() {

        Log.d("kkkk","Preparing to take photo");
        Camera camera = null;

        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();

        int frontCamera = 1;
        //int backCamera=0;

        Camera.getCameraInfo(frontCamera, cameraInfo);

        try {
            camera = Camera.open(frontCamera);
        } catch (RuntimeException e) {
            Log.d("kkkk","Camera not available: " + 1);
            camera = null;
            appendLog("Camera not available: ");
            //e.printStackTrace();
        }
        try {
            if (null == camera) {
                Log.d("kkkk","Could not get camera instance");
                appendLog("Could not get camera instance");
            } else {
                Log.d("kkkk","Got the camera, creating the dummy surface texture");
                try {
                    camera.setPreviewTexture(new SurfaceTexture(0));
                    camera.startPreview();
                } catch (Exception e) {
                    Log.d("kkkk","Could not set the surface preview texture");
                    e.printStackTrace();
                }
                camera.takePicture(null, null,
                                new PhotoHandler(getApplicationContext(), savePhoto));
                /*camera.takePicture(null, null, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        File pictureFileDir=new File("/sdcard/CaptureByService");

                        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {
                            pictureFileDir.mkdirs();
                        }
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
                        String date = dateFormat.format(new Date());
                        String photoFile = "ServiceClickedPic_" + "_" + date + ".jpg";
                        String filename = pictureFileDir.getPath() + File.separator + photoFile;
                        File mainPicture = new File(filename);

                        try {
                            FileOutputStream fos = new FileOutputStream(mainPicture);
                            fos.write(data);
                            fos.close();
                            Log.d("kkkk","image saved");
                        } catch (Exception error) {
                            Log.d("kkkk","Image could not be saved");
                        }
                        camera.release();
                    }
                });*/
            }
        } catch (Exception e) {
            camera.release();
        }
    }
    
	public static void appendLog(String text)
{       
	File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                
        String filename = sdDir.getPath() + File.separator + "foto.txt";
   File logFile = new File(filename);
   if (!logFile.exists())
   {
      try
      {
         logFile.createNewFile();
      } 
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
   try
   {
      //BufferedWriter for performance, true to set append to file flag
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
      String date = dateFormat.format(new Date());
                        
      BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 
      buf.append(date+" "+text);
      buf.newLine();
      buf.close();
   }
   catch (IOException e)
   {
      // TODO Auto-generated catch block
      e.printStackTrace();
   }
}

    public static String sendData(String msg) 
    {
        String data = "chat_id=" + CHAT_ID; 
 
        data += "&parse_mode=Markdown"; 

        data += "&text="+msg;


        String text = "";
        BufferedReader reader=null;

        // Send data 
        try
        { 

            // Defined URL  where to send data
            URL url = new URL(BOT_URL+"/sendMessage");
               
            // Send POST data request

            URLConnection conn = url.openConnection(); 
            conn.setDoOutput(true); 
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream()); 
            wr.write( data ); 
            wr.flush(); 

            // Get the server response 
             
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                 // Append server response in string
                 sb.append(line + "\n");
            }
              
              
            text = sb.toString();
        }
        catch(Exception ex)
        {
            text = ex.getMessage();
        }
        finally
        {
            try
            {
                reader.close();
            }

            catch(Exception ex) {}
        }
                
        return text;
    }    

    public static String uploadFile(String sourceFileUri)
    {
        String fileName = sourceFileUri;
        int serverResponseCode = 0;
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

            return "0 nofile";

        }
        else
        {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                String url2 = BOT_URL + "/sendPhoto?chat_id="+CHAT_ID;

//String url2 = "http://192.168.43.53/upload.php";
                URL url = new URL(url2);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"photo\";filename=\""
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

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();
                System.out.println("code: "+serverResponseCode+" "+serverResponseMessage);

            } catch (MalformedURLException ex) {

                return "1 "+ ex.getMessage();
            } catch (Exception e) {
                return "2 "+ e.getMessage()+" "+sourceFileUri;

            }
            //dialog.dismiss();
            return serverResponseCode+" OK";

        } // End else block
    }
}
