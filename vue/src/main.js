import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import * as echarts from 'echarts'

import '@/assets/css/global.css'

// import locale from 'element-ui/lib/locale/lang/en'

Vue.config.productionTip = false

/*** createApp(App).use(store).use(router).use(ElementUI).mountpath("#app")
      3.X格式
 */

Vue.use(ElementUI
    , {
    // locale,       //国际化组件引入
      size: 'small'
    }
)

new Vue({
  router,
  store,
  ElementUI,
  render: h => h(App)
}).$mount('#app')

Vue.prototype.$echarts = echarts