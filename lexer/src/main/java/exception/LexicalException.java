package exception;

public class LexicalException extends Exception {

    private String msg;

    public LexicalException(char c) {
        msg = String.format("Unexpected character %c", c);
    }

    public LexicalException(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }
}
