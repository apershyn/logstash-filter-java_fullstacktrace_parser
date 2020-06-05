package org.logstashplugins.java.fst;

public interface IFieldAssember {
	void addValueToFiled(String fieldName, Object fieldValue);
	Object getFieldValue(String fieldName);

}
