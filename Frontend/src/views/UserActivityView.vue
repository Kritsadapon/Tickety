<template>
  <div class="activity-container">
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

    <!-- Confirmation Dialog -->
    <div v-if="showConfirmation" class="confirmation-dialog-overlay">
      <div class="confirmation-dialog">
        <h3>{{ confirmationTitle }}</h3>
        <p>{{ confirmationMessage }}</p>
        <div class="confirmation-buttons">
          <button class="cancel-button" @click="cancelConfirmation">
            Cancel
          </button>
          <button class="confirm-button" @click="confirmAction">Confirm</button>
        </div>
      </div>
    </div>

    <div class="activity-header">
      <h1>Your Activity</h1>
      <router-link to="/profile" class="back-button">
        <font-awesome-icon :icon="['fas', 'arrow-left']" class="icon" />
        Back to Profile
      </router-link>
    </div>

    <div v-if="loading" class="loading">Loading your activity...</div>
    <div v-else class="activity-content">
      <!-- Followed Forums Section -->
      <div class="activity-section">
        <h2>Followed Forums</h2>
        <div v-if="followedForums.length > 0" class="forum-list">
          <div
            v-for="forum in followedForums"
            :key="forum.forum.forumid"
            class="forum-card"
            @click="goToForum(forum.forum.forumid)"
          >
            <h3>{{ forum.forum.forumTitle }}</h3>
            <p class="description">{{ forum.forum.forumDescription }}</p>
            <div class="meta">
              <span class="date">
                <font-awesome-icon :icon="['far', 'calendar']" class="icon" />
                {{ formatDate(forum.forum.forumDateTime) }}
              </span>
              <span class="creator">
                <font-awesome-icon :icon="['far', 'user']" class="icon" />
                {{ forum.forum.user?.username || "Anonymous" }}
              </span>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <font-awesome-icon :icon="['far', 'bookmark']" class="icon" />
          <p>You haven't followed any forums yet.</p>
        </div>
      </div>

      <!-- User Forums Section -->
      <div class="activity-section">
        <h2>Your Forums</h2>
        <div v-if="userForums.length > 0" class="forum-list">
          <div
            v-for="forum in userForums"
            :key="forum.forumid"
            class="forum-card"
            @click="goToForum(forum.forumid)"
          >
            <h3>{{ forum.forumTitle }}</h3>
            <p class="description">{{ forum.forumDescription }}</p>
            <div class="meta">
              <span class="date">
                <font-awesome-icon :icon="['far', 'calendar']" class="icon" />
                {{ formatDate(forum.forumDateTime) }}
              </span>
              <div class="forum-actions">
                <button
                  class="action-button delete"
                  @click.stop="deleteForum(forum.forumid)"
                >
                  <font-awesome-icon
                    :icon="['far', 'trash-alt']"
                    class="icon"
                  />
                  Delete
                </button>
                <button
                  class="action-button edit"
                  @click.stop="editForum(forum)"
                >
                  <font-awesome-icon :icon="['far', 'edit']" class="icon" />
                  Edit
                </button>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <font-awesome-icon :icon="['far', 'comments']" class="icon" />
          <p>You haven't created any forums yet.</p>
        </div>
      </div>

      <!-- User Posts Section -->
      <div class="activity-section">
        <h2>Your Posts</h2>
        <div v-if="userPosts.length > 0" class="post-list">
          <div
            v-for="post in userPosts"
            :key="post.postid"
            class="post-card"
            @click="goToPost(post.postid)"
          >
            <h3>{{ post.postTitle }}</h3>
            <p class="description">{{ post.postDescription }}</p>
            <div class="meta">
              <span class="forum">
                <font-awesome-icon :icon="['far', 'folder']" class="icon" />
                {{ post.forum?.forumTitle || "Unknown Forum" }}
              </span>
              <span class="date">
                <font-awesome-icon :icon="['far', 'calendar']" class="icon" />
                {{ formatDate(post.postDateTime) }}
              </span>
              <div class="post-actions">
                <button
                  class="action-button delete"
                  @click.stop="deletePost(post.postid)"
                >
                  <font-awesome-icon
                    :icon="['far', 'trash-alt']"
                    class="icon"
                  />
                  Delete
                </button>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="empty-state">
          <font-awesome-icon :icon="['far', 'file-alt']" class="icon" />
          <p>You haven't created any posts yet.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { useStore } from "vuex";
import axios from "axios";
import { library } from "@fortawesome/fontawesome-svg-core";
import { FontAwesomeIcon } from "@fortawesome/vue-fontawesome";
import {
  faArrowLeft,
  faCalendar,
  faUser,
  faFolder,
  faBookmark,
  faFileAlt,
  faComments,
  faEdit,
  faTrashAlt,
} from "@fortawesome/free-solid-svg-icons";

// Add the icons to the library
library.add(
  faArrowLeft,
  faCalendar,
  faUser,
  faFolder,
  faBookmark,
  faFileAlt,
  faComments,
  faEdit,
  faTrashAlt
);

export default {
  components: {
    FontAwesomeIcon,
  },
  setup() {
    const router = useRouter();
    const store = useStore();
    const loading = ref(true);
    const followedForums = ref([]);
    const userPosts = ref([]);
    const userForums = ref([]);
    const showNotification = ref(false);
    const notificationMessage = ref("");
    const notificationType = ref("success");

    // Confirmation dialog state
    const showConfirmation = ref(false);
    const confirmationTitle = ref("");
    const confirmationMessage = ref("");
    const pendingAction = ref(null);
    const pendingActionParams = ref(null);

    const showConfirmationDialog = (title, message, action, params) => {
      confirmationTitle.value = title;
      confirmationMessage.value = message;
      pendingAction.value = action;
      pendingActionParams.value = params;
      showConfirmation.value = true;
    };

    const cancelConfirmation = () => {
      showConfirmation.value = false;
      pendingAction.value = null;
      pendingActionParams.value = null;
    };

    const confirmAction = () => {
      if (pendingAction.value) {
        pendingAction.value(pendingActionParams.value);
      }
      showConfirmation.value = false;
      pendingAction.value = null;
      pendingActionParams.value = null;
    };

    const showNotificationMessage = (message, type = "success") => {
      notificationMessage.value = message;
      notificationType.value = type;
      showNotification.value = true;
      setTimeout(() => {
        showNotification.value = false;
      }, 3000);
    };

    const fetchUserActivity = async () => {
      try {
        // Check if user is logged in and has an ID
        if (!store.state.isLoggedIn || !store.state.id) {
          console.error("User not logged in or ID not available");
          router.push("/login");
          return;
        }

        // Fetch followed forums
        const forumsResponse = await axios.get("/api/forums/followed");
        followedForums.value = forumsResponse.data;

        // Fetch user posts
        const postsResponse = await axios.get(
          `/api/posts/user/${store.state.id}`
        );
        userPosts.value = postsResponse.data;

        // Fetch user forums
        const userForumsResponse = await axios.get(
          `/api/forums/user/${store.state.id}`
        );
        userForums.value = userForumsResponse.data;
      } catch (error) {
        console.error("Error fetching user activity:", error);
        if (error.response?.status === 401) {
          router.push("/login");
        }
      } finally {
        loading.value = false;
      }
    };

    const formatDate = (dateString) => {
      if (!dateString) return "Unknown date";
      const date = new Date(dateString);
      return date.toLocaleDateString("en-US", {
        year: "numeric",
        month: "long",
        day: "numeric",
      });
    };

    const goToForum = (forumId) => {
      router.push(`/forums/${forumId}`);
    };

    const goToPost = (postId) => {
      router.push(`/posts/${postId}`);
    };

    const deleteForum = async (forumId) => {
      showConfirmationDialog(
        "Delete Forum",
        "Are you sure you want to delete this forum?",
        async () => {
          try {
            // Get the forum details first to verify ownership
            const forumResponse = await axios.get(`/api/forums/${forumId}`, {
              withCredentials: true,
            });
            const forum = forumResponse.data;

            // Check if the current user is the creator
            if (forum.user.id !== store.state.id) {
              console.log("Debug - User IDs don't match:", {
                forumUserId: forum.user.id,
                currentUserId: store.state.id,
              });
              showNotificationMessage(
                "You can only delete forums that you created.",
                "error"
              );
              return;
            }

            await axios.delete(`/api/forums/delete/${forumId}`, {
              withCredentials: true,
            });

            // Refresh the user forums list
            try {
              const userForumsResponse = await axios.get(
                `/api/forums/user/${store.state.id}`,
                {
                  withCredentials: true,
                }
              );
              userForums.value = userForumsResponse.data;
            } catch (error) {
              // If 404, it means no forums left, so set to empty array
              if (error.response?.status === 404) {
                userForums.value = [];
              } else {
                throw error;
              }
            }

            // Filter out posts that belonged to the deleted forum
            userPosts.value = userPosts.value.filter(
              (post) => post.forum?.forumid !== forumId
            );

            // Filter out the deleted forum from followed forums
            followedForums.value = followedForums.value.filter(
              (forum) => forum.forum.forumid !== forumId
            );

            showNotificationMessage("Forum deleted successfully!");
          } catch (error) {
            console.error("Error deleting forum:", error);
            if (error.response?.status === 403) {
              showNotificationMessage(
                "You can only delete forums that you created.",
                "error"
              );
            } else if (error.response?.status === 401) {
              showNotificationMessage(
                "Your session has expired. Please log in again.",
                "error"
              );
              router.push("/login");
            } else {
              showNotificationMessage(
                "Failed to delete forum. Please try again.",
                "error"
              );
            }
          }
        },
        forumId
      );
    };

    const editForum = (forum) => {
      router.push(`/forums/edit/${forum.forumid}`);
    };

    const deletePost = async (postId) => {
      showConfirmationDialog(
        "Delete Post",
        "Are you sure you want to delete this post?",
        async () => {
          try {
            await axios.delete(`/api/posts/delete/${postId}`);
            // Refresh the user posts list
            const postsResponse = await axios.get(
              `/api/posts/user/${store.state.id}`
            );
            userPosts.value = postsResponse.data;
            showNotificationMessage("Post deleted successfully!");
          } catch (error) {
            console.error("Error deleting post:", error);
            if (error.response?.status === 403) {
              showNotificationMessage(
                "You are not authorized to delete this post.",
                "error"
              );
            } else {
              showNotificationMessage(
                "Failed to delete post. Please try again.",
                "error"
              );
            }
          }
        },
        postId
      );
    };

    onMounted(fetchUserActivity);

    return {
      loading,
      followedForums,
      userPosts,
      userForums,
      formatDate,
      goToForum,
      goToPost,
      deleteForum,
      editForum,
      deletePost,
      showNotification,
      notificationMessage,
      notificationType,
      showConfirmation,
      confirmationTitle,
      confirmationMessage,
      cancelConfirmation,
      confirmAction,
    };
  },
};
</script>

<style scoped>
.activity-container {
  max-width: 1200px;
  margin: 40px auto;
  padding: 20px;
}

.activity-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
}

.activity-header h1 {
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

.activity-section {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 40px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.activity-section h2 {
  font-size: 1.8rem;
  color: #2c3e50;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 2px solid #18c6c6;
}

.forum-list,
.post-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.forum-card,
.post-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #e9ecef;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.forum-card:hover,
.post-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.forum-card h3,
.post-card h3 {
  font-size: 1.4rem;
  color: #2c3e50;
  margin-bottom: 12px;
}

.description {
  color: #666;
  font-size: 0.95rem;
  line-height: 1.5;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex-grow: 1;
}

.meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  font-size: 0.9rem;
  color: #888;
  flex-wrap: wrap;
  margin-top: auto;
  padding-top: 12px;
  border-top: 1px solid #e9ecef;
}

.meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.icon {
  font-size: 1rem;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #666;
}

.empty-state .icon {
  font-size: 3rem;
  color: #18c6c6;
  margin-bottom: 16px;
}

.loading {
  text-align: center;
  padding: 40px;
  color: #666;
  font-size: 1.2rem;
}

.forum-actions,
.post-actions {
  display: flex;
  gap: 8px;
  opacity: 1 !important;
  visibility: visible !important;
  pointer-events: auto;
  position: static !important;
}

.action-button {
  display: flex !important;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
  opacity: 1 !important;
  visibility: visible !important;
  height: 28px;
  background-color: #f8f9fa;
  pointer-events: auto;
  position: static !important;
}

.action-button.edit {
  background-color: #e3f2fd;
  color: #1976d2;
}

.action-button.delete {
  background-color: #ffebee;
  color: #d32f2f;
}

.action-button:hover {
  opacity: 0.8;
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .activity-container {
    padding: 16px;
  }

  .activity-header {
    flex-direction: column;
    gap: 16px;
    text-align: center;
  }

  .activity-section {
    padding: 20px;
  }

  .forum-list,
  .post-list {
    grid-template-columns: 1fr;
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

.confirmation-dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.confirmation-dialog {
  background: white;
  padding: 30px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  max-width: 400px;
  width: 90%;
}

.confirmation-dialog h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 1.5rem;
}

.confirmation-dialog p {
  margin: 0 0 25px 0;
  color: #666;
  line-height: 1.5;
}

.confirmation-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.confirmation-buttons button {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cancel-button {
  background-color: #f8f9fa;
  color: #2c3e50;
}

.cancel-button:hover {
  background-color: #e9ecef;
}

.confirm-button {
  background-color: #f44336;
  color: white;
}

.confirm-button:hover {
  background-color: #d32f2f;
}
</style>
