<template>
  <transition name="em-modal-cover">
    <div v-show="show" class="em-modal-cover">
      <transition name="em-modal-panel">
        <div v-show="show" class="em-modal-panel">
          <div class="em-modal-content">
            <slot />
          </div>
          <div class="em-modal-buttons" v-if="$slots.buttons">
            <slot name="buttons" />
          </div>
        </div>
      </transition>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'EmModal',
  props: {
    show: {
      default: false,
    },
  },
}
</script>

<style>
.em-modal-cover {
  position: absolute;
  z-index: 100;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  background-color: #0004;
}

.em-modal-panel {
  position: absolute;
  left: 10px;
  right: 10px;
  bottom: 70px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px #0002;
}

.em-modal-content {
  padding: 20px 10px 10px 10px;
}

.em-modal-buttons {
  text-align: right;
  padding: 10px;
}

.em-modal-buttons .em-button {
  margin-left: 10px;
}

.em-modal-cover-enter-active,
.em-modal-cover-leave-active {
  transition: opacity 0.2s ease;
}

.em-modal-cover-enter,
.em-modal-cover-leave-to {
  opacity: 0;
}

.em-modal-panel-enter-active,
.em-modal-panel-leave-active {
  transition: transform 0.2s ease;
}

.em-modal-panel-enter,
.em-modal-panel-leave-to {
  transform: scale(0.5);
}
</style>
