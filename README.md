# frameworks
frameworks
# Breeze-run framework :
## Requierement : 
- ==Java version 8 or later==
- ==generic.jar==

## Constraints:
- all your class attributes, and function parameter must be Class(String,Double,etc) not primitive type(int,double,etc)
- all your class must be in the same package


Follow this step to setup this framework successfully:
    -   Step 1: create a new webapp application directory in your webapp in your tomcat folder (example:breeze-run)
    -   Step 2: in your WEB-INF in the breeze-run directory create a folder called lib and paste the breeze-run.jar framework
    -   Step 3: create a web.xml file in the WEB-INF directory and paste this following code : 
    
    ```xml
        <servlet>
            <servlet-name>frontServlet</servlet-name>
            <servlet-class>etu2074.framework.servlet.FrontServlet</servlet-class>
            <init-param>
                <param-name>pathos</param-name>
                <param-value>##</param-value>
            </init-param>
        </servlet>
        <servlet-mapping>
            <servlet-name>frontServlet</servlet-name>
            <url-pattern>/</url-pattern>
        </servlet-mapping>
    ```

    -  Step 4: replace the "##" in the param-value snippet with your package name
    -  Step 5: Now create  a .java file with the package name you choose
    -  Step 6: Now to access to method , you must use the annotation @Link (import:etu2074.framework.annotations.Link) and link there the url you want to access
    
    Example:

    ```Java
        @Link(url="testMethod")
        public ModelView andrana(){
            return new ModelView("Test.jsp");
        }
    ```


    - Step 7: All linked method must return a ModelView class(import:etu2074.framework.controller) and to add a destination page in it you must use the setView() in the ModelView who take as a parameter a String example
    
    ```Java
        setView("Test.jsp")
    ```
    
    - Step 8: To send data from a function to a page you must called the addItem() function in the ModelView class , it take as parameter an HashMap<String,Object>(String: the name of the data, Object:the data you want to send)
    example:

    ```Java
        @Link(url = "sprint8")
        public ModelView sprint_8( ){
            String message = "this is a message";
            ModelView modelView = new ModelView("Test.jsp");
            modelView.addItem("message",message);
            return modelView;
        }
    ```


    - Step 9: To get this data send in the previous step in a .jsp files you just need to get the the attributes in the request , and the name of the attribute is the name of data you want to send exemples:
    
    ```Jsp
        <%
            String message =(String) request.getAttribute("message");
        %>
    ```
   
    - Step 10: To send data from a .jsp web page to the breeze-run framework there is 2 way to do it:
        -Linked method doesn't take a parameter: the parameter name in the request must match the attributes name in your class
        -Linked method take a parameter: the parameter name in the request must match the function paramater name
    
    You can use those way separatedly or simultanously .


### Hope your enjoy using my framework 😁
