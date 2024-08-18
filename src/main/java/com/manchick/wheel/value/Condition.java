package com.manchick.wheel.value;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Condition {

    Condition FALSE = (left, right) -> false;
    Condition TRUE = (left, right) -> true;

    boolean test(double left, double right);

    static Condition readCondition(String string){
        Parser parser = new Parser(string);
        return parser.readCondition(string);
    }

    class Parser {

        private final Logger LOGGER = LoggerFactory.getLogger(Condition.class);

        private final String string;
        private int cursor;

        private Parser(String string){
            this.string = string;
            this.cursor = 0;
        }

        public char peek(){
            return string.charAt(cursor);
        }

        public void skip(){
            cursor++;
        }

        public boolean canRead(){
            return cursor < string.length();
        }

        public Condition readCondition(String string){
            double left = readPart(string);
            char operator;
            if(!canRead()){
                LOGGER.warn("Couldn't properly read the expression: {}, evaluating to true", string);
                return TRUE;
            } else {
                operator = peek();
                skip();
            };
            double right = readPart(string);
            return toCondition(left, operator, right);
        }

        public double readPart(String string){
            int start = cursor;
            while (canRead() && !isOperator(peek())){
                skip();
            }
            String part = string.substring(start, cursor);
            if(part.startsWith("$")){
                return ValueStorage.get(Identifier.of(part.substring(1)));
            }
            return Double.parseDouble(part);
        }

        private static Condition toCondition(double a, char c, double b){
            return switch (c){
                case '=' -> (left, right) -> left == right;
                case '>' -> (left, right) -> left > right;
                case '<' -> (left, right) -> left < right;
                default -> throw new IllegalArgumentException("Invalid operator: " + c);
            };
        }

        public boolean isOperator(char c){
            return switch (c){
                case '=', '>', '<' -> true;
                default -> false;
            };
        }
    }
}
