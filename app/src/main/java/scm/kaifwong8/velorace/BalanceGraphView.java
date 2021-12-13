package scm.kaifwong8.velorace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import java.util.ArrayList;

public class BalanceGraphView extends View {
    private static final String TAG = "BalanceGraphView";
    private static final int MAX_COL = 30;

    private boolean first;
    private ArrayList<Float> speedArr;
    private float[] posY;
    private float colWidth;
    private Paint paint;

    public BalanceGraphView(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.first = true;
        this.speedArr = new ArrayList<>();
        this.posY = new float[MAX_COL];
        this.paint = new Paint();
        this.paint.setColor(Color.WHITE);
        this.paint.setStrokeWidth(3);
        this.paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (first) {
            colWidth = getWidth()/MAX_COL;
            first = false;
        }

        Path path = new Path();
        if (speedArr.size() > 1) {
            for (int i=0; i<speedArr.size(); i++) {
                if (i==0) {
                    path.moveTo(colWidth*i, posY[i]);
                } else {
                    path.lineTo(colWidth*i, posY[i]);
                }
            }
        }

        canvas.drawPath(path, paint);
    }

    public void update(float speed) {
        speedArr.add(speed);
        if (speedArr.size() > MAX_COL) speedArr.remove(0);

        for (int i=0; i<speedArr.size(); i++) {
            posY[i] = getHeight()-((speedArr.get(i) - (-10)) / (10 - (-10)) * (getHeight() - 0));
        }

        invalidate();
    }
}
