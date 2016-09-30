package net.mhoff.flexibletasks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import net.mhoff.flexibletasks.model.Task;


public class StatusIcon extends View {

    private static final Paint paint = new Paint();
    private static final float PADDING = 0.1f;

    static {
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
    }

    private Task task;

    private final RectF rect = new RectF();

    public StatusIcon(Context context) {
        super(context);
    }

    public StatusIcon(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rect.set(canvas.getWidth() * PADDING, canvas.getHeight() * PADDING, canvas.getWidth() * (1 - PADDING), canvas.getHeight() * (1 - PADDING));
        if (task.isEnabled()) {
            drawRatio(canvas, 360 * task.getDuePercentage());
        } else {
            paint.setColor(ContextCompat.getColor(getContext(), R.color.task_disabled));
            canvas.drawArc(rect, 0, 360, true, paint);
        }
    }

    private void drawRatio(Canvas canvas, float midAngle) {
        paint.setColor(ContextCompat.getColor(getContext(), R.color.task_due));
        canvas.drawArc(rect, 0, midAngle, true, paint);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.task_done));
        canvas.drawArc(rect, midAngle, 360 - midAngle, true, paint);
    }

    public void setTask(Task task) {
        this.task = task;
        invalidate();
    }
}
