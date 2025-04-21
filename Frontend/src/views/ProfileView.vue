<template>
  <div class="profile-container">
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
    <div class="profile-card">
      <div class="profile-left">
        <h1>User Profile</h1>
        <img :src="profilePictureUrl" alt="User Icon" class="profile-image" />
        <input
          type="file"
          accept="image/*"
          @change="uploadProfilePicture"
          class="upload-input"
          ref="fileInput"
        />
        <div class="change-photo-button" @click="triggerFileInput">
          <font-awesome-icon :icon="['fas', 'camera']" class="icon" />
          Change Photo
        </div>
      </div>
      <div class="profile-right">
        <div class="profile-details">
          <div class="profile-box">
            <p class="label">Display Name</p>
            <div class="box-container" :class="{ editing: editingDisplayName }">
              <input
                v-if="editingDisplayName"
                v-model="newDisplayName"
                class="box"
                ref="displayNameInputRef"
              />
              <div v-else class="box">{{ userDetails["Display Name"] }}</div>
              <button @click="toggleEditDisplayName" class="edit-button">
                <font-awesome-icon
                  :icon="['fas', editingDisplayName ? 'check' : 'pen']"
                  class="icon"
                />
              </button>
              <button
                v-if="editingDisplayName"
                @click="cancelEditDisplayName"
                class="cancel-button"
              >
                <font-awesome-icon :icon="['fas', 'times']" class="icon" />
              </button>
            </div>
          </div>
          <div class="profile-box">
            <p class="label">Password</p>
            <div class="box-container" :class="{ editing: editingPassword }">
              <input
                v-if="editingPassword"
                type="password"
                v-model="oldPassword"
                placeholder="Old Password"
                class="box"
                ref="oldPasswordInputRef"
              />
              <input
                v-if="editingPassword"
                type="password"
                v-model="newPassword"
                placeholder="New Password"
                class="box"
              />
              <div v-else class="box">********</div>
              <button @click="toggleEditPassword" class="edit-button">
                <font-awesome-icon
                  :icon="['fas', editingPassword ? 'check' : 'pen']"
                  class="icon"
                />
              </button>
              <button
                v-if="editingPassword"
                @click="cancelEditPassword"
                class="cancel-button"
              >
                <font-awesome-icon :icon="['fas', 'times']" class="icon" />
              </button>
            </div>
          </div>
          <router-link to="/profile/activity" class="activity-button">
            <font-awesome-icon :icon="['fas', 'history']" class="icon" />
            View Activity
          </router-link>
        </div>
        <button @click="logout" :disabled="loading" class="logout-button">
          <font-awesome-icon
            v-if="loading"
            :icon="['fas', 'hourglass-half']"
            class="icon"
          />
          <span v-else>Logout</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, ref, nextTick } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import axios from "axios";
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import {
  faCamera,
  faPen,
  faCheck,
  faHourglassHalf,
  faTimes,
} from "@fortawesome/free-solid-svg-icons";

// Add the icons to the library
library.add(faCamera, faPen, faCheck, faHourglassHalf, faTimes);

export default {
  components: {
    FontAwesomeIcon,
  },
  setup() {
    const store = useStore();
    const router = useRouter();
    const loading = ref(false);
    const fileInput = ref(null);

    const userDetails = computed(() => ({
      "Display Name": store.state.name || "N/A",
      Password: "********",
    }));

    const profilePictureUrl = computed(() => {
      return store.state.profilePictureUrl || "/default-avatar.svg";
    });

    // Add function to fetch profile picture
    const fetchProfilePicture = async () => {
      try {
        const response = await axios.get("/api/users/profile-picture", {
          responseType: "blob",
        });
        const blob = new Blob([response.data], { type: "image/jpeg" });
        const url = URL.createObjectURL(blob);
        store.commit("setProfilePictureUrl", url);
      } catch (error) {
        console.error("Failed to fetch profile picture", error);
        store.commit("setProfilePictureUrl", "/default-avatar.svg");
      }
    };

    // Call fetchProfilePicture when component is mounted
    fetchProfilePicture();

    const editingDisplayName = ref(false);
    const newDisplayName = ref(store.state.name || "");
    const displayNameInputRef = ref(null);

    const editingPassword = ref(false);
    const oldPassword = ref("");
    const newPassword = ref("");
    const oldPasswordInputRef = ref(null);

    const showNotification = ref(false);
    const notificationMessage = ref("");
    const notificationType = ref("success");

    const showNotificationMessage = (message, type = "success") => {
      notificationMessage.value = message;
      notificationType.value = type;
      showNotification.value = true;
      setTimeout(() => {
        showNotification.value = false;
      }, 3000);
    };

    const toggleEditDisplayName = async () => {
      if (editingDisplayName.value) {
        try {
          await axios.post("/api/users/change-display-name", null, {
            params: { displayName: newDisplayName.value },
          });
          store.commit("setName", newDisplayName.value);
          showNotificationMessage("Display Name Change Successfully!");
        } catch (error) {
          console.error("Failed to update display name", error);
          showNotificationMessage(
            "Failed to update Display Name. Please try again.",
            "error"
          );
        }
      }
      editingDisplayName.value = !editingDisplayName.value;

      if (editingDisplayName.value) {
        nextTick(() => displayNameInputRef.value?.focus());
      }
    };

    const toggleEditPassword = async () => {
      if (editingPassword.value) {
        try {
          await axios.post("/api/users/change-password", null, {
            params: {
              oldPassword: oldPassword.value,
              newPassword: newPassword.value,
            },
          });
          oldPassword.value = "";
          newPassword.value = "";
          showNotificationMessage("Password Change Successfully!");
        } catch (error) {
          console.error("Failed to update password", error);
          showNotificationMessage(
            "Failed to update password. Please try again.",
            "error"
          );
        }
      }
      editingPassword.value = !editingPassword.value;

      if (editingPassword.value) {
        nextTick(() => oldPasswordInputRef.value?.focus());
      }
    };

    const logout = async () => {
      loading.value = true;
      try {
        await axios.get("/api/logout");
        store.dispatch("logout");
        router.push("/login");
      } catch (error) {
        console.error("Logout failed:", error);
      } finally {
        loading.value = false;
      }
    };

    const uploadProfilePicture = async (event) => {
      const file = event.target.files[0];
      if (!file) return;

      const formData = new FormData();
      formData.append("file", file);

      try {
        const response = await axios.put(
          "/api/users/profile-picture",
          formData,
          {
            headers: {
              "Content-Type": "multipart/form-data",
            },
          }
        );
        console.log("Upload response:", response.data);
        await fetchProfilePicture();
        showNotificationMessage("Photo Change Successfully!");
      } catch (error) {
        console.error("Failed to upload profile picture", error);
        showNotificationMessage(
          "Failed to upload profile picture. Please try again.",
          "error"
        );
      }
    };

    // Add function to trigger file input
    const triggerFileInput = () => {
      fileInput.value.click();
    };

    const cancelEditDisplayName = () => {
      editingDisplayName.value = false;
      newDisplayName.value = store.state.name || "";
    };

    const cancelEditPassword = () => {
      editingPassword.value = false;
      oldPassword.value = "";
      newPassword.value = "";
    };

    return {
      userDetails,
      logout,
      loading,
      editingDisplayName,
      newDisplayName,
      displayNameInputRef,
      toggleEditDisplayName,
      cancelEditDisplayName,
      editingPassword,
      oldPassword,
      newPassword,
      oldPasswordInputRef,
      toggleEditPassword,
      cancelEditPassword,
      uploadProfilePicture,
      profilePictureUrl,
      fileInput,
      triggerFileInput,
      showNotification,
      notificationMessage,
      notificationType,
    };
  },
};
</script>

<style scoped>
.profile-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 40px 20px;
}

.profile-card {
  display: flex;
  background: white;
  padding: 60px;
  border-radius: 20px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 1200px;
  min-height: 750px;
  transition: transform 0.3s ease;
  gap: 40px;
}

.profile-card:hover {
  transform: translateY(-5px);
}

.profile-left {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: flex-start;
  width: 40%;
  position: relative;
  padding-top: 30px;
}

.profile-left h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin-bottom: 40px;
  font-weight: 600;
  text-align: center;
  width: 100%;
}

.profile-image {
  width: 250px;
  height: 250px;
  border-radius: 50%;
  object-fit: cover;
  margin-bottom: 20px;
  border: 4px solid #fff;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.upload-input {
  display: none !important;
  visibility: hidden;
  position: absolute;
  width: 0;
  height: 0;
  opacity: 0;
  pointer-events: none;
}

.upload-input::-webkit-file-upload-button {
  display: none;
}

.upload-input::file-selector-button {
  display: none;
}

.upload-input::before {
  display: none;
}

.upload-input::after {
  display: none;
}

.change-photo-button {
  background: rgba(0, 123, 255, 0.9);
  color: white;
  padding: 10px 20px;
  border-radius: 25px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(0, 123, 255, 0.3);
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 140px;
  margin-bottom: 40px;
}

.change-photo-button:hover {
  background: rgba(0, 123, 255, 1);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 123, 255, 0.4);
}

.profile-right {
  width: 60%;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding-top: 30px;
}

.profile-right h1 {
  display: none;
}

.profile-details {
  display: flex;
  flex-direction: column;
  gap: 35px;
  width: 100%;
  max-width: 500px;
  margin-top: 80px;
}

.profile-box {
  text-align: left;
  width: 100%;
}

.label {
  font-weight: 600;
  font-size: 1.2rem;
  color: #2c3e50;
  margin-bottom: 12px;
  text-align: left;
}

.box-container {
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 15px;
  transition: all 0.3s ease;
  width: 100%;
  position: relative;
}

.box-container:hover {
  background: #f1f3f5;
}

.box-container.editing {
  background: #fff;
  border: 2px solid #007bff;
  box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}

.box {
  flex: 1;
  font-size: 1.1rem;
  color: #2c3e50;
  border: none;
  background: transparent;
  padding: 8px 12px;
  border-radius: 8px;
  width: 100%;
  text-align: left;
  padding-right: 45px;
}

.box:focus {
  outline: none;
  background: #fff;
}

.edit-button {
  background: none;
  border: none;
  font-size: 1.1rem;
  cursor: pointer;
  color: #007bff;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.2s ease;
  position: absolute;
  right: 45px;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.edit-button:hover {
  background: rgba(0, 123, 255, 0.1);
  color: #0056b3;
}

.cancel-button {
  background: none;
  border: none;
  font-size: 1.1rem;
  cursor: pointer;
  color: #dc3545;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.2s ease;
  position: absolute;
  right: 5px;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cancel-button:hover {
  background: rgba(220, 53, 69, 0.1);
  color: #c82333;
}

.logout-button {
  margin-top: 40px;
  width: 100%;
  max-width: 500px;
  padding: 15px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff4757 100%);
  color: white;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 1.1rem;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.3);
}

.logout-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.4);
}

.logout-button:disabled {
  background: #ccc;
  transform: none;
  box-shadow: none;
}

.icon {
  margin-right: 8px;
  font-size: 1rem;
}

.change-photo-button .icon {
  margin-right: 8px;
  font-size: 0.9rem;
}

.edit-button .icon {
  font-size: 1rem;
}

.logout-button .icon {
  margin-right: 8px;
  font-size: 1rem;
}

@media (max-width: 768px) {
  .profile-card {
    flex-direction: column;
    padding: 30px;
    align-items: center;
  }

  .profile-left,
  .profile-right {
    width: 100%;
    align-items: center;
    padding-top: 0;
  }

  .profile-left {
    margin-bottom: 30px;
  }

  .profile-image {
    width: 200px;
    height: 200px;
  }

  .change-photo-button {
    margin-bottom: 30px;
  }

  .profile-details {
    margin-top: 40px;
  }

  .profile-box {
    width: 100%;
    max-width: 400px;
  }
}

.notification {
  position: fixed;
  top: 20px;
  right: 20px;
  padding: 15px 25px;
  border-radius: 8px;
  color: white;
  font-weight: 500;
  transform: translateX(120%);
  transition: transform 0.3s ease-in-out;
  z-index: 1000;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.notification.show {
  transform: translateX(0);
}

.notification.success {
  background-color: #4caf50;
}

.notification.error {
  background-color: #f44336;
}

.activity-button {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #18c6c6;
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
  margin-top: 20px;
  width: 100%;
  max-width: 500px;
}

.activity-button:hover {
  background: #15b3b3;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 198, 198, 0.2);
}

.activity-button .icon {
  font-size: 1.1rem;
}
</style>
