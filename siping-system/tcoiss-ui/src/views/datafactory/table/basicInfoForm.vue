<template>
  <el-form ref="basicInfoForm" :model="info" :rules="rules" label-width="150px">
    <el-row>
      <el-col :span="12">
        <el-form-item label="表名称" prop="busTableName">
          <el-input placeholder="请输入仓库名称" v-model="info.busTableName" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="表描述" prop="busTableComment">
          <el-input placeholder="请输入" v-model="info.busTableComment" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item label="同步api编码" prop="syncApiCode">
          <el-input v-model="info.syncApiCode" placeholder="请输入同步api编码" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="业务分组名称" prop="busGroupName">
          <el-input v-model="info.busGroupName" placeholder="请输入业务分组名称" />
        </el-form-item>
      </el-col>
      <el-col :span="24">
        <el-form-item label="备注" prop="remark">
          <el-input type="textarea" :rows="3" v-model="info.remark"></el-input>
        </el-form-item>
      </el-col>
    </el-row>
  </el-form>
</template>
<script>
export default {
  name: "BasicInfoForm",
  props: {
    info: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      rules: {
        busTableName: [
          { required: true, message: "请输入表名称", trigger: "blur" }
        ],
        busTableComment: [
          { required: true, message: "请输入表描述", trigger: "blur" }
        ],
        syncApiCode: [
          { required: true, message: "请输入同步api编码", trigger: "blur" }
        ],
        busGroupName: [
          { required: true, message: "请输入业务分组", trigger: "blur" }
        ]
      }
    };
  }
};
</script>
