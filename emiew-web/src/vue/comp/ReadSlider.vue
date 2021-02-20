<template>
  <transition name="read-slider">
    <div class="read-slider" v-if="total > 1">
      <div class="read-slider-track"></div>

      <div
        ref="handle"
        class="read-slider-handle"
        :class="{ animated: !touching }"
        @touchmove="handleTouchMove"
        @touchend="handleTouchEnd"
      ></div>
    </div>
  </transition>
</template>

<script>
export default {
  name: 'ReadSlider',
  props: {
    value: {
      type: Number,
      default: 0,
    },
    total: {
      type: Number,
      default: 0,
    },
  },
  mounted() {
    this.$nextTick(this.setStaticValue)
  },
  data() {
    return {
      sliderWidth: 0,
      sliderInterval: 0,

      touching: false,
    }
  },
  methods: {
    handleTouchMove(e) {
      this.touching = true

      // set handle position
      let x = e.touches[0].clientX - 85
      if (x < 0) {
        x = 0
      } else if (x > this.sliderWidth - 30) {
        x = this.sliderWidth - 30
      }
      this.$refs.handle.style.left = x + 'px'

      // emit final value
      this.$emit('input', Math.round(x / this.sliderInterval))
    },
    handleTouchEnd() {
      this.touching = false
      this.slideByIndex()
      this.$emit('finish')
    },
    slideByIndex() {
      let left = this.value * this.sliderInterval
      this.$refs.handle.style.left = left + 'px'
    },
    setStaticValue() {
      if (this.total <= 1) {
        return
      }
      this.sliderWidth = this.$el.getBoundingClientRect().width
      this.sliderInterval = (this.sliderWidth - 30) / (this.total - 1)
    },
  },
  watch: {
    value() {
      this.$nextTick(this.slideByIndex)
    },
  },
}
</script>

<style>
.read-slider {
  display: inline-block;
  position: relative;
  margin: 0 10px;
  width: 100%;
  height: 100%;
}

.read-slider-track {
  position: absolute;
  left: 15px;
  right: 15px;
  top: 0;
  bottom: 0;
  margin: auto;
  height: 5px;
  border-radius: 2.5px;
  background-color: #ddd;
}

.read-slider-handle {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  margin: auto;
  width: 30px;
  height: 30px;
  border-radius: 15px;
  background-color: #fff;
  border: 1px solid #0af;
  box-shadow: 0 1px 3px #0262;
}

.read-slider-handle.animated {
  transition: left 0.2s ease;
}

.read-slider-enter-active,
.read-slider-leave-active {
  transition: opacity 0.2s ease;
}

.read-slider-enter,
.read-slider-leave-to {
  opacity: 0;
}
</style>
