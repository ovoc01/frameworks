package etu2074.framework.controller;

import java.util.HashMap;
import java.util.LinkedList;

public class ModelView {
    String view;
    private HashMap<String,Object> data = new HashMap<>();
    private String method= "";
    private HashMap<String,Object> sessions =new HashMap<>();

    private boolean isJson ;
    private boolean invalidateSession=false;
    private LinkedList<String> removeSession= new LinkedList<>();
    public ModelView(String view){
        setView(view);
    }

    public ModelView(){

    }

    public boolean isInvalidateSession() {
        return invalidateSession;
    }

    public void setInvalidateSession(boolean invalidateSession) {
        this.invalidateSession = invalidateSession;
    }

    public LinkedList<String> getRemoveSession() {
        return removeSession;
    }

    public void setRemoveSession(LinkedList<String> removeSession) {
        this.removeSession = removeSession;
    }

    public boolean isJson() {
        return isJson;
    }

    public void setJson(boolean json) {
        isJson = json;
    }

    public HashMap<String, Object> getSessions() {
        return sessions;
    }

    public void setSessions(HashMap<String, Object> sessions) {
        this.sessions = sessions;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

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


}
