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

<style scoped>
.login-container {
  max-width: 400px;
  margin: 40px auto;
  padding: 40px;
  border-radius: 12px;
  background: #2d2d2d;
  border: 1px solid #404040;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

h2 {
  color: #ffffff;
  text-align: center;
  margin-bottom: 30px;
  font-size: 2rem;
}

.input-group {
  margin-bottom: 20px;
}

label {
  display: block;
  color: #b3b3b3;
  margin-bottom: 8px;
  font-size: 0.9rem;
}

input {
  width: 100%;
  padding: 12px;
  border: 1px solid #404040;
  border-radius: 8px;
  background: #1a1a1a;
  color: #ffffff;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

input:focus {
  outline: none;
  border-color: #007bff;
}

button {
  width: 100%;
  padding: 12px;
  background: #007bff;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

button:hover {
  background: #0056b3;
  transform: translateY(-2px);
}

button:disabled {
  background: #404040;
  cursor: not-allowed;
  transform: none;
}

.loading-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  100% {
    transform: rotate(360deg);
  }
}

.success-message {
  color: #4caf50;
  text-align: center;
  margin-top: 20px;
}

.error-message {
  color: #f44336;
  text-align: center;
  margin-top: 20px;
}

.signup-text {
  text-align: center;
  margin-top: 20px;
  color: #b3b3b3;
}

.signup-text a {
  color: #007bff;
  text-decoration: none;
  font-weight: 600;
}

.signup-text a:hover {
  text-decoration: underline;
}
</style>
