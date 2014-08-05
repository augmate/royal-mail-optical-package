package com.augmate.sdk.scanner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

public class ScannerVisualDebugger extends View {
    private static final String TAG = ScannerVisualDebugger.class.getName();

    //public ResultPoint[] points;
    public ArrayList<Point> mMessagePoints = new ArrayList<Point>();

    private Point bottomLeft = null;
    private Point topLeft = null;
    private Point topRight = null;

    private Paint outlineColor = null;
    private Paint outlinePointsColor = null;
    private Paint _PaintColor;
    public int rawImgWidth = 0;
    public int rawImgHeight = 0;
    public int[] debugImg = null;

    public ScannerVisualDebugger(Context context) {
        super(context);
        preparePaints();
    }

    public ScannerVisualDebugger(Context context, AttributeSet attr) {
        super(context, attr);
        preparePaints();
    }

    private void preparePaints() {
        _PaintColor = new Paint();
        _PaintColor.setColor(0xaa1abc9c);
        _PaintColor.setStrokeWidth(25.0f);

        outlineColor = new Paint();
        outlineColor.setColor(0xEE22AA99);
        outlineColor.setStrokeWidth(8.0f);

        outlinePointsColor = new Paint();
        outlinePointsColor.setColor(0xAA22AA99);
        outlinePointsColor.setStrokeWidth(20.0f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(20, 20, 340, 160, _PaintColor);

        if(debugImg != null) {
            canvas.drawBitmap(debugImg, 0, rawImgWidth, 0, 0, rawImgWidth, rawImgHeight, true, null);
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
            canvas.drawCircle((float) point.x, (float) point.y, 25.0f, _PaintColor);
            canvas.drawCircle((float) point.x, (float) point.y, 15.0f, _PaintColor);
        }

        if (bottomLeft != null) {

            canvas.drawPoint(topLeft.x, topLeft.y, outlinePointsColor);
            canvas.drawPoint(topRight.x, topRight.y, outlinePointsColor);
            canvas.drawPoint(bottomLeft.x, bottomLeft.y, outlinePointsColor);

            drawLine(canvas, outlineColor, bottomLeft, topLeft);
            drawLine(canvas, outlineColor, topLeft, topRight);
            //drawLine(canvas, outlineColor, topRight, pts[3]);
            //drawLine(canvas, outlineColor, pts[3], pts[0]);
        }

        postInvalidate();
    }

    private void drawLine(Canvas canvas, Paint paint, Point a, Point b) {
        if (a != null && b != null) {
            canvas.drawLine(a.x, a.y, b.x, b.y, paint);
        }
    }

    public void setPoints(Point bottomLeft, Point topLeft, Point topRight) {
        Log.d(TAG, "Setting points");
        this.bottomLeft = bottomLeft;
        this.topLeft = topLeft;
        this.topRight = topRight;
    }

    public DebugVizHandler getHandler() {
        return msgHandler;
    }
    private final DebugVizHandler msgHandler = new DebugVizHandler(this);

    private static class DebugVizHandler extends Handler {
        private final String TAG = DebugVizHandler.class.getName();
        private final ScannerVisualDebugger scanActivity;

        DebugVizHandler(ScannerVisualDebugger activity) {
            this.scanActivity = activity;
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what == R.id.visualizationNewData) {
                Log.d(TAG, "Got new points for visualization");
                Point[] pts = (Point[]) message.obj;
                if (pts.length == 3)
                    scanActivity.setPoints(pts[0], pts[1], pts[2]);
            }
        }
    }
}
