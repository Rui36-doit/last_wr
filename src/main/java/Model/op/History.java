package Model.op;

import java.time.LocalDateTime;

public class History {
    public int id;
    public int seller_id;
    public int buyer_id;
    public String name;
    public String type;
    public String seller_name;
    public String buyer_name;
    public LocalDateTime time;
    public History(){

    }
    public History(int id, int seller_id, int buyer_id, String name,
                   String type, String seller_name, String buyer_name, LocalDateTime time){
        this.id = id;
        this.seller_id = seller_id;
        this.buyer_id = buyer_id;
        this.name = name;
        this.type = type;
        this.seller_name = seller_name;
        this.buyer_name = buyer_name;
        this.time = time;
    }

}
