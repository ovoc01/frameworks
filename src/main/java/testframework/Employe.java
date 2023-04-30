package testframework;


import etu2074.framework.controller.ModelView;
import etu2074.framework.url.Link;

public class Employe {
    @Link(url = "try")
    public ModelView controller(){
        System.out.println("nety");
        return new ModelView("index.jsp");
    }
}
