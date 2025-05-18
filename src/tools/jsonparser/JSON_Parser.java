/**
 * @author Bruce Lamb
 * @since 17 MAY 2025
 */
package tradedatacorp.tools.jsonparser;

import java.util.Collections;
import java.util.Stack;
import java.util.ArrayList;

public final class JSON_Parser{

    public static final String ESCAPES;

    static{
        char[] escapes = {'"','\\','/','b','f','n','r','t'};
        ESCAPES = String.copyValueOf(escapes);
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

        return parseJSON_Tokens(jsonString, attributeName, tokenList, 0);
    }

    public static JSON_Object parse(String jsonString){return parse(jsonString,"root");}

    private static JSON_Object parseJSON_Tokens(String jsonString, String attributeName, ArrayList<JSON_Token> tokenArray, int arrayIDindex){
        //DEBUG SECTION
        System.out.println("DEBUG: print "+tokenArray.size()+" tokens");
        for(JSON_Token t : tokenArray){
            System.out.println(
                "DEBUG: id: "+t.pairID+
                " , String index: "+t.strIndex+
                " , index "+t.arrayIndex+
                " , partnerIndex "+t.partnerArrayIndex+
                (t.isOpen() ? " , open" : " ,closed")+
                (t.isBrace() ? " , brace" : " , no-brace")
            );
        }
        System.out.println("DEBUG: done printing tokens");
        System.out.println("DEBUG: ESCAPE CHARACTERS: "+ESCAPES);
        //END DEBUG SECTION

        //0. Initialize token
        JSON_Object r = new JSON_Object(attributeName); //return value
        JSON_Token tmpToken = tokenArray.get(arrayIDindex);
        JSON_Token openToken;
        JSON_Token closeToken;

        if(tmpToken.isOpen()){
            openToken=tmpToken;
            closeToken=tokenArray.get(openToken.partnerArrayIndex);
        }else{
            closeToken=tmpToken;
            openToken=tokenArray.get(closeToken.partnerArrayIndex);
        }

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

        char nextChar;
        String keyString;
        String valueString;

        int controlIndex=startBodyIndex;

        //base case
        if(controlIndex >= endBodyIndex) return r;

        //BRACES ONLY Parse, create, add attributes 1 at a time.
        if(openToken.isBrace() && closeToken.isBrace()) System.out.println("DEBUG: IS BRACE");
        while(controlIndex <= endBodyIndex){
            //1.1 Find first quotation (if any) otherwise no attributes in this object
            attrStartIndex = -1; //marker to indicate beginning of attribute not found
            for(int i=controlIndex; i<=endBodyIndex; ++i){
                nextChar = jsonString.charAt(i);
                if(nextChar == '"'){
                    attrStartIndex=i;
                    controlIndex=attrStartIndex+1;
                    break;
                }else if(!Character.isWhitespace(nextChar)) return null; //only whitespace is allowed prior to attribute
            }
            if(attrStartIndex == -1) return r; //no attriubtes found
            else keyStartIndex=attrStartIndex;

            // 1.2 Obtain key string
            attrEndIndex = -1;
            for(int i=controlIndex; i<=endBodyIndex; ++i){
                nextChar = jsonString.charAt(i);
                if(nextChar != '"'){
                    if(nextChar == '\\'){
                        ++i; //should be safe from out of bounds because private function guarantees proper params were passed in.
                        if(ESCAPES.indexOf(jsonString.charAt(i)) == -1) return null; //invalid escape sequence
                    }
                    else if(nextChar < 32) return null; //Invalid control character
                }else{
                    keyEndIndex=i;
                    controlIndex=keyEndIndex+1;
                    keyString=jsonString.substring(keyStartIndex+1,keyEndIndex);
                    System.out.println("DEBUG: KEY ATTR: "+keyString);
                    break;
                }
            }
            
            
            // 1.3 find ':' to begin parsing corresponding value
            break;
        }

        return null;
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