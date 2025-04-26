package Model.op;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Order {
    public int id;
    public int shop_id;
    public int seller_id;
    public int buyer_id;
    public int number;
    public String type;
    public String pay_type;
    public String seller_name;
    public String shop_name;
    public String buyer_name;
    public LocalDateTime sent_time;
    public double money;
    public Order(){

    }
    public Order(int id, int shop_id, int seller_id, int buyer_id, int number, String type, String pay_type,
                 String seller_name, String shop_name, String buyer_name, LocalDateTime sent_time, double money){
        this.id = id;
        this.shop_id = shop_id;
        this.seller_id = seller_id;
        this.buyer_id = buyer_id;
        this.type = type;
        this.pay_type = pay_type;
        this.sent_time = sent_time;
        this.seller_name = seller_name;
        this.buyer_name = buyer_name;
        this.shop_name = shop_name;
        this.money = money;
        this.number = number;
    }
}
