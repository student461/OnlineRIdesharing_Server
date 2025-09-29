package manager;

import common.*;
import strategy.*;
import model.*;

public class StrategyMgr {
    private static StrategyMgr strategyMgrInstance;

    private StrategyMgr() {}

    public static StrategyMgr getInstance() {
        if (strategyMgrInstance == null) {
            synchronized (StrategyMgr.class) {
                if (strategyMgrInstance == null) {
                    strategyMgrInstance = new StrategyMgr();
                }
            }
        }
        return strategyMgrInstance;
    }

    public PricingStrategy determinePricingStrategy(TripMetaData metaData) {
        if (metaData.getRiderRating() == RATING.FIVE_STARS || metaData.getRiderRating() == RATING.FOUR_STARS) {
            return new HighRatingPricingStrategy();
        }
        return new DefaultPricingStrategy();
    }

    public DriverMatchingStrategy determineMatchingStrategy(String strategyType) {
        return "HIGH_RATING".equalsIgnoreCase(strategyType)
                ? new HighRatingDriverMatchingStrategy()
                : new LeastTimeBasedMatchingStrategy();
    }
}
