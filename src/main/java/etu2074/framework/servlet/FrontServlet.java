package etu2074.framework.servlet;
import etu2074.framework.controller.Model_view;
import etu2074.framework.loader.Loader;
import etu2074.framework.mapping.Mapping;
import etu2074.framework.url.Link;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;


public class FrontServlet extends HttpServlet {
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Mapping> mappingUrl;

    public FrontServlet(){

    }
    public HashMap<String, Mapping> getMappingUrl() {
        return mappingUrl;
    }

    public void setMappingUrl(HashMap<String, Mapping> mappingUrl) {
        this.mappingUrl = mappingUrl;
    }

    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    public HttpServletResponse getHttpServletResponse() {
        return httpServletResponse;
    }

    public HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        try{
            super.init(servletConfig);
            String path = getInitParameter("pathos");
            mappingUrl = new HashMap<>();
            retrieveAllMappedMethod(path);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void retrieveAllMappedMethod(String paths) throws URISyntaxException, ClassNotFoundException {
        Set<Class> classSet = null;
        classSet = Loader.findAllClasses(paths);
        for(Class classes:classSet){
            Method[]methods = classes.getMethods();
            for (Method method:methods) {
                Link link = method.getAnnotation(Link.class);
                if(link!=null){
                    mappingUrl.put(link.url(),new Mapping(classes.getName(),method,classes));
                }
            }
        }
    }
    private void retrieveAllMappedMethod() throws URISyntaxException, ClassNotFoundException {
        Set<Class> classSet = null;
        classSet = Loader.findAllClasses("etu2074.framework.controller");
        for(Class classes:classSet){
            Method[]methods = classes.getMethods();
            for (Method method:methods) {
                Link link = method.getAnnotation(Link.class);
                if(link!=null){
                    mappingUrl.put(link.url(),new Mapping(classes.getName(),method,classes));
                }
            }
        }
    }

    public final void dispatch(String URL) {
        try {
            getHttpServletRequest().getRequestDispatcher(URL).forward(getHttpServletRequest(),getHttpServletResponse());
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    public void processRequest(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
        setHttpServletRequest(request);setHttpServletResponse(response);
        Vector<String> stringVector = retrieveRequestUrl(request);
        PrintWriter writer = response.getWriter();
        try{
            System.out.println("===>url"+retrieveRequestUrl(request));
            String values = request.getRequestURI();
            writer.println(values);
            writer.println("<br>");
            HashMap<String,Mapping> list = getMappingUrl();
            writer.println(list);
            Model_view modelView = redirection(request);
            if(modelView!=null){
                dispatch(modelView.getView());
            }
            redirection(request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private Model_view redirection(HttpServletRequest request) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Vector<String> links = retrieveRequestUrl(request);
        if(!links.isEmpty()){
            Mapping objectMapping = mappingUrl.get(links.get(0));
            if(objectMapping!=null){
                Object temp = objectMapping.getaClass().newInstance();
                Model_view model_view= (Model_view) temp.getClass().getMethod(objectMapping.getMethod().getName()).invoke(temp);
                return model_view;
            }
        }
        return null;
    }



    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
        processRequest(request,response);
    }

    @Override
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
        processRequest(request,response);
    }

    private Vector<String>retrieveRequestUrl(HttpServletRequest request){
        String requestURI = request.getRequestURI();
        String [] linkURI=requestURI.split("/");
        Vector<String> stringVector = new Vector<>();
        for (String t: linkURI) {
            stringVector.add(t);
        }
        stringVector.remove(0);
        stringVector.remove(0);
        return stringVector;
    }


    private String requestURL(HttpServletRequest request){
        Vector<String> requestURL = retrieveRequestUrl(request);
        String ur = "";
        for (String url:requestURL) {
            ur+=url;
        }
        return ur;
    }

    public static void main(String[] args) {
        // Set<Class>classSet = Loader.findAllClasses("etu2074.framework.controller");
        //System.out.println(classSet);
    }
}
