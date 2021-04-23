<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="业务表名称" prop="busTableName">
        <el-input
          v-model="queryParams.busTableName"
          placeholder="请输入业务表名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="业务分组名称" prop="busGroupName">
        <el-input
          v-model="queryParams.busGroupName"
          placeholder="请输入业务分组名称"
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
          v-hasPermi="['bus:table:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-upload"
          size="mini"
          @click="openImportTable"
          v-hasPermi="['bus:table:import']"
        >导入表</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
		  plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['bus:table:edit']"
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
          v-hasPermi="['bus:table:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
		  plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['bus:table:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="tableList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="tableId" />
      <el-table-column label="业务表名称" align="center" prop="busTableName" />
      <el-table-column label="业务表说明" align="center" prop="busTableComment" />
      <el-table-column label="api编码" align="center" prop="syncApiCode" />
      <el-table-column label="分组名称" align="center" prop="busGroupName" />
      <el-table-column label="拆分表名" align="center" prop="" />
      <el-table-column label="子表名" align="center" prop="" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="handleSyncJg(scope.row)"
          >同步结构</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-refresh"
            @click="handleInitData(scope.row)"
          >初始化数据</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleEditTable(scope.row)"
          >编辑</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['bus:table:remove']"
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

    <!-- 添加或修改代码生成业务对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="表物理名" prop="busTableName">
              <el-input v-model="form.busTableName" placeholder="请输入表物理名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="表业务名" prop="busTableComment">
              <el-input v-model="form.busTableComment"  placeholder="请输入业务名" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="同步接口" prop="syncApiCode">
              <el-select v-model="form.syncApiCode" placeholder="请选择同步接口" clearable size="small">
                <el-option
                  v-for="api in syncApiOptions"
                  :key="api.apiCode"
                  :label="api.apiName"
                  :value="api.apiCode"
                />
              </el-select>


            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="同步参数" prop="syncTableParam">
              <el-input v-model="form.syncTableParam" placeholder="请输入同步参数" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分组名称" prop="busGroupName">
              <el-input v-model="form.busGroupName" placeholder="请输入分组名称" />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item prop="splitTables">
              <span slot="label">拆分表
                <el-tooltip content="存在多个拆分表时用 ， 隔开" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.splitTables" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item prop="subTables">
              <span slot="label">
                子表名
                <el-tooltip content="存在多个子表时 ， 号隔开" placement="top">
                  <i class="el-icon-question"></i>
                </el-tooltip>
              </span>
              <el-input v-model="form.subTables" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>

        </el-row>
        <!--<el-form-item label="表类型" prop="syncApiCode">
          <el-input v-model="form.syncApiCode" placeholder="请输入同步api编码" />
        </el-form-item>-->
        <!--<el-form-item label="同步api编码" prop="syncApiCode">
          <el-input v-model="form.syncApiCode" placeholder="请输入同步api编码" />
        </el-form-item>-->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
    <!--<import-table ref="import" @ok="handleQuery" />-->
  </div>
</template>

<script>
import { listTable, getTable, delTable, addTable, updateTable,syncTableJg,initTableData } from "@/api/datafactory/table";

export default {
  name: "Table",
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
      // 代码生成业务表格数据
      tableList: [],

      syncApiOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        busTableName: null,
        busTableComment: null,
        syncApiCode: null,
        busGroupName: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      }
    };
  },
  created() {
    this.getList();
    var query = {"appName":"kingdee"};
    this.getSyncApi(query).then(response => {
      this.syncApiOptions = response.data;
    });
  },
  methods: {
    /** 查询代码生成业务列表 */
    getList() {
      this.loading = true;
      listTable(this.queryParams).then(response => {
        this.tableList = response.rows;
        this.total = response.total;
        this.loading = false;
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
        tableId: null,
        busTableName: null,
        busTableComment: null,
        syncApiCode: null,
        busGroupName: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
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
      this.ids = selection.map(item => item.tableId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加业务表";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const tableId = row.tableId || this.ids
      getTable(tableId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改业务表";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.tableId != null) {
            updateTable(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addTable(this.form).then(response => {
              this.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 打开导入表弹窗 */
    openImportTable() {
      this.$refs.import.show();
    },
    
    /** 同步表结构操作 */
    handleSync(row) {
      const tableName = row.tableName;
      this.$confirm('确认要强制同步"' + tableName + '"表的关联结构吗？', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        return syncTableJg({'tableId':row.tableId});
      }).then(() => {
        this.msgSuccess("同步成功");
      })
    },
    handleCreateTable(row){
      const tableName = row.tableName;
      this.$confirm('确认要创建"' + tableName + '"表，及其关联表吗？', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        return createTable({'tableId':row.tableId});
      }).then(() => {
        this.msgSuccess("同步成功");
    })
    },
    handleInitData(row) {
      const tableName = row.tableName;
      this.$confirm('确认要强制同步"' + tableName + '"表的关联结构吗？', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        return initTableData({'tableId':row.tableId});
      }).then(() => {
        this.msgSuccess("同步成功");
    })
    },
    /** 修改按钮操作 */
    handleEditTable(row) {
      const tableId = row.tableId || this.ids[0];
      this.$router.push("/bus/editTable/" + tableId);
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const tableIds = row.tableId || this.ids;
      this.$confirm('是否确认删除代码生成业务编号为"' + tableIds + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delTable(tableIds);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('gen/table/export', {
        ...this.queryParams
      }, `gen_table.xlsx`)
    }
  }
};
</script>
