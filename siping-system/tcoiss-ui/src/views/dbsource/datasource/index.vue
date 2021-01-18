<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="数据源id" prop="datasourceId">
        <el-input
          v-model="queryParams.datasourceId"
          placeholder="请输入数据源id"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="用户名" prop="userName">
        <el-input
          v-model="queryParams.userName"
          placeholder="请输入用户名"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>


      <el-form-item label="数据库类型" prop="databasetype">
        <el-select v-model="queryParams.databasetype" placeholder="请选择数据库类型" clearable size="small">
          <el-option
            v-for="dict in databasetypeOptions"
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
          v-hasPermi="['dbsource:datasource:add']"
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
          v-hasPermi="['dbsource:datasource:edit']"
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
          v-hasPermi="['dbsource:datasource:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
		  plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['dbsource:datasource:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="datasourceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="数据源的id" align="center" prop="datasourceId" />
      <el-table-column label="连接信息" align="center" prop="url" />
      <el-table-column label="用户名" align="center" prop="userName" />
      <el-table-column label="密码" align="center" prop="passWord" />
      <el-table-column label="暂留字段" align="center" prop="code" />
      <el-table-column label="数据库类型" align="center" prop="databasetype" :formatter="databasetypeFormat" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['dbsource:datasource:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['dbsource:datasource:remove']"
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

    <!-- 添加或修改datasource对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="数据源的id" prop="datasourceId">
          <el-input v-model="form.datasourceId" placeholder="请输入数据源的id" />
        </el-form-item>
        <el-form-item label="连接信息" prop="url">
          <el-input v-model="form.url" placeholder="请输入连接信息" />
        </el-form-item>
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="form.userName" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="passWord">
          <el-input v-model="form.passWord" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="暂留字段" prop="code">
          <el-input v-model="form.code" placeholder="请输入暂留字段" />
        </el-form-item>
        <el-form-item label="数据库类型" prop="databasetype">
          <el-select v-model="form.databasetype" placeholder="请选择数据库类型">
            <el-option
              v-for="dict in databasetypeOptions"
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
import { listDatasource, getDatasource, delDatasource, addDatasource, updateDatasource } from "@/api/dbsource/datasource";

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
      // datasource表格数据
      datasourceList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 数据库类型字典
      databasetypeOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        datasourceId: null,
        url: null,
        userName: null,
        passWord: null,
        code: null,
        databasetype: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        datasourceId: [
          { required: true, message: "数据源的id不能为空", trigger: "blur" }
        ],
        url: [
          { required: true, message: "连接信息不能为空", trigger: "blur" }
        ],
        userName: [
          { required: true, message: "用户名不能为空", trigger: "blur" }
        ],
        passWord: [
          { required: true, message: "密码不能为空", trigger: "blur" }
        ],
        databasetype: [
          { required: true, message: "数据库类型不能为空", trigger: "change" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getDicts("sys_database_type").then(response => {
      this.databasetypeOptions = response.data;
    });
  },
  methods: {
    /** 查询datasource列表 */
    getList() {
      this.loading = true;
      listDatasource(this.queryParams).then(response => {
        this.datasourceList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 数据库类型字典翻译
    databasetypeFormat(row, column) {
      return this.selectDictLabel(this.databasetypeOptions, row.databasetype);
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        datasourceId: null,
        url: null,
        userName: null,
        passWord: null,
        code: null,
        databasetype: null
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
      this.ids = selection.map(item => item.datasourceId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加datasource";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const datasourceId = row.datasourceId || this.ids
      getDatasource(datasourceId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改datasource";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.datasourceId != null) {
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
      const datasourceIds = row.datasourceId || this.ids;
      this.$confirm('是否确认删除datasource编号为"' + datasourceIds + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delDatasource(datasourceIds);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('dbsource/datasource/export', {
        ...this.queryParams
      }, `dbsource_datasource.xlsx`)
    }
  }
};
</script>
