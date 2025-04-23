<template>
  <div class="dashboard-container">
    <div class="sidebar">
      <div class="sidebar-header">
        <h2>Tickety</h2>
      </div>
      <div class="sidebar-menu">
        <router-link to="/dashboard" class="menu-item" active-class="active">
          <font-awesome-icon :icon="['fas', 'home']" class="icon" />
          Dashboard
        </router-link>
        <router-link to="/teams" class="menu-item" active-class="active">
          <font-awesome-icon :icon="['fas', 'users']" class="icon" />
          Teams
        </router-link>
        <router-link to="/flows" class="menu-item" active-class="active">
          <font-awesome-icon :icon="['fas', 'project-diagram']" class="icon" />
          Flows
        </router-link>
        <router-link to="/tickets" class="menu-item" active-class="active">
          <font-awesome-icon :icon="['fas', 'ticket-alt']" class="icon" />
          Tickets
        </router-link>
      </div>
      <div class="sidebar-footer">
        <router-link to="/profile" class="menu-item">
          <font-awesome-icon :icon="['fas', 'user']" class="icon" />
          Profile
        </router-link>
      </div>
    </div>
    <div class="main-content">
      <div class="header">
        <h1>{{ currentView }}</h1>
        <div class="user-info">
          <span>{{ userName }}</span>
          <img :src="profilePictureUrl" alt="Profile" class="profile-image" />
        </div>
      </div>
      <div class="content">
        <router-view></router-view>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from "vue";
import { useStore } from "vuex";
import { useRoute } from "vue-router";
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import {
  faHome,
  faUsers,
  faProjectDiagram,
  faTicketAlt,
  faUser,
} from "@fortawesome/free-solid-svg-icons";

library.add(faHome, faUsers, faProjectDiagram, faTicketAlt, faUser);

export default {
  components: {
    FontAwesomeIcon,
  },
  setup() {
    const store = useStore();
    const route = useRoute();

    const userName = computed(() => store.state.name || "User");
    const profilePictureUrl = computed(
      () => store.state.profilePictureUrl || "/default-avatar.svg"
    );
    const currentView = computed(() => {
      const path = route.path.split("/")[1];
      return path.charAt(0).toUpperCase() + path.slice(1);
    });

    return {
      userName,
      profilePictureUrl,
      currentView,
    };
  },
};
</script>

<style scoped>
.dashboard-container {
  display: flex;
  min-height: 100vh;
  background-color: #1a1a1a;
  color: #ffffff;
}

.sidebar {
  width: 250px;
  background-color: #2d2d2d;
  padding: 20px;
  display: flex;
  flex-direction: column;
  border-right: 1px solid #404040;
}

.sidebar-header {
  padding-bottom: 20px;
  border-bottom: 1px solid #404040;
  margin-bottom: 20px;
}

.sidebar-header h2 {
  color: #ffffff;
  font-size: 1.5rem;
  font-weight: 600;
}

.sidebar-menu {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 15px;
  color: #b3b3b3;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.menu-item:hover {
  background-color: #404040;
  color: #ffffff;
}

.menu-item.active {
  background-color: #404040;
  color: #ffffff;
}

.menu-item .icon {
  margin-right: 10px;
  width: 20px;
}

.sidebar-footer {
  padding-top: 20px;
  border-top: 1px solid #404040;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: #2d2d2d;
  border-bottom: 1px solid #404040;
}

.header h1 {
  font-size: 1.8rem;
  font-weight: 600;
  color: #ffffff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info span {
  color: #b3b3b3;
}

.profile-image {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
</style>
