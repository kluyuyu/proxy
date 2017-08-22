package org.tiger.proxy.parsing.parser;

import com.google.common.collect.Sets;


import java.util.Set;

/**
 * 解析器.
 *
 * @author fish
 */
public abstract class AbstractParser {
    

    private final Lexer lexer;
    
    /**
     * 跳过小括号内所有的词法标记.
     *
     * @return 小括号内所有的词法标记
     */
    // TODO 判断? 增加param index
    public final String skipParentheses() {
        StringBuilder result = new StringBuilder("");
        int count = 0;
        if (Symbol.LEFT_PAREN == getLexer().getCurrentToken().getType()) {
            final int beginPosition = getLexer().getCurrentToken().getEndPosition();
            result.append(Symbol.LEFT_PAREN.getLiterals());
            getLexer().nextToken();
            while (true) {
                if (Assist.END == getLexer().getCurrentToken().getType() || (Symbol.RIGHT_PAREN == getLexer().getCurrentToken().getType() && 0 == count)) {
                    break;
                }
                if (Symbol.LEFT_PAREN == getLexer().getCurrentToken().getType()) {
                    count++;
                } else if (Symbol.RIGHT_PAREN == getLexer().getCurrentToken().getType()) {
                    count--;
                }
                getLexer().nextToken();
            }
            result.append(getLexer().getInput().substring(beginPosition, getLexer().getCurrentToken().getEndPosition()));
            getLexer().nextToken();
        }
        return result.toString();
    }
    
    /**
     * 断言当前词法标记类型与传入值相等并跳过.
     *
     * @param tokenType 待判断的词法标记类型
     */
    public final void accept(final TokenType tokenType) {
        if (lexer.getCurrentToken().getType() != tokenType) {
            throw new SQLParsingException(lexer, tokenType);
        }
        lexer.nextToken();
    }
    
    /**
     * 判断当前词法标记类型是否与其中一个传入值相等.
     *
     * @param tokenTypes 待判断的词法标记类型
     * @return 是否有相等的词法标记类型
     */
    public final boolean equalAny(final TokenType... tokenTypes) {
        for (TokenType each : tokenTypes) {
            if (each == lexer.getCurrentToken().getType()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 如果当前词法标记类型等于传入值, 则跳过.
     *
     * @param tokenTypes 待跳过的词法标记类型
     * @return 是否跳过(或可理解为是否相等)
     */
    public final boolean skipIfEqual(final TokenType... tokenTypes) {
        if (equalAny(tokenTypes)) {
            lexer.nextToken();
            return true;
        }
        return false;
    }
    
    /**
     * 跳过所有传入的词法标记类型.
     *
     * @param tokenTypes 待跳过的词法标记类型
     */
    public final void skipAll(final TokenType... tokenTypes) {
        Set<TokenType> tokenTypeSet = Sets.newHashSet(tokenTypes);
        while (tokenTypeSet.contains(lexer.getCurrentToken().getType())) {
            lexer.nextToken();
        }
    }
    
    /**
     * 直接跳转至传入的词法标记类型.
     *
     * @param tokenTypes 跳转至的词法标记类型
     */
    public final void skipUntil(final TokenType... tokenTypes) {
        Set<TokenType> tokenTypeSet = Sets.newHashSet(tokenTypes);
        tokenTypeSet.add(Assist.END);
        while (!tokenTypeSet.contains(lexer.getCurrentToken().getType())) {
            lexer.nextToken();
        }
    }
}
