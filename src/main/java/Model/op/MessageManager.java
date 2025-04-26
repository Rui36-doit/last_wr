package Model.op;

import Model.utils.JDBCUtil;

import java.sql.SQLException;

public class MessageManager {
    public static void addmessage(int userid, String type, Order order, String infore) throws SQLException {
        String sql = " insert into system_infores( content, user_id, type) value (?, ?, ?);";
        String content = getcontent(infore, order);
        int t = JDBCUtil.update(sql, content, userid, type);
        if(t == 0){
            System.out.println("消息添加失败");
        }else {
            System.out.println("消息添加成功");
        }
    }

    public static String getcontent(String infore, Order order){
        String content = null;
        if(infore.equals("senting")){
            content = "您的商品：" + order.shop_name + "正在发货中";
        }else if(infore.equals("waitingget")){
            content = "您的商品：" + order.shop_name + "已到货，请您查收";
        }else if(infore.equals("waitingsend")){
            content = "你已购买商品：" + order.shop_name;
        } else if (infore.equals("hasget")) {
            content = "用户：" + order.buyer_name + "(" + order.buyer_id + ")" + "已签收";
        } else if(infore.equals("backshops")){
            content = "用户：" + order.buyer_name + "(" + order.buyer_id + ")" + "要求退货商品：" + order.shop_name + "请在你的订单处尽快处理";
        }
        return content;
    }
}
