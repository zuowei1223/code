<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="所属方案" prop="schemeId">
        <el-input
          v-model="queryParams.schemeId"
          placeholder="请输入所属方案"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="作业名称" prop="workName">
        <el-input
          v-model="queryParams.workName"
          placeholder="请输入作业名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="业务表" prop="tableName">
        <el-input
          v-model="queryParams.tableName"
          placeholder="请输入业务表"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="作业类型" prop="workType">
        <el-select v-model="queryParams.workType" placeholder="请选择作业类型" clearable size="small">
          <el-option
            v-for="dict in workTypeOptions"
            :key="dict.dictValue"
            :label="dict.dictLabel"
            :value="dict.dictValue"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="数据状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择数据状态" clearable size="small">
          <el-option
            v-for="dict in statusOptions"
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
          v-hasPermi="['datafactory:work:add']"
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
          v-hasPermi="['datafactory:work:edit']"
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
          v-hasPermi="['datafactory:work:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
		  plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['datafactory:work:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="workList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <!--<el-table-column label="编号" align="center" prop="workId" />-->
      <el-table-column label="所属方案" align="center" prop="schemeId" />
      <el-table-column label="作业名称" align="center" prop="workName" />
      <el-table-column label="业务表" align="center" prop="tableName" />
      <el-table-column label="作业说明" align="center" prop="workExplain" />
      <el-table-column label="作业类型" align="center" prop="workType" :formatter="workTypeFormat" />
      <el-table-column label="数据状态" align="center" prop="status" :formatter="statusFormat" />
      <el-table-column label="创建人姓名" align="center" prop="creatorName" />
      <el-table-column label="创建日期" align="center" prop="createDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['datafactory:work:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['datafactory:work:remove']"
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

    <!-- 添加或修改作业对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="所属方案" prop="schemeId">
          <el-input v-model="form.schemeId" placeholder="请输入所属方案" />
        </el-form-item>
        <el-form-item label="作业名称" prop="workName">
          <el-input v-model="form.workName" placeholder="请输入作业名称" />
        </el-form-item>
        <el-form-item label="业务表" prop="tableName">
          <el-input v-model="form.tableName" placeholder="请输入业务表" />
        </el-form-item>
        <el-form-item label="作业说明" prop="workExplain">
          <el-input v-model="form.workExplain" placeholder="请输入作业说明" />
        </el-form-item>
        <el-form-item label="作业类型" prop="workType">
          <el-select v-model="form.workType" placeholder="请选择作业类型">
            <el-option
              v-for="dict in workTypeOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="parseInt(dict.dictValue)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="作业脚本" prop="workScript">
          <el-input v-model="form.workScript" type="textarea" placeholder="请输入内容" />
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
import { listWork, getWork, delWork, addWork, updateWork } from "@/api/datafactory/work";

export default {
  name: "Work",
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
      // 作业表格数据
      workList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 作业类型字典
      workTypeOptions: [],
      // 数据状态字典
      statusOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        schemeId: null,
        workName: null,
        tableName: null,
        workType: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        schemeId: [
          { required: true, message: "所属方案不能为空", trigger: "blur" }
        ],
        workName: [
          { required: true, message: "作业名称不能为空", trigger: "blur" }
        ],
        tableName: [
          { required: true, message: "业务表不能为空", trigger: "blur" }
        ],
        workType: [
          { required: true, message: "作业类型不能为空", trigger: "change" }
        ],
      }
    };
  },
  created() {
    this.getList();
    this.getDicts("work_type").then(response => {
      this.workTypeOptions = response.data;
    });
    this.getDicts("data_level").then(response => {
      this.statusOptions = response.data;
    });
  },
  methods: {
    /** 查询作业列表 */
    getList() {
      this.loading = true;
      listWork(this.queryParams).then(response => {
        this.workList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 作业类型字典翻译
    workTypeFormat(row, column) {
      return this.selectDictLabel(this.workTypeOptions, row.workType);
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
        workId: null,
        schemeId: null,
        workName: null,
        tableName: null,
        workExplain: null,
        workType: null,
        workScript: null,
        status: 0,
        creatorName: null,
        createDate: null
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
      this.ids = selection.map(item => item.workId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加作业";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const workId = row.workId || this.ids
      getWork(workId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改作业";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.workId != null) {
            updateWork(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addWork(this.form).then(response => {
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
      const workIds = row.workId || this.ids;
      this.$confirm('是否确认删除作业编号为"' + workIds + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delWork(workIds);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('datafactory/work/export', {
        ...this.queryParams
      }, `datafactory_work.xlsx`)
    }
  }
};
</script>
