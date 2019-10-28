package com.fforkboat.scanner;

import java.io.*;
import java.util.*;

import com.fforkboat.common.CompileError;
import com.fforkboat.common.ConstValueTable;
import com.fforkboat.scanner.token.Token;
import com.fforkboat.scanner.token.TokenFactory;
import com.fforkboat.scanner.token.TokenType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Scanner {
    // 单例
    private static Scanner scanner = new Scanner();
    private Scanner(){}
    public static Scanner getScanner(){
        return scanner;
    }

    private List<Token> outputTokens = new ArrayList<>();
    private List<CompileError> errors = new ArrayList<>();

    private static String[] keywords = {"if", "else", "while", "for", "break", "continue", "int", "double", "bool", "string", "true", "false", "function", "return"};

    public static void main(String[] args) throws IOException {

        ApplicationContext context = new FileSystemXmlApplicationContext("src/main/resources/META-INF/scannerContext.xml");

        State startState = (State)context.getBean("E");
        Scanner.getScanner().scan(startState, new File("src/test/input/b.txt"));
    }

    public List<Token> scan(State startState, File source) throws IOException {
        if (startState == null)
            throw new IllegalArgumentException("Start state should not be null.");

        this.outputTokens.clear();
        this.errors.clear();

        try(BufferedReader reader = new BufferedReader(new FileReader(source))){
            int rowCount = 0;
            String line;
            while ((line = reader.readLine()) != null){
                rowCount++;
                line = line.trim();
                if (line.length() >= 2 && line.substring(0, 2).equals("//"))
                    continue;

                int i = 0;
                while (i < line.length()){
                    char c = line.charAt(i);
                    while (c == ' ')
                        c = line.charAt(++i);

                    State state = startState;
                    StringBuilder builder = new StringBuilder();
                    while (state.canReceive(c)){
                        builder.append(c);
                        state = state.getNextState(c);

                        if (i == line.length() - 1){
                            i++;
                            break;
                        }

                        c = line.charAt(++i);
                    }
                    String word = builder.toString();
                    if (state.isFinalState()){
                        Token token;
                        switch (state.getCorrespondingTokeType()){
                            case IDENTIFIER:
                                if (word.equals("true") || word.equals("false")){
                                    token = TokenFactory.createLiteralToken(TokenType.BOOL_LITERAL, rowCount, ConstValueTable.getConstValueTable().getBoolIndex(word));
                                }
                                else if (Arrays.asList(keywords).contains(word)){
                                    TokenType tokenType = TokenType.valueOf(word.toUpperCase());
                                    token = TokenFactory.createNormalToken(tokenType, rowCount);
                                }
                                else{
                                    if (word.charAt(builder.length() - 1) == '_'){
                                        errors.add(new CompileError("Illegal identifier name:" + word, rowCount));
                                        // TODO 处理报错
                                    }
                                    token = TokenFactory.createIdentifierToken(rowCount, word);
                                }
                                break;
                            case DOUBLE_LITERAL:
                                token = TokenFactory.createLiteralToken(TokenType.DOUBLE_LITERAL, rowCount, ConstValueTable.getConstValueTable().getDoubleIndex(word));
                                break;
                            case INT_LITERAL:
                                token = TokenFactory.createLiteralToken(TokenType.INT_LITERAL, rowCount, ConstValueTable.getConstValueTable().getIntegerIndex(word));
                                break;
                            case STRING_LITERAL:
                                // 识别得到的单元一定符合"literal"的格式，提取出literal
                                String literal = builder.length() > 2 ? builder.substring(1, builder.length()) : "";
                                token = TokenFactory.createLiteralToken(TokenType.STRING_LITERAL, rowCount, ConstValueTable.getConstValueTable().getStringIndex(literal));
                                break;
                            case ARRAY_OPERATION:
                                // 识别得到的单元一定符合[index]的格式，提取出index
                                int index = Integer.valueOf(builder.substring(1, builder.length()));
                                token = TokenFactory.createArrayOperationToken(rowCount, index);
                                break;

                            case GREATER:
                            case GREATER_EQUAL:
                            case LESS:
                            case LESS_EQUAL:
                            case EQUAL:
                            case UNEQUAL:
                                token = TokenFactory.createNormalToken(TokenType.getTypeForRelationalOperator(word), rowCount);
                                break;
                            default:
                                token = TokenFactory.createNormalToken(state.getCorrespondingTokeType(), rowCount);
                        }

                        outputTokens.add(token);
                    }
                    else{
                        // TODO: 处理报错
                        errors.add(new CompileError("Can not recognize symbol: " + word, rowCount));
                    }

                }
            }
            
        }

        // TODO 错误处理
        if (errors.size() > 0){
            System.out.println(errors);
        }
        else {
            System.out.println(outputTokens);
        }

        return new ArrayList<>(outputTokens);
    }
}
