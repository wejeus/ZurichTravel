package co.daresay.bellatrix.capture2.zxing;

import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import co.daresay.bellatrix.R;


/**
 * Camera preview callback
 */
public class PreviewCallback implements Camera.PreviewCallback {
    private static final String TAG = PreviewCallback.class.getSimpleName();
    /**
     * Xzing multi format reader
     */
    private final MultiFormatReader multiFormatReader = new MultiFormatReader();
    /**
     * Handler to send messages
     *
     * @see CaptureHandler
     */
    private Handler handler;
    /**
     * Camera manager
     */
    private CameraManager cameraManager;

    public PreviewCallback(Handler handler, CameraManager cameraManager) {
        this.handler = handler;
        this.cameraManager = cameraManager;
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
    	try {
	        Camera.Size previewSize = camera.getParameters().getPreviewSize();
	        
	        int width = previewSize.width;
	        int height = previewSize.height;

	        new DecodeAsyncTask(width, height).execute(bytes);
    	} catch (Exception e) {

    	}
    }

    /**
     * Asynchronous task for decoding and finding barcode
     */
    private class DecodeAsyncTask extends AsyncTask<byte[], Void, Result> {
        /**
         * Width of image
         */
        private int width;
        /**
         * Height of image
         */
        private int height;

        /**
         * @param width  Width of image
         * @param height Height of image
         */
        private DecodeAsyncTask(int width, int height) {
            this.width = width;
            this.height = height;
        }

        @Override
        protected void onPostExecute(Result result) {
            if (result != null) {
                if (handler != null) {
                    Message message = Message.obtain(handler, R.id.decoded);
                    Bundle bundle = new Bundle();
                    bundle.putString(CaptureHandler.DECODED_DATA, result.toString());
                    message.setData(bundle);
                    message.sendToTarget();
                }
            } else {
                if (handler != null) {
                    Message message = Message.obtain(handler, R.id.decode_failed);
                    message.sendToTarget();
                }
            }
        }

        @Override
        protected Result doInBackground(byte[]... datas) {
            if (!cameraManager.hasCamera()) {
                Log.d(TAG, "does not have camera");
            	return null;
            }
            Result rawResult = null;
            
            final PlanarYUVLuminanceSource source =
                    cameraManager.buildLuminanceSource(datas[0], width,
                            height, cameraManager.getBoundingRect());
            if (source != null) {
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                try {
                    rawResult = multiFormatReader.decodeWithState(bitmap);
                } catch (Exception re) {

                } finally {
                    multiFormatReader.reset();
                }
            }

            return rawResult;
        }
    }
}
