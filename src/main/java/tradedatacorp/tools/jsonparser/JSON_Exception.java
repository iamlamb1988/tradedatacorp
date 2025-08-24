/**
 * @author Bruce Lamb
 * @since 30 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

/**
 * Exception thrown to indicate an error occurred during JSON parsing or processing.
 */
public class JSON_Exception extends RuntimeException{
    public JSON_Exception(String message){super(message);}
}