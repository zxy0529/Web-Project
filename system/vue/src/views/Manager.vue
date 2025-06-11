<template>
  <div class="min-h-screen bg-gradient-to-b from-blue-100 to-blue-300 flex flex-col font-sans">
    <!-- Header -->
    <header class="flex items-center justify-between px-6 py-4 bg-gradient-to-r from-blue-200 to-blue-400 shadow-lg rounded-b-xl">
      <div class="flex items-center space-x-4">
        <img src="@/assets/imgs/logo.png" alt="logo" class="w-12 h-12 rounded-full shadow-md bg-white p-2" />
        <h1 class="text-white text-3xl font-semibold tracking-wide">校园招聘系统</h1>
      </div>
      <el-breadcrumb separator="/" class="text-white text-sm">
        <el-breadcrumb-item :to="{ path: '/manager/home' }">首页</el-breadcrumb-item>
        <el-breadcrumb-item>{{ router.currentRoute.value.meta.name }}</el-breadcrumb-item>
      </el-breadcrumb>
      <el-dropdown>
        <div class="flex items-center space-x-2 cursor-pointer pr-2 text-white">
          <img :src="data.user.avatar" alt="avatar" class="w-10 h-10 rounded-full border-2 border-white shadow-lg" />
          <span class="font-medium text-lg">{{ data.user.name }}</span>
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
      <aside class="w-64 bg-blue-200 shadow-xl rounded-tr-2xl rounded-br-2xl py-6 px-4">
        <el-menu
            :default-active="router.currentRoute.value.path"
            :default-openeds="['1', '2', '3']"
            router
            class="border-none space-y-2"
        >
          <el-menu-item index="/manager/dashboard" v-if="data.user.role === 'COMPANY_USER'">
            <el-icon><HomeFilled /></el-icon>
            <span class="font-semibold">系统首页</span>
          </el-menu-item>
          <el-menu-item index="/manager/home" v-if="data.user.role === 'USER'">
            <el-icon><HomeFilled /></el-icon>
            <span class="font-semibold">系统首页</span>
          </el-menu-item>
          <el-menu-item index="/manager/notice" v-if="data.user.role === 'ADMIN'">
            <el-icon><HomeFilled /></el-icon>
            <span class="font-semibold">系统首页</span>
          </el-menu-item>
          <el-menu-item index="/manager/application" v-if="data.user.role === 'USER'">
            <el-icon><Notification /></el-icon>
            <span class="font-semibold">浏览职位</span>
          </el-menu-item>
          <el-menu-item index="/manager/applicationInfo" v-if="data.user.role === 'USER' || data.user.role === 'COMPANY_USER'">
            <el-icon><Document /></el-icon>
            <span class="font-semibold">投递信息</span>
          </el-menu-item>

          <el-sub-menu index="1" v-if="data.user.role === 'COMPANY_USER'">
            <template #title>
              <el-icon><Menu /></el-icon>
              <span class="font-semibold">招聘管理</span>
            </template>
            <el-menu-item index="/manager/jobs">职位管理</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="2" v-if="data.user.role === 'ADMIN'">
            <template #title>
              <el-icon><Menu /></el-icon>
              <span class="font-semibold">用户管理</span>
            </template>
            <el-menu-item index="/manager/admin">管理员信息</el-menu-item>
            <el-menu-item index="/manager/user">用户信息</el-menu-item>
            <el-menu-item index="/manager/publisher">企业用户信息</el-menu-item>
          </el-sub-menu>

          <el-sub-menu index="3" v-if="data.user.role === 'ADMIN'">
            <template #title>
              <el-icon><Grid /></el-icon>
              <span class="font-semibold">公司管理</span>
            </template>
            <el-menu-item index="/manager/company">公司信息</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </aside>

      <!-- Main Content -->
      <main class="flex-1 p-6">
        <div class="bg-white rounded-xl shadow-xl p-6 min-h-[80vh]">
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
/* Header 背景渐变 */
header {
  background: linear-gradient(45deg, #A5C7F1, #74A8FF);
}

/* Sidebar 背景和字体颜色 */
aside {
  background-color: #A5C7F1;
}

/* Sidebar 和菜单项 */
.el-menu-item,
.el-sub-menu > .el-menu-item {
  padding-left: 2rem;
}

/* 提升菜单项的字体粗细 */
.el-menu-item .el-icon,
.el-sub-menu > .el-menu-item .el-icon {
  font-size: 20px;
}

/* 菜单按钮的悬浮效果 */
.el-menu-item:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

/* 页面主体的阴影和圆角 */
.main-content {
  background: white;
  border-radius: 1rem;
  box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1);
}

/* 字体调整 */
h1, span {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}
</style>
