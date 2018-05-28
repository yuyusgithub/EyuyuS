package db.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 彦祖 .
 */
public class Student {

    public Student() {
    }

    public Student(String name, Integer s1, Integer s2, Integer s3) {
        this.name = name;
        this.s1 = s1;
        this.s2 = s2;
        this.s3 = s3;
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmss");
        String currentTimeStr = sdf.format(new Date(currentTime));
        this.time = currentTime;
        this.timestr = currentTimeStr;
    }

    private String name;

    private Integer s1;

    private Integer s2;

    private Integer s3;

    private String timestr;

    private Long time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getS1() {
        return s1;
    }

    public void setS1(Integer s1) {
        this.s1 = s1;
    }

    public Integer getS2() {
        return s2;
    }

    public void setS2(Integer s2) {
        this.s2 = s2;
    }

    public Integer getS3() {
        return s3;
    }

    public void setS3(Integer s3) {
        this.s3 = s3;
    }

    public String getTimestr() {
        return timestr;
    }

    public void setTimestr(String timestr) {
        this.timestr = timestr;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
