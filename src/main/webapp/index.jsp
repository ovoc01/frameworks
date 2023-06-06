<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>JSP - Hello World</title>
</head>
<body>
<h1><%= "Hello World!" %>
</h1>
<br/>
<form action="fileUpload" method="post" enctype="multipart/form-data">
    <input type="text" name="nom"><br>
    <br>
    <input type="text" name="entrance"><br><br>
    <br>
    <input type="file" name="picture"><br><br>
    <input type="submit" value="submit">
</form>
</body>
</html>