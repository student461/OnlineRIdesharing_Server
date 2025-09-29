package common;

public class Util {
    public static String ratingToString(RATING rating) {
        switch (rating) {
            case ONE_STAR: return "one star";
            case TWO_STARS: return "two stars";
            case THREE_STARS: return "three stars";
            case FOUR_STARS: return "four stars";
            case FIVE_STARS: return "five stars";
            default: return "invalid rating";
        }
    }

    public static boolean isHighRating(RATING rating) {
        return rating == RATING.FOUR_STARS || rating == RATING.FIVE_STARS;
    }
}
