package com.zokbet.betdd.activity;

import java.io.IOException;
import java.util.Vector;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.zokbet.betdd.R;
import com.zokbet.betdd.utils.Unicode;
import com.zokbet.betdd.zxing.camera.CameraManager;
import com.zokbet.betdd.zxing.decoding.CaptureActivityHandler;
import com.zokbet.betdd.zxing.decoding.InactivityTimer;
import com.zokbet.betdd.zxing.view.ViewfinderView;

/**
 * Initial the camera
 * @author Ryan.Tang
 */
public class CaptureActivity extends BaseActivity implements Callback {

    public static final int REQ_SCAN_CODE = 7788;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;
    private Button cancelScanButton;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        //ViewUtil.addTopView(getApplicationContext(), this, R.string.scan_card);
        CameraManager.init(getApplication());
        viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        cancelScanButton = (Button) this.findViewById(R.id.btn_cancel_scan);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

        //quit the scan view
        cancelScanButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                CaptureActivity.this.finish();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * Handler scan result
     * @param result
     * @param barcode
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        //FIXME
        if (resultString.equals("")) {
            Toast.makeText(CaptureActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        }else {
            if(resultString.startsWith("merch://")) {
                String other = resultString.substring("merch://".length());
                if(!TextUtils.isEmpty(other) && other.contains(",")) {
                    String[] str = other.split(",");
                    if(str != null && str.length >= 2) {
                        String id = str[0];
                        String name = str[1];
                        if(getDefaultUser() != null) {
                            Intent intent = new Intent(this, PayActivity.class);
                            intent.putExtra("id", id);
                            intent.putExtra("name", Unicode.unicode2String(name));
                            startActivityForResult(intent, PayActivity.REQ_PAY_CODE);
                        }else {
                            Intent intent = new Intent(this, LoginActivity.class);
                            startActivity(intent);
                            CaptureActivity.this.finish();
                        }
                    }else {
                        scanFailed("二维码格式错误");
;                   }
                }else {
                    scanFailed("二维码格式错误");
                }
            }else {
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("result", resultString);
                resultIntent.putExtras(bundle);
                this.setResult(RESULT_OK, resultIntent);
                CaptureActivity.this.finish();
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats,
                    characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final OnCompletionListener beepListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    private void scanFailed(String resultString) {
        Intent resultIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("result", resultString);
        resultIntent.putExtras(bundle);
        this.setResult(RESULT_OK, resultIntent);
        CaptureActivity.this.finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            if(requestCode == PayActivity.REQ_PAY_CODE) {
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("pay", "finish");
                resultIntent.putExtras(bundle);
                this.setResult(RESULT_OK, resultIntent);
                CaptureActivity.this.finish();
            }
        }else {
            if(requestCode == PayActivity.REQ_PAY_CODE) {
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("pay", "cancel");
                resultIntent.putExtras(bundle);
                this.setResult(RESULT_OK, resultIntent);
                CaptureActivity.this.finish();
            }
        }
    }
}