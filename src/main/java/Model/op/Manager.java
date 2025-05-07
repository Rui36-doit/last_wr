package Model.op;

import Controller.UserServelt;
import Model.utils.Box;
import Model.utils.Historymanager;
import Model.utils.JDBCUtil;
import Model.utils.JSONUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class Manager {
    public int id;
    public String username;
    public String password;
    public Manager(int id, String username, String password){
        this.id = id;
        this.username = username;
        this.password = password;
    }

    //审核商品
    public void checkshops(String json) throws IOException, SQLException {
        Shops shops = JSONUtils.getObjectbyjson(json, Shops.class);
        String sql = "update shops set type = ? where id = ?;";
        JDBCUtil.update(sql, shops.type, shops.id);
    }

    //获取用户数组
    public static ArrayList<User> getuserlist(Box box) throws SQLException {
        ResultSet resultSet = box.getResult();
        ArrayList<User> list = new ArrayList<>();
        while (resultSet.next()){
            User user = new User(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("email"),
                    resultSet.getDouble("score"), resultSet.getDouble("money"), resultSet.getString("phonenumber"), resultSet.getString("type"),
                    resultSet.getString("avatar"));
            list.add(user);
        }
        return list;
    }

    //展示用户
    public ArrayList<User> showuser() throws SQLException {
        String sql = "select id, name, email, money, score, phonenumber, type, avatar from users";
        Box box = JDBCUtil.search(sql);
        ArrayList<User> list = getuserlist(box);
        box.close();
        return list;
    }

    //封禁用户
    public boolean changetypeuser(User user) throws SQLException {
        String sql = "update users set type = ? where id = ?";
        int t = JDBCUtil.update(sql, user.type, user.id);
        if(t == 0){
            return false;
        }
        return true;
    }

    //展示用户的订单
    public ArrayList<Order> getuserorder(User user) throws SQLException {
        String sql = "select id, shop_id, seller_id, type, pay_type, sent_time, buyer_id, number, " +
                "shop_name, seller_name, buyer_name, money from orders where buyer_id = ?";
        Box box = JDBCUtil.search(sql, user.id);
        TransationManager tm = new TransationManager();
        ArrayList<Order> list = tm.getorderlist(box);
        box.close();
        return list;
    }

    //搜索相关订单
    public ArrayList<Order> searchfororders(String name) throws SQLException {
        String sql = "select id, shop_id, seller_id, type, pay_type, sent_time, buyer_id, number," +
                " shop_name, seller_name, buyer_name, money from orders where locate(?, shop_name) > 0;";
        Box box = JDBCUtil.search(sql, name);
        TransationManager tm = new TransationManager();
        ArrayList<Order> list = tm.getorderlist(box);
        box.close();
        return list;
    }

    //下架商品
    public void blockshop(String json) throws IOException, SQLException {
        Shops shops = JSONUtils.getObjectbyjson(json, Shops.class);
        String sql = "update shops set type = 'no' where id = ?;";
        JDBCUtil.update(sql, shops.id);
    }

    //商品上架
    public void upshop(String json) throws IOException, SQLException {
        Shops shops = JSONUtils.getObjectbyjson(json, Shops.class);
        String sql = "update shops set type = 'pass' where id = ?;";
        JDBCUtil.update(sql, shops.id);
    }

    //获取商品的总额
    public Double getallmoney() throws SQLException {
        String sql = "select money from orders where type = '已完成' or type = '已评论'";
        Box box = JDBCUtil.search(sql);
        ResultSet resultSet = box.getResult();
        Double sum = 0.0;
        while (resultSet.next()){
            sum = sum + resultSet.getDouble("money");
        }
        return sum;
    }

    //获取特定月份商品的总额
    public double getmoneybymonth(String data) throws SQLException {
        double sum = 0;
        LocalDateTime dateTime = LocalDateTime.parse(data + "-01T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        String sql = "select money from orders where (type = '已完成' or type = '已评论') " +
                "and year(sent_time) = ? and month(sent_time) = ?;";
        Box box = JDBCUtil.search(sql, year, month);
        ResultSet resultSet = box.getResult();
        while (resultSet.next()){
            sum = sum + resultSet.getDouble("money");
        }
        return sum;
    }

    public static boolean isonline(HttpServletRequest req, int id){
        Map<String, HttpSession> map = (Map<String, HttpSession>) req.getServletContext().
                getAttribute("onlineusers");
        String removeid = null;
        for(Map.Entry<String, HttpSession> entry: map.entrySet()){
            HttpSession session1 = entry.getValue();
            User user1 = (User) session1.getAttribute("user");
            if(user1.id == id){
                removeid = entry.getKey();
                System.out.println("用户在线");
                return true;
            }
        }
        return false;
    }
}
