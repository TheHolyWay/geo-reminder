package ru.holyway.georeminder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.holyway.georeminder.nlp.grapheme.impl.OpenNlpGraphemeAnalyzer;
import ru.holyway.georeminder.nlp.morphology.impl.JLanguageToolMorphologyAnalyzer;
import ru.holyway.georeminder.nlp.morphology.impl.OpenNlpMorphologyAnalyzer;
import ru.holyway.georeminder.nlp.spelling.SpellingAnalyzer;
import ru.holyway.georeminder.nlp.spelling.impl.JLanguageToolSpellingAnalyzer;
import ru.holyway.georeminder.nlp.util.Lemmatizer;

import java.io.IOException;

@Configuration
public class NlpConfiguration {

    @Bean("nlp.morphology.OpenNlpMorphologyAnalyzer")
    public OpenNlpMorphologyAnalyzer openNlpMorphologyAnalyzer() throws IOException {
        return new OpenNlpMorphologyAnalyzer(OpenNlpMorphologyAnalyzer.getDefaultModelRu());
    }

    @Bean("nlp.morphology.JLanguageToolMorphologyAnalyzer")
    public JLanguageToolMorphologyAnalyzer jLanguageToolMorphologyAnalyzer() {
        return new JLanguageToolMorphologyAnalyzer();
    }

    @Bean
    public OpenNlpGraphemeAnalyzer openNlpGraphemeAnalyzer() throws IOException {
        return new OpenNlpGraphemeAnalyzer(OpenNlpGraphemeAnalyzer.getDefaultModelRu());
    }

    @Bean
    public Lemmatizer lemmatizer(OpenNlpMorphologyAnalyzer openNlpMorphologyAnalyzer, JLanguageToolMorphologyAnalyzer jLanguageToolMorphologyAnalyzer) {
        return new Lemmatizer(jLanguageToolMorphologyAnalyzer, openNlpMorphologyAnalyzer);
    }

    @Bean
    public SpellingAnalyzer spellingAnalyzer() {
        return new JLanguageToolSpellingAnalyzer();
    }
}
