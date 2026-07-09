package com.tripiz.api.configurations;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class StationSubscriptionTracker {

    private static final Pattern STATION_TOPIC_PATTERN =
            Pattern.compile("^/topic/stations/([0-9a-fA-F-]{36})/buses$");

    // clé = sessionId:subscriptionId, valeur = stationId
    private final Map<String, String> subscriptions = new ConcurrentHashMap<>();

    public void track(String sessionId, String subscriptionId, String destination) {
        Matcher matcher = STATION_TOPIC_PATTERN.matcher(destination);
        if (matcher.matches()) {
            subscriptions.put(sessionId + ":" + subscriptionId, matcher.group(1));
        }
    }

    public void untrack(String sessionId, String subscriptionId) {
        subscriptions.remove(sessionId + ":" + subscriptionId);
    }

    public void untrackSession(String sessionId) {
        subscriptions.keySet().removeIf(key -> key.startsWith(sessionId + ":"));
    }

    public Set<String> getActiveStationIds() {
        return Set.copyOf(subscriptions.values());
    }
}