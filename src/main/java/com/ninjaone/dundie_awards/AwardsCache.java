package com.ninjaone.dundie_awards;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class AwardsCache {

    private final AtomicInteger totalAwards = new AtomicInteger(0);

    public int getTotalAwards() {
        return totalAwards.get();
    }

    public void setTotalAwards(int totalAwards) {
        this.totalAwards.set(totalAwards);
    }

    public void addOneAward() {
        this.totalAwards.incrementAndGet();
    }
}
