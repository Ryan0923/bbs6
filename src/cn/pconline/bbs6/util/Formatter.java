package cn.pconline.bbs6.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class Formatter {

    @SuppressWarnings("unchecked")
    public static String fromatRequestParameter(Map paramterMap) {
        StringBuffer buf = new StringBuffer();
        List names = new ArrayList();
        for (Iterator it = paramterMap.keySet().iterator(); it.hasNext();)
            names.add(it.next());

        buf.append('{');
        Collections.sort(names);
        for (int i = 0, c = names.size(); i < c; ++i) {
            String name = (String) names.get(i);
            buf.append(name).append('=');
            buf.append(format(paramterMap.get(name)));
            if (i < c - 1)
                buf.append(", ");

        }
        buf.append('}');
        return buf.toString();
    }

    private static String format(Object object) {
        String[] values = (String[]) object;
        if (values.length == 1)
            return values[0];
        StringBuffer buf = new StringBuffer();
        buf.append('[');
        for (int i = 0, c = values.length; i < c; ++i) {
            buf.append(values[i]);
            if (i < c - 1)
                buf.append(", ");
        }
        buf.append(']');
        return buf.toString();
    }

    public static String formatMethodArgumentts(Object[] arguments) {
        if (arguments == null) {
            return "{}";
        }
        StringBuffer buf = new StringBuffer();
        buf.append("{");

        for (int i = 0, c = arguments.length; i < c; ++i) {
            if (arguments[i] != null) {
                buf.append(arguments[i].getClass()).append("=");
                buf.append(arguments[i].toString());
            }

            if (i < c - 1)
                buf.append(", ");
        }

        buf.append("}");
        return buf.toString();
    }

}