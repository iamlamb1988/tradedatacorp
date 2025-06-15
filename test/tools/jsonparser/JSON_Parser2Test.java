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
            JSON_Object obj = JSON_Parser.parse(basictest1);

            assertEquals(3,obj.getKeyCount());
            String key1 = "name";
            String key2 = "isCool";
            String key3 = "stuff";

            JSON_String val1 = (JSON_String)obj.getJSON_Attribute(key1);
            JSON_Boolean val2 = (JSON_Boolean)obj.getJSON_Attribute(key2);
            JSON_Array val3 = (JSON_Array)obj.getJSON_Attribute(key3);

            assertEquals("test",val1.getStringValue());
            assertEquals(true,val2.getBooleanValue());

            assertEquals(3,val3.getItemCount());

            JSON_Integer child0 = (JSON_Integer)val3.getItem(0);
            JSON_Decimal child1 = (JSON_Decimal)val3.getItem(1);
            JSON_String child2 = (JSON_String)val3.getItem(2);

            assertEquals(1,child0.getIntegerValue());
            assertEquals(2.718,child1.getDecimalValue());
            assertEquals("3.14",child2.getStringValue());
        }

        @Test
        public void deeptest1Test(){
            String deeptest1 = jsonFileFetcher.getTextFileContents("tools/jsonparser/deeptest1.json");
            JSON_Object rootObj = JSON_Parser.parse(deeptest1);
            assertEquals(2,rootObj.getKeyCount());

            JSON_String note = (JSON_String)rootObj.getJSON_Attribute("note");
            assertEquals("This is the root node",note.getStringValue());

            JSON_Object childObj = (JSON_Object)rootObj.getJSON_Attribute("child");
            assertEquals(1,childObj.getKeyCount());

            JSON_Object grandChilObj = (JSON_Object)childObj.getJSON_Attribute("grandchild");
            assertEquals(1,grandChilObj.getKeyCount());

            JSON_Object greatGrandChildObj = (JSON_Object)grandChilObj.getJSON_Attribute("greatgranddhild");
            assertEquals(1,greatGrandChildObj.getKeyCount());

            note = (JSON_String)greatGrandChildObj.getJSON_Attribute("note");
            assertEquals("This is the final leaf node.",note.getStringValue());
        }
    }
}