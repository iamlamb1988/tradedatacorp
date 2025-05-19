/**
 * @author Bruce Lamb
 * @since 18 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

import java.util.Collections;
import java.util.Stack;
import java.util.ArrayList;

public final class JSON_Parser{

    public static final String ESCAPES;
    private static final String[] VALID_UNQUOTED_VALUES;

    static{
        char[] escapes = {'"','\\','/','b','f','n','r','t'};
        ESCAPES = String.copyValueOf(escapes);

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
        JSON_Token tmpToken = new JSON_OpenBrace(nextID, rootOpenBraceIndex);

        tokenStack.push(tmpToken);
        tmpToken.arrayIndex=tokenList.size();
        tokenList.add(tmpToken);
        ++nextID;
        controlIndex=rootOpenBraceIndex+1;

        while(controlIndex < LENGTH){
            switch(jsonString.charAt(controlIndex)){
                case '{':
                    tmpToken = new JSON_OpenBrace(nextID, controlIndex);
                    tokenStack.push(tmpToken);
                    tmpToken.arrayIndex=tokenList.size();
                    tokenList.add(tmpToken);
                    ++nextID;
                    break;
                case '}':
                    tmpToken = new JSON_CloseBrace(tokenStack.peek().pairID, controlIndex);
                    tmpToken.arrayIndex=tokenList.size();
                    tokenList.add(tmpToken);
                    tmpToken.partnerArrayIndex=tokenStack.peek().arrayIndex;
                    tokenStack.peek().partnerArrayIndex=tmpToken.arrayIndex;
                    tokenStack.pop();
                    break;
                case '[':
                    tmpToken = new JSON_OpenBracket(nextID, controlIndex);
                    tokenStack.push(tmpToken);
                    tmpToken.arrayIndex=tokenList.size();
                    tokenList.add(tmpToken);
                    ++nextID;
                    break;
                case ']':
                    tmpToken = new JSON_CloseBracket(tokenStack.peek().pairID, controlIndex);
                    tmpToken.arrayIndex=tokenList.size();
                    tokenList.add(tmpToken);
                    tmpToken.partnerArrayIndex=tokenStack.peek().arrayIndex;
                    tokenStack.peek().partnerArrayIndex=tmpToken.arrayIndex;
                    tokenStack.pop();
                    break;            
            }
            ++controlIndex;
        }

        //Check Validation
        if(!tokenStack.empty() && tokenList.size()%2 != 0) return null;
        if(tokenList.get(0).pairID != tokenList.get(tokenList.size()-1).pairID) return null;

        return (JSON_Object)parseJSON_Tokens(jsonString, tokenList, 0);
    }

    public static JSON_Object parse(String jsonString){return parse(jsonString,"root");}

    private static JSON_Composite parseJSON_Tokens(String jsonString, ArrayList<JSON_Token> tokenArray, int arrayIDindex){
        //DEBUG SECTION
        System.out.println("DEBUG: print "+tokenArray.size()+" tokens");
        for(JSON_Token t : tokenArray){
            System.out.println(
                "DEBUG: id: "+t.pairID+
                " , String index: "+t.strIndex+
                " , index "+t.arrayIndex+
                " , partnerIndex "+t.partnerArrayIndex+
                (t.isOpen() ? " , open" : " , closed")+
                (t.isBrace() ? " , brace" : " , no-brace")
            );
        }
        System.out.println("DEBUG: done printing tokens");
        System.out.println("DEBUG: ESCAPE CHARACTERS: "+ESCAPES);
        //END DEBUG SECTION

        //0. Initialize token
        JSON_Composite r; //return value
        JSON_Token tmpToken = tokenArray.get(arrayIDindex);
        JSON_Token openToken;
        JSON_Token closeToken;
        int nextTokenIndex; //if any
        boolean hasNextAttribute;

        if(tmpToken.isOpen()){
            openToken=tmpToken;
            closeToken=tokenArray.get(openToken.partnerArrayIndex);
        }else{
            closeToken=tmpToken;
            openToken=tokenArray.get(closeToken.partnerArrayIndex);
        }
        if(openToken.isBrace()){
            r = new JSON_Object(); 
        }else r = new JSON_Array();
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

        //base case: all whitespace characters within body or '{}' or '[]'
        while(controlIndex <= endBodyIndex){
            nextChar=jsonString.charAt(controlIndex);
            if(!Character.isWhitespace(nextChar)){
                break;
            }
            ++controlIndex;
        }

        if(controlIndex >= endBodyIndex){
            System.out.println("DEBUG: Base case: Empty object");
            return r;
        }

        while(controlIndex < endBodyIndex){
            //0. reset values
            hasNextAttribute=false; //marker until comma is found

            //1. obtain key value
            controlIndex = obtainJSON_StringParse(tmpStringArr,jsonString,controlIndex)+1;
            if(controlIndex == -1) return null;
            keyString = tmpStringArr[0];

            // 2 find ':' to begin parsing corresponding value
            isColonSeparated=false; //marker to indicate colon is not yet found to corresponding the value for key.
            for(int i=controlIndex; i<endBodyIndex; ++i){
                nextChar = jsonString.charAt(i);
                if(nextChar == ':'){
                    isColonSeparated=true;
                    controlIndex=i+1;
                    break;
                }else if(!Character.isWhitespace(nextChar)) return null;
            }
            if(!isColonSeparated) return null; //Missing colon, invalid JSON body format.

            //3 Find value
            //3.1 Find first valid character
            valueStartIndex = -1;
            while(controlIndex <= endBodyIndex){
                nextChar=jsonString.charAt(controlIndex);
                if(!Character.isWhitespace(nextChar)){
                    valueStartIndex=controlIndex;
                    ++controlIndex;
                    break;
                }
                ++controlIndex;
            }
            if(valueStartIndex == -1) return null; //value cannot be empty

            //3.2 Determine if type is primitive base (String, Boolean, Number, Null) or Compound (Object, Array)
            if(nextChar == '"'){
                controlIndex = obtainJSON_StringParse(tmpStringArr,jsonString,valueStartIndex)+1;
                valueString = tmpStringArr[0];
                if(r instanceof JSON_Object){
                    ((JSON_Object)r).addJSON_Attribute(keyString, new JSON_String(valueString));
                }else if(r instanceof JSON_Array)
                    ((JSON_Array)r).addJSON_Item(new JSON_String(valueString));
                else return null; //Should not be possible to return at this line.
            }else if(jsonString.startsWith("true",controlIndex)){
                System.out.println("DEBUG: Handle boolean with value TRUE");
                if(r instanceof JSON_Object)
                    ((JSON_Object)r).addJSON_Attribute(keyString, new JSON_Boolean(true));
                else if(r instanceof JSON_Array)
                    ((JSON_Array)r).addJSON_Item(new JSON_Boolean(true));
                else return null; //Should not be possible to return at this line.
                controlIndex += 4; //length of "true"
            }else if(jsonString.startsWith("false",controlIndex)){
                System.out.println("DEBUG: Handle boolean with value FALSE");
                if(r instanceof JSON_Object)
                    ((JSON_Object)r).addJSON_Attribute(keyString, new JSON_Boolean(false));
                else if(r instanceof JSON_Array)
                    ((JSON_Array)r).addJSON_Item(new JSON_Boolean(false));
                else return null; //Should not be possible to return at this line.
                controlIndex += 5; //length of "false"
            }else if(jsonString.startsWith("null",controlIndex)){
                System.out.println("DEBUG: Handle NULL");
                if(r instanceof JSON_Object)
                    ((JSON_Object)r).addJSON_Attribute(keyString, new JSON_Null());
                else if(r instanceof JSON_Array)
                    ((JSON_Array)r).addJSON_Item(new JSON_Null());
                else return null; //Should not be possible to return at this line.
                controlIndex += 4; //length of "null"
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
                    else return null; //invalid character received for a Decimal
                }

                if(valueEndIndex == -1) return null;

                valueString=jsonString.substring(valueStartIndex,valueEndIndex+1);

                JSON_Number tmpNumber;
                if(isDecimal) tmpNumber = new JSON_Decimal(Double.parseDouble(valueString));
                else tmpNumber = new JSON_Integer(Long.parseLong(valueString));

                if(r instanceof JSON_Object)
                    ((JSON_Object)r).addJSON_Attribute(keyString, tmpNumber);
                else if(r instanceof JSON_Array)
                    ((JSON_Array)r).addJSON_Item(tmpNumber);
                else return null; //Should not be possible to return at this line.
                controlIndex += 4; //length of "null"
            }else if(nextChar == '{' || nextChar == '['){
                tmpToken = tokenArray.get(nextTokenIndex);
                // if(tmpToken.getArrayIndexFromStrIndex(valueStartIndex) != nextTokenIndex) return null;
                if(r instanceof JSON_Object){
                    System.out.println("DEBUG: Handle object recursively");
                    ((JSON_Object)r).addJSON_Attribute(keyString, parseJSON_Tokens(jsonString, tokenArray, nextTokenIndex));
                }else if(r instanceof JSON_Array){
                    System.out.println("DEBUG: Handle array recursively");
                    ((JSON_Array)r).addJSON_Item(parseJSON_Tokens(jsonString, tokenArray, nextTokenIndex));
                }else return null; //Should not be possible to return at this line.

                tmpToken = tokenArray.get(tmpToken.partnerArrayIndex);
                nextTokenIndex = tmpToken.arrayIndex+1;
                controlIndex = tmpToken.strIndex+1;
            }else return null; //invalid value

            // 4. Search for a comma to begin to obtain next attribute
            hasNextAttribute = false;
            while(controlIndex < endBodyIndex){ //If no comma, main loop will be false
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

    /**
     * A helper function that modifies an existing array tailored to parsing json bodies.
     * @param resultArray Must have at least 3 elements
     * index 0 -> be unquoted value with no surounding quotes and all escape characters resolved to their raw values. null if invalid
     * index 1 -> quoted Attribute including quoations. Will be an exact substring. null if invalid.
     * @return the index of the terminating quotation of the JSON string. -Negative number if invalid.
     * 
     */
    private static int obtainJSON_StringParse(String[] resultArray, String jsonString, int startIndex){
        int attrStartIndex;
        int attrEndIndex;
        int controlIndex = startIndex;
        boolean hasOnlyWhitespace = true;

        char nextChar;
        //1 Find first quotation (if any) otherwise no attributes in this object
        attrStartIndex = -1; //marker to indicate beginning of attribute not found yet.
        for(int i=startIndex; i<=jsonString.length(); ++i){
            nextChar = jsonString.charAt(i);
            if(nextChar == '"'){
                attrStartIndex=i;
                controlIndex=attrStartIndex+1;
                break;
            }else if(!Character.isWhitespace(nextChar)){ //only whitespace is allowed prior to attribute
                resultArray[0] = null;
                resultArray[1] = null;
                return -1;
            }
        }
        if(attrStartIndex == -1){
            resultArray[0] = null;
            resultArray[1] = null;
            return -1;
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
                        return -1;
                    };
                }
                else if(nextChar < 32){ //Invalid control character
                    resultArray[0] = null;
                    resultArray[1] = null;
                    return -1;
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
            return -1;
        }
        return -1; //Should never return at this location
    }

    private static abstract class JSON_Token implements Comparable<JSON_Token>{
        int pairID;
        int strIndex;
        int arrayIndex;
        int partnerArrayIndex;
        JSON_Token(int new_id, int index){
            pairID=new_id;
            strIndex=index;
            arrayIndex=-1;
        }
        abstract boolean isOpen();
        abstract boolean isBrace();
        int getArrayIndexFromStrIndex(int stringIndex){ return (stringIndex==strIndex ? arrayIndex : -1);}

        @Override
        public int compareTo(JSON_Token otherToken) {
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