package com.logginghub.logging.logeventformatters.log4j;

import com.logginghub.logging.LogEvent;
import com.logginghub.logging.LogEventFormatter;

public class Log4jPatternLogEventFormatter implements LogEventFormatter {

    /**
     * Default pattern string for log output. Currently set to the string
     * <b>"%m%n"</b> which just prints the application supplied message.
     */
    public final static String DEFAULT_CONVERSION_PATTERN = "%m%n";

    /**
     * A conversion pattern equivalent to the TTCCCLayout. Current value is
     * <b>%r [%t] %p %c %x - %m%n</b>.
     */
    public final static String TTCC_CONVERSION_PATTERN = "%r [%t] %p %c %x - %m%n";

    protected final int BUF_SIZE = 256;
    protected final int MAX_CAPACITY = 1024;

    // output buffer appended to when format() is invoked
    private StringBuffer sbuf = new StringBuffer(BUF_SIZE);

    private String pattern;

    private Log4jPatternConvertor head;

    private String timezone;

    /**
     * Constructs a PatternLayout using the DEFAULT_LAYOUT_PATTERN.
     * 
     * The default pattern just produces the application supplied message.
     */
    public Log4jPatternLogEventFormatter() {
        this(DEFAULT_CONVERSION_PATTERN);
    }

    /**
     * Constructs a PatternLayout using the supplied conversion pattern.
     */
    public Log4jPatternLogEventFormatter(String pattern) {
        setPattern(pattern);
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
        head = createPatternParser((pattern == null) ? DEFAULT_CONVERSION_PATTERN : pattern).parse();
    }

    /**
     * Set the <b>ConversionPattern</b> option. This is the string which
     * controls formatting and consists of a mix of literal content and
     * conversion specifiers.
     */
    public void setConversionPattern(String conversionPattern) {
        pattern = conversionPattern;
        head = createPatternParser(conversionPattern).parse();
    }

    /**
     * Returns the value of the <b>ConversionPattern</b> option.
     */
    public String getConversionPattern() {
        return pattern;
    }

    /**
     * Does not do anything as options become effective
     */
    public void activateOptions() {
        // nothing to do.
    }

    /**
     * The PatternLayout does not handle the throwable contained within
     * {@link LoggingEvent LoggingEvents}. Thus, it returns <code>true</code>.
     * 
     * @since 0.8.4
     */
    public boolean ignoresThrowable() {
        return true;
    }

    /**
     * Returns PatternParser used to parse the conversion string. Subclasses may
     * override this to return a subclass of PatternParser which recognize
     * custom conversion characters.
     * 
     * @since 0.9.0
     */
    protected Log4jPatternParser createPatternParser(String pattern) {
        return new Log4jPatternParser(pattern);
    }

    /**
     * Produces a formatted string as specified by the conversion pattern.
     */
    public String format(LogEvent event) {
        // Reset working stringbuffer
        if (sbuf.capacity() > MAX_CAPACITY) {
            sbuf = new StringBuffer(BUF_SIZE);
        }
        else {
            sbuf.setLength(0);
        }

        Log4jPatternConvertor c = head;

        while (c != null) {
            c.format(sbuf, event);
            c = c.next;
        }
        return sbuf.toString();
    }
}
