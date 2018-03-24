package ru.holyway.georeminder.nlp.morphology.impl;

import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import ru.holyway.georeminder.nlp.morphology.MorphologyAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Set;


public class OpenNlpMorphologyAnalyzer implements MorphologyAnalyzer {

    private static final String[] POS_TAGS = new String[] { "NNP", "NNP", "NNP", "POS", "NNP", "NN",
            "VBD", "PRP", "VBD", "DT", "JJ", "NN", "VBG", "PRP$", "NN", "IN",
            "NNP", "NNP", "TO", "VB", "JJ", "NNS", "IN", "NNP", "POS", "CD", "NNS",
            "." };

    private LemmatizerME lemmatizer;

    public OpenNlpMorphologyAnalyzer(String modelPath) throws IOException {
        LemmatizerModel model = new LemmatizerModel(new File(modelPath));
        lemmatizer = new LemmatizerME(model);
    }

    public OpenNlpMorphologyAnalyzer(LemmatizerModel model) {
        lemmatizer = new LemmatizerME(model);
    }

    @Override
    public String[] extractLemmas(String[] words) {
        String[] lemmas = lemmatizer.lemmatize(words, POS_TAGS);
        return lemmatizer.decodeLemmas(words, lemmas);
    }

    @Override
    public String extractLemma(String word) {
        return extractLemmas(new String[] { word })[0];
    }

    @Override
    public Set<String> extractLemmas(String word) {
        return Collections.singleton(extractLemma(word));
    }

    public static LemmatizerModel getDefaultModelRu() throws IOException {
        ClassLoader classLoader = OpenNlpMorphologyAnalyzer.class.getClassLoader();

        InputStream modelStream = classLoader.getResourceAsStream("model/ru-lemmatizer.bin");
        return new LemmatizerModel(modelStream);
    }
}
