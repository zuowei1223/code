<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="chrome=1">
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no, width=device-width">
    <link rel="stylesheet" href="https://a.amap.com/jsapi_demos/static/demo-center/css/demo-center.css" type="text/css">
    <style>
        html,body,#container{
            height: 100%
        }
        .input-item{
            height: 3rem;
        }
        .btn{
            width: 6rem;
            margin: 0 1rem 0 2rem;
        }
        .input-text{
            width: 4rem;
            margin-right:1rem;
        }
    </style>
    <title>鼠标工具绘制</title>
</head>
<body>
<div id='container'></div>
<div class='info' id="text">操作说明：绘制电子围栏并放回绘制点的坐标
    <div class="input-item">
        <div class="input-item-prepend">
            <span class="input-item-text" style="width:8rem;">请输入关键字</span>
        </div>
        <input id='tipinput' type="text">
    </div>
</div>
<div class="input-card" style="width:16rem">
    <h4>地图点击相关事件</h4>
    <div>

    </div>
</div>
<div class="input-card" style='width: 50rem;'>
    <div class="input-item">
        <input type="radio" name='func' value='marker'><span class="input-text">画点</span>
        <input type="radio" name='func' value='polyline'><span class="input-text">画折线</span>
        <input type="radio" name='func' checked="" value='polygon'><span class="input-text" style='width:5rem;'>画多边形</span>
        <input type="radio" name='func' value='rectangle'><span class="input-text">画矩形</span>
        <input type="radio" name='func' value='circle'><span class="input-text">画圆</span>
    </div>
    <div class="input-item">
        <input id="begin" type="button" class="btn" value="开始绘制围栏" />
        <input id="clear" type="button" class="btn" value="清空围栏" />
        <input id="clear" type="button" class="btn" value="清空围栏" />
        <input id="close" type="button" class="btn" value="关闭绘制围栏" />
        <!-- <input id="clickOn" type="button" class="btn" value="绑定鼠标" />
        <input id="clickOff" type="button" class="btn" value="解绑鼠标" /> -->
    </div>
</div>
<script src="https://webapi.amap.com/maps?v=1.4.15&key=a33e36c0e15b373a2ebd7fc4aa1ec0c2&plugin=AMap.MouseTool"></script>
<script src="https://a.amap.com/jsapi_demos/static/demo-center/js/demoutils.js"></script>
<script type="text/javascript">
    var map = new AMap.Map('container',{
        zoom:14
    });
    var beginDraw = false;
    var key =
    var mouseTool = new AMap.MouseTool(map);
    mouseTool.close(true)//默认鼠标绘制关闭
    //监听draw事件可获取画好的覆盖物
    var overlays = [];
    mouseTool.on('draw',function(e){
        overlays.push(e.obj);
    })

    var coordinate = [];
    function draw(type,beginDraw){
        if(beginDraw!=true){
            return;
        }
        switch(type){
            case 'marker':{
                mouseTool.marker({
                    //同Marker的Option设置
                });
                break;
            }
            case 'polyline':{
                mouseTool.polyline({
                    strokeColor:'#80d8ff'
                    //同Polyline的Option设置
                });
                break;
            }
            case 'polygon':{
                mouseTool.polygon({
                    fillColor:'#00b0ff',
                    strokeColor:'#80d8ff'
                    //同Polygon的Option设置
                });
                break;
            }
            case 'rectangle':{
                mouseTool.rectangle({
                    fillColor:'#00b0ff',
                    strokeColor:'#80d8ff'
                    //同Polygon的Option设置
                });
                break;
            }
            case 'circle':{
                mouseTool.circle({
                    fillColor:'#00b0ff',
                    strokeColor:'#80d8ff'
                    //同Circle的Option设置
                });
                break;
            }
        }
    }
    var radios = document.getElementsByName('func');
    for(var i=0;i<radios.length;i+=1){
        radios[i].onchange = function(e){
            draw(e.target.value,beginDraw)
        }
    }
    /* draw('marker') */
    document.getElementById('begin').onclick = function(){

        beginDraw = true;//开启鼠标绘制工具
        mouseTool.close(false);
        for(var i=0;i<radios.length;i+=1){
            if(radios[i].checked == true){
                draw(radios[i].defaultValue,beginDraw)
            }
        }
        clickOn();
    }
    document.getElementById('clear').onclick = function(){
        map.remove(overlays)
        overlays = [];
    }
    document.getElementById('close').onclick = function(){
        beginDraw = false;//关闭鼠标绘制工具
        mouseTool.close(true)//关闭，并清除覆盖物
        for(var i=0;i<radios.length;i+=1){
            radios[i].checked = false;
        }
        clickOff();
        //完成绘制后将向后台发送请求将绘制图形上传平台并保存到数据库
        $.ajax({
            type : 'POST',
            url : base_url + '/gaode/dbSave',
            data : {
                'key' : key
            },
            dataType : "json",
            success : function(data){
                if (data.code == 200) {
                    //界面初始化图层
                    initdraw(data.content);
                } else {
                    log.error("缓存异常");
                }
            }
        });
    }

    //初始化图层
    function initdraw(list){
        var path = [];
        for(var i=0;i<list.length;i+=1){
            path.push(new AMap.LngLat(list[i]));
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
            fillColor: '#fff', // 多边形填充颜色
            borderWeight: 2, // 线条宽度，默认为 1
            strokeColor: 'red', // 线条颜色
        });

        map.add(polygon);
    }
    //按钮绑定
    function showInfoClick(e){
        var text = '您在 [ '+e.lnglat.getLng()+','+e.lnglat.getLat()+' ] 的位置单击了地图！'
        document.querySelector("#text").innerText = text;
        //将当前点击的点并根据点击的顺序标好序号保存到list中
        coordinate.push(e.lnglat.getLng()+'-'+e.lnglat.getLat());

    }
    function showInfoDbClick(e){
        var text = '您在 [ '+e.lnglat.getLng()+','+e.lnglat.getLat()+' ] 的位置双击了地图！'
        document.querySelector("#text").innerText = text;
    }
    function showInfoMove(){
        var text = '您移动了您的鼠标！'
        document.querySelector("#text").innerText = text;
    }
    function showInfoSave(){
        var text = '围栏已保存到list中';
        document.querySelector("#text").innerText = text;
        //将围栏信息list保存到缓存信息中，围栏中坐标信息不能低于4，否则信息不保存
        $.ajax({
            type : 'POST',
            url : base_url + '/gaode/redisSave',
            data : {
                'coordinate' : coordinate
            },
            dataType : "json",
            success : function(data){
                if (data.code == 200) {
                    //获取缓存返回的key
                    key = data.content;
                    //清空list
                    coordinate = [];
                } else {
                    log.error("缓存异常");
                }
            }
        });
    }
    // 事件绑定
    function clickOn(){
        log.success("绑定事件!");
        map.on('click', showInfoClick);
        map.on('dblclick', showInfoDbClick);
        map.on('mousemove', showInfoMove);
        map.on('mousedown',showInfoSave);
    }
    // 解绑事件
    function clickOff(){
        log.success("解除事件绑定!");
        map.off('click', showInfoClick);
        map.off('dblclick', showInfoDbClick);
        map.off('mousemove', showInfoMove);
        map.on('mousedown',showInfoSave);
    }

    // 给按钮绑定事件
    document.getElementById("clickOn").onclick = clickOn;
    document.getElementById("clickOff").onclick = clickOff;

    AMap.plugin('AMap.Autocomplete', function(){
        /* // 实例化Autocomplete
        var autoOptions = {
        // input 为绑定输入提示功能的input的DOM ID
            input: 'tipinput'
        }
        var autoComplete= new AMap.Autocomplete(autoOptions);
        // 无需再手动执行search方法，autoComplete会根据传入input对应的DOM动态触发search */

        // 实例化Autocomplete
        var autoOptions = {
            //city 限定城市，默认全国
            input: 'tipinput'
        }
        var autoComplete= new AMap.Autocomplete(autoOptions);
        autoComplete.search("南昌", function(status, result) {
            // 搜索成功时，result即是对应的匹配数据
        })



    })

</script>
</body>
</html>