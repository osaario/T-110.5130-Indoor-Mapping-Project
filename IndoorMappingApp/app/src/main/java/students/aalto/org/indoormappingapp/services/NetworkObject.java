package students.aalto.org.indoormappingapp.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class NetworkObject {

    /**
     * Returns the HTTP request media type.
     */
    public abstract String requestContentType();

    /**
     * Creates an HTTP request body presenting the object.
     */
    public abstract String toRequestBody() throws Exception;

    /**
     * Parses object values from an HTTP response body.
     */
    public abstract void parseResponseBody(String body) throws Exception;

    /**
     * Splits HTTP response body for multiple objects.
     */
    public String[] splitResponseBody(String body) throws Exception {
        String[] parts = { body };
        return parts;
    }
}
