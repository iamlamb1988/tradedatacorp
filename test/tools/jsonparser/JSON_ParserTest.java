/**
 * @author Bruce Lamb
 * @since 28 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import tradedatacorp.tools.jsonparser.JSON_Parser;
import tradedatacorp.tools.jsonparser.JSON_Item;
import tradedatacorp.tools.jsonparser.JSON_Object;
import tradedatacorp.tools.jsonparser.JSON_Array;
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
        final String empty2= "{ }";
        final String empty3 = "{    }";
        final String pi1 = "{\"pi\":3.1415}";
        final String pi2 = "{\"desc\": \"pi test\"  ,  \"pi\" : 3.14 }";
        final String neg = "{\"neg\": -3}";
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
        public void testEmpty3(){
            JSON_Object emtpy2Obj = JSON_Parser.parse(empty3);

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
        public void testNeg1(){
            JSON_Object obj = JSON_Parser.parse(neg);

            assertEquals(1,obj.getKeyCount());
            assertEquals(1,obj.getKeyArray().length);
            String key = "neg";

            JSON_Item JSON_value = obj.getJSON_Attribute(key);
            assertEquals(JSON_value.getType(),JSON_Object.INTEGER);

            JSON_Integer JSON_IntegerValue = (JSON_Integer)JSON_value;

            long integer = JSON_IntegerValue.getIntegerValue();

            assertEquals(-3,integer);
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
    @DisplayName("Simple nested cases for 1 level of recursion")
    class BasicNesting{
        final String empty1 = "{\"empty\":{}}";
        final String empty2 = "{ \"empty\" : {  },\"extra\": {}}";
        final String empty3 = "{\"empty\":[]}";
        final String empty4 = "{\"L1\":[] ,\"L2\" :[ ], \"L3\": [    ]}";
        final String array1 = "{  \"stuff\" : [1]  }";
        final String array2 = "{  \"stuff\" : [1, -2]  }";
        final String array3 = "{ \"a1 \"    : [ 3, \"two\",null  ,-2.718, false]   }";
        final String object1 = "{ \"o1\"   : { }, \"o2\": { \"co1\":  -777, \"co2\":25.99, \"co3\"  :true}   }";
        final String object2 = "{ \"o1\":{\"co1\":null, \"co2\":\"temp\"}, \"a1\": [true,99]}";

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

        @Test
        public void testEmpty3(){
            JSON_Object obj = JSON_Parser.parse(empty3);

            assertEquals(1,obj.getKeyCount());
            assertEquals(1,obj.getKeyArray().length);
            String key = "empty";

            JSON_Item childItem = obj.getJSON_Attribute(key);
            assertEquals(childItem.getType(),JSON_Object.ARRAY);

            JSON_Array childArr = (JSON_Array)childItem;

            assertEquals(0,childArr.getItemCount());
        }

        @Test
        public void testEmpty4(){
            JSON_Object obj = JSON_Parser.parse(empty4);

            assertEquals(3,obj.getKeyCount());
            assertEquals(3,obj.getKeyArray().length);
            String key1 = "L1";
            String key2 = "L2";
            String key3 = "L3";

            JSON_Item childItem1 = obj.getJSON_Attribute(key1);
            JSON_Item childItem2 = obj.getJSON_Attribute(key2);
            JSON_Item childItem3 = obj.getJSON_Attribute(key3);

            assertEquals(childItem1.getType(),JSON_Object.ARRAY);
            assertEquals(childItem2.getType(),JSON_Object.ARRAY);
            assertEquals(childItem3.getType(),JSON_Object.ARRAY);

            JSON_Array childArr1 = (JSON_Array)childItem1;
            JSON_Array childArr2 = (JSON_Array)childItem2;
            JSON_Array childArr3 = (JSON_Array)childItem3;

            assertEquals(0,childArr1.getItemCount());
            assertEquals(0,childArr2.getItemCount());
            assertEquals(0,childArr3.getItemCount());
        }

        @Test
        public void testArray1(){
            JSON_Object obj = JSON_Parser.parse(array1);

            assertEquals(1,obj.getKeyCount());
            assertEquals(1,obj.getKeyArray().length);
            String key = "stuff";

            JSON_Item childItem = obj.getJSON_Attribute(key);
            assertEquals(childItem.getType(),JSON_Object.ARRAY);

            JSON_Array childArr = (JSON_Array)childItem;

            assertEquals(1,childArr.getItemCount());
        }

        @Test
        public void testArray2(){
            JSON_Object obj = JSON_Parser.parse(array2);

            assertEquals(1,obj.getKeyCount());
            assertEquals(1,obj.getKeyArray().length);
            String key = "stuff";

            JSON_Item childItem = obj.getJSON_Attribute(key);
            assertEquals(childItem.getType(),JSON_Object.ARRAY);

            JSON_Array childArr = (JSON_Array)childItem;

            assertEquals(2,childArr.getItemCount());

            JSON_Integer childInteger = (JSON_Integer)childArr.getItem(0);
            assertEquals(1,childInteger.getIntegerValue());

            childInteger = (JSON_Integer)childArr.getItem(1);
            assertEquals(-2,childInteger.getIntegerValue());
        }

        @Test
        public void testArray3(){
            JSON_Object obj = JSON_Parser.parse(array3);

            assertEquals(1,obj.getKeyCount());
            assertEquals(1,obj.getKeyArray().length);
            String key = "a1 ";

            JSON_Item childItem = obj.getJSON_Attribute(key);
            assertEquals(childItem.getType(),JSON_Object.ARRAY);

            JSON_Array childArr = (JSON_Array)childItem;

            assertEquals(5,childArr.getItemCount());

            JSON_Integer childInteger = (JSON_Integer)childArr.getItem(0);
            JSON_String childString = (JSON_String)childArr.getItem(1);
            JSON_Null childNull = (JSON_Null)childArr.getItem(2);
            JSON_Decimal childDec = (JSON_Decimal)childArr.getItem(3);
            JSON_Boolean childBool = (JSON_Boolean)childArr.getItem(4);

            assertEquals(3,childInteger.getIntegerValue());
            assertEquals("two",childString.getStringValue());
            assertNull(childNull.getValue());
            assertEquals(-2.718,childDec.getDecimalValue());
            assertFalse(childBool.getBooleanValue());
        }

        @Test
        public void testObject1(){
            JSON_Object obj = JSON_Parser.parse(object1);

            assertEquals(2,obj.getKeyCount());
            assertEquals(2,obj.getKeyArray().length);
            String key1 = "o1";
            String key2 = "o2";

            JSON_Item childItem1 = obj.getJSON_Attribute(key1);
            JSON_Item childItem2 = obj.getJSON_Attribute(key2);
            assertEquals(childItem1.getType(),JSON_Object.OBJECT);
            assertEquals(childItem2.getType(),JSON_Object.OBJECT);

            JSON_Object childObj1 = (JSON_Object)childItem1;
            JSON_Object childObj2 = (JSON_Object)childItem2;

            assertEquals(0,childObj1.getKeyCount());
            assertEquals(3,childObj2.getKeyCount());

            String childKey1 = "co1";
            String childKey2 = "co2";
            String childKey3 = "co3";

            JSON_Integer childInteger = (JSON_Integer)childObj2.getJSON_Attribute(childKey1);
            JSON_Decimal childDec = (JSON_Decimal)childObj2.getJSON_Attribute(childKey2);
            JSON_Boolean childBool = (JSON_Boolean)childObj2.getJSON_Attribute(childKey3);

            assertEquals(-777,childInteger.getIntegerValue());
            assertEquals(25.99,childDec.getDecimalValue());
            assertTrue(childBool.getBooleanValue());
        }

        @Test
        public void testObject2(){
            JSON_Object obj = JSON_Parser.parse(object2);

            assertEquals(2,obj.getKeyCount());
            assertEquals(2,obj.getKeyArray().length);
            String key1 = "o1";
            String key2 = "a1";

            JSON_Item childItem1 = obj.getJSON_Attribute(key1);
            JSON_Item childItem2 = obj.getJSON_Attribute(key2);
            assertEquals(childItem1.getType(),JSON_Object.OBJECT);
            assertEquals(childItem2.getType(),JSON_Object.ARRAY);

            JSON_Object childObj1 = (JSON_Object)childItem1;
            JSON_Array childObj2 = (JSON_Array)childItem2;

            assertEquals(2,childObj1.getKeyCount());
            assertEquals(2,childObj2.getItemCount());

            String childKey1 = "co1";
            String childKey2 = "co2";

            JSON_Null child1_Null = (JSON_Null)childObj1.getJSON_Attribute(childKey1);
            JSON_String child1_String = (JSON_String)childObj1.getJSON_Attribute(childKey2);
            JSON_Boolean child2_Bool = (JSON_Boolean)childObj2.getItem(0);
            JSON_Integer child2_Int = (JSON_Integer)childObj2.getItem(1);

            assertNull(child1_Null.getValue());
            assertEquals("temp",child1_String.getStringValue());
            assertTrue(child2_Bool.getBooleanValue());
            assertEquals(99,child2_Int.getIntegerValue());
        }
    }
}