const { defineConfig } = require("@vue/cli-service");

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    host: "localhost",
    port: 8080,
    https: false,
    proxy: {
      "/api": {
        target: "http://localhost:8081", // Proxy to the Express.js server
        changeOrigin: true,
        secure: false,
      },
    },
  },
  pages: {
    index: {
      entry: "src/main.js",
      title: "UniTalk",
    },
  },
});
