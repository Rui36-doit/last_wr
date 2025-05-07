package Model.utils;



import Model.mysql.Connectpool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class JDBCUtil {
    public static Connectpool connectpool = null;

    //更新数据进数据库
    public static int update(String sql, Object... parms) throws SQLException {
        Connection connection = connectpool.getConnection();
        PreparedStatement pres = connection.prepareStatement(sql);
        int t = 0;
        try {
            connection.setAutoCommit(false);
            for(int i = 0; i < parms.length; i++){
                if(parms[i].getClass() == Integer.class){
                    int n = (int)parms[i];
                    pres.setInt(i + 1, n);
                } else if(parms[i].getClass() == Double.class){
                    double num = (double)parms[i];
                    pres.setDouble(i + 1, num);
                } else if(parms[i].getClass() == LocalDateTime.class){
                    pres.setObject(i + 1, parms[i]);
                } else{
                    pres.setString(i + 1, (String)parms[i]);
                }
            }
            t = pres.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            System.out.println("操作有误");
            e.printStackTrace();
            connection.rollback();
        } finally {
            pres.close();
            connectpool.backConnection(connection);
            //connection.close();
            return t;
        }
    }

    //查询数据
    public static Box search(String sql, Object... parms) throws SQLException {
        Connection connection = connectpool.getConnection();
        //连接获取数据库对象
        PreparedStatement pres = connection.prepareStatement(sql);
        try {
            ResultSet result = null;
            connection.setAutoCommit(false);
            if(parms.length != 0) {
                for (int i = 0; i < parms.length; i++) {
                    if (parms[i].getClass() == Integer.class) {
                        pres.setInt(i + 1, (int) parms[i]);
                    } else {
                        pres.setString(i + 1, parms[i].toString());
                    }
                }
            }
            result = pres.executeQuery();
            connection.commit();
            Box box = new Box(result, pres);
            //pres.close();
            connectpool.backConnection(connection);
            return box;
        } catch (SQLException e) {
            pres.close();
            connectpool.backConnection(connection);
            e.printStackTrace();
            return null;
        }
    }

    /*
    public static Box search(String sql, Object... parms) throws SQLException {
        Connection connection = connectpool.getConnection();
        //连接获取数据库对象
        PreparedStatement pres = connection.prepareStatement(sql);
        ResultSet result = null;
        connection.setAutoCommit(false);
        if(parms.length != 0) {
            for (int i = 0; i < parms.length; i++) {
                if (parms[i].getClass() == Integer.class) {
                    pres.setInt(i + 1, (int) parms[i]);
                } else {
                    pres.setString(i + 1, parms[i].toString());
                }
            }
        }
        result = pres.executeQuery();
        connection.commit();
        Box box = new Box(result, pres);
        //pres.close();
        connectpool.backConnection(connection);
        return box;
    }
     */
}

