package ru.holyway.georeminder.nlp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.holyway.georeminder.nlp.morphology.impl.JLanguageToolMorphologyAnalyzer;
import ru.holyway.georeminder.nlp.morphology.impl.OpenNlpMorphologyAnalyzer;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


public class Lemmatizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Lemmatizer.class);

    private JLanguageToolMorphologyAnalyzer jLanguageToolMorphologyAnalyzer;
    private OpenNlpMorphologyAnalyzer openNlpMorphologyAnalyzer;

    public Lemmatizer(JLanguageToolMorphologyAnalyzer jLanguageToolMorphologyAnalyzer, OpenNlpMorphologyAnalyzer openNlpMorphologyAnalyzer) {
        this.jLanguageToolMorphologyAnalyzer = jLanguageToolMorphologyAnalyzer;
        this.openNlpMorphologyAnalyzer = openNlpMorphologyAnalyzer;
    }

    public String resolveLemma(String word) {
        LOGGER.trace("Start extract lemma for the word: {}", word);
        String dubiousLemma = openNlpMorphologyAnalyzer.extractLemma(word);
        LOGGER.trace("Dubious lemma: {}", dubiousLemma);

        Set<String> lemmasSet = jLanguageToolMorphologyAnalyzer.extractLemmas(word);
        List<String> trustedLemmas  = lemmasSet.stream().collect(Collectors.toList());
        trustedLemmas.removeIf(String::isEmpty);

        LOGGER.trace("Trusted lemmas: {}", trustedLemmas);

        if (trustedLemmas.size() == 1) {
            Optional<String> lemma = trustedLemmas.stream().findFirst();
            if (lemma.isPresent()) {
                LOGGER.trace("Returned lemma: {}", lemma.get());
                return lemma.get();
            }
        }
        if (trustedLemmas.contains(dubiousLemma)) {
            LOGGER.trace("Returned lemma: {}", dubiousLemma);
            return dubiousLemma;
        }

        Optional<String> lemma = trustedLemmas.stream().findFirst();
        if (lemma.isPresent()) {
            LOGGER.trace("Returned lemma: {}", lemma.get());
            return lemma.get();
        }

        LOGGER.trace("Cant resolve lemma for word {}. " +
                "Will return null", word);
        return null;
    }
}
