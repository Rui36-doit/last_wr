package Model.utils;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Box {
    public ResultSet resultSet;
    public PreparedStatement preparedStatement;
    public Box(ResultSet resultSet, PreparedStatement preparedStatement){
        this.resultSet = resultSet;
        this.preparedStatement = preparedStatement;
    }

    public ResultSet getResult(){
        return resultSet;
    }

    public PreparedStatement grtPre(){
        return preparedStatement;
    }

    public void close() throws SQLException {
        resultSet.close();
        preparedStatement.close();
    }
}
