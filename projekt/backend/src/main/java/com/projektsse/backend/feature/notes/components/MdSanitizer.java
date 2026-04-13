package com.projektsse.backend.feature.notes.components;

import org.jsoup.Jsoup;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class MdSanitizer {
    /*
    * Taken from Documentation: https://jsoup.org/apidocs/org/jsoup/safety/Safelist.html
    */

    private static final Safelist MARKDOWN_SAFELIST = Safelist.simpleText() // b, em, i, strong, u zulassen
            .addTags("h1", "h2", "h3", "h4", "h5", "h6") // Überschriften zulassen
            .addTags("code", "pre", "br", "hr") // Codeblöcke und Zeilenumbrüche zulassen
            .addTags("table", "thead", "tbody", "tfoot", "tr", "td", "th") // Tabellen zulasssen
            .addTags("blockquote") // Blockzitate zulassen
            .addTags("ul", "ol", "li"); // Listen zulassen

    private static final String NEWLINE_PLACEHOLDER = "___NEWLINE___";

    public String sanitizeContent(String markdown) {
        if (markdown == null) return "";
        if (markdown.length() > 10000) {
            markdown = markdown.substring(0, 10000);
        }

        String markdownWithPlaceholders = markdown
                .replace("\r\n", NEWLINE_PLACEHOLDER)
                .replace("\n", NEWLINE_PLACEHOLDER);

        String cleanedMarkdown = Jsoup.clean(markdownWithPlaceholders, MARKDOWN_SAFELIST);

        return cleanedMarkdown.replace(NEWLINE_PLACEHOLDER, "\n");
    }

    public String sanitizeTitle(String title) {
        if (title == null) return "";
        Cleaner cleaner = new Cleaner(Safelist.none());
        return cleaner.clean(Jsoup.parse(title)).text();
    }
}
