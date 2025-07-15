package model;

import java.time.LocalDateTime;

public class Rental {
    private long rentalId;
    private long userId;
    private long bicycleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Rental() {}

    public Rental(long rentalId, long userId, long bicycleId, LocalDateTime startTime, LocalDateTime endTime) {
        this.rentalId = rentalId;
        this.userId = userId;
        this.bicycleId = bicycleId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getRentalId() { return rentalId; }
    public void setRentalId(long rentalId) { this.rentalId = rentalId; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public long getBicycleId() { return bicycleId; }
    public void setBicycleId(long bicycleId) { this.bicycleId = bicycleId; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public boolean isActive() {
        return endTime == null;
    }

    @Override
    public String toString() {
        return "Rental ID: " + rentalId + " - User: " + userId + " - Bicycle: " + bicycleId +
                " - Start: " + startTime + (endTime != null ? " - End: " + endTime : " - ACTIVE");
    }
}