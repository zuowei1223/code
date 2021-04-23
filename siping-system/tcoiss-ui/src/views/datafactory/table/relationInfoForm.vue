<template>
  <el-form ref="relationInfoForm" :model="info" :rules="rules" label-width="150px">
    <el-row>
      <el-col :span="12">
        <el-form-item prop="splitTables">
          <span slot="label">拆分表名
            <el-tooltip content="数据转换时会关联的子表名称" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="info.splitTables" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item prop="splitApiCode">
          <span slot="label">拆分表api编码</span>
          <el-input v-model="info.splitApiCode" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="splitGlsqlScript">
          <span slot="label">关联拆分表脚本</span>
          <el-input v-model="info.splitGlsqlScript" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="subTables">
          <span slot="label">
            子表名
            <el-tooltip content="数据转换时会关联的子表名称" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="info.subTables" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="subGlsqlScript">
          <span slot="label">
            关联子表脚本
            <el-tooltip content="可写sql关联语句" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="info.subGlsqlScript" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="resultType">
          <span slot="label">
            结果类型
            <el-tooltip content="可以为表，视图，" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="info.resultType" />
        </el-form-item>
      </el-col>

      <el-col :span="12">
        <el-form-item prop="resultTableName">
          <span slot="label">
            结果名称
            <el-tooltip content="结果类型为表时需要先建表，视图可以在做转换时生成" placement="top">
              <i class="el-icon-question"></i>
            </el-tooltip>
          </span>
          <el-input v-model="info.resultTableName" />
        </el-form-item>
      </el-col>

    </el-row>

  </el-form>
</template>
<script>
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  name: "RelationInfoForm",
  components: { Treeselect },
  props: {
    info: {
      type: Object,
      default: null
    },
    menus: {
      type: Array,
      default: []
    },
  },
  data() {
    return {
      rules: {

      }
    };
  },
  created() {},
  methods: {
    /** 转换菜单数据结构 */
    /*normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.menuId,
        label: node.menuName,
        children: node.children
      };
    }*/
  }
};
</script>
