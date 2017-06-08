package com.nanacorp.finn.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.nanacorp.finn.R;
import com.nanacorp.finn.entity.FinnAmountRange;
import com.nanacorp.finn.entity.FinnDaily;
import com.nanacorp.finn.entity.FinnPortfolioData;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.nanacorp.finn.entity.FinnPortfolioData.DAY_DISP_PLOT_NUM;
import static com.nanacorp.finn.entity.FinnPortfolioData.MONTHS;
import static com.nanacorp.finn.entity.FinnPortfolioData.QUARTER;

/**
 * Created by lcuong on 6/5/17.
 * <p>
 * Chart render view
 */

public class FinnChartView extends View {

    private static final int TYPE_DAILY = 1;
    private static final int TYPE_MONTHLY = 2;
    private static final int TYPE_QUARTER = 3;

    private static final float LINE_WIDTH = 1;
    private static final float NORMAL_WIDTH_RATIO = 0.8f;
    private static final float NORMAL_ASPECT_RATIO = 1.1f;

    private int displayPlotNum = DAY_DISP_PLOT_NUM;
    private float chartHeight;
    private float chartWidth;

    private float mMainFrameLeft;
    private float mMainFrameRight;
    private float mMainFrameTop;
    private float mMainFrameBottom;

    private float fontHeight;
    private RectF mMainFrameArea;


    // Paint
    private Paint mPaintBackground = new Paint();
    private Paint mPaintFrame = new Paint();
    private Paint mPaintSmoothLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintText = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintFont = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mPaintAux = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mMaxDigitsLabel = 0;
    private int mMinDigitsLabel = 0;
    private int displayWidth = getResources().getDisplayMetrics().widthPixels;
    private int displayHeight = getResources().getDisplayMetrics().heightPixels;
    private int width = (int) (NORMAL_WIDTH_RATIO * displayWidth), height = (int) (width / NORMAL_ASPECT_RATIO);
    private float mTextSize;
    private List<FinnPortfolioData> finnPortfolioDatas = new ArrayList<>();

    private int mChartType = TYPE_DAILY;
    public Map<String, Map<String, List<FinnDaily>>> mChartDrawData = new HashMap<>();
    private String mCurrentMonth = MONTHS.get(11);
    public static final int MAX_DAY_OF_MONTH = 31;

    public void processChartData() {
        switch (mChartType) {
            case TYPE_DAILY:
                displayPlotNum = DAY_DISP_PLOT_NUM;
                break;

            case TYPE_MONTHLY:
                displayPlotNum = MONTHS.size();
                break;

            case TYPE_QUARTER:
                displayPlotNum = QUARTER.size();
                break;

            default:
                break;
        }
        for (FinnPortfolioData eachPortfolio : finnPortfolioDatas) {
            switch (mChartType) {
                case TYPE_DAILY:
                    Map<String, List<FinnDaily>> currentMonthData = eachPortfolio.getDataDaily(mCurrentMonth);
                    mChartDrawData.put(eachPortfolio.portfolioId, currentMonthData);
                    break;

                case TYPE_MONTHLY:
                    mChartDrawData.put(eachPortfolio.portfolioId, eachPortfolio.getDataMonthly());
                    break;

                case TYPE_QUARTER:
                    mChartDrawData.put(eachPortfolio.portfolioId, eachPortfolio.getDataQuarter());
                    break;

                default:
                    break;
            }
        }
    }

    public FinnAmountRange getMinMaxAmount(Map<String, Map<String, List<FinnDaily>>> chartDrawData) {
        FinnAmountRange result = new FinnAmountRange();
        Set<String> keySet = chartDrawData.keySet();
        if (keySet.size() > 0) {
            switch (mChartType) {
                case TYPE_DAILY:
                    for (int i = 0; i < MAX_DAY_OF_MONTH; i++) {
                        float minOfPeriod = Float.MAX_VALUE;
                        float maxOfPeriod = 0;
                        for (String keyPortfolioId : keySet) {
                            List<FinnDaily> portfolioData = chartDrawData.get(keyPortfolioId).get(mCurrentMonth);
                            if (i < portfolioData.size()) {
                                FinnDaily daily = portfolioData.get(i);
                                minOfPeriod = Math.min(minOfPeriod, daily.amount);
                                maxOfPeriod += daily.amount;
                            }
                        }
                        result.minAmount = Math.min(minOfPeriod, result.minAmount);
                        result.maxAmount = Math.max(maxOfPeriod, result.maxAmount);
                    }
                    break;

                case TYPE_MONTHLY:
                    for (String month : MONTHS) {
                        float minOfPeriod = Float.MAX_VALUE;
                        float maxOfPeriod = 0;
                        for (String keyPortfolioId : keySet) {
                            List<FinnDaily> portfolioData = chartDrawData.get(keyPortfolioId).get(month);
                            if (portfolioData.size() > 0) {
                                FinnDaily daily = portfolioData.get(0);
                                minOfPeriod = Math.min(minOfPeriod, daily.amount);
                                maxOfPeriod += daily.amount;
                            }
                        }
                        result.minAmount = Math.min(minOfPeriod, result.minAmount);
                        result.maxAmount = Math.max(maxOfPeriod, result.maxAmount);
                    }
                    break;

                case TYPE_QUARTER:
                    for (String quarter : QUARTER) {
                        float minOfPeriod = Float.MAX_VALUE;
                        float maxOfPeriod = 0;
                        for (String keyPortfolioId : keySet) {
                            List<FinnDaily> portfolioData = chartDrawData.get(keyPortfolioId).get(quarter);
                            if (portfolioData.size() > 0) {
                                FinnDaily daily = portfolioData.get(0);
                                minOfPeriod = Math.min(minOfPeriod, daily.amount);
                                maxOfPeriod += daily.amount;
                            }
                        }
                        result.minAmount = Math.min(minOfPeriod, result.minAmount);
                        result.maxAmount = Math.max(maxOfPeriod, result.maxAmount);
                    }
                    break;

                default:
                    break;
            }
        }
        return result;
    }

    public void setChartData(List<FinnPortfolioData> historicalPriceData) {

        finnPortfolioDatas = historicalPriceData;
        processChartData();
        invalidate();
    }

    // paint settings
    private void setPaintSettings(AttributeSet attr) {
        mTextSize = getResources().getDimension(R.dimen.chart_text_size);

        mPaintBackground.setStyle(Paint.Style.FILL);
        mPaintBackground.setColor(ContextCompat.getColor(getContext(), R.color.chart_background));
        mPaintFrame.setStrokeWidth(LINE_WIDTH);
        mPaintFrame.setStyle(Paint.Style.STROKE);
        mPaintFrame.setColor(ContextCompat.getColor(getContext(), R.color.chart_frame));
        mPaintFont.setTextSize(mTextSize);
    }

    private void setChartParameters() {
        mMaxDigitsLabel = 1;
        mMinDigitsLabel = 0;
    }

    // chart canvas config
    private void setCanvasConfig() {
        float canvasHeight = height;
        float canvasWidth = width;
        mPaintFont.setTextSize(mTextSize);
        Paint.FontMetrics fontMetrics = mPaintFont.getFontMetrics();
        fontHeight = -1 * (fontMetrics.bottom + fontMetrics.top);
        float yLabelWidth = mPaintFont.measureText("" + Float.MAX_VALUE);

        chartHeight = canvasHeight - fontHeight * 6f;

        chartWidth = canvasWidth - yLabelWidth;
        mMainFrameLeft = 0;
        mMainFrameRight = chartWidth;
        mMainFrameTop = fontHeight * 3.5f;
        mMainFrameBottom = chartHeight + mMainFrameTop;
        mMainFrameArea = new RectF(mMainFrameLeft, mMainFrameTop, mMainFrameRight, mMainFrameBottom);
    }

    public FinnChartView(Context context) {
        super(context);
    }

    public FinnChartView(Context context, AttributeSet attr) {
        super(context, attr);

        setPaintSettings(attr);
        setFocusable(true);
    }

    @Override
    public void onDraw(Canvas canvas) {

        // background
        setChartParameters();
        setCanvasConfig();

        canvas.drawRect(mMainFrameArea, mPaintBackground);


        float mMax = 0;
        float mMin = 9999999;
        // calculate min and max range of amount
        if (finnPortfolioDatas.size() > 0) {
            FinnAmountRange minMaxAmount = getMinMaxAmount(mChartDrawData);
            mMin = minMaxAmount.minAmount;
            mMax = minMaxAmount.maxAmount;
        }

        float amountInterval = (int) Math.pow(10, Integer.toString((int) (mMax - mMin)).length() - 1);
        // smooth min max
        for (int i = 1; ; i++) {
            if (mMin > (i - 1) * amountInterval && i * amountInterval >= mMin)
                mMin = (i - 1) * amountInterval;
            if (i * amountInterval > mMax) {
                mMax = i * amountInterval;
                break;
            }
        }

        // chart interval
        float xInterval = chartWidth / displayPlotNum;
        float yInterval = chartHeight / mMax;

        // draw date bar
        float lx = xInterval / 2;
        mPaintAux.setColor(ContextCompat.getColor(getContext(), R.color.chart_aux));
        for (int i = 0; i < displayPlotNum; i++) {
            drawString(canvas, "test", lx, mMainFrameBottom + fontHeight * 1.2f, "c", ContextCompat.getColor(getContext(), R.color.chart_label_text), mTextSize);
            canvas.drawLine(lx, mMainFrameTop, lx, mMainFrameBottom, mPaintAux);
            lx += xInterval;
        }

        //draw amount bar
        float y = mMax;
        drawString(canvas, getFormatedValue(y, false, mMaxDigitsLabel, mMinDigitsLabel, true), mMainFrameRight + 5, mMainFrameTop + fontHeight, "l", ContextCompat.getColor(getContext(), R.color.chart_label_text), mTextSize);
        for (y = mMax - amountInterval; y > mMin; y -= amountInterval) {
            drawString(canvas, getFormatedValue(y, false, mMaxDigitsLabel, mMinDigitsLabel, true), mMainFrameRight + 5, mMainFrameTop + (yInterval * (mMax - y)) + fontHeight / 2, "l", ContextCompat.getColor(getContext(), R.color.chart_label_text), mTextSize);
            drawDottedLine(canvas, mMainFrameLeft, mMainFrameTop + (yInterval * (mMax - y)), mMainFrameRight, mMainFrameTop + (yInterval * (mMax - y)), ContextCompat.getColor(getContext(), R.color.chart_aux));
        }
        if (y == mMin) {
            //drawString(canvas, getFormatedValue(y, false, mMaxDigitsLabel, mMinDigitsLabel, true), mMainFrameRight + 5, mMainFrameBottom, "l", ContextCompat.getColor(getContext(), R.color.chart_label_text), mTextSize);
        }

        int colorLine = 0xAA0000FF;
        for (String key : mChartDrawData.keySet()) {

            List<FinnDaily> portfolio = mChartDrawData.get(key).get(mCurrentMonth);
            if (portfolio.size() > 0) {
                Date[] date = new Date[displayPlotNum];
                float[] amount = new float[displayPlotNum];
                int dataNum = 0;
                for (int i = 0; i < displayPlotNum; i++, dataNum++) {
                    if (i < portfolio.size() && dataNum < displayPlotNum) {
                        date[dataNum] = portfolio.get(i).date;
                        amount[dataNum] = portfolio.get(i).getAmount();
                    }
                }

                // price
                float currentAmount, nextAmount;
                float x = xInterval / 2;
                for (int i = 0; i < dataNum - 1; i++) {
                    nextAmount = amount[i + 1];
                    currentAmount = amount[i];
                    drawSmoothLine(canvas, x, mMainFrameTop + (mMax - currentAmount) * yInterval, x + xInterval, mMainFrameTop + (mMax - nextAmount) * yInterval,
                            colorLine, LINE_WIDTH);
                    x += xInterval;
                }
                colorLine += 5000;
            }
        }

        canvas.drawRect(mMainFrameArea, mPaintFrame);
    }

    private void drawSmoothLine(Canvas c, float x1, float y1, float x2, float y2, int color, float lineWidth) {
        mPaintSmoothLine.setStrokeWidth(lineWidth);
        mPaintSmoothLine.setColor(color);

        c.drawLine(x1, y1, x2, y2, mPaintSmoothLine);
    }

    private void drawDottedLine(Canvas c, float x1, float y1, float x2, float y2, int color) {
        drawDottedLine(c, x1, y1, x2, y2, color, LINE_WIDTH);
    }

    private void drawDottedLine(Canvas c, float x1, float y1, float x2, float y2, int color, float lineWidth) {
        Paint dp = new Paint();

        dp.setPathEffect(new DashPathEffect(new float[]{5, 3}, 0));
        dp.setStyle(Paint.Style.STROKE);
        dp.setStrokeWidth(lineWidth);
        dp.setColor(color);

        Path path = new Path();
        path.moveTo(x1, y1);
        path.lineTo(x2, y2);
        c.drawPath(path, dp);
    }

    private void drawString(Canvas c, String str, float x, float y, String align, int color, float textSize) {
        mPaintText.setTextSize(textSize);

        mPaintText.setColor(color);
        switch (align) {
            case "r":
                mPaintText.setTextAlign(Paint.Align.RIGHT);
                break;
            case "l":
                mPaintText.setTextAlign(Paint.Align.LEFT);
                break;
            default:
                mPaintText.setTextAlign(Paint.Align.CENTER);
                break;
        }

        c.drawText(str, x, y, mPaintText);
    }


    private String getDisplayDate(@NonNull Date date) {
        switch (mChartType) {
            case TYPE_DAILY:
                SimpleDateFormat daily = new SimpleDateFormat("dd/MM", Locale.US);
                return daily.format(date);

            case TYPE_MONTHLY:
                SimpleDateFormat month = new SimpleDateFormat("MMM", Locale.US);
                return month.format(date);

            case TYPE_QUARTER:
                return date.toString();

            default:
                return date.toString();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);

        if (MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY) {
            height = (int) (width / NORMAL_ASPECT_RATIO);

            Configuration config = getResources().getConfiguration();
            boolean isLandscape = (config.orientation == Configuration.ORIENTATION_LANDSCAPE);
            if (isLandscape) {
                height = (int) (0.75 * displayHeight);
            }
        }

        setMeasuredDimension(width, height);
    }

    public static String getFormatedValue(double value, boolean addPlus, int maximumFractionDigits, int minimumFractionDigits, boolean allowZero) {
        String ret = "---";

        if (value == 0 && !allowZero) {
            return ret;
        } else if (value == 0 && allowZero) {
            return "0";
        }

        try {
            NumberFormat mNumberFormat = NumberFormat.getNumberInstance();
            mNumberFormat.setMaximumFractionDigits(maximumFractionDigits);
            mNumberFormat.setMinimumFractionDigits(minimumFractionDigits);

            ret = mNumberFormat.format(value);

            if (addPlus && value > 0) {
                ret = "+" + ret;
            }
        } catch (Exception ignored) {

        }

        return ret;
    }
}
