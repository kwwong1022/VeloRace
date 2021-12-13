package scm.kaifwong8.velorace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class ResultView extends View {
    private static final String TAG = "ResultView";
    private static final int MAX_COL = 60;

    private boolean first;
    private float width;
    private float colWidth;
    private float maxSpeed;
    private float maxElev;
    private ArrayList<RideRecord> rideRecords;

    private Paint paint;

    public ResultView(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.first = true;
        this.rideRecords = new ArrayList<>();
        this.paint = new Paint();
        this.paint.setColor(Color.WHITE);
        this.paint.setStrokeWidth(3);
        this.paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (first) {
            this.width = getWidth() - getWidth()/10;
            this.colWidth = this.width/MAX_COL;
            this.first = false;
        }

        Path speedPath = new Path();
        Path elevPath = new Path();
        if (rideRecords.size() > 1) {

            for (int i=0; i<MAX_COL; i++) {
                int getIndex = rideRecords.size()/MAX_COL==0? 0:rideRecords.size()/MAX_COL*i;
                float normalSpeed = (rideRecords.get(getIndex).getSpeed() - 0) / (maxSpeed - 0) * ((getHeight()-getHeight()/10/2) - getHeight()/10/2);
                float normalElev = (rideRecords.get(getIndex).getElev() - 0) / (maxElev - 0) * ((getHeight()-getHeight()/10/2) - getHeight()/10/2);

                if (i==0) {
                    speedPath.moveTo(colWidth*i+getWidth()/10/2, (getHeight()-getHeight()/10/2)-normalSpeed);
                    elevPath.moveTo(colWidth*i+getWidth()/10/2, (getHeight()-getHeight()/10/2)-normalElev);
                } else {
                    speedPath.lineTo(colWidth*i+getWidth()/10/2, (getHeight()-getHeight()/10/2)-normalSpeed);
                    elevPath.lineTo(colWidth*i+getWidth()/10/2, (getHeight()-getHeight()/10/2)-normalElev);
                }
            }

            this.paint.setColor(Color.HSVToColor(new float[]{201, 64, 79}));
            canvas.drawPath(speedPath, paint);
            this.paint.setColor(Color.HSVToColor(new float[]{0, 0, 30}));
            canvas.drawPath(elevPath, paint);
        }
    }

    public void update(ArrayList<RideRecord> rideRecords, float maxSpeed, float maxElev) {
        this.rideRecords = (ArrayList<RideRecord>) rideRecords.clone();
        this.maxSpeed = maxSpeed;
        this.maxElev = maxElev;
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return true;
    }
}
