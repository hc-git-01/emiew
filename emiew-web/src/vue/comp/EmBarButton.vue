<template>
  <transition name="em-bar-button">
    <button class="em-bar-button" @click="$emit('click')">
      <fa-icon v-if="icon" :name="icon" scale="1.5" />
      <slot />
    </button>
  </transition>
</template>

<script>
export default {
  name: 'EmBarButton',
  props: {
    icon: {
      type: String,
    },
  },
}
</script>

<style>
.em-bar-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  flex-flow: wrap;
  width: 50px;
  height: 100%;
  border: none;
  border-radius: 0;
  background-color: #fff;
  padding: 0;
  vertical-align: top;
  transition: background-color 0.1s ease;
  -webkit-user-select: none;
  -moz-user-select: none;
}

.em-bar-button:active {
  background-color: #ddd;
}

.em-bar-button-enter-active,
.em-bar-button-leave-active {
  transition: transform 0.2s ease, width 0.2s ease;
}

.em-bar-button-enter,
.em-bar-button-leave-to {
  transform: scale(0);
  width: 0;
}
</style>
