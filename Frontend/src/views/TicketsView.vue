<template>
  <div class="tickets-container">
    <div class="tickets-header">
      <h2>Tickets</h2>
      <button
        class="create-ticket-button"
        @click="showCreateTicketModal = true"
      >
        <font-awesome-icon :icon="['fas', 'plus']" class="icon" />
        Create Ticket
      </button>
    </div>

    <div class="tickets-filters">
      <div class="filter-group">
        <label>Team</label>
        <select v-model="selectedTeam" class="filter-select">
          <option value="">All Teams</option>
          <option v-for="team in teams" :key="team.id" :value="team.id">
            {{ team.name }}
          </option>
        </select>
      </div>
      <div class="filter-group">
        <label>Status</label>
        <select v-model="selectedStatus" class="filter-select">
          <option value="">All Statuses</option>
          <option value="TODO">To Do</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="DONE">Done</option>
          <option value="BLOCKED">Blocked</option>
        </select>
      </div>
      <div class="filter-group">
        <label>Assignee</label>
        <select v-model="selectedAssignee" class="filter-select">
          <option value="">All Assignees</option>
          <option v-for="user in users" :key="user.id" :value="user.id">
            {{ user.name }}
          </option>
        </select>
      </div>
    </div>

    <div class="tickets-grid">
      <div
        v-for="ticket in filteredTickets"
        :key="ticket.id"
        class="ticket-card"
      >
        <div class="ticket-header">
          <span class="ticket-id">#{{ ticket.id }}</span>
          <span class="ticket-status" :class="ticket.status.toLowerCase()">
            {{ ticket.status }}
          </span>
        </div>
        <h3 class="ticket-title">{{ ticket.title }}</h3>
        <p class="ticket-description">{{ ticket.description }}</p>
        <div class="ticket-meta">
          <div class="meta-item">
            <font-awesome-icon :icon="['fas', 'user']" class="icon" />
            <span>{{ ticket.assignee?.name || "Unassigned" }}</span>
          </div>
          <div class="meta-item">
            <font-awesome-icon :icon="['fas', 'clock']" class="icon" />
            <span>{{ formatDate(ticket.createdAt) }}</span>
          </div>
        </div>
        <div class="ticket-actions">
          <button class="action-button" @click="handleEditTicketModal(ticket)">
            <font-awesome-icon :icon="['fas', 'edit']" class="icon" />
          </button>
          <button
            class="action-button"
            @click="handleAssignTicketModal(ticket)"
          >
            <font-awesome-icon :icon="['fas', 'user-plus']" class="icon" />
          </button>
          <button
            class="action-button"
            @click="handleUpdateStatusModal(ticket)"
          >
            <font-awesome-icon :icon="['fas', 'sync']" class="icon" />
          </button>
        </div>
      </div>
    </div>

    <!-- Create Ticket Modal -->
    <div v-if="showCreateTicketModal" class="modal">
      <div class="modal-content">
        <h3>Create New Ticket</h3>
        <input
          v-model="newTicket.title"
          type="text"
          placeholder="Title"
          class="modal-input"
        />
        <textarea
          v-model="newTicket.description"
          placeholder="Description"
          class="modal-textarea"
        ></textarea>
        <select v-model="newTicket.teamId" class="modal-select">
          <option value="">Select Team</option>
          <option v-for="team in teams" :key="team.id" :value="team.id">
            {{ team.name }}
          </option>
        </select>
        <select v-model="newTicket.flowId" class="modal-select">
          <option value="">Select Flow (Optional)</option>
          <option v-for="flow in flows" :key="flow.id" :value="flow.id">
            {{ flow.name }}
          </option>
        </select>
        <div class="modal-actions">
          <button @click="createTicket" class="modal-button primary">
            Create
          </button>
          <button @click="showCreateTicketModal = false" class="modal-button">
            Cancel
          </button>
        </div>
      </div>
    </div>

    <!-- Edit Ticket Modal -->
    <div v-if="showEditTicketModal" class="modal">
      <div class="modal-content">
        <h3>Edit Ticket</h3>
        <input
          v-model="selectedTicket.title"
          type="text"
          placeholder="Title"
          class="modal-input"
        />
        <textarea
          v-model="selectedTicket.description"
          placeholder="Description"
          class="modal-textarea"
        ></textarea>
        <div class="modal-actions">
          <button @click="updateTicket" class="modal-button primary">
            Save
          </button>
          <button @click="showEditTicketModal = false" class="modal-button">
            Cancel
          </button>
        </div>
      </div>
    </div>

    <!-- Assign Ticket Modal -->
    <div v-if="showAssignTicketModal" class="modal">
      <div class="modal-content">
        <h3>Assign Ticket</h3>
        <select v-model="selectedAssignee" class="modal-select">
          <option value="">Select Assignee</option>
          <option v-for="user in users" :key="user.id" :value="user.id">
            {{ user.name }}
          </option>
        </select>
        <div class="modal-actions">
          <button @click="assignTicket" class="modal-button primary">
            Assign
          </button>
          <button @click="showAssignTicketModal = false" class="modal-button">
            Cancel
          </button>
        </div>
      </div>
    </div>

    <!-- Update Status Modal -->
    <div v-if="showUpdateStatusModal" class="modal">
      <div class="modal-content">
        <h3>Update Status</h3>
        <select v-model="newStatus" class="modal-select">
          <option value="TODO">To Do</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="DONE">Done</option>
          <option value="BLOCKED">Blocked</option>
        </select>
        <div class="modal-actions">
          <button @click="updateStatus" class="modal-button primary">
            Update
          </button>
          <button @click="showUpdateStatusModal = false" class="modal-button">
            Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from "vue";
import axios from "axios";
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import {
  faPlus,
  faEdit,
  faUserPlus,
  faSync,
  faUser,
  faClock,
} from "@fortawesome/free-solid-svg-icons";

library.add(faPlus, faEdit, faUserPlus, faSync, faUser, faClock);

export default {
  components: {
    FontAwesomeIcon,
  },
  setup() {
    const tickets = ref([]);
    const teams = ref([]);
    const flows = ref([]);
    const users = ref([]);
    const selectedTeam = ref("");
    const selectedStatus = ref("");
    const selectedAssignee = ref("");
    const showCreateTicketModal = ref(false);
    const showEditTicketModal = ref(false);
    const showAssignTicketModal = ref(false);
    const showUpdateStatusModal = ref(false);
    const selectedTicket = ref(null);
    const newStatus = ref("");
    const newTicket = ref({
      title: "",
      description: "",
      teamId: "",
      flowId: "",
    });

    const fetchTickets = async () => {
      try {
        const response = await axios.get("/api/tickets");
        tickets.value = response.data;
      } catch (error) {
        console.error("Failed to fetch tickets:", error);
      }
    };

    const fetchTeams = async () => {
      try {
        const response = await axios.get("/api/teams");
        teams.value = response.data;
      } catch (error) {
        console.error("Failed to fetch teams:", error);
      }
    };

    const fetchFlows = async () => {
      try {
        const response = await axios.get("/api/flows");
        flows.value = response.data;
      } catch (error) {
        console.error("Failed to fetch flows:", error);
      }
    };

    const fetchUsers = async () => {
      try {
        const response = await axios.get("/api/users");
        users.value = response.data;
      } catch (error) {
        console.error("Failed to fetch users:", error);
      }
    };

    const createTicket = async () => {
      try {
        await axios.post("/api/tickets/create", null, {
          params: {
            teamId: newTicket.value.teamId,
            title: newTicket.value.title,
            description: newTicket.value.description,
            flowId: newTicket.value.flowId,
          },
        });
        newTicket.value = {
          title: "",
          description: "",
          teamId: "",
          flowId: "",
        };
        showCreateTicketModal.value = false;
        await fetchTickets();
      } catch (error) {
        console.error("Failed to create ticket:", error);
      }
    };

    const handleEditTicketModal = (ticket) => {
      selectedTicket.value = { ...ticket };
      showEditTicketModal.value = true;
    };

    const updateTicket = async () => {
      try {
        await axios.put(`/api/tickets/${selectedTicket.value.id}`, null, {
          params: {
            title: selectedTicket.value.title,
            description: selectedTicket.value.description,
          },
        });
        showEditTicketModal.value = false;
        await fetchTickets();
      } catch (error) {
        console.error("Failed to update ticket:", error);
      }
    };

    const handleAssignTicketModal = (ticket) => {
      selectedTicket.value = ticket;
      showAssignTicketModal.value = true;
    };

    const assignTicket = async () => {
      try {
        await axios.post(
          `/api/tickets/${selectedTicket.value.id}/assign`,
          null,
          {
            params: { username: selectedAssignee.value },
          }
        );
        showAssignTicketModal.value = false;
        await fetchTickets();
      } catch (error) {
        console.error("Failed to assign ticket:", error);
      }
    };

    const handleUpdateStatusModal = (ticket) => {
      selectedTicket.value = ticket;
      showUpdateStatusModal.value = true;
    };

    const updateStatus = async () => {
      try {
        await axios.post(
          `/api/tickets/${selectedTicket.value.id}/status`,
          null,
          {
            params: { newStatus: newStatus.value },
          }
        );
        showUpdateStatusModal.value = false;
        await fetchTickets();
      } catch (error) {
        console.error("Failed to update status:", error);
      }
    };

    const formatDate = (dateString) => {
      return new Date(dateString).toLocaleDateString();
    };

    const filteredTickets = computed(() => {
      return tickets.value.filter((ticket) => {
        const teamMatch =
          !selectedTeam.value || ticket.team.id === selectedTeam.value;
        const statusMatch =
          !selectedStatus.value || ticket.status === selectedStatus.value;
        const assigneeMatch =
          !selectedAssignee.value ||
          ticket.assignee?.id === selectedAssignee.value;
        return teamMatch && statusMatch && assigneeMatch;
      });
    });

    onMounted(() => {
      fetchTickets();
      fetchTeams();
      fetchFlows();
      fetchUsers();
    });

    return {
      tickets,
      teams,
      flows,
      users,
      selectedTeam,
      selectedStatus,
      selectedAssignee,
      showCreateTicketModal,
      showEditTicketModal,
      showAssignTicketModal,
      showUpdateStatusModal,
      selectedTicket,
      newStatus,
      newTicket,
      createTicket,
      handleEditTicketModal,
      updateTicket,
      handleAssignTicketModal,
      assignTicket,
      handleUpdateStatusModal,
      updateStatus,
      formatDate,
      filteredTickets,
    };
  },
};
</script>

<style scoped>
.tickets-container {
  padding: 20px;
}

.tickets-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.tickets-header h2 {
  font-size: 1.8rem;
  color: #ffffff;
}

.create-ticket-button {
  display: flex;
  align-items: center;
  padding: 10px 20px;
  background-color: #404040;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.create-ticket-button:hover {
  background-color: #505050;
}

.create-ticket-button .icon {
  margin-right: 8px;
}

.tickets-filters {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}

.filter-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.filter-group label {
  color: #b3b3b3;
  font-size: 0.9rem;
}

.filter-select {
  padding: 8px 12px;
  background-color: #404040;
  border: 1px solid #505050;
  border-radius: 6px;
  color: #ffffff;
  min-width: 150px;
}

.filter-select:focus {
  outline: none;
  border-color: #007bff;
}

.tickets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.ticket-card {
  background-color: #2d2d2d;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #404040;
}

.ticket-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.ticket-id {
  color: #b3b3b3;
  font-size: 0.9rem;
}

.ticket-status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 500;
}

.ticket-status.todo {
  background-color: #404040;
  color: #ffffff;
}

.ticket-status.in_progress {
  background-color: #007bff;
  color: #ffffff;
}

.ticket-status.done {
  background-color: #28a745;
  color: #ffffff;
}

.ticket-status.blocked {
  background-color: #dc3545;
  color: #ffffff;
}

.ticket-title {
  color: #ffffff;
  font-size: 1.1rem;
  margin-bottom: 10px;
}

.ticket-description {
  color: #b3b3b3;
  font-size: 0.9rem;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.ticket-meta {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #b3b3b3;
  font-size: 0.9rem;
}

.meta-item .icon {
  color: #b3b3b3;
}

.ticket-actions {
  display: flex;
  gap: 10px;
}

.action-button {
  background: none;
  border: none;
  color: #b3b3b3;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.action-button:hover {
  background-color: #404040;
  color: #ffffff;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #2d2d2d;
  padding: 30px;
  border-radius: 12px;
  width: 100%;
  max-width: 500px;
}

.modal-content h3 {
  color: #ffffff;
  margin-bottom: 20px;
  font-size: 1.4rem;
}

.modal-input,
.modal-textarea,
.modal-select {
  width: 100%;
  padding: 12px;
  background-color: #404040;
  border: 1px solid #505050;
  border-radius: 8px;
  color: #ffffff;
  margin-bottom: 20px;
}

.modal-textarea {
  min-height: 100px;
  resize: vertical;
}

.modal-input:focus,
.modal-textarea:focus,
.modal-select:focus {
  outline: none;
  border-color: #007bff;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.modal-button {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.modal-button.primary {
  background-color: #007bff;
  color: #ffffff;
}

.modal-button.primary:hover {
  background-color: #0056b3;
}

.modal-button:not(.primary) {
  background-color: #404040;
  color: #ffffff;
}

.modal-button:not(.primary):hover {
  background-color: #505050;
}
</style>
