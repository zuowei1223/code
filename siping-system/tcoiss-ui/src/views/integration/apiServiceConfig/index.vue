<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="70px">
      <el-form-item label="API名称" prop="apiName">
        <el-input
          v-model="queryParams.apiName"
          placeholder="请输入API名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="请求方式" prop="requestType">
        <el-select v-model="queryParams.requestType" placeholder="请选择请求方式" clearable size="small">
          <el-option
            v-for="dict in requestTypeOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <!--<el-form-item label="业务对象" prop="apiObj">
        <el-input
          v-model="queryParams.apiObj"
          placeholder="请输入业务对象"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>-->


      <el-form-item label="所属应用" prop="appName">
        <el-select v-model="queryParams.appName" placeholder="请选择所属应用" clearable size="small">
          <el-option
            v-for="dict in appTypeOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="数据级别" prop="dataLevel">
        <el-select v-model="queryParams.dataLevel" placeholder="请选择数据级别" clearable size="small">
          <el-option
            v-for="dict in dataLevelOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
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
          v-hasPermi="['integration:apiServiceConfig:add']"
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
          v-hasPermi="['integration:apiServiceConfig:edit']"
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
          v-hasPermi="['integration:apiServiceConfig:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['integration:apiServiceConfig:export']"
        >导出</el-button>
      </el-col>

      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-search"
          size="mini"
          @click="handleApiTest"
          v-hasPermi="['integration:apiServiceConfig:test']"
        >测试</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="apiServiceConfigList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="API名称" align="center" prop="apiName" />
      <el-table-column label="API编码" align="center" prop="apiCode" />
      <!--<el-table-column label="业务对象" align="center" prop="requestType" :formatter="requestTypeFormat" />-->
      <el-table-column label="请求方式" align="center" prop="requestType" :formatter="requestTypeFormat" />
      <el-table-column label="所属应用" align="center" prop="appName" />
      <el-table-column label="内容格式" align="center" prop="dataType" />
      <el-table-column label="数据级别" align="center" prop="dataLevel" :formatter="dataLevelFormat" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-search"
            @click="handleApiTest(scope.row)"
            v-hasPermi="['integration:apiServiceConfig:test']"
          >测试</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['integration:apiServiceConfig:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['integration:apiServiceConfig:remove']"
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

    <!-- 添加或修改API服务配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="50%" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="API名称" prop="apiName">
          <el-input v-model="form.apiName" placeholder="请输入API名称" />
        </el-form-item>
        <el-form-item label="API编码" prop="apiCode">
          <el-input v-model="form.apiCode" placeholder="请输入API编码" />
        </el-form-item>
        <el-form-item label="请求方式" prop="requestType">
          <el-select v-model="form.requestType" placeholder="请选择请求方式">
            <el-option
              v-for="dict in requestTypeOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            ></el-option>
          </el-select>
        </el-form-item>
        <!--<el-form-item label="业务对象" prop="apiObj">
          <el-select v-model="form.apiObj" placeholder="请选择数据级别">
            <el-option
              v-for="obj in apiObjOptions"
              :key="obj.busTableName"
              :label="obj.busTableComment"
              :value="obj.busTableName"
            ></el-option>
          </el-select>
        </el-form-item>-->
        <el-form-item label="所属应用" prop="appName">
          <el-select v-model="form.appName" placeholder="请选择所属应用" clearable size="small">
            <el-option
              v-for="dict in appTypeOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="API地址" prop="apiUrl">
          <el-input v-model="form.apiUrl" placeholder="请输入API地址" />
        </el-form-item>
        <el-form-item label="内容格式" prop="dataType">
          <el-input v-model="form.dataType" placeholder="请输入内容格式" />
        </el-form-item>
        <el-form-item label="参数模板" prop="paramTemplate">
          <el-input v-model="form.paramTemplate" type="textarea" rows="5" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="数据级别" prop="dataLevel">
          <el-select v-model="form.dataLevel" placeholder="请选择数据级别">
            <el-option
              v-for="dict in dataLevelOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="parseInt(dict.dictValue)"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listApiServiceConfig, getApiServiceConfig, delApiServiceConfig, addApiServiceConfig, updateApiServiceConfig,testApiServiceConfig } from "@/api/integration/apiServiceConfig";

export default {
  name: "ApiServiceConfig",
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
      // API服务配置表格数据
      apiServiceConfigList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 请求方式字典
      requestTypeOptions: [],
      // 数据级别字典
      dataLevelOptions: [],
      // 业务对象列表
      apiObjOptions: [],

      appTypeOptions: [],

      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        apiName: null,
        requestType: null,
        appName: null,
        createName: null,
        dataLevel: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        requestType: [
          { required: true, message: "请求方式不能为空", trigger: "change" }
        ],
        apiUrl: [
          { required: true, message: "API地址不能为空", trigger: "blur" }
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getDicts("request_type").then(response => {
      this.requestTypeOptions = response.data;
    });
    this.getDicts("data_level").then(response => {
      this.dataLevelOptions = response.data;
    });
    this.getDicts("app_type").then(response => {
      this.appTypeOptions = response.data;
    });
    var query = {};
    this.getTables(query).then(response => {
      this.apiObjOptions = response.data;
  });

  },
  methods: {
    /** 查询API服务配置列表 */
    getList() {
      this.loading = true;
      listApiServiceConfig(this.queryParams).then(response => {
        this.apiServiceConfigList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 请求方式字典翻译
    requestTypeFormat(row, column) {
      return this.selectDictLabel(this.requestTypeOptions, row.requestType);
    },
    // 数据级别字典翻译
    dataLevelFormat(row, column) {
      return this.selectDictLabel(this.dataLevelOptions, row.dataLevel);
    },
    apiObjFormat(row, column) {
      return this.selectDictLabel(this.dataLevelOptions, row.dataLevel);
    },

    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        apiName: null,
        apiCode: null,
        requestType: null,
        apiObj: null,
        appName: null,
        apiUrl: null,
        dataType: null,
        paramTemplate: null,
        createId: null,
        createName: null,
        createTime: null,
        dataLevel: null,
        orderNo: null
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
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加API服务配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getApiServiceConfig(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改API服务配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateApiServiceConfig(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addApiServiceConfig(this.form).then(response => {
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
      this.$confirm('是否确认删除API服务配置编号为"' + ids + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delApiServiceConfig(ids);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('integration/apiServiceConfig/export', {
        ...this.queryParams
      }, `integration_apiServiceConfig.xlsx`)
    },
    /** 测试API操作 */
    handleApiTest(row) {
      if(row.dataType==1){
        this.msgSuccess("api已启用无需测试");
      }
      const ids = row.id || this.ids;
      return testApiServiceConfig(ids);
    }
  }
};
</script>
