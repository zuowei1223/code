<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="服务名称" prop="fwName">
        <el-input
          v-model="queryParams.fwName"
          placeholder="请输入服务名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="平台ID" prop="gaodeKey">
        <el-input
          v-model="queryParams.gaodeKey"
          placeholder="请输入平台ID"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="轨迹服务ID" prop="serviceId">
        <el-input
          v-model="queryParams.serviceId"
          placeholder="请输入轨迹服务ID"
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
          v-hasPermi="['webservice:trackservice:add']"
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
          v-hasPermi="['webservice:trackservice:edit']"
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
          v-hasPermi="['webservice:trackservice:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
		  plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['webservice:trackservice:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="trackserviceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="服务名称" align="center" prop="fwName" />
      <el-table-column label="平台ID" align="center" prop="gaodeKey" :formatter="keyFormat" />
      <el-table-column label="轨迹服务ID" align="center" prop="serviceId" />
      <el-table-column label="服务描述" align="center" prop="serviceDesc" />
      <el-table-column label="创建人编号" align="center" prop="creatorId" />
      <el-table-column label="创建人姓名" align="center" prop="creatorName" />
      <el-table-column label="创建日期" align="center" prop="createDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="数据级别" align="center" prop="dataLevel" :formatter="dataLevelFormat" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['webservice:trackservice:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['webservice:trackservice:remove']"
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

    <!-- 添加或修改轨迹服务配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="服务名称" prop="fwName">
          <el-input v-model="form.fwName" placeholder="请输入服务名称" />
        </el-form-item>
        <el-form-item label="平台ID" prop="gaodeKey">
          <el-select v-model="form.gaodeKey" placeholder="请选择key">
            <el-option
              v-for="dict in keyOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="描述信息" prop="serviceDesc">
          <el-input
            type="textarea"
            :rows="2"
            placeholder="请输入内容"
            value=""
            v-model="form.serviceDesc">
          </el-input>
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
import { listTrackservice, getTrackservice, delTrackservice, addTrackservice, updateTrackservice } from "@/api/integration/trackservice";

export default {
  name: "Trackservice",
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
      // 轨迹服务配置表格数据
      trackserviceList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 平台ID字典
      keyOptions: [],
      // 数据级别字典
      dataLevelOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        fwName: null,
        key: null,
        serviceId: null,
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
    this.getDicts("gaode_key").then(response => {
      this.keyOptions = response.data;
      console.log(this.keyOptions);
    });
    this.getDicts("data_level").then(response => {
      this.dataLevelOptions = response.data;
    });
  },
  methods: {
    /** 查询轨迹服务配置列表 */
    getList() {
      this.loading = true;
      listTrackservice(this.queryParams).then(response => {
        this.trackserviceList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 平台ID字典翻译
    keyFormat(row, column) {
      return this.selectDictLabel(this.keyOptions, row.gaodeKey);
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
        fwName: null,
        key: null,
        serviceId: null,
        creatorId: null,
        creatorName: null,
        createDate: null,
        dataLevel: null
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
      this.title = "添加轨迹服务配置";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getTrackservice(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改轨迹服务配置";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateTrackservice(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addTrackservice(this.form).then(response => {
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
      this.$confirm('是否确认删除轨迹服务配置编号为"' + ids + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delTrackservice(ids);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('webservice/trackservice/export', {
        ...this.queryParams
      }, `webservice_trackservice.xlsx`)
    }
  }
};
</script>
