import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import store from "./store";
import vuetify from "./plugins/vuetify";
import vueAxios from "./plugins/vue-axios"; // Import the updated plugin

// Import fontawesome for icons
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { fas } from "@fortawesome/free-solid-svg-icons";
import { far } from "@fortawesome/free-regular-svg-icons";

library.add(fas, far);

const app = createApp(App);

app.use(router);
app.use(store);
app.use(vuetify);
app.use(vueAxios);
app.component("font-awesome-icon", FontAwesomeIcon);

app.mount("#app");
