<template>
  <button class="em-button" :class="type" @click="$emit('click')">
    <slot />
  </button>
</template>

<script>
export default {
  name: 'EmButton',
  props: {
    type: {
      type: String,
      default: 'blue',
    },
  },
}
</script>

<style>
.em-button {
  display: inline-flex;
  align-items: center;
  height: 40px;
  border: none;
  border-radius: 5px;
  background-color: none;
  color: #fff;
  fill: #fff;
  padding: 0 15px;
  transition: background-color 0.1s ease;
  vertical-align: top;
  -webkit-user-select: none;
  -moz-user-select: none;
}

.em-button.blue {
  background-color: #0af;
}

.em-button.blue:active {
  background-color: #08d;
}

.em-button.orange {
  background-color: #f80;
}

.em-button.orange:active {
  background-color: #d60;
}

.em-button.red {
  background-color: #f66;
}

.em-button.red:active {
  background-color: #d44;
}

.em-button.gray {
  color: #234;
  fill: #234;
  background-color: #eee;
}

.em-button.gray:active {
  background-color: #ccc;
}

.em-button.github {
  background-color: #24292e;
}

.em-button.github:active {
  background-color: #02070c;
}
</style>
