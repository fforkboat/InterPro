package com.fforkboat.scanner;

import com.fforkboat.Configure;
import com.fforkboat.common.Error;
import com.fforkboat.program.ConstValueTable;
import com.fforkboat.scanner.token.Token;
import com.fforkboat.scanner.token.TokenFactory;
import com.fforkboat.scanner.token.TokenType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scanner {
    private static String[] keywords = {"if", "else", "while", "for", "break", "continue", "int", "real", "bool", "string", "void", "true", "false", "function", "return"};

    /**
     * @param source the source code file
     * @return the token list generated from source code. The result is null if there are errors in the source code file.
     * */
    public static List<Token> scan(File source) throws IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/scannerContext.xml");

        State startState = (State)context.getBean("E");

        List<Token> outputTokens = new ArrayList<>();
        List<Error> errors = new ArrayList<>();

        try(BufferedReader reader = new BufferedReader(new FileReader(source))){
            int rowCount = 0;
            String line;
            // 是否在块注释内
            boolean isInBlockComment = false;
            // 一行一行的读取源代码文件
            while ((line = reader.readLine()) != null){
                rowCount++;
                // 去掉这一行头尾的空格
                line = line.trim();
                int i = 0;
                // 一个一个字符的读一行的内容
                while (i < line.length()){
                    char c = line.charAt(i);
                    // 忽略空格
                    while (c == ' ' || c == '\t')
                        c = line.charAt(++i);

                    if (isInBlockComment){
                        if (c == '*' && i < line.length() -1 && line.charAt(i+1) == '/'){
                            isInBlockComment = false;
                            i = i + 2;
                            continue;
                        }
                        i++;
                        continue;
                    }

                    if (c == '/' && i < line.length() -1 && line.charAt(i+1) == '/'){
                        break;
                    }

                    if (c == '/' && i < line.length() -1 && line.charAt(i+1) == '*'){
                        i = i + 2;
                        isInBlockComment = true;
                        continue;
                    }

                    State state = startState;
                    StringBuilder builder = new StringBuilder();
                    boolean foundEscapeChar = false;
                    do {
                        if (foundEscapeChar){
                            switch (c){
                                case 'n':
                                    c = '\n';
                                    break;
                                case 't':
                                    c = '\t';
                                    break;
                                case '\\':
                                    c = '\\';
                                    break;
                                default:
                                    errors.add(Error.createCompileError("Illegal escape character", rowCount));
                            }
                            foundEscapeChar = false;
                        }
                        else if (c == '\\'){
                            foundEscapeChar = true;

                            if (i == line.length() - 1){
                                i++;
                                break;
                            }
                            c = line.charAt(++i);
                            continue;
                        }

                        builder.append(c);
                        state = state.getNextState(c);

                        if (i == line.length() - 1){
                            i++;
                            break;
                        }
                        c = line.charAt(++i);
                    }
                    while (state != null && state.canReceive(c));

                    String word = builder.toString();
                    if (state != null && state.isFinalState()){
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
                                        errors.add(Error.createCompileError("Illegal identifier name:" + word, rowCount));
                                    }
                                    token = TokenFactory.createIdentifierToken(rowCount, word);
                                }
                                break;
                            case REAL_LITERAL:
                                token = TokenFactory.createLiteralToken(TokenType.REAL_LITERAL, rowCount, ConstValueTable.getConstValueTable().getRealIndex(word));
                                break;
                            case INT_LITERAL:
                                token = TokenFactory.createLiteralToken(TokenType.INT_LITERAL, rowCount, ConstValueTable.getConstValueTable().getIntegerIndex(word));
                                break;
                            case STRING_LITERAL:
                                // 识别得到的单元一定符合"literal"的格式，提取出literal
                                String literal = builder.length() > 2 ? builder.substring(1, builder.length()-1) : "";
                                token = TokenFactory.createLiteralToken(TokenType.STRING_LITERAL, rowCount, ConstValueTable.getConstValueTable().getStringIndex(literal));
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
                        errors.add(Error.createCompileError("Can not recognize symbol: " + word, rowCount));
                    }

                }
            }

        }

        if (errors.size() > 0){
            Error.printErrorList(errors);
            return null;
        }

        if (Configure.isPrintTokens){
            System.out.println(outputTokens);
        }

        return outputTokens;
    }
}
