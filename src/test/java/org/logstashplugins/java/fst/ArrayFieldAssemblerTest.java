package org.logstashplugins.java.fst;

import org.junit.Assert;
import org.junit.Test;

public class ArrayFieldAssemblerTest {

	@Test
	public void testAddValueToFiled() {
		ArrayFieldAssembler fa = new ArrayFieldAssembler();
		fa.addValueToFiled("field1", "value1");
		fa.addValueToFiled("field1", "value2");
		fa.addValueToFiled("field2", 2);
		fa.addValueToFiled("field2", 3);
		Object field1Value = fa.getFieldValue("field1");
		Object field2Value = fa.getFieldValue("field2");
		Assert.assertTrue(field1Value instanceof String);
		Assert.assertTrue(field2Value instanceof String);
		Assert.assertEquals(field1Value.toString(), "[value1, value2]");
		Assert.assertEquals(field2Value.toString(), "[2, 3]");

	}

}
