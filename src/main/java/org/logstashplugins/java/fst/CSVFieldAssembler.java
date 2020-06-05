package org.logstashplugins.java.fst;

import java.util.HashMap;
import java.util.Map;

public class CSVFieldAssembler implements IFieldAssember {
	Map<String, Object> fieldsMap;

	CSVFieldAssembler() {
		fieldsMap = new HashMap<String, Object>();
	}

	@Override
	public void addValueToFiled(String fieldName, Object fieldValue) {
		Object stringBuffer = fieldsMap.get(fieldName);
		if (stringBuffer == null) {
			StringBuffer sb = new StringBuffer();
			fieldsMap.put(fieldName, sb);
			sb.append(fieldValue);
		} else {
			((StringBuffer) stringBuffer).append("," + fieldValue);
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		return fieldsMap.get(fieldName).toString();
	}

}
