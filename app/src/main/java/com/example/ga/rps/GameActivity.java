package com.example.ga.rps;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ga.rps.data.HandType;
import com.example.ga.rps.util.ScreenUtils;
import com.example.ga.rps.view.HandView;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private List<HandView> handViews = new ArrayList<>();
    private SparseArray<Face> faces;

    private int loser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String path = getIntent().getStringExtra("path");
        final String uriPath = getIntent().getStringExtra("uri");

        Bitmap originalBitmap;

        if (path != null) {
            originalBitmap = BitmapFactory.decodeFile(path);
        } else {
            final InputStream imageStream;
            try {
                imageStream = getContentResolver().openInputStream(Uri.parse(uriPath));
                originalBitmap = BitmapFactory.decodeStream(imageStream);
            } catch (FileNotFoundException e) {
                Log.d("asd", "file not found");
                finish();
                return;
            }
        }

        ImageView myImageView = findViewById(R.id.imgView);

        int screenHeight = ScreenUtils.getScreenSize().heightPixels;

        Matrix fitParentMatrix = new Matrix();
        float ratio = (float) screenHeight / originalBitmap.getHeight();
        fitParentMatrix.setScale(-ratio, ratio);
        Bitmap myBitmap = Bitmap.createBitmap(originalBitmap, 0, 0,
                originalBitmap.getWidth(), originalBitmap.getHeight(), fitParentMatrix, false);

        FaceDetector faceDetector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        if (!faceDetector.isOperational()) {
            new AlertDialog.Builder(this).setMessage("couldn't set up the face detector!");
            return;
        }

        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
        faces = faceDetector.detect(frame);

        faceDetector.release();

        myImageView.setImageDrawable(new BitmapDrawable(getResources(), myBitmap));

        findViewById(R.id.btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.btn_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sendShareIntent()) {
                    (new saveImage()).execute(true);
                }

            }
        });

        findViewById(R.id.btn_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri != null) {
                    finishSaveAction();
                    return;
                }

                ImageView img = findViewById(R.id.img_save);
                img.setImageResource(R.drawable.loading_img);
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.ratate_btn);
                img.setAnimation(animation);
                (new saveImage()).execute(false);
            }
        });


        int size = faces.size();
        if (size <= 0) {
            Toast.makeText(this, "아무도 없어요 ㅠㅠ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (size == 1) {
            Toast.makeText(this, "친구와 함께해요 ㅠㅠ", Toast.LENGTH_SHORT).show();
            return;
        }

        final ViewGroup playPlain = findViewById(R.id.play_plain);
        final Random random = new Random();

        int width = myBitmap.getWidth();
        int height = myBitmap.getHeight();

        //주먹 -> 보자기 1dp 어긋남 오른쪽으로 1dp 이동해야함
        loser = random.nextInt(size);
        HandType loserHandType = HandType.getRandomHandType();


        for (int i = 0; i < size; i++) {
            Face thisFace = faces.valueAt(i);
            float x1 = thisFace.getPosition().x;
            float y1 = thisFace.getPosition().y;
            float x2 = x1 + thisFace.getWidth();
            float y2 = y1 + thisFace.getHeight();

            float centerX = (x1 + x2) / 2.f;
            float centerY = (y1 + y2) / 2.f;

            HandView handView;
            if (centerX < width / 2) {
                if (y2 < height / 2) {
                    //left upper side
                    handView = new HandView(this)
                            .setPosition(centerX, y2)
                            .setRotation(90, centerX, centerY);
                } else {
                    //left bottom side
                    handView = new HandView(this)
                            .setPosition(centerX, y2)
                            .setRotation(0, centerY, centerX);
                }
            } else {
                if (y2 < height / 2) {
                    //right upper side
                    handView = new HandView(this)
                            .setPosition(centerX, y2)
                            .setRotation(180, centerY, centerX);
                } else {
                    //right bottom side
                    handView = new HandView(this)
                            .setPosition(centerX, y2)
                            .setRotation(270, centerX, centerY);
                }
            }

            handView.setHandType(random.nextInt(9), HandType.getRandomHandType(),
                    i == loser ? loserHandType : HandType.getHandType(loserHandType.getWinner()));
            handView.setVisibility(View.GONE);
            playPlain.addView(handView);
            handViews.add(handView);
        }

        playPlain.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (HandView handView : handViews) {
                    handView.showWithAnimation();
                }
                playPlain.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (HandView handView : handViews) {
                            handView.startHandGame();
                        }

                        Face thisFace = faces.valueAt(loser);
                        float x1 = thisFace.getPosition().x;
                        float y1 = thisFace.getPosition().y;

                        final View loserView = getLayoutInflater().inflate(R.layout.loser, playPlain, false);
                        loserView.getLayoutParams().width = (int) thisFace.getWidth();
                        loserView.getLayoutParams().height = (int) thisFace.getHeight();
                        loserView.setTranslationX(x1);
                        loserView.setTranslationY(y1);

                        playPlain.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                playPlain.addView(loserView, 0);
                            }
                        }, 2000);
                    }
                }, 1000);
            }
        }, 500);
    }


    private class saveImage extends AsyncTask<Boolean, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(Boolean... share) {
            View imageView = findViewById(R.id.imgView);
            Bitmap bitmap = Bitmap.createBitmap(imageView.getMeasuredWidth(), imageView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            imageView.draw(c);
            View playView = findViewById(R.id.play_plain);
            playView.draw(c);


            ByteArrayOutputStream bytes = null;
            String path;
            try {
                bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes); // bmp is your Bitmap instance
                path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), bitmap, "MyCameraApp/IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".jpg", null);
            } finally {
                if (bytes != null) {
                    try {
                        bytes.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            uri = Uri.parse(path);

            return share[0];
        }

        @Override
        protected void onPostExecute(Boolean share) {
            super.onPostExecute(share);
            if (share) sendShareIntent();
            else finishSaveAction();
        }
    }

    private Uri uri = null;

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    public void finishSaveAction() {
        ImageView btn = findViewById(R.id.img_save);
        btn.setImageResource(R.drawable.saved_img);
        btn.clearAnimation();
        findViewById(R.id.btn_save_fill).setVisibility(View.VISIBLE);
    }

    public boolean sendShareIntent() {
        if (uri == null) return false;
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(sharingIntent, "Share image using"));
        return true;
    }
}
