import { createRouter, createWebHistory } from "vue-router";
import DashboardView from "../views/DashboardView.vue";
import ProfileView from "../views/ProfileView.vue";
import LoginView from "../views/LoginView.vue";
import RegisterView from "../views/RegisterView.vue";
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
    path: "/login",
    name: "login",
    component: LoginView,
  },
  {
    path: "/register",
    name: "register",
    component: RegisterView,
  },
  {
    path: "/dashboard",
    name: "dashboard",
    component: DashboardView,
    children: [
      {
        path: "",
        name: "dashboard-home",
        component: () => import("../views/DashboardHomeView.vue"),
      },
      {
        path: "teams",
        name: "teams",
        component: () => import("../views/TeamsView.vue"),
      },
      {
        path: "flows",
        name: "flows",
        component: () => import("../views/FlowsView.vue"),
      },
      {
        path: "tickets",
        name: "tickets",
        component: () => import("../views/TicketsView.vue"),
      },
      {
        path: "profile",
        name: "profile",
        component: ProfileView,
      },
      {
        path: "team-invitations",
        name: "team-invitations",
        component: () => import("../views/TeamInvitationsView.vue"),
      },
    ],
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
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
  const publicRoutes = ["login", "register"]; // Public routes that don't require authentication

  if (to.name === "login" && isLoggedIn) {
    next({ name: "dashboard-home" }); // Redirect logged-in users to dashboard
  } else if (!publicRoutes.includes(to.name) && !isLoggedIn) {
    next({ name: "login" }); // Redirect unauthorized users to login
  } else {
    next(); // Allow access
  }
});

export default router;
