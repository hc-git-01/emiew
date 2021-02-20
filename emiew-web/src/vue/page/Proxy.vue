<template>
  <em-main :loading="fetchingGetProxy || fetchingSetProxy">
    <em-scroller :gap="true">
      <em-title icon="comment">注意</em-title>
      <div>
        <p>
          本代理应用于服务端而非客户端。
        </p>
        <p>
          通常情况下，服务端程序会自动检测设备上的代理配置。
        </p>
        <p>
          当自动检测无效时，请通过手动配置代理来进行网络请求。
        </p>
      </div>

      <em-title icon="pen">配置</em-title>
      <div id="proxy-input-group">
        <em-input id="host-input" placeholder="IP" v-model="proxy.host" />
        <span id="proxy-splitter">:</span>
        <em-input
          id="port-input"
          type="number"
          placeholder="端口"
          v-model="proxy.port"
        />
      </div>
    </em-scroller>

    <em-bar>
      <em-bar-button icon="sync-alt" @click="fetchGetProxy" />
      <em-bar-button icon="eraser" @click="handleClear" />
      <em-bar-button icon="save" @click="fetchSetProxy" />
    </em-bar>
  </em-main>
</template>

<script>
export default {
  name: 'Proxy',
  mounted() {
    this.fetchGetProxy()
  },
  data() {
    return {
      proxy: {
        host: '',
        port: '',
      },

      fetchingGetProxy: false,
      fetchingSetProxy: false,
    }
  },
  methods: {
    fetchGetProxy() {
      if (this.fetchingGetProxy) {
        return
      }
      this.fetchingGetProxy = true
      this.$axios
        .get('/config/proxy')
        .then((proxy) => {
          this.proxy = proxy
          this.fetchingGetProxy = false
        })
        .catch(() => {
          this.fetchingGetProxy = false
        })
    },
    fetchSetProxy() {
      if (this.fetchingSetProxy) {
        return
      }
      this.fetchingSetProxy = true
      this.$axios
        .post('/config/proxy', this.proxy)
        .then(() => {
          this.fetchingSetProxy = false
          alert('保存成功')
          location.reload()
        })
        .catch(() => {
          this.fetchingSetProxy = false
        })
    },
    handleClear() {
      this.proxy = {
        host: '',
        port: '',
      }
    },
  },
}
</script>

<style scoped>
#proxy-input-group {
  white-space: nowrap;
  overflow: hidden;
  width: 100%;
}

#host-input {
  width: calc(60% - 10px);
}

#port-input {
  width: calc(40% - 10px);
}

#proxy-splitter {
  display: inline-block;
  width: 20px;
  line-height: 40px;
  text-align: center;
}
</style>
