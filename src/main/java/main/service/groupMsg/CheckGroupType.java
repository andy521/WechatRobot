package main.service.groupMsg;

import config.GlobalConfig;
import enums.GroupType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Javior
 * @date 2019/6/17 16:07
 */
@Slf4j
public class CheckGroupType {

    private static final String GROUP_WHITELIST = GlobalConfig.getValue("group.whitelist", "");

    private static final String GROUP_WHITE_KEYWORD = GlobalConfig.getValue("group.whiteKeyword", "");

    private static final String GROUP_BLACKLIST = GlobalConfig.getValue("group.blacklist", "");

    private static final String GROUP_BLACK_KEYWORD = GlobalConfig.getValue("group.blackKeyword", "");

    private static final String GROUP_MODE_ONLY = GlobalConfig.getValue("group.modeOnly", "");

    private static final String GROUP_MODE_KEYWORD = GlobalConfig.getValue("group.modeOnlyKeyword", "");

    private static List<String> WHITE_LIST = new LinkedList<>();

    private static List<String> WHITE_KEYWORD_LIST = new LinkedList<>();

    private static List<String> MODE_KEYWORD_LIST = new LinkedList<>();

    private static List<String> MODE_ONLY_LIST = new LinkedList<>();

    private static List<String> BLACK_KEYWORD_LIST = new LinkedList<>();

    private static List<String> BLACK_LIST = new LinkedList<>();

    static {
        WHITE_LIST.addAll(Arrays.stream(GROUP_WHITELIST.split("#")).filter(StringUtils::isNotBlank).collect(Collectors.toList()));

        WHITE_KEYWORD_LIST.addAll(Arrays.stream(GROUP_WHITE_KEYWORD.split("#")).filter(StringUtils::isNotBlank).collect(Collectors.toList()));

        BLACK_KEYWORD_LIST.addAll(Arrays.stream(GROUP_BLACK_KEYWORD.split("#")).filter(StringUtils::isNotBlank).collect(Collectors.toList()));

        BLACK_LIST.addAll(Arrays.stream(GROUP_BLACKLIST.split("#")).filter(StringUtils::isNotBlank).collect(Collectors.toList()));

        MODE_ONLY_LIST.addAll(Arrays.stream(GROUP_MODE_ONLY.split("#")).filter(StringUtils::isNotBlank).collect(Collectors.toList()));

        MODE_KEYWORD_LIST.addAll(Arrays.stream(GROUP_MODE_KEYWORD.split("#")).filter(StringUtils::isNotBlank).collect(Collectors.toList()));
    }

    private static Map<String, GroupType> cache = new ConcurrentHashMap<>();

    public static GroupType checkGroupType(String from) {
        if (StringUtils.isBlank(from)) {
            log.info("checkGroupType, GroupName: {}, GroupType: {}", from, GroupType.GROUP_NOT_EXISTS);
            return GroupType.GROUP_NOT_EXISTS;
        }

        GroupType type = GroupType.GROUP_DEFAULT;

        if (cache.containsKey(from)) {
            type = cache.getOrDefault(from, GroupType.GROUP_NOT_EXISTS);
            log.info("checkGroupType, GroupName: {}, GroupType: {}, fromCache: true", from, type);
            return type;
        }

        for (String s : MODE_KEYWORD_LIST) {
            if (from.contains(s)) {
                type = GroupType.GROUP_MODE_ONLY;
            }
        }
        for (String s : MODE_ONLY_LIST) {
            if (from.equals(s))
                type = GroupType.GROUP_MODE_ONLY;
        }

        for (String s : WHITE_KEYWORD_LIST) {
            if (from.contains(s))
                type = GroupType.GROUP_WHITELIST;
        }
        for (String s : WHITE_LIST) {
            if (from.equals(s))
                type = GroupType.GROUP_WHITELIST;
        }

        for (String s : BLACK_KEYWORD_LIST) {
            if (from.contains(s))
                type = GroupType.GROUP_BLACKLIST;
        }
        for (String s : BLACK_LIST) {
            if (from.equals(s))
                type = GroupType.GROUP_BLACKLIST;
        }
        cache.put(from, type);
        log.info("checkGroupType, GroupName: {}, GroupType: {}, fromCache: false", from, type);
        return type;
    }
}
