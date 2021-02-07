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
        >修改</el-button>
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
          type="warning"
		  plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['webservice:fence:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    <div id="table">
      <el-table v-loading="loading" :data="fenceList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="编号" align="center" prop="id" />
        <el-table-column label="围栏本地编码" align="center" prop="localKey" />
        <el-table-column label="平台编号" align="center" prop="fenceGid" />
        <el-table-column label="名称" align="center" prop="fenceName" />
        <el-table-column label="描述信息" align="center" prop="fenceDesc" />
        <el-table-column label="创建人ID" align="center" prop="createorId" />
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
    <div id="container" style="display: none;width:100%; height:900px" ></div>

    
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
          <el-form-item label="编码" prop="localKey">
            <el-input v-model="form.localKey" placeholder="请输入围栏本地编码" />
          </el-form-item>
          <el-form-item label="名称" prop="fenceName">
            <el-input v-model="form.fenceName" placeholder="请输入名称" />
          </el-form-item>
          <el-form-item  label="坐标集" prop="fencePoints" >
            <el-input id = "points" v-model="form.fencePoints" placeholder="坐标集" disabled />
          </el-form-item>
          <el-form-item label="描述信息" prop="fenceDesc">
            <el-input
              type="textarea"
              :rows="2"
              placeholder="请输入内容"
              v-model="form.fenceDesc">
            </el-input>
          </el-form-item>

        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="beginDraw">绘制围栏</el-button>
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
import { listFence, getFence, delFence, addFence, updateFence,fenceCache } from "@/api/integration/fence";
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

var fromData ;
export default {
  mounted: function () {
    this.init();
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
        map.setFitView(tempPolygon);//视口自适应
        //将围栏对象坐标缓存
        //fromData.fencePoints = e.obj.getPath();
        $("#points").val(e.obj.getPath());
        console.log(this.form);//获取路径/范围
        fenceCache(this.form).then(response => {
          this.msgSuccess("暂存成功");
          //关闭鼠标绘制工具
          mouseTool.close(true);
          this.getList();
        });
        console.log(e.obj.getPath());//获取路径/范围
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
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        localKey: null,
        fenceName: null,
        createorId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        localKey: [
          { required: true, message: "编码不能为空", trigger: "blur" }
        ],
        fenceName: [
          { required: true, message: "名称不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
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
    /** 查询电子围栏列表 */
    getList() {
      this.loading = true;
      listFence(this.queryParams).then(response => {
        this.fenceList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 开始绘制围栏,需将弹框编辑的保存到页面对象中*/
    beginDraw(){
      this.$refs["form"].validate(valid => {
        if(valid) {
          this.draw("polygon");
          this.open = false;
        }
      });

    },
    // 取消按钮
    cancel() {
      $("#table").show();
      $("#container").hide();
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        localKey: null,
        fenceGid: null,
        fenceName: null,
        fencePoints: null,
        fenceEnable: null,
        validTime: null,
        fenceRepeat: null,
        fenceTime: null,
        fenceDesc: null,
        alertCondition: null,
        createTime: null,
        createorId: null
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
      $("#table").hide();
      $("#container").show();
      this.reset();
      this.open = true;
      this.title = "添加电子围栏";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getFence(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改电子围栏";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            fromData = this.form;
            /*updateFence(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });*/
          } else {
            fromData = this.form;
          }
        }
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
    /** 导出按钮操作 */
    handleExport() {
      this.download('webservice/fence/export', {
        ...this.queryParams
      }, `webservice_fence.xlsx`)
    }
  }
};
</script>
