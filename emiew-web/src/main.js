import Vue from 'vue'
import App from '@/vue/App'
import router from '@/script/conf/router'
import VuePageStack from 'vue-page-stack'
import VueLazyload from 'vue-lazyload'

Vue.use(VuePageStack, { router, keyName: 'p' })
Vue.use(VueLazyload, {
  error: require('@/image/image-broken.png'),
  loading: require('@/image/image.png'),
})

import '@/script/conf/project'
import '@/script/conf/axios'
import '@/script/conf/regist'

import '@/style/common.css'

// Vue.config.productionTip = false

new Vue({
  router,
  render: (h) => h(App),
}).$mount('#app')
