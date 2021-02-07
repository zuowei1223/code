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
        #panel {
            position: absolute;
            background-color: white;
            max-height: 90%;
            overflow-y: auto;
            top: 10px;
            right: 10px;
            width: 280px;
        }
    </style>
    <title>鼠标工具绘制</title>
</head>
<body>
<div id='container'></div>
<div id="panel"></div>
<div class='info' >操作说明：绘制电子围栏并放回绘制点的坐标
    <div class="input-item">
        <div class="input-item-prepend">
            <span class="input-item-text" style="width:8rem;">请输入关键字</span>
        </div>
        <input id='input' type="text">
        <p><span id="input-info"></span></p>
    </div>
</div>
<div class='info' id="text" style="top: 8rem;">操作提示
    <div class='info' id="text0" style="right: 25rem;">
        <div class="input-item">
            <div class="input-item-prepend">
                <span class="input-item-text" style="width:8rem;">请输入查询地址</span>
            </div>
            <input id='address' type="text">
            <input id="query" type="button" class="btn" value="查询" />
        </div>
    </div>
</div>
<div class="input-card" style="width:16rem">
    <h4>地图点击相关事件</h4>
    <div>

    </div>
</div>
<div class="input-card" style='width: 50rem;'>
    <div class="input-item">
        <input id="begin" type="button" class="btn" value="激活鼠标" />
        <input id="beginPolygon" type="button" class="btn" value="绘制围栏" />
        <input id="beginBj" type="button" class="btn" value="标记" />
        <input id="clear" type="button" class="btn" value="删除围栏" />
        <input id="close" type="button" class="btn" value="结束并保存" />
        <!-- <input id="clickOn" type="button" class="btn" value="绑定鼠标" />
        <input id="clickOff" type="button" class="btn" value="解绑鼠标" /> -->
    </div>
</div>
<script src="https://a.amap.com/jsapi_demos/static/demo-center/js/demoutils.js"></script>
<link rel="stylesheet" href="https://cache.amap.com/lbs/static/main1119.css"/>
<script type="text/javascript" src="https://webapi.amap.com/maps?v=1.4.15&key=3fa060bd1711d61ee47bb8983d7b1101&plugin=AMap.MouseTool,AMap.Autocomplete,AMap.PlaceSearch"></script>
<script type="text/javascript" src="https://cache.amap.com/lbs/static/addToolbar.js"></script>
<script type="text/javascript">
    var base_url = '${request.contextPath}';
    //初始化地图
    var map = new AMap.Map('container',{
        zoom:14,
        resizeEnable: true
    });
    //初始化鼠标工具
    var mouseTool = new AMap.MouseTool(map);
    mouseTool.close(true)//默认鼠标绘制关闭
    var beginDraw = false;
    var localKey ='${localKey}';
    var fenceRedis = [];
    var name = '${name}';
    var coordinate = [];
    //监听draw事件可获取画好的覆盖物
    var overlays = [];
    mouseTool.on('draw',function(e){
        overlays.push(e.obj);
    })


    function draw(type) {
        /*if (beginDraw != true) {
            return;
        }*/
        switch(type){
            case 'marker':{
                mouseTool.marker({
                    //同Marker的Option设置
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
        }
    }

    //输入提示
    var autoOptions = {
        input: "input"
    };
    var auto = new AMap.Autocomplete(autoOptions);
    var placeSearch = new AMap.PlaceSearch({
        map: map
    });

    //构造地点查询类，根据选择的地址进行关键字查询
    AMap.event.addListener(auto, "select", select);//注册监听，当选中某条记录时会触发
    function select(e) {
        //判断是否存在详细地址，存在则定位不存在则定位到区域
        if(e.poi.address.length!=0){
            var address = e.poi.district+e.poi.address[0]
            $("#input").val(address);
        }else{
            var district = e.poi.district;

        }

    }


</script>
<!-- jQuery 2.1.4 -->
<script src="${request.contextPath}/static/adminlte/bower_components/jquery/jquery.min.js"></script>
<!-- Bootstrap 3.3.5 -->
<script src="${request.contextPath}/static/adminlte/bower_components/bootstrap/js/bootstrap.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="${request.contextPath}/static/adminlte/bower_components/datatables.net-bs/js/dataTables.bootstrap.min.js"></script>
<script src="${request.contextPath}/static/js/gaode.js"></script>
</body>
</html>