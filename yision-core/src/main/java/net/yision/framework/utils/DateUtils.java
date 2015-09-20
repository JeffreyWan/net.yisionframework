package net.yision.framework.utils;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Created by Jeffrey on 15/9/20.
 * @author Jeffrey
 */
public class DateUtils {
    /**
     * 将日期转换成只有年月日的日期，其余默认为0。
     *
     * @param date 转换用日期
     * @return yyyy-MM-dd日期
     */
    public static Date convertToYYYYMMDD(Date date) {
        DateTime tmpTime = new DateTime(date);
        return new DateTime(
                tmpTime.getYear(),
                tmpTime.getMonthOfYear(),
                tmpTime.getDayOfMonth(),
                0, 0, 0
        ).toDate();
    }

    /**
     * 返回标准日期格式字符串对应的日期。
     *
     * @param dateStr 标准日期格式字符串（标准格式：yyyy-MM-dd）
     * @return yyyy-mm-dd格式对应的日期
     */
    public static Date convertToYYYYMMDD(String dateStr) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        return fmt.parseDateTime(dateStr).toDate();
    }

    /**
     * 返回日期的标准字符串描述（标准格式：yyyy-MM-dd）。
     *
     * @param date 日期
     * @return
     */
    public static String convertToYYYYMMDDStr(Date date) {
        return new DateTime(date).toString("yyyy-MM-dd");
    }

    /**
     * 返回日期的标准字符串描述（标准格式：yyyy-MM-dd hh:mm）。
     *
     * @param date 日期
     * @return
     */
    public static String convertToYYYYMMDDHHStr(Date date) {
        return date == null ? null : new DateTime(date).toString("yyyy-MM-dd kk:mm");
    }

    /**
     * 计算两个日期间隔天数。
     *
     * @param fromDate 起始时间
     * @param toDate   结束时间
     * @return 时间间隔（天数）
     */
    public static int calculatePeiorDaysOfTwoDate(Date fromDate, Date toDate) {
        Period period = new Period(new DateTime(fromDate), new DateTime(toDate), PeriodType.days());
        return period.getDays();
    }

    public static int calculateDaysOfTwoDateIgnoreHours(Date former, Date latter) {
        Calendar formerCalendar = Calendar.getInstance();
        Calendar latterCalendar = Calendar.getInstance();
        formerCalendar.setTime(former);
        latterCalendar.setTime(latter);
        formerCalendar.set(
                formerCalendar.get(Calendar.YEAR),
                formerCalendar.get(Calendar.MONTH),
                formerCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0
        );
        latterCalendar.set(
                latterCalendar.get(Calendar.YEAR),
                latterCalendar.get(Calendar.MONTH),
                latterCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0
        );

        return calculatePeiorDaysOfTwoDate(formerCalendar.getTime(), latterCalendar.getTime());

    }

    /**
     * 计算两个日期间隔分钟。
     *
     * @param fromDate 起始时间
     * @param toDate   结束时间
     * @return 时间间隔（分钟）
     */
    public static int calculatePeiorMiniutesOfTwoDate(Date fromDate, Date toDate) {
        Period period = new Period(new DateTime(fromDate), new DateTime(toDate), PeriodType.minutes());
        return period.getMinutes();
    }

    /**
     * 判断当前时间是星期几
     */
    public static int calculateTodayForWeek() {
        Calendar c = Calendar.getInstance();
        int dayForWeek = 0;
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            dayForWeek = 7;
        } else {
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    public static String calculateTodayForWeek(Date date, int num) {

        String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, num);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (week_index < 0) {
            week_index = 0;
        }
        return weeks[week_index];
    }

    /**
     * 获取所有的节假日
     *
     * @return
     */
    public static List getAlLHoliday() {
        List list = new ArrayList();
        String path = DateUtils.class.getClassLoader().getResource("holiday.txt").getPath();
        File file = new File(path);
        if (file.isFile() && file.exists()) {
            try {
                InputStreamReader isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(isr);
                String holiday;
                while ((holiday = bufferedReader.readLine()) != null) {
                    list.add(holiday);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    /**
     * 添加天数
     * @param date
     * @param num
     * @return
     */
    public static Date addDay(Date date, int num){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, num);
        return calendar.getTime();
    }

    public static boolean oneDayDifference(Date oldDate, Date newDate) {
        DateUtils.convertToYYYYMMDD(oldDate);
        DateUtils.convertToYYYYMMDD(newDate);
        if (DateUtils.convertToYYYYMMDD(oldDate).equals(DateUtils.convertToYYYYMMDD(newDate))) {
            return false;
        } else {
            return true;
        }
    }

    // 按照24小时计算天数
    // TODO：继续优化方法
    public static int calculatePeiorDaysOfTwoDateWith24(Date from, Date to) {

        Calendar cfrom = Calendar.getInstance();
        Calendar cto = Calendar.getInstance();
        cfrom.setTime(from);
        cto.setTime(to);
        cfrom.set(Calendar.HOUR_OF_DAY, 0);
        cfrom.set(Calendar.MINUTE, 0);
        cfrom.set(Calendar.SECOND, 0);
        cfrom.set(Calendar.MILLISECOND, 0);

        cto.set(Calendar.HOUR_OF_DAY, 0);
        cto.set(Calendar.MINUTE, 0);
        cto.set(Calendar.SECOND, 0);
        cto.set(Calendar.MILLISECOND, 0);

        return DateUtils.calculatePeiorDaysOfTwoDate(cfrom.getTime(), cto.getTime());
    }

    //当周开始时间
    public static Date getCurrentDate() {
        Calendar currentDate = new GregorianCalendar();
        currentDate.setFirstDayOfWeek(Calendar.MONDAY);

        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);
        currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return currentDate.getTime();
    }

}
