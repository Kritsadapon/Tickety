<template>
  <div class="search-page">
    <h2>Search Results for "{{ keyword }}" in this forum</h2>

    <div v-if="loading" class="loading">Loading...</div>
    <div v-else-if="posts.length === 0" class="no-results">No posts found.</div>

    <div v-else class="post-grid">
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
  </div>
</template>

<script>
export default {
  data() {
    return {
      posts: [],
      keyword: "",
      forumid: null,
      loading: true,
    };
  },
  watch: {
    "$route.query.keyword": {
      immediate: true,
      handler(newKeyword) {
        this.keyword = newKeyword || "";
        if (this.keyword && this.forumid) {
          this.fetchPostSearchResults();
        } else {
          this.posts = [];
          this.loading = false;
        }
      },
    },
    "$route.params.forumid": {
      immediate: true,
      handler(newForumId) {
        this.forumid = newForumId;
        if (this.keyword && this.forumid) {
          this.fetchPostSearchResults();
        }
      },
    },
  },
  methods: {
    async fetchPostSearchResults() {
      try {
        const response = await fetch(
          `/api/posts/forum/${this.forumid}/search?keyword=${encodeURIComponent(
            this.keyword
          )}`
        );
        const data = await response.json();
        this.posts = data;
      } catch (error) {
        console.error("Error fetching post search results:", error);
      } finally {
        this.loading = false;
      }
    },
    formatDate(dateString) {
      if (!dateString) return "Unknown date";
      const date = new Date(dateString);
      return date.toLocaleDateString("en-US", {
        year: "numeric",
        month: "long",
        day: "numeric",
      });
    },
    goToPost(postId) {
      this.$router.push(`/posts/${postId}`);
    },
  },
  mounted() {
    this.keyword = this.$route.query.keyword || "";
    this.forumid = this.$route.params.forumid;
    if (this.keyword || this.forumid) {
      this.fetchPostSearchResults();
    } else {
      this.loading = false;
    }
  },
};
</script>

<style scoped>
.search-page {
  max-width: 700px;
  margin: 40px auto;
  padding: 20px;
  text-align: center;
}

.loading,
.no-results {
  font-size: 18px;
  color: gray;
  margin-top: 20px;
}

.post-grid {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-top: 20px;
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
</style>
