package com.github.danshan.jiraformatmd.tools

import spock.lang.Specification

/**
 * @author shanhonghao
 */
class MarkdownToolsTest extends Specification {

    def "convert null"() {
        given:
        when:
        MarkdownTools.toMarkdown(null)

        then:
        thrown(IllegalArgumentException)
    }

    def "convert single line quote"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input                    || output
        'line1\nbq.line2\nline3' || 'line1\n> line2\nline3'
    }

    def "convert strong"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input          || output
        'abc*test*def' || 'abc**test**def'
        'abc_test_def' || 'abc*test*def'
    }

    def "convert multi level numbered list"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input      || output
        '# line'   || '  1. line'
        ' # line'  || ' # line'
        '## line'  || '      1. line'
        ' ## line' || ' ## line'
    }

    def "convert headers"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input        || output
        'h0.header'  || '#header'
        ' h0.header' || ' h0.header'
        'h5.header'  || '######header'
        ' h5.header' || ' h5.header'
    }

    def "convert monospacesd"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input           || output
        'abc{{123}}def' || 'abc`123`def'
    }

    def "convert citation"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input           || output
        'abc??123??def' || 'abc<cite>123</cite>def'
    }

    def "convert deleted"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input       || output
        '-deleted-' || '-deleted-'
    }


    def "convert inserted"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input        || output
        '+inserted+' || '<ins>inserted</ins>'
    }

    def "convert superscript"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input           || output
        '^superscript^' || '<sup>superscript</sup>'
    }

    def "convert subscript"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input         || output
        '~subscript~' || '<sub>subscript</sub>'
    }

    def "convert code"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input                       || output
        '{code:java}\njava\n{code}' || '```java\njava\n```'
    }

    def "convert quote lines"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input                                  || output
        '{quote}\r\nline1\r\nline2\r\n{quote}' || '> \n> line1\n> line2'
    }

    def "convert image"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input                     || output
        '!http://google.com.gif!' || '![](http://google.com.gif)'
    }

    def "convert link"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input                        || output
        '[http://google.com] '       || '<http://google.com> '
        '[Google|http://google.com]' || '[Google](http://google.com)'
    }

    def "convert unformat"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input                                                                 || output
        '''{noformat}\n  so *no* further _formatting_ is done here\n{noformat}''' || '```\n  so **no** further *formatting* is done here\n```'
    }

    def "convert color"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input                                        || output
        '{color:red}\n  look ma, red text!\n{color}' || "<span style=\"color:red\">\n  look ma, red text!\n</span>"
    }

    def "convert tabel"() {
        expect:
        MarkdownTools.toMarkdown(input) == output

        where:
        input                                        || output
        '||H1||H2||\n|C1|C2|\n\n||H3||H4||\n|C3|C4|' || '|H1|H2|\n|---|---|\n|C1|C2|\n\n|H3|H4|\n|---|---|\n|C3|C4|'
    }

}
