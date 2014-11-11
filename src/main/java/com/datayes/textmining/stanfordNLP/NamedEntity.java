package com.datayes.textmining.stanfordNLP;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations;

import java.util.List;
import java.io.IOException;



/** This is a demo of calling CRFClassifier programmatically.
 *  <p>
 *  Usage: <code> java -mx400m -cp "stanford-ner.jar:." NERDemo [serializedClassifier [fileName]]</code>
 *  <p>
 *  If arguments aren't specified, they default to
 *  ner-eng-ie.crf-3-all2006.ser.gz and some hardcoded sample text.
 *  <p>
 *  To use CRFClassifier from the command line:
 *  java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier
 *      [classifier] -textFile [file]
 *  Or if the file is already tokenized and one word per line, perhaps in
 *  a tab-separated value format with extra columns for part-of-speech tag,
 *  etc., use the version below (note the 's' instead of the 'x'):
 *  java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier
 *      [classifier] -testFile [file]
 *
 *  @author Jenny Finkel
 *  @author Christopher Manning
 */

public class NamedEntity {

    public static void main(String[] args) throws IOException {

      String serializedClassifier = "classifiers/chinese.misc.distsim.crf.ser.gz";

      if (args.length > 0) {
        serializedClassifier = args[0];
      }

      AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifierNoExceptions(serializedClassifier);


        String s1 = "第二届 董事会 第二十二次 会议 相关事项";
       // String s2 = "I go to school at Stanford University, which is located in California.";
        System.out.println(classifier.classifyToString(s1));
        System.out.println(classifier.classifyWithInlineXML(s1));
        System.out.println(classifier.classifyToString(s1, "xml", true));
        int i=0;
        for (List<CoreLabel> lcl : classifier.classify(s1)) {
          for (CoreLabel cl : lcl) {
            System.out.println(i++ + ":");
            System.out.println(cl);
          }
        }
    }
}