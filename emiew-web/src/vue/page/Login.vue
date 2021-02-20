<template>
  <em-main :loading="fetchingLogin || fetchingGetCookie || fetchingSetCookie">
    <em-scroller :gap="true">
      <em-title icon="comment">注意</em-title>
      <div>
        <p>
          填写 Cookie 后，将从
          <a href="https://exhentai.org/" target="_blank">ExHentai</a>
          获取数据。
        </p>
        <p>
          通过
          <a
            href="https://forums.e-hentai.org/index.php?act=Reg"
            target="_blank"
            >Registration</a
          >
          进行注册。
        </p>
      </div>

      <em-title icon="sign-in-alt">通过登录获取Cookie</em-title>
      <em-input
        class="login-input"
        icon="user"
        placeholder="用户名"
        v-model="form.username"
      />
      <em-input
        class="login-input"
        icon="lock"
        type="password"
        placeholder="密码"
        v-model="form.password"
      />
      <em-button @click="fetchLogin">登录</em-button>

      <em-title icon="cookie-bite">直接填写Cookie</em-title>
      <em-input
        class="login-input"
        placeholder="ipb_pass_hash"
        v-model="cookie.ipbPassHash"
      />
      <em-input
        class="login-input"
        placeholder="ipb_member_id"
        v-model="cookie.ipbMemberId"
      />
      <em-input
        class="login-input"
        placeholder="igneous"
        v-model="cookie.igneous"
      />
    </em-scroller>

    <em-bar>
      <em-bar-button icon="sync-alt" @click="fetchGetCookie" />
      <em-bar-button icon="eraser" @click="handleClear" />
      <em-bar-button icon="save" @click="fetchSetCookie" />
    </em-bar>
  </em-main>
</template>

<script>
export default {
  name: 'Login',
  mounted() {
    this.fetchGetCookie()
  },
  data() {
    return {
      form: {
        username: '',
        password: '',
      },

      cookie: {
        ipbPassHash: '',
        ipbMemberId: '',
        igneous: '',
      },

      fetchingLogin: false,
      fetchingGetCookie: false,
      fetchingSetCookie: false,
    }
  },
  methods: {
    fetchLogin() {
      if (this.fetchingLogin) {
        return
      }

      if (!this.form.username || !this.form.password) {
        this.$toast('请填写正确的用户名和密码', 'warn')
        return
      }

      this.cookie = {
        ipbPassHash: '',
        ipbMemberId: '',
        igneous: '',
      }

      this.fetchingLogin = true
      this.$axios
        .post('/login', this.form)
        .then((cookie) => {
          this.cookie = cookie
          this.$toast('已成功获取Cookie')
          this.fetchingLogin = false
        })
        .catch(() => {
          this.fetchingLogin = false
        })
    },
    fetchGetCookie() {
      if (this.fetchingGetCookie) {
        return
      }
      this.fetchingGetCookie = true
      this.$axios
        .get('/login/cookie')
        .then((cookie) => {
          this.cookie = cookie
          this.fetchingGetCookie = false
        })
        .catch(() => {
          this.fetchingGetCookie = false
        })
    },
    fetchSetCookie() {
      if (this.fetchingSetCookie) {
        return
      }
      this.fetchingSetCookie = true
      this.$axios
        .post('/login/cookie', this.cookie)
        .then(() => {
          this.fetchingSetCookie = false
          alert('保存成功')
          location.reload()
        })
        .catch(() => {
          this.fetchingSetCookie = false
        })
    },
    handleClear() {
      this.cookie = {
        ipbPassHash: '',
        ipbMemberId: '',
        igneous: '',
      }
      this.form = {
        username: '',
        password: '',
      }
    },
  },
}
</script>

<style scoped>
.login-input {
  margin-bottom: 10px;
}

.login-input:last-of-type {
  margin-bottom: 0;
}
</style>
