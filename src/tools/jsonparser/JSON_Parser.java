package tradedatacorp.tools.jsonparser;

import java.util.Collections;
import java.util.Stack;
import java.util.ArrayList;

public final class JSON_Parser{

    public static JSON_Object parse(String jsonString){
        //0. Preflight check
        int controlIndex = 0;
        int nextID = 0;
        int nextOpenBraceIndex;
        int nextCloseBraceIndex;
        int nextOpenBracketIndex;
        int nextCloseBracketIndex;
        int rootOpenBraceIndex;
        int LENGTH = jsonString.length();

        //1. find root brace
        rootOpenBraceIndex=jsonString.indexOf('{');
        if(rootOpenBraceIndex == -1) return null; //not valid JSON
        Stack<JSON_Token> tokenStack = new Stack<JSON_Token>();
        tokenStack.push(new JSON_OpenBrace(nextID, rootOpenBraceIndex));

        //DEBUG SECTION
        System.out.println("DEBUG ROOT TOKEN: id: "+tokenStack.peek().id + " , index: "+tokenStack.peek().strIndex+" , "+(tokenStack.peek().isOpen() ? "open" : "closed")+" , "+(tokenStack.peek().isBrace() ? "brace" : "no-brace"));
        //END DEBUG SECTION

        ++nextID;
        controlIndex=rootOpenBraceIndex+1;

        //Obtain ALL 4 tokens
        boolean bracketsRemain=true;
        boolean bracesRemain=true;
        ArrayList<JSON_Token> nextTokenL = new ArrayList<JSON_Token>(4); 
        do{
            nextOpenBraceIndex=jsonString.indexOf('{',controlIndex);
            nextCloseBraceIndex=jsonString.indexOf('}',controlIndex);
            nextOpenBracketIndex=jsonString.indexOf('[',controlIndex);
            nextCloseBracketIndex=jsonString.indexOf(']',controlIndex);

            if(nextOpenBraceIndex != -1){
                nextTokenL.add(new JSON_OpenBrace(-1, nextOpenBraceIndex)); //tmp assign -1 to id until next tokens are added
            }

            if(nextCloseBraceIndex != -1){
                nextTokenL.add(new JSON_CloseBrace(-1, nextCloseBraceIndex)); //tmp assign -1 to id until next tokens are added
            }

            if(nextOpenBracketIndex != -1){
                nextTokenL.add(new JSON_OpenBracket(-1, nextOpenBracketIndex)); //tmp assign -1 to id until next tokens are added
            }

            if(nextCloseBracketIndex != -1){
                nextTokenL.add(new JSON_CloseBracket(-1, nextCloseBracketIndex)); //tmp assign -1 to id until next tokens are added
            }

            Collections.sort(nextTokenL);
            for(JSON_Token t : nextTokenL){
                t.id=nextID;
                tokenStack.push(t);
                //DEBUG SECTION
                System.out.println("DEBUG TOKEN: id: "+t.id+" , index: "+tokenStack.peek().strIndex+" , "+(t.isOpen() ? "open" : "closed")+" , "+(t.isBrace() ? "brace" : "no-brace"));
                //END DEBUG SECTION
                if(t.isOpen()) ++nextID;
                else --nextID;
            }
            controlIndex = nextTokenL.get(nextTokenL.size()-1).strIndex+1;
            nextTokenL.clear();

            if(nextOpenBracketIndex == nextCloseBracketIndex) bracketsRemain=false;
        }while(controlIndex<LENGTH && bracketsRemain && nextID >= 0);

        //Same loop but not searching for brackets
        while(controlIndex<LENGTH && nextID >= 0){
            nextOpenBraceIndex=jsonString.indexOf('{',controlIndex);
            nextCloseBraceIndex=jsonString.indexOf('}',controlIndex);

            if(nextOpenBraceIndex != -1){
                nextTokenL.add(new JSON_OpenBrace(-1, nextOpenBraceIndex)); //tmp assign -1 to id until next tokens are added
            }

            if(nextCloseBraceIndex != -1){
                nextTokenL.add(new JSON_CloseBrace(-1, nextCloseBraceIndex)); //tmp assign -1 to id until next tokens are added
            }

            Collections.sort(nextTokenL);
            for(JSON_Token t : nextTokenL){
                t.id=nextID;
                tokenStack.push(t);
                //DEBUG SECTION
                System.out.println("DEBUG NO-BRACKET TOKEN: id: "+t.id+" , index: "+tokenStack.peek().strIndex+" , "+(t.isOpen() ? "open" : "closed")+" , "+(t.isBrace() ? "brace" : "no-brace"));
                //END DEBUG SECTION
                if(t.isOpen()) ++nextID;
                else --nextID;
            }
            controlIndex = nextTokenL.get(nextTokenL.size()-1).strIndex+1;
            nextTokenL.clear();
        }

        return parseJSON_Tokens(jsonString, tokenStack);
    }

    private static JSON_Object parseJSON_Tokens(String jsonString, Stack<JSON_Token> tokenStack){
        //DEBUG SECTION
        System.out.println("DEBUG: print tokens");
        for(JSON_Token t : tokenStack){
            System.out.println("DEBUG: id: "+t.id+" , index: "+t.strIndex+" , "+(t.isOpen() ? "open" : "closed")+" , "+(t.isBrace() ? "brace" : "no-brace"));
        }
        System.out.println("DEBUG: done printing tokens");
        //END DEBUG SECTION
        return null;
    }

    private static abstract class JSON_Token implements Comparable<JSON_Token>{
        int id;
        int strIndex;
        JSON_Token(int new_id, int index){
            id=new_id;
            strIndex=index;
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