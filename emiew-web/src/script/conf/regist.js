import Vue from 'vue'
import 'vue-awesome/icons'

Vue.component('fa-icon', () => import('vue-awesome/components/Icon'))

Vue.component('em-button', () => import('@/vue/comp/EmButton'))
Vue.component('em-input', () => import('@/vue/comp/EmInput'))
Vue.component('em-tag', () => import('@/vue/comp/EmTag'))
Vue.component('em-title', () => import('@/vue/comp/EmTitle'))
Vue.component('em-modal', () => import('@/vue/comp/EmModal'))
Vue.component('em-toast', () => import('@/vue/comp/EmToast'))
Vue.component('em-bar', () => import('@/vue/comp/EmBar'))
Vue.component('em-bar-button', () => import('@/vue/comp/EmBarButton'))
Vue.component('em-main', () => import('@/vue/comp/EmMain'))
Vue.component('em-scroller', () => import('@/vue/comp/EmScroller'))