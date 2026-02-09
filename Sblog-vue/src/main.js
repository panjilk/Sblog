import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import '@/assets/styles/dark-mode.css'
import { setupLazyLoadDirective } from '@/directives/lazyLoad'

const app = createApp(App)

app.use(router)
app.use(ElementPlus)

// 注册懒加载指令
setupLazyLoadDirective(app)

app.mount('#app')
