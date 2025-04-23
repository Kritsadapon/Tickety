<template>
  <div class="team-invitations">
    <h2>Team Invitations</h2>

    <div v-if="loading" class="loading">
      <p>Loading invitations...</p>
    </div>

    <div v-else-if="invitations.length === 0" class="no-invitations">
      <p>You don't have any pending team invitations.</p>
    </div>

    <div v-else class="invitations-list">
      <div
        v-for="invitation in invitations"
        :key="invitation.id"
        class="invitation-card"
      >
        <div class="invitation-header">
          <h3>Invitation to {{ invitation.teamName }}</h3>
          <span class="invitation-date">
            {{ formatDate(invitation.invitationDate) }}
          </span>
        </div>

        <div class="invitation-details">
          <p>
            <strong>From:</strong> {{ invitation.inviterDisplayName }} ({{
              invitation.inviterUsername
            }})
          </p>
        </div>

        <div class="invitation-actions">
          <button
            @click="acceptInvitation(invitation.id)"
            class="accept-btn"
            :disabled="invitation.accepted || invitation.rejected"
          >
            Accept
          </button>
          <button
            @click="rejectInvitation(invitation.id)"
            class="reject-btn"
            :disabled="invitation.accepted || invitation.rejected"
          >
            Reject
          </button>
        </div>

        <div v-if="invitation.accepted" class="invitation-status accepted">
          <p>
            You accepted this invitation on
            {{ formatDate(invitation.responseDate) }}
          </p>
        </div>

        <div v-if="invitation.rejected" class="invitation-status rejected">
          <p>
            You rejected this invitation on
            {{ formatDate(invitation.responseDate) }}
          </p>
        </div>
      </div>
    </div>

    <div
      class="notification"
      :class="{
        show: showNotification,
        success: notificationType === 'success',
        error: notificationType === 'error',
      }"
    >
      {{ notificationMessage }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";

const invitations = ref([]);
const loading = ref(true);
const showNotification = ref(false);
const notificationMessage = ref("");
const notificationType = ref("success");

// Format date for display
const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleString();
};

// Show notification message
const showNotificationMessage = (message, type = "success") => {
  notificationMessage.value = message;
  notificationType.value = type;
  showNotification.value = true;
  setTimeout(() => {
    showNotification.value = false;
  }, 3000);
};

// Fetch pending invitations
const fetchInvitations = async () => {
  try {
    loading.value = true;
    const response = await axios.get("/api/teams/invitations/pending");
    invitations.value = response.data;
  } catch (error) {
    console.error("Error fetching invitations:", error);
    showNotificationMessage("Failed to load invitations", "error");
  } finally {
    loading.value = false;
  }
};

// Accept an invitation
const acceptInvitation = async (invitationId) => {
  try {
    await axios.post(`/api/teams/invitations/${invitationId}/accept`);
    showNotificationMessage("Invitation accepted successfully");
    // Refresh invitations list
    fetchInvitations();
    // Optionally redirect to the team page
    // router.push(`/teams/${teamId}`);
  } catch (error) {
    console.error("Error accepting invitation:", error);
    showNotificationMessage("Failed to accept invitation", "error");
  }
};

// Reject an invitation
const rejectInvitation = async (invitationId) => {
  try {
    await axios.post(`/api/teams/invitations/${invitationId}/reject`);
    showNotificationMessage("Invitation rejected");
    // Refresh invitations list
    fetchInvitations();
  } catch (error) {
    console.error("Error rejecting invitation:", error);
    showNotificationMessage("Failed to reject invitation", "error");
  }
};

// Load invitations when component mounts
onMounted(() => {
  fetchInvitations();
});
</script>

<style scoped>
.team-invitations {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.loading,
.no-invitations {
  text-align: center;
  padding: 20px;
  color: #666;
}

.invitations-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.invitation-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.invitation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.invitation-header h3 {
  margin: 0;
  color: #333;
}

.invitation-date {
  color: #666;
  font-size: 0.9em;
}

.invitation-details {
  margin-bottom: 15px;
}

.invitation-actions {
  display: flex;
  gap: 10px;
  margin-top: 15px;
}

.accept-btn,
.reject-btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.accept-btn {
  background-color: #4caf50;
  color: white;
}

.accept-btn:hover {
  background-color: #45a049;
}

.reject-btn {
  background-color: #f44336;
  color: white;
}

.reject-btn:hover {
  background-color: #d32f2f;
}

.accept-btn:disabled,
.reject-btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.invitation-status {
  margin-top: 15px;
  padding: 10px;
  border-radius: 4px;
}

.invitation-status.accepted {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.invitation-status.rejected {
  background-color: #ffebee;
  color: #c62828;
}

.notification {
  position: fixed;
  bottom: 20px;
  right: 20px;
  padding: 15px 20px;
  border-radius: 4px;
  background-color: #4caf50;
  color: white;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  transform: translateY(100px);
  opacity: 0;
  transition: all 0.3s ease;
}

.notification.show {
  transform: translateY(0);
  opacity: 1;
}

.notification.error {
  background-color: #f44336;
}
</style>
