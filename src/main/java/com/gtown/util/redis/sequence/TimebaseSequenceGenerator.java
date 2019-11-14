package com.gtown.util.redis.sequence;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 基于时间格式的序列号生成器
 */
public class TimebaseSequenceGenerator {
    @Resource
    private SequenceGenerator              sequenceGenerator;
    private Map<String, DateTimeFormatter> formatCache = new HashMap<>(5);

    /**
     * 获取当前时间戳格式的序列
     *
     * @param key
     * @return
     */
    public String nextTimestampSeq(String key) {
        long seq = sequenceGenerator.next(key);
        return String.format("%d%d", System.currentTimeMillis(), seq);
    }

    /**
     * 根据key和时间格式, 生成序列
     *
     * @param key
     * @param timeFormat
     * @return
     */
    public String next(String key, String timeFormat) {
        return next(key, timeFormat, "%04d");
    }

    /**
     * 根据key和时间格式, 生成格式化序列
     *
     * @param key
     * @param timeFormat
     * @param seqFormat  序列号格式
     * @return
     * @see String#format
     */
    public String next(String key, String timeFormat, String seqFormat) {
        return String.format("%s" + seqFormat, LocalDateTime.now().format(getFormat(timeFormat)), sequenceGenerator.next(key));
    }

    /**
     * 根据key和时间格式, 生成n个序列
     *
     * @param key
     * @param timeFormat
     * @param n
     * @return
     */
    public List<String> next(String key, int n, String timeFormat) {
        return next(key, n, timeFormat, "%04d");
    }

    /**
     * 根据key和时间格式, 生成n个序列
     *
     * @param key
     * @param timeFormat
     * @param n
     * @return
     */
    public List<String> next(String key, int n, String timeFormat, String seqFormat) {
        List<Long> list = sequenceGenerator.next(key, n);
        String     time = LocalDateTime.now().format(getFormat(timeFormat));
        return list.stream().map(i -> String.format("%s" + seqFormat, time, i)).collect(Collectors.toList());
    }

    public static class TimeFormatter {
        private final String                    fmt;
        private final TimebaseSequenceGenerator generator;

        TimeFormatter(TimebaseSequenceGenerator generator, String fmt) {
            this.fmt = fmt;
            this.generator = generator;
        }

        public String next(String key) {
            return generator.next(key, fmt);
        }

        public String next(String key, String seqFormat) {
            return generator.next(key, fmt, seqFormat);
        }

        public List<String> next(String key, int n) {
            return generator.next(key, n, fmt, "%04d");
        }

        public List<String> next(String key, int n, String seqFormat) {
            return generator.next(key, n, fmt, seqFormat);
        }
    }

    public TimeFormatter YYYY           = new TimeFormatter(this, "yyyy");
    public TimeFormatter YYYYMM         = new TimeFormatter(this, "yyyyMM");
    public TimeFormatter MM             = new TimeFormatter(this, "MM");
    public TimeFormatter MMDD           = new TimeFormatter(this, "MMdd");
    public TimeFormatter DD             = new TimeFormatter(this, "dd");
    public TimeFormatter DDHHmm         = new TimeFormatter(this, "ddHHmm");
    public TimeFormatter HHMM           = new TimeFormatter(this, "HHmm");
    public TimeFormatter HHMMSS         = new TimeFormatter(this, "HHmmss");
    public TimeFormatter YYYYMMDD       = new TimeFormatter(this, "yyyyMMdd");
    public TimeFormatter YYYYMMDDHHMMSS = new TimeFormatter(this, "yyyyMMddHHmmss");

    private DateTimeFormatter getFormat(String format) {
        DateTimeFormatter fmt = formatCache.get(format);
        if (fmt == null) {
            fmt = DateTimeFormatter.ofPattern(format);
            formatCache.put(format, fmt);
        }
        return fmt;
    }
}
