import java.util.regex.Pattern;

/**
 * 字符类型的正则模板
 */
public class AlphabetHelper {
    // 字母类型
    static Pattern ptnLetter = Pattern.compile("^[a-zA-Z]$");
    // 数字类型
    static Pattern ptnNumber = Pattern.compile("^[0-9]$");
    // 文字
    static Pattern ptnLiteral = Pattern.compile("^[_a-zA-Z0-9]$");
    // 运算符 基本运算符 不全
    static Pattern ptnOperator = Pattern.compile("^[+\\-*/<>=!&|^%,]$");

    public static boolean isLetter(char c) {
        // Matcher，一个Matcher对象是一个状态机器，它依据Pattern对象做为匹配模式对字符串展开匹配检查。
        // matches()对整个字符串进行匹配,只有整个字符串都匹配了才返回true
        return ptnLetter.matcher(c + "").matches();
    }

    public static boolean isNumber(char c) {
        return ptnNumber.matcher(c+"").matches();
    }

    public static boolean isLiteral(char c) {
        return ptnLiteral.matcher(c + "").matches();
    }

    public static boolean isOperator(char c) {
        return ptnOperator.matcher(c + "").matches();
    }
}
