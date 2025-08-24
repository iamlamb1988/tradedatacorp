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
        final String polygonIOresponse = "{\"ticker\":\"X:BTCUSD\",\"queryCount\":3,\"resultsCount\":3,\"adjusted\":true,\"results\":[{\"v\":12.090991929999998,\"vw\":93418.9561,\"o\":93405.94,\"c\":93383.35,\"h\":93817,\"l\":93350.2,\"t\":1735689480000,\"n\":507},{\"v\":20.81521069999994,\"vw\":93382.3019,\"o\":93385.6,\"c\":93354.22,\"h\":93798,\"l\":93324.33,\"t\":1735689540000,\"n\":713},{\"v\":1.9909046399999997,\"vw\":93746.3183,\"o\":93758,\"c\":93781,\"h\":93781,\"l\":93721,\"t\":1735689600000,\"n\":49}],\"status\":\"OK\",\"request_id\":\"496cf875d10fc4df8d4e94150ae54608\",\"count\":3}";

        @Test
        public void testEmpty1(){
            JSON_Object obj = JSON_Parser.parse(empty1);

            assertEquals(1,obj.getKeyCount());
            assertEquals(1,obj.getKeyArray().length);
            String key = "empty";

            JSON_Item childItem = obj.getJSON_Attribute(key);
            assertEquals(childItem.getType(),JSON_Item.OBJECT);

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

            assertEquals(childItem1.getType(),JSON_Item.OBJECT);
            assertEquals(childItem2.getType(),JSON_Item.OBJECT);

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
            assertEquals(childItem1.getType(),JSON_Item.OBJECT);
            assertEquals(childItem2.getType(),JSON_Item.OBJECT);

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
            assertEquals(childItem1.getType(),JSON_Item.OBJECT);
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

    @Nested
    @DisplayName("Simple tests, no tricks yet, deeper recursive levels")
    class BasicDeeperRecursion{
        final String polygonIOresponse = "{\"ticker\":\"X:BTCUSD\",\"queryCount\":3,\"resultsCount\":3,\"adjusted\":true,\"results\":[{\"v\":12.090991929999998,\"vw\":93418.9561,\"o\":93405.94,\"c\":93383.35,\"h\":93817,\"l\":93350.2,\"t\":1735689480000,\"n\":507},{\"v\":20.81521069999994,\"vw\":93382.3019,\"o\":93385.6,\"c\":93354.22,\"h\":93798,\"l\":93324.33,\"t\":1735689540000,\"n\":713},{\"v\":1.9909046399999997,\"vw\":93746.3183,\"o\":93758,\"c\":93781,\"h\":93781,\"l\":93721,\"t\":1735689600000,\"n\":49}],\"status\":\"OK\",\"request_id\":\"496cf875d10fc4df8d4e94150ae54608\",\"count\":3}";

        @Test
        public void testPolygonIOresponse(){
            JSON_Object obj = JSON_Parser.parse(polygonIOresponse);

            // Root-level checks
            assertEquals(8, obj.getKeyCount());
            String keyTicker="ticker";
            String keyQueryCount="queryCount";
            String keyResultsCount="resultsCount";
            String keyAdjusted="adjusted";
            String keyResults="results";
            String keyStatus="status";
            String keyRequestID="request_id";
            String keyCount="count";

            // Check types and values of root keys
            JSON_String ticker = (JSON_String)obj.getJSON_Attribute(keyTicker);
            assertEquals("X:BTCUSD", ticker.getStringValue());

            JSON_Integer queryCount = (JSON_Integer)obj.getJSON_Attribute(keyQueryCount);
            assertEquals(3, queryCount.getIntegerValue());

            JSON_Integer resultsCount = (JSON_Integer)obj.getJSON_Attribute(keyResultsCount);
            assertEquals(3, resultsCount.getIntegerValue());

            JSON_Boolean adjusted = (JSON_Boolean)obj.getJSON_Attribute(keyAdjusted);
            assertTrue(adjusted.getBooleanValue());

            JSON_String status = (JSON_String)obj.getJSON_Attribute(keyStatus);
            assertEquals("OK", status.getStringValue());

            JSON_String requestId = (JSON_String)obj.getJSON_Attribute(keyRequestID);
            assertEquals("496cf875d10fc4df8d4e94150ae54608", requestId.getStringValue());

            JSON_Integer count = (JSON_Integer)obj.getJSON_Attribute(keyCount);
            assertEquals(3, count.getIntegerValue());

            // Check results array
            JSON_Array results = (JSON_Array)obj.getJSON_Attribute(keyResults);
            assertEquals(3, results.getItemCount());

            // Check 1st result object
            JSON_Object firstResult = (JSON_Object)results.getItem(0);
            assertEquals(8, firstResult.getKeyCount());

            JSON_Integer t = (JSON_Integer)firstResult.getJSON_Attribute("t");
            assertEquals(1735689480000L, t.getIntegerValue());

            JSON_Number o = (JSON_Number)firstResult.getJSON_Attribute("o");
            assertEquals(93405.94, o.getDecimalValue(), 0.001);
            JSON_Number h = (JSON_Number)firstResult.getJSON_Attribute("h");
            assertEquals(93817, h.getDecimalValue());
            JSON_Number l = (JSON_Number)firstResult.getJSON_Attribute("l");
            assertEquals(93350.2, l.getDecimalValue(), 0.001);
            JSON_Number c = (JSON_Number)firstResult.getJSON_Attribute("c");
            assertEquals(93383.35, c.getDecimalValue(), 0.001);

            JSON_Number v = (JSON_Number)firstResult.getJSON_Attribute("v");
            assertEquals(12.090991929999998, v.getDecimalValue(), 0.0000001);
            JSON_Number vw = (JSON_Number)firstResult.getJSON_Attribute("vw");
            assertEquals(93418.9561, vw.getDecimalValue(), 0.0001);

            JSON_Integer n = (JSON_Integer)firstResult.getJSON_Attribute("n");
            assertEquals(507, n.getIntegerValue());

            // Check 2nd result object
            JSON_Object secondResult = (JSON_Object) results.getItem(1);
            assertEquals(8, secondResult.getKeyCount());

            t = (JSON_Integer)secondResult.getJSON_Attribute("t");
            assertEquals(1735689540000L, t.getIntegerValue());

            o = (JSON_Number)secondResult.getJSON_Attribute("o");
            assertEquals(93385.6, o.getDecimalValue(), 0.001);
            h = (JSON_Number)secondResult.getJSON_Attribute("h");
            assertEquals(93798, h.getDecimalValue());
            l = (JSON_Number)secondResult.getJSON_Attribute("l");
            assertEquals(93324.33, l.getDecimalValue(), 0.001);
            c = (JSON_Number)secondResult.getJSON_Attribute("c");
            assertEquals(93354.22, c.getDecimalValue(), 0.001);

            v = (JSON_Number)secondResult.getJSON_Attribute("v");
            assertEquals(20.81521069999994, v.getDecimalValue(), 0.0000001);
            vw = (JSON_Number)secondResult.getJSON_Attribute("vw");
            assertEquals(93382.3019, vw.getDecimalValue(), 0.0001);

            n = (JSON_Integer)secondResult.getJSON_Attribute("n");
            assertEquals(713, n.getIntegerValue());

            // Check 3rd result object
            JSON_Object thirdResult = (JSON_Object) results.getItem(2);
            assertEquals(8, thirdResult.getKeyCount());

            t = (JSON_Integer)thirdResult.getJSON_Attribute("t");
            assertEquals(1735689600000L, t.getIntegerValue());

            o = (JSON_Number)thirdResult.getJSON_Attribute("o");
            assertEquals(93758.0, o.getDecimalValue(), 0.001);
            h = (JSON_Number)thirdResult.getJSON_Attribute("h");
            assertEquals(93781, h.getDecimalValue());
            l = (JSON_Number)thirdResult.getJSON_Attribute("l");
            assertEquals(93721, l.getDecimalValue());
            c = (JSON_Number)thirdResult.getJSON_Attribute("c");
            assertEquals(93781, c.getDecimalValue());

            v = (JSON_Number)thirdResult.getJSON_Attribute("v");
            assertEquals(1.9909046399999997, v.getDecimalValue(), 0.0000001);
            vw = (JSON_Number)thirdResult.getJSON_Attribute("vw");
            assertEquals(93746.3183, vw.getDecimalValue(), 0.0001);

            n = (JSON_Integer)thirdResult.getJSON_Attribute("n");
            assertEquals(49, n.getIntegerValue());
        }
    }

}