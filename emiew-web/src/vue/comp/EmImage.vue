<template>
  <img v-if="loaded" class="em-image" :src="src" />
  <div v-else class="em-image-agent" @click.stop="failed = false">
    <img
      v-if="!failed"
      v-show="false"
      :src="src"
      @load="loaded = true"
      @error="failed = true"
    />
    <img v-if="failed" :src="error" :style="agentStyle" />
    <img v-else :src="loading" :style="agentStyle" />
  </div>
</template>

<script>
export default {
  name: 'EmImage',
  props: ['src'],
  mounted() {
    this.agentStyle = {
      maxWidth: this.$el.parentNode.clientWidth + 'px',
      maxHeight: this.$el.parentNode.clientHeight + 'px',
    }
  },
  data() {
    const loading = require('@/image/image.png')
    const error = require('@/image/image-broken.png')

    return {
      loading: loading,
      error: error,

      loaded: false,
      failed: false,

      agentStyle: {},

      maxWidth: 0,
      maxHeight: 0,
    }
  },
}
</script>

<style>
.em-image-agent {
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
</style>
