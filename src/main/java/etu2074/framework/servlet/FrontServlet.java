package etu2074.framework.servlet;
import etu2074.framework.url.RequestParameter;
import etu2074.framework.controller.ModelView;
import etu2074.framework.loader.Loader;
import etu2074.framework.mapping.Mapping;
import etu2074.framework.url.Link;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import com.ovoc01.dao.utilities.Utilities;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.util.*;


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

    public final void processRequest(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
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
            ModelView modelView = redirection(request);
            if(modelView!=null){
                if(!modelView.getData().isEmpty()) addDataToRequest(modelView.getData());
                dispatch(modelView.getView());
            }
            //redirection(request);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void instantiateObjectParameter(Map<String,String[]>requestParameter,Object object) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Field[] fields = object.getClass().getDeclaredFields();//maka ny attribut rehetra declarer ao @ ilay objet
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Field field:fields) {
            String [] parameter = requestParameter.get(field.getName());
            if(parameter!=null){
                String setter = Utilities.createSetter(field.getName());
                Method method_setter = stringMatchingMethod(methods,setter);
                Class<?>[]method_parameter = arrayMethodParameter(method_setter);
                method_setter.invoke(object,dynamicCast(method_parameter,parameter));
            }
        }
    }

    /**
     * function who dynamically cast an Object with the matching classes
     * @param classes
     * @param args
     * @return Object array
     */

    private Object [] dynamicCast(Class<?>[]classes,Object[]args){
        Object[] array = new Object[classes.length];
        int i = 0;
        for (Class<?> cl:classes) {
            System.out.println(args.getClass());
            array[i] = cl.cast(args[i]);
            i++;
        }
        return array;
    }
    private Method stringMatchingMethod(Method[] methods,String method_name){
        Method matchingMethod = null;
        for (Method method:methods) {
            if(method.getName().equals(method_name)){
                //System.out.println(method.getName());
                return method;
            }
        }
        return null;
    }

    private   Class<?>[] arrayMethodParameter(Method method) {
        // Get the parameters of the method
        Parameter[] parameters = method.getParameters();
        // Create an array to store the classes of the parameter instances
        Class<?>[] paramClasses = new Class<?>[parameters.length];
        // Iterate through the parameters and get their classes
        for (int i = 0; i < parameters.length; i++) {
            paramClasses[i] = parameters[i].getType();
            //System.out.println(parameters[i].getType());
        }
        // Return the array of parameter classes
        return paramClasses;
    }
    private void addDataToRequest(HashMap<String,Object>data)
    {
        for (Map.Entry<String,Object>value:data.entrySet()){
            getHttpServletRequest().setAttribute(value.getKey(), value.getValue());
        }
    }

    private ModelView redirection(HttpServletRequest request) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Vector<String> links = retrieveRequestUrl(request);
        Map<String,String[]> requestParameter = request.getParameterMap();
        if(!links.isEmpty()){
            Mapping objectMapping = mappingUrl.get(links.get(0));
            if(objectMapping!=null){
                Object temp = objectMapping.getaClass().newInstance();
                instantiateObjectParameter(requestParameter,temp);
                Method calledMethod = objectMapping.getMethod();
                System.out.println(calledMethod);
                //ModelView model_view= (ModelView) calledMethod.invoke(temp);
                ModelView modelView = invokeMethod(calledMethod,temp,requestParameter);
                return modelView;
            }
        }
        return null;
    }

    private ModelView invokeMethod(Method method,Object object,Map<String,String[]> requestParameter) throws InvocationTargetException, IllegalAccessException {
        Vector<Parameter> parameters =  annotedMethodParameters(method);//maka ny parametre rehetra anle fonction
        ModelView modelView = (ModelView) method.invoke(object);
        System.out.println(parameters);
        if(!parameters.isEmpty()){
            //  System.out.println("1");
            Class<?>[] parameterClasses = parametersClass(parameters);//maka ny class anle parametre
            //System.out.println("2");
            Object[] parameterValue = requestParamAttr(parameters,requestParameter);
            //System.out.println("3");
            modelView = (ModelView) method.invoke(object,dynamicCast(parameterClasses,parameterValue));//
            //System.out.println("4");
            System.out.println(modelView);
        }
        return modelView;
    }

    private Object[] requestParamAttr(Vector<Parameter> parameterVector,Map<String,String[]> requestParameter){
        Vector<Object> objectVector =  new Vector<>();
        for (Parameter params: parameterVector) {
            String[] val = requestParameter.get(params.getAnnotation(RequestParameter.class).name());//maka an'ilay anle valeur anle
            //System.out.println(val[0]);
            objectVector.add(val[0]);// atao anaty anilay Vector daoly na null na tsy null
        }
        return objectVector.toArray(new Object[0]);
    }


    private Vector<Parameter> annotedMethodParameters(Method method)
    {
        Vector<Parameter> parameterVector = new Vector<>();
        for (Parameter params:method.getParameters()) {
            if(params.isAnnotationPresent(RequestParameter.class)) parameterVector.add(params);//maka ny parametre rehetra ka annoté oueh @RequestParameter
        }
        return  parameterVector;
    }

    private Class<?>[] parametersClass(Vector<Parameter> parameters){
        if(parameters.isEmpty()) return null;
        Class<?>[] classes = new Class[parameters.size()];
        int i = 0;
        for(Parameter params:parameters){
            classes[i] = params.getType();
            i++;
        }
        return classes;
    }
    private Class<?>[] parametersClass(Parameter[] parameters){
        if(parameters.length==0) return null;
        Class<?>[] classes = new Class[parameters.length];
        int i = 0;
        for (Parameter params:parameters){
            classes[i] = params.getType();//mametraka anaty tableau ny class correspondant a chaque parametre
            i++;
        }
        return classes;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
        processRequest(request,response);
    }

    @Override
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException{
        processRequest(request,response);
    }

    private  Vector<String>retrieveRequestUrl(HttpServletRequest request){
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


    private  String requestURL(HttpServletRequest request){
        Vector<String> requestURL = retrieveRequestUrl(request);
        String ur = "";
        for (String url:requestURL) {
            ur+=url;
        }
        return ur;
    }

    public static void main(String[] args) {
        //Set<Class>classSet = Loader.findAllClasses("etu2074.framework.controller");
        //System.out.println(classSet);
    }
}
