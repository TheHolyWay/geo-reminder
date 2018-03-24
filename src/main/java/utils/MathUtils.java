package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Location;

public class MathUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(MathUtils.class);

    public static boolean isNear(final Location target, final Location current) {
        double distance = sphericalDistance(target.getLongitude(), target.getLatitude(), current.getLongitude(), current.getLatitude());
        LOGGER.info("Distance between user and target is {} m", distance);
        return distance < 300;
    }

    public static double sphericalDistance(double lon1, double lat1, double lon2, double lat2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return c * 6371000;
    }
}
