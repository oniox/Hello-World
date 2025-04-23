import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A utility class that extends JSON parsing capabilities by adding JSON Path support.
 * This class uses JsonParserUtil for the basic parsing and adds the ability to query
 * the parsed JSON using JSON Path expressions.
 */
public class JsonPathExtractor {

    private final ObjectMapper objectMapper;
    private final Configuration jsonPathConfig;
    
    /**
     * Constructs a new JsonPathExtractor with default configuration.
     */
    public JsonPathExtractor() {
        objectMapper = new ObjectMapper();
        jsonPathConfig = Configuration.builder()
                .jsonProvider(new JacksonJsonNodeJsonProvider())
                .mappingProvider(new JacksonMappingProvider(objectMapper))
                .build();
    }
    
    /**
     * Parses a JSON string and returns the root JsonNode.
     * 
     * @param jsonString The JSON string to parse
     * @return The parsed JsonNode
     * @throws IOException If parsing fails
     */
    public JsonNode parse(String jsonString) throws IOException {
        return JsonParserUtil.parseJson(jsonString);
    }
    
    /**
     * Extracts a value from a JsonNode using a JSON path expression.
     * 
     * @param <T> The expected return type
     * @param node The JsonNode to extract from
     * @param jsonPath The JSON path expression
     * @param returnType The expected return type class
     * @return The extracted value
     */
    public <T> T extractValue(JsonNode node, String jsonPath, Class<T> returnType) {
        String jsonString = node.toString();
        return JsonPath.using(jsonPathConfig)
                .parse(jsonString)
                .read(jsonPath, returnType);
    }
    
    /**
     * Extracts a value from a JSON string using a JSON path expression.
     * This is a convenience method that combines parsing and extraction.
     * 
     * @param <T> The expected return type
     * @param jsonString The JSON string to parse and extract from
     * @param jsonPath The JSON path expression
     * @param returnType The expected return type class
     * @return The extracted value
     * @throws IOException If parsing fails
     */
    public <T> T extractValue(String jsonString, String jsonPath, Class<T> returnType) throws IOException {
        JsonNode node = parse(jsonString);
        return extractValue(node, jsonPath, returnType);
    }
    
    /**
     * Gets a string value from a JsonNode using a JSON path.
     * 
     * @param node The JsonNode to extract from
     * @param jsonPath The JSON path expression
     * @return The extracted string value or null if not found
     */
    public String getString(JsonNode node, String jsonPath) {
        try {
            return extractValue(node, jsonPath, String.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Gets an integer value from a JsonNode using a JSON path.
     * 
     * @param node The JsonNode to extract from
     * @param jsonPath The JSON path expression
     * @return The extracted integer value or null if not found
     */
    public Integer getInteger(JsonNode node, String jsonPath) {
        try {
            return extractValue(node, jsonPath, Integer.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Gets a boolean value from a JsonNode using a JSON path.
     * 
     * @param node The JsonNode to extract from
     * @param jsonPath The JSON path expression
     * @return The extracted boolean value or null if not found
     */
    public Boolean getBoolean(JsonNode node, String jsonPath) {
        try {
            return extractValue(node, jsonPath, Boolean.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Gets a list of values from a JsonNode using a JSON path.
     * 
     * @param <T> The expected element type of the list
     * @param node The JsonNode to extract from
     * @param jsonPath The JSON path expression
     * @param elementType The class of the list elements
     * @return The extracted list or null if not found
     */
    public <T> List<T> getList(JsonNode node, String jsonPath, Class<T> elementType) {
        try {
            return JsonPath.using(jsonPathConfig)
                    .parse(node.toString())
                    .read(jsonPath, List.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Example usage of the JsonPathExtractor.
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
                
        JsonPathExtractor extractor = new JsonPathExtractor();
        
        try {
            // Parse the JSON string
            JsonNode rootNode = extractor.parse(jsonString);
            
            // Extract values using JSON path
            String firstBookTitle = extractor.getString(rootNode, "$.store.book[0].title");
            Integer bookCount = extractor.extractValue(rootNode, "$.store.book.length()", Integer.class);
            Boolean isExpensive = extractor.getBoolean(rootNode, "$.expensive");
            List<String> allAuthors = extractor.getList(rootNode, "$.store.book[*].author", String.class);
            Double bicyclePrice = extractor.extractValue(rootNode, "$.store.bicycle.price", Double.class);
            
            // Print the results
            System.out.println("First book title: " + firstBookTitle);
            System.out.println("Number of books: " + bookCount);
            System.out.println("Is expensive: " + isExpensive);
            System.out.println("All authors: " + allAuthors);
            System.out.println("Bicycle price: " + bicyclePrice);
            
            // Extract a value directly from the JSON string
            String bicycleColor = extractor.extractValue(jsonString, "$.store.bicycle.color", String.class);
            System.out.println("Bicycle color: " + bicycleColor);
            
        } catch (IOException e) {
            System.err.println("Error processing JSON: " + e.getMessage());
        }
    }
}