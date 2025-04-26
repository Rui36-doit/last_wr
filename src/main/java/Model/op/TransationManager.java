package Model.op;

import Model.utils.Box;
import Model.utils.JDBCUtil;
import Model.utils.JSONUtils;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransationManager {
    //获取订单数组
    public ArrayList<Order> getorderlist(Box box) throws SQLException {
        ArrayList<Order> list = new ArrayList<>();
        ResultSet resultSet = box.getResult();
        while (resultSet.next()){
            LocalDateTime ldtstart = resultSet.getTimestamp("sent_time").toLocalDateTime();
            Order order = new Order(resultSet.getInt("id"), resultSet.getInt("shop_id"), resultSet.getInt("seller_id"),
                    resultSet.getInt("buyer_id"), resultSet.getInt("number"),resultSet.getString("type"),
                    resultSet.getString("pay_type"), resultSet.getString("seller_name"),resultSet.getString("shop_name"), resultSet.getString("buyer_name"),
                    ldtstart, resultSet.getDouble("money"));
            list.add(order);
        }
        return list;
    }
    //查看订单的信息
    public ArrayList<Order> showsrders(int buyerid) throws SQLException {
        String sql = "select id, shop_id, seller_id, type, pay_type, sent_time, buyer_id, number, shop_name," +
                " seller_name, buyer_name, money from orders where buyer_id = ? and type != '交易取消'";
        Box box = JDBCUtil.search(sql, buyerid);
        ArrayList<Order> list = getorderlist(box);
        return list;
    }
    //修改商品数据
    public boolean updatashopnumber(int shop_id, int n) throws SQLException {
        Connection connection = null;
        PreparedStatement pres = null;
        PreparedStatement pres2 = null;
        ResultSet rs1 = null;
        try {
            //开启事务
            connection = JDBCUtil.connectpool.getConnection();
            connection.setAutoCommit(false);
            String sql = "select number from shops where id = ? for update;";
            String sql2 = "update shops set number = number - ? where id = ?;";
            // 判断有没有货
            pres = connection.prepareStatement(sql);
            pres.setInt(1, shop_id);
            rs1 = pres.executeQuery();
            rs1.next();
            int t = rs1.getInt("number");
            if(t < n){
                System.out.println("存货不足");
                pres.close();
                rs1.close();
                connection.rollback();
                JDBCUtil.connectpool.backConnection(connection);
                return false;
            }
            //更新数据
            pres2 = connection.prepareStatement(sql2);
            pres2.setInt(1, n);
            pres2.setInt(2, shop_id);
            t = pres2.executeUpdate();
            if(t == 0){
                System.out.println("无法更新数据");
            }
            pres.close();
            pres2.close();
            rs1.close();
            connection.commit();
            JDBCUtil.connectpool.backConnection(connection);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            connection.rollback();
            if(pres != null)pres.close();
            if(pres2 != null)pres2.close();
            if(rs1 != null)rs1.close();
            JDBCUtil.connectpool.backConnection(connection);
            return false;
        }
    }
    //添加订单
    public boolean addorder(String json, User user) throws IOException, SQLException {
        System.out.println(user.name);
        Order order = JSONUtils.getObjectbyjson(json, Order.class);
        boolean result = updatashopnumber(order.shop_id, order.number);
        if(!result){
            System.out.println("无法更新shops数据");
            return false;
        }
        LocalDateTime time = LocalDateTime.now();
        order.sent_time = time;
        String sql = "insert into orders(shop_id, seller_id, type, pay_type, sent_time, " +
                "buyer_id, number, shop_name, seller_name, buyer_name, money) value (?,?,?,?,?,?,?,?,?,?,?);";
        int t = JDBCUtil.update(sql, order.shop_id, order.seller_id, order.type, order.pay_type, order.sent_time, order.buyer_id,
                order.number, order.shop_name, order.seller_name, order.buyer_name, order.money);
        if(order.pay_type.equals("先支付后到货")){
            System.out.println(user.money);
            System.out.println(user.id);
            user.pay(order.money);
        }
        if(t > 0){
            return true;
        }else {
            System.out.println("无法添加orders数据");
            return false;
        }
    }

    //修改订单的类型
    public static boolean updatatype(String type, int id) throws SQLException {
        String sql = "update orders set type = ? where id = ?;";
        int t = JDBCUtil.update(sql, type, id);
        if(t == 0){
            System.out.println("修改失败");
            return false;
        }
        return true;
    }

    //展示所有订单
    public ArrayList<Order> showallorder() throws SQLException {
        String sql = "select id, shop_id, seller_id, type, pay_type, sent_time, buyer_id, " +
                "number, shop_name, seller_name, buyer_name, money from orders;";
        Box box = JDBCUtil.search(sql);
        ArrayList<Order> list = getorderlist(box);
        box.close();
        return list;
    }

    //退货
    public boolean backshop(Order order) throws SQLException {
        //更新订单
        String sql = "update orders set type = '待处理' where id = ?";
        int t = JDBCUtil.update(sql, order.id);
        if(t == 0){
            return false;
        }
        return true;
    }

    //退款处理
    public boolean backmoney(Order order, User user) throws SQLException {
        try {
            if(user.money < order.money){
                System.out.println("余额不足");
                return false;
            }
            //刷新订单
            updatatype(order.type, order.id);
            //修改用户的金额
            String sql = "update users set money = money - ? where id = ?";
            JDBCUtil.update(sql, order.money, order.seller_id);
            String sql2 = "update users set money = money + ? where id = ?";
            JDBCUtil.update(sql2, order.money, order.buyer_id);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("退款异常");
            return false;
        }
    }
}
