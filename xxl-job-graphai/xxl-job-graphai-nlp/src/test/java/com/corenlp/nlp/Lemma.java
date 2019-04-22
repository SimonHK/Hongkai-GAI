package com.corenlp.nlp;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import edu.stanford.nlp.util.CoreMap;

public class Lemma {

    // 词干化
    public String stemmed(String inputStr) {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation document = new Annotation(inputStr);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        String outputStr = "";
        for (CoreMap sentence : sentences) {
            // traversing the words in the current sentence
            // a CoreLabel is a CoreMap with additional token-specific methods
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                String lema = token.get(LemmaAnnotation.class);
                outputStr += lema+" ";
            }

        }
        return outputStr;
    }
    public static void main(String[]args){

        Lemma lemma=new Lemma();
        String input="jack had been to china there months ago. he likes china very much,and he is falling love with this country";
        String output=lemma.stemmed(input);
        System.out.print("原句    ：");
        System.out.println(input);
        System.out.print("词干化：");
        System.out.println(output);


    }

}
