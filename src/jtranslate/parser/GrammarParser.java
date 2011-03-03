package jtranslate.parser;

import de.susebox.jtopas.*;
import jtranslate.grammar.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;

public class GrammarParser
{
    public GrammarSet parse(File f) throws FileNotFoundException, TokenizerException {
        StandardTokenizerProperties tps = new StandardTokenizerProperties();
        tps.addBlockComment("/*", "*/");
        tps.addLineComment("//");
        tps.addSpecialSequence("->");
        tps.setParseFlags(Flags.F_COUNT_LINES);

        tps.addSpecialSequence("@");


        ReaderSource src = new ReaderSource(new FileReader(f));
        StandardTokenizer tokenizer = new StandardTokenizer(tps);
        tokenizer.setSource(src);

        GrammarSet gs = new GrammarSet();
        Token t = null;

        while(tokenizer.hasMoreToken() && (t = tokenizer.nextToken()).getType() != Token.EOF) {
            if(t.getType() == Token.SPECIAL_SEQUENCE && t.getImage().equals("@"))
            {
                RewriteParser parser = new RewriteParser();
                LinkedList<RewriteRule> rules = parser.parse(tokenizer);
                gs.addRewriteRules(rules);
            }
            else {
                GrammarRuleParser parser = new GrammarRuleParser();
                GrammarRule rule = parser.parse(tokenizer);
                gs.addGrammarRule(rule);
            }
        }

        tokenizer.close();
        src.close();

        return gs;
    }
}