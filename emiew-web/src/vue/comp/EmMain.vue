<template>
  <div class="em-main">
    <slot />

    <transition name="loading">
      <div v-show="loading" class="loading">
        <fa-icon name="circle-notch" spin scale="1.5" />
      </div>
    </transition>
  </div>
</template>

<script>
export default {
  name: 'EmMain',
  props: {
    loading: {
      default: false,
    },
  },
}
</script>

<style>
.em-main {
  position: fixed;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  overflow: hidden;
  background-color: #fff;
  box-shadow: 0 1px 3px #0002;
}

.loading {
  position: absolute;
  z-index: 300;
  left: 0;
  right: 0;
  top: 10px;
  margin: auto;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #fff;
  border-radius: 20px;
  box-shadow: 0 1px 3px #0002;
}

.loading-enter-active,
.loading-leave-active {
  transition: transform 0.2s ease;
}

.loading-enter,
.loading-leave-to {
  transform: scale(0);
}
</style>
