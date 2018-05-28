package db.jni.result;

import java.io.Serializable;

public class MultiTrendResult implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * 时间
	 */
	private long time;
	
	/**
	 * 数值
	 */
	private double[] value;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public double[] getValue() {
		return value;
	}

	public void setValue(double[] value) {
		this.value = value;
	}
}
