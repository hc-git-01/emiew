<template>
  <div id="app">
    <em-toast />

    <transition :name="transition" @after-enter="handleAfterEnter">
      <vue-page-stack>
        <router-view :key="$route.fullPath"/>
      </vue-page-stack>
    </transition>
  </div>
</template>

<script>
import Vue from 'vue'

export default {
  name: 'App',
  mounted() {
    this.listenTransition()
  },
  data() {
    return {
      transition: '',
      isRouterChange: false,
    }
  },
  methods: {
    handleAfterEnter() {
      this.transition = ''
    },
    listenTransition() {
      window.onpopstate = (e) => {
        if (!this.isRouterChange) {
          document.styleSheets[0].disabled = true
        }
        this.isRouterChange = false
      }

      Vue.prototype.$push = (location) => {
        document.styleSheets[0].disabled = false
        this.transition = 'forward'
        this.isRouterChange = true
        this.$router.push(location)
      }

      Vue.prototype.$back = () => {
        document.styleSheets[0].disabled = false
        this.transition = 'back'
        this.isRouterChange = true
        this.$router.back()
      }
    },
  },
}
</script>

<style scoped>
.forward-enter-active,
.forward-leave-active,
.back-enter-active,
.back-leave-active {
  transition: filter 0.4s ease, transform 0.4s ease;
}

.back-leave-active {
  z-index: 1000;
}

.forward-enter,
.back-leave-to {
  transform: translateX(100%);
}

.forward-leave-to,
.back-enter {
  filter: brightness(0.6);
}

.forward-enter-active .tool-bar,
.forward-leave-active .tool-bar,
.back-enter-active .tool-bar,
.back-leave-active .tool-bar {
  transition: transform 0.4s ease;
}

.forward-leave-to .tool-bar,
.back-enter .tool-bar,
.forward-enter .tool-bar,
.back-leave-to .tool-bar {
  transform: translateY(70px);
}
</style>
