package etu2074.framework.controller;

import java.util.HashMap;

public class Model_view {
    String view;
    private HashMap<String,Object> data = new HashMap<>();

    public HashMap<String, Object> getData() {
        return data;
    }

    public void addItem(String identifier,Object data){
        getData().put(identifier,data);
    }
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public Model_view(String view){
        setView(view);
    }

    public Model_view(){

    }
}
