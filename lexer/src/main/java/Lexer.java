
import exception.LexicalException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class Lexer {

    public List<Token> scanner(PeekIterator<Character> it) throws LexicalException {
        List<Token> tokenList = new ArrayList<>();

        while (it.hasNext()) {
            char c = it.next();
            if (c == 0) {
                // 结束符
                break;
            }

            // 预看一位
            char lookahead = it.peek();

            // 处理空格或换行符
            if (c == ' ' || c == '\n') {
                continue;
            }

            // 删除注释
            if (c == '/') {
                if (lookahead == '/') {
                    // 以双//开头, 单行注释
                    while (it.hasNext() && (c = it.next()) != '\n') {

                    }
                    continue;
                } else if (lookahead == '*') {
                    // 多行注释
                    // 多读一个* 避免/*/通过
                    it.next();
                    boolean valid = false;
                    while (it.hasNext()) {
                        char p = it.next();
                        if (p == '*' && it.peek() == '/') {
                            it.next();
                            valid = true;
                            break;
                        }
                    }
                    if (!valid) {
                        // 注释格式不正确
                        throw new LexicalException("comments not match");
                    }
                    continue;
                }
            }

            // 括号
            if (c == '{' || c == '}' || c == '(' || c == ')') {
                tokenList.add(new Token(TokenType.BRACKET, c + ""));
                continue;
            }

            // 字符串
            if (c == '"' || c == '\'') {
                it.putBack();
                tokenList.add(Token.makeString(it));
                continue;
            }

            // 变量或关键字(非数字开头)
            if (AlphabetHelper.isLetter(c)) {
                it.putBack();
                tokenList.add(Token.makeVarOrKeyword(it));
                continue;
            }

            // 数字
            if (AlphabetHelper.isNumber(c)) {
                it.putBack();
                tokenList.add(Token.makeNumber(it));
                continue;
            }

            // +/-/.开头，后面紧接数字
            if ((c == '+' || c == '-' || c == '.') && AlphabetHelper.isNumber(lookahead)) {
                Token lastToken = tokenList.size() == 0 ? null : tokenList.get(tokenList.size() - 1);

                if (lastToken == null || !lastToken.isValue() || lastToken.isOperator()) {
                    it.putBack();
                    tokenList.add(Token.makeNumber(it));
                    continue;
                }
            }

            // 操作符
            if (AlphabetHelper.isOperator(c)) {
                it.putBack();
                tokenList.add(Token.makeOp(it));
                continue;
            }

            throw new LexicalException("该字符无法判断:" + c);
        }// end while
        return tokenList;
    }

    public List<Token> scanner(Stream source) throws LexicalException {
        var it = new PeekIterator<Character>(source, (char) 0);
        return scanner(it);
    }

    /**
     * 从源代码文件加载并解析
     * @param src
     * @return
     * @throws Exception
     */
    public static List<Token> fromFile(String src) throws Exception {
        var file = new File(src);
        var fileStream = new FileInputStream(file);
        var inputStreamReader = new InputStreamReader(fileStream, "UTF-8");

        var br = new BufferedReader(inputStreamReader);

        // 利用bufferedReader每次读取一行
        var it = new Iterator<Character>() {
            private String line = null;
            private int cursor = 0;

            private void readLine() throws IOException {
                if (line == null || cursor == line.length()) {
                    line = br.readLine();
                    cursor = 0;
                }
            }

            @Override
            public boolean hasNext() {
                try {
                    readLine();
                    return line != null;
                } catch (IOException e) {
                    return false;
                }
            }

            @Override
            public Character next() {
                try {
                    readLine();
                    return line != null ? line.charAt(cursor++) : null;
                } catch (IOException e) {
                    return null;
                }
            }
        };

        var peekIt = new PeekIterator<Character>(it, '\0');

        var lexer = new Lexer();
        return lexer.scanner(peekIt);
    }
}
