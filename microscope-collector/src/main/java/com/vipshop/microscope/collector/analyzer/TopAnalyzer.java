package com.vipshop.microscope.collector.analyzer;

import com.vipshop.microscope.storage.StorageRepository;
import com.vipshop.microscope.thrift.Span;
import com.vipshop.microscope.trace.span.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class TopAnalyzer {

    public static final Logger logger = LoggerFactory.getLogger(TopReport.class);

    private ConcurrentHashMap<Category, FixedPriorityQueue> container;

    public TopAnalyzer() {
        this.container = new ConcurrentHashMap<Category, FixedPriorityQueue>(Category.values().length);
        Category[] categories = Category.values();
        for (int i = 0; i < categories.length; i++) {
            container.put(categories[i], new FixedPriorityQueue(10));
        }
    }

    /**
     * Analyze top by span.
     *
     * @param span
     */
    public void analyze(Span span, StorageRepository storager) {
        FixedPriorityQueue queue = container.get(Category.valueOf(span.getSpanType()));
        queue.add(span);
        writeReport(storager);
    }

    public ConcurrentHashMap<Category, FixedPriorityQueue> getContainer() {
        return container;
    }

    private void writeReport(StorageRepository storager) {
        HashMap<String, Object> top = new HashMap<String, Object>();
        for (Entry<Category, FixedPriorityQueue> entry : container.entrySet()) {
            StringBuilder builder = new StringBuilder();
            TreeMap<Integer, Span> queue = entry.getValue().getQueue();
            for (Entry<Integer, Span> treeEntry : queue.entrySet()) {
                Span span = treeEntry.getValue();
                builder.append(span.getAppName())
                        .append("=")
                        .append(span.getTraceId())
                        .append("=")
                        .append(span.getDuration())
                        .append("=")
                        .append(span.getStartTime())
                        .append(";");
            }
            top.put(entry.getKey().getStrValue(), builder.toString());
        }
        storager.saveTop(top);
    }
}