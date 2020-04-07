package com.kreatifapp.android;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import id.zelory.compressor.Compressor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PhotoHandler implements PictureCallback {

    private final Context context;
    private int savePhoto;
    public PhotoHandler(Context context, int savePhoto) {
        this.context = context;
        this.savePhoto = savePhoto;
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

        File pictureFileDir = getDir();

        if (!pictureFileDir.exists() && !pictureFileDir.mkdirs()) {

            Log.d(MainActivity.DEBUG_TAG, "Can't create directory to save image.");
            Toast.makeText(context, "Can't create directory to save image.",
                    Toast.LENGTH_LONG).show();
            return;

        }

        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        //String date = dateFormat.format(new Date());
        String photoFile = "tmp.jpg";

        String filename = pictureFileDir.getPath() + File.separator + photoFile;

        File pictureFile = new File(filename);

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
            //String filename2 = decodeFile(filename);
            File newfile = new Compressor(context).compressToFile(pictureFile);
            
            File mFolder = getDir();
            
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        	String date = dateFormat.format(new Date());
        
            String s = "Picture_" + date + ".jpg";

            File f = new File(mFolder.getAbsolutePath(), s);
            if(savePhoto == 1)
                copyFile(newfile, f);
            //Compressor.getDefault(context).compressToFile(pictureFile);
            //Toast.makeText(context, "New Image saved:" + newfile.getAbsolutePath(), Toast.LENGTH_LONG).show();
            //String status = MyService.uploadFile(filename);
            //Toast.makeText(context, "Status:" + status, Toast.LENGTH_LONG).show();
            new RequestTask().execute(newfile.getAbsolutePath());
        } catch (Exception error) {
            Log.d(MainActivity.DEBUG_TAG, "File" + filename + "not saved: "
                    + error.getMessage());
            Toast.makeText(context, "Image could not be saved.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private File getDir() {
        File sdDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return new File(sdDir, "CameraAPIDemo");
    }

    class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            String responseString = null;
            MyService.uploadFile(uri[0]);
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
        }
    }
    
    
    
    public static void copyFile(File afile, File bfile)
    {	
    	
    	InputStream inStream = null;
	OutputStream outStream = null;
		
    	try{
    		    		
    	    inStream = new FileInputStream(afile);
    	    outStream = new FileOutputStream(bfile);
        	
    	    byte[] buffer = new byte[1024];
    		
    	    int length;
    	    //copy the file content in bytes 
    	    while ((length = inStream.read(buffer)) > 0){
    	  
    	    	outStream.write(buffer, 0, length);
    	 
    	    }
    	 
    	    inStream.close();
    	    outStream.close();
    	      
    	    System.out.println("File is copied successful!");
    	    
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
}
