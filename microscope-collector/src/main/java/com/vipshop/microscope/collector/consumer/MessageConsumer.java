package com.vipshop.microscope.collector.consumer;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vipshop.microscope.collector.processor.MessageProcessors;
import com.vipshop.microscope.collector.server.CollectorQueue;
import com.vipshop.microscope.thrift.LogEntry;

public class MessageConsumer implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
	
	@Override
	public void run() {
		while (true) {
			LogEntry logEntry = CollectorQueue.poll();
			if (logEntry != null) {
				MessageProcessors.process(logEntry);
			} else {
				try {
					TimeUnit.MILLISECONDS.sleep(3);
				} catch (InterruptedException e) {
					logger.error("InterruptedException on MessageConsumer but continue");
				}
			}
		}
	}
	
}
