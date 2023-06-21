package etu2074.framework.servlet;

import etu2074.framework.url.*;

import etu2074.framework.controller.ModelView;
import etu2074.framework.loader.Loader;
import etu2074.framework.mapping.Mapping;
import etu2074.framework.upload.FileUpload;
import etu2074.framework.utilities.Utilities;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.util.*;

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50) // 50MB
public class FrontServlet extends HttpServlet {
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private HashMap<String, Mapping> mappingUrl;
    private HashMap<String,Object> controllerInstance;
    private String sessionId;
    private String profileName;

    public FrontServlet(){

    }

    public String getProfileName() {
        return profileName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public HashMap<String, Object> getControllerInstance() {
        return controllerInstance;
    }

    public void setControllerInstance(HashMap<String, Object> controllerInstance) {
        this.controllerInstance = controllerInstance;
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
            setProfileName(getInitParameter("profile_name"));
            setSessionId(getInitParameter("session_name"));

            mappingUrl = new HashMap<>();
            controllerInstance = new HashMap<>();
            retrieveAllMappedMethod(path);
            addSingletonMap(path);
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

    private void addSingletonMap(String path) throws URISyntaxException, ClassNotFoundException {
        Set<Class> classSet = null;
        classSet = Loader.findAllClasses(path);
        for (Class c: classSet) {
            if(c.isAnnotationPresent(Scope.class)){
                controllerInstance.put(c.getName(),null);
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
            HashMap<String,Mapping> list = getMappingUrl();

            //writer.println(list);
            ModelView modelView = redirection(request);
            if(modelView!=null){
                if(modelView.getData().size()>0) addDataToRequest(modelView.getData());
                if(modelView.getSessions().size()>0) addDataToSession(modelView.getSessions());
                System.out.println(modelView.getView());
                dispatch(modelView.getView());
            }
            //redirection(request);
        }catch (Exception e){
            e.printStackTrace();
            response.getWriter().println(e);
        }
    }

    private void addDataToSession(HashMap<String, Object> sessions) {
        HttpSession httpSession = getHttpServletRequest().getSession();
        for (Map.Entry<String,Object>value:sessions.entrySet()){
            System.out.println(value.getKey()+" "+value.getValue());
            httpSession.setAttribute(value.getKey(), value.getValue());
        }
    }


    public boolean hasParts(HttpServletRequest request) {
        // Check if the request is multipart
        String contentType = request.getContentType();
        if (contentType != null && contentType.startsWith("multipart/")) {
            // Check if the request has any parts
            try {
                HttpServletRequestWrapper requestWrapper = (HttpServletRequestWrapper) request;
                Part[] parts = requestWrapper.getParts().toArray(new Part[0]);
                return parts.length > 0;
            } catch (Exception e) {
                // Handle any exceptions that may occur while retrieving parts
                e.printStackTrace();
            }
        }
        return false;
    }

    private ModelView redirection(HttpServletRequest request) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, ServletException, IOException {
        Vector<String> links = retrieveRequestUrl(request);
        Map<String,String[]> requestParameter = request.getParameterMap();
        Collection<Part> parts = null;
        if(hasParts(request)) {
            parts =request.getParts();
        }

        if(!links.isEmpty()){
            Mapping objectMapping = mappingUrl.get(links.get(0));
            if(objectMapping!=null){
                Object temp = instanceToCall(objectMapping);
                instantiateObjectParameter(requestParameter,temp,parts);
                Method calledMethod = objectMapping.getMethod();
                System.out.println(calledMethod);
                // System.out.println(request.getSession());
                //ModelView model_view= (ModelView) calledMethod.invoke(temp);
                return invokeMethod(calledMethod,temp,requestParameter,request.getSession());
            }
        }
        return null;
    }

    private Object instanceToCall(Mapping mapping) throws IllegalAccessException, InstantiationException {
        Object object =mapping.getaClass().newInstance();
        if(mapping.getaClass().isAnnotationPresent(Scope.class)){
            Object temp = controllerInstance.get(mapping.getaClass().getName());
            if(temp==null){
                object = mapping.getaClass().newInstance();
                controllerInstance.replace(mapping.getaClass().getName(),object);
                return object;
            }
            //Utilities.resetObjectParameter(temp);
            return temp;
        }
        return object;
    }


    private ModelView invokeMethod(Method method,Object object,Map<String,String[]> requestParameter,HttpSession session) throws InvocationTargetException, IllegalAccessException {
        Vector<Parameter> parameters =  annotedMethodParameters(method);//maka ny parametre rehetra anle fonction
        System.out.println(parameters);
        methodAuthentification(method,session);
        checkIfSessionIsNeed(object,method,session);
        if(!parameters.isEmpty()){
            Class<?>[] parameterClasses = parametersClass(parameters);//maka ny class anle parametre
            Object[] parameterValue = requestParamAttr(parameters,requestParameter);
            return (ModelView) method.invoke(object,dynamicCast(parameterClasses,parameterValue));//
        }else{
            return (ModelView) method.invoke(object);
        }
    }

    private void checkIfSessionIsNeed(Object object,Method method,HttpSession httpSession) throws IllegalAccessException {
        if(!method.isAnnotationPresent(Session.class)) return;
        setSessionInObject(object,httpSession);
    }

    private void setSessionInObject(Object object,HttpSession httpSession) throws RuntimeException, IllegalAccessException {
        Field session = getSessionFieldInObject(object);
        if(session==null)throw new RuntimeException("To access Session with @Session annotation you should create an HashMap<String,Object> called session");
        sessionSessionInObject(object,session,httpSession);
    }

    private final Field getSessionFieldInObject(Object object){
        return  Arrays.stream(object.getClass().getDeclaredFields()).filter(field ->field.getName().equals("session")).findFirst().orElse(null);
    }

    private final void sessionSessionInObject(Object object , Field field,HttpSession httpSession ) throws IllegalAccessException {
        Enumeration<String> stringEnumeration =  httpSession.getAttributeNames();
        HashMap<String,Object> sessionObject = new HashMap<>();
        while(stringEnumeration.hasMoreElements()){
            String key = stringEnumeration.nextElement();
            sessionObject.put("key",httpSession.getAttribute(key));
        }
        field.setAccessible(true);
        field.set(object,sessionObject);
    }


    private void methodAuthentification(Method method,HttpSession session)throws RuntimeException{
        Authentification authentification = method.getAnnotation(Authentification.class);
        if(authentification==null) return;
        if(session.getAttribute(getProfileName())!=authentification.auth()) throw new RuntimeException("Authentification failed");
    }

    private void checkSessionPresence(HttpSession session){

    }
    private String  listProfileNeeded(Authentification authentification){
        return authentification.auth();
    }

    private void instantiateObjectParameter(Map<String,String[]>requestParameter,Object object,Collection<Part> partCollection) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ServletException, IOException {
        Field[] fields = object.getClass().getDeclaredFields();//maka ny attribut rehetra declarer ao @ ilay objet
        Method[] methods = object.getClass().getDeclaredMethods();
        for (Field field:fields) {
            String [] parameter = requestParameter.get(field.getName());
            if(parameter!=null && field.getType()!= FileUpload.class){
                String setter = Utilities.createSetter(field.getName());
                Method method_setter = stringMatchingMethod(methods,setter);
                Class<?>[]method_parameter = arrayMethodParameter(Objects.requireNonNull(method_setter));
                method_setter.invoke(object,dynamicCast(method_parameter,parameter));
            } else if (partCollection!=null && field.getType()== FileUpload.class) {
                Part part = getMatchingPart(field.getName(),partCollection);
                FileUpload fileUpload = instantiateFileUpload(part);
                String setter = Utilities.createSetter(field.getName());
                Method method_setter = stringMatchingMethod(methods,setter);
                assert method_setter != null;
                method_setter.invoke(object,fileUpload);
            }
        }
    }

    private Part getMatchingPart(String partName,Collection<Part> partCollection){
        return partCollection.stream().filter(part -> part.getName().equals(partName)).findFirst().orElse(null);
    }
    private FileUpload instantiateFileUpload(Part part) throws IOException {
        return  new FileUpload(part.getSubmittedFileName(),part.getInputStream().readAllBytes(),null);
    }

   /* private void instantiateObjectParameter(Map<String, String[]> requestParameter, Object object) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Field[] fields = object.getClass().getDeclaredFields();
        Method[] methods = object.getClass().getDeclaredMethods();

        Arrays.stream(fields)
                .filter(field -> requestParameter.containsKey(field.getName()))
                .forEach(field -> {
                    String[] parameter = requestParameter.get(field.getName());
                    String setter = Utilities.createSetter(field.getName());
                    Method methodSetter = stringMatchingMethod(methods, setter);
                    Class<?>[] methodParameter = arrayMethodParameter(methodSetter);
                    try {
                        methodSetter.invoke(object, dynamicCast(methodParameter, parameter));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                });
    }*/


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
            System.out.println(value.getKey()+" "+value.getValue());
            getHttpServletRequest().setAttribute(value.getKey(), value.getValue());
        }
    }

    private Object[] requestParamAttr(Vector<Parameter> parameterVector,Map<String,String[]> requestParameter){
        Vector<Object> objectVector =  new Vector<>();
        for (Parameter params: parameterVector) {
            String[] val = requestParameter.get(params.getName());//maka an'ilay anle valeur anle
            objectVector.add(val[0]);// atao anaty anilay Vector daoly na null na tsy null
        }
        return objectVector.toArray(new Object[0]);
    }


    private Vector<Parameter> annotedMethodParameters(Method method)
    {
        Vector<Parameter> parameterVector = new Vector<>();
        for (Parameter params:method.getParameters()) {
            //if(params.isAnnotationPresent(RequestParameter.class))
            parameterVector.add(params);//maka ny parametre rehetra ka annoté oueh @RequestParameter
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

    private Vector<Parameter> fileUpload(Vector<Parameter> parameters){
        if(parameters.isEmpty()) return null;
        int i = 0;
        Vector<Parameter> fileUploadList = new Vector<>();
        for (Parameter parameter: parameters) {
            if(parameter.getType() == FileUpload.class) fileUploadList.add(parameter);
        }
        return fileUploadList;
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
}
