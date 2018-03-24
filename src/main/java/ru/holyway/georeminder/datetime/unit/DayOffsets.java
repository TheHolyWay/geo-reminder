package ru.holyway.georeminder.datetime.unit;

public enum DayOffsets {

    TODAY(0),
    TOMORROW(1),
    DAY_AFTER_TOMORROW(2);

    private int offset;

    DayOffsets(int offset) {
        this.offset = offset;
    }

    public int getOffset() {
        return offset;
    }
}
