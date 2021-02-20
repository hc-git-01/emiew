<template>
  <div class="em-bar">
    <transition name="em-bar-tools" appear>
      <div class="em-bar-tools">
        <em-bar-button icon="arrow-left" @click="$back" />
        <div class="em-bar-center">
          <slot />
        </div>
        <em-bar-button
          v-if="$slots.functions"
          class="functions-button"
          :class="{ active: functionsShow }"
          :icon="$slots['functions-button'] ? '' : 'ellipsis-h'"
          @click="functionsShow ^= true"
        >
          <slot name="functions-button" />
        </em-bar-button>
        <div v-else class="empty-right-button"></div>
      </div>
    </transition>

    <transition name="em-bar-functions">
      <div v-if="functionsShow" class="em-bar-functions">
        <slot name="functions" />
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: 'EmBar',
  mounted() {
    this.bindTouch()
  },
  data() {
    return {
      functionsShow: false,
    }
  },
  methods: {
    findParent(node, parentClass) {
      if (!node) {
        return
      }
      if (node.classList && node.classList.contains(parentClass)) {
        return node
      }
      return this.findParent(node.parentNode, parentClass)
    },
    bindTouch() {
      let main = this.findParent(this.$el, 'em-main')

      main.ontouchstart = (e) => {
        if (!this.findParent(e.target, 'em-bar')) {
          this.functionsShow = false
        }
      }
      main.onclick = (e) => {
        if (!this.findParent(e.target, 'functions-button')) {
          this.functionsShow = false
        }
      }
    },
  },
  watch: {
    $route() {
      this.functionsShow = false
    },
  },
}
</script>

<style>
.em-bar-tools {
  position: absolute;
  z-index: 12;
  left: 10px;
  right: 10px;
  bottom: 10px;
  height: 50px;
  display: flex;
  background-color: #fff;
  box-shadow: 0 1px 3px #0002;
  border-radius: 5px;
  overflow: hidden;
}

.em-bar-center {
  flex: 1;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-right-button {
  display: inline-block;
  width: 50px;
}

.em-bar-functions {
  position: fixed;
  z-index: 11;
  right: 10px;
  bottom: 70px;
  height: 50px;
  background-color: #fff;
  box-shadow: 0 1px 3px #0002;
  border-radius: 5px;
  overflow: hidden;
}

.em-bar-tools-enter-active {
  transition: transform 0.4s ease;
}

.em-bar-tools-enter {
  transform: translateY(70px);
}

.em-bar-functions-enter-active,
.em-bar-functions-leave-active {
  transition: transform 0.2s ease;
}

.em-bar-functions-enter,
.em-bar-functions-leave-to {
  transform: translate(calc(50% - 25px), 60px) scale(0);
}
</style>
