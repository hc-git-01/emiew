module.exports = {
  publicPath: './',
  runtimeCompiler: true,
  devServer: {
    proxy: {
      '^/api': {
        target: 'http://127.0.0.1:8848/',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '',
        },
      },
    },
  },
}
