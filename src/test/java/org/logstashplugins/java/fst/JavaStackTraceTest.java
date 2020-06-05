package org.logstashplugins.java.fst;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.logstash.plugins.ConfigurationImpl;
import org.logstashplugins.java.fst.JavaStackTraceParser.FIELDS;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Event;
import co.elastic.logstash.api.FilterMatchListener;

public class JavaStackTraceTest {

	@Test(expected = RuntimeException.class)
	public void testWrongPattern() {
		Map<String, Object> configMap = new HashMap<String, Object>();
		configMap.put("exception_pattern", "/sasdf?*");
		Configuration config = new ConfigurationImpl(configMap);
		JavaStackTraceParser filter = new JavaStackTraceParser("test-id", config, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testWrongOutputPattern() {
		Map<String, Object> configMap = new HashMap<String, Object>();
		configMap.put("output_pattern", "blabla");
		Configuration config = new ConfigurationImpl(configMap);
		JavaStackTraceParser filter = new JavaStackTraceParser("test-id", config, null);
	}

	@Test
	public void testSimpleCSV() {
		Configuration config = new ConfigurationImpl(new HashMap<String, Object>());
		JavaStackTraceParser filter = new JavaStackTraceParser("test-id", config, null);
		Event e = new org.logstash.Event();
		TestMatchListener matchListener = new TestMatchListener();
		e.setField("full_stack_trace", TestData.FST_SIMPLE);
		Collection<Event> results = filter.filter(Collections.singletonList(e), matchListener);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(((Event) (results.toArray()[0])).getData().size(), 8);
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.EXCEPTIONS.getFieldName()).toString(),
				"java.lang.ClassNotFoundException");
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.MESSAGES.getFieldName()).toString(),
				"com.test.solutions.google.opf.product.orch.actions.agreement.ActivateContractChanges_Fail");
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.JAVA_CLASSES.getFieldName()).toString(),
				"weblogic.utils.classloaders.GenericClassLoader,weblogic.utils.classloaders.GenericClassLoader,java.lang.ClassLoader,java.lang.ClassLoader,weblogic.utils.classloaders.GenericClassLoader,java.lang.Class,com.test.platform.orchestrator.core.actions.synchronous.SynchronousJavaActionExecutor,com.test.platform.orchestrator.core.actions.synchronous.SynchronousJavaActionExecutor,com.test.platform.orchestrator.core.orch.OrchestrateMultipleTasksAction,com.test.platform.orchestrator.core.orch.MultipleTaskHandler,com.test.platform.orchestrator.core.orch.MultipleTaskHandler,com.test.platform.orchestrator.core.orch.MultipleTaskExecutor,com.test.platform.orchestrator.core.orch.ProcessExecutor,com.test.platform.orchestrator.core.orch.ProcessExecutor,com.test.platform.orchestrator.core.flow.FlowManager$7,com.test.platform.orchestrator.core.flow.FlowManager$7,com.test.platform.orchestrator.core.orch.ProcessHandler,com.test.platform.orchestrator.core.flow.FlowManager,com.test.platform.orchestrator.core.flow.FlowManager$2,com.test.platform.orchestrator.core.queue.BaseQueueMessageHandler,com.test.platform.orchestrator.core.queue.AbstractHandleMessageAction,com.test.platform.orchestrator.core.queue.HandleMessageAction,com.test.platform.orchestrator.core.queue.HandleMessageAction,com.test.platform.orchestrator.core.queue.HandleMessageAction$1,weblogic.security.acl.internal.AuthenticatedSubject,weblogic.security.service.SecurityManager,weblogic.security.Security,com.test.configuration.impl.weblogic.facets.WLSecurityFacet,com.test.configuration.compatibility.ConfigImpl,com.test.ejb.session.security.SecurityProcessor,com.test.platform.orchestrator.utils.KernelUtil,com.test.platform.orchestrator.core.queue.HandleMessageAction,com.test.platform.orchestrator.core.queue.MessageManager,com.test.platform.orchestrator.core.queue.OrchestrationQueueInvokerBean,com.test.platform.orchestrator.core.queue.OrchestrationQueueInvokerBean,com.test.platform.utils.tools.queueservice.QueueInvoker,com.test.platform.utils.tools.queueservice.QueueInvoker$1,com.test.platform.utils.tools.queueservice.QueueService,com.test.platform.utils.tools.queueservice.QueueService$4,com.test.platform.utils.tools.queueservice.QueueService$4,com.test.platform.utils.tools.queueservice.QueueService,com.test.platform.utils.tools.queueservice.QueueService,com.test.platform.utils.tools.queueservice.QueueInvoker,com.test.platform.utils.tools.queueservice.QueueInvoker,com.test.platform.utils.tools.queueservice.QueueInvokerMDB,com.test.platform.utils.tools.queueservice.QueueInvokerMDB,weblogic.ejb.container.internal.MDListener,weblogic.ejb.container.internal.MDListener,weblogic.ejb.container.internal.MDListener,weblogic.jms.client.JMSSession,weblogic.jms.client.JMSSession,weblogic.jms.client.JMSSession,weblogic.jms.client.JMSSession,weblogic.jms.client.JMSSession$UseForRunnable,weblogic.work.SelfTuningWorkManagerImpl$WorkAdapterImpl,weblogic.work.ExecuteThread,weblogic.work.ExecuteThread");
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.METHODS.getFieldName()).toString(),
				"findLocalClass,findClass,loadClass,loadClass,loadClass,forName,getActionClassInstance,executeAction,handleTask,executeTaskByHandler,handleMultipleTasks,orchestrateMultipleTasks,orchestrateTasks,orchestrateProcess,continueProcessExecution,handleProcess,handleProcess,performCompleteAsyncTask,processMessage,handleMessage,handleMessageByHandler,handleMessageAndErrors,access$000,run,doAs,runAs,runAs,doAs,doAs,doAsSystem,doAsSystem,handle,handleMessage,handleMessage,handleMessage,handleMessage,handle,handleMessage,processByCommonAlgorithm,executeServiceAction,executeTransactedAction,invokeOnQueueSegment,processMessage,processMessage,processMessage,onMessage,execute,transactionalOnMessage,onMessage,onMessage,execute,executeMessage,access$000,run,run,execute,run");
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.LINE_NUMBERS.getFieldName()).toString(),
				"297,270,425,358,179,195,65,79,185,276,156,42,313,152,1270,1164,110,1107,98,86,32,50,16,39,363,146,61,46,85,55,32,35,107,59,37,108,66,922,540,483,889,445,62,47,115,71,583,486,388,4817,4491,3945,115,5337,550,263,221");
		Assert.assertEquals(1, matchListener.getMatchCount());
	}

	@Test
	public void testCauseBy1CSV() {
		Configuration config = new ConfigurationImpl(new HashMap<String, Object>());
		JavaStackTraceParser filter = new JavaStackTraceParser("test-id", config, null);
		Event e = new org.logstash.Event();
		TestMatchListener matchListener = new TestMatchListener();
		e.setField("full_stack_trace", TestData.FST_CAUSED_BY_1);
		Collection<Event> results = filter.filter(Collections.singletonList(e), matchListener);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(((Event) (results.toArray()[0])).getData().size(), 8);
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.EXCEPTIONS.getFieldName()).toString(),
				"com.test.salesorder.engine.business.impl.exception.exceptions.SoeUnexpectedBusinessException,java.lang.IllegalStateException");
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.MESSAGES.getFieldName()).toString(),
				"java.lang.IllegalStateException: Price Guarantee Item 9154087675563809226 must have Add action instead of - if Price Guarantee Instance status is absentArguments\\nGot java.lang.IllegalStateException: Price Guarantee Item 9154087675563809226 must have Add action instead of - if Price Guarantee Instance status is absent \\nWhile execution(ActionResult com.test.salesorder.engine.business.impl.SalesOrderContextBusinessServiceImpl.doAction(SalesOrder,BaseActionParams)) \\nWith:\\n null,Price Guarantee Item 9154087675563809226 must have Add action instead of - if Price Guarantee Instance status is absent");
		Assert.assertEquals(1, matchListener.getMatchCount());
	}

	@Test
	public void testCauseBy2CSV() {
		Configuration config = new ConfigurationImpl(new HashMap<String, Object>());
		JavaStackTraceParser filter = new JavaStackTraceParser("test-id", config, null);
		Event e = new org.logstash.Event();
		TestMatchListener matchListener = new TestMatchListener();
		e.setField("full_stack_trace", TestData.FST_CAUSED_BY_2);
		Collection<Event> results = filter.filter(Collections.singletonList(e), matchListener);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(((Event) (results.toArray()[0])).getData().size(), 8);
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.EXCEPTIONS.getFieldName()).toString(),
				"java.lang.RuntimeException,java.lang.RuntimeException,com.test.framework.jdbc.DataAccessException,java.sql.SQLException,com.test.framework.transactions.TxException,java.lang.RuntimeException,by");
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.MESSAGES.getFieldName()).toString(),
				"Error during request processing with timeout,com.test.framework.jdbc.DataAccessException: Error due to access to database: ,Error due to access to database: ,Transaction BEA1-53EEE8AC402327B9D231 not active anymore. tx status = Marked rollback. [Reason=java.lang.RuntimeException: Error during request processing with timeout,java.lang.RuntimeException: java.util.ConcurrentModificationException,java.util.ConcurrentModificationException,java.util.ConcurrentModificationException");
		Assert.assertEquals(1, matchListener.getMatchCount());
	}

	@Test
	public void testCauseBy3CSV() {
		Configuration config = new ConfigurationImpl(new HashMap<String, Object>());
		JavaStackTraceParser filter = new JavaStackTraceParser("test-id", config, null);
		Event e = new org.logstash.Event();
		TestMatchListener matchListener = new TestMatchListener();
		e.setField("full_stack_trace", TestData.FST_CAUSED_BY_3);
		Collection<Event> results = filter.filter(Collections.singletonList(e), matchListener);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(((Event) (results.toArray()[0])).getData().size(), 8);
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.EXCEPTIONS.getFieldName()).toString(),
				"ca.google.exception.base.ossj.OssjProcessingError,java.lang.RuntimeException,ca.google.exception.base.ossj.OssjProcessingError,com.test.NCException,ca.google.exception.base.ossj.OssjDefaultShawRuntimeException");
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.MESSAGES.getFieldName()).toString(),
				"EJB Exception: : java.lang.RuntimeException: java.lang.RuntimeException: ca.google.exception.base.ossj.OssjProcessingError: There is processing order with this correlationId,ca.google.exception.base.ossj.OssjProcessingError: There is processing order with this correlationId,There is processing order with this correlationId,There is processing order with this correlationId,There is processing order with this correlationId");
		Assert.assertEquals(1, matchListener.getMatchCount());
	}

	@Test
	public void testSimpleArray() {
		Map<String, Object> configMap = new HashMap<String, Object>();
		configMap.put("output_pattern", "array");
		Configuration config = new ConfigurationImpl(configMap);
		JavaStackTraceParser filter = new JavaStackTraceParser("test-id", config, null);
		Event e = new org.logstash.Event();
		TestMatchListener matchListener = new TestMatchListener();
		e.setField("full_stack_trace", TestData.FST_SIMPLE);
		Collection<Event> results = filter.filter(Collections.singletonList(e), matchListener);
		Assert.assertEquals(1, results.size());
		Assert.assertEquals(((Event) (results.toArray()[0])).getData().size(), 8);
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.EXCEPTIONS.getFieldName()).toString(),
				"[java.lang.ClassNotFoundException]");
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.MESSAGES.getFieldName()).toString(),
				"[com.test.solutions.google.opf.product.orch.actions.agreement.ActivateContractChanges_Fail]");
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.JAVA_CLASSES.getFieldName()).toString(),
				"[weblogic.utils.classloaders.GenericClassLoader, weblogic.utils.classloaders.GenericClassLoader, java.lang.ClassLoader, java.lang.ClassLoader, weblogic.utils.classloaders.GenericClassLoader, java.lang.Class, com.test.platform.orchestrator.core.actions.synchronous.SynchronousJavaActionExecutor, com.test.platform.orchestrator.core.actions.synchronous.SynchronousJavaActionExecutor, com.test.platform.orchestrator.core.orch.OrchestrateMultipleTasksAction, com.test.platform.orchestrator.core.orch.MultipleTaskHandler, com.test.platform.orchestrator.core.orch.MultipleTaskHandler, com.test.platform.orchestrator.core.orch.MultipleTaskExecutor, com.test.platform.orchestrator.core.orch.ProcessExecutor, com.test.platform.orchestrator.core.orch.ProcessExecutor, com.test.platform.orchestrator.core.flow.FlowManager$7, com.test.platform.orchestrator.core.flow.FlowManager$7, com.test.platform.orchestrator.core.orch.ProcessHandler, com.test.platform.orchestrator.core.flow.FlowManager, com.test.platform.orchestrator.core.flow.FlowManager$2, com.test.platform.orchestrator.core.queue.BaseQueueMessageHandler, com.test.platform.orchestrator.core.queue.AbstractHandleMessageAction, com.test.platform.orchestrator.core.queue.HandleMessageAction, com.test.platform.orchestrator.core.queue.HandleMessageAction, com.test.platform.orchestrator.core.queue.HandleMessageAction$1, weblogic.security.acl.internal.AuthenticatedSubject, weblogic.security.service.SecurityManager, weblogic.security.Security, com.test.configuration.impl.weblogic.facets.WLSecurityFacet, com.test.configuration.compatibility.ConfigImpl, com.test.ejb.session.security.SecurityProcessor, com.test.platform.orchestrator.utils.KernelUtil, com.test.platform.orchestrator.core.queue.HandleMessageAction, com.test.platform.orchestrator.core.queue.MessageManager, com.test.platform.orchestrator.core.queue.OrchestrationQueueInvokerBean, com.test.platform.orchestrator.core.queue.OrchestrationQueueInvokerBean, com.test.platform.utils.tools.queueservice.QueueInvoker, com.test.platform.utils.tools.queueservice.QueueInvoker$1, com.test.platform.utils.tools.queueservice.QueueService, com.test.platform.utils.tools.queueservice.QueueService$4, com.test.platform.utils.tools.queueservice.QueueService$4, com.test.platform.utils.tools.queueservice.QueueService, com.test.platform.utils.tools.queueservice.QueueService, com.test.platform.utils.tools.queueservice.QueueInvoker, com.test.platform.utils.tools.queueservice.QueueInvoker, com.test.platform.utils.tools.queueservice.QueueInvokerMDB, com.test.platform.utils.tools.queueservice.QueueInvokerMDB, weblogic.ejb.container.internal.MDListener, weblogic.ejb.container.internal.MDListener, weblogic.ejb.container.internal.MDListener, weblogic.jms.client.JMSSession, weblogic.jms.client.JMSSession, weblogic.jms.client.JMSSession, weblogic.jms.client.JMSSession, weblogic.jms.client.JMSSession$UseForRunnable, weblogic.work.SelfTuningWorkManagerImpl$WorkAdapterImpl, weblogic.work.ExecuteThread, weblogic.work.ExecuteThread]");
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.METHODS.getFieldName()).toString(),
				"[findLocalClass, findClass, loadClass, loadClass, loadClass, forName, getActionClassInstance, executeAction, handleTask, executeTaskByHandler, handleMultipleTasks, orchestrateMultipleTasks, orchestrateTasks, orchestrateProcess, continueProcessExecution, handleProcess, handleProcess, performCompleteAsyncTask, processMessage, handleMessage, handleMessageByHandler, handleMessageAndErrors, access$000, run, doAs, runAs, runAs, doAs, doAs, doAsSystem, doAsSystem, handle, handleMessage, handleMessage, handleMessage, handleMessage, handle, handleMessage, processByCommonAlgorithm, executeServiceAction, executeTransactedAction, invokeOnQueueSegment, processMessage, processMessage, processMessage, onMessage, execute, transactionalOnMessage, onMessage, onMessage, execute, executeMessage, access$000, run, run, execute, run]");
		Assert.assertEquals(((Event) (results.toArray()[0])).getField(FIELDS.LINE_NUMBERS.getFieldName()).toString(),
				"[297, 270, 425, 358, 179, 195, 65, 79, 185, 276, 156, 42, 313, 152, 1270, 1164, 110, 1107, 98, 86, 32, 50, 16, 39, 363, 146, 61, 46, 85, 55, 32, 35, 107, 59, 37, 108, 66, 922, 540, 483, 889, 445, 62, 47, 115, 71, 583, 486, 388, 4817, 4491, 3945, 115, 5337, 550, 263, 221]");
		Assert.assertEquals(1, matchListener.getMatchCount());
	}

}

class TestMatchListener implements FilterMatchListener {

	private AtomicInteger matchCount = new AtomicInteger(0);

	@Override
	public void filterMatched(Event event) {
		matchCount.incrementAndGet();
	}

	public int getMatchCount() {
		return matchCount.get();
	}
}