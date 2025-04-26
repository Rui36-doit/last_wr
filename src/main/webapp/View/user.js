new Vue({
    el:"#userapp",
    data(){
        return{
            user:{id: 0, name: "", password:"", email:"", money:0.0, score:0.0, phonenumber:"", type:"", avatar:""},
            OrderUI:{ordersUI: false, theorderUI:false, payUI:false},
            orders:[],
            UI:{shopsUI: true,shopsHeadUI: true, inforUI: false, evaluateUI: false, historyUI: false, scoreUI: false, shopinforUI:false,
            theshopsUI: false, searchUI:false, searchHead: true, myshopsUI:false, myshopinforeUI: false, addUI:false, kindshopsUI: false},
            shop:{id: 0, name:"", price:0.0, sellername:"", photo:"", describe:"", kind:"", number:0,
                time:moment().format('YYYY-MM-DD HH:mm:ss'), sellerid: 0, type:""},
            order:{id:0, shop_id:0, seller_id:0, seller_name:"", shop_name:"",number:0,pay_type:"",buyer_id:0,
                buyer_name:"", type:"", money:0.0},
            evaUI:{myevaUI:false},
            historyUI:{shopUI:false},
            messageUI:false,
            myevaluate:{content:"", score:0.0, buyer_id:0, seller_id:0, buyername:"", shopname:"", response:""},
            evaluates:[],
            histories:[],
            adviceshops:[],
            shop_evaluates:[],
            shop_order:[],
            searchshops:[],
            myshops:[],
            myevaluates:[],
            kindshops:[],
            messagesinfore:[],
            kinds:['电子产品', '服装', '生活用品', '书本'],
            searchkey:"",
            shopfile: null,
            shoppreviewUrl: null,
            n: 0,
            response: "",
            sort:'no',
            seller_score: "",
            infor_type:"有新消息",
            socket: null
        }
    },
    async mounted() {
        //获取用户的数据
        const userresp = await axios.post("http://localhost:8080/the_last_exam_war/userservelt",
            {}, {headers: {'X-Action': "sentinfore"}});
        this.user = userresp.data;
        //建立连接
        await this.initWebSocket()
        //获取用户的消息和历史消息
        const messageresp = await axios.post("http://localhost:8080/the_last_exam_war/inforservelt", this.user.id,
            {headers: {'X-Action': "showinfore", 'Content-Type': 'application/json'}});
        this.messagesinfore = messageresp.data;
        for (let i = 0; i < this.messagesinfore.length; i++) {
            if (this.messagesinfore[i].type === '未读') {
                this.infor_type = '有新消息';
                break;
            }
        }
        console.log('初始 messagesinfore:', this.messagesinfore);
        //获取商品的信息
        axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", {},
            {headers: {'X-Action': "showshops", 'Content-Type': 'application/json'}}).then(resp => {
            if (resp.data === "数据获取异常") {
                alert(resp.data);
            } else {
                this.adviceshops = resp.data;
            }
        });
    },
    beforeDestroy() {
        if (this.previewUrl) {
            URL.revokeObjectURL(this.previewUrl);
        }
        if(this.socket){
            this.socket.close();
        }
    },
    methods: {
        //检查电话号码符不符合格式
        checkphonenumber(phonenumber) {
            const pattern = /^\d+$/;
            if (pattern.test(phonenumber) || phonenumber === '') {
                return true;
            } else {
                return false;
            }
        },
        //提交修改的信息
        submitchangeinfor(){
            axios.post("http://localhost:8080/the_last_exam_war/userservelt", this.user,
                {headers: { 'X-Action': "changeinfor", 'Content-Type': 'application/json' }})
                .then(resp=>{
                    alert(resp.data);
                });
        },
        //跳转到页面
        toinfore(){
            this.UI.shopsUI = false;
            this.UI.inforUI = true;
        },
        //回到主页
        backtoshop(){
            this.UI.shopsUI = true;
            this.UI.searchHead = true;
            this.UI.shopsHeadUI = true;
            this.UI.searchUI = false;
            this.UI.kindshopsUI = false;
            this.UI.inforUI = false;
            this.UI.scoreUI = false;
            this.UI.evaluateUI = false;
            this.UI.historyUI = false;
            this.UI.theshopsUI = false;
            this.UI.shopinforUI = false;
            this.UI.myshopsUI = false;
            this.UI.myshopinforeUI = false;
            this.UI.addUI = false;
            this.OrderUI.payUI = false;
            this.OrderUI.ordersUI = false;
            this.OrderUI.theorderUI = false;
            this.evaUI.myevaUI = false;
            this.historyUI.shopUI = false;
            this.messageUI = false;
            axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", {},
                {headers: { 'X-Action': "showshops", 'Content-Type': 'application/json'}}).
            then(resp => {
                if(resp.data === "数据获取异常"){
                    alert(resp.data);
                }else{
                    this.adviceshops = resp.data;
                }
            });
        },
        //显示评价
        toevaluate(){
            this.UI.evaluateUI = true;
            this.UI.historyUI = false;
        },
        //显示历史记录
        tohistory(){
            this.UI.evaluateUI = false;
            this.UI.historyUI = true;
            //获取历史数据
            axios.post("http://localhost:8080/the_last_exam_war/historyservelt", {},
                {headers: { 'X-Action': "gethistory", 'Content-Type': 'application/json' }})
                .then(resp => {
                    this.histories = resp.data;
                });
        },
        //显示诚信信息
        toscore(){
            this.UI.shopsUI = false;
            this.UI.searchHead = false;
            this.UI.shopsHeadUI = true;
            this.UI.searchUI = false;
            this.UI.inforUI = false;
            this.UI.scoreUI = true;
            this.UI.evaluateUI = true;
            this.UI.historyUI = false;
            this.UI.theshopsUI = false;
            this.UI.myshopinforeUI = false;
            this.OrderUI.payUI = false;
            this.OrderUI.ordersUI = false;
            this.OrderUI.theorderUI = false;
            this.evaUI.myevaUI = false;
            alert("your infore");
            //获取评论数据
            axios.post("http://localhost:8080/the_last_exam_war/userservelt", {},
                {headers: { 'X-Action': "getevaluate", 'Content-Type': 'application/json' }})
                .then(resp => {
                    this.evaluates = resp.data;
                });
        },
        //显示商品信息
        showtheshops(i, id, shopsarr){
            this.UI.shopsUI = false;
            this.UI.searchUI = false;
            this.UI.searchHead = false;
            this.UI.theshopsUI = true;
            this.shop = shopsarr[i];
            axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", id,
                {headers: { 'X-Action': "getshopevaluate", 'Content-Type': 'application/json' }})
                .then(resp => {
                    this.shop_evaluates = resp.data;
                });
            axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", shopsarr[i].sellerid,
                {headers: { 'X-Action': "getsellerscore", 'Content-Type': 'application/json' }})
                .then(resp => {
                    const score = parseFloat(resp.data);
                    if(score >= 90){
                        this.seller_score = "好评如潮";
                    }else if(score >= 80 && score <= 90){
                        this.seller_score = "值得称赞";
                    }else if(score >= 50 && score <= 80){
                        this.seller_score = "海星";
                    }else if(score <= 50) {
                        this.seller_score = "差评";
                    }else if(score < 0){
                        this.seller_score = "待封号，谨慎购买";
                    }
                })
        },
        //查询商品数据
        searchforshops(){
            this.UI.shopsUI = false;
            this.UI.searchUI = true;
            axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", this.searchkey,
                {headers: { 'X-Action': "searchshops", 'sort-type': this.sort, 'Content-Type': 'charset=UTF-8'}})
                .then(resp => {
                    if(resp.data === "获取数据异常"){
                        alert(resp.data);
                    }else {
                        this.searchshops = resp.data;
                    }
                });
        },
        //查询分类商品数据
        searchkindshops(kind){
            this.UI.shopsUI = false;
            this.UI.searchUI = false;
            this.UI.kindshopsUI = true;
            //alert(kind);
            axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", kind,
                {headers: { 'X-Action': "getkindshops", 'Content-Type': 'charset=UTF-8'}})
                .then(resp => {
                    if(resp.data === "获取数据异常"){
                        alert(resp.data);
                    }else {
                        this.kindshops = resp.data;
                    }
                });
        },
        //返回搜索页面
        tosearch(){
            this.UI.shopsUI = false;
            this.UI.searchHead = true;
            this.UI.shopsHeadUI = true;
            this.UI.searchUI = true;
            this.UI.inforUI = false;
            this.UI.scoreUI = false;
            this.UI.evaluateUI = false;
            this.UI.historyUI = false;
            this.UI.theshopsUI = false;
            this.UI.myshopsUI = false;
        },
        //查看我的商品
        showmyshops(){
            this.UI.shopsUI = false;
            this.UI.searchHead = false;
            this.UI.myshopsUI = true;
            this.UI.theshopsUI = false;
            this.UI.myshopinforeUI = false;
            axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", this.user.id,
                {headers: { 'X-Action': "searchmyshops", 'Content-Type': 'charset=UTF-8'}})
                .then(resp => {
                    if(resp.data === "获取数据异常"){
                        alert(resp.data);
                    }else {
                        this.myshops = resp.data;
                    }
                });
        },
        //展示个人商品信息
        showmyshopsinfore(i, id, shopsarr){
            this.UI.shopsUI = false;
            this.UI.searchUI = false;
            this.UI.searchHead = false;
            this.UI.myshopsUI = false;
            this.UI.myshopinforeUI = true;
            this.shop = shopsarr[i];
            axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", id,
                {headers: { 'X-Action': "getshopevaluate", 'Content-Type': 'application/json' }})
                .then(resp => {
                    this.shop_evaluates = resp.data;
                });
        },
        //修改信息
        changeshopinfore(){
            axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", this.shop,
                {headers: { 'X-Action': "changeinfore", 'Content-Type': 'application/json' }})
                .then(resp => {
                    this.shop_evaluates = resp.data;
                })
        },
        //删除商品
        deleteshops(id){
            axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", id,
                {headers: { 'X-Action': "delectshops", 'Content-Type': 'application/json'}})
                .then();
        },
        //显示预览的照片
        handleFileChange(e) {
            const selectedFile = e.target.files[0];
            if (!selectedFile) return;
            this.shopfile = selectedFile;
            // 生成临时预览 URL
            this.shoppreviewUrl = URL.createObjectURL(selectedFile);
        },
        showadd(){
            this.UI.myshopsUI = false
            this.UI.addUI = true;
        },
        //上传商品数据
        async addshops(){
            //数据存入Formdata
            const formData = new FormData;
            formData.append('image', this.shopfile);
            const {data: iamgeurl} = await axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", formData,
                {headers: { 'X-Action': "getphoto",'type': "shops", 'Content-Type': 'multipart/form-data'}})
                this.shop.photo = iamgeurl;
            this.shop.type = "waiting";
            this.shop.sellername = this.user.name;
            this.shop.sellerid = this.user.id;
            const {data: result} = await axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", this.shop,
                {headers: { 'X-Action': "addshops", 'Content-Type': 'application/json' }})
            alert(result);
        },

        //展示订单
        showorder(){
            this.UI.shopsUI = false;
            this.UI.searchHead = false;
            this.UI.shopsHeadUI = true;
            this.UI.searchUI = false;
            this.UI.kindshopsUI = false;
            this.UI.inforUI = false;
            this.UI.scoreUI = false;
            this.UI.evaluateUI = false;
            this.UI.historyUI = false;
            this.UI.theshopsUI = false;
            this.UI.shopinforUI = false;
            this.UI.myshopsUI = false;
            this.UI.myshopinforeUI = false;
            this.UI.addUI = false;
            this.OrderUI.payUI = false;
            this.OrderUI.ordersUI = true;
            this.OrderUI.theorderUI = false;
            this.evaUI.myevaUI = false;
            this.historyUI.shopUI = false;
            this.messageUI = false;
            axios.post("http://localhost:8080/the_last_exam_war/transationservelt", this.user.id,
                {headers: { 'X-Action': "showorder", 'Content-Type': 'application/json' }})
                .then(resp => {
                    if(resp.data === '数据出错了'){
                        alert('数据出错了');
                    }else {
                        this.orders = resp.data;
                    }
                });
        },
        //添加订单
        addorder(){
            if(this.order.number > this.shop.number){
                alert("存货不足");
                return;
            }
            if(this.order.money > this.user.money){
                alert("余额不足");
                return;
            }
            if(this.user.id === this.order.seller_id){
                alert("这是你的商品，无法购买");
                return;
            }
            this.order.shop_id = this.shop.id;
            this.order.seller_id = this.shop.sellerid;
            this.order.seller_name = this.shop.sellername;
            this.order.shop_name = this.shop.name;
            this.order.buyer_name = this.user.name;
            this.order.buyer_id = this.user.id;
            this.order.type = '待发货';
            this.order.money = this.shop.price*this.order.number;
            //alert("osn" + this.order.shop_name + "sn" + this.shop.name);
            //alert("obn" + this.order.buyer_name + "bn" + this.user.name);
            axios.post("http://localhost:8080/the_last_exam_war/transationservelt", this.order,
                {headers: { 'X-Action': "addorder", 'Content-Type': 'application/json' }})
                .then(resp =>{
                    if(resp.data === 'yes'){
                        this.user.money = this.user.money - this.order.money;
                        alert("交易成功");
                    }else{
                        alert("交易失败");
                    }
                });
            //添加历史记录
            axios.post("http://localhost:8080/the_last_exam_war/historyservelt", this.order,
                {headers: { 'X-Action': "addhistory",'Content': "waitingsend", 'Content-Type': 'application/json' }})
                .then(resp => {
                    if(resp.data === 'yes'){
                        alert("已保存历史记录");
                    }else{
                        alert("保存数据失败");
                    }
                })
        },
        pay(){
            this.UI.theshopsUI = false;
            this.OrderUI.payUI = true;
        },
        //返回商品页面
        backtotheshop(){
            this.OrderUI.payUI = false;
            this.UI.theshopsUI = true;
        },
        //取消订单
        removeorder(i, arr){
            if(arr[i].type !== '待发货'){
                alert("商品已经无法退货");
                return;
            }
            axios.post("http://localhost:8080/the_last_exam_war/transationservelt", arr[i],
                {headers: { 'X-Action': "removeorder", 'Content-Type': 'application/json' }})
                .then(resp => {
                    if(resp.data === 'yes'){
                        alert("退货成功");
                    }else {
                        alert("退货失败");
                    }
                })
        },
        //改变时间格式
        formattedDate(dateArray) {
            const adjustedArray = [...dateArray];
            adjustedArray[1] -= 1; // 月份减 1
            return moment(adjustedArray).format('YYYY-MM-DD HH:mm:ss');
        },
        //确认收货
        sureorders(i, arr){
            //收货
            axios.post("http://localhost:8080/the_last_exam_war/transationservelt", arr[i],
                {headers: { 'X-Action': "sureorder", 'Content-Type': 'application/json' }})
                .then(resp => {
                    if(resp.data === 'no'){
                        alert("无法确认收货");
                    }else {
                        alert("成功确认");
                    }
                })
            arr[i].type = "已收货";
            //添加历史记录
            axios.post("http://localhost:8080/the_last_exam_war/historyservelt", arr[i],
                {headers: { 'X-Action': "addhistory",'Content': "hasget", 'Content-Type': 'application/json' }})
        },
        //提交评论
        submitevalue(i, arr){
            this.myevaluate.seller_id = arr[i].seller_id;
            this.myevaluate.buyer_id = this.user.id;
            this.myevaluate.buyername = this.user.name
            this.myevaluate.shopname = arr[i].shop_name;
            this.myevaluate.shop_id = arr[i].shop_id;
            axios.post("http://localhost:8080/the_last_exam_war/userservelt", this.myevaluate,
                {headers: { 'X-Action': "addevalue", 'Content-Type': 'application/json' }})
                .then(resp => {
                    if(resp.data === 'yes'){
                        alert("上传成功");
                    }else {
                        alert("上传失败");
                    }
                })
            axios.post("http://localhost:8080/the_last_exam_war/transationservelt", arr[i].id,
                {headers: { 'X-Action': "sureorder_inner", 'Content-Type': 'application/json' }})
        },
        //回复卖家
        responsebuyer(i, arr){
            //arr[i].response = this.response;
            const newitem = { ...arr[i] };
            newitem.response = this.response;
            axios.post("http://localhost:8080/the_last_exam_war/transationservelt", newitem,
                {headers: { 'X-Action': "addresponse", 'Content-Type': 'application/json' }})
                .then(resp => {
                    if(resp.data === 'yes'){
                        alert("上传成功");
                    }else {
                        alert("上传失败");
                    }
                })
        },
        //展示我的评论
        showmyevalues(){
            this.UI.shopsUI = false;
            this.UI.searchHead = false;
            this.UI.shopsHeadUI = true;
            this.UI.searchUI = false;
            this.UI.inforUI = false;
            this.UI.scoreUI = false;
            this.UI.evaluateUI = false;
            this.UI.historyUI = false;
            this.UI.theshopsUI = false;
            this.UI.shopinforUI = false;
            this.UI.myshopsUI = false;
            this.UI.myshopinforeUI = false;
            this.OrderUI.payUI = false;
            this.OrderUI.ordersUI = false;
            this.OrderUI.theorderUI = false;
            this.evaUI.myevaUI = true;
            this.historyUI.shopUI = false;
            axios.post("http://localhost:8080/the_last_exam_war/transationservelt", this.user.id,
                {headers: { 'X-Action': "showemyvalue", 'Content-Type': 'application/json' }})
                .then(resp => {
                    this.myevaluates = resp.data;
                });
        },
        //查看商品的订单
        showshoporder(){
            this.UI.shopsUI = false;
            this.UI.searchHead = false;
            this.UI.shopsHeadUI = true;
            this.UI.searchUI = false;
            this.UI.inforUI = false;
            this.UI.scoreUI = false;
            this.UI.evaluateUI = false;
            this.UI.historyUI = false;
            this.UI.theshopsUI = false;
            this.UI.shopinforUI = false;
            this.UI.myshopsUI = false;
            this.UI.myshopinforeUI = false;
            this.OrderUI.payUI = false;
            this.OrderUI.ordersUI = false;
            this.OrderUI.theorderUI = false;
            this.evaUI.myevaUI = true;
            this.historyUI.shopUI = true;
            this.inforUI = false;
            axios.post("http://localhost:8080/the_last_exam_war/transationservelt", this.shop,
                {headers: { 'X-Action': "showshoporder", 'Content-Type': 'application/json' }})
                .then(resp => {
                    this.shop_order = resp.data;
                })
        },
        //改变商品的状态
        changeordertype(i, arr){
            const neworder = { ...arr[i] };
            let content = "";
            if(neworder.type === '待发货'){
                neworder.type = '配送中';
                content = "senting";
            }else if(neworder.type === '配送中'){
                neworder.type = '待收货';
                content = "waitingget";
            }else {
                return;
            }
            //改变信息
            axios.post("http://localhost:8080/the_last_exam_war/transationservelt", neworder,
                {headers: { 'X-Action': "changeordertype", 'Content-Type': 'application/json' }})
                .then(resp => {
                    if(resp.data === 'yes'){
                        alert("修改成功");
                    }else {
                        alert("修改失败");
                    }
                })
            //添加历史记录
            axios.post("http://localhost:8080/the_last_exam_war/historyservelt", neworder,
                {headers: { 'X-Action': "addhistory",'Content': content, 'Content-Type': 'application/json' }})
                .then()
        },
        //退出登录
        outlogin(){
            axios.post("http://localhost:8080/the_last_exam_war/userservelt", {},
                {headers: { 'X-Action': "outlogin", 'Content-Type': 'application/json' }})
                .then(resp => {
                    this.user = null;
                    window.location.href = "/the_last_exam_war/View/login.html";
                })
        },
        //展示消息
        showmassage(){
            this.UI.shopsUI = false;
            this.UI.searchHead = false;
            this.UI.shopsHeadUI = true;
            this.UI.searchUI = false;
            this.UI.inforUI = false;
            this.UI.scoreUI = false;
            this.UI.evaluateUI = false;
            this.UI.historyUI = false;
            this.UI.theshopsUI = false;
            this.UI.shopinforUI = false;
            this.UI.myshopsUI = false;
            this.UI.myshopinforeUI = false;
            this.OrderUI.payUI = false;
            this.OrderUI.ordersUI = false;
            this.OrderUI.theorderUI = false;
            this.evaUI.myevaUI = false;
            this.historyUI.shopUI = false;
            this.messageUI = true;
            axios.post("http://localhost:8080/the_last_exam_war/inforservelt", this.user.id,
                {headers: { 'X-Action': "showinfore", 'Content-Type': 'application/json'}})
                .then(resp => {
                    this.messages = resp.data;
                    for(i = 0; i < this.messages.length; i++){
                        if(this.messages[i].type === '未读'){
                            this.messages[i].type === '已读';
                        }
                    }
                });
            this.infor_type = "无新消息";
            axios.post("http://localhost:8080/the_last_exam_war/inforservelt", this.user.id,
                {headers: { 'X-Action': "updatainfore", 'Content-Type': 'application/json'}})
        },
        //连接WebSocket
        initWebSocket(){
            wsUrl = `ws://localhost:8080/the_last_exam_war/system_ws/${this.user.id}`;
            this.socket = new WebSocket(wsUrl);

            this.socket.onopen = () => {
                console.log('连接成功');
            }

            this.socket.onmessage = (event) => {
                this.messagesinfore.push(JSON.parse(event.data));
            }

            this.socket.onclose = () => {
                setTimeout(this.initWebSocket, 5000);
                console.log('重连成功');
            }
        },
        //退货操作
        backshop(i, arr){
            const neworder = { ...arr[i] };
            neworder.type = "已退货";
            axios.post("http://localhost:8080/the_last_exam_war/transationservelt", neworder,
                {headers: { 'X-Action': "backshop", 'Content-Type': 'application/json'}})
                .then(resp => {
                    if(resp.data === "yes"){
                        alert("已退货，请等待商家处理");
                    }else {
                        alert("退货失败");
                    }
                });
            axios.post("http://localhost:8080/the_last_exam_war/historyservelt", neworder,
                {headers: { 'X-Action': "addhistory", 'Content': "backshop", 'Content-Type': 'application/json'}})
        },
        //退款处理
        backmoney(i, arr) {
            const neworder = { ...arr[i] };
            neworder.type = "已退款";
            //更新订单并退款
            if(this.user.money > arr[i].money){axios.post("http://localhost:8080/the_last_exam_war/transationservelt", neworder,
                {headers: { 'X-Action': "backmoney", 'Content': "backshop", 'Content-Type': 'application/json'}})
                .then(resp => {
                    if(resp.data === 'yes'){
                        alert("处理成功");
                    }else{
                        alert("处理失败");
                    }
                })
            }else {
                alert("你的余额不足");
            }

        }
    }
})
