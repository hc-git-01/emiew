<template>
  <div
    class="em-scroller"
    :class="{ gap: gap }"
    ref="scroller"
    @scroll="$emit('scroll', $event)"
  >
    <slot />
  </div>
</template>

<script>
export default {
  name: 'EmScroller',
  props: {
    gap: {
      default: false,
    },
  },
  activated() {
    this.$refs.scroller.scrollTop = this.scrollTop
  },
  deactivated() {
    this.scrollTop = this.$refs.scroller.scrollTop
  },
  data() {
    return {
      scrollTop: 0,
    }
  },
}
</script>

<style>
.em-scroller {
  position: relative;
  width: 100%;
  height: 100%;
  padding-bottom: 70px;
  overflow-x: hidden;
  overflow-y: auto;
}

.em-scroller.gap {
  padding: 10px 10px 70px 10px;
}
</style>
