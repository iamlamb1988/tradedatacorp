package tradedatacorp.tools.jsonparser;

import java.util.Collections;
import java.util.Stack;
import java.util.ArrayList;

public final class JSON_Parser{

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

        if(!tokenStack.empty() && tokenList.size()%2 != 0) return null;

        return parseJSON_Tokens(jsonString, tokenList);
    }

    private static JSON_Object parseJSON_Tokens(String jsonString, ArrayList<JSON_Token> tokenArray){
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
        //END DEBUG SECTION

        //Implement the creation of a usable JSON object.
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