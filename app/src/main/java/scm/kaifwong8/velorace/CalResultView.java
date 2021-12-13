package scm.kaifwong8.velorace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

public class CalResultView extends View {
    private static final String TAG = "CalResultView";

    private static final int STROKE_WIDTH = 60;
    private Paint paint;
    private RectF mRect;
    private PointF c;
    private float r, normal;
    private String distance;
    private boolean first;

    public CalResultView(Context context) {
        super(context);
        init();
    }

    private void init(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        c = new PointF(0, 0);
        first = true;
        normal = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (first) {
            c.x = getWidth()/ 2;
            c.y = getHeight()/ 2;
            r = Math.min(c.x, c.y);

            mRect = new RectF(c.x-r+STROKE_WIDTH/2, STROKE_WIDTH/2, c.x+r-STROKE_WIDTH/2, c.y+r-STROKE_WIDTH/2);
            first = false;
        }

        /**
         * Code modified from function onDraw(), found at:
         * https://stackoverflow.com/questions/39206733/android-drawing-an-arc-inside-a-circle
         * */
        paint.setColor(Color.rgb(200, 200, 200));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        canvas.drawCircle(c.x, c.y, r - (STROKE_WIDTH/2), paint);
        paint.setColor(Color.rgb(76, 165, 212));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(mRect, -90, normal, false, paint);   // -90 to 360
        paint.setColor(Color.rgb(38, 38, 38));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(c.x, c.y, r - (STROKE_WIDTH/2), paint);
        paint.setColor(Color.rgb(200, 200, 200));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6);
        paint.setTextSize(r/2.5f);
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(distance, c.x, c.y+r/4, paint);
        paint.setStrokeWidth(2f);
        paint.setTextSize(r/5f);
        canvas.drawText("Distance", c.x, c.y-r/6, paint);
    }

    public void update(float rideTime, float avgSpd, float maxSpd, String distance) {
        float rideNormal = rideTime>3600? 10:(rideTime - 0) / (3600 - 0) * (10 - 0);
        float avgSpdNormal = avgSpd>40? 10:(avgSpd - 0) / (40 - 0) * (10 - 0);
        float maxSpdNormal = maxSpd>50? 10:(maxSpd - 0) / (50 - 0) * (10 - 0);
        normal = ((rideNormal+avgSpdNormal+maxSpdNormal)-0) / (30 - 0) * (360 - (-90));
        this.distance = distance;

        Log.d(TAG, "rideTime: " + rideTime + ", avgSpd: " + avgSpd + ", maxSpd: " + maxSpd);
        Log.d(TAG, "normal: " + normal);

        invalidate();
    }

}
