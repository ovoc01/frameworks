package testframework;

import etu2074.framework.url.Authentification;
import etu2074.framework.url.Link;
import etu2074.framework.url.Scope;
import etu2074.framework.controller.ModelView;

@Scope("singleton")
public class Yes {
    private Integer i= 0;
    public Yes() {

    }


    @Authentification(auth = "admin")
    @Link(url = "showI.do")
    public ModelView showI(){
        i++;
        ModelView mv = new ModelView();
        mv.addItem("i", i);
        mv.setView("Test.jsp");
        System.out.println(this);
        return mv;
    }

    @Link(url = "login.do")
    public ModelView login(){
        ModelView modelView = new ModelView();
        modelView.getSessions().put("profile","root");
        modelView.getSessions().put("request","nety");
        modelView.setView("Home.jsp");
        return modelView;
    }
}