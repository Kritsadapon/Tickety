<template>
  <div class="edit-forum-container">
    <div class="edit-forum-header">
      <h1>Edit Forum</h1>
      <router-link to="/profile/activity" class="back-button">
        <font-awesome-icon :icon="['fas', 'arrow-left']" class="icon" />
        Back to Activity
      </router-link>
    </div>

    <div v-if="loading" class="loading">Loading forum details...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else class="edit-form">
      <div class="form-group">
        <label for="forumTitle">Forum Title</label>
        <input
          type="text"
          id="forumTitle"
          v-model="forum.forumTitle"
          class="form-control"
          placeholder="Enter forum title"
        />
      </div>

      <div class="form-group">
        <label for="forumDescription">Forum Description</label>
        <textarea
          id="forumDescription"
          v-model="forum.forumDescription"
          class="form-control"
          rows="5"
          placeholder="Enter forum description"
        ></textarea>
      </div>

      <div class="form-actions">
        <button class="cancel-button" @click="goBack">Cancel</button>
        <button class="save-button" @click="saveForum" :disabled="saving">
          {{ saving ? "Saving..." : "Save Changes" }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import axios from "axios";
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import { faArrowLeft } from "@fortawesome/free-solid-svg-icons";

library.add(faArrowLeft);

export default {
  components: {
    FontAwesomeIcon,
  },
  setup() {
    const router = useRouter();
    const route = useRoute();
    const loading = ref(true);
    const saving = ref(false);
    const error = ref(null);
    const forum = ref({
      forumTitle: "",
      forumDescription: "",
    });

    const fetchForum = async () => {
      try {
        const response = await axios.get(`/api/forums/${route.params.id}`);
        forum.value = response.data;
      } catch (err) {
        console.error("Error fetching forum:", err);
        error.value = "Failed to load forum details. Please try again.";
      } finally {
        loading.value = false;
      }
    };

    const saveForum = async () => {
      if (!forum.value.forumTitle.trim()) {
        alert("Please enter a forum title");
        return;
      }

      saving.value = true;
      try {
        await axios.put(`/api/forums/update/${route.params.id}`, forum.value);
        router.push("/profile/activity");
      } catch (err) {
        console.error("Error updating forum:", err);
        if (err.response?.status === 403) {
          alert("You are not authorized to edit this forum.");
        } else {
          alert("Failed to update forum. Please try again.");
        }
      } finally {
        saving.value = false;
      }
    };

    const goBack = () => {
      router.push("/profile/activity");
    };

    onMounted(fetchForum);

    return {
      loading,
      saving,
      error,
      forum,
      saveForum,
      goBack,
    };
  },
};
</script>

<style scoped>
.edit-forum-container {
  max-width: 800px;
  margin: 40px auto;
  padding: 20px;
}

.edit-forum-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.edit-forum-header h1 {
  font-size: 2.5rem;
  color: #2c3e50;
  margin: 0;
}

.back-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: #f8f9fa;
  border: none;
  border-radius: 8px;
  color: #2c3e50;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
}

.back-button:hover {
  background: #e9ecef;
  transform: translateX(-2px);
}

.edit-form {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #2c3e50;
}

.form-control {
  width: 100%;
  padding: 12px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.form-control:focus {
  outline: none;
  border-color: #18c6c6;
}

textarea.form-control {
  resize: vertical;
  min-height: 120px;
}

.form-actions {
  display: flex;
  gap: 16px;
  justify-content: flex-end;
  margin-top: 32px;
}

.cancel-button,
.save-button {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.cancel-button {
  background: #f8f9fa;
  color: #2c3e50;
}

.cancel-button:hover {
  background: #e9ecef;
}

.save-button {
  background: #18c6c6;
  color: white;
}

.save-button:hover {
  background: #15b3b3;
}

.save-button:disabled {
  background: #a8e6e6;
  cursor: not-allowed;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #666;
  font-size: 1.2rem;
}

.error {
  text-align: center;
  padding: 40px;
  color: #dc3545;
  font-size: 1.2rem;
}

@media (max-width: 768px) {
  .edit-forum-container {
    padding: 16px;
  }

  .edit-forum-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }

  .edit-form {
    padding: 20px;
  }
}
</style>
