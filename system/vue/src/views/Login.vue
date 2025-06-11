<template>
  <div class="min-h-screen flex items-center justify-center bg-gradient-to-t from-[#7f7fd5] via-[#86a8e7] to-[#91eae4]">
    <div class="w-full max-w-sm bg-white/70 rounded-2xl shadow-xl p-8">
      <h2 class="text-2xl font-bold text-center text-[#1450aa] mb-8">欢迎使用校园招聘系统</h2>

      <el-form ref="formRef" :model="data.form" :rules="data.rules">
        <el-form-item prop="username">
          <el-input
              :prefix-icon="User"
              size="large"
              v-model="data.form.username"
              placeholder="请输入账号"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              show-password
              :prefix-icon="Lock"
              size="large"
              v-model="data.form.password"
              placeholder="请输入密码"
          />
        </el-form-item>

        <el-form-item prop="role">
          <el-select size="large" v-model="data.form.role" class="w-full">
            <el-option value="ADMIN" label="管理员" />
            <el-option value="USER" label="用户" />
            <el-option value="COMPANY_USER" label="企业用户" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" class="w-full" @click="login">登 录</el-button>
        </el-form-item>

        <div class="text-right text-sm text-gray-600">
          <a href="/register" class="hover:underline hover:text-blue-700">注册</a>
        </div>
        <div class="text-right text-sm text-gray-600">
          <a href="/registerPublisher" class="hover:underline hover:text-blue-700">企业用户注册</a>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import {reactive, ref} from "vue";
import {User, Lock} from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";
import router from "@/router/index.js";

const data = reactive({
  form: {role: "ADMIN"},
  rules: {
    username: [{required: true, message: "请输入账号", trigger: "blur"}],
    password: [{required: true, message: "请输入密码", trigger: "blur"}]
  }
});

const formRef = ref();

const login = () => {
  formRef.value.validate(valid => {
    if (valid) {
      request.post("/login", data.form).then(res => {
        if (res.code === "200") {
          ElMessage.success("登录成功");
          localStorage.setItem("current-user-role", JSON.stringify(res.data));
          const userRole = res.data.role;

          if (userRole === "COMPANY_USER") {
            // 企业用户跳转到企业首页
            router.push("/manager/dashboard");
          } else if (userRole === "ADMIN") {
            // 管理员跳转到管理员首页
            router.push("/manager/notice");
          } else {
            router.push("/manager/home");
          }

        } else {
          ElMessage.error(res.msg);
        }
      });
    }
  });
};
</script>