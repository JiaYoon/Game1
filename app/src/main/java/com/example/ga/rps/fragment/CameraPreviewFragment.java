package com.example.ga.rps.fragment;

import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ga.rps.GameActivity;
import com.example.ga.rps.R;
import com.example.ga.rps.view.CameraPreview;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by GA on 2018. 4. 13..
 */

public class CameraPreviewFragment extends Fragment {

    private final static int CAMERA_FACING = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private final static int RESULT_LOAD_IMG = 0x999;

    private CameraPreview cameraPreview;
    private Camera camera;

    private ViewGroup cameraContainer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera_preview, container, false);

        cameraContainer = view.findViewById(R.id.camera_preview);
        setCamera();

        View capture = view.findViewById(R.id.btnCapture);
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera == null) return;
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    public void onPictureTaken(byte[] data, Camera camera) {
                        File pictureFile = getOutputMediaFile();
                        if (pictureFile == null) {
                            return;
                        }
                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(data);
                            fos.close();
                        } catch (IOException ignored) {
                        }

                        Intent intent = new Intent(getActivity(), GameActivity.class);
                        intent.putExtra("path", pictureFile.getAbsolutePath());
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
    }

    public void setCamera() {
        camera = getCameraInstance();
        cameraPreview = new CameraPreview(getContext(), camera);
        cameraContainer.addView(cameraPreview);
    }

    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();              // release the camera immediately on pause event
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
