import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 * Utility class for parsing JSON strings into Jackson JsonNode objects.
 */
public class JsonParserUtil {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Parses a JSON string into a JsonNode object.
     * 
     * @param jsonString The JSON string to parse
     * @return The parsed JsonNode object
     * @throws IOException If the input is not valid JSON
     */
    public static JsonNode parseJson(String jsonString) throws IOException {
        if (jsonString == null || jsonString.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }
        
        return objectMapper.readTree(jsonString);
    }
    
    /**
     * Parses a JSON string into a JsonNode object, returning null on error instead of throwing an exception.
     * 
     * @param jsonString The JSON string to parse
     * @return The parsed JsonNode object or null if parsing fails
     */
    public static JsonNode parseJsonSafely(String jsonString) {
        try {
            return parseJson(jsonString);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Validates if a string is valid JSON.
     * 
     * @param jsonString The JSON string to validate
     * @return true if the string is valid JSON, false otherwise
     */
    public static boolean isValidJson(String jsonString) {
        return parseJsonSafely(jsonString) != null;
    }
    
    /**
     * Example usage of the JsonParserUtil.
     */
    public static void main(String[] args) {
        // Example JSON strings
        String validJson = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";
        String invalidJson = "{name:John,age:30,city:New York}";
        
        // Parse valid JSON
        try {
            JsonNode jsonNode = JsonParserUtil.parseJson(validJson);
            System.out.println("Valid JSON parsed successfully:");
            System.out.println("Name: " + jsonNode.get("name").asText());
            System.out.println("Age: " + jsonNode.get("age").asInt());
            System.out.println("City: " + jsonNode.get("city").asText());
            
            // Validate JSON strings
            System.out.println("\nJSON Validation:");
            System.out.println("Is valid JSON valid? " + JsonParserUtil.isValidJson(validJson));
            System.out.println("Is invalid JSON valid? " + JsonParserUtil.isValidJson(invalidJson));
            
        } catch (IOException e) {
            System.err.println("Error parsing JSON: " + e.getMessage());
        }
    }
}