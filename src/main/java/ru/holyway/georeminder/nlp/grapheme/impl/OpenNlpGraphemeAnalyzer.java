package ru.holyway.georeminder.nlp.grapheme.impl;

import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import ru.holyway.georeminder.nlp.grapheme.GraphemeAnalyzer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


public class OpenNlpGraphemeAnalyzer implements GraphemeAnalyzer {

    private Tokenizer tokenizer;

    public OpenNlpGraphemeAnalyzer(String modelPath) throws IOException {
        TokenizerModel tokenizerModel = new TokenizerModel(new File(modelPath));
        tokenizer = new TokenizerME(tokenizerModel);
    }

    public OpenNlpGraphemeAnalyzer(TokenizerModel tokenizerModel) {
        tokenizer = new TokenizerME(tokenizerModel);
    }

    public OpenNlpGraphemeAnalyzer() {
        tokenizer = SimpleTokenizer.INSTANCE;
    }

    @Override
    public String[] extractGraphemes(String text) {
        return tokenizer.tokenize(text);
    }

    public static TokenizerModel getDefaultModelRu() throws IOException {
        ClassLoader classLoader = OpenNlpGraphemeAnalyzer.class.getClassLoader();

        InputStream modelStream = classLoader.getResourceAsStream("model/ru-tokenizer.bin");
        return new TokenizerModel(modelStream);
    }
}
