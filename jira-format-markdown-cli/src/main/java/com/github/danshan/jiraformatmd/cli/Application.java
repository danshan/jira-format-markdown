package com.github.danshan.jiraformatmd.cli;

import picocli.CommandLine;

/**
 * @author shanhonghao
 * @since 1.0.0
 */
public class Application {

    public static void main(String[] args) {
        System.exit(new CommandLine(new MarkdownCommand()).execute(args));
    }

}
