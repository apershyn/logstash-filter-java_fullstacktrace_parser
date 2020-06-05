package org.logstashplugins.java.fst;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayFieldAssembler implements IFieldAssember {

	Map<String, Object> fieldsMap;

	ArrayFieldAssembler() {
		fieldsMap = new HashMap<String, Object>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addValueToFiled(String fieldName, Object fieldValue) {
		Object arrayList = fieldsMap.get(fieldName);
		if (arrayList == null) {
			List<Object> list = new ArrayList<Object>();
			fieldsMap.put(fieldName, list);
			list.add(fieldValue);
		} else {
			((List<Object>) arrayList).add(fieldValue);
		}
	}

	@Override
	public Object getFieldValue(String fieldName) {
		return fieldsMap.get(fieldName).toString();
	}


}
