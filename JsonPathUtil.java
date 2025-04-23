import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import java.util.List;
import java.util.Map;

/**
 * A stateless utility class for parsing JSON and extracting values using JsonPath.
 * Optimized to parse JSON once into a DocumentContext for multiple path evaluations.
 */
public final class JsonPathUtil {
    
    // Private constructor to prevent instantiation
    private JsonPathUtil() {
        throw new AssertionError("JsonPathUtil is a utility class and should not be instantiated");
    }
    
    // Thread-local ObjectMapper to ensure thread safety while reusing instances
    private static final ThreadLocal<ObjectMapper> MAPPER_THREAD_LOCAL = ThreadLocal.withInitial(ObjectMapper::new);
    
    // Thread-local Configuration to ensure thread safety while reusing instances
    private static final ThreadLocal<Configuration> CONFIG_THREAD_LOCAL = ThreadLocal.withInitial(() -> {
        ObjectMapper mapper = MAPPER_THREAD_LOCAL.get();
        return Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider())
                .mappingProvider(new JacksonMappingProvider(mapper))
                .options(Option.SUPPRESS_EXCEPTIONS)
                .build();
    });
    
    /**
     * Parses a JSON string into a DocumentContext for efficient path evaluation.
     *
     * @param json The JSON string to parse
     * @return A DocumentContext for the parsed JSON
     * @throws IllegalArgumentException If the JSON string is null or empty
     */
    public static DocumentContext parseJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }
        return JsonPath.using(CONFIG_THREAD_LOCAL.get()).parse(json);
    }
    
    /**
     * Parses a JSON string into a Jackson JsonNode.
     *
     * @param json The JSON string to parse
     * @return The parsed JsonNode
     * @throws JsonProcessingException If the JSON is invalid
     */
    public static JsonNode parseJsonNode(String json) throws JsonProcessingException {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }
        return MAPPER_THREAD_LOCAL.get().readTree(json);
    }
    
    /**
     * Extracts a value from a DocumentContext using JsonPath.
     *
     * @param <T> The expected return type
     * @param context The DocumentContext
     * @param path The JsonPath expression
     * @param returnType The class of the expected return type
     * @return The extracted value or null if not found
     */
    public static <T> T read(DocumentContext context, String path, Class<T> returnType) {
        try {
            return context.read(path, returnType);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Gets a String value from a DocumentContext using JsonPath.
     *
     * @param context The DocumentContext
     * @param path The JsonPath expression
     * @return The extracted String or null if not found
     */
    public static String getString(DocumentContext context, String path) {
        return read(context, path, String.class);
    }
    
    /**
     * Gets an Integer value from a DocumentContext using JsonPath.
     *
     * @param context The DocumentContext
     * @param path The JsonPath expression
     * @return The extracted Integer or null if not found
     */
    public static Integer getInteger(DocumentContext context, String path) {
        return read(context, path, Integer.class);
    }
    
    /**
     * Gets a Double value from a DocumentContext using JsonPath.
     *
     * @param context The DocumentContext
     * @param path The JsonPath expression
     * @return The extracted Double or null if not found
     */
    public static Double getDouble(DocumentContext context, String path) {
        return read(context, path, Double.class);
    }
    
    /**
     * Gets a Boolean value from a DocumentContext using JsonPath.
     *
     * @param context The DocumentContext
     * @param path The JsonPath expression
     * @return The extracted Boolean or null if not found
     */
    public static Boolean getBoolean(DocumentContext context, String path) {
        return read(context, path, Boolean.class);
    }
    
    /**
     * Gets a List from a DocumentContext using JsonPath.
     *
     * @param context The DocumentContext
     * @param path The JsonPath expression
     * @return The extracted List or null if not found
     */
    @SuppressWarnings("unchecked")
    public static List<Object> getList(DocumentContext context, String path) {
        return read(context, path, List.class);
    }
    
    /**
     * Gets a Map from a DocumentContext using JsonPath.
     *
     * @param context The DocumentContext
     * @param path The JsonPath expression
     * @return The extracted Map or null if not found
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getMap(DocumentContext context, String path) {
        return read(context, path, Map.class);
    }
    
    /**
     * Checks if a JSON path exists in the given DocumentContext.
     *
     * @param context The DocumentContext
     * @param path The JsonPath expression
     * @return true if the path exists, false otherwise
     */
    public static boolean pathExists(DocumentContext context, String path) {
        try {
            Object result = context.read(path);
            return result != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Convenience method to extract a value directly from a JSON string.
     * This method is less efficient for multiple extractions from the same JSON.
     *
     * @param <T> The expected return type
     * @param json The JSON string
     * @param path The JsonPath expression
     * @param returnType The class of the expected return type
     * @return The extracted value or null if not found
     */
    public static <T> T extractValue(String json, String path, Class<T> returnType) {
        try {
            DocumentContext context = parseJson(json);
            return read(context, path, returnType);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Cleans up thread-local resources. Should be called when done using the utility
     * in contexts where thread pools are used to prevent memory leaks.
     */
    public static void cleanupThreadLocals() {
        MAPPER_THREAD_LOCAL.remove();
        CONFIG_THREAD_LOCAL.remove();
    }
    
    /**
     * Example usage of JsonPathUtil with DocumentContext for efficient multiple path evaluations.
     */
    public static void main(String[] args) {
        String jsonString = "{\n" +
                "  \"store\": {\n" +
                "    \"book\": [\n" +
                "      {\n" +
                "        \"category\": \"reference\",\n" +
                "        \"author\": \"Nigel Rees\",\n" +
                "        \"title\": \"Sayings of the Century\",\n" +
                "        \"price\": 8.95\n" +
                "      },\n" +
                "      {\n" +
                "        \"category\": \"fiction\",\n" +
                "        \"author\": \"Evelyn Waugh\",\n" +
                "        \"title\": \"Sword of Honour\",\n" +
                "        \"price\": 12.99\n" +
                "      }\n" +
                "    ],\n" +
                "    \"bicycle\": {\n" +
                "      \"color\": \"red\",\n" +
                "      \"price\": 19.95\n" +
                "    }\n" +
                "  },\n" +
                "  \"expensive\": true\n" +
                "}";
                
        try {
            // Parse JSON once into a DocumentContext for multiple path evaluations
            DocumentContext context = JsonPathUtil.parseJson(jsonString);
            
            // Extract multiple values from the same context
            String firstBookTitle = JsonPathUtil.getString(context, "$.store.book[0].title");
            Integer bookCount = JsonPathUtil.read(context, "$.store.book.length()", Integer.class);
            Boolean isExpensive = JsonPathUtil.getBoolean(context, "$.expensive");
            List<Object> allAuthors = JsonPathUtil.getList(context, "$.store.book[*].author");
            Double bicyclePrice = JsonPathUtil.getDouble(context, "$.store.bicycle.price");
            
            // Print the results
            System.out.println("First book title: " + firstBookTitle);
            System.out.println("Number of books: " + bookCount);
            System.out.println("Is expensive: " + isExpensive);
            System.out.println("All authors: " + allAuthors);
            System.out.println("Bicycle price: " + bicyclePrice);
            
            // Check if paths exist
            boolean hasExpensiveFlag = JsonPathUtil.pathExists(context, "$.expensive");
            boolean hasDiscountFlag = JsonPathUtil.pathExists(context, "$.discount");
            System.out.println("Has expensive flag: " + hasExpensiveFlag);
            System.out.println("Has discount flag: " + hasDiscountFlag);
            
            // Alternative approach - extract a value directly (less efficient for multiple extractions)
            String bicycleColor = JsonPathUtil.extractValue(jsonString, "$.store.bicycle.color", String.class);
            System.out.println("Bicycle color: " + bicycleColor);
            
        } catch (Exception e) {
            System.err.println("Error processing JSON: " + e.getMessage());
        } finally {
            // Clean up thread-local resources when done
            JsonPathUtil.cleanupThreadLocals();
        }
    }
}