package com.riabchych.puzzle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

class DrawView extends View {
    private final int size = 4;
    private Puzzle puzzle;

    public DrawView(Context context) {
        super(context);
        this.puzzle = new Puzzle(this.size);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.puzzle.setX((int) event.getX());
            this.puzzle.setY((int) event.getY());
            this.puzzle.incClicks();
            invalidate();
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.puzzle.getHeight() == 0 || this.puzzle.getWidth() == 0) {
            this.puzzle.setHeight(this.getHeight());
            this.puzzle.setWidth(this.getWidth());
            this.puzzle.fillRect();
        }
        this.puzzle.draw(canvas);
        if (this.puzzle.getClicks() > 30) {
            if (this.puzzle.checkVictory()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
                builder.setTitle("Выполнено!")
                        .setMessage(String.format("Последовательность собрана за %d касаний!", this.puzzle.getClicks()))
                        .setCancelable(false)
                        .setNegativeButton("Ок",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        puzzle.shuffle(150);
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

}