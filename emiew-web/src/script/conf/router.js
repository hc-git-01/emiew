import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'Gallery',
    component: () => import('@/vue/page/Gallery'),
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/vue/page/Search'),
  },
  {
    path: '/book',
    name: 'Book',
    component: () => import('@/vue/page/Book'),
  },
  {
    path: '/read',
    name: 'Read',
    component: () => import('@/vue/page/Read'),
  },
  {
    path: '/download',
    name: 'Download',
    component: () => import('@/vue/page/Download'),
  },
  {
    path: '/config',
    name: 'Config',
    component: () => import('@/vue/page/Config'),
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/vue/page/Login'),
  },
  {
    path: '/block',
    name: 'Block',
    component: () => import('@/vue/page/Block'),
  },
  {
    path: '/proxy',
    name: 'Proxy',
    component: () => import('@/vue/page/Proxy'),
  },
]

const router = new VueRouter({
  routes,
})

export default router
