/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

import java.util.Collections;
import java.util.Stack;

import tradedatacorp.tools.jsonparser.JSON_Exception;
import tradedatacorp.tools.jsonparser.JSON_Object;

import java.util.ArrayList;

public final class JSON_Parser{

    public static final String ESCAPES;
    public static final String RAW_ESCAPES;
    private static final String[] VALID_UNQUOTED_VALUES;

    static{
        char[] escapes = {'"','\\','/','b','f','n','r','t'};
        char[] rawEscapes = {'"','\\','/',8,12,10,13,9};
        ESCAPES = String.copyValueOf(escapes);
        RAW_ESCAPES = String.copyValueOf(rawEscapes);

        VALID_UNQUOTED_VALUES = new String[3];

        VALID_UNQUOTED_VALUES[0]="true";
        VALID_UNQUOTED_VALUES[1]="false";
        VALID_UNQUOTED_VALUES[2]="null";
    }

    public static JSON_Object parse(String jsonString, String attributeName){
        int controlIndex = 0;
        int nextID = 0;
        int rootOpenBraceIndex;
        int LENGTH = jsonString.length();

        //1. find root brace
        rootOpenBraceIndex=jsonString.indexOf('{');
        if(rootOpenBraceIndex == -1) return null; //not valid JSON

        Stack<JSON_Token> tokenStack = new Stack<JSON_Token>(); //tmp used to easily pair tokens
        ArrayList<JSON_Token> tokenList = new ArrayList<>(); //preserve ordering
        JSON_Token tmpToken1 = new JSON_OpenBrace(nextID, rootOpenBraceIndex);
        JSON_Token tmpToken2;

        tokenStack.push(tmpToken1);
        tmpToken1.arrayIndex=tokenList.size();
        tokenList.add(tmpToken1);
        ++nextID;
        controlIndex=rootOpenBraceIndex+1;

        while(controlIndex < LENGTH){
            switch(jsonString.charAt(controlIndex)){
                case '{':
                tmpToken1 = new JSON_OpenBrace(nextID, controlIndex);
                    tokenStack.push(tmpToken1);
                    tmpToken1.arrayIndex=tokenList.size();
                    tokenList.add(tmpToken1);
                    ++nextID;
                    break;
                case '}':
                    tmpToken1 = new JSON_CloseBrace(tokenStack.peek().pairID, controlIndex);
                    tmpToken1.arrayIndex=tokenList.size();
                    tmpToken2 = tokenStack.peek();
                    tokenList.add(tmpToken1);

                    tmpToken1.partnerArrayIndex=tmpToken2.arrayIndex;
                    tmpToken2.partnerArrayIndex=tmpToken1.arrayIndex;
                    tmpToken1.partnerStrIndex=tmpToken2.partnerStrIndex;
                    tmpToken2.partnerStrIndex=tmpToken1.partnerStrIndex;
                    tokenStack.pop();
                    break;
                case '[':
                    tmpToken1 = new JSON_OpenBracket(nextID, controlIndex);
                    tokenStack.push(tmpToken1);
                    tmpToken1.arrayIndex=tokenList.size();
                    tokenList.add(tmpToken1);
                    ++nextID;
                    break;
                case ']':
                    tmpToken1 = new JSON_CloseBracket(tokenStack.peek().pairID, controlIndex);
                    tmpToken1.arrayIndex=tokenList.size();
                    tmpToken2 = tokenStack.peek();
                    tokenList.add(tmpToken1);

                    tmpToken1.partnerArrayIndex=tmpToken2.arrayIndex;
                    tmpToken2.partnerArrayIndex=tmpToken1.arrayIndex;
                    tmpToken1.partnerStrIndex=tmpToken2.partnerStrIndex;
                    tmpToken2.partnerStrIndex=tmpToken1.partnerStrIndex;
                    tokenStack.pop();
                    break;            
            }
            ++controlIndex;
        }

        //Check Validation
        if(!tokenStack.empty() && tokenList.size()%2 != 0) return null;
        if(tokenList.get(0).pairID != tokenList.get(tokenList.size()-1).pairID) return null;

        return (JSON_Object)parseJSON_ObjectTokens(jsonString, tokenList, 0);
    }

    public static JSON_Object parse(String jsonString){return parse(jsonString,"root");}

    private static JSON_Object parseJSON_ObjectTokens(String jsonString, ArrayList<JSON_Token> tokenArray, int arrayIDindex) throws JSON_Exception{
        //0. Initialize token
        JSON_Object r = new JSON_Object(); //return value
        JSON_Token tmpToken1 = tokenArray.get(arrayIDindex);
        JSON_Token tmpToken2;
        JSON_Token openToken;
        JSON_Token closeToken;
        int nextTokenIndex; //if any
        boolean hasNextAttribute;

        if(tmpToken1.isOpen()){
            openToken=tmpToken1;
            closeToken=tokenArray.get(openToken.partnerArrayIndex);
        }else{
            closeToken=tmpToken1;
            openToken=tokenArray.get(closeToken.partnerArrayIndex);
        }

        nextTokenIndex=openToken.arrayIndex+1;

        //1. get attribute : value pairs (all attribute keys are in " ")
        //1.0 set temp variables for isolating key value pair in attributes
        int startBodyIndex = openToken.strIndex+1;
        int endBodyIndex = closeToken.strIndex-1;
        int attrStartIndex;
        int attrEndIndex;
        int keyStartIndex;
        int keyEndIndex;
        int valueStartIndex;
        int valueEndIndex;
        boolean isColonSeparated;

        char nextChar = 0;
        String keyString;
        String valueString;

        String[] tmpStringArr = new String[2];
        int controlIndex=startBodyIndex;

        if(controlIndex >= endBodyIndex){return r;}// case 1: {} case 2 { }

        while(controlIndex < endBodyIndex){
            //1. obtain key value
            controlIndex = obtainJSON_StringParse(tmpStringArr,jsonString,controlIndex)+1;
            if(controlIndex == -1) return r; // -1 => empty string => OK
            else if(controlIndex < -1) throw new JSON_Exception("Error obtaining attribute"); //some invalid error
            keyString = tmpStringArr[0];

            // 2 find separating ':' to begin parsing corresponding value
            isColonSeparated=false; //marker to indicate colon is not yet found to corresponding the value for key.
            for(int i=controlIndex; i<endBodyIndex; ++i){
                nextChar = jsonString.charAt(i);
                if(nextChar == ':'){
                    isColonSeparated=true;
                    controlIndex=i+1;
                    break;
                }else if(!Character.isWhitespace(nextChar)) return null;
            }
            if(!isColonSeparated) throw new JSON_Exception("Error no colon found after attribute: "+keyString); //Missing colon, invalid JSON body format.

            //3 Find value
            //3.1 Find first valid character
            valueStartIndex = -1;
            while(controlIndex <= endBodyIndex){
                nextChar=jsonString.charAt(controlIndex);
                if(!Character.isWhitespace(nextChar)){
                    valueStartIndex=controlIndex;
                    break;
                }
                ++controlIndex;
            }
            if(valueStartIndex == -1) throw new JSON_Exception("Error empty value not acceptable at: "+keyString); //Missing colon, invalid JSON body format.; //value cannot be empty

            //3.2 Determine if type is primitive base (String, Boolean, Number, Null) or Compound (Object, Array)
            if(nextChar == '"'){
                controlIndex = obtainJSON_StringParse(tmpStringArr,jsonString,valueStartIndex)+1;
                valueString = tmpStringArr[0];
                r.addJSON_Attribute(keyString, new JSON_String(valueString));
            }else if(jsonString.startsWith("true",controlIndex)){
                controlIndex += 4; //length of "true"
                r.addJSON_Attribute(keyString, new JSON_Boolean(true));
            }else if(jsonString.startsWith("false",controlIndex)){
                controlIndex += 5; //length of "false"
                r.addJSON_Attribute(keyString, new JSON_Boolean(false));
            }else if(jsonString.startsWith("null",controlIndex)){
                controlIndex += 4; //length of "null"
                r.addJSON_Attribute(keyString, new JSON_Null());
            }else if(Character.isDigit(nextChar)){
                boolean isDecimal = false; //marker until first (and only) '.' is found
                valueEndIndex = -1;
                if(controlIndex == closeToken.strIndex){ //special case, at very end of object
                    valueEndIndex=valueStartIndex;
                }
                while(controlIndex <= closeToken.strIndex){
                    nextChar=jsonString.charAt(controlIndex);
                    if(Character.isDigit(nextChar)){++controlIndex;}
                    else if(nextChar == '.'){
                        if(!isDecimal) isDecimal=true;
                        else return null;
                        ++controlIndex;
                    }else if(Character.isWhitespace(nextChar)){
                        valueEndIndex=controlIndex-1;
                        ++controlIndex;
                        break;
                    }else if(nextChar == ','){
                        hasNextAttribute=true;
                        valueEndIndex=controlIndex-1;
                        ++controlIndex;
                        break;
                    }else if(nextChar == '}' || nextChar == ']'){
                        valueEndIndex=controlIndex-1;
                        ++controlIndex;
                        break;
                    }
                    else throw new JSON_Exception("Error with Number value, invalid character."); //invalid character received for a Decimal
                }

                if(valueEndIndex == -1) throw new JSON_Exception("Error with Number value.");

                valueString=jsonString.substring(valueStartIndex,valueEndIndex+1);

                JSON_Number tmpNumber;
                if(isDecimal) tmpNumber = new JSON_Decimal(Double.parseDouble(valueString));
                else tmpNumber = new JSON_Integer(Long.parseLong(valueString));

                r.addJSON_Attribute(keyString, tmpNumber);
                controlIndex = valueEndIndex + 1;; //length of "value"
            }else if(nextChar == '{'){
                tmpToken1 = tmpToken2 = null;
                for(int i=openToken.arrayIndex + 1; i<closeToken.arrayIndex; ++i){
                    tmpToken1=tokenArray.get(i);
                    if(tmpToken1.strIndex == controlIndex){
                        tmpToken2 = tokenArray.get(tmpToken1.partnerArrayIndex);
                        valueEndIndex=tmpToken2.strIndex;
                        controlIndex=valueEndIndex+1;
                        break;
                    }
                }
                
                System.out.println("DEBUG: Handle object recursively");
                r.addJSON_Attribute(keyString, parseJSON_ObjectTokens(jsonString, tokenArray, nextTokenIndex));
            }else if(nextChar == '['){
                tmpToken1 = tmpToken2 = null;
                for(int i=openToken.arrayIndex + 1; i<closeToken.arrayIndex; ++i){
                    tmpToken1=tokenArray.get(i);
                    if(tmpToken1.strIndex == controlIndex){
                        tmpToken2 = tokenArray.get(tmpToken1.partnerArrayIndex);
                        valueEndIndex=tmpToken2.strIndex;
                        controlIndex=valueEndIndex+1;
                        break;
                    }
                }
                
                System.out.println("DEBUG: Handle array recursively");
                r.addJSON_Attribute(keyString, parseJSON_ArrayTokens(jsonString, tokenArray, nextTokenIndex));
            }else throw new JSON_Exception("Error: Invalid start value");; //invalid value

            // 4. Search for a comma to begin to obtain next attribute
            hasNextAttribute = false;
            while(controlIndex <= endBodyIndex){ //If no comma, main loop will be false
                nextChar = jsonString.charAt(controlIndex);
                if(nextChar == ','){
                    hasNextAttribute=true;
                    ++controlIndex;
                    break;
                }else if(!Character.isWhitespace(nextChar)) return null;
                ++controlIndex;
            }

            if(!hasNextAttribute) return r; //no more valid attributes, this object is valid.
        }

        return r;
    }

    private static JSON_Array parseJSON_ArrayTokens(String jsonString, ArrayList<JSON_Token> tokenArray, int arrayIDindex){
        //0. Initialize token
        JSON_Array r = new JSON_Array(); //return value
        JSON_Token tmpToken1 = tokenArray.get(arrayIDindex);
        JSON_Token tmpToken2;
        JSON_Token openToken;
        JSON_Token closeToken;
        int nextTokenIndex; //if any
        boolean hasNextAttribute;

        if(tmpToken1.isOpen()){
            openToken=tmpToken1;
            closeToken=tokenArray.get(openToken.partnerArrayIndex);
        }else{
            closeToken=tmpToken1;
            openToken=tokenArray.get(closeToken.partnerArrayIndex);
        }
        nextTokenIndex=openToken.arrayIndex+1;

        //1. get attribute : value pairs (all attribute keys are in " ")
        //1.0 set temp variables for isolating key value pair in attributes
        int startBodyIndex = openToken.strIndex+1;
        int endBodyIndex = closeToken.strIndex-1;

        JSON_Item nextElement;
        Integer[] resultArray = new Integer[3];
        int controlIndex=startBodyIndex;

        System.out.println("DEBUG: Parsing JSON Array Tokens from "+startBodyIndex+ " to "+endBodyIndex);
        if(controlIndex > endBodyIndex){return r;}// case 1: []

        while(controlIndex <= endBodyIndex){
            System.out.println("DEBUG: Parsing element to array.");
            nextElement = parseJSON_BaseItemFromToken(resultArray, tokenArray, jsonString, controlIndex);
            if(nextElement != null){
                System.out.println("DEBUG: Element found to add to array.");
                r.addJSON_Item(nextElement);
                controlIndex = resultArray[1].intValue()+1;
            }else if(resultArray[0].intValue() == -1){ // last element is ALL white space => no element => OK
                System.out.println("DEBUG: Empty String. No element added to array.");
                return r;
            }else if(resultArray[2].intValue() == JSON_Object.OBJECT){
                System.out.println("DEBUG: Object Found to add to array.");
                controlIndex = resultArray[0].intValue()+1;
                nextElement = parseJSON_ObjectTokens(jsonString, tokenArray, openToken.arrayIndex+1);
                r.addJSON_Item(nextElement);
                return r;
            }else if(resultArray[2].intValue() == JSON_Object.ARRAY){
                System.out.println("DEBUG: Sub Array Found to add to array.");
                controlIndex = resultArray[0].intValue()+1;
                nextElement = parseJSON_ArrayTokens(jsonString, tokenArray, openToken.arrayIndex+1);
                r.addJSON_Item(nextElement);
                return r;
            }else{
                throw new JSON_Exception("DEBUG: ERROR: "+resultArray[3].intValue());
            }
        }
        return r;
    }


    /**
     * 
     * @param logArray. returns values based on the result. Must have at least 3 elements.
     * index 0: starting index of attribute or value.
     * index 1: ending index of attribute or value.
     * index 2: JSON_Object type
     */
    private static JSON_Item parseJSON_BaseItemFromToken(Integer[] logArray, ArrayList<JSON_Token> tokenArray, String jsonString, int startIndex){
        int attrStartIndex = -1; //marker to indicate beginning of attribute not found yet.
        int attrEndIndex;
        int controlIndex = startIndex;
        boolean isFirstCharacterFound = false; //marker to indicate that first character is not found yet.

        char nextChar = 0; // marker to indicate that last character fetched
        //1 Find first quotation (if any) otherwise no attributes in this object
        for(int i=startIndex; i<=jsonString.length(); ++i){
            nextChar = jsonString.charAt(i);
            if(nextChar == '"'){
                logArray[0] = Integer.valueOf(i);
                attrStartIndex=logArray[0].intValue();
                controlIndex=attrStartIndex+1;
                isFirstCharacterFound=true;
                break;
            }else if(!Character.isWhitespace(nextChar)){ //only whitespace is allowed prior to attribute
                logArray[0] = Integer.valueOf(i);
                attrStartIndex=logArray[0].intValue();
                isFirstCharacterFound=true;
                if(nextChar == '{'){
                    logArray[2] = Integer.valueOf(JSON_Object.OBJECT);
                    return null;
                }else if(nextChar == '{'){
                    logArray[2] = Integer.valueOf(JSON_Object.ARRAY);
                    return null;
                }
                break;
            }
        }
        if(!isFirstCharacterFound){
            logArray[0] = Integer.valueOf(-1);
            logArray[1] = Integer.valueOf(-1);
            logArray[2] = Integer.valueOf(-1);
            return null; //No attribute, all white space characters
        };

        if(jsonString.startsWith("true", controlIndex)){
            logArray[1] = Integer.valueOf(attrStartIndex+4);
            logArray[2] = Integer.valueOf(JSON_Object.BOOLEAN);
            return new JSON_Boolean(true);
        }else if(jsonString.startsWith("false", controlIndex)){
            logArray[1] = Integer.valueOf(attrStartIndex+5);
            logArray[2] = Integer.valueOf(JSON_Object.BOOLEAN);
            return new JSON_Boolean(false);
        }else if(jsonString.startsWith("null", controlIndex)){
            logArray[1] = Integer.valueOf(attrStartIndex+4);
            logArray[2] = Integer.valueOf(JSON_Object.NULL);
            return new JSON_Null();
        }else if(nextChar == '"'){
            StringBuilder rendered = new StringBuilder(); //return value, quotes removed and all escape characters resolved.

            ++controlIndex;
            while(controlIndex < jsonString.length()){
                nextChar = jsonString.charAt(controlIndex);
                if(nextChar != '"'){
                    if(nextChar == '\\'){
                        ++controlIndex;
                        if(controlIndex < jsonString.length()){
                            int escapeIndex = ESCAPES.indexOf(jsonString.charAt(controlIndex));
                            if(escapeIndex == -1){
                                logArray[1] = Integer.valueOf(controlIndex);
                                logArray[2] = Integer.valueOf(-91);
                                return null;
                            }else nextChar = RAW_ESCAPES.charAt(escapeIndex);
                        }else{
                            logArray[1] = Integer.valueOf(controlIndex);
                            logArray[2] = Integer.valueOf(-92);
                            return null; //out of bounds at escape sequence
                        }
                    }
                    else if(nextChar < 32){ //Invalid control character (should be checked at first parse passthrough)
                        logArray[1] = Integer.valueOf(controlIndex);
                        logArray[2] = Integer.valueOf(-93);
                        return null;
                    }
                    rendered.append(nextChar);
                }else{ //Terminating quote found
                    logArray[1] = Integer.valueOf(controlIndex);
                    logArray[2] = Integer.valueOf(JSON_Object.STRING);
                    attrEndIndex=controlIndex;
                    return new JSON_String(rendered.toString());
                }
            }
            logArray[1] = Integer.valueOf(controlIndex);
            logArray[2] = Integer.valueOf(-93);
            return null; //Should be impossible to return here
        }else if(Character.isDigit(nextChar) || nextChar == '-'){
            boolean isPositive;
            boolean isInteger = true; //Marker to indicte no decimal is found
            if(nextChar == '-'){ //double check?
                isPositive = false;
                ++controlIndex;
            }else isPositive = true;

            while(controlIndex < jsonString.length()){
                nextChar=jsonString.charAt(controlIndex);
                if(Character.isDigit(nextChar)){++controlIndex;}
                else if(nextChar == '.'){
                    if(isInteger){
                        isInteger=false;
                        ++controlIndex;
                        if(controlIndex < jsonString.length() && Character.isDigit(jsonString.charAt(controlIndex))){ //Next character MUST exist and be a digit
                            ++controlIndex;
                        }else{ //Invalid JSON_Number non-number found in numerical String
                            logArray[1] = Integer.valueOf(-2);
                            logArray[2] = Integer.valueOf(-2);
                            return null;
                        }
                    }else{ //Invalid JSON_Number too many decimals
                        logArray[1] = Integer.valueOf(-3);
                        logArray[2] = Integer.valueOf(-3);
                        return null;
                    }
                }else if(Character.isWhitespace(nextChar) || nextChar == ',' || nextChar == ']' || nextChar == '}'){ //last character case
                    logArray[1] = Integer.valueOf(controlIndex-1);
                    attrEndIndex = logArray[1].intValue();
                    if(isInteger){
                        logArray[2] = Integer.valueOf(JSON_Object.INTEGER);
                        return new JSON_Integer(Long.parseLong(jsonString.substring(attrStartIndex, controlIndex)));
                    }else{
                        logArray[2] = Integer.valueOf(JSON_Object.DECIMAL);
                        return new JSON_Integer(Long.parseLong(jsonString.substring(attrStartIndex, controlIndex)));
                    }
                }else{ //Invalid end character, should be impoosible to return here.
                    logArray[1] = Integer.valueOf(-4);
                    logArray[2] = Integer.valueOf(-4);
                    return null;
                }
            }

        }
        return null;
    }

    /**
     * A helper function that modifies an existing array tailored to parsing json bodies.
     * @param resultArray Must have at least 3 elements
     * index 0 -> be unquoted value with no surounding quotes and all escape characters resolved to their raw values. null if invalid
     * index 1 -> quoted Attribute including quoations. Will be an exact substring. null if invalid.
     * @return the index of the terminating quotation of the JSON string. -Negative number if invalid.
     * -1 indicates no quoation was found
     */
    private static int obtainJSON_StringParse(String[] resultArray, String jsonString, int startIndex){
        int attrStartIndex = -1; //marker to indicate beginning of attribute not found yet.
        int attrEndIndex;
        int controlIndex = startIndex;

        char nextChar;
        //1 Find first quotation (if any) otherwise no attributes in this object
        for(int i=startIndex; i<=jsonString.length(); ++i){
            nextChar = jsonString.charAt(i);
            if(nextChar == '"'){
                attrStartIndex=i;
                controlIndex=attrStartIndex+1;
                break;
            }else if(!Character.isWhitespace(nextChar)){ //only whitespace is allowed prior to attribute
                resultArray[0] = null;
                resultArray[1] = null;
                return -2; //Invalid return; //Return negative EX: -1 * JSON_Object.<TYPE>
            }
        }
        if(attrStartIndex == -1){
            resultArray[0] = null;
            resultArray[1] = null;
            return -1; //No attribute, all white space characters
        };

        StringBuilder substring = new StringBuilder();
        StringBuilder rendered = new StringBuilder();

        substring.append('"'); //

        // 2 Obtain key string (A terminating quote must exist or invalid)
        attrEndIndex = -1; //marker to indicate terminating quote not found yet.
        for(int i=controlIndex; i<=jsonString.length(); ++i){
            nextChar = jsonString.charAt(i);
            if(nextChar != '"'){
                if(nextChar == '\\'){
                    ++i; //should be safe from out of bounds because private function guarantees proper params were passed in.
                    if(ESCAPES.indexOf(jsonString.charAt(i)) == -1){ //invalid escape sequence
                        resultArray[0] = null;
                        resultArray[1] = null;
                        return -2;
                    };
                }
                else if(nextChar < 32){ //Invalid control character
                    resultArray[0] = null;
                    resultArray[1] = null;
                    return -3;
                }
                substring.append(nextChar);
                rendered.append(nextChar);
            }else{
                attrEndIndex=i;
                substring.append('"');
                resultArray[0]=rendered.toString();
                resultArray[1]=substring.toString();
                return attrEndIndex;
            }
        }
        if(attrEndIndex == -1) {//open quote was found but not a terminating closing quote
            resultArray[0] = null;
            resultArray[1] = null;
            return -4;
        }
        return -5; //Should never return at this location
    }

    private static abstract class JSON_Token implements Comparable<JSON_Token>{
        int pairID;
        int strIndex;
        int arrayIndex;
        int partnerArrayIndex;
        int partnerStrIndex;
        JSON_Token(int new_id, int index){
            pairID=new_id;
            strIndex=index;
            arrayIndex=-1;
            partnerArrayIndex=-1;
        }
        abstract boolean isOpen();
        abstract boolean isBrace();
        int getArrayIndexFromStrIndex(int stringIndex){return (stringIndex==strIndex ? arrayIndex : -1);}

        @Override
        public int compareTo(JSON_Token otherToken){
            return strIndex - otherToken.strIndex;
        }
    }

    private static class JSON_OpenBrace extends JSON_Token{
        JSON_OpenBrace(int new_id, int index){super(new_id,index);}

        @Override
        boolean isOpen(){return true;}

        @Override
        boolean isBrace(){return true;}
    }

    private static class JSON_CloseBrace extends JSON_Token{
        JSON_CloseBrace(int new_id, int index){super(new_id,index);}

        @Override
        boolean isOpen(){return false;}

        @Override
        boolean isBrace(){return true;}
    }

    private static class JSON_OpenBracket extends JSON_Token{
        JSON_OpenBracket(int new_id, int index){super(new_id,index);}

        @Override
        boolean isOpen(){return true;}

        @Override
        boolean isBrace(){return false;}
    }

    private static class JSON_CloseBracket extends JSON_Token{
        JSON_CloseBracket(int new_id, int index){super(new_id,index);}

        @Override
        boolean isOpen(){return false;}

        @Override
        boolean isBrace(){return false;}
    }
}