package com.fforkboat.interpreter;

import com.fforkboat.common.*;
import com.fforkboat.common.Error;
import com.fforkboat.parser.container.SyntaxTreeContainer;
import com.fforkboat.parser.tree.SyntaxTreeBranchNode;
import com.fforkboat.parser.tree.SyntaxTreeLeafNode;
import com.fforkboat.parser.tree.SyntaxTreeNode;
import com.fforkboat.program.ConstValueTable;
import com.fforkboat.program.FunctionTable;
import com.fforkboat.scanner.token.IdentifierToken;
import com.fforkboat.scanner.token.LiteralToken;
import com.fforkboat.scanner.token.Token;
import com.fforkboat.scanner.token.TokenType;

import java.io.DataOutputStream;
import java.util.*;

public class BranchNodeActionProvider {
    private BranchNodeActionProvider(){}
    private static Map<String, BranchNodeAction> actionMap = new HashMap<>();

    static BranchNodeAction getAction(String branchName){
        return actionMap.get(branchName);
    }

    static {
        actionMap.put("S", children -> {
            SyntaxTreeNode firstChild = children.get(0);

            if (firstChild instanceof SyntaxTreeBranchNode){
                SyntaxTreeBranchNode branch = (SyntaxTreeBranchNode) firstChild;

                // 当第一个节点是A时，表示变量声明语句
                if (branch.getName().equals("A")){
                    Identifier identifier = (Identifier) actionMap.get("A").act(branch.getChildren());

                    // 看声明的是否是数组，并看是否给出了数组的大小
                    boolean isArray = false;
                    int arraySize =  -1;
                    SyntaxTreeBranchNode A1 = (SyntaxTreeBranchNode) branch.getChildren().get(1);
                    SyntaxTreeBranchNode A2 = (SyntaxTreeBranchNode) A1.getChildren().get(1);
                    if (A2.getChildren().size() > 0){
                        isArray = true;
                        SyntaxTreeBranchNode A3 = (SyntaxTreeBranchNode) A2.getChildren().get(1);
                        if (A3.getChildren().size() != 0){
                            SyntaxTreeBranchNode W1 = (SyntaxTreeBranchNode) A3.getChildren().get(0);
                            Object value = actionMap.get("W1").act(W1.getChildren());
                            if (value == null)
                                return null;
                            if (!(value instanceof Integer)){
                                InterpreterContext.throwError(
                                        Error.createRuntimeError("Array size is not an integer",
                                                branch.getLineIndexOfSourceProgramOfFirstToken())
                                );
                                return null;
                            }
                            arraySize = (int) value;
                        }
                    }

                    SyntaxTreeBranchNode B = ((SyntaxTreeBranchNode)children.get(1));
                    // 如果B的孩子数量为0，说明仅仅是声明变量，不需要赋值，而声明变量已经在语法分析中做过了，所以这里可以直接返回
                    if (B.getChildren().size() == 0){
                        if (!isArray)
                            return null;

                        // 当声明了数组，没有设置数组的初始大小，而此时又没有赋值语句，抛出错误
                        if (arraySize == -1){
                            InterpreterContext.throwError(Error.createRuntimeError("No initial value for array.", branch.getLineIndexOfSourceProgramOfFirstToken()));
                        }
                        // 当声明了数组，且设置了数组大小时对数组进行初始化
                        else{
                            switch (identifier.getDataType()){
                                case STRING_ARRAY:
                                    identifier.setValue(new String[arraySize]);
                                    break;
                                case BOOL_ARRAY:
                                    identifier.setValue(new boolean[arraySize]);
                                    break;
                                case INT_ARRAY:
                                    identifier.setValue(new int[arraySize]);
                                    break;
                                case REAL_ARRAY:
                                    identifier.setValue(new double[arraySize]);
                                    break;
                                default:
                                    throw new IllegalStateException();
                            }
                        }
                        return null;
                    }

                    Object value = actionMap.get("B").act(B.getChildren());
                    // 当value等于null，说明在获取值的过程中发生了异常，异常已经在发生处抛出，这里直接返回就好
                    if (value == null)
                        return null;

                    // 为非数组变量赋值的情况
                    if (!isArray){
                        // 如果值的类型和变量类型不兼容就抛出异常
                        if (!identifier.setValue(value)){
                            InterpreterContext.throwError(
                                    Error.createRuntimeError("Incompatible value for identifier '" + identifier.getName() + "'",
                                            branch.getLineIndexOfSourceProgramOfFirstToken())
                            );
                        }
                        return null;
                    }

                    // 为数组赋值的情况
                    Object array;
                    if (value instanceof Integer[]){
                        Integer[] tmpArray = (Integer[]) value;
                        if (arraySize == -1)
                            arraySize = tmpArray.length;

                        if (tmpArray.length > arraySize){
                            InterpreterContext.throwError(
                                    Error.createRuntimeError("Too many initial elements for array",
                                            branch.getLineIndexOfSourceProgramOfFirstToken())
                            );
                            return null;
                        }

                        array = new Integer[arraySize];
                        Arrays.fill((Integer[])array, 0);
                        System.arraycopy(tmpArray, 0, array, 0, tmpArray.length);

                    }
                    else if (value instanceof Double[]){
                        Double[] tmpArray = (Double[]) value;
                        if (arraySize == -1)
                            arraySize = tmpArray.length;

                        if (tmpArray.length > arraySize){
                            InterpreterContext.throwError(
                                    Error.createRuntimeError("Too many initial elements for array",
                                            branch.getLineIndexOfSourceProgramOfFirstToken())
                            );
                            return null;
                        }

                        array = new Double[arraySize];
                        Arrays.fill((Double[])array, 0.0);
                        System.arraycopy(tmpArray, 0, array, 0, tmpArray.length);

                    }
                    else if (value instanceof Boolean[]){
                        Boolean[] tmpArray = (Boolean[]) value;
                        if (arraySize == -1)
                            arraySize = tmpArray.length;

                        if (tmpArray.length > arraySize){
                            InterpreterContext.throwError(
                                    Error.createRuntimeError("Too many initial elements for array",
                                            branch.getLineIndexOfSourceProgramOfFirstToken())
                            );
                            return null;
                        }

                        array = new Boolean[arraySize];
                        Arrays.fill((Boolean[])array, false);
                        System.arraycopy(tmpArray, 0, array, 0, tmpArray.length);

                    }
                    else if (value instanceof String[]){
                        String[] tmpArray = (String[]) value;
                        if (arraySize == -1)
                            arraySize = tmpArray.length;

                        if (tmpArray.length > arraySize){
                            InterpreterContext.throwError(
                                    Error.createRuntimeError("Too many initial elements for array",
                                            branch.getLineIndexOfSourceProgramOfFirstToken())
                            );
                            return null;
                        }

                        array = new String[arraySize];
                        Arrays.fill((String[])array, "");
                        System.arraycopy(tmpArray, 0, array, 0, tmpArray.length);

                    }
                    else {
                        InterpreterContext.throwError(
                                Error.createRuntimeError("Incompatible value for identifier '" + identifier.getName() + "'",
                                        branch.getLineIndexOfSourceProgramOfFirstToken())
                        );
                        return null;
                    }

                    // 如果数组的类型和变量类型不兼容就抛出异常
                    if (!identifier.setValue(array)){
                        InterpreterContext.throwError(
                                Error.createRuntimeError("Incompatible value for identifier '" + identifier.getName() + "'",
                                        branch.getLineIndexOfSourceProgramOfFirstToken())
                        );
                    }
                }
            }
            else {
                // TODO
                SyntaxTreeLeafNode leaf = (SyntaxTreeLeafNode) firstChild;
                // 当语句为return语句时
                if (leaf.getToken().getTokenType() == TokenType.RETURN){
                    return actionMap.get("W1").act(((SyntaxTreeBranchNode)children.get(1)).getChildren());
                }
                // 当语句为continue语句时
                if (leaf.getToken().getTokenType() == TokenType.CONTINUE){
                    InterpreterContext.setEncounterContinue(true);
                    InterpreterContext.setContinueToExecute(false);
                    return null;
                }
                // 当语句为break语句时
                if (leaf.getToken().getTokenType() == TokenType.BREAK){
                    InterpreterContext.setContinueToExecute(false);
                    InterpreterContext.setEncounterBreak(true);
                    return null;
                }
                // 当语句为赋值或者函数调用时
                if (leaf.getToken().getTokenType() == TokenType.IDENTIFIER){
                    String identifierName = ((IdentifierToken)leaf.getToken()).getIdentifierName();

                    SyntaxTreeBranchNode M = (SyntaxTreeBranchNode) children.get(1);
                    Token firstTokenOfM = ((SyntaxTreeLeafNode)M.getChildren().get(0)).getToken();
                    // 当语句为变量赋值时
                    if (firstTokenOfM.getTokenType() == TokenType.ASSIGN){
                        Identifier identifier = InterpreterContext.getCurrentContainer().getIdentifier(identifierName);

                        SyntaxTreeBranchNode W1 = (SyntaxTreeBranchNode) M.getChildren().get(1);
                        Object value = actionMap.get("W1").act(W1.getChildren());
                        if (value == null)
                            return null;


                        if (!identifier.setValue(value)){
                            InterpreterContext.throwError(
                                    Error.createRuntimeError("Incompatible value for identifier '"+ identifierName + "'.", leaf.getToken().getLineIndexOfSourceProgram())
                            );
                            return null;
                        }
                    }
                    if (firstTokenOfM.getTokenType() == TokenType.LEFT_BRACKET){
                        Identifier identifier = InterpreterContext.getCurrentContainer().getIdentifier(identifierName);
                        Object[] array = (Object[]) identifier.getValue();

                        SyntaxTreeBranchNode W1ForIndex = (SyntaxTreeBranchNode) M.getChildren().get(1);
                        Object index = actionMap.get("W1").act(W1ForIndex.getChildren());
                        if (index == null)
                            return null;
                        if (!(index instanceof Integer)){
                            InterpreterContext.throwError(
                                    Error.createRuntimeError("Array index is not an integer", leaf.getToken().getLineIndexOfSourceProgram())
                            );
                            return null;
                        }
                        if ((int) index >= array.length){
                            InterpreterContext.throwError(
                                    Error.createRuntimeError("Array index out of bound", leaf.getToken().getLineIndexOfSourceProgram())
                            );
                            return null;
                        }

                        SyntaxTreeBranchNode W1ForValue = (SyntaxTreeBranchNode) M.getChildren().get(4);
                        Object value = actionMap.get("W1").act(W1ForValue.getChildren());
                        if (value == null)
                            return null;
                        if (value.getClass() != array.getClass().getComponentType()){
                            InterpreterContext.throwError(
                                    Error.createRuntimeError("Incompatible value for array element", leaf.getToken().getLineIndexOfSourceProgram())
                            );
                            return null;
                        }
                        array[(int)index] = value;
                    }
                    // 当为函数调用时
                    else if (firstTokenOfM.getTokenType() == TokenType.LEFT_PARENTHESIS){
                        BaseFunction function = FunctionTable.getInstance().getFunction(identifierName);
                        if (function == null){
                            InterpreterContext.throwError(
                                    Error.createRuntimeError("Function '" + identifierName + "' is not defined.", leaf.getToken().getLineIndexOfSourceProgram())
                            );
                            return null;
                        }

                        SyntaxTreeBranchNode M1 = (SyntaxTreeBranchNode) M.getChildren().get(1);
                        List<Object> arguments = new ArrayList<>();
                        if (M1.getChildren().size() != 0){
                            SyntaxTreeBranchNode W1 = (SyntaxTreeBranchNode) M1.getChildren().get(0);
                            SyntaxTreeBranchNode M2 = (SyntaxTreeBranchNode) M1.getChildren().get(1);

                            Object value = actionMap.get("W1").act(W1.getChildren());
                            if (value == null)
                                return null;

                            arguments.add(value);
                            while (M2.getChildren().size() != 0){
                                W1 = (SyntaxTreeBranchNode) M2.getChildren().get(1);

                                value = actionMap.get("W1").act(W1.getChildren());
                                if (value == null)
                                    return null;
                                arguments.add(value);

                                M2 = (SyntaxTreeBranchNode) M2.getChildren().get(2);
                            }
                        }

                        if (function.getArgumentTypes().size() != arguments.size()){
                            InterpreterContext.throwError(
                                    Error.createRuntimeError("Incorrect argument number.", leaf.getToken().getLineIndexOfSourceProgram())
                            );
                            return null;
                        }

                        // 判断参数类型是否正确
                        for (int i = 0; i < arguments.size(); i++) {
                            DataType functionArgumentType = function.getArgumentTypes().get(i);
                            Object realArgument = arguments.get(i);
                            if (!DataType.isDataTypeMatches(functionArgumentType, realArgument.getClass())){
                                InterpreterContext.throwError(
                                        Error.createRuntimeError("Unmatched argument type.", leaf.getToken().getLineIndexOfSourceProgram())
                                );
                                return null;
                            }
                        }

                        // 当是内置函数时
                        if (function instanceof BuiltInFunction){
                            BuiltInFunction builtInFunction = (BuiltInFunction) function;
                            builtInFunction.call(arguments);
                        }
                        // 当是自定义函数时
                        else{
                            CustomFunction customFunction = (CustomFunction) function;
                            SyntaxTreeContainer functionSyntaxTreesContainer = customFunction.getSyntaxTreeContainer();
                            for (int i = 0; i < arguments.size(); i++) {
                                String name = customFunction.getArgumentNames().get(i);
                                functionSyntaxTreesContainer.getIdentifier(name).setValue(arguments.get(i));
                            }

                            SyntaxTreeContainer currentContainer = InterpreterContext.getCurrentContainer();
                            InterpreterContext.setCurrentContainer(functionSyntaxTreesContainer);
                            Interpreter.interpret(functionSyntaxTreesContainer);
                            InterpreterContext.setCurrentContainer(currentContainer);
                        }

                        return null;
                    }
                }
            }

            return null;
        });

        actionMap.put("A", children -> {
            SyntaxTreeBranchNode A1 = (SyntaxTreeBranchNode) children.get(1);
            SyntaxTreeLeafNode ID = (SyntaxTreeLeafNode) A1.getChildren().get(0);
            String identifierName = ((IdentifierToken) ID.getToken()).getIdentifierName();
            return InterpreterContext.getCurrentContainer().getIdentifier(identifierName);
        });

        actionMap.put("B", children -> {
            SyntaxTreeBranchNode W = (SyntaxTreeBranchNode) children.get(1);
            return actionMap.get("W").act(W.getChildren());
        });

        actionMap.put("W", children -> {
            SyntaxTreeNode firstChild = children.get(0);
            if (firstChild instanceof SyntaxTreeBranchNode){
                SyntaxTreeBranchNode branch = (SyntaxTreeBranchNode) firstChild;
                return actionMap.get("W1").act(branch.getChildren());
            }

            return actionMap.get("W2").act(((SyntaxTreeBranchNode)children.get(1)).getChildren());
        });

        actionMap.put("W1", children -> {
            SyntaxTreeNode firstChild = children.get(0);

            if (firstChild instanceof SyntaxTreeLeafNode){
                Token token = ((SyntaxTreeLeafNode)firstChild).getToken();
                if (token.getTokenType() == TokenType.STRING_LITERAL)
                    return ConstValueTable.getConstValueTable().getString(((LiteralToken)token).getPointer());
                else if (token.getTokenType() == TokenType.BOOL_LITERAL)
                    return ConstValueTable.getConstValueTable().getBool(((LiteralToken)token).getPointer());
            }
            else {
                SyntaxTreeBranchNode branch = (SyntaxTreeBranchNode) firstChild;
                return actionMap.get(branch.getName()).act(branch.getChildren());
            }

            // 不应该到这个位置
            throw new IllegalStateException();
        });

        actionMap.put("W2", children -> {
            SyntaxTreeBranchNode W1 = (SyntaxTreeBranchNode) children.get(0);
            SyntaxTreeBranchNode W21 = (SyntaxTreeBranchNode) children.get(1);

            Object value = actionMap.get("W1").act(W1.getChildren());
            if (value == null)
                return null;

            List<Object> values = new ArrayList<>();
            values.add(value);
            while (W21.getChildren().size() != 0){
                W1 = (SyntaxTreeBranchNode) W21.getChildren().get(1);
                value = actionMap.get("W1").act(W1.getChildren());
                if (value == null)
                    return null;
                values.add(value);
                W21 = (SyntaxTreeBranchNode) W21.getChildren().get(2);
            }

            Class arrayClass = values.get(0).getClass();
            Object[] resultArray = (Object[]) java.lang.reflect.Array.newInstance(arrayClass, values.size());
            for (int i = 0; i < values.size(); i++) {
                Object object = values.get(i);
                if (!arrayClass.isInstance(object)){
                    InterpreterContext.throwError(Error.createRuntimeError("Elements of the array have inconsistent types", W1.getLineIndexOfSourceProgramOfFirstToken()));
                    return null;
                }
                resultArray[i] = object;
            }

            return resultArray;
        });

        actionMap.put("N", children -> {
            SyntaxTreeBranchNode X = (SyntaxTreeBranchNode) children.get(0);
            SyntaxTreeBranchNode Z = (SyntaxTreeBranchNode) children.get(1);
            if (Z.getChildren().size() == 0){
                return actionMap.get("X").act(X.getChildren());
            }
            else{
                Object value1 = actionMap.get("X").act(X.getChildren());
                Object value2 = actionMap.get("X").act(((SyntaxTreeBranchNode)Z.getChildren().get(1)).getChildren());
                Token token = ((SyntaxTreeLeafNode)Z.getChildren().get(0)).getToken();
                if (value1 == null || value2 == null)
                    return null;

                if (value1 instanceof Integer){
                    if (value2 instanceof Integer){
                        switch (token.getTokenType()){
                            case GREATER:
                                return ((Integer) value1) > ((Integer) value2);
                            case GREATER_EQUAL:
                                return ((Integer) value1) >= ((Integer) value2);
                            case LESS:
                                return ((Integer) value1) < ((Integer) value2);
                            case LESS_EQUAL:
                                return ((Integer) value1) <= ((Integer) value2);
                            case EQUAL:
                                return value1.equals(value2);
                            case UNEQUAL:
                                return !value1.equals(value2);
                            default:
                                throw new IllegalStateException();
                        }
                    }
                    if (value2 instanceof Double){
                        switch (token.getTokenType()){
                            case GREATER:
                                return ((Integer) value1) > ((Double) value2);
                            case GREATER_EQUAL:
                                return ((Integer) value1) >= ((Double) value2);
                            case LESS:
                                return ((Integer) value1) < ((Double) value2);
                            case LESS_EQUAL:
                                return ((Integer) value1) <= ((Double) value2);
                            case EQUAL:
                                return value1.equals(value2);
                            case UNEQUAL:
                                return !value1.equals(value2);
                            default:
                                throw new IllegalStateException();
                        }
                    }
                }

                if (value1 instanceof Double){
                    if (value2 instanceof Integer){
                        switch (token.getTokenType()){
                            case GREATER:
                                return ((Double) value1) > ((Integer) value2);
                            case GREATER_EQUAL:
                                return ((Double) value1) >= ((Integer) value2);
                            case LESS:
                                return ((Double) value1) < ((Integer) value2);
                            case LESS_EQUAL:
                                return ((Double) value1) <= ((Integer) value2);
                            case EQUAL:
                                return value1.equals(value2);
                            case UNEQUAL:
                                return !value1.equals(value2);
                            default:
                                throw new IllegalStateException();
                        }
                    }
                    if (value2 instanceof Double){
                        switch (token.getTokenType()){
                            case GREATER:
                                return ((Double) value1) > ((Double) value2);
                            case GREATER_EQUAL:
                                return ((Double) value1) >= ((Double) value2);
                            case LESS:
                                return ((Double) value1) < ((Double) value2);
                            case LESS_EQUAL:
                                return ((Double) value1) <= ((Double) value2);
                            case EQUAL:
                                return value1.equals(value2);
                            case UNEQUAL:
                                return !value1.equals(value2);
                            default:
                                throw new IllegalStateException();
                        }
                    }
                }

                InterpreterContext.throwError(Error.createRuntimeError("Illegal operands.", token.getLineIndexOfSourceProgram()));
                return null;

            }
        });

        actionMap.put("X", children -> {
            List inOrderExp = new ArrayList();
            SyntaxTreeBranchNode X1 = (SyntaxTreeBranchNode) children.get(0);
            SyntaxTreeBranchNode X2 = (SyntaxTreeBranchNode) children.get(1);
            boolean b1 = traversal(X1, inOrderExp);
            boolean b2 = traversal(X2, inOrderExp);
            if (!(b1 && b2))
                return null;

            return calculateInorderExp(inOrderExp, X1.getLineIndexOfSourceProgramOfFirstToken());
        });

        actionMap.put("X3", children -> {
            SyntaxTreeNode firstChild = children.get(0);

            if (firstChild instanceof SyntaxTreeLeafNode){
                Token token = ((SyntaxTreeLeafNode)firstChild).getToken();

                if (token.getTokenType() == TokenType.LEFT_PARENTHESIS){
                    SyntaxTreeBranchNode X = (SyntaxTreeBranchNode) children.get(1);
                    Object value = actionMap.get("X").act(X.getChildren());
                    if (value == null)
                        return null;

                    return value;
                }
                else if (token.getTokenType() == TokenType.INT_LITERAL){
                    return ConstValueTable.getConstValueTable().getInteger(((LiteralToken)token).getPointer());
                }
                else if (token.getTokenType() == TokenType.REAL_LITERAL){
                    return ConstValueTable.getConstValueTable().getReal(((LiteralToken)token).getPointer());
                }
                else if (token.getTokenType() == TokenType.ADD){
                    SyntaxTreeBranchNode X31 = (SyntaxTreeBranchNode) children.get(1);
                    Object object = actionMap.get("X31").act(X31.getChildren());
                    if (!(object instanceof Integer) && !(object instanceof Double)){
                        InterpreterContext.throwError(Error.createRuntimeError("Illegal operand.", token.getLineIndexOfSourceProgram()));
                        return null;
                    }
                    return object;
                }
                else if(token.getTokenType() == TokenType.SUBTRACT){
                    SyntaxTreeBranchNode X31 = (SyntaxTreeBranchNode) children.get(1);
                    Object object = actionMap.get("X31").act(X31.getChildren());
                    if (object instanceof Integer){
                        return - (Integer)object;
                    }
                    else if (object instanceof Double){
                        return - (Double)object;
                    }

                    InterpreterContext.throwError(Error.createRuntimeError("Illegal operand.", token.getLineIndexOfSourceProgram()));
                    return null;
                }
            }
            else{
                SyntaxTreeBranchNode UID = (SyntaxTreeBranchNode) firstChild;
                Object value = actionMap.get("UID").act(UID.getChildren());
                if (value == null)
                    return null;

                return value;
            }

            throw new IllegalStateException();
        });

        actionMap.put("X31", children -> {
            SyntaxTreeNode firstChild = children.get(0);
            if (firstChild instanceof SyntaxTreeLeafNode){
                Token token = ((SyntaxTreeLeafNode)firstChild).getToken();
                if (token.getTokenType() == TokenType.INT_LITERAL)
                    return ConstValueTable.getConstValueTable().getInteger(((LiteralToken)token).getPointer());
                if (token.getTokenType() == TokenType.REAL_LITERAL)
                    return ConstValueTable.getConstValueTable().getReal(((LiteralToken)token).getPointer());
            }
            else{
                SyntaxTreeBranchNode UID = (SyntaxTreeBranchNode) firstChild;
                return actionMap.get("UID").act(UID.getChildren());
            }

            throw new IllegalStateException();
        });

        actionMap.put("UID", children -> {
            SyntaxTreeLeafNode ID = (SyntaxTreeLeafNode) children.get(0);
            SyntaxTreeBranchNode U1 = (SyntaxTreeBranchNode) children.get(1);
            String identifierName = ((IdentifierToken)ID.getToken()).getIdentifierName();

            if (U1.getChildren().size() == 0){
                Identifier identifier = InterpreterContext.getCurrentContainer().getIdentifier(identifierName);
                Object value = identifier.getValue();
                if (value == null){
                    InterpreterContext.throwError(
                            Error.createRuntimeError("Variable '" + identifierName + "' is not initialized", ID.getToken().getLineIndexOfSourceProgram())
                    );
                    return null;
                }

                return value;
            }

            Token firstTokenOfU1 = ((SyntaxTreeLeafNode)U1.getChildren().get(0)).getToken();
            if (firstTokenOfU1.getTokenType() == TokenType.LEFT_BRACKET){
                SyntaxTreeBranchNode X = (SyntaxTreeBranchNode) U1.getChildren().get(1);
                Object index = actionMap.get("X").act(X.getChildren());
                if (!(index instanceof Integer)){
                    InterpreterContext.throwError(
                            Error.createRuntimeError("Illegal operand.", ID.getToken().getLineIndexOfSourceProgram())
                    );
                    return null;
                }
                Identifier identifier = InterpreterContext.getCurrentContainer().getIdentifier(identifierName);
                if (identifier.getDataType() == DataType.BOOL_ARRAY){
                    Boolean[] array = (Boolean[]) identifier.getValue();
                    if ((Integer)index >= array.length){
                        InterpreterContext.throwError(
                                Error.createRuntimeError("Index out of bound", ID.getToken().getLineIndexOfSourceProgram())
                        );
                        return null;
                    }
                    return array[(Integer)index];
                }
                if (identifier.getDataType() == DataType.STRING_ARRAY){
                    String[] array = (String[]) identifier.getValue();
                    if ((Integer)index >= array.length){
                        InterpreterContext.throwError(
                                Error.createRuntimeError("Index out of bound", ID.getToken().getLineIndexOfSourceProgram())
                        );
                        return null;
                    }
                    return array[(Integer)index];
                }
                if (identifier.getDataType() == DataType.INT_ARRAY){
                    Integer[] array = (Integer[]) identifier.getValue();
                    if ((Integer)index >= array.length){
                        InterpreterContext.throwError(
                                Error.createRuntimeError("Index out of bound", ID.getToken().getLineIndexOfSourceProgram())
                        );
                        return null;
                    }
                    return array[(Integer)index];
                }
                if (identifier.getDataType() == DataType.REAL_ARRAY){
                    Double[] array = (Double[]) identifier.getValue();
                    if ((Integer)index >= array.length){
                        InterpreterContext.throwError(
                                Error.createRuntimeError("Index out of bound", ID.getToken().getLineIndexOfSourceProgram())
                        );
                        return null;
                    }
                    return array[(Integer)index];
                }

                InterpreterContext.throwError(
                        Error.createRuntimeError("Identifier '" + identifierName + "' is not an array.", ID.getToken().getLineIndexOfSourceProgram())
                );
                return null;
            }
            else if (firstTokenOfU1.getTokenType() == TokenType.LEFT_PARENTHESIS){
                BaseFunction function = FunctionTable.getInstance().getFunction(identifierName);
                if (function == null){
                    InterpreterContext.throwError(
                            Error.createRuntimeError("Function '" + identifierName + "' is not defined.", ID.getToken().getLineIndexOfSourceProgram())
                    );
                    return null;
                }
                if (function.getReturnType() == ReturnType.VOID){
                    InterpreterContext.throwError(
                            Error.createRuntimeError("Function (" + identifierName + ") with no return value is not allowed here.", ID.getToken().getLineIndexOfSourceProgram())
                    );
                    return null;
                }

                // 构建参数列表
                List<Object> arguments = new ArrayList<>();
                SyntaxTreeBranchNode U10 = (SyntaxTreeBranchNode) U1.getChildren().get(1);
                if (U10.getChildren().size() != 0){
                    SyntaxTreeBranchNode U11 = (SyntaxTreeBranchNode) U10.getChildren().get(0);
                    SyntaxTreeBranchNode W1 = (SyntaxTreeBranchNode) U11.getChildren().get(0);
                    SyntaxTreeBranchNode U12 = (SyntaxTreeBranchNode) U11.getChildren().get(1);

                    Object value = actionMap.get("W1").act(W1.getChildren());
                    if (value == null)
                        return null;
                    arguments.add(value);

                    while (U12.getChildren().size() != 0){
                        U11 = (SyntaxTreeBranchNode) U12.getChildren().get(1);
                        W1 = (SyntaxTreeBranchNode) U11.getChildren().get(0);
                        value = actionMap.get("W1").act(W1.getChildren());
                        if (value == null)
                            return null;
                        arguments.add(value);
                        U12 = (SyntaxTreeBranchNode) U11.getChildren().get(1);
                    }
                }

                // 判断参数数量是否正确
                if (function.getArgumentTypes().size() != arguments.size()){
                    InterpreterContext.throwError(
                            Error.createRuntimeError("Incorrect argument number.", ID.getToken().getLineIndexOfSourceProgram())
                    );
                    return null;
                }

                // 判断参数类型是否正确
                for (int i = 0; i < arguments.size(); i++) {
                    DataType functionArgumentType = function.getArgumentTypes().get(i);
                    Object realArgument = arguments.get(i);
                    if (!DataType.isDataTypeMatches(functionArgumentType, realArgument.getClass())){
                        InterpreterContext.throwError(
                                Error.createRuntimeError("Unmatched argument type.", ID.getToken().getLineIndexOfSourceProgram())
                        );
                        return null;
                    }
                }

                // 当是内置函数的时候
                if (function instanceof BuiltInFunction){
                    BuiltInFunction builtInFunction = (BuiltInFunction) function;
                    return builtInFunction.call(arguments);
                }
                // 当是自定义函数的时候
                else{
                    CustomFunction customFunction = (CustomFunction) function;
                    SyntaxTreeContainer functionSyntaxTreesContainer = customFunction.getSyntaxTreeContainer();
                    for (int i = 0; i < arguments.size(); i++) {
                        String name = customFunction.getArgumentNames().get(i);
                        functionSyntaxTreesContainer.getIdentifier(name).setValue(arguments.get(i));
                    }

                    SyntaxTreeContainer currentContainer = InterpreterContext.getCurrentContainer();
                    InterpreterContext.setCurrentContainer(functionSyntaxTreesContainer);
                    Object returnValue =  Interpreter.interpretFunctionWithReturnValue(functionSyntaxTreesContainer, ID.getToken().getLineIndexOfSourceProgram());
                    InterpreterContext.setCurrentContainer(currentContainer);

                    return returnValue;
                }

            }

            return null;
        });
    }

    @SuppressWarnings("unchecked")
    private static boolean traversal(SyntaxTreeBranchNode branch, List exp){
        if (branch.getName().equals("X3")){
            Object value = actionMap.get("X3").act(branch.getChildren());
            if (value == null)
                return false;

            if (value instanceof Integer || value instanceof Double)
                exp.add(Number.createNumber(value));
            else
                exp.add(value);
            return true;
        }
        else if (branch.getName().equals("X1")) {
            boolean b1 = traversal((SyntaxTreeBranchNode) branch.getChildren().get(0), exp); // X3
            boolean b2 = traversal((SyntaxTreeBranchNode) branch.getChildren().get(1), exp); // X4
            return b1 && b2;
        }
        else if (branch.getName().equals("X2") && branch.getChildren().size() > 0){
            TokenType operator = ((SyntaxTreeLeafNode)branch.getChildren().get(0)).getToken().getTokenType();
            exp.add(operator);
            SyntaxTreeBranchNode X = (SyntaxTreeBranchNode) branch.getChildren().get(1);
            SyntaxTreeBranchNode X1 = (SyntaxTreeBranchNode) X.getChildren().get(0);
            SyntaxTreeBranchNode X2 = (SyntaxTreeBranchNode) X.getChildren().get(1);
            boolean b1 = traversal(X1, exp);
            boolean b2 = traversal(X2, exp);
            return b1 && b2;
        }
        else if (branch.getName().equals("X4") && branch.getChildren().size() > 0){
            TokenType operator = ((SyntaxTreeLeafNode)branch.getChildren().get(0)).getToken().getTokenType();
            exp.add(operator);
            SyntaxTreeBranchNode X1 = (SyntaxTreeBranchNode) branch.getChildren().get(1);
            return traversal(X1, exp);
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    private static Object calculateInorderExp(List inOrderExp, int lineCount){
        List postOrderExp = new ArrayList();
        Stack<TokenType> operators = new Stack<>();
        for (Object element :
                inOrderExp) {
            if (element instanceof TokenType){
                TokenType operator = (TokenType) element;
                switch (operator){
                    case ADD:
                    case SUBTRACT:
                        while (operators.size() != 0){
                            postOrderExp.add(operators.pop());
                        }
                        operators.push(operator);
                        break;
                    case MULTIPLY:
                    case DIVIDE:
                        if (operators.size() != 0){
                            TokenType topOperator = operators.peek();
                            if (topOperator == TokenType.MULTIPLY || topOperator == TokenType.DIVIDE){
                                postOrderExp.add(operators.pop());
                            }
                        }
                        operators.push(operator);
                        break;
                    default:
                        throw new IllegalStateException();
                }

            }
            else {
                postOrderExp.add(element);
            }
        }
        while (operators.size() != 0){
            postOrderExp.add(operators.pop());
        }

        Stack operands = new Stack();
        for (Object element :
                postOrderExp) {
            if (element instanceof TokenType){
                TokenType operator = (TokenType) element;
                if (!(operands.peek() instanceof Number)){
                    InterpreterContext.throwError(Error.createRuntimeError("Illegal operand.", lineCount));
                    return null;
                }
                Number op2 = (Number) operands.pop();
                if (!(operands.peek() instanceof Number)){
                    InterpreterContext.throwError(Error.createRuntimeError("Illegal operand.", lineCount));
                    return null;
                }
                Number op1 = (Number) operands.pop();

                switch (operator){
                    case ADD:
                        operands.push(Number.add(op1, op2));
                        break;
                    case SUBTRACT:
                        operands.push(Number.subtract(op1, op2));
                        break;
                    case MULTIPLY:
                        operands.push(Number.multiply(op1, op2));
                        break;
                    case DIVIDE:
                        Number value = Number.divide(op1, op2);
                        if (value == null){
                            InterpreterContext.throwError(Error.createRuntimeError("Divisor can not be 0.", lineCount));
                            return null;
                        }
                        operands.push(value);
                        break;
                    default:
                        throw new IllegalStateException();
                }
            }
            else{
                operands.push(element);
            }
        }

        Object operand = operands.pop();
        if (operand instanceof Number)
            return ((Number)operand).getValue();
        else
            return operand;
    }

}
