import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringExtractor {

    public static Pair<Integer, Integer> extractNumbers(String input) {
        Pattern pattern = Pattern.compile("(.+?)_([0-9]+)_([0-9]+)$");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            int firstNumber = Integer.parseInt(matcher.group(2));
            int secondNumber = Integer.parseInt(matcher.group(3));
            return new Pair<>(firstNumber, secondNumber);
        }

        return null; // Or throw an exception if no match is found
    }

    public static void main(String[] args) {
        String input = "id_version_12_2";
        Pair<Integer, Integer> numbers = extractNumbers(input);
        System.out.println(numbers); // Output: (12, 2)
    }
}