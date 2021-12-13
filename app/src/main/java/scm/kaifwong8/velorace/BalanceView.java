package scm.kaifwong8.velorace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class BalanceView extends View {
    private Paint paint;
    private float hsv[] = {0, 0, 255};
    private boolean first;
    private Rect baseLine;
    float cx;

    public BalanceView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.rgb(250, 250, 250));
        this.first = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (first) {
            this.baseLine = new Rect(0, getHeight()/2-2, getWidth(), getHeight()/2+2);
            first = false;
        }

        canvas.drawRect(baseLine, paint);
        paint.setColor(Color.rgb(250, 250, 250));
        canvas.drawCircle(cx, getHeight()/2, 14, paint);
    }

    public void update(float gx) {
        //255*gx/3000
        hsv[1] = gx>0? gx*0.085f:gx*-0.085f;

        paint.setColor(Color.HSVToColor(hsv));

        cx = (getWidth()/2) - (getWidth()/20*gx);
        invalidate();
    }
}
