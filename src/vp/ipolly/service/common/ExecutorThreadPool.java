package vp.ipolly.service.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 默认线程池
 * @author vpsong
 *
 */
public class ExecutorThreadPool {

	private static ExecutorService executorService = Executors
			.newCachedThreadPool();

	private ExecutorThreadPool() {
	}

	public static ExecutorService getExecutor() {
		return executorService;
	}

}
