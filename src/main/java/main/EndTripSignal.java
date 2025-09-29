package main;

public class EndTripSignal {
    private static volatile boolean ended = false;

    public static boolean isEnded() {
        return ended;
    }

    public static void setEnded(boolean value) {
        ended = value;
    }
}
