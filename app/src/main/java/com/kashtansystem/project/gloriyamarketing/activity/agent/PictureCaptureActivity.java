package com.kashtansystem.project.gloriyamarketing.activity.agent;

import android.content.Intent;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by FlameKaf on 19.05.2017.
 * ----------------------------------
 * Камера приложения. Создаёт снимки, сохраняя в переданном пути, и возвращает превью и адрес хранения фото,
 * либо null
 */

public class PictureCaptureActivity extends BaseActivity implements View.OnClickListener, SurfaceHolder.Callback
{
    private Camera camera = null;
    private String title;
    private int photoRepId = 0;
    private File file;
    private View photoViews[] = new View[3];

    @Override
    public String getActionBarTitle()
    {
        return "";
    }

    @Override
    public boolean getHomeButtonEnable()
    {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        title = getIntent().getStringExtra(C.KEYS.EXTRA_DATA) + "_" +
            new SimpleDateFormat("ddMMyy_HHmmss", Locale.getDefault()).format(new Date(System.currentTimeMillis()));
        setResult(RESULT_CANCELED);

        setContentView(R.layout.camera);

        photoViews[0] = findViewById(R.id.ivPhotoRep1);
        photoViews[1] = findViewById(R.id.ivPhotoRep2);
        photoViews[2] = findViewById(R.id.ivPhotoRep3);

        findViewById(R.id.pcBtnCapture).setOnClickListener(this);
        findViewById(R.id.pcBtnOk).setOnClickListener(this);

        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.pcSurfaceView);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        camera = Camera.open(0);
        unBlockCapture();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (camera != null)
        {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent iResult = new Intent();
        iResult.putExtra(C.KEYS.EXTRA_CONTENT,
            new String[]{
                photoViews[0].getTag().toString(),
                photoViews[1].getTag().toString(),
                photoViews[2].getTag().toString()});
        setResult(RESULT_OK, iResult);
        finish();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        try
        {
            setDisplayOrientation();
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.pcBtnCapture:
                blockCapture();
                camera.takePicture(null, null, new Camera.PictureCallback()
                {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera)
                    {
                        try
                        {
                            file = new File(getStorageFilePath());
                            FileOutputStream fos = new FileOutputStream(file);
                            fos.write(data);
                            fos.close();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            break;
            case R.id.pcBtnOk:
                final ImageView photoRep = (ImageView)photoViews[photoRepId];
                photoRep.setImageBitmap(Util.getDecodedFile(file.getAbsolutePath()));
                photoRep.setTag(file.getAbsolutePath());

                photoRepId++;
                if (photoRepId > 2)
                    photoRepId = 0;
                unBlockCapture();
                camera.startPreview();
            break;
        }
    }

    /**
     * Преднастройка камеры - устанавливается вертикальная ориентация камеры, качество снимков
     * и разрешение (1280 х 960)
     * */
    private void setDisplayOrientation()
    {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(0, info);
        int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;

        switch (rotation)
        {
            case Surface.ROTATION_0:
                degrees = 0;
            break;
            case Surface.ROTATION_90:
                degrees = 90;
            break;
            case Surface.ROTATION_180:
                degrees = 180;
            break;
            case Surface.ROTATION_270:
                degrees = 270;
            break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        }
        else
        {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        Camera.Parameters parameters = camera.getParameters();
        parameters.setJpegQuality(75);
        parameters.setPictureSize(1280, 960);
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        parameters.setRotation(result);

        camera.setParameters(parameters);
        camera.setDisplayOrientation(result);
    }

    /**
     * Блокирует кнопку захвата изображения
     * */
    private void blockCapture()
    {
        findViewById(R.id.pcBtnCapture).setEnabled(false);
        findViewById(R.id.pcBtnOk).setEnabled(true);
    }

    /**
     * Разблокирует кнопку захвата изображения
     * */
    private void unBlockCapture()
    {
        findViewById(R.id.pcBtnCapture).setEnabled(true);
        findViewById(R.id.pcBtnOk).setEnabled(false);
    }

    /**
     * Формирует путь, в котором будет сохранён фотоотчёт
     * @return путь для сохранения фотоотчёта
     */
    private String getStorageFilePath()
    {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED))
        {
            // storage
            File storageDir = new File(String.format("%s/%s/%s", Environment.getExternalStorageDirectory(),
                getString(R.string.app_name), "photo"));
            if (storageDir.exists() || storageDir.mkdirs())
                return String.format("%s/%s", storageDir.getAbsolutePath(), String.format("%s_%s.%s",
                    title, photoRepId, "jpg"));
        }
        return "";
    }
}