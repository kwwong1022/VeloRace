package scm.kaifwong8.velorace;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.view.WindowId;

public class ResultBarGraph extends View {
    private static final String TAG = "ResultBarGraph";

    private Paint paint;
    private boolean first;
    private float width = 0;
    private float height = 0;
    private float normal = 0;
    private float value = 0;
    private int type = -1;

    public ResultBarGraph(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setColor(Color.HSVToColor(new float[]{0, 0, 30}));
        this.first = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (first) {
            this.width = getWidth();
            Log.d(TAG, "width: " + width);
            this.height = getHeight()/10;
            this.first = false;
        }

        this.paint.setColor(Color.HSVToColor(new float[]{0, 0, 30}));
        canvas.drawRect(new RectF(0, (getHeight()/2)-(height/2), width, (getHeight()/2)+(height/2)), paint);

        switch (type) {
            case 0:
                normal = value>3600? width:(value - 0) / (3600 - 0) * (width - 0f);
                break;
            case 1:
                normal = value>40? width:(value - 0f) / (40f - 0f) * (width - 0f);
                break;
            case 2:
                normal = value>50? width:(value - 0) / (50 - 0) * (width - 0);
                break;
        }
        this.paint.setColor(Color.HSVToColor(new float[]{122, 60, 83}));
        canvas.drawRect(new RectF(0, (getHeight()/2)-(height/2), normal, (getHeight()/2)+(height/2)), paint);
    }

    void update(float value, int type) {
        this.value = value;
        this.type = type;

        invalidate();
    }
}
