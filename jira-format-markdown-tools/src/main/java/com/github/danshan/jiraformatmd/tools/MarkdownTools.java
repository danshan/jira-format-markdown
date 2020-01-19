package com.github.danshan.jiraformatmd.tools;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
public class MarkdownTools {

    /**
     * Takes Jira markup and converts it to Markdown.
     * <p>
     * https://jira.atlassian.com/secure/WikiRendererHelpAction.jspa?section=all
     *
     * @param input Jira markup text
     * @returns Markdown formatted text
     */
    public static String toMarkdown(String input) {
        Convertor convertor = Optional.ofNullable(input)
            .map(Convertor::new)
            .orElseThrow(() -> new IllegalArgumentException("input should not be null"));

        return convertor
            .convertQuote()
            .convertStrong()
            .convertMultiLevelNumberedList()
            .convertHeader()
            .convertMonospaced()
            .convertCitation()
            .convertIns()
            .convertSup()
            .convertSub()
            .convertDel()
            .convertCode()
            .convertQuoteLines()
            .convertImage()
            .convertLink()
            .convertTable()
            .convertUnformat()
            .convertColor()
            .finish();
    }

    private static class Convertor {

        private String text;

        public Convertor(String text) {
            this.text = text;
        }

        private Convertor convertQuote() {
            Pattern pattern = Pattern.compile("^(bq\\.)(.*)$", Pattern.MULTILINE);
            text = pattern.matcher(text).replaceAll("> $2");
            return this;
        }

        private Convertor convertStrong() {
            Pattern pattern = Pattern.compile("([*_])(.*)\\1");
            Matcher matcher = pattern.matcher(text);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String star = "*";
                if (matcher.group(1).equals("*")) {
                    star = "**";
                }
                matcher.appendReplacement(sb, (star + matcher.group(2) + star));
            }
            matcher.appendTail(sb);
            this.text = sb.toString();
            return this;
        }

        private Convertor convertMultiLevelNumberedList() {
            // multi-level numbered list
            Pattern pattern = Pattern.compile("^((?:#|-|\\+|\\*)+) (.*)$", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(text);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String level = matcher.group(1);
                String content = matcher.group(2);

                int len = 2;
                if (level.length() > 1) {
                    len = (level.length() - 1) * 4 + 2;
                }
                // take the last character of the level to determine the replacement
                String prefix = level.substring(level.length() - 1);
                if (prefix.equals("#")) {
                    prefix = "1.";
                }
                matcher.appendReplacement(sb, StringUtils.repeat(" ", len) + prefix + " " + content);
            }

            matcher.appendTail(sb);
            this.text = sb.toString();
            return this;
        }

        private Convertor convertHeader() {
            // headers, must be after numbered lists
            Pattern pattern = Pattern.compile("^h([0-6])\\.(.*)$", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(text);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String level = matcher.group(1);
                String content = matcher.group(2);

                matcher.appendReplacement(sb, StringUtils.repeat("#", Integer.parseInt(level) + 1) + content);
            }

            matcher.appendTail(sb);
            this.text = sb.toString();
            return this;
        }

        private Convertor convertMonospaced() {
            Pattern pattern = Pattern.compile("\\{\\{([^}]+)}}");
            text = pattern.matcher(text).replaceAll("`$1`");
            return this;
        }

        private Convertor convertCitation() {
            Pattern pattern = Pattern.compile("\\?\\?([^?]+)\\?\\?");
            text = pattern.matcher(text).replaceAll("<cite>$1</cite>");
            return this;
        }

        private Convertor convertIns() {
            Pattern pattern = Pattern.compile("\\+([^+]*)\\+");
            text = pattern.matcher(text).replaceAll("<ins>$1</ins>");
            return this;
        }

        private Convertor convertSup() {
            Pattern pattern = Pattern.compile("\\^([^^]*)\\^");
            text = pattern.matcher(text).replaceAll("<sup>$1</sup>");
            return this;
        }

        private Convertor convertSub() {
            Pattern pattern = Pattern.compile("~([^~]*)~");
            text = pattern.matcher(text).replaceAll("<sub>$1</sub>");
            return this;
        }

        private Convertor convertDel() {
            Pattern pattern = Pattern.compile("-([^-]*)-");
            text = pattern.matcher(text).replaceAll("-$1-");
            return this;
        }

        private Convertor convertCode() {
            Pattern pattern = Pattern.compile("\\{code(?::([a-z]+))?}([\\s\\S]*?)\\{code}", Pattern.MULTILINE);
            text = pattern.matcher(text).replaceAll("```$1$2```");
            return this;
        }

        private Convertor convertQuoteLines() {
            Pattern pattern = Pattern.compile("\\{quote}([\\s\\S]*)\\{quote}", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(text);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String content = matcher.group(1);
                String[] lines = content.split("\r?\n");

                for (int i = 0; i < lines.length; i++) {
                    lines[i] = "> " + lines[i];
                }
                String atfer = Joiner.on("\n").join(lines);
                matcher.appendReplacement(sb, atfer);
            }

            matcher.appendTail(sb);
            this.text = sb.toString();
            return this;
        }

        private Convertor convertImage() {
            Pattern pattern = Pattern.compile("!([^\\s]+)!");
            text = pattern.matcher(text).replaceAll("![]($1)");
            return this;
        }

        private Convertor convertLink() {
            Pattern pattern1 = Pattern.compile("\\[([^|]+)\\|(.+?)]");
            text = pattern1.matcher(text).replaceAll("[$1]($2)");

            Pattern pattern2 = Pattern.compile("\\[(.+?)]([^(]+|$)");
            text = pattern2.matcher(text).replaceAll("<$1>$2");
            return this;
        }

        private Convertor convertUnformat() {
            text = text.replaceAll("\\{noformat}", "```");
            return this;
        }

        private Convertor convertColor() {
            Pattern pattern = Pattern.compile("\\{color:([^}]+)}([\\s\\S]*?)\\{color}", Pattern.MULTILINE);
            text = pattern.matcher(text).replaceAll("<span style=\"color:$1\">$2</span>");
            return this;
        }

        private Convertor convertTable() {
            // Convert header rows of tables by splitting input on lines
            List<String> lines = Lists.newArrayList(text.split("\r?\n"));
            Pattern sepPtn = Pattern.compile("\\|\\|");
            Matcher matcher;
            for (int i = 0; i < lines.size(); i++) {
                String lineContent = lines.get(i);
                if ((matcher = sepPtn.matcher(lineContent)).find()) {
                    int columnCnt = lineContent.split("\\|\\|").length;
                    lines.set(i, matcher.replaceAll("|"));

                    StringBuilder headerLine = new StringBuilder();
                    for (int j = 0; j < columnCnt - 1; j++) {
                        headerLine.append("|---");
                    }
                    headerLine.append("|");
                    lines.add(i + 1, headerLine.toString());
                }
            }

            text = Joiner.on("\n").join(lines);
            return this;
        }


        private String finish() {
            return this.text;
        }
    }

}
