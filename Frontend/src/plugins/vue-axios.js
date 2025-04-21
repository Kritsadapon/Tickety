import axios from "axios";
import VueAxios from "vue-axios";

export default {
  install(app) {
    app.use(VueAxios, axios);
    app.config.globalProperties.$axios = axios;
  },
};
