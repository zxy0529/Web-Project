<template>
  <div class="min-h-screen flex flex-col bg-blue-50 p-6">
    <!-- 搜索和操作按钮 -->
    <div class="flex flex-wrap items-center gap-4 mb-4">
      <el-input
          v-model="data.name"
          prefix-icon="Search"
          placeholder="请输入职位名称查询"
          class="w-60"
      />
      <el-button type="info" plain @click="load" class="h-10">查询</el-button>
      <el-button type="warning" plain @click="reset" class="h-10">重置</el-button>
      <div class="ml-auto flex gap-2">
        <el-button type="primary" plain @click="handleAdd" class="h-10">新增</el-button>
        <el-button type="danger" plain @click="delBatch" class="h-10">批量删除</el-button>
      </div>
    </div>

    <!-- 职位列表表格 -->
    <div class="bg-white rounded-lg shadow p-4 mb-4 overflow-x-auto">
      <el-table
          stripe
          :data="data.tableData"
          @selection-change="handleSelectionChange"
          class="min-w-[900px]"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="company" label="公司" min-width="120" />
        <el-table-column prop="name" label="职位名称" min-width="120" />
        <el-table-column prop="salary" label="薪资" min-width="100" />
        <el-table-column prop="description" label="职位描述" min-width="200" />
        <el-table-column prop="requirement" label="任职要求" min-width="200" />
        <el-table-column prop="base" label="工作地点" min-width="120" />
        <el-table-column prop="type" label="职位类型" min-width="100">
          <template #default="{ row }">
            <span
                class="px-2 py-1 rounded text-sm font-medium"
                :class="row.type === 1 ? 'bg-green-200 text-green-800' : 'bg-blue-200 text-blue-800'"
            >
              {{ row.type === 1 ? '实习' : '全职' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" fixed="right" width="120" align="center">
          <template #default="{ row }">
            <el-button
                type="primary"
                circle
                size="small"
                :icon="Edit"
                @click="handleEdit(row)"
                aria-label="编辑"
            />
            <el-button
                type="danger"
                circle
                size="small"
                :icon="Delete"
                @click="del(row.id)"
                aria-label="删除"
                class="ml-2"
            />
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="bg-white rounded-lg shadow p-4 flex justify-end">
      <el-pagination
          @current-change="load"
          background
          layout="prev, pager, next"
          :page-size="data.pageSize"
          v-model:current-page="data.pageNum"
          :total="data.total"
      />
    </div>

    <!-- 职位信息弹窗 -->
    <el-dialog
        title="职位信息"
        v-model="data.formVisible"
        width="40%"
        destroy-on-close
        class="rounded-lg"
    >
      <el-form ref="form" :model="data.form" label-width="100px" class="space-y-4 px-4">
        <el-form-item label="公司" prop="company">
          <el-input v-model="data.form.company" placeholder="请输入公司名称" />
        </el-form-item>
        <el-form-item label="职位名称" prop="name">
          <el-input v-model="data.form.name" placeholder="请输入职位名称" />
        </el-form-item>
        <el-form-item label="薪资" prop="salary">
          <el-input v-model="data.form.salary" placeholder="请输入薪资范围" />
        </el-form-item>
        <el-form-item label="职位描述" prop="description">
          <el-input
              type="textarea"
              rows=3
              v-model="data.form.description"
              placeholder="请输入职位描述"
              class="resize-none"
          />
        </el-form-item>
        <el-form-item label="任职要求" prop="requirement">
          <el-input
              type="textarea"
              rows=3
              v-model="data.form.requirement"
              placeholder="请输入任职要求"
              class="resize-none"
          />
        </el-form-item>
        <el-form-item label="工作地点" prop="base">
          <el-input v-model="data.form.base" placeholder="请输入工作地点" />
        </el-form-item>
        <el-form-item label="职位类型" prop="type">
          <el-select v-model="data.form.type" placeholder="请选择职位类型" class="w-full">
            <el-option label="实习" :value="1" />
            <el-option label="全职" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="flex justify-end space-x-4 px-4 pb-4">
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="save">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {reactive} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";
import {Delete, Edit} from "@element-plus/icons-vue";


const data = reactive({
  user: JSON.parse(localStorage.getItem('current-user-role') || '{}'),
  formVisible: false,
  form: {},
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  name: null,
  ids: []
})

const baseUrl = import.meta.env.VITE_BASE_URL
const handleFileUpload = (res) => {
  data.form.img = res.data
}

const load = () => {
  request.get('/jobs/selectPage', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      name: data.name
    }
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data?.list || []
      data.total = res.data?.total
    }
  })
}
const handleAdd = () => {
  data.form = {}
  data.formVisible = true
}
const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}
const add = () => {
  request.post('/jobs/add', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const update = () => {
  request.put('/jobs/update', data.form).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    }
  })
}

const save = () => {
  data.form.id ? update() : add()
}

const del = (id) => {
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete('/jobs/delete/' + id).then(res => {
      if (res.code === '200') {
        ElMessage.success("删除成功")
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
    console.error(err)
  })
}
const delBatch = () => {
  if (!data.ids.length) {
    ElMessage.warning("请选择数据")
    return
  }
  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }).then(res => {
    request.delete("/jobs/delete/batch", {data: data.ids}).then(res => {
      if (res.code === '200') {
        ElMessage.success('操作成功')
        load()
      } else {
        ElMessage.error(res.msg)
      }
    })
  }).catch(err => {
    console.error(err)
  })
}
const handleSelectionChange = (rows) => {
  data.ids = rows.map(v => v.id)
}

const reset = () => {
  data.name = null
  load()
}

load()
</script>
