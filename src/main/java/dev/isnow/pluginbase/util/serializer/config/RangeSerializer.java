package dev.isnow.pluginbase.util.serializer.config;

import de.exlll.configlib.Serializer;
import dev.isnow.pluginbase.util.Range;

public class RangeSerializer implements Serializer<Range, String> {
    @Override
    public String serialize(final Range range) {
        return range.getMin() + "-" + range.getMax();
    }

    @Override
    public Range deserialize(final String s) {
        final String[] split = s.split("-");

        return new Range(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
    }
}
