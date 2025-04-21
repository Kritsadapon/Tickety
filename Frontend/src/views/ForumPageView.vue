<template>
  <div class="forum-page">
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

    <div class="back-button-container">
      <button class="back-button" @click="goBack">← Back</button>
    </div>

    <div v-if="loading" class="loading">Loading forum...</div>

    <div v-else-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <div v-else class="forum-content">
      <div class="forum-info">
        <div class="forum-actions">
          <button
            v-if="isAuthenticated"
            class="follow-button"
            @click="toggleFollow"
            :disabled="followLoading"
          >
            <span v-if="followLoading">⏳</span>
            <span v-else>{{ isFollowing ? "Unfollow" : "Follow" }}</span>
          </button>
          <button
            v-else
            class="follow-button"
            @click="
              showNotificationMessage(
                'You need to log in to follow this forum.',
                'error'
              )
            "
          >
            Follow
          </button>
          <button class="create-post-button" @click="goToCreatePost">
            Create Post
          </button>
        </div>
        <h1>u/{{ forum.forumTitle }}</h1>
        <p class="description">{{ forum.forumDescription }}</p>
      </div>

      <div class="forum-header">
        <h2>Posts</h2>
        <div class="filter-container" ref="dropdownRef">
          <button @click="toggleDropdown" class="filter-btn">Filter</button>
          <div v-if="showDropdown" class="dropdown">
            <div
              v-for="option in filterOptions"
              :key="option.value"
              @click="applyFilter(option.value)"
              class="dropdown-item"
            >
              {{ option.label }}
            </div>
          </div>
        </div>
      </div>

      <div v-if="posts.length > 0" class="post-grid">
        <div
          class="post-card"
          v-for="post in posts"
          :key="post.postid"
          @click="goToPost(post.postid)"
        >
          <div class="post-content">
            <div class="post-meta">
              <span class="creator">
                <span class="material-icons">person</span>
                {{ post.user?.username || "Anonymous" }}
              </span>
              <span class="date">
                <span class="material-icons">schedule</span>
                {{ formatDate(post.postDateTime) }}
              </span>
              <span class="likes">
                <span class="material-icons">thumb_up</span>
                {{ post.likeCount }} Likes
              </span>
              <span class="dislikes">
                <span class="material-icons">thumb_down</span>
                {{ post.dislikeCount }} Dislikes
              </span>
            </div>
            <h3>{{ post.postTitle }}</h3>
            <p class="description">{{ post.postDescription }}</p>
          </div>
        </div>
      </div>
      <p v-else class="no-posts">No posts available.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

const route = useRoute();
const router = useRouter();
const forum = ref(null);
const posts = ref([]);
const loading = ref(true);
const errorMessage = ref("");
const isFollowing = ref(false);
const followLoading = ref(false);

const showDropdown = ref(false);
const sortBy = ref("newest");

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

const isAuthenticated = ref(false);

onMounted(async () => {
  try {
    const response = await axios.get("/api/auth/check");
    isAuthenticated.value = response.data.success;
  } catch (error) {
    isAuthenticated.value = false;
  }
});

// To track dropdown for outside click detection
const dropdownRef = ref(null);

const filterOptions = [
  { value: "newest", label: "Newest" },
  { value: "oldest", label: "Oldest" },
  { value: "most_liked", label: "Most Liked" },
  { value: "least_liked", label: "Least Liked" },
];

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value;
};

const closeDropdown = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    showDropdown.value = false;
  }
};

onMounted(() => {
  fetchForum();
  document.addEventListener("click", closeDropdown);
});

onBeforeUnmount(() => {
  document.removeEventListener("click", closeDropdown);
});

const applyFilter = async (selectedSortBy) => {
  sortBy.value = selectedSortBy;
  showDropdown.value = false;
  await fetchFilteredPosts();
};

const fetchFilteredPosts = async () => {
  try {
    const response = await axios.get(
      `/api/posts/filter?sortBy=${sortBy.value}&forumId=${route.params.forumid}`
    );
    posts.value = response.data;
  } catch (error) {
    console.error("Error fetching filtered posts:", error);
  }
};

const fetchForum = async () => {
  try {
    const response = await axios.get(`/api/forums/${route.params.forumid}`);
    forum.value = response.data;

    await fetchFilteredPosts();

    const followResponse = await axios.get(`/api/forums/followed`);
    isFollowing.value = followResponse.data.some(
      (f) => f.forum.forumid === forum.value.forumid
    );
  } catch (error) {
    errorMessage.value = "Forum not found.";
  } finally {
    loading.value = false;
  }
};

// Toggle follow/unfollow
const toggleFollow = async () => {
  followLoading.value = true;
  try {
    if (isFollowing.value) {
      await axios.post(`/api/forums/${forum.value.forumid}/unfollow`);
      isFollowing.value = false;
    } else {
      await axios.post(`/api/forums/${forum.value.forumid}/follow`);
      isFollowing.value = true;
    }
  } catch (error) {
    if (error.response && error.response.status === 403) {
      showNotificationMessage(
        "You need to log in to follow this forum.",
        "error"
      );
    } else {
      console.error("Error toggling follow:", error);
    }
  } finally {
    followLoading.value = false;
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

// Navigate to create post page
const goToCreatePost = () => {
  router.push(`/forums/${forum.value.forumid}/create-post`);
};

// Navigate to post page
const goToPost = (postId) => {
  router.push(`/posts/${postId}`);
};

// Navigate back to previous page
const goBack = () => {
  router.back();
};

onMounted(fetchForum);
</script>

<style>
.forum-info {
  text-align: left;
  background: #252525;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #ccc;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;
  margin-bottom: 20px;
}

.forum-actions {
  display: flex;
  gap: 5px;
  position: absolute;
  top: 2.5px;
  right: 10px;
  margin-bottom: 10px;
}

.forum-info h1 {
  font-size: 24px;
  margin-bottom: 20px;
  margin-left: 10px;
  color: #ffffff;
}

.forum-info .description {
  font-size: 16px;
  color: #fffdfd;
  margin-bottom: 20px;
  border-radius: 8px;
  min-height: 100px;
  max-height: 100px;
  overflow-y: auto;
  padding: 10px;
}

.forum-page {
  max-width: 700px;
  margin: 40px auto;
  text-align: center;
}

.back-button-container {
  text-align: left;
  margin-bottom: 10px;
}

.back-button {
  background: #ddd;
  padding: 8px 16px;
  border: none;
  cursor: pointer;
}

.follow-button,
.create-post-button {
  background: #18c6c6;
  color: white;
  padding: 5px 10px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  margin: 10px;
}

.forum-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter-container {
  position: relative;
}

.filter-btn {
  padding: 10px 15px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}

.dropdown {
  position: absolute;
  top: 40px;
  right: 0;
  width: 150px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 5px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  z-index: 10;
}

.dropdown-item {
  padding: 10px;
  cursor: pointer;
}

.dropdown-item:hover {
  background-color: #f1f1f1;
}

.post-card {
  background: #f8f8f8;
  padding: 15px;
  border-radius: 8px;
  text-align: left;
  cursor: pointer;
  transition: transform 0.2s;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.post-card:hover {
  transform: scale(1.02);
}

.post-content {
  display: flex;
  flex-direction: column;
}

.post-meta {
  display: flex;
  justify-content: flex-start;
  gap: 15px;
  font-size: 12px;
  color: #888;
  margin-bottom: 10px;
}

.post-meta span {
  display: flex;
  align-items: center;
  gap: 5px;
}

.post-meta .material-icons {
  font-size: 14px;
}

.post-content h3 {
  font-size: 18px;
  color: #333;
  margin-bottom: 15px;
}

.post-content .description {
  font-size: 14px;
  color: #606060;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.no-posts {
  font-size: 16px;
  color: gray;
}

.forum-content {
  background: #f8f8f8;
  padding: 20px;
  border-radius: 8px;
}

.description {
  font-size: 16px;
  color: gray;
}

.loading {
  font-size: 18px;
  color: blue;
}

.error-message {
  color: red;
  font-weight: bold;
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
</style>
