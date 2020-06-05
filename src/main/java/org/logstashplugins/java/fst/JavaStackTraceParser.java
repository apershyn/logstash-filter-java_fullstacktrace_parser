package org.logstashplugins.java.fst;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Context;
import co.elastic.logstash.api.Event;
import co.elastic.logstash.api.Filter;
import co.elastic.logstash.api.FilterMatchListener;
import co.elastic.logstash.api.LogstashPlugin;
import co.elastic.logstash.api.PluginConfigSpec;

// class name must match plugin name
@LogstashPlugin(name = "java_stacktrace_parser")
public class JavaStackTraceParser implements Filter {
	public static final String CSV = "csv";
	public static final String ARRAY = "array";

	public static final PluginConfigSpec<String> EXCEPTION_PATTERN = PluginConfigSpec.stringSetting("exception_pattern",
			"((\\w[\\w\\.]+): (.*)?)|(Caused by: (\\w[\\w\\.]+): (.*)?)");
	private static List<Object> EXCEPTION_TYPE_GROUPS = new ArrayList<Object>(Arrays.asList(2, 5));
	private static List<Object> EXCEPTION_MESSSAGE_GROUPS = new ArrayList<Object>(Arrays.asList(3, 6));
	public static final PluginConfigSpec<List<Object>> EXCEPTION_PATTERN_EXCEPTION_GROUPS = PluginConfigSpec
			.arraySetting("exception_type_groups", EXCEPTION_TYPE_GROUPS, false, false);
	public static final PluginConfigSpec<List<Object>> EXCEPTION_PATTERN_MESSAGE_GROUPS = PluginConfigSpec
			.arraySetting("exception_message_groups", EXCEPTION_MESSSAGE_GROUPS, false, false);
	public static final PluginConfigSpec<Long> JAVA_CLASS_GROUP = PluginConfigSpec.numSetting("java_class_group", 1,
			false, false);
	public static final PluginConfigSpec<Long> METHOD_GROUP = PluginConfigSpec.numSetting("method_group", 2, false,
			false);
	public static final PluginConfigSpec<Long> SOURCE_FILE_GROUP = PluginConfigSpec.numSetting("source_file_group", 3,
			false, false);
	public static final PluginConfigSpec<Long> LINE_NUMBER_GROUP = PluginConfigSpec.numSetting("line_number_group", 4,
			false, false);

	public static final PluginConfigSpec<String> SOURCE_FIELD = PluginConfigSpec.stringSetting("source_field",
			"full_stack_trace");
	public static final PluginConfigSpec<String> TRACE_PATTERN = PluginConfigSpec.stringSetting("trace_pattern",
			"\\s+at\\s+([\\w\\.$]+)\\.([\\w$]+)\\((.*java)?:(\\d+)\\)(\\s{1,2}|$)");
	public static final PluginConfigSpec<Boolean> REMOVE_SOURCE_FIELD = PluginConfigSpec
			.booleanSetting("remove_source_filed", true);

	public static final PluginConfigSpec<String> OUTPUT_PATTERN = PluginConfigSpec.stringSetting("output_pattern", CSV);
	private String id;
	private String sourceField;
	private Pattern tracePattern;
	private Pattern exceptionPattern;
	private boolean removeSourceField;
	private String outputPattern;
	private IFieldAssember fieldAssembler;
	private Long javaClassGroup;
	private List<Integer> exceptionGroups;
	private List<Integer> messageGroups;
	private Long methodGroup;
	private Long sourceFileGroup;
	private Long lineNumberGroup;

	public enum FIELDS {
		EXCEPTIONS("exception_classes"), MESSAGES("exception_messages"), JAVA_CLASSES("java_classes"),
		METHODS("methods"), SOURCE_FILE("source_files"), LINE_NUMBERS("line_numbers");

		private String fieldName;

		FIELDS(String name) {
			fieldName = name;
		}

		public String getFieldName() {
			return fieldName;
		}
	};

	public JavaStackTraceParser(String id, Configuration config, Context context) {
		this.id = id;
		this.sourceField = config.get(SOURCE_FIELD);
		try {
			this.exceptionPattern = Pattern.compile(config.get(EXCEPTION_PATTERN));
			this.tracePattern = Pattern.compile(config.get(TRACE_PATTERN));
		} catch (IllegalArgumentException e) {
			Exception e1 = new Exception();
			e1.printStackTrace();
			throw new RuntimeException("Pattern is not valid", e);
		}
		this.outputPattern = config.get(OUTPUT_PATTERN);
		if (!(this.outputPattern.equals(CSV)) && !(this.outputPattern.equals(ARRAY))) {
			throw new IllegalArgumentException("Plugin doesn't support output_pattern " + outputPattern);
		}
		this.removeSourceField = config.get(REMOVE_SOURCE_FIELD);
		this.fieldAssembler = FieldAssemblerFactory.get(outputPattern);

		this.exceptionGroups = new ArrayList<Integer>();
		for (Object object : config.get(EXCEPTION_PATTERN_EXCEPTION_GROUPS)) {
			exceptionGroups.add((int) (object));
		}
		this.messageGroups = new ArrayList<Integer>();
		for (Object object : config.get(EXCEPTION_PATTERN_MESSAGE_GROUPS)) {
			messageGroups.add((int) (object));
		}
		if (exceptionGroups.size() != messageGroups.size()) {
			throw new IllegalArgumentException(
					"The size of exception groups must be equal to the size of message groups");
		}
		this.javaClassGroup = config.get(JAVA_CLASS_GROUP);
		this.methodGroup = config.get(METHOD_GROUP);
		this.sourceFileGroup = config.get(SOURCE_FILE_GROUP);
		this.lineNumberGroup = config.get(LINE_NUMBER_GROUP);
	}

	@Override
	public Collection<PluginConfigSpec<?>> configSchema() {
		// should return a list of all configuration options for this plugin
		Collection<PluginConfigSpec<?>> schemas = new ArrayList<PluginConfigSpec<?>>(
				Arrays.asList(SOURCE_FIELD, EXCEPTION_PATTERN, TRACE_PATTERN, REMOVE_SOURCE_FIELD, OUTPUT_PATTERN));
		return schemas;
	}

	@Override
	public Collection<Event> filter(Collection<Event> events, FilterMatchListener matchListener) {

		for (Event event : events) {
			Object f = event.getField(sourceField);
			if (f instanceof String) {
				String fst = (String) f;

				Matcher exceptionMatcher = exceptionPattern.matcher(fst);
				while (exceptionMatcher.find()) {
					for (int i = 0; i < exceptionGroups.size(); i++) {
						if (exceptionMatcher.group(exceptionGroups.get(i)) != null) {
							fieldAssembler.addValueToFiled(FIELDS.EXCEPTIONS.getFieldName(),
									exceptionMatcher.group(exceptionGroups.get(i)));
						}
						if (exceptionMatcher.group(messageGroups.get(i)) != null) {
							fieldAssembler.addValueToFiled(FIELDS.MESSAGES.getFieldName(),
									exceptionMatcher.group(messageGroups.get(i)));
						}
					}
				}
				Matcher traceMatcher = tracePattern.matcher(fst);
				while (traceMatcher.find()) {
					fieldAssembler.addValueToFiled(FIELDS.JAVA_CLASSES.getFieldName(),  
							traceMatcher.group(javaClassGroup.intValue()));
					fieldAssembler.addValueToFiled(FIELDS.METHODS.getFieldName(),
							traceMatcher.group(methodGroup.intValue()));
					fieldAssembler.addValueToFiled(FIELDS.SOURCE_FILE.getFieldName(),
							traceMatcher.group(sourceFileGroup.intValue()));
					fieldAssembler.addValueToFiled(FIELDS.LINE_NUMBERS.getFieldName(),
							Integer.parseInt(traceMatcher.group(lineNumberGroup.intValue())));
				}
				for (FIELDS field : FIELDS.values()) {
					event.setField(field.getFieldName(), fieldAssembler.getFieldValue(field.getFieldName()));
				}
				if (removeSourceField) {
					event.remove(sourceField);
				}

			}
			matchListener.filterMatched(event);
		}
		return events;
	}

	@Override
	public String getId() {
		return this.id;
	}
}
