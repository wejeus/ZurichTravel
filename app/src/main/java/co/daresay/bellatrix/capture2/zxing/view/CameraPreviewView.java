package co.daresay.bellatrix.capture2.zxing.view;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import co.daresay.bellatrix.capture2.zxing.CameraManager;


/**
 * Camera preview view. Shows camera preview data
 */
public class CameraPreviewView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraPreviewView.class.getSimpleName();

    /**
     * Surface holder for camera preview data
     */
    private SurfaceHolder surfaceHolder;
    /**
     * Camera manager
     */
    private CameraManager cameraManager;

    @SuppressWarnings("deprecation")
    public CameraPreviewView(Context context) {
        super(context);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @SuppressWarnings("deprecation")
    public CameraPreviewView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /**
     * Setter for camera manager
     * @param cameraManager camera manager to set
     */
    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            cameraManager.getCamera().setPreviewDisplay(holder);
            cameraManager.getCamera().setDisplayOrientation(90);
            cameraManager.getCamera().startPreview();
        } catch (Exception e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // empty. Taking care of releasing the Camera preview in activity.
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (surfaceHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            cameraManager.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            cameraManager.getCamera().setPreviewDisplay(surfaceHolder);
            cameraManager.startPreview();

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}
