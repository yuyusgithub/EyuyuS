package db.jni.condition;

import java.io.Serializable;

public class SearchCon implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 索引名称
	 */
	private String[] indexName;
	
	/**
	 * 索引类型
	 */
	private String typeName;
	
	/**
	 * 排序方式 true升序 false降序
	 */
	private boolean asc;
	
	/**
	 * 起始游标
	 */
	private Integer cursor;
	
	/**
	 * 查询结果集的数量限制
	 */
	private Integer limit = 10;
	
	/**
	 * 排序字段
	 */
	private String sortField;
	
	/**
	 * 条件集合
	 */
	private Operation[] opers;

	public String[] getIndexName() {
		return indexName;
	}

	public void setIndexName(String... indexName) {
		this.indexName = indexName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}


	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	public Integer getCursor() {
		return cursor;
	}

	public void setCursor(Integer cursor) {
		this.cursor = cursor;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public Operation[] getOpers() {
		return opers;
	}

	public void setOpers(Operation... opers) {
		this.opers = opers;
	}
}
