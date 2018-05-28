package db.jni.condition;

public class Operation {
	
	private String field;
	
	private Object value;
	
	private Object start;
	
	private Object end;
	
	private Oper oper;

	private Operation(String field, Oper oper, Object value) {
		this.field = field;
		this.oper = oper;
		this.value = value;
	}
	
	private Operation(String field, Oper oper, Object start, Object end) {
		this.field = field;
		this.oper = oper;
		this.start = start;
		this.end = end;
	}
	
	public static Operation lt(String field, Object value) {
		return new Operation(field, Oper.LT, value);
	}
	
	public static Operation lte(String field, Object value) {
		return new Operation(field, Oper.LTE, value);
	}
	
	public static Operation gt(String field, Object value) {
		return new Operation(field, Oper.GT, value);
	}
	
	public static Operation gte(String field, Object value) {
		return new Operation(field, Oper.GTE, value);
	}
	
	public static Operation eq(String field, Object value) {
		return new Operation(field, Oper.EQ, value);
	}
	
	public static Operation neq(String field, Object value) {
		return new Operation(field, Oper.NEQ, value);
	}
	
	public static Operation between(String field, Object start, Object end) {
		return new Operation(field, Oper.BETWEEN, start, end);
	}
	
	public static Operation matchAll(Object value) {
		return new Operation(null, Oper.MATCHALL, value);
	}
	
	public static Operation prefix(String field, Object value) {
		return new Operation(field, Oper.PREFIX, value);
	}
	
	public static Operation should(String field, Object value) {
		return new Operation(field, Oper.SHOULD, value);
	}

	public String getField() {
		return field;
	}

	public Object getValue() {
		return value;
	}

	public Object getStart() {
		return start;
	}

	public Object getEnd() {
		return end;
	}

	public Oper getOper() {
		return oper;
	}
	
}
