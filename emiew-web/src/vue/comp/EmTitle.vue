<template>
  <div class="em-title">
    <fa-icon :name="icon" />
    <span>
      <slot />
    </span>
  </div>
</template>

<script>
export default {
  name: 'EmTitle',
  props: {
    icon: {
      type: String,
      default: 'info',
    },
  },
}
</script>

<style>
.em-title {
  display: flex;
  align-items: center;
  height: 30px;
  margin: 20px 0 10px 0;
  border-bottom: 0.5px solid #ddd;
}

.em-title:first-of-type {
  margin: 0 0 10px 0;
}

.em-title span {
  margin-left: 5px;
  font-weight: bold;
}
</style>
