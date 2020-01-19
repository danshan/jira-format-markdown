package com.github.danshan.jiraformatmd.cli;

import com.github.danshan.jiraformatmd.tools.MarkdownTools;
import com.google.common.io.Files;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.Callable;

import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;

@Command(name = "markdown-cli", mixinStandardHelpOptions = true)
public class MarkdownCommand implements Callable<Integer> {

    @Option(names = {"-i", "--input"}, required = true, description = "input file.")
    private String input;

    @Option(names = {"-c", "--charset"}, required = true, description = "charset. (default: ${DEFAULT-VALUE})")
    private String charset = "UTF-8";

    @Override
    public Integer call() throws Exception {
        File file = new File(input);
        Files.asCharSource(file, Charset.forName(charset));
        String markdown = MarkdownTools.toMarkdown(this.input);
        System.out.println(markdown);
        return 0;
    }

}
