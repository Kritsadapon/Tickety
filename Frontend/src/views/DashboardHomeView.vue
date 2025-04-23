<template>
  <div class="dashboard-home">
    <div class="welcome-section">
      <h2>Welcome to Tickety</h2>
      <p class="subtitle">Your ticket management dashboard</p>
    </div>

    <div class="dashboard-stats">
      <div class="stat-card">
        <div class="stat-icon">
          <font-awesome-icon :icon="['fas', 'ticket-alt']" />
        </div>
        <div class="stat-content">
          <h3>Total Tickets</h3>
          <p class="stat-value">{{ ticketsCount }}</p>
          <p class="stat-label">Across all teams</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">
          <font-awesome-icon :icon="['fas', 'users']" />
        </div>
        <div class="stat-content">
          <h3>Active Teams</h3>
          <p class="stat-value">{{ teamsCount }}</p>
          <p class="stat-label">Managing tickets</p>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">
          <font-awesome-icon :icon="['fas', 'project-diagram']" />
        </div>
        <div class="stat-content">
          <h3>Workflows</h3>
          <p class="stat-value">{{ flowsCount }}</p>
          <p class="stat-label">Active processes</p>
        </div>
      </div>
    </div>

    <div class="quick-actions">
      <h3>Quick Actions</h3>
      <div class="action-buttons">
        <button
          class="action-button"
          @click="$router.push('/dashboard/tickets')"
        >
          <font-awesome-icon :icon="['fas', 'ticket-alt']" />
          View Tickets
        </button>
        <button class="action-button" @click="$router.push('/dashboard/teams')">
          <font-awesome-icon :icon="['fas', 'users']" />
          Manage Teams
        </button>
        <button class="action-button" @click="$router.push('/dashboard/flows')">
          <font-awesome-icon :icon="['fas', 'project-diagram']" />
          Configure Workflows
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import {
  faTicketAlt,
  faUsers,
  faProjectDiagram,
} from "@fortawesome/free-solid-svg-icons";

library.add(faTicketAlt, faUsers, faProjectDiagram);

const teamsCount = ref(0);
const flowsCount = ref(0);
const ticketsCount = ref(0);

const fetchStats = async () => {
  try {
    const [teamsRes, flowsRes, ticketsRes] = await Promise.all([
      axios.get("/api/teams/count"),
      axios.get("/api/flows/count"),
      axios.get("/api/tickets/count"),
    ]);

    teamsCount.value = teamsRes.data;
    flowsCount.value = flowsRes.data;
    ticketsCount.value = ticketsRes.data;
  } catch (error) {
    console.error("Failed to fetch dashboard stats:", error);
  }
};

onMounted(() => {
  fetchStats();
});
</script>

<style scoped>
.dashboard-home {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-section {
  margin-bottom: 40px;
}

.welcome-section h2 {
  font-size: 2.2rem;
  color: #ffffff;
  margin-bottom: 10px;
}

.subtitle {
  color: #b3b3b3;
  font-size: 1.1rem;
}

.dashboard-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.stat-card {
  background-color: #2d2d2d;
  border-radius: 12px;
  padding: 25px;
  display: flex;
  align-items: center;
  gap: 20px;
  border: 1px solid #404040;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  width: 60px;
  height: 60px;
  background-color: #404040;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.8rem;
  color: #007bff;
}

.stat-content {
  flex: 1;
}

.stat-content h3 {
  color: #b3b3b3;
  font-size: 1rem;
  margin: 0 0 5px 0;
}

.stat-value {
  color: #ffffff;
  font-size: 2.2rem;
  font-weight: bold;
  margin: 0;
}

.stat-label {
  color: #b3b3b3;
  font-size: 0.9rem;
  margin: 5px 0 0 0;
}

.quick-actions {
  background-color: #2d2d2d;
  border-radius: 12px;
  padding: 25px;
  border: 1px solid #404040;
}

.quick-actions h3 {
  color: #ffffff;
  font-size: 1.4rem;
  margin-bottom: 20px;
}

.action-buttons {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.action-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 15px;
  background-color: #404040;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 1rem;
}

.action-button:hover {
  background-color: #505050;
}

.action-button .icon {
  font-size: 1.2rem;
}
</style>
