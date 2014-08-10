package com.augmate.sdk.scanner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import com.augmate.sdk.logger.Log;

import java.util.ArrayList;

public class ScannerVisualDebugger extends View {
    //private final DebugVizMessages msgHandler = new DebugVizMessages(this);

    // animated points
    // TODO: kill this
    public ArrayList<Point> mMessagePoints = new ArrayList<Point>();

    public int rawImgWidth = 0;
    public int rawImgHeight = 0;
    public int[][] debugImg = null;
    public int currentDebugBufferIdx = 0;

    // bounding box corners
    private Point[] boxCorners;
    private Paint boxLineColor;
    private Paint boxPointColor;
    private Paint generalPaint;
    private float scaleX;
    private float scaleY;
    private String barcodeValue;

    public ScannerVisualDebugger(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ScannerVisualDebugger(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScannerVisualDebugger(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        preparePaints();
    }

    private void preparePaints() {
        generalPaint = new Paint();
        generalPaint.setColor(0xaa1abc9c);
        generalPaint.setStrokeWidth(25.0f);

        boxLineColor = new Paint();
        boxLineColor.setColor(0xEE22AA99);
        boxLineColor.setStrokeWidth(2.0f);

        boxPointColor = new Paint();
        boxPointColor.setColor(0xAA22AA99);
        boxPointColor.setStrokeWidth(5.0f);
    }

    /**
     * Provides a safe-to-write buffer rgba buffer
     * Meanwhile the other buffer can now be used for reading and drawing-to-screen
     *
     * @return int[] reference to the next buffer
     */
    public int[] getNextDebugBuffer() {
        return debugImg[(currentDebugBufferIdx + 1) % 2];
    }

    /**
     * Switch current buffer
     * Current buffer is read from
     * Next buffer is written to
     * This avoids overwriting a buffer while it's being drawn out to screen
     */
    public void flipDebugBuffer() {
        currentDebugBufferIdx = (currentDebugBufferIdx + 1) % 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        scaleX = (float) canvas.getWidth() / (float) rawImgWidth;
        scaleY = (float) canvas.getHeight() / (float) rawImgHeight;

        canvas.drawRect(10, 10, 100, 100, generalPaint);

        if (debugImg != null) {
            canvas.drawBitmap(debugImg[currentDebugBufferIdx], 0, rawImgWidth, 0, 0, rawImgWidth, rawImgHeight, true, null);
        }

        if(boxCorners != null && boxCorners.length == 4) {

            for (int i = 0; i < 4; i++) {
                drawLine(canvas, boxLineColor, boxCorners[i], boxCorners[(i + 1) % 4]);
            }

            for (int i = 0; i < 4; i++) {
                drawPoint(canvas, boxPointColor, boxCorners[i]);
            }

            canvas.drawText("QR Code:", boxCorners[3].x, boxCorners[3].y + 20, boxPointColor);
            canvas.drawText("[" + barcodeValue + "]", boxCorners[3].x + 100, boxCorners[3].y + 20, boxPointColor);
        }

        ArrayList<Point> pts = new ArrayList<Point>(mMessagePoints);

        for (Point point : pts) {
            point.y -= 18;

            if (point.y < -10) {
                mMessagePoints.remove(point);
            }
        }

        for (Point point : pts) {
            //Log.i(TAG, "drawing point: " + ((float)point.x * scaleX) + "," + ((float)point.y * scaleY));
            canvas.drawCircle((float) point.x, (float) point.y, 25.0f, generalPaint);
            canvas.drawCircle((float) point.x, (float) point.y, 15.0f, generalPaint);
        }

        postInvalidate();
    }

    private void drawPoint(Canvas canvas, Paint paint, Point pt) {
        canvas.drawPoint(pt.x * scaleX, pt.y * scaleY, paint);
    }

    private void drawLine(Canvas canvas, Paint paint, Point a, Point b) {
        canvas.drawLine(a.x * scaleX, a.y * scaleY, b.x * scaleX, b.y * scaleY, paint);
    }

    /**
     * barcode bounding box
     * @param pts 4 corner points
     */
    public void setPoints(Point[] pts) {
        boxCorners = pts.clone();
    }

    /**
     * reallocates rgba debug output buffer that scanner paints unto
     * @param width
     * @param height
     */
    public void setFrameBufferSettings(int width, int height) {
        debugImg = new int[2][width * height];
        rawImgWidth = width;
        rawImgHeight = height;
    }

    public void setBarcodeValue(String barcodeValue) {
        this.barcodeValue = barcodeValue;
    }

    // TODO: is this DEFUNC?

//    public DebugVizMessages getHandler() {
//        return msgHandler;
//    }
//
//    private static class DebugVizMessages extends Handler {
//        private final String TAG = DebugVizMessages.class.getName();
//        private final ScannerVisualDebugger scanActivity;
//
//        DebugVizMessages(ScannerVisualDebugger activity) {
//            this.scanActivity = activity;
//        }
//
//        @Override
//        public void handleMessage(Message message) {
//            if (message.what == R.id.decodingThreadNewJob) {
//                Log.d(TAG, "Got new points for visualization");
//                Point[] pts = (Point[]) message.obj;
//                if (pts.length == 3)
//                    scanActivity.setPoints(pts[0], pts[1], pts[2]);
//            }
//        }
//    }
}
