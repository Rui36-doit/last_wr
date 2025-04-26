package Model.op;

import Model.utils.Box;
import Model.utils.JDBCUtil;
import Model.utils.JSONUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ShopsManager {
    //展示商品的信息
    public String showtheshop(int id){
        try {
            String sql = "select * from shops where id = ?";
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Box box = JDBCUtil.search(sql, id);
            ResultSet resultSet = box.getResult();
            resultSet.next();
            LocalDateTime ldt = resultSet.getTimestamp("time").toLocalDateTime();
            Shops shop = new Shops(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getDouble("price"),
                    resultSet.getString("kind"), resultSet.getInt("seller_id"), resultSet.getInt("number"),
                    resultSet.getString("photo"), ldt, resultSet.getString("describe"),
                    resultSet.getString("type"), resultSet.getString("seller_name"));
            String json = objectMapper.writeValueAsString(shop);
            box.close();
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //获取商品的数组
    public ArrayList<Shops> getshoplists(Box box) throws SQLException {
        ArrayList<Shops> list = new ArrayList<>();
        ResultSet resultSet = box.getResult();
        while (resultSet.next()){
            LocalDateTime ldt = resultSet.getTimestamp("time").toLocalDateTime();
            Shops shop = new Shops(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getDouble("price"),
                    resultSet.getString("kind"), resultSet.getInt("seller_id"), resultSet.getInt("number"),
                    resultSet.getString("photo"), ldt, resultSet.getString("describe"),
                    resultSet.getString("type"), resultSet.getString("seller_name"));
            list.add(shop);
        }
        return list;
    }

    //传递商品的信息
    public ArrayList<Shops> getshops() throws SQLException {
        String sql = "select * from shops";
        Box box = JDBCUtil.search(sql);
        ArrayList<Shops> list = getshoplists(box);
        box.close();
        return list;
    }

    //获取待审核的商品
    public ArrayList<Shops> getwaitingshops() throws SQLException {
        String sql = "select * from shops where type = 'waiting' or type = 'no'";
        Box box = JDBCUtil.search(sql);
        ArrayList<Shops> list = getshoplists(box);
        return list;
    }

    //获取商品评论
    public ArrayList<Evaluate> getshopevaluate(int shopid) throws SQLException {
        String sql = "select * from evaluate where shop_id = ?";
        Box box = JDBCUtil.search(sql, shopid);
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
        return list;
    }

    //搜索商品的信息
    public ArrayList<Shops> searchshops(String name, String type, String sort) throws SQLException {
        ArrayList<Shops> list = new ArrayList<>();
        String sql = "select * from shops s where locate(?, s.name) >0 or locate(?, s.kind)>0";
        Box box = JDBCUtil.search(sql, name, name);
        ResultSet resultSet = box.getResult();
        while (resultSet.next()){
            LocalDateTime ldt = resultSet.getTimestamp("time").toLocalDateTime();
            Shops shop = new Shops(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getDouble("price"),
                    resultSet.getString("kind"), resultSet.getInt("seller_id"), resultSet.getInt("number"),
                    resultSet.getString("photo"), ldt, resultSet.getString("describe"),
                    resultSet.getString("type"), resultSet.getString("seller_name"));
            if(type.equals(type)){
                list.add(shop);
            }
        }
        if(sort.equals("by_price_up")){
            Collections.sort(list, new Comparator<Shops>() {
                @Override
                public int compare(Shops o1, Shops o2) {
                    return (int)(o1.price*100 - o2.price*100);
                }
            });
        }else if(sort.equals("by_price_down")){
            Collections.sort(list, new Comparator<Shops>() {
                @Override
                public int compare(Shops o1, Shops o2) {
                    return (int)(o2.price*100 - o1.price*100);
                }
            });
        }
        box.close();
        return list;
    }

    //展示用户的商品信息
    public ArrayList<Shops> searchusershosp(int id) throws SQLException {
        ArrayList<Shops> list = new ArrayList<>();
        String sql = "select * from shops where seller_id = ?";
        Box box = JDBCUtil.search(sql, id);
        ResultSet resultSet = box.getResult();
        while (resultSet.next()){
            LocalDateTime ldt = resultSet.getTimestamp("time").toLocalDateTime();
            Shops shop = new Shops(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getDouble("price"),
                    resultSet.getString("kind"), resultSet.getInt("seller_id"), resultSet.getInt("number"),
                    resultSet.getString("photo"), ldt, resultSet.getString("describe"),
                    resultSet.getString("type"), resultSet.getString("seller_name"));
            list.add(shop);
        }
        box.close();
        return list;
    }

    //删除商品信息
    public int delect(int id) throws SQLException {
        String sql = "delete from shops where id = ?";
        int t = JDBCUtil.update(sql, id);
        return t;
    }

    //添加商品
    public void addshops(String json) throws JsonProcessingException, SQLException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        System.out.println(json);
        Shops shop = objectMapper.readValue(json, Shops.class);
        shop.time = LocalDateTime.now();
        String sql = "insert into shops(name, price, kind, seller_id, number, photo, time, `describe`, type, seller_name) " +
                "value (?,?,?,?,?,?,?,?,?,?)";
        JDBCUtil.update(sql, shop.name, shop.price, shop.kind, shop.sellerid,
                shop.number, shop.photo, shop.time, shop.describe, shop.type, shop.sellername);
        System.out.println(shop.name);
    }
    //根据分类查询
    public ArrayList<Shops> getkindshops(String kind) throws SQLException {
        String sql = "select id, name, price, kind, seller_id, number," +
                " photo, time, `describe`, type, seller_name from shops where kind = ?";
        Box box = JDBCUtil.search(sql, kind);
        ArrayList<Shops> list = getshoplists(box);
        return list;
    }
    //获取用户的信誉分
    public static Double getscore(int id) throws SQLException {
        String sql = "select score from users where id = ?";
        Box box = JDBCUtil.search(sql, id);
        ResultSet resultSet = box.getResult();
        resultSet.next();
        double score = resultSet.getDouble("score");
        return score;
    }


}
