package testframework.connection;

import com.ovoc01.dao.connection.MyConnection;

import java.sql.Connection;

public class Connnection {
    public static Connection pgCon(){
        return MyConnection.initPgCon("localhost","5432","rakotoharisoa","pixel","postgres");
    }
}
