package db.jni.result;

import java.io.Serializable;
import java.util.LinkedList;

public class AggResult implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 聚合值
	 */
	private LinkedList<Object> aggList = new LinkedList<Object>();

	/**
	 * 聚合数量
	 */
	private long count;

	public Object[] getAggValue() {
		return aggList.toArray();
	}

	public void addAgg(Object agg) {
		this.aggList.addLast(agg);
	}
	
	public LinkedList<Object> getAggList() {
		return aggList;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
