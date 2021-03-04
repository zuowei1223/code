<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="编码" prop="localKey">
        <el-input
          v-model="queryParams.localKey"
          placeholder="编码"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="名称" prop="fenceName">
        <el-input
          v-model="queryParams.fenceName"
          placeholder="请输入名称"
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
    <div id="tableButton">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
        plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['webservice:fence:add']"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
        plain
            icon="el-icon-edit"
            size="mini"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['webservice:fence:edit']"
          >编辑模式</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
        plain
            icon="el-icon-delete"
            size="mini"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['webservice:fence:remove']"
          >删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-save"
            size="mini"
            @click="handleSave(1)"
            v-hasPermi="['webservice:fence:add']"
          >保存</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-save"
            size="mini"
            @click="handleSave(2)"
            v-hasPermi="['webservice:fence:add']"
          >保存修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
        plain
            icon="el-icon-download"
            size="mini"
            @click="changeView"
            v-hasPermi="['webservice:fence:list']"
          >切换视图</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
    </div>
    <div id="fenceButton" style="display: none">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-save"
            size="mini"
            @click="handleSave(1)"
            v-hasPermi="['webservice:fence:add']"
          >保存</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="success"
            plain
            icon="el-icon-close"
            size="mini"
            @click="handleClose"
            v-hasPermi="['webservice:fence:edit']"
          >关闭</el-button>
        </el-col>
      </el-row>
    </div>
    <div id="table">
      <el-table v-loading="loading" :data="fenceList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <!--<el-table-column label="编号" align="center" prop="id" />-->
        <el-table-column label="围栏编码" align="center" prop="localKey" />
        <el-table-column label="围栏名称" align="center" prop="fenceName" />
        <el-table-column label="平台编号" align="center" prop="fenceGid" />
        <el-table-column label="轨迹服务ID" align="center" prop="serviceId" />
        <el-table-column label="行政区划" align="center" prop="adcodeName" />
        <el-table-column label="描述信息" align="center" prop="fenceDesc" />
        <el-table-column label="创建人" align="center" prop="createor" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['webservice:fence:edit']"
            >修改</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDelete(scope.row)"
              v-hasPermi="['webservice:fence:remove']"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <div id="container" style="display: none;width:100%; height:900px" >

    </div>

    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    <div id = "dialog">
      <!-- 添加或修改电子围栏对话框 -->
      <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
        <el-form ref="form" :model="form" :rules="rules" label-width="80px">
          <el-form-item label="围栏名称" prop="fenceName">
            <el-input v-model="form.fenceName" placeholder="请输入围栏名称" />
          </el-form-item>
          <el-form-item label="轨迹服务" prop="serviceId">
            <el-select v-model="form.serviceId" placeholder="请选择数据级别">
              <el-option
                v-for="service in serviceIdOptions"
                :key="service.serviceId"
                :label="service.fwName"
                :value="parseInt(service.serviceId)"
              ></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="围栏属性" prop="fencePop">
            <el-input v-model="form.fencePop" placeholder="请选择围栏属性" />
          </el-form-item>
          <el-form-item label="省份" prop="province">
            <el-select v-model="form.province" placeholder="请选择省份">
              <el-option
                v-for="dict in provinceOptions"
                :key="dict.dictValue"
                :label="dict.dictLabel"
                :value="parseInt(dict.dictValue)"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="市" prop="city">
            <el-select v-model="form.city" placeholder="请选择市">
              <el-option
                v-for="dict in cityOptions"
                :key="dict.dictValue"
                :label="dict.dictLabel"
                :value="parseInt(dict.dictValue)"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item  label="行政区" prop="district" >
            <el-select v-model="form.district" placeholder="请选择行政区" @change = "getDistrict">
              <el-option
                v-for="dict in districtOptions"
                :key="dict.dictValue"
                :label="dict.dictLabel"
                :value="dict.dictValue"
              ></el-option>
            </el-select>
          </el-form-item>

          <el-input
            type="hidden"
            id="adcodeId"
            v-model="form.adcode">
          </el-input>

          <el-form-item label="描述信息" prop="fenceDesc">
            <el-input
              type="textarea"
              :rows="2"
              placeholder="请输入内容"
              v-model="form.fenceDesc">
            </el-input>
          </el-form-item>

        </el-form>
        <div id= "new" slot="footer" class="dialog-footer">
          <el-button type="primary" @click="addDraw">创建围栏</el-button>
          <!--<el-button type="primary" @click="addDistrictDraw">创建行政区划围栏</el-button>-->
          <el-button @click="cancel">取 消</el-button>
        </div>
        <div id= "edit" slot="footer" class="dialog-footer">
          <el-button type="primary" @click="handleSave(2)">保存</el-button>
          <el-button type="primary" @click="editDraw">修改围栏</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-dialog>
    </div>

  </div>
</template>
<style>
  html,
  body,
  #container {
    width: 100%;
    height: 100%;
  }
</style>
<script type="text/javascript" src="https://webapi.amap.com/maps?v=1.4.15&key=3fa060bd1711d61ee47bb8983d7b1101&plugin=AMap.MouseTool,AMap.Autocomplete,AMap.PlaceSearch"></script>
<script type="text/javascript" src="https://cache.amap.com/lbs/static/addToolbar.js"></script>
<script>
import { listFence, getFence, delFence, addFence, updateFence,fenceCache,getDistrictOpints } from "@/api/integration/fence";
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
//绘制或修改后获取的围栏坐标
var drawPoints = [] ;

var districtPoints = [];
//临时存放查询出来的围栏对象
var tempPolygon = [];
//修改的围栏对象
var editFence ;
//缓存KEY
var cacheMap = {} ;
var view = "table";
/*var editKey ;*/

export default {
  mounted: function () {
    this.init();
    //监听围栏绘制事件
    mouseTool.on('draw',function(e){
      if(e.obj.CLASS_NAME == 'AMap.Marker'){

      }else{
        console.log(e.obj.getPath());//获取路径/范围
        var path = e.obj.getPath();
        var polygon = new AMap.Polygon({
          path: path,
          fillColor: '#2482ff', // 多边形填充颜色
          borderWeight: 4, // 线条宽度，默认为 1
          strokeColor: '#13ffff', // 线条颜色
          fillOpacity: 0.35,//填充透明度
          strokeOpacity: 0.3 //线透明度
        });
        map.add(polygon);
        drawPoints = [];
        for(var i=0;i<path.length;i++){
          var fencePoints = {};
          fencePoints.pointName = "坐标"+i;
          fencePoints.pointX = path[i].getLng();
          fencePoints.pointY = path[i].getLat();
          fencePoints.fenceName = cacheMap.fenceName;
          fencePoints.fenceId = cacheMap.localKey;
          drawPoints.push(fencePoints);
        }
        //关闭绘制工具
        mouseTool.close(true);

      }
    })
  },
  name: "Fence",
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
      // 电子围栏表格数据
      fenceList: [],
      serviceIdOptions: [],
      provinceOptions: [],
      cityOptions: [],
      districtOptions: [],
      /*streetOptions: [],*/
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        fenceName: null,
        createorId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        fenceName: [
          { required: true, message: "名称不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getService().then(response => {
      this.serviceIdOptions = response.data;
    });
    this.getDicts("province").then(response => {
      this.provinceOptions = response.data;
    });
    this.getDicts("city").then(response => {
      this.cityOptions = response.data;
    });
    this.getDicts("district").then(response => {
      this.districtOptions = response.data;
    });
    /*this.getDicts("street").then(response => {
      this.streetOptions = response.data;
    });*/

    //获取行政区划列表，默认为江西省
    /*var queryData = {};
    queryData.keywords = ""
    this.getDistrict().then(response => {
      this.serviceIdOptions = response.data;
  });*/
  },
  methods: {
    /*keyFormat(row, column) {
      return this.selectDictLabel(this.provinceOptions, row.gaodeKey);
    },
    keyFormat(row, column) {
      return this.selectDictLabel(this.cityOptions, row.gaodeKey);
    },*/
    /** 初始化地图插件*/
    init: function () {
      map = new AMap.Map('container', {
        resizeEnable: true,
        zoom: 14
      })
      // 初始化地图插件
      AMap.plugin(['AMap.ToolBar', 'AMap.Scale','AMap.MouseTool','AMap.Autocomplete',
        'AMap.PlaceSearch' ,'AMap.PolyEditor','AMap.Geocoder' ], function(){
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
    //初始化图层,
    initDraw: function(list){
      var path = [];
      for(var i=0;i<list.length;i+=1){
        path.push(new AMap.LngLat(list[i].pointX,list[i].pointY));
      }
      var polygon = new AMap.Polygon({
        path: path,
        fillColor: '#ff0816', // 多边形填充颜色
        borderWeight: 4, // 线条宽度，默认为 1
        strokeColor: '#3c91ff', // 线条颜色
        fillOpacity: 0.35,//填充透明度
        strokeOpacity: 0.3 //线透明度
      });
      map.add(polygon);
      return polygon;
    },
    addDistrictDraw() {

    },
    getDistrict(value) {//根据选择的区域返回对应区域坐标
      var data = {};
      this.districtOptions.forEach(item => {
        if(item.dictValue===value)
        {
          data.dictLabel = item.dictLabel;
          data.dictValue = item.dictValue;
          data.serviceId = this.form.serviceId;

        }
      });
      getDistrictOpints(data).then(response =>{

        districtPoints = response.data;

      });
    },
    /** 查询电子围栏列表 并将数据缓存到页面 */
    getList() {
      this.loading = true;
      if(tempPolygon !=null ){
        for(var i=0;i<tempPolygon.length;i++){
          map.remove(tempPolygon[i].initFence);
        }
        tempPolygon = [];
      }
      listFence(this.queryParams).then(response => {
        //清空图层
        this.fenceList = response.rows;
        for(var i=0;i<response.rows.length;i++){
          var table = response.rows[i];
          var list = table.points;
          console.log(list);
          var polygonMap = {};
          console.log(table.localKey);
          polygonMap.localKey = table.localKey;
          polygonMap.initFence = this.initDraw(list);
          tempPolygon.push(polygonMap);
        }
        this.total = response.total;
        this.loading = false;
      });
    },

    /** 开始绘制围栏,需将弹框编辑的保存到缓存对象中*/
    addDraw(){
      this.$refs["form"].validate(valid => {
        if(valid) {
          fenceCache(this.form).then(response => {
            //this.msgSuccess("缓存成功");
            cacheMap =  response.data;
            console.log(cacheMap);
            if(districtPoints){//存在行政区划的可按行政区划创建围栏
              this.initDraw(districtPoints);
              drawPoints = districtPoints;
            }else{
              this.draw("polygon");
            }
            this.open = false;
            view = "container";
            $("#table").hide();
            $("#container").show();
          });
        }
      });
    },
    //将绘制的围栏保存到数据库中,页面缓存的表单数据和坐标数据传入后台保存
    handleSave(type){
      if(type==1){
        var data = {"cacheKey":cacheMap.cacheKey,"drawPoints":drawPoints};
        addFence(data).then(response => {
          //关闭鼠标绘制工具
          mouseTool.close(true);
          //清空页面缓存数据
          cacheMap = {};
          this.getList();
        });
      }else if(type==2){//保存修改数据
        polyEditor.close();
        if (this.form.id != null) {
          var data = {"cacheKey":this.form.localKey,"drawPoints":drawPoints};
          updateFence(data).then(response => {
            this.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
        }
      }else if(type==3){

      }

    },
    clickOn: function(){
      for(var i=0;i<tempPolygon.length;i++){
        tempPolygon[i].initFence.on('dblclick', function (e) {
          console.log(polyEditor);
          AMap.plugin(["AMap.PolyEditor"],function(){
            polyEditor = new AMap.PolyEditor(map,e.target);
            polyEditor.open();
            polyEditor.on('end', function(event) {
              //log.info('触发事件： end')
              // event.target 即为编辑后的折线对象
              console.log(event.target);
              //将对象的path保存到数据库

            })
          });
        });
      }
    },
    //关闭地图，打开列表
    handleClose(){
      this.$confirm('请确定是否退出地理围栏的编辑?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        $("#table").show();
        $("#container").hide();
      })

    },
    //开启编辑模式,双击围栏选中
    beginEdit(){
      this.clickOn();
    },
    editDraw(){
      if(view=="table"){
        view = "container";
        $("#table").hide();
        $("#container").show();
      }
      this.open = false;
      //当前围栏颜色加深并视图定位
      console.log(tempPolygon);
      for(var i=0;i<tempPolygon.length;i++){
        var key = tempPolygon[i].localKey;
        if(key == this.form.localKey){
          var editPolygon = tempPolygon[i].initFence;
          map.setFitView(editPolygon);
          AMap.plugin(["AMap.PolyEditor"],function(){
            polyEditor = new AMap.PolyEditor(map,editPolygon);
            polyEditor.open();
            polyEditor.on('end', function(event) {
              //log.info('触发事件： end')
              // event.target 即为编辑后的折线对象
              var path = event.target.getPath();
              console.log(path);
              drawPoints = [];
              for(var i=0;i<path.length;i++){
                var fencePoints = {};
                fencePoints.pointName = "坐标"+i;
                fencePoints.pointX = path[i].getLng();
                fencePoints.pointY = path[i].getLat();
                //console.log(this.form);
                /*fencePoints.fenceName = editFence.fenceName;
                fencePoints.fenceId = editFence.localKey;*/
                drawPoints.push(fencePoints);
              }
            })
          });
        }
      }

    },

    // 取消按钮,
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        localKey: null,
        fenceName: null,
        serviceId: null,
        fencePop: null,
        province: null,
        city: null,
        district: null,
        fenceDesc:null


      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
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
    handleAdd() {
      this.reset();
      $("#new").show();
      $("#edit").hide();
      this.open = true;
      this.title = "添加电子围栏";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      $("#new").hide();
      $("#edit").show();
      this.reset();
      const id = row.id || this.ids
      getFence(id).then(response => {
        this.form = response.data;
        //editFence = response.data;
        /*editKey = response.data.localKey;*/
        this.open = true;
        this.title = "修改电子围栏";
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$confirm('是否确认删除电子围栏编号为"' + ids + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delFence(ids);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        })
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
            fillColor:'#00b0ff',
            strokeColor:'#80d8ff'
            //同Polygon的Option设置
          });
          break;
        }
      }
    },
    /** 切换视图操作 */
    changeView() {
      if(view=="table"){
        view = "container";
        $("#table").hide();
        $("#container").show();
      }else{
        /*if(editFence){
          this.msgSuccess("围栏坐标编辑状态下无法切换视图");
          return;
        }*/
        if($.isEmptyObject(cacheMap) && !polyEditor){
          view ="table";
          $("#table").show();
          $("#container").hide();
        }else{
          this.$confirm('存在未保存的围栏，确定是否退出地理围栏的编辑?', "警告", {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"
          }).then(function() {
            view = "table";
            $("#table").show();
            $("#container").hide();
            cacheMap = {};
            //关闭绘制工具
            if(polyEditor){
              polyEditor.close();
              polyEditor = null;
            }
            mouseTool.close(true);
          })
        }



      }
    }
  }
}
</script>
