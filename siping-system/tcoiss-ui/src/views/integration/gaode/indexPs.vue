<template>
  <div class="app-container" id="container"  >
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="70px" class = "top-input">
      <el-form-item label="围栏名称" prop="fenceName" >
        <el-input
          v-model="queryParams.fenceName"
          placeholder="请输围栏名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item prop="cityCode" label="服务城市">
        <el-select v-model="queryParams.cityCode" placeholder="请选择城市" @change = "getDistrict" >
          <el-option
            v-for="dict in cityOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item  label="行政区" prop="adcode" >
        <el-select v-model="queryParams.adcode" placeholder="请选择行政区">
          <el-option
            v-for="district in districtOptions"
            :key="district.adcode"
            :label="district.name"
            :value="district.adcode"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8 top-btn" >
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleOpenAdd"
          :disabled = "openIsAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-i  con-save"
          size="mini"
          @click="handleSave"
          :disabled = "openIsSave"
        >保存</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          icon="el-icon-save"
          size="mini"
          @click="handleClose"
          :disabled = "openIsClose"
        >退出</el-button>
      </el-col>
    </el-row>
    <div id = "dialog">
      <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
        <el-form ref="form" :model="form" :rules="rules" label-width="80px">

          <el-form-item  label="围栏名称" prop="fenceName" >
            <el-input v-model="form.fenceName" placeholder="请输入围栏名称" />
          </el-form-item>
          <div id = "other">
            <el-form-item label="服务城市" prop="cityCode">
              <el-select v-model="form.cityCode" placeholder="请选择城市" @change="getDistrict">
                <el-option
                  v-for="dict in cityOptions"
                  :key="dict.dictValue"
                  :label="dict.dictLabel"
                  :value="dict.dictValue"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item  label="围栏类型" prop="fenceType" >
              <el-select v-model="form.fenceType" placeholder="请选择围栏类型"  >
                <el-option
                  v-for="dict in fenceTypeOptions"
                  :key="dict.dictValue"
                  :label="dict.dictLabel"
                  :value="parseInt(dict.dictValue)"
                ></el-option>
              </el-select>
            </el-form-item>
            <el-form-item  label="行政区" prop="adcode" >
              <el-select v-model="form.adcode" placeholder="请选择行政区" >
                <el-option
                  v-for="district in districtOptions"
                  :key="district.adcode"
                  :label="district.name"
                  :value="district.adcode"
                ></el-option>
              </el-select>
            </el-form-item>
          </div>


          <el-form-item label="描述信息" prop="fenceDesc">
            <el-input
              type="textarea"
              :rows="2"
              placeholder="请输入内容"
              v-model="form.fenceDesc">
            </el-input>
          </el-form-item>
        </el-form>
        <div id= "add" slot="footer" class="dialog-footer">
          <el-button type="primary" @click="handleAdd">确定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
        <div id= "edit" slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitEdit">保存</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-dialog>
    </div>

  </div>
</template>
<style>
  html, body, #container {
    height: 600px;
    width: 100%;
    margin: 0;
  }
  .top-btn {
    position: fixed;
    margin-bottom: 7px;
    z-index: 999;
    top: 150px;

  }

  .top-input {
    position: fixed;
    margin-bottom: 7px;
    z-index: 999;
  }

</style>
<script>
  import AMapLoader from '@amap/amap-jsapi-loader';
  import $ from 'jquery';
  import { listFence,getFence,saveCache,delFence,checkFence,getDistrictByCity,updateFence } from "@/api/integration/gaode";

  var map;
  var polyEditor;
  var rightClickPolygon = null;
  var infoWindow;
  var contextMenu;
  var mouseTool;
  var opType;
  var tempFence = {};
export default {
  name: "GaodePs",
  components: {
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 显示搜索条件
      showSearch: true,
      // 弹出层标题
      title: "",
      fenceList:[],
      cityOptions: [],
      districtOptions: [],
      fenceTypeOptions:[],
      // 是否显示弹出层
      open: false,
      openIsAdd: false,
      openIsSave:true,
      openIsClose:true,
      // 查询参数
      queryParams: {
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        fenceName: [
          { required: true, message: "名称不能为空", trigger: "blur" }
        ],
        cityCode: [
          { required: true, message: "请选择服务城市", trigger: "blur" }
        ],
        fenceType: [
          { required: true, message: "请选择围栏类型", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    //this.getList();
    this.init();
    this.getDicts("city").then(response => {
      this.cityOptions = response.data;
    });
    this.getDicts("fenceType").then(response =>{
      this.fenceTypeOptions = response.data;
    });
  },

  methods: {
    init(){
      AMapLoader.load({
        "key": "3fa060bd1711d61ee47bb8983d7b1101",              // 申请好的Web端开发者Key，首次调用 load 时必填
        "version": "2.0",   // 指定要加载的 JSAPI 的版本，缺省时默认为 1.4.15
        "plugins": ['AMap.Scale','AMap.ToolBar','AMap.PolygonEditor','AMap.MouseTool'],           // 需要使用的的插件列表，如比例尺'AMap.Scale'等
      }).then((AMap)=>{
        map = new AMap.Map('container', {
          resizeEnable: true,
          /*center: [116.471354, 39.994257],*/
          zoom: 16.8,
          showIndoorMap: false
        });
        infoWindow = new AMap.InfoWindow({offset: new AMap.Pixel(0, -30)});
        contextMenu = new AMap.ContextMenu();
        contextMenu.addItem("修改围栏信息", this.handleOpenEdit, 0);
        contextMenu.addItem("修改围栏地图", this.handleUpdate, 1);
        contextMenu.addItem("删除", this.handleDelete, 2);
        map.addControl(new AMap.Scale())
        map.addControl(new AMap.ToolBar())
        mouseTool = new AMap.MouseTool(map);
        //map.setCity("宜春");
        const fenceId = this.$route.params && this.$route.params.id;
        this.getFence(fenceId);
        polyEditor = new AMap.PolygonEditor(map);
        polyEditor.on('add', this.addCache);
        polyEditor.on('end', this.updateCache)
      }).catch(e => {
          console.log(e);
      })
    },
    updateCache(data){
      var polygon = data.target;
      console.log(polygon);
      if(opType == "saveCache"){//将地图页面上的围栏保存到数据库中
        if(polygon==null){
          this.msgError("无数据可以保存");
          return;
        }
        var path = polygon.getPath();
        var points = [];
        for(var i=0;i<path.length;i++){
          var point = {};
          point.pointX = path[i].getLng();
          point.pointY = path[i].getLat();
          points.push(point);
        }
        var fence = {};
        fence = polygon.getExtData().fence;
        if(fence.fenceType=="0"&&points.length>100){
          this.msgError("保存失败，当前编辑的围栏：【"+fence.name+"】顶点数超过了100个");
          this.handleQuery();
          return;
        }else{
          fence.points = points
          saveCache(fence).then(response => {
            this.handleQuery();
            this.msgSuccess("保存成功");
          });
        }
      }

    },
    addCache(data){//将围栏信息缓存到地图页面中
        var polygon = data.target;
        var path = polygon.getPath();
        polyEditor.addAdsorbPolygons(polygon);
        var points = [];
        for(var i=0;i<path.length;i++){
          var point = {};
          point.pointX = path[i].getLng();
          point.pointY = path[i].getLat();
          points.push(point);
        }

        tempFence.points = points;
        var extData = {fence:tempFence};
        polygon.setExtData(extData);
        map.setFitView(
          polygon,  // 覆盖物数组
          false,  // 动画过渡到制定位置
          [60, 60, 60, 60],  // 周围边距，上、下、左、右
          16,  // 最大 zoom 级别
        );
    },
    initPolygon(fence,fillColor,strokeColor){
      var path = [];
      var list = fence.points;
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
          fence: fence
        }
      });
      map.add(polygon);
      polygon.content = '围栏名称：'+fence.fenceName;
      polygon.on('click', this.polygonClick);
      polygon.on('rightclick', function (e) {
        if(!polyEditor.getTarget()){
          rightClickPolygon = e.target;
          contextMenu.open(map, e.lnglat);
        }

      });
      return polygon;
    },

    polygonClick(e) {
      infoWindow.setContent(e.target.content);
      infoWindow.open(map, e.lnglat);
      map.setFitView(
        e.target,  // 覆盖物数组
        false,  // 动画过渡到制定位置
        [60, 60, 60, 60],  // 周围边距，上、下、左、右
        16,  // 最大 zoom 级别
      );
    },

    getFence(fenceId){
      if(fenceId){
        getFence(fenceId).then(response => {
          //this.queryParams.fenceId = response.data.fenceId;
          var queryFence = response.data;
          console.log(queryFence);
          this.queryParams.fenceName = queryFence.fenceName;
          this.getList();
        });
      }else {
        this.getList();
      }

    },
    getList(){
      var queryPolygons = [];
      this.queryParams.fencePop = "0";
      listFence(this.queryParams).then(response => {
        this.fenceList = response.data;
        for(var i=0;i<this.fenceList.length;i++){
          var table = this.fenceList[i];
          if(table.type=="queryFence"){
            var queryPolygon = this.initPolygon(table,"#3c91ff","#ff0816");
            queryPolygons.push(queryPolygon);
          }else{
            this.initPolygon(table,"#ff0816","#3c91ff");
          }
        }
        if(queryPolygons.length == 0 ){
          map.setCity(this.queryParams.cityCode);
          //this.msgInfo("未查询到指定的围栏数据")
          return;
        }
        polyEditor.addAdsorbPolygons(queryPolygons);
        map.setFitView(
          queryPolygons,  // 覆盖物数组
          false,  // 动画过渡到制定位置
          [60, 60, 60, 60],  // 周围边距，上、下、左、右
          16,  // 最大 zoom 级别
        );

      });
    },



    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.reset();
      opType = "query";
      map.clearMap();
      this.getList();
    },

    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleClose(){
      if(polyEditor.getTarget()){
        var polygon = polyEditor.getTarget();
        map.setFitView(
          polygon,  // 覆盖物数组
          false,  // 动画过渡到制定位置
          [60, 60, 60, 60],  // 周围边距，上、下、左、右
          16,  // 最大 zoom 级别
        );
        var fence = polygon.getExtData().fence;
        this.$confirm('围栏：【' + fence.fenceName + '】未保存，确定要放弃编辑?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(() =>{
          this.openIsClose = true;
          this.openIsAdd = false;
          this.openIsSave = true;
          opType = "";
          polyEditor.setTarget();
          polyEditor.close();//关闭编辑*/
          if(!fence.fenceCode){
            map.remove(polygon);
          }
          $(".top-input").show();

        }).catch(() => {
          console.log("cancel");
        });

      }else{
        this.openIsClose = true;
        this.openIsAdd = false;
        this.openIsSave = true;
        opType = "";
        polyEditor.close();//关闭编辑*/
        $(".top-input").show();
      }
    },
    handleOpenAdd(){
      this.reset();
      this.open = true;
      $("#other").show();
      $("#add").show();
      $("#edit").hide();
      this.title = "新增围栏";
    },
    handleOpenEdit(){
      this.reset();
      contextMenu.close();
      $("#other").hide();
      $("#add").hide();
      $("#edit").show();
      this.form = rightClickPolygon.getExtData().fence;
      console.log(this.form);
      this.open = true;
      this.title = "编辑围栏名称";
    },
    submitEdit(){
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateFence(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.handleQuery();
            });
          }
        }
      });
    },

    /** 新增按钮操作 */
    handleAdd() {//校验表单数据
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.districtOptions.forEach(item =>{
            if(this.form.adcode == item.adcode){
              this.form.adcodeName = item.name;
            }
          })
          this.form.fencePop = "0";
          checkFence(this.form).then(response=>{
            if(response.code == 200){
              this.open = false;
              this.openIsAdd = true;
              this.openIsClose = false;
              this.openIsSave = false;
              $(".top-input").hide();
              tempFence = this.form
              tempFence.fencePop = "0";//配送
              map.setZoom(16,false);
              //map.setCity(this.form.cityCode);
              if(this.form.fenceType == "1"){
                var fence = response.data;
                var polygon = this.initPolygon(fence,"#3c91ff","#ff0816");
                var extData = {fence: fence };
                polygon.setExtData(extData);
                opType = ""
                polyEditor.setTarget(polygon)
              }
              polyEditor.open();//开启编辑器

            }else{
              this.msgSuccess(response.msg)
            }

          })
        }
      });

    },
    /** 修改按钮操作，进入编辑状态 */
    handleUpdate() {
      contextMenu.close();
      opType = "addCache";
      this.openIsSave = false;
      this.openIsAdd = true;
      this.openIsClose = false;
      $(".top-input").hide();
      polyEditor.setTarget(rightClickPolygon);
      polyEditor.open();
    },
    handleSave(){
      this.openIsAdd = false;
      opType = "saveCache";
      polyEditor.setTarget();
    },
    /** 删除按钮操作 */
    handleDelete() {
      contextMenu.close();
      var fence = rightClickPolygon.getExtData().fence;
      var data = {'name': fence.fenceName,'code': fence.fenceCode};
      this.$confirm('是否确认删除名称为' + fence.fenceName + '的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        return delFence(data);
      }).then(() => {
        this.handleQuery();
        this.msgSuccess("删除成功");
      })
    },
    getDistrict(value){
      var data ={};
      data.cityCode = value;
      console.log(value);
      this.cityOptions.forEach(item =>{
        if(value==item.dictValue){
          data.city = item.dictLabel;
          this.form.city = item.dictLabel;
        }
      })
      getDistrictByCity(data).then(response=>{
        this.districtOptions = response.data;
      })
    },

    /** 清空缓存 */
    /*handleDeleteCache() {
      if(cachePolygon!=null){
        var fenceName = cachePolygon.getExtData().name;
        var fenceCode = cachePolygon.getExtData().id;
        var data = {'name': fenceName,'code': fenceCode};
        deleteCache(data).then(response => {
          this.msgSuccess("清空成功");
          map.remove(cachePolygon);
          cachePolygon = null;
        });
      }else{
        this.msgSuccess("未指定清除的数据")
      }

    }*/

  }
};
</script>
