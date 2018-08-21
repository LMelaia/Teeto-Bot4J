package net.lmelaia.teeto;

/**
 * Represents a response stored in the responses.properties
 * file. This class allows replacing of placeholders
 * before retrieving the response.
 */
public class Response {

    /**
     * The response text.
     */
    private String responseText;

    /**
     * Constructs a new response.
     *
     * @param templateResponse the response text as stored in the responses file.
     */
    Response(String templateResponse){
        this.responseText = templateResponse;
    }

    /**
     * Replaces a set placeholder in the response with the given String.
     * This works like {@link String#replace(char, char)}.
     *
     * @param placeholder the placeholder
     * @param replacement the replacement.
     * @return {@code this}.
     */
    public Response setPlaceholder(String placeholder, String replacement){
        this.responseText = responseText.replace(placeholder, replacement);
        return this;
    }

    /**
     * @return the response with all placeholders set.
     */
    public String get(){
        return responseText;
    }
}
