package testframework;

import etu2074.framework.url.*;
import etu2074.framework.controller.ModelView;

@Scope("singleton")
public class Yes {
    private Integer i= 0;
    public Yes() {

    }

    @Link(url = "showI")
    public ModelView showI(){
        i++;
        ModelView mv = new ModelView();
        mv.addItem("i", i);
        mv.setView("Test.jsp");
        System.out.println(this);
        return mv;
    }
}
