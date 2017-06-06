package com.ggdsn.jkl.statisticview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "MainActivity";
	private static final int LENGTH_CHECKSUM = 4;
	private StatisticView statisticView;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		statisticView = (StatisticView) findViewById(R.id.statisticView);
		List<StatisticView.Data> data = new ArrayList<>();
		Random random = new Random();
		//for (int i = 0; i < 10; i++) {
		//	data.add(
		//		new OrderStatisticView.Data(String.valueOf(random.nextInt(12) + 1) + "/" + (random.nextInt(31) + 1),
		//			random.nextInt(10) + 1, random.nextInt(3)));
		//}

		for (int i = 0; i < 7; i++) {
			StatisticView.Data d =
				new StatisticView.Data(String.valueOf(random.nextInt(12) + 1) + "/" + (random.nextInt(31) + 1),
					random.nextInt(10) + 1, false ? StatisticView.Data.TYPE_LIGHT : StatisticView.Data.TYPE_NORMAL);
			data.add(d);
		}

		statisticView.setData(data);
		//long res = daysDistance(1486899239559L, 1486982083000L);
		//long res = daysDistance(1486899239559L, 1486899539559L);
		//Log.i(TAG, "dis=" + res);

		//for (int i = 0; i < 256; i++) {
		//	test(i);
		//}
		test();

		byte[] wholeFragment = getWholeFragment(new byte[] { 1, 2 }, new byte[] { 3, 4,5,6 });
		Log.i(TAG, Arrays.toString(wholeFragment));
	}

	public void test(int i) {
		byte[] bytes = { 01, 05, (byte) i };
		byte[] checksum = Utils.checksum(bytes, 0, bytes.length);
		Log.i(TAG, "i=" + i + " checksum=" + Utils.bytesToHexString(checksum));
	}

	public void test() {
		byte[] bytes = { 01, 0x0a, 00, (byte) 0XFF, (byte) 0XFF, (byte) 0XFF, (byte) 0XFF, (byte) 0XFF };
		byte[] checksum = Utils.checksum(bytes, 0, bytes.length);
		Log.i(TAG, " checksum=" + Utils.bytesToHexString(checksum));
	}

	byte[] getWholeFragment(byte[] datagram, byte[] checksum) {
		if (datagram != null) {
			byte[] bytes = new byte[datagram.length + LENGTH_CHECKSUM];
			System.arraycopy(datagram, 0, bytes, 0, datagram.length);
			System.arraycopy(checksum, 0, bytes, datagram.length, LENGTH_CHECKSUM);
			return bytes;
		} else {
			return checksum;
		}
	}
	/**
	 * 判断是否是今天
	 *
	 * @return 返回0是今天。若返回正数，代表该日期在今天之后。若返回负数，代表该日期在今天之前
	 */
	public static int isToday(long time) {
		Calendar sDefaultCalendar = Calendar.getInstance();
		sDefaultCalendar.setTimeInMillis(System.currentTimeMillis());
		sDefaultCalendar.set(Calendar.HOUR_OF_DAY, 0);
		sDefaultCalendar.set(Calendar.MINUTE, 0);
		sDefaultCalendar.set(Calendar.SECOND, 0);
		Date today = sDefaultCalendar.getTime();
		sDefaultCalendar.add(Calendar.HOUR_OF_DAY, 24);
		Date tomorrow = sDefaultCalendar.getTime();
		Log.i(TAG, "today=" + today + " tomorrow=" + tomorrow);
		if (time >= tomorrow.getTime()) {
			return 1;
		} else if (time < today.getTime()) {
			return -1;
		} else {
			return 0;
		}
	}

	public static long daysDistance(long start, long end) {
		Calendar sDefaultCalendar = Calendar.getInstance();
		sDefaultCalendar.setTimeInMillis(start);
		sDefaultCalendar.set(Calendar.HOUR_OF_DAY, 0);
		sDefaultCalendar.set(Calendar.MINUTE, 0);
		sDefaultCalendar.set(Calendar.SECOND, 0);
		sDefaultCalendar.set(Calendar.MILLISECOND, 0);
		long startDate = sDefaultCalendar.getTimeInMillis();
		sDefaultCalendar.setTimeInMillis(end);
		sDefaultCalendar.set(Calendar.HOUR_OF_DAY, 0);
		sDefaultCalendar.set(Calendar.MINUTE, 0);
		sDefaultCalendar.set(Calendar.SECOND, 0);
		sDefaultCalendar.set(Calendar.MILLISECOND, 0);
		long endDate = sDefaultCalendar.getTimeInMillis();
		return TimeUnit.DAYS.convert(endDate - startDate, TimeUnit.MILLISECONDS);
	}
}
