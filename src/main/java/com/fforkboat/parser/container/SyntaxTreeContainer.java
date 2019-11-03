package com.fforkboat.parser.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fforkboat.common.*;


/**
 * 语法树容器的基类
 * */
public abstract class SyntaxTreeContainer implements SyntaxTreeContainerComponent {
    private List<SyntaxTreeContainerComponent> components = new ArrayList<>();
    private SyntaxTreeContainer parent;
    private boolean isRootContainer;

    // 一个语法树容器和其子容器中所有可访问的标识符的集合
    private Map<String, Identifier> identifiers = new HashMap<>();

    SyntaxTreeContainer(SyntaxTreeContainer parent, boolean isRootContainer){
        this.parent = parent;
        this.isRootContainer = isRootContainer;
    }

    public void addComponent(SyntaxTreeContainerComponent component){
        components.add(component);
    }

    public void addIdentifier(String name, DataType dataType){
        if (identifiers.containsKey(name)){
            // TODO:出错处理
            return;
        }
        SyntaxTreeContainer tmp = parent;
        while (tmp != null){
            if (tmp.identifiers.containsKey(name)){
                // TODO:出错处理
                return;
            }
            tmp = tmp.parent;
        }

        identifiers.put(name, new Identifier(dataType));
    }

    public Identifier getIdentifier(String name){
        if (identifiers.containsKey(name)){
            return identifiers.get(name);
        }

        SyntaxTreeContainer tmp = parent;
        while (tmp != null){
            if (tmp.identifiers.containsKey(name)){
                return tmp.identifiers.get(name);
            }
            tmp = tmp.parent;
        }

        // TODO 出错处理
        return null;
    }

    public List<SyntaxTreeContainerComponent> getComponents() {
        return components;
    }

    public SyntaxTreeContainer getParent() {
        return parent;
    }

    public boolean isRootContainer() {
        return isRootContainer;
    }
}
