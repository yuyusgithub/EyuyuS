package db.jni.condition;


public class AggCon extends SearchCon {

    private static final long serialVersionUID = 1L;

    /**
     * 聚合字段
     */
    private String[] aggField;

    public String[] getAggField() {
        return aggField;
    }

    public void setAggField(String... aggField) {
        this.aggField = aggField;
    }


}
