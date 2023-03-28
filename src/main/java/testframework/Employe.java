package testframework;


import etu2074.framework.controller.Model_view;
import etu2074.framework.url.Link;

public class Employe {
    @Link(url = "try")
    public Model_view controller(){
        System.out.println("nety");
        return new Model_view("index.jsp");
    }

    public static <T> T dynamicCast(Object obj, Class<T> cls) {
        return cls.cast(obj);
    }
}
