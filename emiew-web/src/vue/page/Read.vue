<template>
  <em-main>
    <read-swiper
      ref="swiper"
      v-model="swiperIndex"
      :urls="imageUrls"
      :mode="viewMode"
      @finish="sliderIndex = swiperIndex"
    />

    <em-bar>
      <template slot="default">
        <read-slider
          v-show="viewMode === 'h'"
          ref="slider"
          v-model="sliderIndex"
          :total="pages"
          @finish="swiperIndex = sliderIndex"
        />
      </template>

      <template id="page" slot="functions-button">
        <div id="index">
          {{ sliderIndex + 1 }}
        </div>
        <div id="pages">{{ pages || '-' }}</div>
      </template>

      <template slot="functions">
        <em-bar-button :icon="modeIcon" @click="changeViewMode" />
      </template>
    </em-bar>
  </em-main>
</template>

<script>
import ReadSwiper from '@/vue/comp/ReadSwiper'
import ReadSlider from '@/vue/comp/ReadSlider'

export default {
  name: 'Read',
  components: {
    ReadSwiper,
    ReadSlider,
  },
  mounted() {
    if (
      !this.$route.query.url ||
      !this.$route.query.pages ||
      Number.isNaN(parseInt(this.$route.query.pages))
    ) {
      this.$back()
    }

    this.readViewMode()
  },
  data() {
    return {
      url: this.$route.query.url,
      pages: parseInt(this.$route.query.pages),

      swiperIndex: 0,
      sliderIndex: 0,

      viewMode: 'h',
    }
  },
  methods: {
    changeViewMode() {
      this.viewMode = this.viewMode === 'h' ? 'v' : 'h'
      this.saveViewMode()
    },
    readViewMode() {
      this.viewMode = localStorage.getItem('read-view-mode') || 'h'
    },
    saveViewMode() {
      localStorage.setItem('read-view-mode', this.viewMode)
    },
  },
  computed: {
    imageUrls() {
      let urls = []
      for (let i = 0; i < this.pages; i++) {
        urls.push(
          `${this.$project.api}image/by-index?url=${this.url}&index=${i}`
        )
      }
      return urls
    },
    modeIcon() {
      return {
        v: 'arrows-alt-v',
        h: 'arrows-alt-h',
      }[this.viewMode]
    },
  },
}
</script>

<style scoped>
#index {
  min-width: 40px;
  height: 25px;
  line-height: 25px;
  font-size: 14px;
  border-bottom: 1px solid #567;
  overflow: visible;
}

.active #index {
  border-bottom: 1px solid #0af;
}

#pages {
  min-width: 40px;
  height: 25px;
  line-height: 25px;
  font-size: 14px;
  overflow: visible;
}
</style>
