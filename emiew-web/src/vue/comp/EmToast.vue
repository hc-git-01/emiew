<template>
  <transition name="em-toast">
    <div v-show="counter > 0" class="em-toast" :class="type">
      {{ message }}
    </div>
  </transition>
</template>

<script>
import Vue from 'vue'

export default {
  name: 'EmToast',
  mounted() {
    Vue.prototype.$toast = (msg, type) => {
      this.counter++
      this.message = msg
      this.type = type || 'normal'
      setTimeout(() => {
        this.counter--
      }, 2000)
    }
  },
  data() {
    return {
      message: '',
      type: '',
      counter: 0,
    }
  },
  watch: {
    message() {
      this.$nextTick(() => {
        this.$el.style.left = 'unset'
        this.$nextTick(() => {
          let half = this.$el.clientWidth / 2 + 'px'
          this.$el.style.left = `calc(50vw - ${half})`
        })
      })
    },
  },
}
</script>

<style>
.em-toast {
  position: absolute;
  z-index: 200;
  bottom: 70px;
  border-radius: 10px;
  color: #fff;
  background-color: #6c6;
  box-shadow: 0 1px 3px #0002;
  overflow-wrap: break-word;
  padding: 10px;
  text-align: center;
}

.em-toast.warn {
  background-color: #f80;
}

.em-toast.error {
  background-color: #f66;
}

.em-toast-enter-active,
.em-toast-leave-active {
  transition: transform 0.2s ease, opacity 0.2s ease;
}

.em-toast-enter,
.em-toast-leave-to {
  transform: scale(0.5);
  opacity: 0;
}
</style>
