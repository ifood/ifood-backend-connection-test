package br.com.ifood.ifoodbackendconnection.utilities;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringSubstitutor {

    private static final Pattern escapePattern = Pattern.compile("\\$\\{(.+?)\\}");

    public static String replace(String str, Map<String, String> variablesMapping) {
        Matcher matcher = escapePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String var = matcher.group(1);
            if (variablesMapping.containsKey(var)) {
                String replacement = variablesMapping.get(var);
                matcher.appendReplacement(sb, replacement);
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}