<template>
 <div>
  <div id="container" style="width:100%; height:900px"></div>
  <div class="info">
      <div class="input-item">
		  <el-input
		    placeholder="关键字"
		    clearable
		    size="small"
		    id = "tipinput"
		  />
		  <el-button @click="queryByAddress">查询</el-button>
		  <el-button @click="clickOn('marker')">标记</el-button>
		  <el-button @click="handleAdd">创建围栏</el-button>
		  <el-button @click="clickOn('colse')">保存</el-button>
      </div>
  </div>
  <!-- 添加或修改电子围栏对话框 -->
  <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
    <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      
      <el-form-item label="围栏名称" prop="fenceName">
        <el-input v-model="form.fenceName" placeholder="请输入围栏名称" />
      </el-form-item>
      <el-form-item label="描述信息" prop="fenceDesc">
        <el-input v-model="form.fenceDesc" placeholder="请输入描述信息" />
      </el-form-item>
	  <el-form-item label="平台key" prop="localKey">
	    <el-input v-model="form.localKey" placeholder="请输入平台key" />
	  </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="clickOn('polygon')">绘制围栏</el-button>
      <el-button @click="cancel">取 消</el-button>
    </div>
  </el-dialog>
 </div>
</template>
<style>
	html,
	body,
	#container {
	  width: 100%;
	  height: 100%;
	}
	.info {
		/* padding: .75rem 1.25rem; */
		margin-bottom: 1rem;
		border-radius: .25rem;
		position: fixed;
		top: 6rem;
		background-color: white;
		width: auto;
		min-width: 22rem;
		border-width: 0;
		right: 1rem;
	}
	.input-item{
	  height: 3rem;
	}
	.btn{
	  width: 6rem;
	  margin: 0 1rem 0 2rem;
	}
</style>

<script type="text/javascript" src="https://webapi.amap.com/maps?v=1.4.15&key=3fa060bd1711d61ee47bb8983d7b1101&plugin=AMap.MouseTool,AMap.Autocomplete,AMap.PlaceSearch"></script>
<script type="text/javascript" src="https://cache.amap.com/lbs/static/addToolbar.js"></script>

<script>
 import { listFence, getFence, delFence, addFence, updateFence,queryByAddr } from "@/api/integration/gaode";
 import $ from 'jquery'
 import Vue from 'vue'
 import VueAMap from 'vue-amap';
 Vue.use(VueAMap);
 var map
 //鼠标工具
 var mouseTool;
 //围栏编辑插件
 var polyEditor;
 //创建右键菜单
 var contextMenu ;
 //地理编码服务
 var geocoder;
 var overlays = [];
 var initFence = [];
 var coordinate = [];
 
 var tempPolygon ;
 export default {
  mounted: function () {
   this.init()
   // 默认开启围栏绘制
   this.draw("polygon");
   //监听围栏绘制事件
   mouseTool.on('draw',function(e){
   	overlays.push(e.obj);
	if(e.obj.CLASS_NAME == 'AMap.Marker'){
		
	}else{
		tempPolygon = new AMap.Polygon({
		    path: e.obj.getPath(),
		    fillColor: '#00b0ff', // 多边形填充颜色
		    borderWeight: 3, // 线条宽度，默认为 1
		    strokeColor: '#FF33FF', // 线条颜色	
		    fillOpacity: 0.35,//填充透明度
		    strokeOpacity: 0.3 //线透明度
		});
		map.add(tempPolygon);
		map.setFitView(polygons);//视口自适应
		console.log(e.obj.getPath());//获取路径/范围
	}
   })
  },
  
  methods: {
	  init: function () {
	  		map = new AMap.Map('container', {
	  		 resizeEnable: true,
	  		 zoom: 14
	  		})
	  		//初始化插件
	  		AMap.plugin(['AMap.ToolBar', 'AMap.Scale','AMap.MouseTool','AMap.Autocomplete',
			'AMap.PlaceSearch'/* ,'AMap.PolyEditor,'*/,'AMap.Geocoder' ], function(){
	  			map.addControl(new AMap.ToolBar())
	  			map.addControl(new AMap.Scale())
	  			mouseTool = new AMap.MouseTool(map);
				contextMenu = new AMap.ContextMenu();
				//右键添加Marker标记
				contextMenu.addItem("添加标记", function (e) {
					var marker = new AMap.Marker({
						map: map,
						position: contextMenuPositon //基点位置
					});
				}, 3);
				geocoder = new AMap.Geocoder({
					// city 指定进行编码查询的城市，支持传入城市名、adcode 和 citycode
					city: '全国'
				}) 
	  		})
			
			
	  },
   //初始化图层
   initdraw(list){
       var path = [];
       for(var i=0;i<list.length;i+=1){
           var zb = list[i].split(",");
           path.push(new AMap.LngLat(zb[0],zb[1]));
       }
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
   },
   /** 新增按钮操作 */
   handleAdd() {
     this.reset();
     this.open = true;
     this.title = "添加电子围栏";
   },
   
   draw(type) {
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
   }, 
   // 事件绑定
   clickOn(type){
        //获取地图上的坐标
		/* mouseTool.marker({
			//同Marker的Option设置
		}); */
		if(type=="marker"){
			this.draw("marker");
			map.on('click', function(e) {
				//将当前点击的点并根据点击的顺序标好序号保存到list中
				coordinate.push(e.lnglat.getLng()+','+e.lnglat.getLat());
				//后台发送请求返回地址信息
				var location = [e.lnglat.getLng(), e.lnglat.getLat()];
				geocoder.getAddress(location, function(status, result) {
					if (status === 'complete' && result.info === 'OK') {
						// result为对应的地理位置详细信息
						$("#tipinput").val(result.regeocode.formattedAddress);
						console.log(result.regeocode.formattedAddress);
					}
				})
			});
		}else if(type == "polygon"){
			this.draw("polygon");
		}else{
			mouseTool.close(true);
		}
		
    },
    // 解绑事件
    
	//根据地址查询围栏信息
	queryByAddress(){
		var address = $("#tipinput").val();
		//根据地址查询坐标信息
		geocoder.getLocation(address, function(status, result) {
			if (status === 'complete' && result.info === 'OK') {
			  // result中对应详细地理坐标信息
			  console.log(result);
			  queryByAddr().then(response => {
			    this.msgSuccess("查询成功");
			    //this.open = false;
			    //this.getList();
			  });
			}
		})
	}
	
  }
 }
</script>