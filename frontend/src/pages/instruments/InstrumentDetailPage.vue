<template>
  <div v-if="instrument" class="detail">
    <!-- 이미지 섹션 -->
    <div class="images">
      <div class="main-img-wrap">
        <img v-if="currentImage" :src="currentImage" :alt="instrument.title" class="main-img" />
        <div v-else class="no-img">🎵</div>
        <span class="status-badge" :class="instrument.status.toLowerCase()">{{ statusLabel }}</span>
      </div>
      <div v-if="instrument.images.length > 1" class="thumbnails">
        <img v-for="img in instrument.images" :key="img.id" :src="img.imageUrl"
          :class="{ active: currentImage === img.imageUrl }" @click="currentImage = img.imageUrl" />
      </div>
    </div>

    <!-- 정보 섹션 -->
    <div class="info">
      <div class="top-meta">
        <span class="category-tag">{{ instrument.categoryName }}</span>
        <span class="region">📍 {{ instrument.region }}</span>
      </div>
      <h1 class="title">{{ instrument.title }}</h1>
      <p class="price">{{ instrument.price.toLocaleString() }}원</p>

      <div class="seller-card">
        <RouterLink :to="`/users/${instrument.sellerId}`" class="seller-link">
          <div class="avatar">{{ instrument.sellerNickname?.charAt(0) }}</div>
          <div>
            <p class="seller-name">{{ instrument.sellerNickname }}</p>
            <p class="seller-region">📍 {{ instrument.sellerRegion }}</p>
          </div>
        </RouterLink>
      </div>

      <!-- 액션 버튼 영역 -->
      <div class="action-area">
        <!-- 판매자 본인 -->
        <template v-if="isOwner">
          <div class="owner-actions">
            <select v-model="selectedStatus" @change="handleStatusChange" class="status-select">
              <option value="SELLING">📗 판매중</option>
              <option value="RESERVED">📙 예약중</option>
              <option value="SOLD">📕 판매완료</option>
            </select>
            <RouterLink :to="`/instruments/${instrument.id}/edit`" class="btn btn-secondary">수정</RouterLink>
            <button class="btn btn-danger" @click="handleDelete">삭제</button>
          </div>
          <RouterLink to="/me/trades/received" class="btn-trade-manage">
            거래 요청 관리 →
          </RouterLink>
        </template>

        <!-- 구매자 -->
        <template v-else-if="authStore.isLoggedIn">
          <div class="buyer-actions">
            <button
              v-if="instrument.status === 'SELLING'"
              class="btn-trade"
              :disabled="alreadyRequested"
              @click="showTradeModal = true"
            >
              {{ alreadyRequested ? '✓ 거래 요청 완료' : '💬 거래 요청하기' }}
            </button>
            <p v-else-if="instrument.status === 'RESERVED'" class="status-msg reserved">예약중인 상품입니다</p>
            <p v-else-if="instrument.status === 'SOLD'" class="status-msg sold">판매 완료된 상품입니다</p>

            <button class="btn-wish" :class="{ wished }" @click="toggleWish">
              {{ wished ? '♥ 찜 해제' : '♡ 찜하기' }}
            </button>
          </div>
        </template>

        <!-- 비로그인 -->
        <template v-else>
          <RouterLink to="/login" class="btn-trade">로그인 후 거래 요청하기</RouterLink>
        </template>
      </div>

      <!-- 상세 설명 -->
      <div class="description">
        <h3>상품 설명</h3>
        <p>{{ instrument.description }}</p>
      </div>

      <div class="sub-meta">
        <span>조회 {{ instrument.viewCount }}</span>
        <span>등록 {{ formatDate(instrument.createdAt) }}</span>
      </div>
    </div>
  </div>

  <div v-else class="loading">불러오는 중...</div>

  <!-- 거래 요청 모달 -->
  <Teleport to="body">
    <div v-if="showTradeModal" class="modal-overlay" @click.self="showTradeModal = false">
      <div class="modal">
        <h2>거래 요청</h2>
        <p class="modal-instrument">{{ instrument?.title }}</p>
        <p class="modal-price">{{ instrument?.price.toLocaleString() }}원</p>
        <div class="field">
          <label>판매자에게 보낼 메시지 (선택)</label>
          <textarea v-model="tradeMessage" rows="4"
            placeholder="직거래/택배 여부, 원하는 거래 방법 등을 적어주세요" />
        </div>
        <p v-if="tradeError" class="error">{{ tradeError }}</p>
        <div class="modal-btns">
          <button class="btn-cancel" @click="showTradeModal = false">취소</button>
          <button class="btn-confirm" :disabled="tradingLoading" @click="submitTradeRequest">
            {{ tradingLoading ? '요청 중...' : '거래 요청하기' }}
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { instrumentsApi } from '@/api/instruments'
import { tradesApi } from '@/api/trades'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const instrument = ref(null)
const wished = ref(false)
const alreadyRequested = ref(false)
const selectedStatus = ref('SELLING')
const currentImage = ref(null)
const showTradeModal = ref(false)
const tradeMessage = ref('')
const tradeError = ref('')
const tradingLoading = ref(false)

const isOwner = computed(() => authStore.user?.id === instrument.value?.sellerId)
const statusLabel = computed(() => ({
  SELLING: '판매중', RESERVED: '예약중', SOLD: '판매완료'
}[instrument.value?.status]))

onMounted(async () => {
  const res = await instrumentsApi.getDetail(route.params.id)
  instrument.value = res.data.data
  selectedStatus.value = instrument.value.status
  currentImage.value = instrument.value.images.find(i => i.isThumbnail)?.imageUrl
    || instrument.value.images[0]?.imageUrl || null

  // 로그인 상태이고 본인 상품이 아닐 때 찜/거래요청 여부 조회
  if (authStore.isLoggedIn && !isOwner.value) {
    try {
      const statusRes = await instrumentsApi.getMyStatus(route.params.id)
      wished.value = statusRes.data.data.wishlisted
      alreadyRequested.value = statusRes.data.data.alreadyRequested
    } catch (e) {
      // 무시
    }
  }
})

async function toggleWish() {
  try {
    if (wished.value) {
      await instrumentsApi.removeWishlist(instrument.value.id)
    } else {
      await instrumentsApi.addWishlist(instrument.value.id)
    }
    wished.value = !wished.value
  } catch (e) {
    alert(e.response?.data?.message || '오류가 발생했습니다.')
  }
}

async function handleStatusChange() {
  try {
    const res = await instrumentsApi.updateStatus(instrument.value.id, selectedStatus.value)
    instrument.value.status = res.data.data.status
  } catch (e) {
    alert(e.response?.data?.message || '상태 변경에 실패했습니다.')
    selectedStatus.value = instrument.value.status
  }
}

async function handleDelete() {
  if (!confirm('게시글을 삭제하시겠습니까?')) return
  await instrumentsApi.delete(instrument.value.id)
  router.push('/instruments')
}

async function submitTradeRequest() {
  tradeError.value = ''
  tradingLoading.value = true
  try {
    await tradesApi.createRequest(instrument.value.id, { message: tradeMessage.value })
    alreadyRequested.value = true
    showTradeModal.value = false
    alert('거래 요청이 전송되었습니다. 판매자의 수락을 기다려주세요.')
  } catch (e) {
    tradeError.value = e.response?.data?.message || '거래 요청에 실패했습니다.'
  } finally {
    tradingLoading.value = false
  }
}

function formatDate(dt) {
  if (!dt) return ''
  return new Date(dt).toLocaleDateString('ko-KR')
}
</script>

<style scoped>
.detail { display: grid; grid-template-columns: 1fr 1fr; gap: 40px; }
@media (max-width: 768px) { .detail { grid-template-columns: 1fr; } }

.main-img-wrap { position: relative; }
.main-img { width: 100%; border-radius: 12px; object-fit: cover; aspect-ratio: 4/3; background: #f5f5f5; }
.no-img { aspect-ratio: 4/3; background: #f0f0f0; display: flex; align-items: center; justify-content: center; font-size: 4rem; border-radius: 12px; }
.status-badge { position: absolute; top: 12px; left: 12px; padding: 5px 12px; border-radius: 20px; font-size: 0.82rem; font-weight: 700; }
.status-badge.selling { background: #e8f5e9; color: #2e7d32; }
.status-badge.reserved { background: #fff3e0; color: #e65100; }
.status-badge.sold { background: #eee; color: #757575; }
.thumbnails { display: flex; gap: 8px; margin-top: 10px; flex-wrap: wrap; }
.thumbnails img { width: 64px; height: 64px; object-fit: cover; border-radius: 8px; cursor: pointer; border: 2px solid transparent; }
.thumbnails img.active { border-color: #1a73e8; }

.top-meta { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; }
.category-tag { background: #e8f0fe; color: #1a73e8; padding: 4px 10px; border-radius: 20px; font-size: 0.8rem; font-weight: 600; }
.region { font-size: 0.88rem; color: #888; }
.title { font-size: 1.5rem; font-weight: 700; margin-bottom: 12px; line-height: 1.4; }
.price { font-size: 1.7rem; font-weight: 800; color: #1a73e8; margin-bottom: 20px; }

.seller-card { background: #f8f9fa; border-radius: 10px; padding: 14px 16px; margin-bottom: 20px; }
.seller-link { display: flex; align-items: center; gap: 12px; color: #333; }
.avatar { width: 42px; height: 42px; border-radius: 50%; background: #1a73e8; color: #fff; display: flex; align-items: center; justify-content: center; font-weight: 700; font-size: 1.1rem; flex-shrink: 0; }
.seller-name { font-weight: 600; font-size: 0.95rem; }
.seller-region { font-size: 0.82rem; color: #888; margin-top: 2px; }

.action-area { margin-bottom: 24px; }
.owner-actions { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 12px; }
.status-select { padding: 10px 14px; border: 1px solid #ddd; border-radius: 8px; font-size: 0.9rem; cursor: pointer; }
.btn { padding: 10px 18px; border-radius: 8px; font-size: 0.92rem; border: none; cursor: pointer; }
.btn-secondary { background: #f0f4ff; color: #1a73e8; }
.btn-danger { background: #ffeaea; color: #d32f2f; }
.btn-trade-manage { display: inline-block; color: #1a73e8; font-size: 0.9rem; margin-top: 4px; }

.buyer-actions { display: flex; flex-direction: column; gap: 10px; }
.btn-trade { padding: 14px; background: #1a73e8; color: #fff; border: none; border-radius: 10px; font-size: 1rem; font-weight: 700; width: 100%; cursor: pointer; }
.btn-trade:disabled { background: #90caf9; cursor: not-allowed; }
.btn-wish { padding: 11px; background: #fff; border: 2px solid #e0e0e0; border-radius: 10px; font-size: 0.95rem; cursor: pointer; width: 100%; }
.btn-wish.wished { border-color: #e91e63; color: #e91e63; }
.status-msg { text-align: center; padding: 14px; border-radius: 10px; font-weight: 600; font-size: 0.95rem; }
.status-msg.reserved { background: #fff3e0; color: #e65100; }
.status-msg.sold { background: #eee; color: #757575; }

.description { border-top: 1px solid #eee; padding-top: 20px; margin-bottom: 16px; }
.description h3 { font-size: 1rem; margin-bottom: 12px; color: #333; }
.description p { color: #555; line-height: 1.8; white-space: pre-wrap; font-size: 0.95rem; }
.sub-meta { display: flex; gap: 16px; font-size: 0.82rem; color: #bbb; }
.loading { text-align: center; padding: 80px; color: #888; }

/* 모달 */
.modal-overlay { position: fixed; inset: 0; background: rgba(0,0,0,0.5); z-index: 1000; display: flex; align-items: center; justify-content: center; padding: 16px; }
.modal { background: #fff; border-radius: 16px; padding: 28px; width: 100%; max-width: 480px; }
.modal h2 { font-size: 1.2rem; margin-bottom: 16px; }
.modal-instrument { font-weight: 600; margin-bottom: 4px; }
.modal-price { color: #1a73e8; font-weight: 700; font-size: 1.1rem; margin-bottom: 20px; }
.field { margin-bottom: 16px; }
label { display: block; font-size: 0.88rem; color: #666; margin-bottom: 6px; }
textarea { width: 100%; padding: 10px 12px; border: 1px solid #ddd; border-radius: 8px; resize: none; font-family: inherit; font-size: 0.9rem; outline: none; }
textarea:focus { border-color: #1a73e8; }
.error { color: #d32f2f; font-size: 0.88rem; margin-bottom: 12px; }
.modal-btns { display: flex; gap: 10px; justify-content: flex-end; }
.btn-cancel { padding: 10px 20px; border: 1px solid #ddd; border-radius: 8px; background: #fff; color: #666; cursor: pointer; }
.btn-confirm { padding: 10px 24px; background: #1a73e8; color: #fff; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; }
.btn-confirm:disabled { opacity: 0.6; }
</style>
