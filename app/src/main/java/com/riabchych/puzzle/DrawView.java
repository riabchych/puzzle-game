package com.riabchych.puzzle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

class DrawView extends View {
    private final int size = 4;
    private Boolean isMove;
    private Paint matrix[][];
    private Rect rects[][];
    private int nums[][];
    private int nextX, widthPixels;
    private int nextY, heightPixels;
    private int clicksCount;

    public DrawView(Context context) {
        super(context);
        this.matrix = new Paint[this.size][this.size];
        this.nums = new int[this.size][this.size];
        this.rects = new Rect[this.size][this.size];
        this.nextX = this.nextY = this.clicksCount = 0;
        this.isMove = false;
        this.fillArray();
        this.shuffle(101);
    }


    private void fillArray() {
        int count = 0;
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                nums[i][j] = ++count;
            }
        }
        nums[this.size - 1][this.size - 1] = 0;
    }

    private int[] getEmptyField() {
        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++)
                if (nums[i][j] == 0) return new int[]{i, j};
        }
        return new int[]{0, 0};
    }

    private boolean getRandomBool() {
        return Math.floor(Math.random() * 2) == 0;
    }

    private void shuffle(int stepCount) {
        int x = 0, y = 0;
        int[] nullXY;
        boolean hMove, upLeft;

        for (int i = 0; i < stepCount; i++) {
            nullXY = this.getEmptyField();

            hMove = getRandomBool();
            upLeft = getRandomBool();
            if (!hMove && !upLeft) {
                y = nullXY[1];
                x = nullXY[0] - 1;
            }
            if (hMove && !upLeft) {
                x = nullXY[0];
                y = nullXY[1] + 1;
            }
            if (!hMove && upLeft) {
                y = nullXY[1];
                x = nullXY[0] + 1;
            }
            if (hMove && upLeft) {
                x = nullXY[0];
                y = nullXY[1] - 1;
            }
            if (0 <= x && x < this.size && 0 <= y && y < this.size) {
                this.move(x, y);
            }
        }

        this.clicksCount = 0;
    }

    private void move(int x, int y) {
        int[] nullXY = this.getEmptyField();
        if (((x - 1 == nullXY[0] || x + 1 == nullXY[0]) && y == nullXY[1]) || ((y - 1 == nullXY[1] || y + 1 == nullXY[1]) && x == nullXY[0])) {
            this.nums[nullXY[0]][nullXY[1]] = this.nums[x][y];
            this.nums[x][y] = 0;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            this.nextX = (int) event.getX();
            this.nextY = (int) event.getY();
            clicksCount++;
            invalidate();
        }
        return true;
    }

    private void fillRect() {
        int rectWidth = (this.widthPixels / this.size), rectHeight = (this.heightPixels / this.size);
        for (int i = 0; i < this.size; i++)
            for (int j = 0; j < this.size; j++) {
                this.matrix[i][j] = new Paint();
                this.matrix[i][j].setStyle(Paint.Style.FILL);
                this.rects[i][j] = new Rect(rectWidth * i, rectHeight * j, rectWidth * (i + 1), rectHeight * (j + 1));
            }
    }

    private int getClicks() {
        return this.clicksCount;
    }

    private boolean checkVictory() {
        int count = 1;
        for (int i = 0; i < this.size; i++)
            for (int j = 0; j < this.size; j++) {
                if (nums[j][i] != count) {
                    return false;
                } else {
                    if (count + 1 < this.size * this.size)
                        count++;
                    else
                        break;
                }

            }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.heightPixels == 0 || this.widthPixels == 0) {
            this.heightPixels = this.getHeight();
            this.widthPixels = this.getWidth();
            this.fillRect();
        }
        int rectWidth = (widthPixels / this.size), rectHeight = (heightPixels / this.size);
        Paint paint = new Paint();
        for (int i = 0; i < this.size; i++)
            for (int j = 0; j < this.size; j++) {
                if (nextX != 0 && nextY != 0) {
                    if (nextX > rects[i][j].left && nextX < rects[i][j].right &&
                            nextY > rects[i][j].top && nextY < rects[i][j].bottom) {

                        this.move(i, j);
                        isMove = false;
                        nextX = nextY = 0;
                        invalidate();
                    }
                }

                // fill
                matrix[i][j].setColor(this.nums[i][j] == 0 ? Color.WHITE : Color.LTGRAY);
                canvas.drawRect(this.rects[i][j], matrix[i][j]);

                // border
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(2);
                canvas.drawRect(this.rects[i][j], paint);

                if (this.nums[i][j] != 0) {
                    String number = Integer.toString(this.nums[i][j]);
                    paint.setTextSize(60);
                    paint.setStrokeWidth(0);
                    paint.setColor(Color.BLACK);
                    paint.setTextAlign(Paint.Align.CENTER);
                    float x1 = ((widthPixels / this.size) * i) + rectWidth / 2;
                    float y1 = ((heightPixels / this.size) * j) + rectHeight / 2;
                    canvas.drawText(number, x1, y1, paint);
                }

                if (this.clicksCount > 30) {
                    if (this.checkVictory()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
                        builder.setTitle("Выполнено!")
                                .setMessage(String.format("Последовательность собрана за %d касаний!", this.getClicks()))
                                .setCancelable(false)
                                .setNegativeButton("Ок",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                shuffle(100);
                                                dialog.cancel();
                                            }
                                        });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
            }
    }

}