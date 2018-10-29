package me.varunon9.saathmetravel.utils;

public class FirestoreQuery {

    private int conditionCode;
    private String field;
    private Object value;

    public FirestoreQuery(int conditionCode, String field, Object value) {
        this.conditionCode = conditionCode;
        this.field = field;
        this.value = value;
    }

    public int getConditionCode() {
        return conditionCode;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}
