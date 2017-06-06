package com.ggdsn.jkl.statisticview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import java.util.List;
import java.util.Random;

/**
 * 这个类可以显示一组数据（{@link Data})的直方图。可以使用{@link #setTitle(String)}指定标题。<br/> {@link Data}有三种样式，分别是{@link
 * Data#TYPE_NORMAL}、{@link
 * Data#TYPE_DARK}和{@link Data#TYPE_LIGHT}。<br/> 其中最右边的TYPE_LIGHT一定会显示它对应的标题（{@link
 * Data#setName(String)}），另外可能会根据情况显示左中右三个标题。
 * <br/>
 * Created by LiaoXingyu on 07/2/2017.
 */
public class StatisticView extends View {

	private static final int BAR_HEIGHT = 70;
	private static final int MARGIN_TOP = 10;
	private static final int MARGIN_BOTTOM = 10;
	private static final int MARGIN_LEFT = 15;
	private static final int MARGIN_RIGHT = 15;
	private static final int BAR_GAP = 5;
	private static final int TEXT_GAP = 5;
	private static final int FRAMES = 60;
	private static final int TITLE_HEIGHT = 40;
	private static final int NAME_SIZE = 32;
	private static final int UNIT_HEIGHT = 20;
	private static final int DARK_COLOR = 0xFF8C8C8C;
	private static final int LIGHT_COLOR = 0xFFA7CD45;
	private static final String TAG = "StatisticView";

	private int barWidth;
	private int barHeight;
	private int barGap;
	private int innerMarginTop;
	private int innerMarginBottom;
	private int innerMarginLeft;
	private int innerMarginRight;
	private int barStartX;
	/**
	 * 绘制图标的起始Y值，自底向上绘制
	 */
	private int barStartY;
	private Data maxNumber;
	private int growthPerFrame;
	private Data[] data;
	private Paint normalPaint;
	private Paint darkPaint;
	private Paint lightPaint;
	private Paint namePaint;
	private RectF mBarRectF;
	private String title;
	private int titleHeight;
	/**
	 * 底部日期高度
	 */
	private int unitHeight;
	private int titleX;
	private int titleBaseLine;
	private int nameSize;
	private int textGap;

	public StatisticView(Context context) {
		super(context);
		init(context, null, 0);
	}

	public StatisticView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs, 0);
	}

	public StatisticView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public StatisticView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs, defStyleAttr);
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr) {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		barHeight = (int) dip2px(metrics, BAR_HEIGHT);
		barGap = (int) dip2px(metrics, BAR_GAP);
		textGap = (int) dip2px(metrics, TEXT_GAP);
		innerMarginTop = (int) dip2px(metrics, MARGIN_TOP);
		innerMarginBottom = (int) dip2px(metrics, MARGIN_BOTTOM);
		innerMarginLeft = (int) dip2px(metrics, MARGIN_LEFT);
		innerMarginRight = (int) dip2px(metrics, MARGIN_RIGHT);
		nameSize = (int) dip2px(metrics, NAME_SIZE);
		barStartX = innerMarginLeft;
		normalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		normalPaint.setColor(Color.WHITE);
		normalPaint.setTextSize(TITLE_HEIGHT);
		darkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		darkPaint.setColor(DARK_COLOR);
		darkPaint.setTextSize(TITLE_HEIGHT);
		darkPaint.setTextAlign(Paint.Align.CENTER);
		lightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		lightPaint.setColor(LIGHT_COLOR);
		namePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		namePaint.setColor(DARK_COLOR);
		namePaint.setTextSize(NAME_SIZE);
		namePaint.setTextAlign(Paint.Align.CENTER);
		mBarRectF = new RectF();

		unitHeight = (int) dip2px(metrics, UNIT_HEIGHT);

		Drawable background = getBackground();
		if (background == null) {
			setBackgroundColor(Color.DKGRAY);
		}
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.StatisticView, 0, 0);
		try {
			title = a.getString(R.styleable.StatisticView_title);
			// TODO: 09/02/2017 增加自定义圆柱颜色的方法
		} finally {
			a.recycle();
		}

		if (isInEditMode()) {
			if (TextUtils.isEmpty(title)) {
				title = "分析视图";
			}
			Random random = new Random();
			data = new Data[random.nextInt(12) + 20];
			for (int i = 0; i < data.length; i++) {
				StatisticView.Data d =
					new StatisticView.Data(String.valueOf(random.nextInt(12) + 1) + "/" + (random.nextInt(31) + 1),
						random.nextInt(10) + 1, random.nextInt(3));
				data[i] = d;
			}
		}
		titleHeight = TextUtils.isEmpty(title) ? 0 : (int) dip2px(metrics, TITLE_HEIGHT);
	}

	@Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		int paddingBottom = getPaddingBottom();
		int paddingTop = getPaddingTop();
		int paddingLeft = getPaddingLeft();
		//求图形的起点和总长
		barStartY = height - paddingBottom - unitHeight - innerMarginBottom;
		barHeight = height - titleHeight - unitHeight - paddingTop - paddingBottom - innerMarginBottom - innerMarginTop;
		barWidth = (width - innerMarginLeft - innerMarginRight - barGap * 29) / 30;
		growthPerFrame = (int) (barHeight / (float) FRAMES);

		//求标题基点
		if (titleHeight > 0) {
			titleX = paddingLeft + innerMarginLeft;
			titleBaseLine = paddingTop + innerMarginTop + titleHeight / 2;
		}

		if (data == null || data.length == 0) {
			return;
		}

		int length = data.length;
		int idxLast = length - 1;
		int idxHalf = length / 2;

		Data max = data[0];
		int maxTextWidth = 0;
		Data maxLight = null;
		for (int i = 0; i < length; i++) {
			Data d = data[i];
			d.index = i;
			//求最大的值
			if (d.value > max.value) {
				max = d;
			}
			//求最大的字符串长度
			int textWidth = (int) namePaint.measureText(d.name);
			if (textWidth > maxTextWidth) {
				maxTextWidth = textWidth;
			}
			if (d.type == Data.TYPE_LIGHT) {
				maxLight = d;
			}
		}
		maxNumber = max;

		int itemWidth = barWidth + barGap;
		int textSectionWidth = maxTextWidth + textGap;
		int totalLength = (idxLast) * (barGap + barWidth) + maxTextWidth;
		if (totalLength > width) {
			totalLength = width;
		}

		if (maxLight != null) {
			maxLight.drawName = true;
			if (totalLength > textSectionWidth) {
				if (!willOverlap(maxLight.index, 0, itemWidth, maxTextWidth)) {
					data[0].drawName = true;
				}
				if (!willOverlap(maxLight.index, idxLast, itemWidth, maxTextWidth)) {
					data[idxLast].drawName = true;
				}

				if (totalLength >= textSectionWidth * 3) {
					if (!willOverlap(maxLight.index, idxHalf, itemWidth, maxTextWidth)) {
						data[idxHalf].drawName = true;
					}
				}
			}
		} else {
			if (totalLength < textSectionWidth * 2) {
				//只能容纳一段文字
				data[idxHalf].drawName = true;
			} else if (totalLength < textSectionWidth * 3) {
				//可以容纳两段文字
				data[0].drawName = true;
				data[idxLast].drawName = true;
			} else {
				//可容纳3段文字
				data[0].drawName = true;
				data[idxLast].drawName = true;
				data[idxHalf].drawName = true;
			}
		}
	}

	private static boolean willOverlap(int item1, int item2, int itemWidth, int safeDistance) {
		return Math.abs((item1 - item2)) * itemWidth < safeDistance;
	}

	@Override protected void onDraw(Canvas canvas) {
		if (titleHeight > 0) {
			canvas.drawText(title, titleX, titleBaseLine, normalPaint);
		}
		if (data == null || data.length == 0) {
			canvas.drawText("暂无数据", getWidth() / 2, getHeight() / 2, darkPaint);
			return;
		}

		// TODO: 05/12/2016 add animation
		int startX = barStartX;
		Paint drawPaint;
		for (Data number : data) {
			mBarRectF.set(startX, maxNumber.value == 0 ? barStartY
				: (int) (barStartY - (float) number.value / maxNumber.value * barHeight), startX + barWidth, barStartY);
			switch (number.type) {
				default:
				case Data.TYPE_NORMAL:
					drawPaint = normalPaint;
					break;
				case Data.TYPE_DARK:
					drawPaint = darkPaint;
					break;
				case Data.TYPE_LIGHT:
					drawPaint = lightPaint;
					break;
			}
			canvas.drawRoundRect(mBarRectF, barWidth / 2, barWidth / 2, drawPaint);
			if (number.drawName) {
				canvas.drawText(number.name, startX + barWidth / 2, barStartY + nameSize / 2, namePaint);
			}
			startX += (barWidth + barGap);
		}
	}

	/**
	 * 根据手机的分辨率从 dp 转成 px
	 */
	private static float dip2px(DisplayMetrics metrics, float dpValue) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, metrics);
	}

	public void setData(Data[] data) {
		this.data = data;
		invalidate();
	}

	public void setData(List<Data> numbers) {
		if (numbers == null || numbers.size() == 0) {
			data = null;
			invalidate();
			return;
		}

		setData(numbers.toArray(new Data[numbers.size()]));
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public static class Data {
		public static final int TYPE_NORMAL = 0;
		public static final int TYPE_LIGHT = 1;
		public static final int TYPE_DARK = 2;

		String name;
		int value;
		int type;
		/**
		 * 内部作为文字显示使用
		 */
		boolean drawName;
		int index;

		public Data(String name, int value) {
			this(name, value, 0);
		}

		public Data(String name, int value, int type) {
			this.name = name;
			this.value = value;
			this.type = type;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}
	}
}
