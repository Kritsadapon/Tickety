import { createRouter, createWebHashHistory } from "vue-router";
import HomeView from "../views/HomeView.vue";
import axios from "axios";
import store from "@/store";

const routes = [
  {
    path: "/",
    name: "home",
    component: HomeView,
  },
  {
    path: "/about",
    name: "about",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/AboutView.vue"),
  },
  {
    path: "/login",
    name: "login",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/LoginView.vue"),
  },
  {
    path: "/register",
    name: "register",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/RegisterView.vue"),
  },
  {
    path: "/profile",
    name: "profile",
    // route level code-splitting
    // this generates a separate chunk (about.[hash].js) for this route
    // which is lazy-loaded when the route is visited.
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/ProfileView.vue"),
  },
  {
    path: "/profile/activity",
    name: "userActivity",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/UserActivityView.vue"),
  },
  {
    path: "/forums/create",
    name: "create",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/CreateForumView.vue"),
  },
  {
    path: "/forums/:forumid",
    name: "forumPageView",
    component: () =>
      import(/* webpackChunkName: "about" */ "../views/ForumPageView.vue"),
  },
  {
    path: "/forums/:forumid/create-post",
    name: "createPost",
    component: () => import("../views/CreatePostView.vue"),
  },
  {
    path: "/posts/:postid",
    name: "postPage",
    component: () => import("../views/PostPageView.vue"),
  },
  {
    path: "/search",
    name: "forumSearch",
    component: () => import("../views/ForumSearchView.vue"),
  },
  {
    path: "/forums/:forumid/search",
    name: "postSearch",
    component: () => import("../views/PostSearchView.vue"),
  },
  {
    path: "/forums/edit/:id",
    name: "editForum",
    component: () => import("../views/EditForumView.vue"),
  },
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});

router.beforeEach(async (to, from, next) => {
  try {
    // Get login state using whoami API call
    let response = await axios.get("/api/whoami");
    await store.dispatch("setLoggedInUser", response.data);
  } catch (error) {
    store.dispatch("logout"); // Clear state if session is invalid
  }

  const isLoggedIn = store.state.isLoggedIn;
  const authRequiredRoutes = [
    "profile",
    "dashboard",
    "settings",
    "userActivity",
  ]; // Protected routes

  if (to.name === "login" && isLoggedIn) {
    next({ name: "home" }); // Redirect logged-in users away from login page
  } else if (authRequiredRoutes.includes(to.name) && !isLoggedIn) {
    next({ name: "login" }); // Redirect unauthorized users to login
  } else {
    next(); // Allow access
  }
});

export default router;
