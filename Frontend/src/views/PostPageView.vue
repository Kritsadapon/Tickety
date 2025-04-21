<template>
  <div class="post-page">
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
    <button class="back-button" @click="goBack">← Back</button>

    <div v-if="loading" class="loading">Loading post...</div>

    <div v-else-if="errorMessage" class="error-message">
      {{ errorMessage }}
    </div>

    <div v-else class="post-content">
      <div class="post-meta">
        <p class="forum-name">
          <strong>u/{{ post.forum.forumTitle }}</strong>
        </p>
        <p class="username">By {{ post.user.username }}</p>
      </div>

      <h1>{{ post.postTitle }}</h1>
      <p class="description">{{ post.postDescription }}</p>

      <div class="post-actions">
        <div class="like-dislike-container">
          <button
            v-if="isAuthenticated"
            class="like-button"
            :class="{ active: liked }"
            @click="toggleLike"
            :disabled="likeDislikeLoading"
          >
            <font-awesome-icon :icon="[liked ? 'fas' : 'far', 'thumbs-up']" />
            <span>{{ likeCount }}</span>
          </button>
          <button
            v-else
            class="like-button"
            @click="
              showNotificationMessage(
                'You need to log in to like this post.',
                'error'
              )
            "
          >
            <font-awesome-icon :icon="['far', 'thumbs-up']" />
            <span>{{ likeCount }}</span>
          </button>

          <div class="separator"></div>

          <button
            v-if="isAuthenticated"
            class="dislike-button"
            :class="{ active: disliked }"
            @click="toggleDislike"
            :disabled="likeDislikeLoading"
          >
            <font-awesome-icon
              :icon="[disliked ? 'fas' : 'far', 'thumbs-down']"
            />
            <span>{{ dislikeCount }}</span>
          </button>
          <button
            v-else
            class="dislike-button"
            @click="
              showNotificationMessage(
                'You need to log in to dislike this post.',
                'error'
              )
            "
          >
            <font-awesome-icon :icon="['far', 'thumbs-down']" />
            <span>{{ dislikeCount }}</span>
          </button>
        </div>
      </div>

      <!-- Comments Section -->
      <h2>Comments</h2>

      <div class="add-comment">
        <div class="add-comment-container">
          <textarea
            v-model="newComment"
            placeholder="Write a comment..."
            maxlength="255"
          ></textarea>
          <div class="char-counter">
            {{ 255 - newComment.length }}
          </div>
          <button
            v-if="isAuthenticated"
            @click="addComment"
            :disabled="commentLoading"
          >
            <span v-if="commentLoading">⏳</span>
            <span v-else>Post</span>
          </button>
          <button
            v-else
            @click="
              showNotificationMessage(
                'You need to log in to post a comment.',
                'error'
              )
            "
          >
            Post
          </button>
        </div>
      </div>

      <div v-if="comments.length > 0" class="comment-list">
        <div
          class="comment-card"
          v-for="comment in comments"
          :key="comment.commentId"
        >
          <div class="comment-header">
            <div class="user-profile">
              <div class="avatar">
                <img
                  v-if="comment.profilePictureUrl"
                  :src="comment.profilePictureUrl"
                  :alt="comment.username"
                  class="profile-image"
                />
                <font-awesome-icon v-else :icon="['fas', 'user-circle']" />
              </div>
              <span class="username">{{ comment.username }}</span>
            </div>
          </div>
          <p class="comment-text">{{ comment.comment }}</p>
        </div>
      </div>
      <p v-else class="no-comments">No comments yet.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";

const route = useRoute();
const router = useRouter();
const post = ref(null);
const comments = ref([]);
const newComment = ref("");
const loading = ref(true);
const commentLoading = ref(false);
const errorMessage = ref("");
const likeCount = ref(0);
const dislikeCount = ref(0);
const liked = ref(false);
const disliked = ref(false);
const likeDislikeLoading = ref(false);
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

const fetchPost = async () => {
  try {
    const response = await axios.get(`/api/posts/${route.params.postid}`);
    post.value = response.data;

    if (!post.value.forum) {
      console.warn("Forum data is missing in the post response.");
    }

    const interactionResponse = await axios.get(
      `/api/posts/${route.params.postid}/interaction`
    );
    const userInteraction = interactionResponse.data;

    if (userInteraction === true) {
      liked.value = true;
      disliked.value = false;
    } else if (userInteraction === false) {
      liked.value = false;
      disliked.value = true;
    } else {
      liked.value = false;
      disliked.value = false;
    }

    const likeCountResponse = await axios.get(
      `/api/posts/${route.params.postid}/like-count`
    );
    likeCount.value = likeCountResponse.data;

    const dislikeCountResponse = await axios.get(
      `/api/posts/${route.params.postid}/dislike-count`
    );
    dislikeCount.value = dislikeCountResponse.data;

    const commentsResponse = await axios.get(
      `/api/comments/post/${route.params.postid}`
    );
    comments.value = commentsResponse.data || [];
  } catch (error) {
    errorMessage.value = error.message || "Post not found.";
    console.error("Error fetching post:", error);
  } finally {
    loading.value = false;
  }
};

const toggleLike = async () => {
  if (likeDislikeLoading.value) return;

  likeDislikeLoading.value = true;

  try {
    await axios.post(`/api/posts/${post.value.postid}/like`);

    liked.value = !liked.value;
    if (liked.value) {
      disliked.value = false;
    }

    const likeCountResponse = await axios.get(
      `/api/posts/${post.value.postid}/like-count`
    );
    likeCount.value = likeCountResponse.data;

    const dislikeCountResponse = await axios.get(
      `/api/posts/${post.value.postid}/dislike-count`
    );
    dislikeCount.value = dislikeCountResponse.data;
  } catch (error) {
    if (error.response && error.response.status === 403) {
      showNotificationMessage("You need to log in to like this post.", "error");
    } else {
      console.error("Error toggling like:", error);
    }
  } finally {
    likeDislikeLoading.value = false;
  }
};

const toggleDislike = async () => {
  if (likeDislikeLoading.value) return;

  likeDislikeLoading.value = true;

  try {
    await axios.post(`/api/posts/${post.value.postid}/dislike`);

    disliked.value = !disliked.value;
    if (disliked.value) {
      liked.value = false;
    }

    const likeCountResponse = await axios.get(
      `/api/posts/${post.value.postid}/like-count`
    );
    likeCount.value = likeCountResponse.data;

    const dislikeCountResponse = await axios.get(
      `/api/posts/${post.value.postid}/dislike-count`
    );
    dislikeCount.value = dislikeCountResponse.data;
  } catch (error) {
    if (error.response && error.response.status === 403) {
      showNotificationMessage(
        "You need to log in to dislike this post.",
        "error"
      );
    } else {
      console.error("Error toggling dislike:", error);
    }
  } finally {
    likeDislikeLoading.value = false;
  }
};

// Add a new comment
const addComment = async () => {
  if (!newComment.value.trim()) return;

  commentLoading.value = true;
  try {
    await axios.post("/api/comments", {
      postId: post.value.postid,
      user: {
        id: post.value.user.id,
      },
      comment: newComment.value,
    });

    const commentsResponse = await axios.get(
      `/api/comments/post/${post.value.postid}`
    );
    comments.value = commentsResponse.data;

    newComment.value = "";
  } catch (error) {
    if (error.response && error.response.status === 403) {
      showNotificationMessage("You need to log in to post a comment.", "error");
    } else {
      console.error("Error adding comment:", error);
    }
  } finally {
    commentLoading.value = false;
  }
};

// Navigate back to previous page
const goBack = () => {
  router.back();
};

onMounted(fetchPost);
</script>

<style>
.post-meta {
  margin-bottom: 10px;
  margin-left: 5px;
}

.forum-name {
  font-size: 14px;
  color: #333;
}

.username {
  font-size: 12px;
  color: #888;
  margin-top: 2px;
}

.post-page {
  max-width: 700px;
  margin: 40px auto;
  text-align: left;
}

.back-button {
  background: #ddd;
  padding: 8px 16px;
  border: none;
  cursor: pointer;
  margin-bottom: 15px;
  width: auto;
}

.post-content {
  background: #ddd;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #ccc;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.post-content h1 {
  font-size: 24px;
  margin-bottom: 20px;
  margin-left: 10px;
}

.post-content p.description {
  font-size: 16px;
  color: #606060;
  margin-bottom: 20px;
  border-radius: 8px;
  border: 1px solid #ccc;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  min-height: 150px;
  max-height: 500px;
  overflow-y: auto;
  padding: 10px;
}

.post-actions {
  margin-top: 10px;
  display: flex;
  justify-content: flex-start;
  gap: 10px;
}

.like-dislike-container {
  display: flex;
  align-items: center;
  background: #f9f9f9;
  border: 1px solid #ddd;
  border-radius: 50px;
  padding: 1px 2px;
  gap: 10px;
  margin-top: 15px;
  margin-bottom: 25px;
}

.like-button,
.dislike-button {
  background: none;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #606060;
  padding: 5px 10px;
  transition: all 0.2s ease;
}

.like-button.active,
.dislike-button.active {
  color: #18c6c6;
}

.like-button:disabled,
.dislike-button:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.like-button span,
.dislike-button span {
  font-size: 12px;
}

.separator {
  width: 1px;
  height: 20px;
  background: #ddd;
}

.comment-list {
  margin-top: 20px;
}

.comment-card {
  background: #f1f1f1;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
  border: 1px solid #ccc;
  max-height: 200px;
  overflow-y: auto;
  word-wrap: break-word;
  word-break: break-word;
}

.comment-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
}

.avatar {
  font-size: 24px;
  color: #666;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar .profile-image {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.username {
  font-weight: 500;
  color: #333;
}

.comment-text {
  margin: 0;
  color: #444;
  line-height: 1.5;
}

.add-comment {
  margin-top: 20px;
}

.add-comment-container {
  position: relative;
  display: flex;
  align-items: center;
}

.add-comment textarea {
  background: #f9f9f9;
  width: 100%;
  padding: 10px 50px 10px 10px;
  border-radius: 5px;
  border: 1px solid #ccc;
  margin-bottom: 10px;
  resize: none;
  padding-right: 50px;
}

.add-comment-container .char-counter {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 12px;
  color: #888;
  pointer-events: none;
}

.add-comment button {
  position: absolute;
  right: 5px;
  bottom: 15px;
  background: #16b3b3;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 5px 10px;
  cursor: pointer;
  font-size: 14px;
  width: auto;
}

.add-comment button:disabled {
  background: #ccc;
  cursor: not-allowed;
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
