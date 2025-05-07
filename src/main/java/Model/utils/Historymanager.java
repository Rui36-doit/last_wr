package Model.utils;

import Model.op.History;
import Model.op.Order;
import Model.utils.Box;
import Model.utils.JDBCUtil;
import Model.utils.JSONUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Historymanager {
    //获取历史记录数组
    public static ArrayList<History> getlist(Box box) throws SQLException {
        ArrayList<History> list = new ArrayList<>();
        ResultSet resultSet = box.getResult();
        while (resultSet.next()){
            LocalDateTime ldt = resultSet.getTimestamp("time").toLocalDateTime();
            History history = new History(resultSet.getInt("id"), resultSet.getInt("seller_id"), resultSet.getInt("buyer_id"),
                    resultSet.getString("name"), resultSet.getString("type"), resultSet.getString("seller_name"),
                    resultSet.getString("buyer_name"), ldt);
            list.add(history);
        }
        return list;
    }

    //获取用户的历史记录
    public static String showuserhistory(int id) throws SQLException, JsonProcessingException {
        String sql = "select id, seller_id, buyer_id, name, type, time, seller_name, buyer_name" +
                " from history where buyer_id = ? or seller_id = ?";
        Box box = JDBCUtil.search(sql, id, id);
        ArrayList<History> list = getlist(box);
        box.close();
        History history = list.get(0);
        System.out.println(history.time);
        String json = JSONUtils.toJSON(list);
        System.out.println(json);
        return json;
    }

    //添加历史记录
    public static boolean addhistory(Order order) throws SQLException {
        String sql = "insert into history(seller_id, buyer_id, name, " +
                "type, time, seller_name, buyer_name) value (?,?,?,?,?,?,?)";
        LocalDateTime time = LocalDateTime.now();
        int t = JDBCUtil.update(sql, order.seller_id, order.buyer_id, order.shop_name, order.type, time, order.seller_name, order.buyer_name);
        if(t == 0){
            return false;
        }
        return true;
    }
}
