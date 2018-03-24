package ru.holyway.georeminder.nlp.spelling;

import org.languagetool.rules.RuleMatch;

import java.util.List;

public interface SpellingAnalyzer {

    List<RuleMatch> match(String text);
}
