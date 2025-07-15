package com.hit.dm;

import java.time.LocalDateTime;

public class Rental {
    private final long rentalId;

    private final long userId;

    private final long bicycleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    public Rental(long rentalId, long userId, long bicycleId, LocalDateTime startTime, LocalDateTime endTime) {
        this.rentalId = rentalId;
        this.userId = userId;
        this.bicycleId = bicycleId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getRentalId() {
        return rentalId;
    }
    public long getUserId() {
        return userId;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public long getBicycleId() {
        return bicycleId;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}

