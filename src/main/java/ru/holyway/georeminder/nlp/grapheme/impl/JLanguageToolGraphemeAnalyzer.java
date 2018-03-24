package ru.holyway.georeminder.nlp.grapheme.impl;

import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.language.Russian;
import org.languagetool.tokenizers.Tokenizer;
import org.languagetool.tokenizers.WordTokenizer;
import ru.holyway.georeminder.nlp.grapheme.GraphemeAnalyzer;

import java.util.List;

public class JLanguageToolGraphemeAnalyzer implements GraphemeAnalyzer {

    private Tokenizer tokenizer = new WordTokenizer();

    @Override
    public String[] extractGraphemes(String text) {
        List<String> tokens = tokenizer.tokenize(text);
        return tokens.toArray(new String[tokens.size()]);
    }
}
