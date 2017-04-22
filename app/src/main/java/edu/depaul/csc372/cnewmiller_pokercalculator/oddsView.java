package edu.depaul.csc372.cnewmiller_pokercalculator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Clay on 11/17/16.
 */

public class oddsView extends View {
    private double probability;
    private Paint paint=new Paint();
    private Handler handler = new Handler();
    int width, height;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    public oddsView(Context context) {super(context);}

    public oddsView(Context context, AttributeSet attrs) {super(context, attrs);}

    @Override
    protected void onDraw(Canvas canvas){
        if (this.probability==1){
            this.paint.setColor(Color.BLUE);
        }
        else if (this.probability>=0.2 && this.probability<1){
            this.paint.setColor(Color.GREEN);
        }
        else if (this.probability>=0.05 && this.probability<0.2){
            this.paint.setColor(Color.YELLOW);
        }
        else if (this.probability>=0.001 && this.probability<0.05){
            this.paint.setColor(getResources().getColor(R.color.colorOrange));
        }
        else{
            this.paint.setColor(Color.RED);
        }
        paint.setTextSize(50);
        canvas.translate(width/3, height/2*1.5f);

        canvas.drawText(""+this.probability, 0, 0, paint);


    }

    public void start(double numberToShow){
        this.probability = numberToShow;
        handler.post(new Runnable() {
            @Override
            public void run() {invalidate();}}
        );
    }

}
