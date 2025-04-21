<template>
  <div id="app">
    <header>
      <div class="header-content">
        <router-link to="/">
          <img src="/Unitalk_Logo%20copy.png" alt="Unitalk Logo" class="logo" />
          <img src="/Unitalk_Name%20copy.png" alt="Unitalk Name" class="name" />
        </router-link>

        <div class="search-container">
          <input
            type="text"
            v-model="searchKeyword"
            @keyup.enter="performSearch"
            :placeholder="
              isForumPageView ? 'Search posts...' : 'Search forums...'
            "
            class="search-input"
          />
        </div>
      </div>

      <div class="auth-button">
        <template v-if="isLoggedIn">
          <div class="user-info">
            <span class="display-name">{{ displayName }}</span>
            <button
              @click="toggleProfileDropdown"
              class="profile-btn"
              ref="profileButton"
            >
              <img
                v-if="profilePictureUrl"
                :src="profilePictureUrl"
                alt="Profile"
                class="profile-image"
              />
              <span v-else class="material-icons profile-icon"
                >account_circle</span
              >
            </button>

            <div
              v-if="profileDropdownVisible"
              class="profile-dropdown"
              ref="profileDropdown"
            >
              <router-link to="/profile" class="dropdown-item"
                >Profile</router-link
              >
              <button @click="logout" class="dropdown-item">Logout</button>
            </div>
          </div>
        </template>

        <template v-else>
          <router-link to="/login" class="sign-in">
            <span class="material-icons">login</span> Sign In
          </router-link>
        </template>
      </div>
    </header>

    <router-view />
  </div>
</template>

<script>
export default {
  computed: {
    isLoggedIn() {
      return this.$store.state.isLoggedIn;
    },
    displayName() {
      return this.$store.state.name;
    },
    isForumPageView() {
      return this.$route.path.startsWith("/forums/");
    },
    profilePictureUrl() {
      return this.$store.state.profilePictureUrl;
    },
  },
  data() {
    return {
      profileDropdownVisible: false,
      searchKeyword: "",
      profilePictureLoaded: false,
    };
  },
  watch: {
    isLoggedIn: {
      immediate: true,
      async handler(newValue) {
        if (newValue) {
          await this.fetchProfilePicture();
        }
      },
    },
    $route: {
      immediate: true,
      async handler() {
        if (this.isLoggedIn) {
          await this.fetchProfilePicture();
        }
      },
    },
    "$route.query.keyword": {
      immediate: true,
      handler(newKeyword) {
        if (newKeyword) {
          this.searchKeyword = newKeyword;
          this.performSearch();
        }
      },
    },
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
      const button = this.$refs.profileButton;
      if (
        dropdown &&
        !dropdown.contains(event.target) &&
        !button.contains(event.target)
      ) {
        this.closeProfileDropdown();
      }
    },
    logout() {
      this.$store.dispatch("logout").then(() => {
        this.$router.push("/");
      });
    },
    performSearch() {
      if (this.searchKeyword.trim()) {
        const query = { keyword: this.searchKeyword.trim() };
        if (this.isForumPageView) {
          const forumId = this.$route.params.forumid;
          this.$router.push({
            path: `/forums/${forumId}/search`,
            query,
          });
        } else {
          this.$router.push({
            path: "/search",
            query,
          });
        }
      }
    },
    async fetchProfilePicture() {
      if (this.isLoggedIn) {
        try {
          const response = await this.$axios.get("/api/users/profile-picture", {
            responseType: "blob",
          });
          const blob = new Blob([response.data], { type: "image/jpeg" });
          const url = URL.createObjectURL(blob);
          this.$store.commit("setProfilePictureUrl", url);
        } catch (error) {
          console.error("Failed to fetch profile picture", error);
          this.$store.commit("setProfilePictureUrl", "/default-avatar.svg");
        }
      }
    },
  },
  mounted() {
    document.addEventListener("click", this.handleClickOutside);
    if (this.isLoggedIn) {
      this.fetchProfilePicture();
    }
  },
  beforeUnmount() {
    document.removeEventListener("click", this.handleClickOutside);
  },
};
</script>

<style scoped>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background-color: rgba(24, 198, 198, 0.94);
}

.header-content {
  display: flex;
  align-items: center;
}

.logo {
  height: 50px;
  margin-right: 10px;
}

.name {
  height: 30px;
}

.search-container {
  margin-left: 30px;
}

.search-input {
  padding: 8px 12px;
  font-size: 16px;
  border-radius: 5px;
  border: none;
  outline: none;
}

.auth-button {
  display: flex;
  align-items: center;
}

.sign-in {
  display: flex;
  align-items: center;
  text-decoration: none;
  font-weight: bold;
  color: white;
  background-color: #2c3e50;
  padding: 10px 15px;
  border-radius: 5px;
  transition: background-color 0.3s;
}

.sign-in:hover {
  background-color: #1a252f;
}

.sign-in .material-icons {
  margin-right: 5px;
}

.user-info {
  position: relative;
  display: flex;
  align-items: center;
}

.display-name {
  font-size: 18px;
  font-weight: bold;
  color: white;
  margin-right: 1px;
}

.profile-btn {
  background: none;
  border: none;
  color: white;
  font-size: 18px;
  cursor: pointer;
  margin-right: 20px;
}

.profile-icon {
  font-size: 35px;
}

.profile-dropdown {
  position: absolute;
  top: 40px;
  right: 0;
  background-color: white;
  color: #2c3e50;
  border-radius: 5px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  min-width: 120px;
  z-index: 1000;
}

.dropdown-item {
  display: block;
  padding: 10px;
  text-decoration: none;
  color: inherit;
  text-align: left;
  background-color: white;
  transition: background-color 0.3s;
}

.dropdown-item:hover {
  background-color: rgba(24, 198, 198, 0.94);
  color: white;
}

.profile-image {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  object-fit: cover;
}

.profile-btn:hover {
  background-color: rgba(24, 198, 198, 0.94);
  color: white;
}

@import url("https://fonts.googleapis.com/icon?family=Material+Icons");
</style>
