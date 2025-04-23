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

form > div {
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

.error {
  color: #f44336;
  text-align: center;
  margin: 20px 0;
}

.success {
  color: #4caf50;
  text-align: center;
  margin: 20px 0;
}

.signin-text {
  text-align: center;
  margin-top: 20px;
  color: #b3b3b3;
}

.signin-text a {
  color: #007bff;
  text-decoration: none;
  font-weight: 600;
}

.signin-text a:hover {
  text-decoration: underline;
}
</style>
