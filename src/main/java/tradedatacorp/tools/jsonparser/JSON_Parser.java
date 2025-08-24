/**
 * @author Bruce Lamb
 * @since 30 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

import java.util.Stack;
import java.util.ArrayList;

/**
 * JSON_Parser is a utility class designed to parse JSON-formatted strings into structured Java objects, using: 
 * {@link JSON_Object}, {@link JSON_Array}, {@link JSON_String}, {@link JSON_Integer}, {@link JSON_Decimal}, {@link JSON_Boolean}, and {@link JSON_Null}.
 * The parser is intended to be compliant with the RFC 8259 JSON standard, with support for objects, arrays, strings, numbers, booleans, and null values.
 * 
 * Current Limitations:
 * <ul>
 *   <li>Does not support scientific notation for numbers.</li>
 *   <li>Does not support Unicode escape sequences: uXXXX.</li>
 * </ul>
 */
public final class JSON_Parser{
    /** String of valid escape characters in JSON strings. */
    public static final String ESCAPES;

    /** String of corresponding raw values for JSON escape sequences. */
    public static final String RAW_ESCAPES;

    static{
        char[] escapes = {'"','\\','/','b','f','n','r','t'};
        char[] rawEscapes = {'"','\\','/',8,12,10,13,9};
        ESCAPES = String.copyValueOf(escapes);
        RAW_ESCAPES = String.copyValueOf(rawEscapes);
    }

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private JSON_Parser() {
        throw new AssertionError("JSON_Parser cannot be instantiated.");
    }
    /**
     * Parses a JSON-formatted string and returns the root {@link JSON_Object}.
     * If the input is not a valid JSON object or is malformed, this method may return {@code null} or throw a {@link JSON_Exception}.
     * @param JSON_Item.OBJECT Must be a valid JSON String format IAW RFC 8259.
     * @return {@link JSON_Object} representing the {@code jsonString}.
     */
    public static JSON_Object parse(String jsonString){
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

    /**
     * Parses a JSON object from tokenized input.
     * @param jsonString the JSON source string.
     * @param tokenArray the list of structural tokens (braces/brackets). The tokens MUST be paired properly and in order. This will NOT be checked.
     * @param arrayIDindex the index in the token array to start parsing. This can be an open token or closing token.
     * @return the parsed {@link JSON_Object}.
     * @throws JSON_Exception if the JSON is malformed.
     */
    private static JSON_Object parseJSON_ObjectTokens(String jsonString, ArrayList<JSON_Token> tokenArray, int arrayIDindex) throws JSON_Exception{
        //0. Initialize token
        JSON_Object r = new JSON_Object(); //return value
        JSON_Token tmpToken1 = tokenArray.get(arrayIDindex);
        JSON_Token tmpToken2 = null;
        JSON_Token openToken;
        JSON_Token closeToken;

        if(tmpToken1.isOpen()){
            openToken=tmpToken1;
            closeToken=tokenArray.get(openToken.partnerArrayIndex);
        }else{
            closeToken=tmpToken1;
            openToken=tokenArray.get(closeToken.partnerArrayIndex);
        }

        int startBodyIndex = openToken.strIndex+1;
        int endBodyIndex = closeToken.strIndex-1;
        int nextPotentialChildIndex = openToken.arrayIndex+1;
        boolean isColonSeparated;

        char nextChar = 0;
        String keyString;

        JSON_Item nextItem;
        String[] tmpStringArr = new String[2];
        Integer[] resultArray = new Integer[3];
        int controlIndex=startBodyIndex;

        if(controlIndex > endBodyIndex){return r;}// case 1: {}

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

            //3 Find value (returns a base case OR resultArray will have next recursive index values)
            nextItem = parseJSON_BaseItemFromToken(resultArray, tokenArray, jsonString, controlIndex);

            if(nextItem != null){
                r.addJSON_Attribute(keyString,nextItem);
                controlIndex = resultArray[1].intValue()+1;
            }else if(resultArray[2].intValue() == -JSON_Item.OBJECT){
                tmpToken1 = null;
                while(nextPotentialChildIndex < closeToken.arrayIndex){
                    tmpToken1 = tokenArray.get(nextPotentialChildIndex);
                    if(tmpToken1.isOpen()){
                        ++nextPotentialChildIndex;
                        break;
                    }
                    ++nextPotentialChildIndex;
                }
                tmpToken2 = tokenArray.get(tmpToken1.partnerArrayIndex);
                nextItem = parseJSON_ObjectTokens(jsonString, tokenArray, tmpToken1.arrayIndex);
                r.addJSON_Attribute(keyString,nextItem);
                controlIndex = tmpToken2.strIndex+1;
            }else if(resultArray[2].intValue() == -JSON_Object.ARRAY){
                tmpToken1 = null;
                while(nextPotentialChildIndex < closeToken.arrayIndex){
                    tmpToken1 = tokenArray.get(nextPotentialChildIndex);
                    if(tmpToken1.isOpen()){
                        ++nextPotentialChildIndex;
                        break;
                    }
                    ++nextPotentialChildIndex;
                }
                tmpToken2 = tokenArray.get(tmpToken1.partnerArrayIndex);
                nextItem = parseJSON_ArrayTokens(jsonString, tokenArray, tmpToken1.arrayIndex);
                r.addJSON_Attribute(keyString,nextItem);
                controlIndex = tmpToken2.strIndex+1;
            }else{
                throw new JSON_Exception(
                    "ERROR:\nindex0: "+resultArray[0].intValue()+
                    "\nindex1: "+resultArray[1].intValue()+
                    "\nindex2: "+resultArray[2].intValue()
                );
            }

            //Find a ',' or terminating character '}'
            while(controlIndex < endBodyIndex){
                nextChar = jsonString.charAt(controlIndex);
                if(Character.isWhitespace(nextChar)) ++controlIndex;
                else if(nextChar == ','){
                    ++controlIndex;
                    break;
                }else if(nextChar == '}') return r;
                else throw new JSON_Exception("Invalid character within Object body: "+((char)nextChar));
            }
        }

        return r;
    }

    /**
     * Parses a JSON array from tokenized input.
     * @param jsonString the JSON source string.
     * @param tokenArray the list of structural tokens (braces/brackets).
     * @param arrayIDindex the index in the token array to start parsing.
     * @return the parsed {@link JSON_Array}.
     * @throws JSON_Exception if the JSON is malformed.
     */
    private static JSON_Array parseJSON_ArrayTokens(String jsonString, ArrayList<JSON_Token> tokenArray, int arrayIDindex){
        //0. Initialize token
        JSON_Array r = new JSON_Array(); //return value
        JSON_Token tmpToken1 = tokenArray.get(arrayIDindex);
        JSON_Token tmpToken2;
        JSON_Token openToken;
        JSON_Token closeToken;

        if(tmpToken1.isOpen()){
            openToken=tmpToken1;
            closeToken=tokenArray.get(openToken.partnerArrayIndex);
        }else{
            closeToken=tmpToken1;
            openToken=tokenArray.get(closeToken.partnerArrayIndex);
        }

        //1. get attribute : value pairs (all attribute keys are in " ")
        //1.0 set temp variables for isolating key value pair in attributes.
        int startBodyIndex = openToken.strIndex+1;
        int endBodyIndex = closeToken.strIndex-1;
        int nextPotentialChildIndex = openToken.arrayIndex+1;

        char nextChar;
        JSON_Item nextElement;
        Integer[] resultArray = new Integer[3];
        int controlIndex=startBodyIndex;

        if(controlIndex > endBodyIndex){return r;}// case 1: []

        while(controlIndex <= endBodyIndex){
            nextElement = parseJSON_BaseItemFromToken(resultArray, tokenArray, jsonString, controlIndex);
            if(nextElement != null){
                r.addJSON_Item(nextElement);
                controlIndex = resultArray[1].intValue()+1;
            }else if(resultArray[2].intValue() == -JSON_Item.OBJECT){
                if(resultArray[0].intValue() > -1){ //open token found
                    tmpToken1 = null;
                    while(nextPotentialChildIndex < closeToken.arrayIndex){
                        tmpToken1 = tokenArray.get(nextPotentialChildIndex);
                        if(tmpToken1.isOpen()){
                            ++nextPotentialChildIndex;
                            break;
                        }
                        ++nextPotentialChildIndex;
                    }
                    tmpToken2 = tokenArray.get(tmpToken1.partnerArrayIndex);
                    nextElement = parseJSON_ObjectTokens(jsonString, tokenArray, tmpToken1.arrayIndex);
                    r.addJSON_Item(nextElement);
                    controlIndex = tmpToken2.strIndex+1;
                }else if(resultArray[1].intValue() > -1){
                    return r; //terminating array
                }else throw new JSON_Exception("Something is terribly wrong.");
            }else if(resultArray[2].intValue() == -JSON_Object.ARRAY){
                if(resultArray[0].intValue() > -1){
                    tmpToken1 = null;
                    while(nextPotentialChildIndex < closeToken.arrayIndex){
                        tmpToken1 = tokenArray.get(nextPotentialChildIndex);
                        if(tmpToken1.isOpen()){
                            ++nextPotentialChildIndex;
                            break;
                        }
                        ++nextPotentialChildIndex;
                    }
                    tmpToken2 = tokenArray.get(tmpToken1.partnerArrayIndex);

                    nextElement = parseJSON_ArrayTokens(jsonString, tokenArray, tmpToken1.arrayIndex);
                    r.addJSON_Item(nextElement);
                    controlIndex = tmpToken2.strIndex+1;
                }else if(resultArray[1].intValue() > -1){
                    return r; //terminating Array
                }else throw new JSON_Exception("");
                
            }else{
                throw new JSON_Exception(
                    "ERROR:\nindex0: "+resultArray[0].intValue()+
                    "\nindex1: "+resultArray[1].intValue()+
                    "\nindex2: "+resultArray[2].intValue()
                );
            }

            //Find a ',' or terminating character ']'
            while(controlIndex <= endBodyIndex){
                nextChar = jsonString.charAt(controlIndex);
                if(Character.isWhitespace(nextChar)) ++controlIndex;
                else if(nextChar == ','){
                    ++controlIndex;
                    break;
                }else if(nextChar == ']') return r;
                else throw new JSON_Exception("Invalid character within Array body: "+((char)nextChar));
            }
        }
        return r;
    }

    /**
     * Attempts to parse a single JSON value (string, number, boolean, null, object, or array) from the given position.
     * @param logArray Integer[3] used as out-parameters:
     *                 index 0: start index of the value,
     *                 index 1: end index of the value,
     *                 index 2: type code (positive for recognized types, negative for structures or errors).
     * @param tokenArray the list of tokens for structural parsing.
     * @param jsonString the JSON source string.
     * @param startIndex the index in the string to begin parsing.
     * @return the parsed {@link JSON_Item}, or {@code null} if the value is an object/array or on error.
     */
    private static JSON_Item parseJSON_BaseItemFromToken(Integer[] logArray, ArrayList<JSON_Token> tokenArray, String jsonString, int startIndex){
        int attrStartIndex = -1; //marker to indicate beginning of attribute not found yet.
        int attrEndIndex;
        int controlIndex = startIndex;
        boolean isFirstCharacterFound = false; //marker to indicate that first character is not found yet.

        char nextChar = 0; // marker to indicate that last character fetched
        //1 Find first quotation (if any) otherwise no attributes in this object
        for(int i=startIndex; i<jsonString.length(); ++i){
            nextChar = jsonString.charAt(i);
            if(nextChar == '"'){
                logArray[0] = Integer.valueOf(i);
                attrStartIndex=logArray[0].intValue();
                controlIndex=attrStartIndex+1;
                isFirstCharacterFound=true;
                break;
            }else if(!Character.isWhitespace(nextChar)){ //only whitespace is allowed prior to attribute
                if(nextChar == '{'){
                    logArray[0] = Integer.valueOf(i);
                    logArray[1] = Integer.valueOf(-1);
                    logArray[2] = Integer.valueOf(-JSON_Item.OBJECT);
                    return null;
                }else if(nextChar == '['){
                    logArray[0] = Integer.valueOf(i);
                    logArray[1] = Integer.valueOf(-1);
                    logArray[2] = Integer.valueOf(-JSON_Object.ARRAY);
                    return null;
                }else if(nextChar == '}'){
                    logArray[0] = Integer.valueOf(-1);
                    logArray[1] = Integer.valueOf(i);
                    logArray[2] = Integer.valueOf(-JSON_Item.OBJECT);
                    return null;
                }else if(nextChar == ']'){
                    logArray[0] = Integer.valueOf(-1);
                    logArray[1] = Integer.valueOf(i);
                    logArray[2] = Integer.valueOf(-JSON_Object.ARRAY);
                    return null;
                }
                logArray[0] = Integer.valueOf(i);
                attrStartIndex=logArray[0].intValue();
                controlIndex=attrStartIndex+1;
                isFirstCharacterFound=true;
                break;
            }
        }
        if(!isFirstCharacterFound){ //Should never cross here for any valid JSON string
            logArray[0] = Integer.valueOf(startIndex);
            logArray[1] = Integer.valueOf(jsonString.length()-1);
            logArray[2] = Integer.valueOf(-11);
            return null; //No attribute, all white space characters
        };

        if(jsonString.startsWith("true", attrStartIndex)){
            logArray[1] = Integer.valueOf(attrStartIndex+3);
            logArray[2] = Integer.valueOf(JSON_Object.BOOLEAN);
            return new JSON_Boolean(true);
        }else if(jsonString.startsWith("false", attrStartIndex)){
            logArray[1] = Integer.valueOf(attrStartIndex+4);
            logArray[2] = Integer.valueOf(JSON_Object.BOOLEAN);
            return new JSON_Boolean(false);
        }else if(jsonString.startsWith("null", attrStartIndex)){
            logArray[1] = Integer.valueOf(attrStartIndex+3);
            logArray[2] = Integer.valueOf(JSON_Object.NULL);
            return new JSON_Null();
        }else if(nextChar == '"'){
            StringBuilder rendered = new StringBuilder(); //return value, quotes removed and all escape characters resolved.

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
                ++controlIndex;
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
                        return new JSON_Decimal(Double.parseDouble(jsonString.substring(attrStartIndex, controlIndex)));
                    }
                }else{ //Invalid end character, should be impoosible to return here.
                    logArray[1] = Integer.valueOf(-4);
                    logArray[2] = Integer.valueOf(-4);
                    return null;
                }
            }

        }
        logArray[0] = Integer.valueOf(-1*logArray[0].intValue());
        logArray[1] = Integer.valueOf(-1*controlIndex);
        if(nextChar == ']' || nextChar == '}') logArray[2] = Integer.valueOf(-21);
        else logArray[2] = Integer.valueOf(-99); //unknown type
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

    /**
     * A temporary class that represents a token for a brace or bracket in the JSON string.
     * Used to match opening and closing braces/brackets during parsing.
     */
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