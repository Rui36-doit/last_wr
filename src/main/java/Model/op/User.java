package Model.op;

import Model.utils.Box;
import Model.utils.JDBCUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class User {
    public int id;
    public String name;
    public String password;
    public String email;
    public double money;
    public double score;
    public String phonenumber;
    public String type;
    public String avatar;

    public User(){

    }

    public User(int id ,String name, String password,  String email, double score, double money, String phonenumber, String type, String avatar){
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.money = money;
        this.phonenumber = phonenumber;
        this.type = type;
        this.avatar = avatar;
        this.score = score;
    }

    public User(int id ,String name, String email, double score, double money, String phonenumber, String type, String avatar){
        this.id = id;
        this.name = name;
        this.email = email;
        this.money = money;
        this.phonenumber = phonenumber;
        this.type = type;
        this.avatar = avatar;
        this.score = score;
    }


    //修改自己的信息
    public void updatainfor(String name, String phonenumber, String avatar) throws SQLException {
        this.name = name;
        this.phonenumber = phonenumber;
        this.avatar = avatar;
        saveinfore();
    }

    //保存数据
    public void saveinfore() throws SQLException {
        String sql = "update users set name = ?, phonenumber = ?, avatar = ? where id = ?";
        JDBCUtil.update(sql, name, phonenumber, avatar, id);
    }

    //获取评论
    public ArrayList<Evaluate> showevaluate() throws SQLException {
        ArrayList<Evaluate> list = new ArrayList<>();
        String sql = "select * from evaluate where seller_id = ?";
        Box box = JDBCUtil.search(sql, id);
        ResultSet resultSet = box.getResult();
        while (resultSet.next()){
            Evaluate evaluate = new Evaluate(resultSet.getInt("id"), resultSet.getInt("buyer_id"),
                    resultSet.getInt("seller_id"), resultSet.getString("content"), resultSet.getDouble("score"),
                    resultSet.getString("buyername"), resultSet.getInt("shop_id"), resultSet.getString("shopname"),
                    resultSet.getString("response"));
            list.add(evaluate);
        }
        box.close();
        return list;
    }
    //支付功能
    public boolean pay(double money) throws SQLException {
        if(money > this.money){
            System.out.println("不够钱了");
            return false;
        }
        String sql = "update users set money = money - ? where id = ?";
        int t = JDBCUtil.update(sql, money, id);
        if(t == 0){
            return false;
        }
        return true;
    }
    //删除订单
    public void removeorder(Order order) throws SQLException {
        String sql = "update orders set type = '交易取消' where id = ?;";
        if(order.pay_type.equals("先支付后到货")){
            String sql1 = "update users set money = money + ? where id = ?;";
            int n = JDBCUtil.update(sql1, money, this.id);
            if(n == 0){
                System.out.println("退款失败");
            }
        }
        int t = JDBCUtil.update(sql, order.id);
        if(t == 0){
            System.out.println("删除失败");
        }
    }
    //给商家评分
    public static boolean addscore(double score, int id) throws SQLException {
        score = score - 50;
        String sql = "update users set score = score + ? where id = ?";
        int t = JDBCUtil.update(sql, score*0.05, id);
        if(t == 0){
            return false;
        }
        return true;
    }
    //确认收货
    public double sureorder(Order order) throws SQLException {
        TransationManager.updatatype("已完成", order.id);
        if(order.pay_type.equals("先到货后支付")){
            pay(order.money);
            System.out.println("支付成功");
            return order.money;
        }
        return 0;
    }

}
