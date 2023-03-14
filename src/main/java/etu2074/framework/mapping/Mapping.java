package etu2074.framework.mapping;

import java.lang.reflect.Method;

public class Mapping {
    String class_name;
    Method method;

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }
    public Mapping(String class_name,Method method){
        setClass_name(class_name);
        setMethod(method);
    }
    public Mapping(){

    }
}
