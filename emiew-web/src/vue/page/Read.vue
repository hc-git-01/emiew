<template>
  <em-main>
    <div
      ref="scroller"
      id="images"
      :class="viewMode"
      v-if="pages"
      @scroll="handleImagesScroll"
    >
      <div
        ref="image"
        :class="{ animated: imageLastTouchX === -1 }"
        v-for="i of pages"
        :key="i"
        @touchstart="handleImageTouchStart"
        @touchmove="handleImageTouchMove"
        @touchend="handleImageTouchEnd"
      >
        <img v-lazy="buildImageUrl(i - 1)" />
      </div>
    </div>

    <em-bar>
      <template slot="default">
        <div id="slider" ref="slider">
          <transition name="track">
            <div id="track" v-show="viewMode === 'horizontal'"></div>
          </transition>
          <transition name="handle">
            <div
              v-show="viewMode === 'horizontal'"
              ref="handle"
              id="handle"
              :class="{ animated: !handling }"
              @touchstart="handleSliderTouchStart"
              @touchmove="handleSliderTouchMove"
              @touchend="handleSliderTouchEnd"
            ></div>
          </transition>
        </div>
      </template>

      <template id="page" slot="functions-button" @click="handleViewModeClick">
        <div id="index">
          {{ index + 1 }}
        </div>
        <div id="pages">{{ pages || '-' }}</div>
      </template>

      <template slot="functions">
        <em-bar-button :icon="modeIcon" @click="handleViewModeClick" />
      </template>
    </em-bar>
  </em-main>
</template>

<script>
export default {
  name: 'Read',
  mounted() {
    if (!this.$route.query.url || !this.$route.query.pages) {
      this.$push({ name: 'Gallery' })
    }
    this.readViewMode()
  },
  data() {
    return {
      url: this.$route.query.url,
      pages: this.$route.query.pages ? parseInt(this.$route.query.pages) : 0,
      imagePrefix: `${this.$project.api}image/by-index?url=`,

      index: 0,

      imageRef: '',
      imageLastTouchX: -1,
      imageMarginLeft: 0,
      imageLastMove: 0,

      sliderRef: '',
      handleRef: '',
      handleLeft: 0,
      handling: false,

      viewMode: 'horizontal',
    }
  },
  methods: {
    handleViewModeClick() {
      this.loadRefs()
      if (this.viewMode === 'horizontal') {
        this.viewMode = 'vertical'
        this.imageMarginLeft = 0
        this.$nextTick(() => {
          this.finishVerticalScrollerPosition()
        })
      } else {
        this.viewMode = 'horizontal'
        this.imageLastTouchX = 0
        this.handling = true
        this.$nextTick(() => {
          this.finishImagePosition()
          this.finishSliderPosition()
        })
      }
      this.saveViewMode()
    },
    handleImageTouchStart(e) {
      if (this.viewMode !== 'horizontal') {
        return
      }

      this.loadRefs()
      this.handling = false
      this.imageLastTouchX = e.touches[0].clientX
    },
    handleImageTouchMove(e) {
      if (this.viewMode !== 'horizontal') {
        return
      }

      let move = e.touches[0].clientX - this.imageLastTouchX
      this.imageMarginLeft += move
      this.imageLastMove = move
      this.imageLastTouchX = e.touches[0].clientX
    },
    handleImageTouchEnd() {
      if (this.viewMode !== 'horizontal') {
        return
      }

      this.imageLastTouchX = -1

      let index = Math.floor(-this.imageMarginLeft / this.imageRef.clientWidth)
      if (this.imageLastMove < 0) {
        index += 1
      }
      if (index < 0) {
        index = 0
      } else if (index >= this.pages) {
        index = this.pages - 1
      }

      this.index = index

      this.finishImagePosition()
      this.finishSliderPosition()
    },
    finishImagePosition() {
      this.imageMarginLeft = -(this.imageRef.clientWidth * this.index)
    },
    handleSliderTouchStart() {
      this.loadRefs()
      this.imageLastTouchX = -1
      this.handling = true
    },
    handleSliderTouchMove(e) {
      let left = e.touches[0].clientX - 85
      if (left < 0) {
        left = 0
      } else if (left > this.sliderRef.clientWidth - 30) {
        left = this.sliderRef.clientWidth - 30
      }
      this.handleLeft = left

      let step = (this.sliderRef.clientWidth - 30) / (this.pages - 1)
      let index = Math.round(this.handleLeft / step)
      this.index = index
    },
    handleSliderTouchEnd() {
      this.handling = false
      this.finishSliderPosition()
      this.finishImagePosition()
    },
    finishSliderPosition() {
      let step = (this.sliderRef.clientWidth - 30) / (this.pages - 1)
      let left = step * this.index
      this.handleLeft = left
    },
    handleImagesScroll(e) {
      if (this.viewMode !== 'vertical') {
        return
      }

      this.loadRefs()

      let scrollTop = e.target.scrollTop
      let imagesRef = this.$refs.image
      let totalHeight = 0

      for (let i = 0; i < imagesRef.length; i++) {
        totalHeight += imagesRef[i].clientHeight

        if (totalHeight > scrollTop) {
          this.index = i
          return
        }
      }
    },
    finishVerticalScrollerPosition() {
      let imagesRef = this.$refs.image
      let scrollTop = 0
      for (let i = 0; i < this.index; i++) {
        scrollTop += imagesRef[i].clientHeight
      }
      this.$refs.scroller.scrollTop = scrollTop
    },
    loadRefs() {
      if (!this.imageRef && this.$refs.image) {
        this.imageRef = this.$refs.image[0]
      }
      if (!this.handleRef) {
        this.handleRef = this.$refs.handle
      }
      if (!this.sliderRef) {
        this.sliderRef = this.$refs.slider
      }
    },
    readViewMode() {
      this.viewMode = localStorage.getItem('view-mode') || 'horizontal'
    },
    saveViewMode() {
      localStorage.setItem('view-mode', this.viewMode)
    },
    buildImageUrl(index) {
      return `${this.$project.api}image/by-index?url=${this.url}&index=${index}`
    },
  },
  computed: {
    modeIcon() {
      return {
        vertical: 'arrows-alt-v',
        horizontal: 'arrows-alt-h',
      }[this.viewMode]
    },
  },
  watch: {
    imageMarginLeft(next) {
      this.imageRef.style.marginLeft = next + 'px'
    },
    handleLeft(next) {
      this.handleRef.style.left = next + 'px'
    },
  },
}
</script>

<style scoped>
#images.horizontal {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 70px;
  overflow: hidden;
  white-space: nowrap;
}

#images.horizontal > div {
  width: 100%;
  height: 100%;
  vertical-align: top;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

#images.horizontal > div.animated {
  transition: margin-left 0.2s ease;
}

#images.horizontal > div img {
  max-width: 100%;
  max-height: 100%;
}

#images.vertical {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
  padding-bottom: 70px;
  font-size: 0;
  line-height: 0;
  overflow-x: hidden;
  overflow-y: auto;
}

#images.vertical > div {
  width: 100%;
}

#images.vertical > div img {
  max-width: 100%;
}

#slider {
  flex: 1;
  margin: 0 10px;
  padding: 10px;
  height: 100%;
  position: relative;
}

#track {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  margin: auto;
  width: calc(100% - 30px);
  height: 5px;
  background-color: #ddd;
  border-radius: 2.5px;
}

#handle {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  margin: auto;
  width: 30px;
  height: 30px;
  border-radius: 15px;
  background-color: #fff;
  border: 1px solid #0af;
  box-shadow: 0 1px 3px rgba(0, 50, 100, 0.2);
}

#handle.animated {
  transition: left 0.2s ease;
}

#index {
  min-width: 40px;
  height: 25px;
  line-height: 25px;
  font-size: 14px;
  border-bottom: 1px solid #567;
  text-align: center;
}

.active #index {
  border-bottom: 1px solid #0af;
}

#pages {
  min-width: 40px;
  height: 25px;
  line-height: 25px;
  font-size: 14px;
  text-align: center;
}

.track-enter-active,
.track-leave-active {
  transition: transform 0.2s ease;
}

.track-enter,
.track-leave-to {
  transform: scaleY(0);
}

.handle-enter-active,
.handle-leave-active {
  transition: transform 0.2s ease;
}

.handle-enter,
.handle-leave-to {
  transform: scale(0);
}
</style>
