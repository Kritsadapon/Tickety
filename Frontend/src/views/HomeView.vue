<template>
  <div class="home">
    <div class="hero-section">
      <div class="hero-content">
        <h1>Welcome to Tickety</h1>
        <p class="subtitle">
          Streamline your team's workflow with our powerful ticket management
          system
        </p>
        <div class="cta-buttons">
          <router-link to="/login" class="primary-button">
            <font-awesome-icon :icon="['fas', 'sign-in-alt']" />
            Get Started
          </router-link>
          <router-link to="/register" class="secondary-button">
            <font-awesome-icon :icon="['fas', 'user-plus']" />
            Create Account
          </router-link>
        </div>
      </div>
    </div>

    <div class="features-section">
      <h2>Key Features</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">
            <font-awesome-icon :icon="['fas', 'ticket-alt']" />
          </div>
          <h3>Ticket Management</h3>
          <p>
            Create, track, and manage tickets with ease. Keep your team
            organized and on track.
          </p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <font-awesome-icon :icon="['fas', 'users']" />
          </div>
          <h3>Team Collaboration</h3>
          <p>
            Work together seamlessly with team-based ticket assignments and
            real-time updates.
          </p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">
            <font-awesome-icon :icon="['fas', 'project-diagram']" />
          </div>
          <h3>Custom Workflows</h3>
          <p>
            Design and implement custom workflows that match your team's unique
            processes.
          </p>
        </div>
      </div>
    </div>

    <div class="benefits-section">
      <h2>Why Choose Tickety?</h2>
      <div class="benefits-grid">
        <div class="benefit-item">
          <font-awesome-icon :icon="['fas', 'bolt']" />
          <h3>Fast & Efficient</h3>
          <p>
            Streamline your workflow and boost productivity with our intuitive
            interface.
          </p>
        </div>
        <div class="benefit-item">
          <font-awesome-icon :icon="['fas', 'shield-alt']" />
          <h3>Secure & Reliable</h3>
          <p>Your data is protected with enterprise-grade security measures.</p>
        </div>
        <div class="benefit-item">
          <font-awesome-icon :icon="['fas', 'chart-line']" />
          <h3>Track Progress</h3>
          <p>
            Monitor team performance and project progress with detailed
            analytics.
          </p>
        </div>
        <div class="benefit-item">
          <font-awesome-icon :icon="['fas', 'mobile-alt']" />
          <h3>Responsive Design</h3>
          <p>
            Access your tickets and manage your team from any device, anywhere.
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import {
  faTicketAlt,
  faUsers,
  faProjectDiagram,
  faBolt,
  faShieldAlt,
  faChartLine,
  faMobileAlt,
  faSignInAlt,
  faUserPlus,
} from "@fortawesome/free-solid-svg-icons";

library.add(
  faTicketAlt,
  faUsers,
  faProjectDiagram,
  faBolt,
  faShieldAlt,
  faChartLine,
  faMobileAlt,
  faSignInAlt,
  faUserPlus
);
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.hero-section {
  min-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  background: linear-gradient(135deg, #1a1a1a 0%, #2a2a2a 100%);
  border-radius: 12px;
  margin: 20px 0;
  padding: 40px 20px;
}

.hero-content {
  max-width: 800px;
}

.hero-section h1 {
  font-size: 3.5rem;
  color: #ffffff;
  margin-bottom: 20px;
}

.subtitle {
  font-size: 1.5rem;
  color: #b3b3b3;
  margin-bottom: 40px;
}

.cta-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.primary-button,
.secondary-button {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px 30px;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  text-decoration: none;
  transition: all 0.3s ease;
}

.primary-button {
  background-color: #007bff;
  color: #ffffff;
}

.primary-button:hover {
  background-color: #0056b3;
  transform: translateY(-2px);
}

.secondary-button {
  background-color: #404040;
  color: #ffffff;
}

.secondary-button:hover {
  background-color: #505050;
  transform: translateY(-2px);
}

.features-section,
.benefits-section {
  padding: 80px 0;
  text-align: center;
}

.features-section h2,
.benefits-section h2 {
  font-size: 2.5rem;
  color: #ffffff;
  margin-bottom: 40px;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.feature-card {
  background-color: #2d2d2d;
  padding: 30px;
  border-radius: 12px;
  border: 1px solid #404040;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 2.5rem;
  color: #007bff;
  margin-bottom: 20px;
}

.feature-card h3 {
  color: #ffffff;
  font-size: 1.5rem;
  margin-bottom: 15px;
}

.feature-card p {
  color: #b3b3b3;
  line-height: 1.6;
}

.benefits-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
  margin-top: 40px;
}

.benefit-item {
  background-color: #2d2d2d;
  padding: 30px;
  border-radius: 12px;
  border: 1px solid #404040;
}

.benefit-item .fa-icon {
  font-size: 2rem;
  color: #007bff;
  margin-bottom: 20px;
}

.benefit-item h3 {
  color: #ffffff;
  font-size: 1.3rem;
  margin-bottom: 15px;
}

.benefit-item p {
  color: #b3b3b3;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .hero-section h1 {
    font-size: 2.5rem;
  }

  .subtitle {
    font-size: 1.2rem;
  }

  .cta-buttons {
    flex-direction: column;
  }

  .primary-button,
  .secondary-button {
    width: 100%;
    justify-content: center;
  }
}
</style>
