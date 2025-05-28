<template>
  <div class="min-h-screen p-8 bg-gradient-to-br from-blue-50 to-white">
    <!-- 标题 -->
    <header class="mb-8">
      <h1 class="text-4xl font-extrabold text-blue-900 mb-2">职位推荐</h1>
      <p class="text-blue-700 text-lg max-w-xl">
        根据您的信息智能推荐适合的职位，助力职业发展。
      </p>
    </header>


    <!-- 推荐职位卡片列表 -->
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <div
          v-for="job in filteredJobs"
          :key="job.id"
          class="bg-white rounded-xl shadow-md p-6 hover:shadow-lg transition cursor-pointer"
          @click="openApplyDialog(job)"
      >
        <h2 class="text-xl font-semibold text-blue-800 mb-2">{{ job.name }}</h2>
        <p class="text-sm text-gray-600 mb-1"><strong>公司：</strong>{{ job.company }}</p>
        <p class="text-sm text-gray-600 mb-1"><strong>薪资：</strong>{{ job.salary }}</p>
        <p class="text-sm text-gray-600 mb-2 truncate"><strong>岗位描述：</strong>{{ job.description }}</p>
        <p class="text-sm text-gray-600 mb-2 truncate"><strong>岗位要求：</strong>{{ job.requirement }}</p>
        <p class="text-sm text-gray-600 mb-3"><strong>工作地点：</strong>{{ job.base }}</p>
        <span
            class="inline-block px-3 py-1 rounded-full text-xs font-semibold"
            :class="job.type === 1 ? 'bg-green-200 text-green-800' : 'bg-blue-200 text-blue-800'"
        >
          {{ job.type === 1 ? '实习' : '全职' }}
        </span>
      </div>
    </div>

    <!-- 投递简历弹窗 -->
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
              :before-upload="beforeUpload"
              class="w-full"
              drag
              accept=".pdf,.doc,.docx"
              :file-list="fileList"
              :on-remove="handleRemove"
              :auto-upload="true"
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
import {ref, computed, onMounted} from 'vue'
import request from '@/utils/request.js'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const searchName = ref('')

const user = JSON.parse(localStorage.getItem('current-user-role') || '{}')
const userId = user.id

const baseUrl = import.meta.env.VITE_BASE_URL || '' // 上传文件接口基础路径

const formVisible = ref(false)

const fileList = ref([]) // 上传文件列表

// 投递表单数据
const form = ref({
  jobId: null,
  resumeUrl: ''
})

const load = () => {
  loading.value = true
  request
      .get(`/recommend/job/${userId}`)
      .then(res => {
        if (res.code === '200') {
          tableData.value = res.data || []
        } else {
          ElMessage.error(res.msg || '加载失败')
        }
      })
      .finally(() => {
        loading.value = false
      })
}

const filteredJobs = computed(() => {
  if (!searchName.value.trim()) return tableData.value
  return tableData.value.filter(job =>
      job.name.toLowerCase().includes(searchName.value.trim().toLowerCase())
  )
})

// 打开弹窗，传入当前职位id
const openApplyDialog = (job) => {
  form.value.jobId = job.id
  form.value.resumeUrl = ''
  fileList.value = []
  formVisible.value = true
}

// 上传前校验（文件大小限制10M以内）
const beforeUpload = (file) => {
  const isAllowedType = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document'].includes(file.type)
  if (!isAllowedType) {
    ElMessage.error('只支持pdf/doc/docx格式')
    return false
  }
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    ElMessage.error('上传文件大小不能超过10MB')
    return false
  }
  return true
}



const handleRemove = () => {
  form.value.resumeUrl = ''
  fileList.value = []
}

const handleFileUpload = (res, file) => {
  if (res.code === "200") {
    form.value.resumeUrl = res.data;
    fileList.value = [file];
    ElMessage.success("上传成功");
  } else {
    ElMessage.error(res.msg);
  }
}


const submitApplication = () => {
  if (!form.value.resumeUrl) {
    ElMessage.warning("请先上传简历文件");
    return;
  }
  request.post("/application/add", form.value).then((res) => {
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
