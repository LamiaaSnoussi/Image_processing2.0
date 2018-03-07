package com.example.lsnoussi.image_processing20;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements View.OnTouchListener, OnItemSelectedListener {


    private Bitmap originalBitmap;
    private Bitmap bmp;
    private Bitmap bmp_final;
    private ImageView imageView1;
    // variables for zoom :
       // 1-fingers
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    private static final String TAG = "Touch";
    @SuppressWarnings("unused")
    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;

      //2-create a matrix
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();
    int mode = NONE;
    //PointF are variables used to know which are the points on the screen that user touch
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;

    // variables : save and load a picture :
    private  static  final  int  CAMERA_REQUEST = 1888;
    private static int REQUEST_EXTERNAL_STORAGE = 1;
    public static String SaveFileName;
    static String Camera_PicturePath = "";
    static String Camera_PictureName = "";
    private static File f;
    private static int Take_Picture = 2;
    private static File gallery;
    private static int RESULT_LOAD_IMG = 1;



    @Override
    public void onCreate(Bundle savedInstanceState) {

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); //will ignore URI exposure for camera
        StrictMode.setVmPolicy(builder.build());


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inScaled = false;
        o.inMutable = true;


        originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.test, o);
        bmp = originalBitmap.copy(Bitmap.Config.ARGB_8888, true); // copy the original bitmap so we can reset it
        imageView1 = findViewById(R.id.imageView3);
        imageView1.setImageBitmap(originalBitmap);

        // Spinner element
        Spinner spinner = findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Menu");
        categories.add("Gray");
        categories.add("Colorize");
        categories.add("Dynamic_extension");
        categories.add("Histogram_gray");
        categories.add("Blurr");
        categories.add("Gauss_blurr");
        categories.add("OverExposure");
        categories.add("Histogram_rgb");
        categories.add("Flea");
        categories.add("Boost red");
        categories.add("Boost green");
        categories.add("Boost blue");
        categories.add("Reset the picture");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
        imageView1.setOnTouchListener(this);

        //seekbar for brightness

        SeekBar sb_value = findViewById(R.id.seekBar);
        sb_value.setProgress(40);
        sb_value.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

           imageView1.setColorFilter(Effects.setBrightness(progress));

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    });

        Button QuitButton = findViewById(R.id.button_quit);
        QuitButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                finish();
            }
        });


         String[] PERMISSIONS_STORAGE = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        Button cameraButton = findViewById(R.id.button_camera);
        cameraButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                picture_Camera();
            }
        });

        Button galleryButton = findViewById(R.id.button_gallery);
        galleryButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                picture_Gallery(v);
            }
        });

        Button saveButton = findViewById(R.id.button_save);
        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                save();
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        switch(item){
            case("Gray"):
                Effects.toGray(originalBitmap);
                imageView1.setImageBitmap(originalBitmap);
                Toast.makeText(parent.getContext(), "apply gray filter", Toast.LENGTH_LONG).show();
                break;
            case("Colorize"):
                Effects.colorize(originalBitmap);
                imageView1.setImageBitmap(originalBitmap);
                 Toast.makeText(parent.getContext(), "apply a colored filter", Toast.LENGTH_LONG).show();
                 break;

            case("Dynamic_extension"):
                Effects.dynamic_extension(originalBitmap);
                imageView1.setImageBitmap(originalBitmap);
                Toast.makeText(parent.getContext(), "linear contrast", Toast.LENGTH_LONG).show();
                break;

            case("Histogram_gray"):
                Effects.histogramEqualization_gray(originalBitmap);
                imageView1.setImageBitmap(originalBitmap);
                Toast.makeText(parent.getContext(), "contrast gray", Toast.LENGTH_LONG).show();
                break;

            case("OverExposure"):
                Effects.over_exposure(originalBitmap);
                imageView1.setImageBitmap(originalBitmap);
                Toast.makeText(parent.getContext(), "over_exposure", Toast.LENGTH_LONG).show();
                break;

            case("Histogram_rgb"):
                Effects.histogramEqualization_RGB(originalBitmap);
                imageView1.setImageBitmap(originalBitmap);
                Toast.makeText(parent.getContext(), "contrast color", Toast.LENGTH_LONG).show();
                break;

            case("Blurr"):
                Convolution.Moyenneur(originalBitmap);
                imageView1.setImageBitmap(originalBitmap);
                Toast.makeText(parent.getContext(), "blurr", Toast.LENGTH_LONG).show();
                break;

            case("Gauss_blurr"):
                Convolution.Gauss_convolution(originalBitmap);
                imageView1.setImageBitmap(originalBitmap);
                Toast.makeText(parent.getContext(), "gaussian blurr", Toast.LENGTH_LONG).show();
                break;

            case("Flea"):
                Effects.FleaEffect(originalBitmap);
                imageView1.setImageBitmap(originalBitmap);
                Toast.makeText(parent.getContext(), "Flea", Toast.LENGTH_LONG).show();
                break;

            case("Boost red"):
                Effects.boost(originalBitmap,1,40);
                imageView1.setImageBitmap(originalBitmap);
                Toast.makeText(parent.getContext(), "boost red color", Toast.LENGTH_LONG).show();
                break;

             case("Boost green"):
                Effects.boost(originalBitmap,2,30);
                imageView1.setImageBitmap(originalBitmap);
                Toast.makeText(parent.getContext(), "boost green color", Toast.LENGTH_LONG).show();
                break;

              case("Boost blue"):
                Effects.boost(originalBitmap,3,60);
                imageView1.setImageBitmap(originalBitmap);
                Toast.makeText(parent.getContext(), "boost blue color", Toast.LENGTH_LONG).show();
                break;

            case("Reset the picture"):
                originalBitmap = bmp.copy(Bitmap.Config.ARGB_8888, true);
                imageView1.setImageBitmap(originalBitmap);
                imageView1.setColorFilter(Effects.setBrightness(100));
                Toast.makeText(parent.getContext(), "the picture has been reset", Toast.LENGTH_LONG).show();
                break;



        }

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


    /**
     * Function that allows the use of the camera to process a picture if the permission is granted
     The picture is saved in a file named "CAM APP" in your phone
     it requires API 23 or higher
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
      public void picture_Camera() {

        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            // creation of folder to put the image take with camera
            SaveFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CAM APP";
            gallery = new File(SaveFileName);
            if (!gallery.exists())
                gallery.mkdirs();

                // saving the picture
            Camera_PictureName = "Picture" + ".jpg";
            Camera_PicturePath = SaveFileName + "/" + "PictureApp" + ".jpg";
            System.err.println(" Camera_Photo_ImagePath  " + Camera_PicturePath);
            f= new File(Camera_PicturePath);
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(
                    MediaStore.EXTRA_OUTPUT, Uri.fromFile(f)), Take_Picture);
            System.err.println("f" + f);
             System.out.println("f" + SaveFileName);

        } else {
            requestPermissions(
                    new String[]{Manifest.permission.CAMERA
                    }, CAMERA_REQUEST);
             System.out.println("erreur");
        }
    }

    /**
     * function that asks for permission to look in the gallery using function uploadFromGallery to
     process a picture from the gallery of your phone
     * it needs API 23 or higher
     * @param v
     */
    @RequiresApi(api = Build.VERSION_CODES.M)

    public void picture_Gallery(View v) {

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            UploadFromGallery(v);
        }else {
            requestPermissions(
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE
                    }, REQUEST_EXTERNAL_STORAGE);
        }

    }


    /**
     * The function allows the app to upload a picture from the gallery
     * @param view
     *
     */

    public void UploadFromGallery(View view) {


        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Start of the intent
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }


    /**
     * save the processed picture in pictures in files directory
     */

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void save() {
        // Verification of permissions
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Bitmap bitmap = ((BitmapDrawable) imageView1.getDrawable()).getBitmap();

            ContentResolver cr = getContentResolver();
            String title = "Bitmap";
            String description = " New Bitmap";
            String savedURL = MediaStore.Images.Media.insertImage(cr, bitmap, title, description);

            Toast.makeText(MainActivity.this, savedURL, Toast.LENGTH_LONG).show();
        } else {
            requestPermissions(
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, REQUEST_EXTERNAL_STORAGE);
        }
    }

    /**
     *function that gives the result of uploading a picture from the gallery or taking it with the camera.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {

            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                // This part treats the code that allows the upload of an image from the gallery


                // Upload of the image
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Uri selectedPicture = data.getData();

                Cursor cursor = getContentResolver().query(selectedPicture,
                        filePathColumn, null, null, null);

                // Setting the cursor on the first column
                cursor.moveToFirst();

                // decode and save the image

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                bmp = BitmapFactory.decodeFile(picturePath);
                bmp = bmp.copy(Bitmap.Config.ARGB_8888,true);
                bmp_final = bmp.copy(Bitmap.Config.ARGB_8888,true);
                imageView1.setImageBitmap(bmp);


            } else if (requestCode == Take_Picture) { //from camera
                String filePath = Camera_PicturePath;
                if (filePath != null) {

                    bmp = (new_decode(new File(filePath)));

                    // Resize the image
                    int newHeight = (int) (bmp.getHeight() * (512/ bmp.getWidth()));
                    Bitmap putImage = Bitmap.createScaledBitmap(bmp, 512, newHeight, true);

                    // Saving the image in bmp_final

                    bmp_final = putImage.copy(Bitmap.Config.ARGB_8888,true);
                    imageView1.setImageBitmap(putImage);
                }
                else{
                    bmp = null;
                }
                 } else {
                Toast.makeText(this, "error, please select a picture !", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "ERROR", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * function that decodes a file containing the picture that you want to process
     * @author  stackOverFlow
     * @param f  = file
     * @return a bitmzp
     */

      public Bitmap new_decode(File f) {

        int targetW = imageView1.getWidth();
        int targetH = imageView1.getHeight();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        // scale value
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scaleFactor = Math.min(width_tmp / targetW, height_tmp / targetH);

        // decode with inSampleSize
        try {
            o.inJustDecodeBounds = false;
            o.inSampleSize = scaleFactor;
            o.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o);
            imageView1.setImageBitmap(bitmap);
            return bitmap;

        } catch (OutOfMemoryError e) {
            // TODO: handle exception
            e.printStackTrace();
            System.gc();
            return null;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }


    /**
     * function that uses motion event to zoom or scroll a picture
     * @author  stackOverFlow
     * @param v
     * @param event
     * @return boolean
     */

    public boolean onTouch(View v, MotionEvent event) {
        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        dumpEvent(event);  // Handle touch events here...

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:   // first finger down only
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG"); // write to LogCat
                mode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_UP: // second finger lifted

                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // first and second finger down

                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (mode == DRAG) { // scroll
                    matrix.set(savedMatrix);
                    // create the transformation in the matrix  of points
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                } else if (mode == ZOOM) {
                    // pinch zooming
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist; // setting the scaling of the
                        // matrix...if scale > 1 means
                        // zoom in...if scale < 1 means
                        // zoom out
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // display the transformation on screen

        return true; // indicate event was handled
    }

    /**
     * function that calculates the space between two fingers on the screen
     * @param event
     * @return a float
     */

    public float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getY(1);
        float y = event.getY(0) - event.getY(1);
        float F = (x * x + y * y) * (x * x + y * y);
        return F;
    }


    /**
     * function that calculates the point in the middle between two fingers on the screen
     * @param point
     * @param event
     */
    public void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getY(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }


    /**
     * function that shows an event in the LogCat view, for debugging
     * @param event
     */

    public void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);

        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }

        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX());
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }

        sb.append("]");
        Log.d("Touch Events ---------", sb.toString());
    }



}