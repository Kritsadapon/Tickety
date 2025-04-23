<template>
  <div id="app">
    <header>
      <div class="header-content">
        <router-link to="/" class="logo-link">
          <span class="logo-text">Tickety</span>
        </router-link>

        <nav class="main-nav" v-if="isLoggedIn">
          <router-link to="/dashboard" class="nav-link">Dashboard</router-link>
          <router-link to="/dashboard/tickets" class="nav-link"
            >Tickets</router-link
          >
          <router-link to="/dashboard/teams" class="nav-link"
            >Teams</router-link
          >
          <router-link to="/dashboard/flows" class="nav-link"
            >Workflows</router-link
          >
        </nav>
      </div>

      <div class="auth-section">
        <template v-if="isLoggedIn">
          <div class="user-info">
            <div class="user-dropdown" @click="toggleProfileDropdown">
              <img
                v-if="profilePictureUrl"
                :src="profilePictureUrl"
                alt="Profile"
                class="profile-image"
              />
              <span v-else class="profile-placeholder">
                {{ displayName.charAt(0).toUpperCase() }}
              </span>
              <span class="user-name">{{ displayName }}</span>
            </div>

            <div v-if="profileDropdownVisible" class="profile-dropdown">
              <router-link to="/dashboard/profile" class="dropdown-item">
                <font-awesome-icon :icon="['fas', 'user']" />
                Profile
              </router-link>
              <router-link
                to="/dashboard/team-invitations"
                class="dropdown-item"
              >
                <font-awesome-icon :icon="['fas', 'envelope']" />
                Team Invitations
              </router-link>
              <button @click="logout" class="dropdown-item">
                <font-awesome-icon :icon="['fas', 'sign-out-alt']" />
                Logout
              </button>
            </div>
          </div>
        </template>

        <template v-else>
          <router-link to="/login" class="auth-button">
            <font-awesome-icon :icon="['fas', 'sign-in-alt']" />
            Sign In
          </router-link>
        </template>
      </div>
    </header>

    <main>
      <router-view />
    </main>
  </div>
</template>

<script>
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import {
  faUser,
  faEnvelope,
  faSignOutAlt,
  faSignInAlt,
} from "@fortawesome/free-solid-svg-icons";

library.add(faUser, faEnvelope, faSignOutAlt, faSignInAlt);

export default {
  components: {
    FontAwesomeIcon,
  },
  computed: {
    isLoggedIn() {
      return this.$store.state.isLoggedIn;
    },
    displayName() {
      return this.$store.state.name;
    },
    profilePictureUrl() {
      return this.$store.state.profilePictureUrl;
    },
  },
  data() {
    return {
      profileDropdownVisible: false,
    };
  },
  methods: {
    toggleProfileDropdown() {
      this.profileDropdownVisible = !this.profileDropdownVisible;
    },
    closeProfileDropdown() {
      this.profileDropdownVisible = false;
    },
    handleClickOutside(event) {
      const dropdown = this.$refs.profileDropdown;
      if (dropdown && !dropdown.contains(event.target)) {
        this.closeProfileDropdown();
      }
    },
    logout() {
      this.$store.dispatch("logout").then(() => {
        this.$router.push("/login");
      });
    },
  },
  mounted() {
    document.addEventListener("click", this.handleClickOutside);
  },
  beforeUnmount() {
    document.removeEventListener("click", this.handleClickOutside);
  },
};
</script>

<style scoped>
#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

header {
  background-color: #2d2d2d;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #404040;
}

.header-content {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.logo-link {
  text-decoration: none;
  color: #ffffff;
}

.logo-text {
  font-size: 1.5rem;
  font-weight: bold;
  color: #007bff;
}

.main-nav {
  display: flex;
  gap: 1.5rem;
}

.nav-link {
  color: #b3b3b3;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.nav-link:hover,
.nav-link.router-link-active {
  color: #ffffff;
  background-color: #404040;
}

.auth-section {
  display: flex;
  align-items: center;
}

.auth-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  background-color: #007bff;
  color: #ffffff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  text-decoration: none;
  transition: background-color 0.3s ease;
}

.auth-button:hover {
  background-color: #0056b3;
}

.user-info {
  position: relative;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.user-dropdown:hover {
  background-color: #404040;
}

.profile-image {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.profile-placeholder {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background-color: #007bff;
  color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
}

.user-name {
  color: #ffffff;
  font-weight: 500;
}

.profile-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background-color: #2d2d2d;
  border: 1px solid #404040;
  border-radius: 6px;
  padding: 0.5rem;
  min-width: 200px;
  margin-top: 0.5rem;
  z-index: 1000;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  color: #b3b3b3;
  text-decoration: none;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.dropdown-item:hover {
  background-color: #404040;
  color: #ffffff;
}

main {
  flex: 1;
  background-color: #1a1a1a;
  color: #ffffff;
}
</style>
