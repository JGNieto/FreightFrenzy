package org.baylorschool.library;

public enum State {
    TURNING,
    TRAVELLING,
    APPROACHING_CARGO,
    PICKING_CARGO,
    APPROACHING_HUB,
    DROPPING_CARGO,
    ENTERING_WAREHOUSE,
    LEAVING_WAREHOUSE,
    HALT;

    private static State state;

    public static State getState() {
        return state;
    }

    public static void setState(State state) {
        State.state = state;
    }
}
