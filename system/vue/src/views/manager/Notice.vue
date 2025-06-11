<template>
  <div class="p-6 bg-blue-50 min-h-screen">

    <!-- 操作按钮 -->
    <div class="bg-white rounded-xl shadow-lg p-4 mb-4 flex justify-between items-center">
      <el-button class="bg-blue-600 text-white hover:bg-blue-700 transition duration-300" @click="addNotice">
        新增公告
      </el-button>
    </div>

    <!-- 公告列表 -->
    <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
      <div v-for="notice in data.tableData" :key="notice.id" class="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow duration-300">
        <h2 class="text-xl font-semibold text-gray-800 mb-2">{{ notice.title }}</h2>
        <p class="text-sm text-gray-600 mb-4">{{ notice.content }}</p>
        <div class="flex justify-between items-center text-xs text-gray-400">
          <span>发布时间: {{ notice.time }}</span>
          <div>
            <el-button
                size="small"
                class="bg-blue-500 text-white hover:bg-blue-600 transition duration-300"
                @click="handleEdit(notice)"
            >
              编辑
            </el-button>
            <el-button
                size="small"
                class="bg-red-500 text-white hover:bg-red-600 ml-2 transition duration-300"
                @click="del(notice.id)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="bg-white rounded-xl shadow-lg p-4 mt-6" v-if="data.total">
      <el-pagination
          background
          layout="prev, pager, next"
          :page-size="data.pageSize"
          v-model:current-page="data.pageNum"
          :total="data.total"
          @current-change="load"
          class="text-sm"
      />
    </div>

    <!-- 弹窗 -->
    <el-dialog title="公告编辑" v-model="data.formVisible" width="40%" destroy-on-close>
      <el-form ref="form" :model="data.form" label-width="90px" class="px-4 py-2">
        <el-form-item prop="title" label="公告标题">
          <el-input v-model="data.form.title" placeholder="请输入公告标题" class="rounded-xl border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200"/>
        </el-form-item>
        <el-form-item prop="content" label="公告内容">
          <el-input type="textarea" v-model="data.form.content" placeholder="请输入公告内容" rows="4" class="rounded-xl border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200"/>
        </el-form-item>
        <el-form-item prop="time" label="发布时间">
          <el-input v-model="data.form.time" placeholder="请输入公告时间" class="rounded-xl border-gray-300 focus:border-blue-500 focus:ring-2 focus:ring-blue-200"/>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer flex justify-end gap-3 p-3">
          <el-button @click="data.formVisible = false" class="bg-gray-300 text-gray-700 hover:bg-gray-400 transition duration-300">
            取消
          </el-button>
          <el-button type="primary" @click="save" class="bg-blue-600 text-white hover:bg-blue-700 transition duration-300">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";

const data = reactive({
  user: JSON.parse(localStorage.getItem('current-user-role') || '{}'),
  formVisible: false,
  form: {},
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  ids: []
});

const load = () => {
  // 加载公告数据
  request.get("/notice/selectPage", {
    params: { pageNum: data.pageNum, pageSize: data.pageSize }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data?.list || [];
      data.total = res.data?.total || 0;
    } else {
      ElMessage.error(res.msg || '加载失败');
    }
  });
};

const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.formVisible = true;
};

const addNotice = () => {
  data.form = {};
  data.formVisible = true;
};

const save = () => {
  if (data.form.id) {
    updateNotice();
  } else {
    addNoticeRequest();
  }
};

const addNoticeRequest = () => {
  request.post('/notice/add', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('新增成功');
      data.formVisible = false;
      load();
    } else {
      ElMessage.error(res.msg || '新增失败');
    }
  });
};

const updateNotice = () => {
  request.put('/notice/update', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('更新成功');
      data.formVisible = false;
      load();
    } else {
      ElMessage.error(res.msg || '更新失败');
    }
  });
};

const del = (id) => {
  ElMessageBox.confirm('删除后公告数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(() => {
    request.delete('/notice/delete/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success("删除成功");
        load();
      } else {
        ElMessage.error(res.msg);
      }
    });
  }).catch(() => {});
};

const delBatch = () => {
  if (!data.ids.length) {
    ElMessage.warning("请选择数据");
    return;
  }
  ElMessageBox.confirm('删除后公告数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(() => {
    request.delete("/notice/delete/batch", { data: data.ids }).then(res => {
      if (res.code === '200') {
        ElMessage.success('批量删除成功');
        load();
      } else {
        ElMessage.error(res.msg || '批量删除失败');
      }
    });
  }).catch(() => {});
};

const handleSelectionChange = (rows) => {
  data.ids = rows.map(v => v.id);
};

load();
</script>

<style scoped>
/* Custom styles for the new layout */
.el-button {
  transition: background-color 0.3s ease;
}

.el-input {
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

/* Card Hover Effect */
.bg-white:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* Dialog Footer Button Styling */
.dialog-footer .el-button {
  transition: background-color 0.3s ease;
}

/* Table Card Styling */
.grid {
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
}

.el-table th,
.el-table td {
  padding: 12px 16px;
  text-align: center;
}
</style>
