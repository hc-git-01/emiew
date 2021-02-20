import Vue from 'vue'
import axios from 'axios'

const request = axios.create({
  baseURL: Vue.prototype.$project.api,
  timeout: 30000,
})

const source = axios.CancelToken.source()

request.interceptors.request.use(
  (config) => {
    return config
  },
  (error) => {
    source.cancel()
    console.error(error)
    Vue.prototype.$toast('网络连接异常', 'error')
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    if (response.data.code !== 200) {
      console.error(response.data.data)
      Vue.prototype.$toast(response.data.data, 'error')
      return Promise.reject(response)
    }

    return response.data.data
  },
  (error) => {
    source.cancel()
    console.error(error)
    Vue.prototype.$toast('请求超时', 'error')
    return Promise.reject(error)
  }
)

function formatQuery(params) {
  if (!params) {
    return {}
  }

  for (let key of Object.keys(params)) {
    let value = params[key]

    if (value instanceof Array && !(value[0] instanceof Object)) {
      params[key] = value.join(',')
    }
  }

  return params
}

function formatFormdata(params) {
  let formData = new FormData()

  if (!params) {
    return formData
  }

  for (let key of Object.keys(params)) {
    let value = params[key]
    
    if (value instanceof Array && !(value[0] instanceof Object)) {
      formData.append(key, value.join(','))
      continue
    }

    if (value instanceof Object) {
      formData.append(key, JSON.stringify(value))
      continue
    }

    formData.append(key, value)
  }

  return formData
}

Vue.prototype.$axios = {
  get(url, params) {
    return request.get(url, { params: formatQuery(params) })
  },
  post(url, params) {
    return request.post(url, formatFormdata(params))
  },
  delete(url, params) {
    return request.delete(url, { params: formatQuery(params) })
  },
}
