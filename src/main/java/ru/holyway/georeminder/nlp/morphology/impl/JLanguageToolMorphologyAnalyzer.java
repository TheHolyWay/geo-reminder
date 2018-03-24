package ru.holyway.georeminder.nlp.morphology.impl;

import org.languagetool.AnalyzedSentence;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.language.Russian;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.holyway.georeminder.nlp.morphology.MorphologyAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class JLanguageToolMorphologyAnalyzer implements MorphologyAnalyzer {

    private static final Logger LOGGER = LoggerFactory.getLogger(JLanguageToolMorphologyAnalyzer.class);

    private JLanguageTool langTool;

    public JLanguageToolMorphologyAnalyzer() {
        this(new Russian());
    }

    public JLanguageToolMorphologyAnalyzer(Language language) {
        langTool = new JLanguageTool(language);
    }

    @Override
    public String[] extractLemmas(String[] words) {
        List<String> result = new ArrayList<>();
        for (String word : words) {
            result.add(extractLemma(word));
        }
        return result.toArray(new String[result.size()]);
    }

    @Override
    public String extractLemma(String word) {
        try {
            //TODO look at the comments below
            List<AnalyzedSentence> sentences = langTool.analyzeText(word);

            Set<String> lemmas = sentences.get(0) //I have only one word...
                    .getLemmaSet(); //How to detect real lemma?
            return lemmas.toArray(new String[lemmas.size()])[1]; // I don't known, how to found better lemma

        } catch (IOException e) {
            LOGGER.error("Error during analyse the text: {}", word, e);
        }
        return null;
    }

    @Override
    public Set<String> extractLemmas(String word) {
        try {
            List<AnalyzedSentence> sentences = langTool.analyzeText(word);
            if (sentences.size() != 1) {
                LOGGER.warn("Define more that one sentence in the word: {}. " +
                        "Will be return result for first sentence.", word);
            }
            return sentences.get(0).getLemmaSet();

        } catch (IOException e) {
            LOGGER.error("Error during analyse the text: {}", word, e);
        }
        return Collections.emptySet();
    }
}
