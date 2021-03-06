<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="方案名称" prop="schemeName">
        <el-input
          v-model="queryParams.schemeName"
          placeholder="请输入方案名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="执行方式" prop="executeType">
        <el-select v-model="queryParams.executeType" placeholder="请选择执行方式" clearable size="small">
          <el-option
            v-for="dict in executeTypeOptions"
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
          v-hasPermi="['datafactory:scheme:add']"
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
          v-hasPermi="['datafactory:scheme:edit']"
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
          v-hasPermi="['datafactory:scheme:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
		  plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['datafactory:scheme:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="schemeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!--<el-table-column label="方案ID" align="center" prop="schemeId" />-->
      <el-table-column label="方案名称" align="center" prop="schemeName" />
      <el-table-column label="执行编码" align="center" prop="executeNumber" />
      <el-table-column label="执行方式" align="center" prop="executeType" :formatter="executeTypeFormat" />
      <el-table-column label="执行策略" align="center" prop="executeStrategy" />
      <el-table-column label="创建人姓名" align="center" prop="creatorName" />
      <el-table-column label="创建日期" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="数据状态" align="center" prop="status" :formatter="statusFormat" />
      <el-table-column label="方案描述" align="center" prop="schemeExplain" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['datafactory:scheme:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['datafactory:scheme:remove']"
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

    <!-- 添加或修改执行方案对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="方案名称" prop="schemeName">
          <el-input v-model="form.schemeName" placeholder="请输入方案名称" />
        </el-form-item>
        <el-form-item label="执行编码" prop="executeNumber">
          <el-input v-model="form.executeNumber" placeholder="请输入执行编码" />
        </el-form-item>
        <el-form-item label="执行方式" prop="executeType">
          <el-select v-model="form.executeType" placeholder="请选择执行方式">
            <el-option
              v-for="dict in executeTypeOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="parseInt(dict.dictValue)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="执行策略" prop="executeStrategy">
          <el-select v-model="form.executeStrategy" placeholder="请选择执行策略">
            <el-option label="请选择字典生成" value="" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建人姓名" prop="creatorName">
          <el-input v-model="form.creatorName" placeholder="请输入创建人姓名" />
        </el-form-item>
        <el-form-item label="数据状态">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in statusOptions"
              :key="dict.dictValue"
              :label="parseInt(dict.dictValue)"
            >{{dict.dictLabel}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="方案描述" prop="schemeExplain">
          <el-input v-model="form.schemeExplain" placeholder="请输入方案描述" />
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
import { listScheme, getScheme, delScheme, addScheme, updateScheme } from "@/api/datafactory/scheme";

export default {
  name: "Scheme",
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
      // 执行方案表格数据
      schemeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 执行方式字典
      executeTypeOptions: [],
      // 数据状态字典
      statusOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        schemeName: null,
        executeType: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        executeNumber: [
          { required: true, message: "执行编码不能为空", trigger: "blur" }
        ],
        executeType: [
          { required: true, message: "执行方式不能为空", trigger: "change" }
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getDicts("execute_type").then(response => {
      this.executeTypeOptions = response.data;
    });
    this.getDicts("data_level").then(response => {
      this.statusOptions = response.data;
    });
  },
  methods: {
    /** 查询执行方案列表 */
    getList() {
      this.loading = true;
      listScheme(this.queryParams).then(response => {
        this.schemeList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 执行方式字典翻译
    executeTypeFormat(row, column) {
      return this.selectDictLabel(this.executeTypeOptions, row.executeType);
    },
    // 数据状态字典翻译
    statusFormat(row, column) {
      return this.selectDictLabel(this.statusOptions, row.status);
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        schemeId: null,
        schemeName: null,
        executeNumber: null,
        executeType: null,
        executeStrategy: null,
        creatorName: null,
        createTime: null,
        status: 0,
        schemeExplain: null
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
      this.ids = selection.map(item => item.schemeId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加执行方案";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const schemeId = row.schemeId || this.ids
      getScheme(schemeId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改执行方案";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.schemeId != null) {
            updateScheme(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addScheme(this.form).then(response => {
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
      const schemeIds = row.schemeId || this.ids;
      this.$confirm('是否确认删除执行方案编号为"' + schemeIds + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delScheme(schemeIds);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('datafactory/scheme/export', {
        ...this.queryParams
      }, `datafactory_scheme.xlsx`)
    }
  }
};
</script>
