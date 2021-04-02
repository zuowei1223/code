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
      <el-form-item prop="cityCode" label="服务城市">
        <el-select v-model="queryParams.cityCode" placeholder="请选择城市" >
          <el-option
            v-for="dict in cityOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          ></el-option>
        </el-select>
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
            :disabled = "true"
            v-hasPermi="['webservice:fence:add']"
          >新增</el-button>
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
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
    </div>
    <el-table v-loading="loading" :data="fenceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!--<el-table-column label="编号" align="center" prop="id" />-->
      <el-table-column label="围栏编码" align="center" prop="fenceCode" >
        <template slot-scope="scope">
          <router-link :to="'/fence/gaodeAz/' + scope.row.id" class="link-type">
            <span>{{ scope.row.fenceCode }}</span>
          </router-link>
        </template>
      </el-table-column>
      <el-table-column label="围栏名称" align="center" prop="fenceName" />
      <el-table-column label="服务城市" align="center" prop="cityCode"  :formatter="cityFormat" />
      <el-table-column label="所属区域" align="center" prop="adcodeName"  />
      <el-table-column label="描述信息" align="center" prop="fenceDesc" />
      <el-table-column label="创建人" align="center" prop="createor" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
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

          <el-form-item label="服务城市" prop="cityCode">
            <el-select v-model="form.cityCode" placeholder="请选择城市" :disabled="true">
              <el-option
                v-for="dict in cityOptions"
                :key="dict.dictValue"
                :label="dict.dictLabel"
                :value="dict.dictValue"
              ></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="所属区域" prop="adcodeName">
            <el-input placeholder="请输入内容" v-model="form.adcodeName" :disabled="true"></el-input>
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
        <div id= "edit" slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-dialog>
    </div>

  </div>
</template>
<style>
</style>
<script>
import { listFence, getFence, delFence, addFence, updateFence,fenceCache,getDistrictOpints } from "@/api/integration/fence";
export default {
  mounted: function () {
  },
  name: "FenceAz",
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
      valueArr: [],
      cityOptions:[],
      popOptions:[],
      serviceIdOptions: [],
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
        ],
        serviceId: [
          { required: true, message: "服务ID不能为空", trigger: "blur" }
        ],
        fencePop: [
          { required: true, message: "属性不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getService().then(response => {
      this.serviceIdOptions = response.data;
    });
    this.getDicts("city").then(response => {
      this.cityOptions = response.data;
    });
    this.getDicts("fencePop").then(response => {
      this.popOptions = response.data;
    });
  },
  methods: {
    cityFormat(row, column) {
      return this.selectDictLabel(this.cityOptions, row.cityCode);
    },
    popFormat(row, column) {
      var test = this.selectDictLabels(this.popOptions, row.fencePop,",")
      return test;
    },
    /** 查询电子围栏列表 并将数据缓存到页面 */
    getList() {
      this.loading = true;
      this.queryParams.fencePop = "1";
      listFence(this.queryParams).then(response => {
        this.fenceList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
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
    getCity(value){
      this.serviceIdOptions.forEach(item =>{
        if(item.serviceId == value){
          this.form.cityCode = item.serviceCity;
          this.cityOptions.forEach(city=>{
            if(item.serviceCity==city.dictValue){
              this.form.city = city.dictLabel;
            }
          })
        }
      })
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
      this.open = true;
      this.title = "添加围栏";
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
          console.log(this.form.fencePop);
          this.form.fencePop = this.form.fencePop.toString();
          /*if(this.valueArr!=null&&this.valueArr.length>0){
            this.form.fencePop = this.valueArr.toString();
          }*/
          if (this.form.id != null) {
            updateFence(this.form).then(response => {
              this.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
          } else {
            addFence(this.form).then(response => {
              this.msgSuccess("新增成功");
            this.open = false;
            this.getList();
          });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$confirm('是否确认删除围栏编号为"' + ids + '"的数据项?', "警告", {
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

  }
}
</script>
