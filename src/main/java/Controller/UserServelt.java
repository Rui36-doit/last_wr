package Controller;

import Model.mysql.Connectpool;
import Model.op.Evaluate;
import Model.op.History;
import Model.op.Manager;
import Model.op.User;
import Model.utils.Box;
import Model.utils.JDBCUtil;
import Model.utils.JSONUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

@WebServlet("/userservelt")
public class UserServelt extends BaseServelt{
    //初始化
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ServletException("驱动加载失败", e);
        }
        JDBCUtil.connectpool = new Connectpool("mark");
        System.out.println("初始化成功");
    }

    //关闭数据库
    @Override
    public void destroy() {
        super.destroy();
        JDBCUtil.connectpool.closeALL();
        System.out.println("连接池关闭");
    }

    public void start(HttpServletRequest req, HttpServletResponse resp){
        System.out.println("s");
    }

    //用户登录代码
    public void login(HttpServletRequest req, HttpServletResponse resp) throws SQLException {
        Box box = null;
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            Infore infore = JSONUtils.getObject(req, Infore.class);
            String sql = "select * from users where email = ?";
            box = JDBCUtil.search(sql, infore.email);
            ResultSet resutl = box.getResult();
            PrintWriter writer = resp.getWriter();
            //登录账号
            if(resutl.next()) {
                String password = resutl.getString("password");
                if (infore.password.equals(password)) {
                    User user = new User(resutl.getInt("id"), resutl.getString("name"), resutl.getString("password"),
                            infore.email, resutl.getDouble("score"), resutl.getDouble("money"), resutl.getString("phonenumber"), resutl.getString("type")
                    , resutl.getString("avatar"));
                    System.out.println(user.name);
                    if(user.type.equals("no")){
                        writer.write("你已被封号，无法登录");
                        return;
                    }
                    //写入数据
                    HttpSession session = req.getSession();
                    session.setAttribute("user", user);
                    Map<String, HttpSession> onlineuser = (Map<String, HttpSession>) req.getServletContext()
                            .getAttribute("onlineusers");
                    onlineuser.put(session.getId(), session);
                    writer.write("yes");
                } else {
                    writer.write("no");
                }
                box.close();
            }else{
                System.out.println("没有该用户");
                writer.write("no");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //向对应的页面传的信息
    public void sentinfore(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        HttpSession session = req.getSession();
        User user = (User)session.getAttribute("user");
        if(!Manager.isonline(req, user.id)){
            System.out.println("用户不在线");
            return;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(user);
        PrintWriter writer = resp.getWriter();
        writer.write(json);
    }


    //检查是否存在该用户
    public void check(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        Infore infore = JSONUtils.getObject(req,Infore.class);
        String sql = "select email from users where email = ?";
        Box box = JDBCUtil.search(sql, infore.email);
        ResultSet resultSet = box.getResult();
        if(resultSet.next()){
            writer.write("no");
        }else {
            writer.write("yes");
        }
    }
    //注册代码
    public void register(HttpServletRequest req, HttpServletResponse resp){
        PrintWriter writer;
        try {
            writer = resp.getWriter();
            User user = JSONUtils.getObject(req, User.class);
            String sql2 = "insert into users(name, password, email, money, score, phonenumber, type, avatar) value (?, ?, ?, ?, ?, ?, ?, ?)";
            JDBCUtil.update(sql2, user.name, user.password, user.email, user.money, user.score, user.phonenumber, user.type, user.avatar);
            writer.write("注册成功，请返回登录");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("io语句出错");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("sql语句出错");
        }
    }

    //修改信息
    public void changeinfor(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter writer  = resp.getWriter();
        String back;
        //获取修改的信息
        try {
            User user1 = JSONUtils.getObject(req, User.class);
            HttpSession session = req.getSession();
            User user = (User)session.getAttribute("user");
            user.updatainfor(user1.name, user1.phonenumber, user1.avatar);
            back = "更改完成";
            System.out.println(user.phonenumber);
        } catch (IOException e) {
            e.printStackTrace();
            back = "更改出错了";
        } catch (SQLException e) {
            e.printStackTrace();
            back = "更改出错了";
        }
        writer.write(back);
    }

    //获取评论
    public void getevaluate(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        PrintWriter writer = resp.getWriter();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            HttpSession session = req.getSession();
            User user = (User) session.getAttribute("user");
            ArrayList<Evaluate> list = user.showevaluate();
            String json = objectMapper.writeValueAsString(list);
            System.out.println(json);
            writer.write(json);
        } catch (SQLException e) {
            writer.write("获取数据失败");
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            writer.write("获取数据失败");
            e.printStackTrace();
        }
    }


    //添加评论
    public void addevalue(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();
        try {
            String json = JSONUtils.getJSONstr(req);
            System.out.println(json);
            Evaluate evaluate = JSONUtils.getObjectbyjson(json, Evaluate.class);
            String sql = "insert into evaluate(content, buyer_id, seller_id, score," +
                    " buyername, shopname, shop_id, response) value (?,?,?,?,?,?,?,?);";
            int t = JDBCUtil.update(sql, evaluate.content, evaluate.buyer_id, evaluate.seller_id, evaluate.score, evaluate.buyername,
                    evaluate.shopname, evaluate.shop_id, evaluate.response);
            if(t == 0){
                System.out.println("无法添加数据");
                writer.write("no");
                return;
            }
            double score = evaluate.score;
            int id = evaluate.seller_id;
            User.addscore(score, id);
            writer.write("yes");
        } catch (SQLException e) {
            e.printStackTrace();
            writer.write("no");
        }
    }
    //退出登录
    public void outlogin(HttpServletRequest req, HttpServletResponse resp){
        HttpSession session = req.getSession(false);
        if(session != null) {
            User user = (User) session.getAttribute("user");
            ServletContext context = req.getServletContext();
            Map<String, HttpSession> onlineUsers = (Map<String, HttpSession>) context.getAttribute("onlineusers");
            synchronized (onlineUsers) {
                onlineUsers.remove(session.getId());
                session.invalidate();
            }
            System.out.println(user.name + "退出登录");
        }
    }
}
