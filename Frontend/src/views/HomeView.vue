<template>
  <div class="home">
    <div class="header-section">
      <h1>Welcome to Unitalk</h1>
      <p class="subtitle">
        Join the conversation in our vibrant community forums
      </p>
      <router-link to="/forums/create" class="create-button">
        <span class="material-icons">add</span>
        Create New Forum
      </router-link>
    </div>

    <div class="forum-section">
      <div class="forum-header">
        <h2>Forums</h2>
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

      <div v-if="forums.length > 0" class="forum-grid">
        <div
          class="forum-card"
          v-for="forum in forums"
          :key="forum.forumid"
          @click="goToForum(forum.forumid)"
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
                {{ forum.followerCount }} Followers
              </span>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="empty-state">
        <span class="material-icons">forum</span>
        <h3>No Forums Yet</h3>
        <p>Be the first to create a forum and start the conversation!</p>
        <router-link to="/forums/create" class="create-button"
          >Create Your First Forum</router-link
        >
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

const forums = ref([]);
const router = useRouter();
const showDropdown = ref(false);
const sortBy = ref("newest");

// To track dropdown for outside click detection
const dropdownRef = ref(null);

const filterOptions = [
  { value: "newest", label: "Newest" },
  { value: "oldest", label: "Oldest" },
  { value: "most_followed", label: "Most Followed" },
  { value: "least_followed", label: "Least Followed" },
];

const fetchForums = async () => {
  try {
    const response = await axios.get(
      `/api/forums/filter?sortBy=${sortBy.value}`
    );
    forums.value = response.data;
  } catch (error) {
    console.error("Error fetching forums:", error);
  }
};

const applyFilter = (selectedSortBy) => {
  sortBy.value = selectedSortBy;
  showDropdown.value = false;
  fetchForums();
};

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value;
};

const closeDropdown = (event) => {
  if (dropdownRef.value && !dropdownRef.value.contains(event.target)) {
    showDropdown.value = false;
  }
};

onMounted(() => {
  fetchForums();
  document.addEventListener("click", closeDropdown);
});

onBeforeUnmount(() => {
  document.removeEventListener("click", closeDropdown);
});

const goToForum = (forumId) => {
  router.push(`/forums/${forumId}`);
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

onMounted(fetchForums);
</script>

<style scoped>
.home {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header-section {
  text-align: center;
  margin-bottom: 40px;
  padding: 40px 20px;
  background: linear-gradient(135deg, #1a1a1a 0%, #2a2a2a 100%);
  border-radius: 12px;
  color: white;
}

.header-section h1 {
  font-size: 2.5rem;
  margin-bottom: 10px;
  color: #18c6c6;
}

.subtitle {
  font-size: 1.2rem;
  color: #cccccc;
  margin-bottom: 20px;
}

.create-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: #18c6c6;
  color: white;
  padding: 12px 24px;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s ease;
}

.create-button:hover {
  background: #15b3b3;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(24, 198, 198, 0.2);
}

.forum-section {
  margin-top: 40px;
}

.forum-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.forum-header h2 {
  font-size: 1.8rem;
  color: #333;
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

@media (max-width: 768px) {
  .home {
    padding: 16px;
  }

  .header-section {
    padding: 30px 16px;
  }

  .header-section h1 {
    font-size: 2rem;
  }

  .forum-grid {
    grid-template-columns: 1fr;
  }
}
</style>
