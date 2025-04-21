<template>
  <div class="search-page">
    <h2>Search Results for "{{ keyword }}"</h2>

    <div v-if="loading" class="loading">Loading...</div>
    <div v-else-if="forums.length === 0" class="empty-state">
      <span class="material-icons">search_off</span>
      <h3>No results found.</h3>
      <p>Try searching for something else.</p>
    </div>

    <div v-else class="forum-grid">
      <router-link
        v-for="forum in forums"
        :key="forum.forumid"
        :to="`/forums/${forum.forumid}`"
        class="forum-card"
      >
        <div class="forum-content">
          <h3>{{ forum.forumTitle }}</h3>
          <p class="description">{{ forum.forumDescription }}</p>
          <div class="forum-meta">
            <span class="date">
              <span class="material-icons">schedule</span>
              {{ formatDate(forum.forumDateTime) }}
            </span>
            <span class="creator">
              <span class="material-icons">person</span>
              {{ forum.user?.username || "Anonymous" }}
            </span>
            <span class="followers">
              <span class="material-icons">group</span>
              {{ forum.followerCount || 0 }} Followers
            </span>
          </div>
        </div>
      </router-link>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      forums: [],
      keyword: "",
      loading: true,
    };
  },
  watch: {
    "$route.query.keyword": {
      immediate: true,
      handler(newKeyword) {
        this.keyword = newKeyword || "";
        if (this.keyword) {
          this.fetchSearchResults();
        } else {
          this.forums = [];
          this.loading = false;
        }
      },
    },
  },
  methods: {
    async fetchSearchResults() {
      try {
        const response = await fetch(
          `/api/forums/search?keyword=${encodeURIComponent(this.keyword)}`
        );
        const data = await response.json();
        this.forums = data;
      } catch (error) {
        console.error("Search failed:", error);
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
  },
  mounted() {
    this.keyword = this.$route.query.keyword || "";
    if (this.keyword) {
      this.fetchSearchResults();
    } else {
      this.loading = false;
    }
  },
};
</script>

<style scoped>
.search-page {
  max-width: 1200px;
  margin: 40px auto;
  padding: 20px;
  text-align: center;
}

h2 {
  font-size: 2rem;
  margin-bottom: 20px;
}

.forum-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
  margin-top: 20px;
}

.forum-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #ccc;
  text-decoration: none;
  color: inherit;
}

.forum-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.forum-content {
  padding: 20px;
}

.forum-content h3 {
  font-size: 1.4rem;
  color: #333;
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
}

.forum-meta {
  display: flex;
  gap: 16px;
  font-size: 0.9rem;
  color: #888;
}

.forum-meta span {
  display: flex;
  align-items: center;
  gap: 4px;
}

.forum-meta .material-icons {
  font-size: 1.1rem;
}

.followers {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.9rem;
  color: #555;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  background: #f8f9fa;
  border-radius: 12px;
  margin-top: 20px;
}

.empty-state .material-icons {
  font-size: 4rem;
  color: #18c6c6;
  margin-bottom: 16px;
}

.empty-state h3 {
  font-size: 1.8rem;
  color: #333;
  margin-bottom: 12px;
}

.empty-state p {
  color: #666;
  margin-bottom: 24px;
}

.loading {
  font-size: 18px;
  color: #3498db;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .forum-grid {
    grid-template-columns: 1fr;
  }

  h2 {
    font-size: 1.5rem;
  }
}
</style>
