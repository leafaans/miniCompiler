import exception.LexicalException;

public class Token {
    TokenType _type;
    // value 是否需要改为object
//     String _value;
    Object _value;

    public Token(TokenType type, Object value) {
        this._type = type;
        this._value = value;
    }

    public TokenType getType() {
        return _type;
    }

    public Object getValue() {
        return _value;
    }

    @Override
    public String toString() {
        return String.format("type %s, value %s", _type, _value);
    }

    /**
     * 判断词素(lexeme)是否是变量
     *
     * @return
     */
    public boolean isVariable() {
        return _type == TokenType.VARIABLE;
    }

    /**
     * 值类型
     *
     * @return
     */
    public boolean isScalar() {
        return _type == TokenType.INTEGER || _type == TokenType.FLOAT
                || _type == TokenType.STRING || _type == TokenType.BOOLEAN;
    }

    public boolean isNumber() {
        return this._type == TokenType.INTEGER || this._type == TokenType.FLOAT;
    }

    public boolean isOperator() {
        return this._type == TokenType.OPERATOR;
    }

    // 一元表达式
    public boolean isPostUnaryOperator() {
        return this._value.equals("++") || this._value.equals("--");
    }


    /**
     * 提取变量或关键字
     *
     * @param it
     * @return
     */
    public static Token makeVarOrKeyword(PeekIterator<Character> it) {
        String word = "";
        // 不停地判断下一个字符是否是表面字符集isLiteral
        while (it.hasNext()) {
            var lookahead = it.peek();
            if (AlphabetHelper.isLiteral(lookahead)) {
                word += lookahead;
            } else {
                break;
            }
            it.next();
        }

        // 判断关键字 或 变量
        if (Keywords.isKeyword(word)) {
            if (word.equals("true") || word.equals("false")) {
                return new Token(TokenType.BOOLEAN, word);
            }
            return new Token(TokenType.KEYWORD, word);
        }

        return new Token(TokenType.VARIABLE, word);

    }

    public static Token makeString(PeekIterator<Character> it) throws LexicalException {
        String word = "";
        int state = 0;

        // 判断字符串的有限状态机
        while (it.hasNext()) {
            char c = it.next();
            switch (state) {
                case 0:
                    if (c == '"') {
                        // 以双引号开始 -> 进入状态1
                        state = 1;
                    } else if (c == '\'') {
                        // 以单引号开始 -> 进入状态2
                        state = 2;
                    } else {
                        throw new LexicalException(c);
                    }
                    word += c;
                    break;
                case 1:
                    if (c == '"') {
                        // 以双引号结束
                        return new Token(TokenType.STRING, word + c);
                    } else {
                        word += c;
                    }
                    break;

                case 2:
                    if (c == '\'') {
                        // 以单引号结束
                        return new Token(TokenType.STRING, word + c);
                    } else {
                        word += c;
                    }
                    break;
            }
        } // end while
        // 如果没有正确返回, 则说明出现了异常
        throw new LexicalException("makeString Unexpected error");
    }


    /**
     * 提取操作符
     *
     * @param it
     * @return
     * @throws LexicalException
     */
    public static Token makeOp(PeekIterator<Character> it) throws LexicalException {
        int state = 0;

        while (it.hasNext()) {
            var lookahead = it.next();

            switch (state) {
                case 0:
                    switch (lookahead) {
                        case '+':
                            state = 1;
                            break;
                        case '-':
                            state = 2;
                            break;
                        case '*':
                            state = 3;
                            break;
                        case '/':
                            state = 4;
                            break;
                        case '>':
                            state = 5;
                            break;
                        case '<':
                            state = 6;
                            break;
                        case '=':
                            state = 7;
                            break;
                        case '!':
                            state = 8;
                            break;
                        case '&':
                            state = 9;
                            break;
                        case '|':
                            state = 10;
                            break;
                        case '^':
                            state = 11;
                            break;
                        case '%':
                            state = 12;
                            break;
                        case ',':
                            return new Token(TokenType.OPERATOR, ',');
                        case ';':
                            return new Token(TokenType.OPERATOR, ';');
                    }
                    break;
                case 1:
                    // +开头的操作符
                    // 进入非0状态，说明多读取了一个字符
                    if (lookahead == '+') {
                        return new Token(TokenType.OPERATOR, "++");
                    } else if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "+=");
                    } else {
                        // 预读取的字符需要放回
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "+");
                    }
                case 2:
                    // -号开头的操作符
                    if (lookahead == '-') {
                        return new Token(TokenType.OPERATOR, "--");
                    } else if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "-=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "-");
                    }
                case 3:
                    // *号开头的操作符
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "*=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "*");
                    }
                case 4:
                    // /号开头的操作符
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "/=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "/");
                    }
                case 5:
                    // >号开头的操作符
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, ">=");
                    } else if (lookahead == '>') {
                        return new Token(TokenType.OPERATOR, ">>");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, ">");
                    }
                case 6:
                    // <号开头的操作符
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "<=");
                    } else if (lookahead == '<') {
                        return new Token(TokenType.OPERATOR, "<<");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "<");
                    }
                case 7:
                    // =号开头的操作符
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "==");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "=");
                    }
                case 8:
                    // !开头的操作符
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "!=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "!");
                    }
                case 9:
                    // &开头的操作符
                    if (lookahead == '&') {
                        return new Token(TokenType.OPERATOR, "&&");
                    } else if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "&=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "&");
                    }
                case 10:
                    // |开头的操作符
                    if (lookahead == '|') {
                        return new Token(TokenType.OPERATOR, "||");
                    } else if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "|=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "|");
                    }
                case 11:
                    // ^开头的操作符
                    if (lookahead == '^') {
                        return new Token(TokenType.OPERATOR, "^^");
                    } else if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "^=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "^");
                    }
                case 12:
                    // %开头的操作符
                    if (lookahead == '=') {
                        return new Token(TokenType.OPERATOR, "%=");
                    } else {
                        it.putBack();
                        return new Token(TokenType.OPERATOR, "%");
                    }
            }
        } // end while

        throw new LexicalException("Unexpected error");
    }

    /**
     * TODO 提取数字类型
     *
     * @param it
     * @return
     * @throws LexicalException
     */
    public static Token makeNumber(PeekIterator<Character> it) throws LexicalException {
        String s = "";
        int state = 0;

        while (it.hasNext()) {
            var lookahead = it.peek();

            switch (state) {
                case 0:
                    if (lookahead == '0') {
                        state = 1;
                    } else if (AlphabetHelper.isNumber(lookahead)) {
                        state = 2;
                    } else if (lookahead == '+' || lookahead == '-') {
                        // 显示的正负数
                        state = 3;
                    } else if (lookahead == '.') {
                        // 小数点
                        state = 5;
                    }
                    break;
                case 1:
                    // 以0开头的数字
                    if (lookahead == '0') {
                        state = 1;
                    } else if (AlphabetHelper.isNumber(lookahead)) {
                        state = 2;
                    } else if (lookahead == '.') {
                        state = 4;
                    } else {
                        return new Token(TokenType.INTEGER, s);
                    }
                    break;
                case 2:
                    // [1-9]开头的数字
                    if (AlphabetHelper.isNumber(lookahead)) {
                        state = 2;
                    } else if (lookahead == '.') {
                        state = 4;
                    } else {
                        return new Token(TokenType.INTEGER, s);
                    }
                    break;
                case 3:
                    // 显示的正负数(+/-开头)
                    if (AlphabetHelper.isNumber(lookahead)) {
                        state = 2;
                    } else if (lookahead == '.') {
                        state = 5;
                    } else {
                        throw new LexicalException("+/-后应该是数字类型：" + lookahead);
                    }
                    break;
                case 4:
                    // 浮点数：以数字类型开头的数字，且中间带小数点的
                    if (lookahead == '.') {
                        throw new LexicalException("浮点型数字类型，.后应该为数字类型：" + lookahead);
                    } else if (AlphabetHelper.isNumber(lookahead)) {
                        state = 20;
                    } else {
                        return new Token(TokenType.FLOAT, s);
                    }
                case 5:
                    // .开头的数字类型
                    if (AlphabetHelper.isNumber(lookahead)) {
                        state = 20;
                    } else {
                        throw new LexicalException("浮点型数字类型，以.开头的数字，.后应该为数字类型：" + lookahead);
                    }
                    break;
                case 20:
                    if (AlphabetHelper.isNumber(lookahead)) {
                        state = 20;
                    } else if (lookahead == '.') {
                        throw new LexicalException("数字中已有一个.，不应该再出现. :" + s + ":" + lookahead);
                    } else {
                        return new Token(TokenType.FLOAT, s);
                    }
                    break;
            }

            it.next();
            s += lookahead;

        } // end while

        throw new LexicalException("Unexpected error");
    }

    /**
     * 判断是否变量或值类型
     * @return
     */
    public boolean isValue() {
        return isVariable() || isScalar();
    }


}
