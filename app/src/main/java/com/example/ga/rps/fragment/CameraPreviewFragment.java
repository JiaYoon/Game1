package com.example.ga.rps.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ga.rps.GameActivity;
import com.example.ga.rps.MainActivity;
import com.example.ga.rps.R;
import com.example.ga.rps.view.CameraPreview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by GA on 2018. 4. 13..
 */

public class CameraPreviewFragment extends Fragment {

    private final static int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private final static int RESULT_LOAD_IMG = 0x999;

    private CameraPreview cameraPreview;
    private Camera camera;

    private ViewGroup cameraContainer;


    private SensorManager mSensorManager;
    private Sensor accelerometer;
    private int degree = 0;

    private final SensorEventListener mEventListener = new SensorEventListener() {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            // Handle the events for which we registered
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    float x = event.values[0];
                    float y = event.values[1];

                    if (x < 5 && x > -5 && y > 5)
                        degree = 0;
                    else if (x < -5 && y < 5 && y > -5)
                        degree = 90;
                    else if (x < 5 && x > -5 && y < -5)
                        degree = 180;
                    else if (x > 5 && y < 5 && y > -5)
                        degree = 270;
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera_preview, container, false);

        cameraContainer = view.findViewById(R.id.camera_preview);
        setCamera();

        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        View capture = view.findViewById(R.id.btnCapture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera == null) return;
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    public void onPictureTaken(byte[] data, Camera camera) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                        boolean isVerticalImage = bitmap.getWidth() < bitmap.getHeight();

                        Log.d("asd", degree + " ");
                        if (isVerticalImage) {
                            Matrix matrix = new Matrix();
                            matrix.postRotate(90);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                        }

                        int imageWidth = bitmap.getWidth();
                        int imageHeight = cameraContainer.getMeasuredWidth() * camera.getParameters().getPictureSize().width / cameraContainer.getMeasuredHeight();
                        bitmap = Bitmap.createBitmap(bitmap
                                , 0 //X 시작위치 (원본의 4/1지점)
                                , (bitmap.getHeight() - imageHeight) //Y 시작위치 (원본의 4/1지점)
                                , imageWidth// 넓이 (원본의 절반 크기)
                                , imageHeight); // 높이 (원본의 절반 크기)


                        Matrix matrix = new Matrix();
                        matrix.postRotate((270 - degree) % 360);
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);


                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream); // bmp is your Bitmap instance
                        String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "MyCameraApp/IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg", null);


                        bitmap.recycle();

                        Intent intent = new Intent(getActivity(), GameActivity.class);
                        intent.putExtra("uri", path);
                        startActivity(intent);
                    }
                });
            }
        });

        view.findViewById(R.id.btn_gallery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        });

        view.findViewById(R.id.btnModeSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showGameMode();
            }
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent(getActivity(), GameActivity.class);
            intent.putExtra("uri", data.getData().toString());
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }


    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(CAMERA_FACING); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onResume() {
        super.onResume();
        mSensorManager.registerListener(mEventListener, accelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    public void setCamera() {
        camera = getCameraInstance();
        cameraPreview = new CameraPreview(getActivity(), camera);
        cameraContainer.addView(cameraPreview);
    }

    @Override
    public void onPause() {
        mSensorManager.unregisterListener(mEventListener);
        releaseCamera();              // release the camera immediately on pause event
        super.onPause();
    }

    public void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();        // release the camera for other applications
            camera = null;

            cameraPreview.getHolder().removeCallback(cameraPreview);
            cameraPreview = null;

            cameraContainer.removeAllViews();
        }
    }
}
