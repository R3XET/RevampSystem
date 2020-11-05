package eu.revamp.system.enums;

import eu.revamp.spigot.utils.string.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum RankType {

    DEFAULT, DONATOR, STAFF;

    public static String toMessage() {
        return StringUtils.getStringFromList(Stream.of(RankType.values()).map(RankType::toString)
                .map(String::toLowerCase).map(StringUtils::convertFirstUpperCase).collect(Collectors.toList()));
    }
}
