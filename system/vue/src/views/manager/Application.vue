<template>
  <div class="space-y-6 bg-gray-50 min-h-screen p-6">
    <!-- 搜索栏 -->
    <div
        class="flex items-center gap-4 bg-white rounded-xl shadow-sm p-4 max-w-4xl mx-auto"
    >
      <el-input
          v-model="search.name"
          placeholder="搜索职位名称"
          class="w-64 rounded-md border border-blue-300 focus-within:border-blue-500"
      />
      <el-button
          type="primary"
          class="bg-blue-500 hover:bg-blue-600 border-blue-500"
          @click="load"
      >
        搜索
      </el-button>
      <el-button
          class="text-blue-600 border border-blue-300 hover:bg-blue-100"
          @click="reset"
      >
        重置
      </el-button>
    </div>

    <!-- 职位列表 -->
    <div
        class="bg-white rounded-xl shadow-md p-6 max-w-6xl mx-auto overflow-x-auto"
    >
      <el-table :data="jobs" border stripe style="min-width: 900px">
        <el-table-column prop="name" label="职位名称" width="180" />
        <el-table-column prop="company" label="公司" width="180" />
        <el-table-column prop="base" label="工作地点" width="120" />
        <el-table-column prop="salary" label="薪资" width="100" />
        <el-table-column label="类型" prop="type" width="100">
          <template #default="scope">
            <el-tag
                effect="plain"
                :type="scope.row.type === 1 ? 'success' : 'primary'"
                class="font-semibold"
            >
              {{ scope.row.type === 1 ? '实习' : '全职' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
            prop="requirement"
            label="岗位要求"
            show-overflow-tooltip
        />
        <el-table-column
            prop="description"
            label="职位描述"
            show-overflow-tooltip
        />
        <el-table-column label="操作" width="160">
          <template #default="scope">
            <el-button
                type="info"
                size="small"
                class="bg-blue-100 text-blue-600 hover:bg-blue-200"
                @click="openDialog(scope.row)"
            >
              投递简历
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div
        class="max-w-6xl mx-auto flex justify-end bg-white rounded-xl shadow-sm p-4"
    >
      <el-pagination
          background
          layout="total, prev, pager, next"
          :total="total"
          :page-size="pageSize"
          v-model:current-page="pageNum"
          @current-change="load"
      />
    </div>

    <!-- 投递弹窗 -->
    <el-dialog
        title="投递简历"
        v-model="formVisible"
        width="480px"
        destroy-on-close
        class="rounded-lg"
    >
      <el-form :model="form" label-width="100px" class="px-4 pt-4">
        <el-form-item prop="file" label="简历">
          <el-upload
              :action="baseUrl + '/files/upload'"
              :on-success="handleFileUpload"
              class="w-full"
              drag
              accept=".pdf,.doc,.docx"
          >
            <i class="el-icon-upload"></i>
            <div class="el-upload__text mt-1 text-gray-600">
              点击上传或拖拽文件到此处，支持pdf/doc/docx格式
            </div>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="flex justify-end gap-3 p-4">
          <el-button @click="formVisible = false">取消</el-button>
          <el-button
              type="primary"
              class="bg-blue-500 hover:bg-blue-600 border-blue-500"
              @click="submitApplication"
          >
            确认投递
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>



<script setup>
import { reactive, ref, onMounted } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

const jobs = ref([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const search = reactive({ name: "", company: "" });
const formVisible = ref(false);
const form = reactive({ jobId: null, file: "" });
const baseUrl = import.meta.env.VITE_BASE_URL

const load = () => {
  request
      .get("/jobs/selectPage", {
        params: {
          pageNum: pageNum.value,
          pageSize: pageSize.value,
          name: search.name,
          company: search.company,
        },
      })
      .then((res) => {
        if (res.code === "200") {
          jobs.value = res.data.list;
          total.value = res.data.total;
        } else {
          ElMessage.error(res.msg);
        }
      });
};

const reset = () => {
  search.name = "";
  search.company = "";
  load();
};

const openDialog = (job) => {
  form.jobId = job.id;
  form.file = "";
  formVisible.value = true;
};

const handleFileUpload = (res) => {
  if (res.code === "200") {
    form.file = res.data;
    ElMessage.success("上传成功");
  } else {
    ElMessage.error(res.msg);
  }
};

const submitApplication = () => {
  if (!form.file) {
    ElMessage.warning("请先上传简历文件");
    return;
  }
  request.post("/application/add", form).then((res) => {
    if (res.code === "200") {
      ElMessage.success("投递成功，等待审核");
      formVisible.value = false;
    } else {
      ElMessage.error(res.msg);
    }
  });
};

onMounted(load);
</script>
