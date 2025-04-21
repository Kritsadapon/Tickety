import { createStore } from "vuex";
import axios from "axios";

export default createStore({
  state: {
    isLoggedIn: false,
    username: null,
    name: null,
    role: "",
    profilePictureUrl: null,
    id: null,
  },
  getters: {},
  mutations: {
    setIsLoggedIn(state, isLoggedIn) {
      state.isLoggedIn = isLoggedIn;
    },
    setUsername(state, username) {
      state.username = username;
    },
    setName(state, name) {
      state.name = name;
    },
    setRole(state, role) {
      state.role = role;
    },
    setProfilePictureUrl(state, profilePictureUrl) {
      state.profilePictureUrl = profilePictureUrl;
    },
    setId(state, id) {
      state.id = id;
    },
  },
  actions: {
    setLoggedInUser({ commit }, payload) {
      commit("setIsLoggedIn", payload.loggedIn);
      commit("setUsername", payload.username);
      commit("setName", payload.name);
      commit("setRole", payload.role);
      commit("setProfilePictureUrl", payload.profilePicture);
      commit("setId", payload.id);
    },
    async logout({ commit }) {
      try {
        await axios.get("/api/logout"); // Ensure this matches your backend route
        commit("setIsLoggedIn", false);
        commit("setUsername", null);
        commit("setName", null);
        commit("setRole", "");
        commit("setProfilePictureUrl", null);
        commit("setId", null);
      } catch (error) {
        console.error("Logout failed:", error);
      }
    },
  },

  modules: {},
});
