$(function() {
    var initFence = [];
    var in_fence = false;
    var nowoverlay = null;
    var beginPolygon = false;
    var beginBj = false;
    var mouse = false;
    //查询数据库中保存的围栏并实例
    $.ajax({
        type : 'GET',
        url : base_url + '/gaode/queryFence?name= ',
        success : function(data){
            if (data.code == 200) {
                //实例围栏
                var fences = data.data;
                for(var i=0;i<fences.length;i++){
                    var pointsVar = fences[i].fencePoints;
                    var points = pointsVar.split(";");
                    initdraw(points);
                }
            } else {
                log.error(data.msg);
            }
        }
    });


    //开启鼠标工具
    document.getElementById('begin').onclick = function(){
        //激活鼠标的点击事件
        if(mouse){
            mouse = false;
            clickOff();
            mouseTool.close(true)//关闭，并清除覆盖物
            $("#begin").val("激活鼠标");
            log.success("鼠标工具已关闭");
        }else{
            mouse = true;
            clickOn();
            $("#begin").val("关闭鼠标");
            log.success("鼠标工具已激活");

        }

    }

    //根据地址查询是否在围栏中
    $("#query").click(function(){
       var address = $("#address").val();
       var city = "南昌";
        $.ajax({
            type : 'POST',
            url : base_url + '/gaode/checkAddress',
            data : {
                'address' : address,
                'city':city
            },
            dataType : "json",
            success : function(data){
                if (data.code == 200) {
                    //查询成功围栏后定位到该围栏，并弹出相关的围栏信息
                    var fenceInfo = data.data;
                    if(fenceInfo.is_in==1){
                        alert("围栏id："+fenceInfo.fence_gid+",围栏名称："+fenceInfo.fence_name);
                    }else{
                        alert("围栏id："+fenceInfo.fence_gid+",距围栏："+fenceInfo.fence_distance);
                    }
                    log.success(data.msg);
                } else {
                    log.error(data.msg);
                }
            }
        });
    });
    //开启围栏绘制
    $("#beginPolygon").click(function(){
        log.success("开启围栏绘制");
        if(beginPolygon){
            log.warn("围栏绘制已开启，请勿重复开启")
        }else{
            beginPolygon = true;
            beginBj = false;
        }
        //mouseTool.close(true);
        draw("polygon");
    });
    //开启标记绘制
    $("#beginBj").click(function(){
        log.success("开启标记绘制");
        if(beginBj){
            log.warn("标记已开启，请勿重复开启")
        }else{
            beginPolygon = false;
            beginBj = true;
        }
        //mouseTool.close(true);
        draw("marker");
    });

    //清除选中的绘图
    document.getElementById('clear').onclick = function(){
        if(nowoverlay!=null){
            var gid = "";
            $.ajax({
                type : 'POST',
                url : base_url + '/gaode/delectFence',
                data : {
                    'gid' : gid
                },
                dataType : "json",
                success : function(data){
                    if (data.code == 200) {
                        map.remove(nowoverlay);
                    } else {
                        log.error(data.msg);
                    }
                }
            });

        }else{
            log.success("请选中需删除的围栏");
        }

    }

    //结束围栏编辑，将缓存中的围栏数据保存至数据库，成功后刷新页面重新加载围栏数据
    document.getElementById('close').onclick = function(){
        /*mouseTool.close(true)//关闭，并清除覆盖物
        //关闭鼠标
        clickOff();*/
        //完成绘制后将向后台发送请求将绘制图形上传平台并保存到数据库
        $.ajax({
            type : 'POST',
            contentType : 'application/json; charset=UTF-8',
            url : base_url + '/gaode/dbSave',
            data : JSON.stringify(fenceRedis),
            dataType : "json",
            success : function(data){
                if (data.code == 200) {
                    location.reload()
                    //界面初始化图层
                    //initdraw(data.data);
                } else {
                    log.error("持久化围栏异常");
                }
            }
        });
    }

    //初始化图层
    function initdraw(list){
        var path = [];
        for(var i=0;i<list.length;i+=1){
            var zb = list[i].split(",");
            path.push(new AMap.LngLat(zb[0],zb[1]));
        }
        /*// 多边形轮廓线的节点坐标数组
        var path = [
            new AMap.LngLat(116.368904,39.913423),
            new AMap.LngLat(116.382122,39.901176),
            new AMap.LngLat(116.387271,39.912501),
            new AMap.LngLat(116.398258,39.904600)
        ];*/

        var polygon = new AMap.Polygon({
            path: path,
            fillColor: '#00b0ff', // 多边形填充颜色
            borderWeight: 3, // 线条宽度，默认为 1
            strokeColor: '#FF33FF', // 线条颜色
            fillOpacity: 0.35,//填充透明度
            strokeOpacity: 0.3 //线透明度
        });

        map.add(polygon);
        initFence.push(polygon);
    }
    //单击地图获取坐标，并判断是否单击点在围栏内在围栏内则不能获取并不能启用鼠标工具
    function showInfoClick(e){
        if(in_fence){
            /*beginDraw = true;//开启鼠标绘制工具
            mouseTool.close(false);
            draw("marker",beginDraw);*/
        }else if(beginPolygon){
            //将当前点击的点并根据点击的顺序标好序号保存到list中
            coordinate.push(e.lnglat.getLng()+','+e.lnglat.getLat());
        }
        //后台发送请求返回地址信息
        var location = e.lnglat.getLng()+','+e.lnglat.getLat();
        $.ajax({
            type : 'POST',
            url : base_url + '/gaode/queryAddressByLocation',
            data : {
                "location": location
            },
            dataType : "json",
            success : function(data){
                if (data.code == 200) {
                    //将返回的地址填到搜索栏中
                    $("#address").val(data.data);
                } else {
                    log.error(data.msg);
                }
            }
        });

    }
    //双击围栏内则选中围栏，右击或单击围栏内取消选中
    function showInfoDbClick(e){
        nowoverlay = e.target;
        //围栏的框线变红
        nowoverlay.fillColor = 'red';
        map.add(nowoverlay);
    }
    /*function showInfoMove(){
        var text = '您移动了您的鼠标！'
        document.querySelector("#text").innerText = text;
    }*/
    function showInfoOver(e){//移入围栏则围栏变色
        in_fence = true;
    }
    function showInfoOut(e){//移除围栏则围栏恢复
        in_fence = false;
    }
    //鼠标右击将围栏存入redis，并实例围栏对象
    function showInfoSave(){
        if (!oEvent) var oEvent=window.event;
        if (oEvent.button==2) {
            if(coordinate.length<3){//获取最新画的围栏
                if(overlays.length>0){
                    var nowPolygon = overlays[overlays.length-1];
                    map.remove(nowPolygon);
                    coordinate = [];
                }
                return;
            }
            //将围栏信息list保存到缓存信息中并将保存的信息返回，围栏中坐标信息不能低于4，否则信息不保存
            $.ajax({
                type : 'POST',
                url : base_url + '/gaode/redisSave',
                contentType : 'application/json; charset=UTF-8',
                data : JSON.stringify(coordinate),
                dataType : "json",
                success : function(data){
                    if (data.code == 200) {
                        //获取缓存返回的key
                        var fenceMap = {};
                        fenceMap.key = data.data;
                        fenceMap.localKey = localKey;
                        fenceMap.name = name;
                        fenceRedis.push(fenceMap);
                        initdraw(coordinate);
                        //清空list
                        coordinate = [];

                    } else {
                        log.error("缓存异常");
                    }
                }
            });
        }

    }
    // 事件绑定
    function clickOn(){

        //获取地图上的坐标
        map.on('click', showInfoClick);
        //map.on('dblclick', showInfoDbClick);
        //map.on('mousemove', showInfoMove);
        map.on('mouseup',showInfoSave);
        //对已画好的围栏对象绑定事件
        for(var i=0;i<initFence.length;i+=1){
            initFence[i].on('dblclick', showInfoDbClick);
            initFence[i].on('mouseover', showInfoOver);
            initFence[i].on('mouseout', showInfoOut);

        }

    }
    // 解绑事件
    function clickOff(){
        log.success("关闭鼠标工具");
        map.off('click', showInfoClick);
        map.off('dblclick', showInfoDbClick);
        //map.off('mousemove', showInfoMove);
        map.off('mouseup',showInfoSave);
        //对已画好的围栏对象绑定事件
        /*for(var i=0;i<overlays.length;i+=1){
            overlays[i].off('mouseout', showInfoOut);
            overlays[i].off('mouseover', showInfoOver);
        }*/
    }
    /*function autoInput(){
        var keywords = document.getElementById("input").value;
        AMap.plugin('AMap.Autocomplete', function(){
            // 实例化Autocomplete
            var autoOptions = {
                city: '全国'
            }
            var autoComplete = new AMap.Autocomplete(autoOptions);
            autoComplete.search(keywords, function(status, result) {
                //调用搜索接口服务
                $.ajax({
                    type : 'POST',
                    url : base_url + '/gaode/queryInputTips',
                    contentType : 'application/json; charset=UTF-8',
                    data : {'keywords':keywords,"city":"南昌"},
                    dataType : "json",
                    success : function(data){
                        if (data.code == 200) {

                        } else {
                            log.error("缓存异常");
                        }
                    }
                });
                // 搜索成功时，result即是对应的匹配数据
                var node = new PrettyJSON.view.Node({
                    el: document.querySelector("#input-info"),
                    data: result
                });
            })
        })
    }*/


});