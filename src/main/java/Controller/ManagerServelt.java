package Controller;

import Model.op.History;
import Model.op.Manager;
import Model.op.Order;
import Model.op.User;
import Model.utils.Box;
import Model.utils.Historymanager;
import Model.utils.JDBCUtil;
import Model.utils.JSONUtils;
import com.fasterxml.jackson.core.JsonProcessingException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

@WebServlet("/managerservelt")
public class ManagerServelt extends BaseServelt{
    //管理员登录
    public void login(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        Infore infore = JSONUtils.getObject(req, Infore.class);
        String sql = "select id, username, password from managers where username = ?;";
        Box box = JDBCUtil.search(sql, infore.email);
        ResultSet resultSet = box.getResult();
        if(resultSet.next()){
            Manager manager = new Manager(resultSet.getInt("id"), resultSet.getString("username"),
                    resultSet.getString("password"));
            HttpSession session = req.getSession();
            session.setAttribute("manager", manager);
            writer.write("yes");
        }else{
            writer.write("no");
        }
    }
    //传输数据到管理员页面
    public void sentinfore(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Manager manager = (Manager) session.getAttribute("manager");
        PrintWriter writer = resp.getWriter();
        String json = JSONUtils.toJSON(manager);
        System.out.println(json);
        writer.write(json);
    }
    //审核商品
    public void checkshops(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Manager manager = (Manager) session.getAttribute("manager");
        PrintWriter writer = resp.getWriter();
        try {
            String json = JSONUtils.getJSONstr(req);
            System.out.println(json);
            manager.checkshops(json);
            writer.write("修改成功");
        } catch (SQLException e) {
            e.printStackTrace();
            writer.write("修改失败");
        }
    }
    //展示用户信息
    public void showusers(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        try {
            HttpSession session = req.getSession();
            Manager manager = (Manager) session.getAttribute("manager");
            ArrayList<User> lsit = manager.showuser();
            String json = JSONUtils.toJSON(lsit);
            System.out.println(json);
            writer.write(json);
        } catch (SQLException e) {
            e.printStackTrace();
            writer.write("no");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            writer.write("no");
        }
    }
    //封禁用户账号（待完善）
    public void blockuser(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        HttpSession session = req.getSession();
        Manager manager = (Manager) session.getAttribute("manager");
        String json = JSONUtils.getJSONstr(req);
        User user = JSONUtils.getObjectbyjson(json, User.class);
        boolean flag = manager.changetypeuser(user);
        //判断有用户在不在线
        Map<String, HttpSession> map = (Map<String, HttpSession>) req.getServletContext().
                getAttribute("onlineusers");
        String removeid = null;
        for(Map.Entry<String, HttpSession> entry: map.entrySet()){
            HttpSession session1 = entry.getValue();
            User user1 = (User) session1.getAttribute("user");
            if(user1.id == user.id){
                removeid = entry.getKey();
                System.out.println("用户在线");
                break;
            }
        }
        if(removeid != null){
            HttpSession hs = map.remove(removeid);
            hs.invalidate();
        }else{
            System.out.println("用户未登录");
        }
        if(flag){
            writer.write("yes");
        }else {
            writer.write("no");
        }
    }
    //解封用户的账号
    public void upuser(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        HttpSession session = req.getSession();
        Manager manager = (Manager) session.getAttribute("manager");
        String json = JSONUtils.getJSONstr(req);
        User user = JSONUtils.getObjectbyjson(json, User.class);
        boolean flag = manager.changetypeuser(user);
        if(flag){
            writer.write("yes");
        }else {
            writer.write("no");
        }
    }
    //显示用户的订单
    public void getuserorder(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        HttpSession session = req.getSession();
        Manager manager = (Manager) session.getAttribute("manager");
        String json = JSONUtils.getJSONstr(req);
        User user = JSONUtils.getObjectbyjson(json, User.class);
        ArrayList<Order> list = manager.getuserorder(user);
        String orderjson = JSONUtils.toJSON(list);
        writer.write(orderjson);
    }
    //显示用户的历史交易记录
    public void getuserhistory(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        String json = JSONUtils.getJSONstr(req);
        User user = JSONUtils.getObjectbyjson(json, User.class);
        String historyjson = Historymanager.showuserhistory(user.id);
        System.out.println(historyjson);
        writer.write(historyjson);
    }
    //查询用户的信息
    public void searchforhistory(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        String name = JSONUtils.getStr(req);
        HttpSession session = req.getSession();
        Manager manager = (Manager) session.getAttribute("manager");
        ArrayList<Order> list = manager.searchfororders(name);
        String json = JSONUtils.toJSON(list);
        writer.write(json);
    }
    //查询订单的信息
    public void searchorders(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        HttpSession session = req.getSession();
        req.setCharacterEncoding("UTF-8");
        Manager manager = (Manager) session.getAttribute("manager");
        String name = JSONUtils.getStr(req);
        System.out.println(name);
        ArrayList<Order> list =  manager.searchfororders(name);
        String json = JSONUtils.toJSON(list);
        System.out.println(json);
        writer.write(json);
    }
    //下架商品
    public void blockshops(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Manager manager = (Manager) session.getAttribute("manager");
        PrintWriter writer = resp.getWriter();
        try {
            String json = JSONUtils.getJSONstr(req);
            System.out.println(json);
            manager.blockshop(json);
            writer.write("yes");
        } catch (SQLException e) {
            e.printStackTrace();
            writer.write("no");
        }
    }
    //上架商品
    public void upshop(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        Manager manager = (Manager) session.getAttribute("manager");
        PrintWriter writer = resp.getWriter();
        try {
            String json = JSONUtils.getJSONstr(req);
            System.out.println(json);
            manager.upshop(json);
            writer.write("yes");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            writer.write("no");
        }
    }
    //获取销售总额
    public void getallmoney(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        HttpSession session = req.getSession();
        Manager manager = (Manager) session.getAttribute("manager");
        PrintWriter writer = resp.getWriter();
        Double sum = manager.getallmoney();
        String sumstr =  sum.toString();
        System.out.println(sumstr);
        writer.write(sumstr);
    }
    //根据月份获取销售总额
    public void getsumbymonth(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        HttpSession session = req.getSession();
        Manager manager = (Manager) session.getAttribute("manager");
        PrintWriter writer = resp.getWriter();
        String data = JSONUtils.getJSONstr(req);
        Double sum = manager.getmoneybymonth(data);
        String sumstr = sum.toString();
        System.out.println(sumstr);
        writer.write(sumstr);
    }
}
