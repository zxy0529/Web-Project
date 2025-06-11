<template>
  <div class="p-6 min-h-screen bg-blue-50">
    <!-- 仪表盘标题 -->
    <header class="mb-6">
      <h1 class="text-3xl font-bold text-blue-900">管理仪表盘</h1>
      <p class="text-gray-600">查看求职者投递情况与岗位投递数据概览</p>
    </header>

    <!-- 数据概览卡片 -->
    <div class="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
      <div class="bg-white rounded-xl shadow p-4">
        <p class="text-gray-500">总投递数</p>
        <h2 class="text-2xl font-bold text-blue-700">{{ stats.total }}</h2>
      </div>
      <div class="bg-white rounded-xl shadow p-4">
        <p class="text-gray-500">实习岗位投递数</p>
        <h2 class="text-2xl font-bold text-green-700">{{ stats.intern }}</h2>
      </div>
      <div class="bg-white rounded-xl shadow p-4">
        <p class="text-gray-500">全职岗位投递数</p>
        <h2 class="text-2xl font-bold text-purple-700">{{ stats.fulltime }}</h2>
      </div>
    </div>

    <!-- 饼图：按岗位名称统计 -->
    <div class="bg-white rounded-xl shadow p-6 mb-6">
      <h3 class="text-lg font-semibold text-gray-800 mb-4">岗位投递数量统计</h3>
      <v-chart :option="pieChartOptions" style="height: 300px;" />
    </div>

    <div class="bg-white rounded-xl shadow p-6">
      <h3 class="text-lg font-semibold text-gray-800 mb-4">最近投递记录</h3>
      <el-table :data="stats.recent" border stripe>
        <el-table-column prop="userName" label="求职者" />
        <el-table-column prop="jobName" label="岗位名称" />
        <el-table-column
            label="岗位类型"
            :formatter="(row) => {
            const typeMap = {
              1: '实习',
              2: '全职'
            }
            return typeMap[row.jobType] || row.jobType
          }"
        />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request.js'
import { ElMessage } from 'element-plus'
import VChart from 'vue-echarts'

const stats = ref({
  total: 0,
  intern: 0,
  fulltime: 0,
  byJobName: [],
  recent: []
})

const pieChartOptions = computed(() => ({
  tooltip: {
    trigger: 'item',
    formatter: '{b}: {c} ({d}%)'
  },
  legend: {
    orient: 'vertical',
    left: 10,
    data: stats.value.byJobName.map(item => item.name)
  },
  series: [
    {
      name: '岗位投递数',
      type: 'pie',
      radius: '60%',
      center: ['50%', '50%'],
      data: stats.value.byJobName.map(item => ({
        value: item.count,
        name: item.name
      })),
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowColor: 'rgba(0, 0, 0, 0.5)'
        }
      }
    }
  ]
}))

const loadDashboardData = () => {
  request
      .get('/application/stats')
      .then(res => {
        if (res.code === '200') {
          stats.value = res.data
        } else {
          ElMessage.error(res.msg || '加载仪表盘数据失败')
        }
      })
      .catch(() => {
        ElMessage.error('加载仪表盘数据失败')
      })
}

onMounted(loadDashboardData)
</script>

<style scoped></style>
