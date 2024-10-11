package com.demo.model;

import java.util.concurrent.atomic.AtomicInteger;

public class LookupPojo {
    private String tag;

    private AtomicInteger counter;

    public LookupPojo(String tag) {
        this.tag = tag;
        counter = new AtomicInteger();

    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public AtomicInteger getCounter() {
        return counter;
    }

    public void setCounter(AtomicInteger counter) {
        this.counter = counter;
    }

    public void incrementCounter() {
        this.counter.incrementAndGet();
    }
}
