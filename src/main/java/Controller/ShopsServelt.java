package Controller;

import Model.op.Evaluate;
import Model.op.Manager;
import Model.op.Shops;
import Model.op.ShopsManager;
import Model.utils.JSONUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

@WebServlet("/shopsservelt")
@MultipartConfig(maxFileSize = 5_242_880)
public class ShopsServelt extends BaseServelt{
    //获取数据
    public void showshops(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ShopsManager shopsManager = new ShopsManager();
        PrintWriter writer = resp.getWriter();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            ArrayList<Shops> list = shopsManager.getshops();
            String json = objectMapper.writeValueAsString(list);
            System.out.println(json);
            writer.write(json);
        } catch (SQLException e) {
            writer.write("数据获取异常");
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            writer.write("数据获取异常");
            e.printStackTrace();
        }
    }

    //展示商品的信息
    public void getshopinfor(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        ShopsManager shopsManager = new ShopsManager();
        PrintWriter writer = resp.getWriter();
        String idstr = JSONUtils.getStr(req);
        int id = Integer.valueOf(idstr);
        System.out.println(id);
        String json = shopsManager.showtheshop(id);
        writer.write(json);
    }

    //获取商品评价
    public void getshopevaluate(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        ShopsManager shopsManager = new ShopsManager();
        PrintWriter writer = resp.getWriter();
        String idstr = JSONUtils.getStr(req);
        int id = Integer.valueOf(idstr);
        ArrayList<Evaluate> list = shopsManager.getshopevaluate(id);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(list);
        writer.write(json);
    }

    //获取待审核的商品
    public void getwaitingshops(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        ShopsManager shopsManager = new ShopsManager();
        PrintWriter writer = resp.getWriter();
        try {
            ArrayList<Shops> list = shopsManager.getwaitingshops();
            String json = JSONUtils.toJSON(list);
            writer.write(json);
        } catch (SQLException e) {
            e.printStackTrace();
            writer.write("数据库出错");
        }
    }

    //搜索商品
    public void searchshops(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        String sort_type = req.getHeader("sort-type");
        System.out.println(sort_type);
        req.setCharacterEncoding("UTF-8");
        ShopsManager shopsManager = new ShopsManager();
        PrintWriter writer = resp.getWriter();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String name = JSONUtils.getStr(req);
            System.out.println(name);
            ArrayList<Shops> list = shopsManager.searchshops(name, "pass", sort_type);
            String json = objectMapper.writeValueAsString(list);
            writer.write(json);
            System.out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
            writer.write("获取数据异常");
        } catch (SQLException e) {
            e.printStackTrace();
            writer.write("获取数据异常");
        }
    }

    //搜索我的商品
    public void searchmyshops(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ShopsManager shopsManager = new ShopsManager();
        PrintWriter writer = resp.getWriter();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            String idstr = JSONUtils.getStr(req);
            int id = Integer.valueOf(idstr);
            System.out.println(id);
            ArrayList<Shops> list = shopsManager.searchusershosp(id);
            String json = objectMapper.writeValueAsString(list);
            writer.write(json);
            System.out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
            writer.write("获取数据异常");
        } catch (SQLException e) {
            e.printStackTrace();
            writer.write("获取数据异常");
        }
    }
    //修改商品的信息
    public void changeinfore(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Shops shop = JSONUtils.getObject(req, Shops.class);
        shop.save();
        writer.write("修改完成");
    }
    //删除商品的信息
    public void delectshops(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        ShopsManager manager = new ShopsManager();
        PrintWriter writer = resp.getWriter();
        String idstr = JSONUtils.getStr(req);
        int id = Integer.valueOf(idstr);
        System.out.println(id);
        int t = manager.delect(id);
        if(t > 0){
            writer.write("删除成功，请刷新数据");
        }else {
            writer.write("似乎不存在该数据");
        }
    }

    //从前端获取图片
    public void getphoto(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String type = req.getHeader("type");
        System.out.println(type);
        PrintWriter out = resp.getWriter();
        String savepath = "C:\\Users\\wrui\\IdeaProjects\\the_last_exam\\src\\main\\webapp\\View";
        if(type.equals("shops")){
            savepath = savepath + "\\shops_image";
        }else {
            savepath = savepath + "\\avatar_image";
        }
        Part file = req.getPart("image");
        String fileName = UUID.randomUUID() + "_" + file.getSubmittedFileName();
        file.write(savepath + File.separator + fileName);
        if(type.equals("shops")){
            out.write("shops_image/" + fileName);
        }else{
            out.write("avatar_image/" + fileName);
        }
    }

    //添加数据
    public void addshops(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter writer = resp.getWriter();
        try {
            String json = JSONUtils.getJSONstr(req);
            ShopsManager shopsManager = new ShopsManager();
            shopsManager.addshops(json);
            writer.write("添加成功，管理员正在审核");
        } catch (IOException e) {
            e.printStackTrace();
            writer.write("数据异常");
        } catch (SQLException e) {
            e.printStackTrace();
            writer.write("获取数据异常");
        }
    }

    //查找分类的商品
    public void getkindshops(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        try {
            String kind = JSONUtils.getStr(req);
            System.out.println(kind);
            ShopsManager shopsManager = new ShopsManager();
            ArrayList<Shops> list = shopsManager.getkindshops(kind);
            String json = JSONUtils.toJSON(list);
            System.out.println(json);
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
            writer.write("获取数据异常");
        } catch (SQLException e) {
            e.printStackTrace();
            writer.write("获取数据异常");
        }
    }

    //查询商家的评分
    public void getsellerscore(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        PrintWriter writer = resp.getWriter();
        String idstr = JSONUtils.getStr(req);
        int id = Integer.valueOf(idstr);
        Double score = ShopsManager.getscore(id);
        System.out.println(score);
        String scorestr = score.toString();
        writer.write(scorestr);
    }
}














