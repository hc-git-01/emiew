<template>
  <em-main :loading="fetchingDisplay || fetchingReloadProxy">
    <em-scroller :gap="true">
      <em-title icon="comment">注意</em-title>
      <div class="config-text">
        <p>
          本软件由 H.C 开发，与
          <a href="https://e-hentai.org/" target="_blank">E-Hentai</a>
          无任何联系。
        </p>
        <p>
          通过邮件
          <a href="mailto:hc-mail-01@yandex.com">hc-mail-01@yandex.com</a>
          来与作者进行沟通。
        </p>
      </div>

      <em-title icon="paper-plane">代理</em-title>
      <div class="config-text">
        <span>工作模式: </span>
        <span
          v-if="display"
          :style="{ color: display.proxySet ? '#0af' : '#6a6' }"
        >
          {{ display.proxySet ? '手动配置' : '自动检测' }}
        </span>
        <fa-icon v-else name="circle-notch" spin />
      </div>
      <div class="config-text">
        <span>启用状态: </span>
        <span :style="{ color: display.proxyEnabled ? '#6a6' : '#f66' }">
          {{ display.proxyEnabled ? '已启用' : '未启用' }}
          <fa-icon
            v-if="display"
            :name="display.proxyEnabled ? 'check' : 'times'"
          />
          <fa-icon v-else name="circle-notch" spin />
        </span>
      </div>
      <em-button @click="$push({ name: 'Proxy' })">配置</em-button>
      <em-button
        id="reload-proxy-button"
        type="orange"
        @click="fetchReloadProxy"
      >
        刷新
      </em-button>

      <em-title icon="window-restore">站点</em-title>
      <div class="config-text">
        <span>当前站点: </span>
        <a
          v-if="display"
          :style="{ color: display.cookieLoaded ? '#6a6' : '#234' }"
          :href="
            display.cookieLoaded
              ? 'https://exhentai.org/'
              : 'https://e-hentai.org/'
          "
          target="_blank"
        >
          {{ display.cookieLoaded ? 'ExHentai' : 'E-Hentai' }}
        </a>
        <fa-icon v-else name="circle-notch" spin />
      </div>
      <em-button @click="$push({ name: 'Login' })">修改</em-button>

      <em-title icon="eye-slash">屏蔽</em-title>
      <div class="config-text">
        <span>屏蔽数量: </span>
        <span v-if="display">{{ display.blockSize }}</span>
        <fa-icon v-else name="circle-notch" spin />
      </div>
      <em-button @click="$push({ name: 'Block' })">查看</em-button>

      <em-title icon="info-circle">版本</em-title>
      <div class="config-text">
        <span>当前版本: </span>
        <span v-if="display">{{ display.appVersion }}</span>
        <fa-icon v-else name="circle-notch" spin />
      </div>
      <!-- <em-button icon="cloud-download-alt" type="blue" text="检查" /> -->
      <!-- <em-button
        id="github-button"
        icon="brands/github"
        type="github"
        text="前往"
      /> -->
    </em-scroller>

    <em-bar>
      <em-bar-button icon="sync-alt" @click="fetchDisplay" />
    </em-bar>
  </em-main>
</template>

<script>
export default {
  name: 'Config',
  activated() {
    this.fetchDisplay()
  },
  data() {
    return {
      display: '',
      fetchingDisplay: false,
      fetchingReloadProxy: false,
    }
  },
  methods: {
    fetchDisplay() {
      if (this.fetchingDisplay) {
        return
      }
      this.fetchingDisplay = true
      this.$axios
        .get('/config/display')
        .then((display) => {
          this.display = display
          this.fetchingDisplay = false
        })
        .catch(() => {
          this.fetchingDisplay = false
        })
    },
    fetchReloadProxy() {
      if (this.fetchingReloadProxy) {
        return
      }
      this.fetchingReloadProxy = true
      this.$axios
        .get('/crawl/reload')
        .then(() => {
          this.fetchingReloadProxy = false
          this.$toast('已更新代理状态')
          this.fetchDisplay()
        })
        .catch(() => {
          this.fetchingReloadProxy = false
        })
    },
  },
}
</script>

<style scoped>
.em-button {
  margin-right: 10px;
}

.config-text {
  margin-bottom: 10px;
}
</style>
