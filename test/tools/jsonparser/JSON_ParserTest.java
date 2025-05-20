package tradedatacorp.tools.jsonparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.Transient;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tradedatacorp.tools.jsonparser.JSON_Parser;
import tradedatacorp.tools.jsonparser.JSON_Item;
import tradedatacorp.tools.jsonparser.JSON_Object;
import tradedatacorp.tools.jsonparser.JSON_Boolean;
import tradedatacorp.tools.jsonparser.JSON_String;
import tradedatacorp.tools.jsonparser.JSON_Integer;
import tradedatacorp.tools.jsonparser.JSON_Decimal;
import tradedatacorp.tools.jsonparser.JSON_Null;

public class JSON_ParserTest{
    @Nested
    @DisplayName("Simple Basic non-recursive Tests")
    class SimpleBasicTest{
        final String empty1 = "{}";
        final String empty2 = "{    }";
        final String pi1 = "{\"pi\":3.1415}";
        final String pi2 = "{\"desc\": \"pi test\"  ,  \"pi\" : 3.14 }";
        final String bool1 = "{ \"is bool\" :true, \"desc\":\"bool test\"}";
        final String int1 = "{\"num\":5,\"isDec\": false}";
        final String null1 = "{\"key\" : null}";

        @Test
        public void testEmpty1(){
            JSON_Object emtpy1Obj = JSON_Parser.parse(empty1);

            assertEquals(0,emtpy1Obj.getKeyCount());
        }

        @Test
        public void testEmpty2(){
            JSON_Object emtpy2Obj = JSON_Parser.parse(empty2);

            assertEquals(0,emtpy2Obj.getKeyCount());
        }

        @Test
        public void testPi1(){
            JSON_Object pi1Obj = JSON_Parser.parse(pi1);

            assertEquals(1,pi1Obj.getKeyCount());
            assertEquals(1,pi1Obj.getKeyArray().length);
            String pi1Key = "pi";

            JSON_Item piJSON_value = pi1Obj.getJSON_Attribute(pi1Key);
            assertEquals(piJSON_value.getType(),JSON_Object.DECIMAL);

            JSON_Decimal piJSON_Doublevalue = (JSON_Decimal)piJSON_value;

            double piValue = piJSON_Doublevalue.getDecimalValue();

            assertEquals(3.1415,piValue,0.00001);
        }

        @Test
        public void testPi2(){
            JSON_Object pi2Obj = JSON_Parser.parse(pi2);

            assertEquals(2,pi2Obj.getKeyCount());
            assertEquals(2,pi2Obj.getKeyArray().length);
            String keyDesc = "desc";
            String keyPi = "pi";

            JSON_Item descJSON = pi2Obj.getJSON_Attribute(keyDesc);
            JSON_Item piJSON_value = pi2Obj.getJSON_Attribute(keyPi);

            assertEquals(descJSON.getType(),JSON_Object.STRING);
            assertEquals(piJSON_value.getType(),JSON_Object.DECIMAL);

            JSON_String piJSON_StringValue = (JSON_String)descJSON;
            JSON_Decimal piJSON_Doublevalue = (JSON_Decimal)piJSON_value;

            String piDesc = piJSON_StringValue.getStringValue();
            double piValue = piJSON_Doublevalue.getDecimalValue();

            assertEquals("pi test",piDesc);
            assertEquals(3.14,piValue,0.001);
        }
    
        @Test
        public void testBool1(){
            JSON_Object bool1Obj = JSON_Parser.parse(bool1);

            assertEquals(2,bool1Obj.getKeyCount());
            assertEquals(2,bool1Obj.getKeyArray().length);
            String keyDesc = "desc";
            String keyBool = "is bool";

            JSON_Item descJSON = bool1Obj.getJSON_Attribute(keyDesc);
            JSON_Item boolJSON_value = bool1Obj.getJSON_Attribute(keyBool);

            assertEquals(descJSON.getType(),JSON_Object.STRING);
            assertEquals(boolJSON_value.getType(),JSON_Object.BOOLEAN);

            JSON_String boolJSON_StringValue = (JSON_String)descJSON;
            JSON_Boolean boolJSON_Boolvalue = (JSON_Boolean)boolJSON_value;

            String boolDesc = boolJSON_StringValue.getStringValue();
            boolean boolValue = boolJSON_Boolvalue.getBooleanValue();

            assertEquals("bool test",boolDesc);
            assertTrue(boolValue);
        }

        @Test
        public void testInt1(){
            JSON_Object int1Obj = JSON_Parser.parse(int1);

            assertEquals(2,int1Obj.getKeyCount());
            assertEquals(2,int1Obj.getKeyArray().length);
            String keyNum = "num";
            String keyBool = "isDec";

            JSON_Item numJSON = int1Obj.getJSON_Attribute(keyNum);
            JSON_Item boolJSON = int1Obj.getJSON_Attribute(keyBool);

            assertEquals(numJSON.getType(),JSON_Object.INTEGER);
            assertEquals(boolJSON.getType(),JSON_Object.BOOLEAN);

            JSON_Integer numJSON_IntValue = (JSON_Integer)numJSON;
            JSON_Boolean boolJSON_BoolValue = (JSON_Boolean)boolJSON;

            long numValue = numJSON_IntValue.getIntegerValue();
            boolean boolValue = boolJSON_BoolValue.getBooleanValue();

            assertEquals(5,numValue);
            assertFalse(boolValue);
        }
    
        @Test
        public void testNull1(){
            JSON_Object obj = JSON_Parser.parse(null1);

            assertEquals(1,obj.getKeyCount());
            assertEquals(1,obj.getKeyArray().length);
            String key = "key";

            JSON_Item JSON_value = obj.getJSON_Attribute(key);
            assertEquals(JSON_value.getType(),JSON_Object.NULL);

            JSON_Null JSON_NullValue = (JSON_Null)JSON_value;
        }
    }

    @Nested
    @DisplayName("Simple nested cases for recursion")
    class BasicNesting{
        final String empty1 = "{\"empty\":{}}";
        final String empty2 = " \"empty\" : {  },\"extra\": {}";

        @Test
        public void testEmpty1(){
            JSON_Object obj = JSON_Parser.parse(empty1);

            assertEquals(1,obj.getKeyCount());
            assertEquals(1,obj.getKeyArray().length);
            String key = "empty";

            JSON_Item childItem = obj.getJSON_Attribute(key);
            assertEquals(childItem.getType(),JSON_Object.OBJECT);

            JSON_Object childObj = (JSON_Object)childItem;

            assertEquals(0,childObj.getKeyCount());
            assertEquals(0,childObj.getKeyArray().length);
        }

        @Test
        public void testEmpty2(){
            JSON_Object obj = JSON_Parser.parse(empty2);

            assertEquals(2,obj.getKeyCount());
            assertEquals(2,obj.getKeyArray().length);
            String key1 = "empty";
            String key2 = "extra";

            JSON_Item childItem1 = obj.getJSON_Attribute(key1);
            JSON_Item childItem2 = obj.getJSON_Attribute(key2);

            assertEquals(childItem1.getType(),JSON_Object.OBJECT);
            assertEquals(childItem2.getType(),JSON_Object.OBJECT);

            JSON_Object childObj1 = (JSON_Object)childItem1;
            JSON_Object childObj2 = (JSON_Object)childItem2;

            assertEquals(0,childObj1.getKeyCount());
            assertEquals(0,childObj2.getKeyArray().length);
        }
    }
}