/**
 * @author Bruce Lamb
 * @since 14 JUN 2025
 */
package tradedatacorp.tools.jsonparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.Transient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tradedatacorp.test.java.TestResourceFetcher;
import tradedatacorp.tools.jsonparser.JSON_Parser;
import tradedatacorp.tools.jsonparser.JSON_Item;
import tradedatacorp.tools.jsonparser.JSON_Object;
import tradedatacorp.tools.jsonparser.JSON_Array;
import tradedatacorp.tools.jsonparser.JSON_Boolean;
import tradedatacorp.tools.jsonparser.JSON_String;
import tradedatacorp.tools.jsonparser.JSON_Integer;
import tradedatacorp.tools.jsonparser.JSON_Decimal;
import tradedatacorp.tools.jsonparser.JSON_Null;

public class JSON_Parser2Test{
    TestResourceFetcher jsonFileFetcher;
    public JSON_Parser2Test(){
        jsonFileFetcher = new TestResourceFetcher();
    }

    @Nested
    @DisplayName("Simple JSON File tests")
    class SimpleBasicTest{

        @Test
        public void basictest1Test(){
            String basictest1 = jsonFileFetcher.getTextFileContents("tools/jsonparser/basictest1.json");
            System.out.println("DEBUG basictest1.json contents: "+basictest1+"\nEND DEBUG");
        }
    }
}