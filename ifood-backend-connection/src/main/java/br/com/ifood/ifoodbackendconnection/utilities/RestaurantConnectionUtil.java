package br.com.ifood.ifoodbackendconnection.utilities;

import br.com.ifood.ifoodbackendconnection.domain.SignalHistory;
import br.com.ifood.ifoodbackendconnection.domain.ConnectionInterval;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class RestaurantConnectionUtil {

    public static List<ConnectionInterval> fetchConnectionsFailures(List<SignalHistory> signalHistory) {
        return apply(signalHistory, SignalPredicates.isFailure());
    }

    public static List<ConnectionInterval> fetchSuccessfulConnections(List<SignalHistory> signalHistory) {
        return apply(signalHistory, SignalPredicates.isSuccessful());
    }

    private static List<ConnectionInterval> apply(List<SignalHistory> signalHistory, Predicate<Duration> predicate) {
        if (signalHistory == null || signalHistory.size() < 2) {
            throw new IllegalStateException("Not enough connection history found to evaluate.");
        }

        List<ConnectionInterval> connectionInterval = new ArrayList<>();

        Collections.sort(signalHistory);

        SignalHistory currentSignal = null;
        for (SignalHistory nextSignal : signalHistory){
            if(currentSignal == null){
                currentSignal = nextSignal;
            } else {
                Duration differenceBetweenSignal = currentSignal.calDifferenceBetween(nextSignal);
                if (predicate.test(differenceBetweenSignal)){
                    connectionInterval.add(new ConnectionInterval(currentSignal, nextSignal));
                }

                currentSignal = nextSignal;
            }
        }

        return connectionInterval;
    }
}

class SignalPredicates {
     private static final long ACCEPTABLE_DELAY_SIGNAL_IN_MINUTES = 2;

    public static Predicate<Duration> isFailure() {
        return p -> p.toMinutes() >= ACCEPTABLE_DELAY_SIGNAL_IN_MINUTES;
    }

    public static Predicate<Duration> isSuccessful() {
        return p -> p.toMinutes() < ACCEPTABLE_DELAY_SIGNAL_IN_MINUTES;
    }
}
