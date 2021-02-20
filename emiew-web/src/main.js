import Vue from 'vue'
import App from '@/vue/App'
import router from '@/script/conf/router'
import VuePageStack from 'vue-page-stack'

Vue.use(VuePageStack, { router, keyName: 'p' })

import '@/script/conf/project'
import '@/script/conf/axios'
import '@/script/conf/regist'

import '@/style/common.css'

new Vue({
  router,
  render: (h) => h(App),
}).$mount('#app')
