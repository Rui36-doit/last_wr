package Controller;

import Model.op.*;
import Model.utils.Box;
import Model.utils.JDBCUtil;
import Model.utils.JSONUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.stream.events.Comment;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.attribute.AclFileAttributeView;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@WebServlet("/transationservelt")
public class TransationServelt extends BaseServelt{
    //添加订单
    public void addorder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();
        try {
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");
            if(!Manager.isonline(req, user.id)){
                writer.write("你已被封禁");
            }
            String json = JSONUtils.getJSONstr(req);
            System.out.println(json + "!");
            TransationManager tm = new TransationManager();
            boolean result = tm.addorder(json, user);
            if(result){
                writer.write("yes");
            }else {
                writer.write("no");
            }
        } catch (IOException e) {
            e.printStackTrace();
            writer.write("no");
        } catch (SQLException e) {
            e.printStackTrace();
            writer.write("no");
        }
    }
    //获取查询的数据
    public void showorder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();
        try {
            String idstr = JSONUtils.getStr(req);
            int id = Integer.valueOf(idstr);
            TransationManager tm = new TransationManager();
            ArrayList<Order> list = tm.showsrders(id);
            String json = JSONUtils.toJSON(list);
            System.out.println(json);
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
            writer.write("数据错误");
        } catch (SQLException e) {
            e.printStackTrace();
            writer.write("数据错误");
        }
    }
    //删除订单
    public void removeorder(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();
        try {
            String json = JSONUtils.getJSONstr(req);
            Order order = JSONUtils.getObjectbyjson(json, Order.class);
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");
            user.removeorder(order);
            writer.write("yes");
        } catch (SQLException e) {
            e.printStackTrace();
            writer.write("no");
        }
    }
    //确认付款
    public void sureorder(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        String json = JSONUtils.getJSONstr(req);
        Order order = JSONUtils.getObjectbyjson(json, Order.class);
        if(!order.type.equals("待收货")){
            writer.write("no");
            return;
        }
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        double m = user.sureorder(order);
        if(m == 0){
            writer.write("yes");
            return;
        }
        //支付
        String sql = "update users set money = money + ? where id = ?;";
        int t = JDBCUtil.update(sql, m, order.seller_id);
        if(t == 0){
            System.out.println("转账失败");
            writer.write("no");
        }
        writer.write("yse");
    }
    //添加状态
    public void sureorder_inner(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        String idstr = JSONUtils.getStr(req);
        int id = Integer.valueOf(idstr);
        String sql = "update orders set type = '已评论' where id = ?;";
        JDBCUtil.update(sql, id);
    }
    //添加回复
    public void addresponse(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        String json = JSONUtils.getJSONstr(req);
        PrintWriter writer = resp.getWriter();
        Evaluate evaluate = JSONUtils.getObjectbyjson(json, Evaluate.class);
        String sql = "update evaluate set response = ? where id = ?;";
        int t = JDBCUtil.update(sql, evaluate.response, evaluate.id);
        if(t == 0){
            writer.write("no");
        }else {
            writer.write("yes");
        }
    }
    //展示我的评论
    public void showemyvalue(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        String idstr = JSONUtils.getStr(req);
        int id = Integer.valueOf(idstr);
        System.out.println(id);
        String sql = "select id, content, buyer_id, seller_id, score, buyername, shopname, " +
                "shop_id, response from evaluate e where buyer_id = ?;";
        Box box = JDBCUtil.search(sql, id);
        ResultSet resultSet = box.getResult();
        ArrayList<Evaluate> list = new ArrayList<>();
        int i = 1;
        while (resultSet.next()){
            Evaluate evaluate = new Evaluate(resultSet.getInt("id"), resultSet.getInt("buyer_id"),
                    resultSet.getInt("seller_id"), resultSet.getString("content"), resultSet.getDouble("score"),
                    resultSet.getString("buyername"), resultSet.getInt("shop_id"), resultSet.getString("shopname"),
                    resultSet.getString("response"));
            list.add(evaluate);
            i++;
        }
        box.close();
        String tojson = JSONUtils.toJSON(list);
        System.out.println(tojson);
        writer.write(tojson);
    }

    //展示模商品的订单
    public void showshoporder(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        String json = JSONUtils.getJSONstr(req);
        Shops shops = JSONUtils.getObjectbyjson(json, Shops.class);
        String sql = "select id, shop_id, seller_id, type, pay_type, sent_time, buyer_id, number, shop_name, seller_name, " +
                "buyer_name, money from orders o where shop_id = ?;";
        Box box = JDBCUtil.search(sql, shops.id);
        TransationManager tm = new TransationManager();
        ArrayList<Order> list = tm.getorderlist(box);
        box.close();
        String orderjson = JSONUtils.toJSON(list);
        writer.write(orderjson);
    }

    //修改订单的状态
    public void changeordertype(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        try {
            String json = JSONUtils.getJSONstr(req);
            Order order = JSONUtils.getObjectbyjson(json, Order.class);
            boolean flag = TransationManager.updatatype(order.type, order.id);
            if(flag){
                writer.write("yes");
            }else {
                writer.write("no");
            }
        } catch (IOException e) {
            e.printStackTrace();
            writer.write("no");
        } catch (SQLException e) {
            e.printStackTrace();
            writer.write("no");
        }
    }

    //展示所有的订单
    public void showallorder(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        TransationManager tm = new TransationManager();
        ArrayList<Order> list = tm.showallorder();
        String json = JSONUtils.toJSON(list);
        System.out.println(json);
        writer.write(json);
    }

    //退货处理
    public void backshop(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        String json = JSONUtils.getJSONstr(req);
        Order order = JSONUtils.getObjectbyjson(json, Order.class);
        TransationManager tm = new TransationManager();
        if(tm.backshop(order)){
            writer.write("yes");
        }else {
            writer.write("no");
        }
    }

    //退款操作
    public void backmoney(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        String json = JSONUtils.getJSONstr(req);
        Order order = JSONUtils.getObjectbyjson(json, Order.class);
        HttpSession session = req.getSession(false);
        User user = (User) session.getAttribute("user");
        TransationManager tm = new TransationManager();
        boolean flag = tm.backmoney(order, user);
        if(flag){
            writer.write("yes");
        }else {
            writer.write("no");
        }
    }

}
