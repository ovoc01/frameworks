package testframework;


import etu2074.framework.controller.ModelView;
import etu2074.framework.url.Link;
import etu2074.framework.url.Scope;
import etu2074.framework.url.Session;

import java.util.HashMap;

@Scope("singleton")
public class SessionR {

    HashMap<String,Object> session;

    public HashMap<String, Object> getSession() {
        return session;
    }

    public void setSession(HashMap<String, Object> session) {
        this.session = session;
    }

    @Link(url = "session-add.do")
    @Session
    public ModelView addSession(){
        System.out.println(session);
        return new ModelView("index.jsp");
    }
}
