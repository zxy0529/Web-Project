<template>
  <div class="min-h-screen bg-blue-300 flex flex-col font-sans">
    <!-- Header -->
    <header class="flex items-center justify-between px-6 py-4 bg-gradient-to-r from-blue-200 to-blue-300 shadow-md">
      <div class="flex items-center space-x-4">
        <img src="@/assets/imgs/logo.png" alt="logo" class="w-10 h-10 rounded-full shadow-md bg-white p-1" />
        <h1 class="text-white text-2xl font-bold tracking-wide">校园招聘系统</h1>
      </div>
      <el-breadcrumb separator="/" class="text-white text-sm">
        <el-breadcrumb-item :to="{ path: '/manager/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>{{ router.currentRoute.value.meta.name }}</el-breadcrumb-item>
      </el-breadcrumb>
      <el-dropdown>
        <div class="flex items-center space-x-2 cursor-pointer pr-2 text-white">
          <img :src="data.user.avatar" alt="avatar" class="w-10 h-10 rounded-full border-2 border-white shadow" />
          <span class="font-medium">{{ data.user.name }}</span>
          <el-icon><arrow-down /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item @click="router.push('/manager/person')">个人资料</el-dropdown-item>
            <el-dropdown-item @click="router.push('/manager/password')">修改密码</el-dropdown-item>
            <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </header>

    <!-- Main Layout -->
    <div class="flex flex-1">
      <!-- Sidebar -->
      <aside class="w-64 bg-blue-200 shadow-md rounded-tr-2xl rounded-br-2xl py-6 px-2">
        <el-menu
            :default-active="router.currentRoute.value.path"
            :default-openeds="['1', '2', '3']"
            router
            class="border-none"
        >
          <el-menu-item index="/manager/dashboard" v-if="data.user.role === 'COMPANY_USER'">
            <el-icon><HomeFilled /></el-icon>
            <span>系统首页</span>
          </el-menu-item>
          <el-menu-item index="/manager/home" v-if="data.user.role === 'USER'">
            <el-icon><HomeFilled /></el-icon>
            <span>系统首页</span>
          </el-menu-item>
          <el-menu-item index="/manager/application" v-if="data.user.role === 'USER'">
            <el-icon><Notification /></el-icon>
            <span>浏览职位</span>
          </el-menu-item>
          <el-menu-item index="/manager/applicationInfo" v-if="data.user.role === 'USER'|| data.user.role === 'COMPANY_USER'">
            <el-icon><Document /></el-icon>
            <span>投递信息</span>
          </el-menu-item>

          <el-sub-menu index="1" v-if="data.user.role === 'COMPANY_USER'">
            <template #title>
              <el-icon><Menu /></el-icon>
              <span>招聘管理</span>
            </template>
            <el-menu-item index="/manager/jobs">职位管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="2" v-if="data.user.role === 'ADMIN'">
            <template #title>
              <el-icon><Menu /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/manager/admin">管理员信息</el-menu-item>
            <el-menu-item index="/manager/user">用户信息</el-menu-item>
            <el-menu-item index="/manager/publisher">企业用户信息</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="3" v-if="data.user.role === 'ADMIN'">
            <template #title>
              <el-icon><Grid /></el-icon>
              <span>公司管理</span>
            </template>
            <el-menu-item index="/manager/company">公司信息</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </aside>

      <!-- Main Content -->
      <main class="flex-1 p-6">
        <div class="bg-white rounded-xl shadow-lg p-6 min-h-[80vh]">
          <RouterView @updateUser="updateUser" />
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import router from "@/router/index.js";
import { ElMessage } from "element-plus";

const data = reactive({
  user: JSON.parse(localStorage.getItem('current-user-role') || '{}')
})

const logout = () => {
  localStorage.removeItem('current-user-role')
  router.push('/login')
}

const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem('current-user-role') || '{}')
}

if (!data.user.id) {
  logout()
  ElMessage.error('请登录！')
}
</script>

<style scoped>
/* 可加入字体或渐变背景支持 */
</style>
