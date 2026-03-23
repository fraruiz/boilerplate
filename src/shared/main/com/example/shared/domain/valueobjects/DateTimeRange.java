package com.example.shared.domain.valueobjects;

import com.example.shared.domain.errors.client.InvalidArgument;

import java.time.OffsetDateTime;
import java.util.Objects;

public final class DateTimeRange {
    private final OffsetDateTime start;
    private final OffsetDateTime end;

    public DateTimeRange(OffsetDateTime start, OffsetDateTime end) {
        ensureNotNull(start, end);
        ensureValidRange(start, end);

        this.start = start;
        this.end = end;
    }

    private void ensureNotNull(OffsetDateTime start, OffsetDateTime end) {
        if (start == null || end == null) {
            throw new InvalidArgument("Dates cannot be null");
        }
    }

    private void ensureValidRange(OffsetDateTime start, OffsetDateTime end) {
        if (start.isAfter(end)) {
            throw new InvalidArgument("Start date [" + start + "] cannot be after end date [" + end + "]");
        }
    }

    public boolean contains(OffsetDateTime date) {
        return (date.isEqual(start) || date.isAfter(start)) &&
               (date.isEqual(end) || date.isBefore(end));
    }

    public boolean overlapsWith(DateTimeRange other) {
        return this.start.isBefore(other.end) && other.start.isBefore(this.end);
    }

    public OffsetDateTime startDate() {
        return start;
    }

    public OffsetDateTime endDate() {
        return end;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateTimeRange that = (DateTimeRange) o;
        return start.equals(that.start) && end.equals(that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "DateRange{" +
               "startDate=" + start +
               ", endDate=" + end +
               '}';
    }
}
