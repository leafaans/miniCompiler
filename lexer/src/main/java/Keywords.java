import java.util.Arrays;
import java.util.HashSet;

/**
 * 只实现简单的核心关键字，后续可扩展
 */
public class Keywords {

    static String[] keywords = {
            "var",
            "int",
            "float",
            "bool",
            "void",
            "string",
            "true",
            "false",
            "if",
            "else",
            "for",
            "while",
            "break",
            "func",
            "return"
    };

    static HashSet<String> set = new HashSet<>(Arrays.asList(keywords));

    public static boolean isKeyword(String word) {
        return set.contains(word);
    }
}
