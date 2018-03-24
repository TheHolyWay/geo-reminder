package ru.holyway.georeminder.nlp.spelling.impl;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.language.Russian;
import org.languagetool.rules.RuleMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.holyway.georeminder.nlp.spelling.SpellingAnalyzer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class JLanguageToolSpellingAnalyzer implements SpellingAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JLanguageToolSpellingAnalyzer.class);

    private JLanguageTool langTool;

    public JLanguageToolSpellingAnalyzer() {
        this(new Russian());
    }

    public JLanguageToolSpellingAnalyzer(Language language) {
        langTool = new JLanguageTool(language);
    }

    @Override
    public List<RuleMatch> match(String text) {

        try {
            List<RuleMatch> matches = langTool.check(text);
            return matches;
        } catch (IOException e) {
            LOGGER.error("Error during spelling check", e);
        }
        return Collections.emptyList();
    }
}