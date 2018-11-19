//import edu.stanford.nlp.coref.data.CorefChain;
//import edu.stanford.nlp.ling.*;
//import edu.stanford.nlp.ie.util.*;
//import edu.stanford.nlp.pipeline.*;
//import edu.stanford.nlp.semgraph.*;
//import edu.stanford.nlp.trees.*;
//import java.util.*;
//import edu.stanford.nlp.simple.*;
//import edu.stanford.nlp.util.CoreMap;
//
//
//public class testStream {
//
//    public static String text = "Running";
//
//    public static void main(String[] args) {
//
////        Sentence sent = new Sentence("Lucy is in the sky with diamonds.");
////        List<String> nerTags = sent.nerTags();  // [PERSON, O, O, O, O, O, O, O]
////        List<String> firstPOSTag = sent.posTags();   // NNP
////
////        for(String pos : firstPOSTag){
////            System.out.println(pos);
////        }
//
//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize, ssplit, pos");
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//
//        // read some text in the text variable
//        String text = "wdvvdgfhfgdfsx";
//
//        // create an empty Annotation just with the given text
//        Annotation document = new Annotation(text);
//
//        // run all Annotators on this text
//        pipeline.annotate(document);
//
//        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
//
//        for(CoreMap sentence: sentences) {
//            // traversing the words in the current sentence
//            // a CoreLabel is a CoreMap with additional token-specific methods
//            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//                // this is the text of the token
//                String word = token.get(CoreAnnotations.TextAnnotation.class);
//                // this is the POS tag of the token
//                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
//                // this is the NER label of the token
//                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//                System.out.println(pos);
//            }
//        }
//
//        System.out.println("ONE - DONE");
//
//        Annotation doc2 = new Annotation("This is some other text for Paul");
//        pipeline.annotate(document);
//
//        List<CoreMap> sentences2 = document.get(CoreAnnotations.SentencesAnnotation.class);
//
//        for(CoreMap sentence: sentences2) {
//            // traversing the words in the current sentence
//            // a CoreLabel is a CoreMap with additional token-specific methods
//            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
//                // this is the text of the token
//                String word = token.get(CoreAnnotations.TextAnnotation.class);
//                // this is the POS tag of the token
//                String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
//                // this is the NER label of the token
//                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//                System.out.println(pos);
//            }
//        }
//
//        String firstPOSTag = "something"
//
//        switch(firstPOSTag){
//
//            /*
//            CC Coordinating conjunction
//CD Cardinal number
//DT Determiner
//EX Existential there
//FW Foreign word
//IN Preposition or subordinating conjunction
//JJ Adjective
//JJR Adjective, comparative
//JJS Adjective, superlative
//LS List item marker
//MD Modal
//NN Noun, singular or mass
//NNS Noun, plural
//NNP Proper noun, singular
//NNPS Proper noun, plural
//PDT Predeterminer
//POS Possessive ending
//PRP Personal pronoun
//PRP$ Possessive pronoun
//RB Adverb
//RBR Adverb, comparative
//RBS Adverb, superlative
//RP Particle
//SYM Symbol
//TO to
//UH Interjection
//VB Verb, base form
//VBD Verb, past tense
//VBG Verb, gerund or present participle
//VBN Verb, past participle
//VBP Verb, non­3rd person singular present
//VBZ Verb, 3rd person singular present
//WDT Wh­determiner
//WP Wh­pronoun
//WP$ Possessive wh­pronoun
//WRB Wh­adverb
//             */
//
//            case "CD":
//                return ("Cardinal number");
//                break;
//
//            case "DT":
//                return ("Determiner");
//                break;
//
//            case "EX":
//                return ("Existential there");
//                break;
//
//            case "FW":
//                return ("Foreign word");
//                break;
//
//            case "IN":
//                return ("Preposition or subordinating conjunction");
//                break;
//
//            case "JJ":
//                return ("Adjective");
//                break;
//
//            case "JJR":
//                return ("Adjective");
//                break;
//
//            case "JJS":
//                return ("Adjective");
//                break;
//
//            case "LS":
//                return ("List item marker");
//                break;
//
//            case "MD":
//                return ("Modal");
//                break;
//
//            case "NN":
//                return ("Noun");
//                break;
//
//            case "NNS":
//                return ("Noun");
//                break;
//
//            case "NNP":
//                return ("Noun");
//                break;
//
//            case "PDT":
//                return ("Predeterminer");
//                break;
//
//            case "POS":
//                return ("Possessive ending");
//                break;
//
//            case "PRP":
//                return ("Personal pronoun");
//                break;
//
//            case "PRP$":
//                return ("Possessive pronoun");
//                break;
//
//            case "RB":
//                return ("Adverb");
//                break;
//
//            case "RBR":
//                return ("Adverb");
//                break;
//
//            case "RBS":
//                return ("Adverb");
//                break;
//
//            case "RP":
//                return ("Particle");
//                break;
//
//            case "SYM":
//                return ("Symbol");
//                break;
//
//            case "TO":
//                return ("To");
//                break;
//
//            case "UH":
//                return ("Interjection");
//                break;
//
//            case "VB":
//                return ("Verb");
//                break;
//
//            case "VBD":
//                return ("Verb");
//                break;
//
//            case "VBN":
//                return ("Verb");
//                break;
//
//            case "VBZ":
//                return ("Verb");
//                break;
//
//            case "VBP":
//                return ("Verb");
//                break;
//
//            case "WDT":
//                return ("Wh-determiner");
//                break;
//
//            case "WP":
//                return ("Wh-pronoun");
//                break;
//
//            case "WP$":
//                return ("Possessive wh-pronoun");
//                break;
//
//            case "WRB":
//                return ("Wh-adverb");
//                break;
//        }
//
//
//    }
//
//}
//
