package db.jni.result;

import java.io.Serializable;

public class TrendResult implements Serializable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 时间
	 */
	private long time;
	
	/**
	 * 数值
	 */
	private long value;

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getValue() {
		return value;
	}

	public void setValue(long value) {
		this.value = value;
	}
}
