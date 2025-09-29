package common;

public enum RATING {
    UNASSIGNED(0.0),
    ONE_STAR(1.0),
    TWO_STARS(2.0),
    THREE_STARS(3.0),
    FOUR_STARS(4.0),
    FIVE_STARS(5.0);

    private final double value;

    RATING(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public static RATING fromValue(double value) {
        for (RATING rating : values()) {
            if (rating.getValue() == value) {
                return rating;
            }
        }
        return UNASSIGNED;
    }
}
