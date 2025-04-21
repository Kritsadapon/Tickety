<template>
  <div class="create-post">
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
    <h2>Create a New Post</h2>

    <form @submit.prevent="createPost">
      <div class="input-group">
        <label for="postTitle">Post Title</label>
        <input
          type="text"
          id="postTitle"
          v-model="postTitle"
          maxlength="100"
          required
        />
        <div class="char-counter">
          {{ 100 - postTitle.length }}
        </div>
      </div>

      <div class="input-group">
        <label for="postDescription">Post Description</label>
        <textarea
          id="postDescription"
          v-model="postDescription"
          maxlength="500"
          required
        ></textarea>
        <div class="char-counter">
          {{ 500 - postDescription.length }}
        </div>
      </div>

      <button type="submit" :disabled="loading">
        <span v-if="loading">⏳ Creating...</span>
        <span v-else>Create Post</span>
      </button>
    </form>

    <router-link :to="`/forums/${forumId}`" class="back-button"
      >Back to Forum</router-link
    >
  </div>
</template>

<script setup>
import { ref } from "vue";
import axios from "axios";
import { useRouter, useRoute } from "vue-router";

const postTitle = ref("");
const postDescription = ref("");
const loading = ref(false);
const router = useRouter();
const route = useRoute();
const forumId = route.params.forumid;

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

const createPost = async () => {
  loading.value = true;

  try {
    const response = await axios.post(`/api/posts/create/${forumId}`, {
      postTitle: postTitle.value,
      postDescription: postDescription.value,
    });

    if (response.data.success) {
      showNotificationMessage("Post created successfully!", "success");
      setTimeout(() => router.push(`/forums/${forumId}`), 1000);
    } else {
      showNotificationMessage(response.data.message, "error");
    }
  } catch (error) {
    if (error.response) {
      if (error.response.status === 403) {
        showNotificationMessage("Please sign in to create a post.", "error");
      } else if (error.response.status === 409) {
        showNotificationMessage(error.response.data.message, "error");
      } else {
        showNotificationMessage(
          "Error creating post. Please try again.",
          "error"
        );
      }
    } else {
      showNotificationMessage(
        "Error creating post. Please try again.",
        "error"
      );
    }
  } finally {
    loading.value = false;
  }
};
</script>

<style>
.create-post {
  max-width: 500px;
  margin: 50px auto;
  padding: 20px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.input-group {
  position: relative;
  margin-bottom: 15px;
  text-align: left;
}

.input-group .char-counter {
  position: absolute;
  bottom: 10px;
  right: 10px;
  font-size: 12px;
  color: #888;
  pointer-events: none;
}

.input-group label {
  display: block;
  font-weight: bold;
  margin-bottom: 5px;
}

.input-group input,
.input-group textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 5px;
  padding-right: 30px;
}

button {
  width: 100%;
  padding: 10px;
  border: none;
  border-radius: 5px;
  background-color: #18c6c6;
  color: white;
  font-weight: bold;
  cursor: pointer;
}

button:disabled {
  background-color: #cccccc;
}

.success-message {
  color: green;
  font-weight: bold;
}

.error-message {
  color: red;
  font-weight: bold;
}

.back-button {
  display: block;
  margin-top: 15px;
  text-decoration: none;
  color: #18c6c6;
  font-weight: bold;
}

.back-button:hover {
  text-decoration: underline;
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
