package dev.isnow.pluginbase.util;

import lombok.Getter;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class NumberAbbreviator {
    public static final int DECIMAL_PLACES = 4;

    private static final NavigableMap<BigDecimal, String> SUFFIXES = new TreeMap<>();
    private static final Map<String, BigDecimal> REVERSE_LOOKUP;
    private static final Pattern PARSE_PATTERN = Pattern.compile("([\\d,.]+)([a-zA-Z]+)");
    private static final DecimalFormat FORMATTER = new DecimalFormat("#,##0.##");
    private static final DecimalFormat PLAIN_FORMATTER = new DecimalFormat("#,##0");

    @Getter
    private static final List<String> COMMON_ABBREVIATION_SAMPLES;

    static {
        SUFFIXES.put(new BigDecimal("1E3"), "K");
        SUFFIXES.put(new BigDecimal("1E6"), "M");
        SUFFIXES.put(new BigDecimal("1E9"), "B");
        SUFFIXES.put(new BigDecimal("1E12"), "T");
        SUFFIXES.put(new BigDecimal("1E15"), "Q");
        SUFFIXES.put(new BigDecimal("1E18"), "Qu");
        SUFFIXES.put(new BigDecimal("1E21"), "S");
        SUFFIXES.put(new BigDecimal("1E24"), "Se");
        SUFFIXES.put(new BigDecimal("1E27"), "O");
        SUFFIXES.put(new BigDecimal("1E30"), "N");
        SUFFIXES.put(new BigDecimal("1E33"), "D");
        SUFFIXES.put(new BigDecimal("1E36"), "UD");
        SUFFIXES.put(new BigDecimal("1E39"), "DD");
        SUFFIXES.put(new BigDecimal("1E42"), "TD");
        SUFFIXES.put(new BigDecimal("1E45"), "QaD");
        SUFFIXES.put(new BigDecimal("1E48"), "QiD");
        SUFFIXES.put(new BigDecimal("1E51"), "SeD");
        SUFFIXES.put(new BigDecimal("1E54"), "SD");
        SUFFIXES.put(new BigDecimal("1E57"), "OD");
        SUFFIXES.put(new BigDecimal("1E60"), "ND");

        REVERSE_LOOKUP = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        SUFFIXES.forEach((key, value) -> REVERSE_LOOKUP.put(value, key));

        COMMON_ABBREVIATION_SAMPLES = new ArrayList<>();
        for (final BigDecimal key : SUFFIXES.keySet()) {
            final String suffix = SUFFIXES.get(key);
            COMMON_ABBREVIATION_SAMPLES.add("1" + suffix);
            COMMON_ABBREVIATION_SAMPLES.add("10" + suffix);
            COMMON_ABBREVIATION_SAMPLES.add("100" + suffix);

            COMMON_ABBREVIATION_SAMPLES.add(convertToScientific(key));
            COMMON_ABBREVIATION_SAMPLES.add(convertToScientific(key.multiply(BigDecimal.TEN)));
            COMMON_ABBREVIATION_SAMPLES.add(convertToScientific(key.multiply(BigDecimal.valueOf(100))));
        }
    }

    public String format(final BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        }

        final BigDecimal absValue = value.abs();
        final Map.Entry<BigDecimal, String> entry = SUFFIXES.floorEntry(absValue);

        if (entry == null) {
            return PLAIN_FORMATTER.format(value);
        }

        final String sign = value.signum() < 0 ? "-" : "";
        final BigDecimal divisor = entry.getKey();
        final String suffix = entry.getValue();

        final BigDecimal scaledValue = absValue.divide(divisor, DECIMAL_PLACES, RoundingMode.HALF_UP);
        return sign + FORMATTER.format(scaledValue) + suffix;
    }

    public BigDecimal parse(final String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        final String sanitizedValue = value.trim().replace(",", "");
        final Matcher matcher = PARSE_PATTERN.matcher(sanitizedValue);

        if (!matcher.matches()) {
            try {
                return new BigDecimal(sanitizedValue);
            } catch (final NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }

        try {
            final String numberPart = matcher.group(1);
            final String suffixPart = matcher.group(2);

            final BigDecimal number = new BigDecimal(numberPart);
            final BigDecimal multiplier = REVERSE_LOOKUP.get(suffixPart);

            if (multiplier == null) {
                return new BigDecimal(sanitizedValue);
            }

            return number.multiply(multiplier);
        } catch (final Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public String convertToScientific(final BigDecimal value) {
        return value.stripTrailingZeros().toString().replace("+", "");
    }
}
