<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="连接ip" prop="ipAddress">
        <el-input
          v-model="queryParams.ipAddress"
          placeholder="请输入连接ip"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="数据库类型" prop="databaseType">
        <el-select v-model="queryParams.databaseType" placeholder="请选择数据库类型" clearable size="small">
          <el-option
            v-for="dict in databaseTypeOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="创建日期" prop="createDate">
        <el-date-picker clearable size="small"
          v-model="queryParams.createDate"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="选择创建日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="数据库名称" prop="databaseName">
        <el-input
          v-model="queryParams.databaseName"
          placeholder="请输入数据库名称"
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
          v-hasPermi="['integration:datasource:add']"
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
          v-hasPermi="['integration:datasource:edit']"
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
          v-hasPermi="['integration:datasource:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
		  plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['integration:datasource:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="datasourceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="连接ip" align="center" prop="ipAddress" />
      <el-table-column label="用户名" align="center" prop="userName" />
      <el-table-column label="密码" align="center" prop="passWord" />
      <el-table-column label="连接属性" align="center" prop="conncetAttr" />
      <el-table-column label="数据库类型" align="center" prop="databaseType" :formatter="databaseTypeFormat" />
      <el-table-column label="创建人编号" align="center" prop="creatorId" />
      <el-table-column label="创建人姓名" align="center" prop="creatorName" />
      <el-table-column label="创建日期" align="center" prop="createDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="数据级别" align="center" prop="dataLevel" :formatter="dataLevelFormat" />
      <el-table-column label="排序号" align="center" prop="orderNo" />
      <el-table-column label="数据库名称" align="center" prop="databaseName" />
      <el-table-column label="连接端口" align="center" prop="ipPort" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['integration:datasource:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['integration:datasource:remove']"
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

    <!-- 添加或修改数据源配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="50%" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="数据库名称" prop="databaseName">
          <el-input v-model="form.databaseName" placeholder="请输入数据库名称" />
        </el-form-item>
        <el-form-item label="连接ip" prop="ipAddress">
          <el-input v-model="form.ipAddress" placeholder="请输入连接ip" />
        </el-form-item>
        <el-form-item label="连接端口" prop="ipPort">
          <el-input v-model="form.ipPort" placeholder="请输入连接端口" />
        </el-form-item>
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="passWord">
          <el-input v-model="form.passWord" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="连接属性" prop="conncetAttr">
          <el-input v-model="form.conncetAttr" placeholder="请输入连接属性" />
        </el-form-item>
        <el-form-item label="数据库类型" prop="databaseType">
          <el-select v-model="form.databaseType" placeholder="请选择数据库类型">
            <el-option
              v-for="dict in databaseTypeOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
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
import { listDatasource, getDatasource, delDatasource, addDatasource, updateDatasource } from "@/api/integration/datasource";

export default {
  name: "Datasource",
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
      // 数据源配置表格数据
      datasourceList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 数据库类型字典
      databaseTypeOptions: [],
      // 数据级别字典
      dataLevelOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        ipAddress: null,
        databaseType: null,
        createDate: null,
        databaseName: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        ipAddress: [
          { required: true, message: "连接ip不能为空", trigger: "blur" }
        ],
        userName: [
          { required: true, message: "用户名不能为空", trigger: "blur" }
        ],
        passWord: [
          { required: true, message: "密码不能为空", trigger: "blur" }
        ],
        databaseType: [
          { required: true, message: "数据库类型不能为空", trigger: "change" }
        ],
        ipPort: [
          { required: true, message: "连接端口不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getDicts("database_type").then(response => {
      this.databaseTypeOptions = response.data;
    });
    this.getDicts("data_level").then(response => {
      this.dataLevelOptions = response.data;
    });
  },
  methods: {
    /** 查询数据源配置列表 */
    getList() {
      this.loading = true;
      listDatasource(this.queryParams).then(response => {
        this.datasourceList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 数据库类型字典翻译
    databaseTypeFormat(row, column) {
      return this.selectDictLabel(this.databaseTypeOptions, row.databaseType);
    },
    // 数据级别字典翻译
    dataLevelFormat(row, column) {
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
        ipAddress: null,
        userName: null,
        passWord: null,
        conncetAttr: null,
        databaseType: null,
        creatorId: null,
        creatorName: null,
        createDate: null,
        dataLevel: null,
        orderNo: null,
        databaseName: null,
        ipPort: null
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
      this.title = "添加数据源配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDatasource(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改数据源配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateDatasource(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addDatasource(this.form).then(response => {
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
      this.$confirm('是否确认删除数据源配置编号为"' + ids + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delDatasource(ids);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('integration/datasource/export', {
        ...this.queryParams
      }, `integration_datasource.xlsx`)
    }
  }
};
</script>
