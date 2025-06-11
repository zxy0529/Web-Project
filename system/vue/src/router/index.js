import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/manager/home' },
    {
      path: '/manager',
      component: () => import('@/views/Manager.vue'),
      children: [
        { path: 'home', meta: { name: '系统首页' }, component: () => import('@/views/manager/Home.vue'),  },
        { path: 'admin', meta: { name: '管理员信息' }, component: () => import('@/views/manager/Admin.vue'), },
        { path: 'person', meta: { name: '个人资料' }, component: () => import('@/views/manager/Person.vue'), },
        { path: 'password', meta: { name: '修改密码' }, component: () => import('@/views/manager/Password.vue'), },
        { path: 'user', meta: { name: '用户信息' }, component: () => import('@/views/manager/User.vue'), },
        { path: 'publisher', meta: { name: '企业用户信息' }, component: () => import('@/views/manager/Publisher.vue'), },
        { path: 'company', meta: { name: '公司信息' }, component: () => import('@/views/manager/Company.vue'), },
        { path: 'jobs', meta: { name: '职位信息' }, component: () => import('@/views/manager/Jobs.vue'), },
        { path: 'application', meta: { name: '投递职位' }, component: () => import('@/views/manager/Application.vue'), },
        { path: 'applicationInfo', meta: { name: '投递信息' }, component: () => import('@/views/manager/ApplicationInfo.vue'), },
        { path: 'dashboard', meta: { name: '仪表盘' }, component: () => import('@/views/manager/dashboard.vue'), }
      ]
    },
    { path: '/login', component: () => import('@/views/Login.vue') },
    { path: '/register', component: () => import('@/views/Register.vue') },
    { path: '/registerPublisher', component: () => import('@/views/RegisterPublisher.vue') },
    { path: '/404', component: () => import('@/views/404.vue') },
    { path: '/:pathMatch(.*)', redirect: '/404' }
  ]
})

export default router
