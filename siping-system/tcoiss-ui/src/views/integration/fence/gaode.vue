<template>
  <div class="app-container" >
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="70px">
      <el-form-item label="围栏名称" prop="fenceName">
        <el-input
          v-model="queryParams.fenceName"
          placeholder="请输入名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
          :disabled="true"
        />
      </el-form-item>
      <el-form-item prop="cityCode" label="城市">
        <el-select v-model="cityCode" placeholder="请选择城市" :disabled="true" >
          <el-option
            v-for="dict in cityOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="分组id" prop="pointsGroupId">
        <el-input
          v-model="queryParams.pointsGroupId"
          placeholder="分组id"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd('draw')"
            :disabled = "openIsAdd"
            v-hasPermi="['webservice:gaode:add']"
          >添加手工围栏</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd('qy')"
            :disabled = "openIsAdd"
            v-hasPermi="['webservice:gaode:add']"
          >添加区域围栏</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="el-icon-edit"
            size="mini"
            @click="handleUpdate"
            :disabled = "openIsEdit "
            v-hasPermi="['webservice:gaode:edit']"
          >开启编辑</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            @click="handleDeleteCache"
            v-hasPermi="['webservice:gaode:remove']"
          >清除缓存</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            @click="handleDelete"
            :disabled = "openIsDetele"
            v-hasPermi="['webservice:gaode:remove']"
          >删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-save"
            size="mini"
            @click="handleSave()"
            :disabled = "openIsSave"
            v-hasPermi="['webservice:gaode:add']"
          >保存</el-button>
        </el-col>
      </el-row>
    <div id = "dialog">
      <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
        <el-form ref="form" :model="form" :rules="rules" label-width="80px">
          <!--<el-form-item label="市" prop="city">
            <el-select v-model="form.city" placeholder="请选择市" >
              <el-option
                v-for="dict in cityOptions"
                :key="dict.dictValue"
                :label="dict.dictLabel"
                :value="parseInt(dict.dictValue)"
              ></el-option>
            </el-select>
          </el-form-item>-->
          <el-form-item  label="行政区" prop="district" >
            <el-select v-model="form.district" placeholder="请选择行政区">
              <el-option
                v-for="district in districtOptions"
                :key="district.adcode"
                :label="district.name"
                :value="district.adcode"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <div id= "edit" slot="footer" class="dialog-footer">
          <el-button type="primary" @click="drawDistrict">确定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-dialog>
    </div>
    <div id="container" >
    </div>

  </div>

</template>
<style>
  html,
  body,
  #container {
    width: 100%;
    height: 600px;
  }
</style>
<link rel="stylesheet" href="https://a.amap.com/jsapi_demos/static/demo-center/css/demo-center.css" />
<script type="text/javascript" src="https://webapi.amap.com/maps?v=2.0&key=3fa060bd1711d61ee47bb8983d7b1101&plugin=AMap.MouseTool"></script>
<script src="https://a.amap.com/jsapi_demos/static/demo-center/js/demoutils.js"></script>
<script>
  import { listFencePoints, listCache,getFencePoints, getFence,delFencePoints, deleteCache,fencePointsCache,addFencePoints, updateFencePoints,getDistrictByCity,districtCache } from "@/api/integration/gaode";
  import $ from 'jquery';
  import Vue from 'vue';
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
  //修改的围栏对象
  var editPolygon = null ;
  var cachePolygon = [];
  //查询出来的围栏对象
  var queryFence = null;
  var infoWindow ;

  var city = "";

  export default {
    mounted: function () {
    },
    name: "gaode",
    components: {
    },
    data() {
      return {
        // 遮罩层
        loading: true,
        // 选中数组
        ids: [],
        // 非单个禁用
        single: true,
        // 非多个禁用
        multiple: true,
        // 显示搜索条件
        showSearch: true,
        // 总条数
        total: 0,
        cityCode: null,
        // 电子围栏表格数据
        fenceList: [],
        provinceOptions: [],
        cityOptions: [],
        districtOptions: [],
        openName: false,
        openIsAdd: false,
        openIsEdit: true,
        openIsDetele: true,
        openIsSave: true,
        modelType: "lookUp",
        // 弹出层标题
        title: "",
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          fenceName: null,
          fenceId:null,
          pointsGroupId:null,
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {
          district: [
            { required: true, message: "区域不能为空", trigger: "blur" }
          ]
        }      };
    },

    created() {
      //先查询所有围栏坐标，id不为空则查询指定围栏
      const fenceId = this.$route.params && this.$route.params.fenceId;
      this.getFence(fenceId);
      this.getDicts("province").then(response => {
        this.provinceOptions = response.data;
      });
      this.getDicts("city").then(response => {
        this.cityOptions = response.data;
      });
      infoWindow = new AMap.InfoWindow({offset: new AMap.Pixel(0, -30)});
    },
    methods: {
      /** 初始化地图插件*/
      init: function () {
        map = new AMap.Map('container', {
          resizeEnable: true,
          /*center: [116.471354, 39.994257],*/
          zoom: 11,
          showIndoorMap: false
        })
        console.log(city);
        map.setCity(city);
        /*var bounds = map.getBounds();
        map.setLimitBounds(bounds);*/
        // 初始化地图插件
        AMap.plugin(['AMap.ToolBar', 'AMap.Scale','AMap.MouseTool','AMap.Autocomplete',
          'AMap.PlaceSearch' ], function(){
          map.addControl(new AMap.ToolBar())
          map.addControl(new AMap.Scale())
          mouseTool = new AMap.MouseTool(map);
        })

        //初始化地图组件
        mouseTool.on('draw',function(e){
          if(e.obj.CLASS_NAME == 'AMap.Marker'){

          }else{
            this.openIsSave = false;
            this.openIsAdd = false;
            var path = e.obj.getPath();
            var drawPoints = [];
            for(var i=0;i<path.length;i++){
              var fencePoints = {};
              fencePoints.pointX = path[i].getLng();
              fencePoints.pointY = path[i].getLat();
              drawPoints.push(fencePoints);
            }
            var pointVo = {};
            pointVo.fenceName = queryFence.fenceName;
            pointVo.drawPoints = drawPoints;
            fencePointsCache(pointVo).then(response => {
              var polygon = new AMap.Polygon({
                path: path,
                fillColor: "#13ffff", // 多边形填充颜色
                strokeColor: "#ffae0c", // 线条颜色
                strokeWeight: 6,
                fillOpacity: 0.4,//填充透明度
                strokeOpacity: 0.4, //线透明度
                zIndex: 50,
                bubble: true,
              });
              map.add(polygon);
              cachePolygon.push(polygon)
            });
              //关闭鼠标绘制工具
              mouseTool.close(true);
            }
          });

      },

      //初始化图层,
      initDraw: function(list,fillColor,strokeColor){
        var path = [];
        for(var i=0;i<list.length;i+=1){
          path.push(new AMap.LngLat(list[i].pointX,list[i].pointY));
        }
        var polygon = new AMap.Polygon({
          path: path,
          fillColor: fillColor, // 多边形填充颜色
          strokeWeight: 5,
          strokeColor: strokeColor, // 线条颜色
          strokeOpacity: 0.4,
          fillOpacity: 0.4,
          zIndex: 50,
          bubble: true,
          extData:{
            id: this.queryParams.pointsGroupId
          }
        });
        map.add(polygon);
        polygon.content = '分组名称：'+list[0].pointName+'，分组ID：' + list[0].pointsGroupId;
        polygon.on('click', this.polygonClick);
        return polygon;
      },
      polygonClick(e) {
        infoWindow.setContent(e.target.content);
        infoWindow.open(map, new AMap.LngLat(e.lnglat.getLng(),e.lnglat.getLat()));
      },
      // 表单重置
      reset() {
        this.form = {
          district: null,
        };
        this.resetForm("form");
      },
      drawDistrict () {//根据选择的区域返回对应区域坐标
        var pointsVo = {};
        this.districtOptions.forEach(item => {
          if(item.adcode===this.form.district)
          {
            pointsVo.adcodeName = item.name;
            pointsVo.adcode = item.adcode;
          }
        });
        pointsVo.fenceName = queryFence.fenceName;
        districtCache(pointsVo).then(response =>{
          this.open = false;
          this.getCache();
        });
      },
      getFence(fenceId){

        if(fenceId){
          getFence(fenceId).then(response => {
            //this.queryParams.fenceId = response.data.fenceId;
            queryFence = response.data;
            //根据cityCode
            this.cityCode = queryFence.cityCode;
            city = queryFence.city;
            this.init();
            this.queryParams.fenceName = queryFence.fenceName;
            var data = {};
            data.adcode = queryFence.cityCode;
            data.adcodeName = city;
            data.fenceName = queryFence.fenceName;
            getDistrictByCity(data).then(response => {
              this.districtOptions = response.data;
            });
            this.getList();
          });
        }else {
          this.init();
          this.getList();
        }

      },
      /** 查询电子围栏列表 并将数据缓存到页面 */
      getList() {
        //this.loading = true;
        editPolygon = null;
        listFencePoints(this.queryParams).then(response => {
          //清空围栏坐标
          var allFence = response.data.all;
          var fence = response.data.fence;
          if(fence.length==0){
            this.msgInfo("未查询到指定的围栏数据")
          }
          for(var i=0;i<allFence.length;i++){
            var table = allFence[i];
            var list = table.drawPoints;
            this.initDraw(list,"#ff0816","#3c91ff");
          }
          for(var i=0;i<fence.length;i++){
            var table = fence[i];
            var list = table.drawPoints;
            var polygon = this.initDraw(list,"#33b7ff","#ff2233");
            //editPolygon.setExtData("id",table.pointsGruopId);
            if(fence.length==1||this.queryParams.pointsGroupId){
              editPolygon = polygon;
              /*queryFence = fence[i];
              this.queryParams.cityCode = queryFence.cityCode;*/
            }
            //this.queryParams.cityCode = queryFence.cityCode;
            this.modelType = "lookUp";
            this.buttonDisabled();
          }
        });
      },
      //获取缓存数据
      getCache(){
        listCache().then(response => {
          var list = response.data;
          for(var i=0;i<list.length;i++){
            var polygon = this.initDraw(list[i].drawPoints,"#13ffff","#ff0816");
            cachePolygon.push(polygon);
          }
        });
      },
      //将绘制的围栏保存到数据库中,页面缓存的表单数据和坐标数据传入后台保存
      handleSave(){
        if(this.modelType=="addDistrict"){
          this.$refs["form"].validate(valid => {
            if(valid) {
              addFencePoints().then(response => {
                this.handleDeleteCache();
                this.handleQuery();
                this.msgSuccess("保存成功")
              });
            }
          })
        }else if(this.modelType=="add"){
          addFencePoints().then(response => {
            this.handleDeleteCache();
            this.handleQuery();
            this.msgSuccess("保存成功")
          });
        }else if(this.modelType=="edit"){//保存修改数据
          polyEditor.close();
          this.handleQuery();
          this.msgSuccess("保存修改成功")
        }
      },
      /** 搜索按钮操作 */
      handleQuery() {
        map.clearMap();
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.resetForm("queryForm");
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.id)
        this.single = selection.length!==1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 关闭列表，打开地图界面*/
      handleAdd(type) {
        //不能做编辑和删除操作
        if(type=="draw"){
          this.draw("polygon");
          this.modelType = "add";
        }else{
          this.modelType = "addDistrict";
          this.reset();
          this.open = true;
          this.title = "选择区域";
        }
        this.buttonDisabled();
      },
      // 取消按钮,
      cancel() {
        this.open = false;
        this.reset();
      },
      /** 修改按钮操作 */
      handleUpdate() {
        if(editPolygon==null){
          this.msgInfo("请查询您具体需要修改的gid")
        }
        if(this.modelType == "edit"){
          this.msgInfo("当前已是编辑模式，请勿重复操作,如需退出可重新查询");
          return
        }
        this.modelType = "edit";
        this.buttonDisabled();
        var gid = this.queryParams.pointsGroupId;
        AMap.plugin(["AMap.PolyEditor"],function(){
          polyEditor = new AMap.PolyEditor(map,editPolygon);
          polyEditor.open();
          //polyEditor.setTarget(editPolygon);
          /*polygon.on('dblclick', () => {
            polyEditor.setTarget(polygon);
            polyEditor.open();
          })*/
          polyEditor.on('end', function(event) {
            var path = event.target.getPath();
            var drawPoints = []
            for(var i=0;i<path.length;i++){
              var fencePoints = {};
              fencePoints.pointX = path[i].getLng();
              fencePoints.pointY = path[i].getLat();
              drawPoints.push(fencePoints);
            }
            var pointVo = {};
            //pointVo.fenceName = queryFence.fenceName;
            pointVo.pointsGroupId = gid;
            /*pointVo.pointsName = queryFence.pointsName;*/
            pointVo.drawPoints = drawPoints;
            updateFencePoints(pointVo).then(response => {
              //this.msgSuccess("成功");
            });
          })
        });

      },
      /** 删除按钮操作 */
      handleDelete() {
        if(editPolygon == null){
          this.msgInfo("请查询您具体需要删除的gid")
          return;
        }
        var gid = this.queryParams.pointsGroupId
        this.$confirm('是否确认删除分组id为' + gid + '的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delFencePoints(gid);
        }).then(() => {
          this.handleQuery();
          this.msgSuccess("删除成功");
        })
      },
      /** 清空缓存操作 */
      handleDeleteCache () {
        this.modelType = "lookUp";
        this.buttonDisabled();
        deleteCache().then(response=>{
          this.msgSuccess(response.msg);
          if(cachePolygon.length>0){
            cachePolygon.forEach(item =>{
              map.remove(item);
            })
            cachePolygon = [];
          }
        });

      },

      buttonDisabled (){
        if(this.modelType == "lookUp"){
          this.openIsAdd = false;
          this.openIsSave = true;
          if(editPolygon==null){
            this.openIsEdit = true;
            this.openIsDetele = true;
          }else{
            this.openIsEdit = false;
            this.openIsDetele = false;
          }
        }else if(this.modelType == "add"||this.modelType=="addDistrict"){
          this.openIsDetele = true;
          this.openIsEdit = true;
          this.openIsSave = false;
        }else if(this.modelType == "edit"){
          this.openIsSave = false;
          this.openIsAdd = true;
          this.openIsDetele = true;
        }


      },

      /** 绘制围栏*/
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
              strokeWeight: 6,
              strokeOpacity: 0.4,
              fillOpacity: 0.4,
              fillColor:'#00b0ff',
              strokeColor:'#80d8ff',
              zIndex: 50,
              bubble: true,
              //同Polygon的Option设置
            });
            break;
          }
        }
      }
    }
  }
</script>
