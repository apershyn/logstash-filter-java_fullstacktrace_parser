package org.logstashplugins.java.fst;

import org.junit.Assert;
import org.junit.Test;

public class FieldAssemblerFactoryTest {

	@Test
	public void testAddValueToFiled() {
		IFieldAssember assemblerClass = FieldAssemblerFactory.get(JavaStackTraceParser.CSV);
		Assert.assertTrue(CSVFieldAssembler.class.isAssignableFrom(assemblerClass.getClass()));
		assemblerClass = FieldAssemblerFactory.get(JavaStackTraceParser.ARRAY);
		Assert.assertTrue(ArrayFieldAssembler.class.isAssignableFrom(assemblerClass.getClass()));
	}

}
