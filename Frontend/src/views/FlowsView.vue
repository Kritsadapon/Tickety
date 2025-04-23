<template>
  <div class="flows-container">
    <div class="flows-header">
      <h2>Ticket Flows</h2>
      <button class="create-flow-button" @click="showCreateFlowModal = true">
        <font-awesome-icon :icon="['fas', 'plus']" class="icon" />
        Create Flow
      </button>
    </div>

    <div class="flows-grid">
      <div v-for="flow in flows" :key="flow.id" class="flow-card">
        <div class="flow-header">
          <h3>{{ flow.name }}</h3>
          <div class="flow-actions">
            <button class="action-button" @click="handleEditFlowModal(flow)">
              <font-awesome-icon :icon="['fas', 'edit']" class="icon" />
            </button>
            <button class="action-button" @click="handleDeleteFlowModal(flow)">
              <font-awesome-icon :icon="['fas', 'trash']" class="icon" />
            </button>
          </div>
        </div>
        <p class="flow-description">{{ flow.description }}</p>
        <div class="flow-tickets">
          <div class="ticket-column">
            <h4>TODO</h4>
            <div class="ticket-list">
              <div
                v-for="ticket in getTicketsByStatus(flow, 'TODO')"
                :key="ticket.id"
                class="ticket-item"
                @click="showTicketDetails(ticket)"
              >
                <span class="ticket-title">{{ ticket.title }}</span>
                <span class="ticket-assignee">{{ ticket.assignee?.name }}</span>
              </div>
            </div>
          </div>
          <div class="ticket-column">
            <h4>IN PROGRESS</h4>
            <div class="ticket-list">
              <div
                v-for="ticket in getTicketsByStatus(flow, 'IN_PROGRESS')"
                :key="ticket.id"
                class="ticket-item"
                @click="showTicketDetails(ticket)"
              >
                <span class="ticket-title">{{ ticket.title }}</span>
                <span class="ticket-assignee">{{ ticket.assignee?.name }}</span>
              </div>
            </div>
          </div>
          <div class="ticket-column">
            <h4>DONE</h4>
            <div class="ticket-list">
              <div
                v-for="ticket in getTicketsByStatus(flow, 'DONE')"
                :key="ticket.id"
                class="ticket-item"
                @click="showTicketDetails(ticket)"
              >
                <span class="ticket-title">{{ ticket.title }}</span>
                <span class="ticket-assignee">{{ ticket.assignee?.name }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Flow Modal -->
    <div v-if="showCreateFlowModal" class="modal">
      <div class="modal-content">
        <h3>Create New Flow</h3>
        <input
          v-model="newFlowName"
          type="text"
          placeholder="Flow Name"
          class="modal-input"
        />
        <textarea
          v-model="newFlowDescription"
          placeholder="Flow Description"
          class="modal-textarea"
        ></textarea>
        <div class="modal-actions">
          <button @click="createFlow" class="modal-button primary">
            Create
          </button>
          <button @click="showCreateFlowModal = false" class="modal-button">
            Cancel
          </button>
        </div>
      </div>
    </div>

    <!-- Edit Flow Modal -->
    <div v-if="showEditFlowModal" class="modal">
      <div class="modal-content">
        <h3>Edit Flow</h3>
        <input
          v-model="selectedFlow.name"
          type="text"
          placeholder="Flow Name"
          class="modal-input"
        />
        <textarea
          v-model="selectedFlow.description"
          placeholder="Flow Description"
          class="modal-textarea"
        ></textarea>
        <div class="modal-actions">
          <button @click="updateFlow" class="modal-button primary">Save</button>
          <button @click="showEditFlowModal = false" class="modal-button">
            Cancel
          </button>
        </div>
      </div>
    </div>

    <!-- Delete Flow Modal -->
    <div v-if="showDeleteFlowModal" class="modal">
      <div class="modal-content">
        <h3>Delete Flow</h3>
        <p>Are you sure you want to delete "{{ selectedFlow?.name }}"?</p>
        <div class="modal-actions">
          <button @click="deleteFlow" class="modal-button danger">
            Delete
          </button>
          <button @click="showDeleteFlowModal = false" class="modal-button">
            Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { useStore } from "vuex";
import axios from "axios";
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { faPlus, faEdit, faTrash } from "@fortawesome/free-solid-svg-icons";

library.add(faPlus, faEdit, faTrash);

export default {
  components: {
    FontAwesomeIcon,
  },
  setup() {
    const store = useStore();
    const flows = ref([]);
    const showCreateFlowModal = ref(false);
    const showEditFlowModal = ref(false);
    const showDeleteFlowModal = ref(false);
    const newFlowName = ref("");
    const newFlowDescription = ref("");
    const selectedFlow = ref(null);

    const fetchFlows = async () => {
      try {
        const response = await axios.get("/api/flows");
        flows.value = response.data;
      } catch (error) {
        console.error("Failed to fetch flows:", error);
      }
    };

    const createFlow = async () => {
      try {
        await axios.post("/api/flows/create", null, {
          params: {
            teamId: store.state.currentTeamId,
            name: newFlowName.value,
            description: newFlowDescription.value,
          },
        });
        newFlowName.value = "";
        newFlowDescription.value = "";
        showCreateFlowModal.value = false;
        await fetchFlows();
      } catch (error) {
        console.error("Failed to create flow:", error);
      }
    };

    const handleEditFlowModal = (flow) => {
      selectedFlow.value = { ...flow };
      showEditFlowModal.value = true;
    };

    const updateFlow = async () => {
      try {
        await axios.put(`/api/flows/${selectedFlow.value.id}`, null, {
          params: {
            name: selectedFlow.value.name,
            description: selectedFlow.value.description,
          },
        });
        showEditFlowModal.value = false;
        await fetchFlows();
      } catch (error) {
        console.error("Failed to update flow:", error);
      }
    };

    const handleDeleteFlowModal = (flow) => {
      selectedFlow.value = flow;
      showDeleteFlowModal.value = true;
    };

    const deleteFlow = async () => {
      try {
        await axios.delete(`/api/flows/${selectedFlow.value.id}`);
        showDeleteFlowModal.value = false;
        await fetchFlows();
      } catch (error) {
        console.error("Failed to delete flow:", error);
      }
    };

    const getTicketsByStatus = (flow, status) => {
      return flow.tickets?.filter((ticket) => ticket.status === status) || [];
    };

    const showTicketDetails = (ticket) => {
      // TODO: Implement ticket details view
      console.log("Show ticket details:", ticket);
    };

    onMounted(fetchFlows);

    return {
      flows,
      showCreateFlowModal,
      showEditFlowModal,
      showDeleteFlowModal,
      newFlowName,
      newFlowDescription,
      selectedFlow,
      createFlow,
      handleEditFlowModal,
      updateFlow,
      handleDeleteFlowModal,
      deleteFlow,
      getTicketsByStatus,
      showTicketDetails,
    };
  },
};
</script>

<style scoped>
.flows-container {
  padding: 20px;
}

.flows-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.flows-header h2 {
  font-size: 1.8rem;
  color: #ffffff;
}

.create-flow-button {
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

.create-flow-button:hover {
  background-color: #505050;
}

.create-flow-button .icon {
  margin-right: 8px;
}

.flows-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(400px, 1fr));
  gap: 20px;
}

.flow-card {
  background-color: #2d2d2d;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #404040;
}

.flow-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.flow-header h3 {
  color: #ffffff;
  font-size: 1.2rem;
}

.flow-actions {
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

.flow-description {
  color: #b3b3b3;
  margin-bottom: 20px;
  font-size: 0.9rem;
}

.flow-tickets {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 15px;
}

.ticket-column {
  background-color: #404040;
  border-radius: 8px;
  padding: 15px;
}

.ticket-column h4 {
  color: #ffffff;
  margin-bottom: 15px;
  font-size: 1rem;
  text-align: center;
}

.ticket-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.ticket-item {
  background-color: #2d2d2d;
  padding: 10px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.ticket-item:hover {
  background-color: #505050;
}

.ticket-title {
  color: #ffffff;
  font-size: 0.9rem;
  display: block;
  margin-bottom: 5px;
}

.ticket-assignee {
  color: #b3b3b3;
  font-size: 0.8rem;
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

.modal-input {
  width: 100%;
  padding: 12px;
  background-color: #404040;
  border: 1px solid #505050;
  border-radius: 8px;
  color: #ffffff;
  margin-bottom: 20px;
}

.modal-textarea {
  width: 100%;
  padding: 12px;
  background-color: #404040;
  border: 1px solid #505050;
  border-radius: 8px;
  color: #ffffff;
  margin-bottom: 20px;
  min-height: 100px;
  resize: vertical;
}

.modal-input:focus,
.modal-textarea:focus {
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

.modal-button.danger {
  background-color: #dc3545;
  color: #ffffff;
}

.modal-button.danger:hover {
  background-color: #c82333;
}

.modal-button:not(.primary):not(.danger) {
  background-color: #404040;
  color: #ffffff;
}

.modal-button:not(.primary):not(.danger):hover {
  background-color: #505050;
}
</style>
