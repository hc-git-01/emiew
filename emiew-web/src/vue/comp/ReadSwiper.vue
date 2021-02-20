<template>
  <div
    class="read-swiper"
    :class="[{ animated: !touching }, mode]"
    @touchstart="handleTouchStart"
    @touchmove="handleTouchMove"
    @touchend="handleTouchEnd"
    @scroll="handleScroll"
  >
    <div
      class="read-image-box"
      :class="[{ animated: !touching }, mode]"
      v-for="(url, index) of urls"
      :key="url"
    >
      <em-image
        v-if="index < value + 4 && index > value - 2"
        :class="mode"
        :src="url"
      />
    </div>
  </div>
</template>

<script>
export default {
  name: 'ReadSwiper',
  props: {
    urls: {
      type: Array,
      default: [],
    },
    value: {
      type: Number,
      default: 0,
    },
    mode: {
      type: String,
      default: 'h',
    },
  },
  mounted() {
    this.$nextTick(this.setStaticValue)
  },
  data() {
    return {
      boxWidth: 0,

      lastX: -1,
      lastLeft: 0,
      lastMove: 0,

      touching: false,
    }
  },
  methods: {
    handleTouchStart(e) {
      if (this.mode !== 'h') {
        return
      }

      this.lastX = e.touches[0].clientX
    },
    handleTouchMove(e) {
      if (this.mode !== 'h') {
        return
      }

      this.touching = true

      let x = e.touches[0].clientX
      let move = x - this.lastX
      let left = this.lastLeft + move

      this.$el.style.marginLeft = left + 'px'

      this.lastX = x
      this.lastLeft = left
      this.lastMove = move
    },
    handleTouchEnd() {
      if (this.mode !== 'h') {
        return
      }

      this.touching = false

      let index = Math.floor(-this.lastLeft / this.boxWidth)
      if (this.lastMove < 0) {
        index++
      }
      if (index < 0) {
        index = 0
      } else if (index >= this.urls.length) {
        index = this.urls.length - 1
      }
      this.$emit('input', index)
      this.$emit('finish')
      this.$nextTick(this.swipeByIndex)
    },
    handleScroll(e) {
      if (this.mode !== 'v') {
        return
      }

      let top = e.target.scrollTop
      let sum = 0
      let index = 0
      for (let i = 0; i < this.urls.length; i++) {
        let height = this.$el.children[i].getBoundingClientRect().height
        sum += height

        if (sum > top) {
          index = i
          break
        }
      }

      this.$emit('input', index)
      this.$emit('finish')
      this.$nextTick(this.swipeByIndex)
    },
    swipeByIndex() {
      if (this.mode !== 'h') {
        return
      }

      let left = -this.boxWidth * this.value
      this.$el.style.marginLeft = left + 'px'

      this.lastLeft = left
    },
    scrollByIndex() {
      if (this.mode !== 'v') {
        return
      }

      let scrollTop = 0
      for (let i = 0; i < this.value; i++) {
        scrollTop += this.$el.children[i].getBoundingClientRect().height
      }
      this.$el.scrollTop = scrollTop + 1
    },
    setStaticValue() {
      this.boxWidth = this.$el.children[0].getBoundingClientRect().width
    },
  },
  watch: {
    mode(next) {
      switch (next) {
        case 'h':
          this.touching = true
          this.$nextTick(this.swipeByIndex)
          break
        case 'v':
          this.$el.style.marginLeft = 0
          this.$nextTick(this.scrollByIndex)
          break
      }
    },
    value() {
      this.$nextTick(this.swipeByIndex)
    },
  },
}
</script>

<style>
.read-swiper.h {
  width: 100%;
  height: calc(100% - 70px);
  white-space: nowrap;
  overflow: visible;
}

.read-swiper.h.animated {
  transition: margin-left 0.2s ease;
}

.read-swiper.v {
  width: 100%;
  height: 100%;
  padding-bottom: 70px;
  overflow-x: hidden;
  overflow-y: auto;
}

.read-image-box.h {
  width: 100%;
  height: 100%;
  vertical-align: top;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.read-image-box.v {
  width: 100%;
  min-height: 35%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0;
}

.em-image.h {
  max-width: 100%;
  max-height: 100%;
}

.em-image.v {
  width: 100%;
}
</style>
