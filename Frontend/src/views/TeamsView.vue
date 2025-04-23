<template>
  <div class="teams-container">
    <div class="teams-header">
      <h2>Teams</h2>
      <button class="create-team-button" @click="showCreateTeamModal = true">
        <font-awesome-icon :icon="['fas', 'plus']" class="icon" />
        Create Team
      </button>
    </div>

    <div class="teams-grid">
      <div v-for="team in teams" :key="team.id" class="team-card">
        <div class="team-header">
          <h3>{{ team.name }}</h3>
          <div class="team-actions">
            <button class="action-button" @click="handleInviteModal(team)">
              <font-awesome-icon :icon="['fas', 'user-plus']" class="icon" />
            </button>
            <button class="action-button" @click="handleManageRolesModal(team)">
              <font-awesome-icon :icon="['fas', 'users-cog']" class="icon" />
            </button>
          </div>
        </div>
        <div class="team-members">
          <div
            v-for="member in team.members"
            :key="member.id"
            class="member-item"
          >
            <img
              :src="member.profilePictureUrl"
              :alt="member.name"
              class="member-avatar"
            />
            <span class="member-name">{{ member.name }}</span>
            <span class="member-role">{{ member.role }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Team Modal -->
    <div v-if="showCreateTeamModal" class="modal">
      <div class="modal-content">
        <h3>Create New Team</h3>
        <input
          v-model="newTeamName"
          type="text"
          placeholder="Team Name"
          class="modal-input"
        />
        <div class="modal-actions">
          <button @click="createTeam" class="modal-button primary">
            Create
          </button>
          <button @click="showCreateTeamModal = false" class="modal-button">
            Cancel
          </button>
        </div>
      </div>
    </div>

    <!-- Invite Member Modal -->
    <div v-if="showInviteModal" class="modal">
      <div class="modal-content">
        <h3>Invite Member to {{ selectedTeam?.name }}</h3>
        <input
          v-model="inviteUsername"
          type="text"
          placeholder="Username"
          class="modal-input"
        />
        <div class="modal-actions">
          <button @click="inviteMember" class="modal-button primary">
            Invite
          </button>
          <button @click="showInviteModal = false" class="modal-button">
            Cancel
          </button>
        </div>
      </div>
    </div>

    <!-- Manage Roles Modal -->
    <div v-if="showManageRolesModal" class="modal">
      <div class="modal-content">
        <h3>Manage Roles in {{ selectedTeam?.name }}</h3>
        <div class="roles-list">
          <div
            v-for="member in selectedTeam?.members"
            :key="member.id"
            class="role-item"
          >
            <span class="member-name">{{ member.name }}</span>
            <select v-model="member.role" class="role-select">
              <option value="OWNER">Owner</option>
              <option value="MANAGER">Manager</option>
              <option value="MEMBER">Member</option>
            </select>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="updateRoles" class="modal-button primary">
            Save
          </button>
          <button @click="showManageRolesModal = false" class="modal-button">
            Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import axios from "axios";
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import {
  faPlus,
  faUserPlus,
  faUsersCog,
} from "@fortawesome/free-solid-svg-icons";

library.add(faPlus, faUserPlus, faUsersCog);

export default {
  components: {
    FontAwesomeIcon,
  },
  setup() {
    const teams = ref([]);
    const showCreateTeamModal = ref(false);
    const showInviteModal = ref(false);
    const showManageRolesModal = ref(false);
    const newTeamName = ref("");
    const inviteUsername = ref("");
    const selectedTeam = ref(null);

    const fetchTeams = async () => {
      try {
        const response = await axios.get("/api/teams");
        teams.value = response.data;
      } catch (error) {
        console.error("Failed to fetch teams:", error);
      }
    };

    const createTeam = async () => {
      try {
        await axios.post("/api/teams/create", null, {
          params: { teamName: newTeamName.value },
        });
        newTeamName.value = "";
        showCreateTeamModal.value = false;
        await fetchTeams();
      } catch (error) {
        console.error("Failed to create team:", error);
      }
    };

    const handleInviteModal = (team) => {
      selectedTeam.value = team;
      showInviteModal.value = true;
    };

    const inviteMember = async () => {
      try {
        await axios.post(`/api/teams/${selectedTeam.value.id}/invite`, null, {
          params: { username: inviteUsername.value },
        });
        inviteUsername.value = "";
        showInviteModal.value = false;
        await fetchTeams();
      } catch (error) {
        console.error("Failed to invite member:", error);
      }
    };

    const handleManageRolesModal = (team) => {
      selectedTeam.value = team;
      showManageRolesModal.value = true;
    };

    const updateRoles = async () => {
      try {
        for (const member of selectedTeam.value.members) {
          await axios.post("/api/team-roles/grant", null, {
            params: {
              teamId: selectedTeam.value.id,
              username: member.username,
              roleType: member.role,
            },
          });
        }
        showManageRolesModal.value = false;
        await fetchTeams();
      } catch (error) {
        console.error("Failed to update roles:", error);
      }
    };

    onMounted(fetchTeams);

    return {
      teams,
      showCreateTeamModal,
      showInviteModal,
      showManageRolesModal,
      newTeamName,
      inviteUsername,
      selectedTeam,
      createTeam,
      handleInviteModal,
      inviteMember,
      handleManageRolesModal,
      updateRoles,
    };
  },
};
</script>

<style scoped>
.teams-container {
  padding: 20px;
}

.teams-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.teams-header h2 {
  font-size: 1.8rem;
  color: #ffffff;
}

.create-team-button {
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

.create-team-button:hover {
  background-color: #505050;
}

.create-team-button .icon {
  margin-right: 8px;
}

.teams-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.team-card {
  background-color: #2d2d2d;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #404040;
}

.team-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.team-header h3 {
  color: #ffffff;
  font-size: 1.2rem;
}

.team-actions {
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

.team-members {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.member-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px;
  background-color: #404040;
  border-radius: 8px;
}

.member-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  object-fit: cover;
}

.member-name {
  flex: 1;
  color: #ffffff;
}

.member-role {
  color: #b3b3b3;
  font-size: 0.9rem;
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

.modal-input:focus {
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

.roles-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 20px;
}

.role-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.role-select {
  padding: 8px;
  background-color: #404040;
  border: 1px solid #505050;
  border-radius: 6px;
  color: #ffffff;
  min-width: 120px;
}

.role-select:focus {
  outline: none;
  border-color: #007bff;
}
</style>
