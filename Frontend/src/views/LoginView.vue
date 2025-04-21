<template>
  <div class="login-container">
    <h2>Login</h2>
    <form @submit.prevent="login">
      <div class="input-group">
        <label for="username">Username</label>
        <input type="text" id="username" v-model="username" required />
      </div>

      <div class="input-group">
        <label for="password">Password</label>
        <input type="password" id="password" v-model="password" required />
      </div>

      <button type="submit" :disabled="loading">
        <span v-if="loading" class="loading-icon">⏳</span>
        <span v-else>Sign In</span>
      </button>

      <p
        v-if="message"
        :class="{ 'success-message': success, 'error-message': !success }"
      >
        {{ message }}
      </p>
    </form>

    <!-- Text Link for Sign Up -->
    <p class="signup-text">
      Not a member? <router-link to="/register">Sign Up</router-link>
    </p>
  </div>
</template>

<script>
import { ref } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";
import { useStore } from "vuex";

export default {
  setup() {
    const username = ref("");
    const password = ref("");
    const message = ref("");
    const success = ref(false);
    const loading = ref(false);
    const router = useRouter();
    const store = useStore();

    const login = async () => {
      loading.value = true;
      message.value = "";

      try {
        const response = await axios.post(
          "api/login",
          new URLSearchParams({
            username: username.value,
            password: password.value,
          }),
          {
            headers: {
              "Content-Type": "application/x-www-form-urlencoded",
            },
            withCredentials: true,
          }
        );

        if (response.data.success) {
          success.value = true;
          message.value = response.data.message;

          // Fetch user details with credentials
          const userResponse = await axios.get("/api/whoami", {
            withCredentials: true,
          });
          store.dispatch("setLoggedInUser", userResponse.data);

          // Redirect to home
          setTimeout(() => router.push("/"), 1000);
        } else {
          success.value = false;
          message.value = response.data.message;
        }
      } catch (error) {
        success.value = false;
        message.value = "Login failed. Please check your credentials.";
      } finally {
        loading.value = false;
      }
    };

    return {
      username,
      password,
      message,
      success,
      loading,
      login,
    };
  },
};
</script>

<style>
.login-container {
  max-width: 350px;
  margin: 50px auto;
  padding: 20px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.input-group {
  margin-bottom: 15px;
  text-align: left;
}

.input-group label {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
}

.input-group input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 5px;
}

button {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 5px;
  background-color: #18c6c6;
  color: white;
  font-weight: bold;
  cursor: pointer;
}

button:disabled {
  background-color: #cccccc;
}

/* Sign Up text link */
.signup-text {
  margin-top: 15px;
  font-size: 14px;
}

.signup-text a {
  color: #18c6c6;
  text-decoration: none;
}

.signup-text a:hover {
  text-decoration: underline;
}

.success-message {
  color: green;
  font-weight: bold;
}

.error-message {
  color: red;
  font-weight: bold;
}
</style>
