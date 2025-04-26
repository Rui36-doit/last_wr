new Vue({
    el:"#managerapp",
    data(){
        return{
            manager:{id:0, username:""},
            waitingshops:[],
            shop:{name:"", price:0.0, sellername:"", photo:"", describe:"", kind:"", number:0,
                time:moment().format('YYYY-MM-DD HH:mm:ss'), sellerid: 0, type:""},
            UI:{waitingshopsUI: true, theshopUI: false, shopsUI:false, thewaitingshopUI: false, searchheadUI:false, searchUI:false},
            UserUI:{usersUI:false, userUI: false, userorderUI:false, userhistoryUI:false},
            OrderUI:{ordersUI:false, startUI:false, searchUI:false},
            SumUI: false,
            user:{id:0, name:"", email:"", money:0.0, score:0.0, phonenumber:"", type:"", avatar:""},
            timesum:0.0,
            sum:0.0,
            monthValue: this.getCurrentMonth(),
            users:[],
            userorders:[],
            userhistory:[],
            allorder:[],
            searchorders:[],
            searchshops:[],
            allshops:[],
            searchkey:""
        }
    },
    mounted(){
        //获取用户数据
        axios.post("http://localhost:8080/the_last_exam_war/managerservelt", {},
            {headers: { 'X-Action': "sentinfore", 'Content-Type': 'application/json'}})
            .then(resp => {
                this.manager = resp.data;
            });
        //获取商品数据
        axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", {},
            {headers: { 'X-Action': "getwaitingshops", 'Content-Type': 'application/json'}})
            .then(resp => {
                if(resp.data === '数据库出错'){
                    alert(resp.data);
                }else {
                    //this.waitingshops = resp.data;
                    this.waitingshops = resp.data.map(item => {
                        return {
                            ...item,
                            time: moment(item.time).format('YYYY-MM-DD HH:mm:ss')
                        }
                    });
                }
            });

    },
    methods:{
        //展示商品的信息
        showtheshop(i, arr){
            this.UI.shopsUI = false;
            this.UI.theshopUI = true;
            this.shop = arr[i];
        },
        //展示审核商品
        showwaitingshop(i, arr){
            this.UI.waitingshopsUI = false;
            this.UI.thewaitingshopUI = true;
            this.shop = arr[i];
        },
        //返回到审核列表
        backtocheck(){
            this.UI.waitingshopsUI = true;
            this.UI.thewaitingshopUI = false;
        },
        //返回商品页面
        backtoshops() {
            this.UI.shopsUI = true;
            this.UI.theshopUI = false;
        },
        //商品审核通过
        checkshop(type){
            this.shop.type = type;
            const payload = {
                ...this.shop,
                type: type,
                time: moment(this.shop.time, 'YYYY-MM-DD HH:mm:ss').toISOString() // 转为 ISO-8601
            };
            axios.post("http://localhost:8080/the_last_exam_war/managerservelt", payload,
                {headers: { 'X-Action': "checkshops", 'Content-Type': 'application/json'}})
                .then(resp => {
                    alert(resp.data);
                });
        },
        //展示用户们的信息
        showusers(){
            this.UI.searchUI = false;
            this.UI.shopsUI = false;
            this.UI.theshopUI = false;
            this.UI.searchheadUI = false;
            this.UI.waitingshopsUI = false;
            this.UserUI.usersUI = true;
            this.UserUI.userUI = false;
            this.UserUI.userorderUI = false;
            this.UserUI.userhistoryUI = false;
            this.OrderUI.startUI = false;
            this.OrderUI.searchUI = false;
            this.OrderUI.ordersUI = false;
            axios.post("http://localhost:8080/the_last_exam_war/managerservelt", {},
                {headers: { 'X-Action': "showusers", 'Content-Type': 'application/json'}})
                .then(resp => {
                    this.users = resp.data;
                })
        },
        //展示用户的信息
        showuserinfore(i, arr){
            this.UserUI.usersUI = false;
            this.UserUI.userUI = true;
            this.user = arr[i];
        },
        //封禁用户的账号
        block_the_user(){
            this.user.type = "no";
            axios.post("http://localhost:8080/the_last_exam_war/managerservelt", this.user,
                {headers: { 'X-Action': "blockuser", 'Content-Type': 'application/json'}})
                .then(resp => {
                    if(resp.data === 'yes'){
                        alert("封禁成功");
                    }else {
                        alert("封禁失败");
                    }
                });
        },
        //解封账号
        up_the_user(){
            this.user.type = "ok";
            axios.post("http://localhost:8080/the_last_exam_war/managerservelt", this.user,
                {headers: { 'X-Action': "upkuser", 'Content-Type': 'application/json'}})
                .then(resp => {
                    if(resp.data === 'yes'){
                        alert("已解封");
                    }else {
                        alert("解封失败");
                    }
                })
        },
        //显示审核商品的信息
        showwaitingshops(){
            this.UI.waitingshopsUI = true;
            this.UI.theshopUI = false;
            this.UserUI.usersUI = false;
            this.UserUI.userUI = false;
            this.OrderUI.ordersUI = false;
            this.OrderUI.startUI = false;
            //获取待审核的商品
            axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", {},
                {headers: { 'X-Action': "getwaitingshops", 'Content-Type': 'application/json'}})
                .then(resp => {
                    if(resp.data === '数据库出错'){
                        alert(resp.data);
                    }else {
                        //this.waitingshops = resp.data;
                        this.waitingshops = resp.data.map(item => {
                            return {
                                ...item,
                                time: moment(item.time).format('YYYY-MM-DD HH:mm:ss')
                            }
                        });
                    }
                })
        },
        //查询用户的订单
        showuserorder(){
            this.UserUI.userorderUI = true;
            this.UserUI.userhistoryUI = false;
            axios.post("http://localhost:8080/the_last_exam_war/managerservelt", this.user,
                {headers: { 'X-Action': "getuserorder", 'Content-Type': 'application/json'}})
                .then(resp => {
                    this.userorders = resp.data;
                })
        },
        //改变时间格式
        formattedDate(dateArray) {
            const adjustedArray = [...dateArray];
            adjustedArray[1] -= 1; // 月份减 1
            return moment(adjustedArray).format('YYYY-MM-DD HH:mm:ss');
        },
        //获取用户方历史交易记录
        showhistory(){
            this.UserUI.userorderUI = false;
            this.UserUI.userhistoryUI = true;
            axios.post("http://localhost:8080/the_last_exam_war/managerservelt", this.user,
                {headers: { 'X-Action': "getuserhistory", 'Content-Type': 'application/json'}})
                .then(resp => {
                    this.userhistory = resp.data;
                })
        },
        //获取所有的订单
        getallorder(){
            this.UI.waitingshopsUI = false;
            this.UI.theshopUI = false;
            this.OrderUI.ordersUI = true;
            this.OrderUI.startUI = true;
            axios.post("http://localhost:8080/the_last_exam_war/transationservelt", this.user,
                {headers: { 'X-Action': "showallorder", 'Content-Type': 'application/json'}})
                .then(resp => {
                    this.allorder = resp.data;
                })
        },
        //查找订单
        searchorder(){
            this.OrderUI.searchUI = true;
            this.OrderUI.startUI = false;
            alert(this.searchkey);
            axios.post("http://localhost:8080/the_last_exam_war/managerservelt", this.searchkey,
                {headers: { 'X-Action': "searchorders"}})
                .then(resp => {
                    this.searchorders = resp.data;
                })
        },
        //显示所有的商品
        showallshops(){
            this.UI.searchheadUI = true;
            this.UI.searchUI = false;
            this.UI.waitingshopsUI = false;
            this.UI.shopsUI = true;
            axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", {},
                {headers: { 'X-Action': "showshops", 'Content-Type': 'application/json'}})
                .then(resp => {
                    this.allshops = resp.data;
                    })
        },
        //下架商品信息
        blockshop(){
            axios.post("http://localhost:8080/the_last_exam_war/managerservelt", this.shop,
                {headers: { 'X-Action': "blockshops", 'Content-Type': 'application/json'}})
                .then(resp => {
                    if(resp.data === 'yes'){
                        alert("下架成功，请刷新页面");
                    }else {
                        alert("下架失败");
                    }
                })
        },
        //上架商品
        upshop(){
            axios.post("http://localhost:8080/the_last_exam_war/managerservelt", this.shop,
                {headers: { 'X-Action': "upshops", 'Content-Type': 'application/json'}})
                .then(resp =>{
                    if(resp.data === 'yes'){
                        alert("上架成功，请刷新页面");
                    }else {
                        alert("上架失败");
                    }
                })
        },
        //搜索商品
        searchforshops(){
            this.UI.shopsUI = false;
            this.UI.searchUI = true;
            axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", this.searchkey,
                {headers: { 'X-Action': "searchshops", 'sort-type': 'no', 'Content-Type': 'charset=UTF-8'}})
                .then(resp => {
                    if(resp.data === "获取数据异常"){
                        alert(resp.data);
                    }else {
                        this.searchshops = resp.data;
                    }
                });
        },
        //获取销售总额
        getallmoney(){
            this.UI.searchUI = false;
            this.UI.shopsUI = false;
            this.UI.theshopUI = false;
            this.UI.searchheadUI = false;
            this.UI.waitingshopsUI = false;
            this.UserUI.usersUI = false;
            this.UserUI.userUI = false;
            this.UserUI.userorderUI = false;
            this.UserUI.userhistoryUI = false;
            this.OrderUI.startUI = false;
            this.OrderUI.searchUI = false;
            this.OrderUI.ordersUI = false;
            this.SumUI = true;
            axios.post("http://localhost:8080/the_last_exam_war/managerservelt", {},
                {headers: { 'X-Action': "getallmoney"}})
                .then(resp => {
                    this.sum = Number(resp.data);
                })
        },
        //获取当前的时间
        getCurrentMonth() {
            const now = new Date();
            const year = now.getFullYear();
            const month = String(now.getMonth() + 1).padStart(2, '0');
            return `${year}-${month}`;
        },
        //通过月份获取信息
        getsumbymonth(){
            axios.post("http://localhost:8080/the_last_exam_war/managerservelt", this.monthValue,
                {headers: { 'X-Action': "getsumbymonth", 'Content-Type': 'charset=UTF-8'}})
                .then(resp => {
                    this.timesum = Number(resp.data);
                })
        }
    }
})