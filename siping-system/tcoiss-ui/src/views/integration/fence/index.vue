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
      <!--<el-form-item label="平台编号" prop="fenceGid">
        <el-input
          v-model="queryParams.fenceGid"
          placeholder="请输入平台编号"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>-->
      <el-form-item label="名称" prop="fenceName">
        <el-input
          v-model="queryParams.fenceName"
          placeholder="请输入名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <!--<el-form-item label="监控状态" prop="fenceEnable">
        <el-input
          v-model="queryParams.fenceEnable"
          placeholder="请输入监控状态"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>-->
      <el-form-item label="过期日期" prop="validTime">
        <el-input
          v-model="queryParams.validTime"
          placeholder="请输入过期日期"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="监控日期" prop="fenceRepeat">
        <el-input
          v-model="queryParams.fenceRepeat"
          placeholder="请输入监控日期"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <!--<el-form-item label="监控时段" prop="fenceTime">
        <el-input
          v-model="queryParams.fenceTime"
          placeholder="请输入监控时段"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>-->
      <!--<el-form-item label="描述信息" prop="fenceDesc">
        <el-input
          v-model="queryParams.fenceDesc"
          placeholder="请输入描述信息"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>-->
      <!--<el-form-item label="配置触发围栏所需动作" prop="alertCondition">
        <el-input
          v-model="queryParams.alertCondition"
          placeholder="请输入配置触发围栏所需动作"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>-->
      <el-form-item label="创建人ID" prop="createorId">
        <el-input
          v-model="queryParams.createorId"
          placeholder="请输入创建人ID"
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

    <el-table v-loading="loading" :data="fenceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="围栏本地编码" align="center" prop="localKey" />
      <el-table-column label="平台编号" align="center" prop="fenceGid" />
      <el-table-column label="名称" align="center" prop="fenceName" />
      <el-table-column label="围栏坐标集" align="center" prop="fencePoints" />
      <el-table-column label="监控状态" align="center" prop="fenceEnable" />
      <el-table-column label="过期日期" align="center" prop="validTime" />
      <el-table-column label="监控日期" align="center" prop="fenceRepeat" />
      <el-table-column label="监控时段" align="center" prop="fenceTime" />
      <el-table-column label="描述信息" align="center" prop="fenceDesc" />
      <el-table-column label="配置触发围栏所需动作" align="center" prop="alertCondition" />
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
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改电子围栏对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="本地编码" prop="localKey">
          <el-input v-model="form.localKey" placeholder="请输入围栏本地编码" />
        </el-form-item>

        <el-form-item label="名称" prop="fenceName">
          <el-input v-model="form.fenceName" placeholder="请输入名称" />
        </el-form-item>

        <el-form-item label="过期日期" prop="validTime">
          <el-input v-model="form.validTime" placeholder="请输入过期日期" />
        </el-form-item>

        <el-form-item label="描述信息" prop="fenceDesc">
          <el-input v-model="form.fenceDesc" placeholder="请输入描述信息" />
        </el-form-item>

        <el-form-item label="创建人ID" prop="createorId">
          <el-input v-model="form.createorId" placeholder="请输入创建人ID" />
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
import { listFence, getFence, delFence, addFence, updateFence } from "@/api/integration/fence";

export default {
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
        fenceGid: null,
        fenceName: null,
        fencePoints: null,
        fenceEnable: null,
        validTime: null,
        fenceRepeat: null,
        fenceTime: null,
        fenceDesc: null,
        alertCondition: null,
        createorId: null
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
  },
  methods: {
    /** 查询电子围栏列表 */
    getList() {
      this.loading = true;
      listFence(this.queryParams).then(response => {
        this.fenceList = response.rows;
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
    /** 新增按钮操作 */
    handleAdd() {
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
    /** 导出按钮操作 */
    handleExport() {
      this.download('webservice/fence/export', {
        ...this.queryParams
      }, `webservice_fence.xlsx`)
    }
  }
};
</script>
