package org.logstashplugins.java.fst;

public class FieldAssemblerFactory {

	public static IFieldAssember get(String outputPattern) {
		if (outputPattern.equals(JavaStackTraceParser.CSV)) {
			return new CSVFieldAssembler();
		} else if (outputPattern.equals(JavaStackTraceParser.ARRAY)) {
			return new ArrayFieldAssembler();
		}
		throw new IllegalArgumentException("output pattern " +outputPattern +" is not supported");
	}

}
