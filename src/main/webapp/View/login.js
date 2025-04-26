new Vue({
    el:"#loginapp",
    data(){
        return{
            count:3,
            type: "login",
            login: {email: "", password: "", warn: 1},
            registerinfor:{passwordagain:""},
            warn1: 1,
            UI:{loginissee: false, inforissee: false,getinforsee: true, managerloginUI: false},
            //UI:{loginissee: false, inforissee: false, classsee: false, getinforsee: false, addclassUI: true},
            user:{id: 0, name: "", password:"", email:"", money:0.0, score:0.0, phonenumber:"", type:"", avatar:""},
            manager:{id: 0, username:"", password:""},
            userfile: null,
            userpreviewUrl: null,
        }
    },
    methods:{
        loginUI(){
            this.count = 1;
            this.type = 'login';
        },
        registerUI(){
            this.count = 2;
            this.type = 'register';
        },
        //对比注册段密码一不一样
        compare(){
            if(this.register.passwordagain && this.register.password !== this.register.passwordagain){
                this.register.warning1 = 1;
            }else{
                this.register.warning1 = 0;
            }
            if(this.register.username === '' || this.register.password === '' || this.register.passwordagain ===''){
                this.register.warn = 1;
            }else{
                this.register.warn = 0;
            }
        },
        isEmptyregister(){
            if(this.register.password === '' || this.register.username === '' || this.register.passwordagain === ''){
                return true;
            }else{
                return false;
            }
        },
        //显示预览的图片
        handleFileChange(e) {
            const selectedFile = e.target.files[0];
            if (!selectedFile) return;
            this.userfile = selectedFile;
            // 生成临时预览 URL
            this.userpreviewUrl = URL.createObjectURL(selectedFile);
        },
        //提交注册时的数据
        doregister(){
            if(this.isEmptyregister()===false&&
                this.user.password===this.registerinfor.passwordagain){
                var infore = {
                    "email": this.user.email,
                    "password": this.register.password,
                };
                axios.post("http://localhost:8080/the_last_exam_war/userservelt", infore,{headers: { 'X-Action': "check" }}).
                    then(resp=>{
                        if(resp.data === "yes"){
                            alert("注册成功");
                            this.UI.loginissee = false;
                            this.count = 3;
                            this.UI.getinforsee = true;
                        }else{
                            alert("该用户已存在");
                        }
                });
            }else{
                alert("输入格式不对");
            }
        },
        //检查账号符不符合格式
        /*
        checkname(name) {
            const pattern = /^\d+$/;
            if (pattern.test(name) || name === '') {
                this.warn1 = 1;
                return true;
            } else {
                this.warn1 = 0;
                return false;
            }
        },

         */
        //提交登录信息
        submitinfor() {
            if(this.login.password === '' || this.login.username === '' || this.warn1 === 0){
                alert("输入格式不对");
            }else {
                var logindate = {
                    "email": this.login.email,
                    "password": this.login.password,
                }
                axios.post("http://localhost:8080/the_last_exam_war/userservelt", logindate,{headers: { 'X-Action': "login" }}).
                then(resp => {
                    if(resp.data === "no"){
                        alert("你的账号不存在或密码错误");
                    }else if(resp.data === "你已被封号，无法登录"){
                        alert(resp.data);
                    }else{
                        //添加到localStorage里面
                        window.location.href = "/the_last_exam_war/View/user.html";
                    }
                })
            }
        },
        //管理员登录
        managerlogin(){
            var infor = {
                "email": this.manager.username,
                "password": this.manager.password,
            }
            axios.post("http://localhost:8080/the_last_exam_war/managerservelt", infor,
                {headers: { 'X-Action': 'login', 'Content-Type': 'application/json'}})
                .then(resp => {
                    if(resp.data === "yes"){
                        window.location.href = "/the_last_exam_war/View/manager.html";
                    }else {
                        alert("密码错误或账号不存在");
                    }
                })
        },
        //管理员登录页面
        managerloginform(){
            this.count = 3;
            this.UI.loginissee = false;
            this.UI.managerloginUI = true;
        },
        async submituser(){
            const formData = new FormData();
            formData.append('image', this.userfile);
            const {data: iamgeurl} = await axios.post("http://localhost:8080/the_last_exam_war/shopsservelt", formData,
                {headers: { 'X-Action': "getphoto",'type': "avatar", 'Content-Type': 'multipart/form-data'}})
            this.user.avatar = iamgeurl;
            this.user.password = this.registerinfor.passwordagain;
            const {data: result} = await axios.post("http://localhost:8080/the_last_exam_war/userservelt", this.user,
                {headers: { 'X-Action': "register", 'Content-Type': 'application/json' }})
            alert(result);
        }
    }
});
