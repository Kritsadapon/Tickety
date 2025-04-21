<template>
  <div class="register-container">
    <h2>Register</h2>
    <form @submit.prevent="register">
      <div>
        <label for="username">Username:</label>
        <input type="text" id="username" v-model="username" required />
      </div>
      <div>
        <label for="password">Password:</label>
        <input
          type="password"
          id="password"
          v-model="password"
          required
          minlength="5"
        />
      </div>
      <div>
        <label for="confirmPassword">Confirm Password:</label>
        <input
          type="password"
          id="confirmPassword"
          v-model="confirmPassword"
          required
        />
      </div>
      <div>
        <label for="displayName">Display Name:</label>
        <input type="text" id="displayName" v-model="displayName" required />
      </div>
      <p v-if="error" class="error">{{ error }}</p>
      <button type="submit" :disabled="loading">
        {{ loading ? "Registering..." : "Register" }}
      </button>
    </form>
    <p v-if="success" class="success">{{ success }}</p>

    <p class="signin-text">
      Already got an account? <router-link to="/login">Sign in</router-link>
    </p>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      username: "",
      password: "",
      confirmPassword: "",
      displayName: "",
      error: "",
      success: "",
      loading: false,
    };
  },
  methods: {
    async register() {
      this.error = "";
      this.success = "";

      if (this.password !== this.confirmPassword) {
        this.error = "Passwords do not match!";
        return;
      }

      this.loading = true;
      try {
        const response = await axios.post("/api/register", {
          username: this.username,
          password: this.password,
          displayName: this.displayName,
        });

        if (response.data.success) {
          this.success = "Registration successful! Redirecting to login...";
          setTimeout(() => this.$router.push("/login"), 2000);
        } else {
          this.error = response.data.message;
        }
      } catch (err) {
        this.error = "An error occurred. Please try again.";
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: auto;
  padding: 25px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
  text-align: center;
}

label {
  display: block;
  margin-top: 12px;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 10px;
  margin-top: 5px;
  border: 1px solid #ccc;
  border-radius: 6px;
  font-size: 16px;
}

button {
  margin-top: 20px;
  padding: 12px;
  width: 100%;
  background: #18c6c6;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 18px;
  cursor: pointer;
  transition: background 0.3s;
}

button:hover {
  background: #14a6a6;
}

button:disabled {
  background: #cccccc;
  cursor: not-allowed;
}

.error {
  color: red;
  margin-top: 10px;
  font-weight: bold;
}

.success {
  color: green;
  margin-top: 10px;
  font-weight: bold;
}

.signin-text {
  margin-top: 20px;
  font-size: 14px;
}

.signin-text a {
  color: #18c6c6;
  text-decoration: none;
}

.signin-text a:hover {
  text-decoration: underline;
}
</style>
