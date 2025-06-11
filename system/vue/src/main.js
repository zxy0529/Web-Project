import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import '@/assets/css/global.css';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import zhCn from 'element-plus/es/locale/lang/zh-cn';
import '@/assets/css/tailwind.css';
import VChart from 'vue-echarts';
import { use } from 'echarts/core';

// 按需引入 ECharts 模块（根据项目需要调整）
import { CanvasRenderer } from 'echarts/renderers';
import {
    PieChart,
    BarChart,
    LineChart,
    RadarChart
} from 'echarts/charts';
import {
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    GridComponent,
    ToolboxComponent
} from 'echarts/components';

// 注册 ECharts 核心模块
use([
    CanvasRenderer,
    // 图表类型
    PieChart,
    BarChart,
    LineChart,
    RadarChart,
    // 组件
    TitleComponent,
    TooltipComponent,
    LegendComponent,
    GridComponent,
    ToolboxComponent
]);

const app = createApp(App);

// 原有插件注册
app.use(router);
app.use(ElementPlus, { locale: zhCn });

// 全局注册 VChart 组件
app.component('VChart', VChart);

// 注册 Element Plus 图标
import * as ElementPlusIconsVue from '@element-plus/icons-vue';
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}

app.mount('#app');