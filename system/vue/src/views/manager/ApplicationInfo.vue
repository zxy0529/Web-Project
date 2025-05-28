<template>
  <div class="p-6 bg-blue-50 min-h-screen">

    <!-- 操作按钮 -->
    <div class="bg-white rounded-xl shadow p-4 mb-4">
      <el-button  class="bg-blue-600 text-white hover:bg-red-600" @click="delBatch">批量删除</el-button>
    </div>

    <!-- 表格 -->
    <div class="bg-white rounded-xl shadow p-4 mb-4">
      <el-table stripe :data="data.tableData" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="userName" label="申请人" />
        <el-table-column prop="jobName" label="职位名称" />
        <el-table-column prop="status" label="审核状态" />
        <el-table-column prop="auditTime" label="审核时间" />
        <el-table-column prop="file" label="简历">
          <template #default="{ row }">
            <a
                v-if="row.file"
                :href="row.file"
                target="_blank"
                class="text-blue-600 hover:underline"
            >
              查看简历
            </a>
            <span v-else class="text-gray-400">无</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="160" fixed="right">
          <template v-slot="scope">
            <el-button
                size="small"
                class="bg-blue-500 text-white hover:bg-blue-600"
                v-if="data.user.role === 'COMPANY_USER'"
                @click="handleEdit(scope.row)"
            >
              审核
            </el-button>
            <el-button
                size="small"
                class="bg-red-500 text-white hover:bg-red-600 ml-2"
                @click="del(scope.row.id)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div class="bg-white rounded-xl shadow p-4" v-if="data.total">
      <el-pagination
          background
          layout="prev, pager, next"
          :page-size="data.pageSize"
          v-model:current-page="data.pageNum"
          :total="data.total"
          @current-change="load"
      />
    </div>

    <!-- 弹窗 -->
    <el-dialog title="申请审核" v-model="data.formVisible" width="40%" destroy-on-close>
      <el-form ref="form" :model="data.form" label-width="90px" class="px-4 py-2">
        <el-form-item prop="status" label="审核状态">
          <el-radio-group v-model="data.form.status">
            <el-radio-button  label="发放offer" value="审核通过" />
            <el-radio-button  label="审核拒绝" value="审核拒绝" />
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer flex justify-end gap-3 p-3">
          <el-button @click="data.formVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>


<script setup>

import {reactive} from "vue";
import request from "@/utils/request.js";
import {ElMessage, ElMessageBox} from "element-plus";



const data = reactive({
  user: JSON.parse(localStorage.getItem('current-user-role') || '{}'),
  formVisible: false,
  form: {},
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  jobName: null,
  ids: []
})

const load = () => {
  const isPublisher = data.user.role === 'COMPANY_USER' // 判断是否是发布者角色

  // 管理员看全部，发布者看自己发布的职位的申请，普通用户看自己投递的申请
  let url = ''
  const params = {
    pageNum: data.pageNum,
    pageSize: data.pageSize,
    jobName: data.jobName,
  }

  if (data.user.role === 'ADMIN') {
    url = '/application/selectPage'
  } else if (isPublisher) {
    url = '/application/selectByPublisher'
  } else {
    url = '/application/selectMyPage'
    params.userId = data.user.id
  }

  request.get(url, { params }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data?.list || []
      data.total = res.data?.total || 0
    } else {
      ElMessage.error(res.msg || '加载失败')
    }
  })
}


const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row))
  data.formVisible = true
}
const add = () => {
  request.post('/application/add', data.form).then(res => {
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
  request.put('/application/update', data.form).then(res => {
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
    request.delete('/application/delete/' + id).then(res => {
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
    request.delete("/application/delete/batch", {data: data.ids}).then(res => {
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
  data.RoomName = null
  load()
}

load()
</script>