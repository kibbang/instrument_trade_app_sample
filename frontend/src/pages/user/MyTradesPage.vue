<template>
  <div class="trades-wrap">
    <h1>거래 관리</h1>

    <!-- 탭 -->
    <div class="tabs">
      <button :class="{ active: tab === 'received' }" @click="tab = 'received'">
        📥 받은 요청 <span v-if="tab === 'received'" class="count">({{ totalReceived }})</span>
      </button>
      <button :class="{ active: tab === 'sent' }" @click="tab = 'sent'">
        📤 보낸 요청 <span v-if="tab === 'sent'" class="count">({{ totalSent }})</span>
      </button>
    </div>

    <!-- 받은 요청 (판매자) -->
    <div v-if="tab === 'received'">
      <div v-if="receivedLoading" class="loading">불러오는 중...</div>
      <div v-else-if="receivedList.length === 0" class="empty">받은 거래 요청이 없습니다.</div>
      <div v-else class="request-list">
        <div v-for="req in receivedList" :key="req.id" class="request-card">
          <div class="req-instrument">
            <RouterLink :to="`/instruments/${req.instrumentId}`" class="inst-link">
              <img v-if="req.instrumentThumbnail" :src="req.instrumentThumbnail" class="thumb" />
              <div v-else class="thumb-placeholder">🎵</div>
              <div class="inst-info">
                <p class="inst-title">{{ req.instrumentTitle }}</p>
                <p class="inst-price">{{ req.instrumentPrice?.toLocaleString() }}원</p>
              </div>
            </RouterLink>
          </div>

          <div class="req-details">
            <div class="req-meta">
              <span class="buyer-name">👤 {{ req.buyerNickname }}</span>
              <span class="req-date">{{ formatDate(req.createdAt) }}</span>
              <span class="status-badge" :class="req.status.toLowerCase()">{{ statusLabel(req.status) }}</span>
            </div>
            <p v-if="req.message" class="req-message">"{{ req.message }}"</p>
          </div>

          <!-- 판매자 액션 버튼 -->
          <div class="req-actions">
            <template v-if="req.status === 'REQUESTED'">
              <button class="btn btn-accept" @click="handleAccept(req)">✅ 수락</button>
              <button class="btn btn-reject" @click="handleReject(req)">❌ 거절</button>
            </template>
            <template v-else-if="req.status === 'ACCEPTED'">
              <button class="btn btn-complete" @click="handleComplete(req)">🤝 거래 완료</button>
            </template>
            <span v-else class="done-label">{{ statusLabel(req.status) }}</span>
          </div>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <div v-if="receivedTotalPages > 1" class="pagination">
        <button v-for="p in receivedTotalPages" :key="p" :class="{ active: p - 1 === receivedPage }"
          @click="loadReceived(p - 1)">{{ p }}</button>
      </div>
    </div>

    <!-- 보낸 요청 (구매자) -->
    <div v-if="tab === 'sent'">
      <div v-if="sentLoading" class="loading">불러오는 중...</div>
      <div v-else-if="sentList.length === 0" class="empty">보낸 거래 요청이 없습니다.</div>
      <div v-else class="request-list">
        <div v-for="req in sentList" :key="req.id" class="request-card">
          <div class="req-instrument">
            <RouterLink :to="`/instruments/${req.instrumentId}`" class="inst-link">
              <img v-if="req.instrumentThumbnail" :src="req.instrumentThumbnail" class="thumb" />
              <div v-else class="thumb-placeholder">🎵</div>
              <div class="inst-info">
                <p class="inst-title">{{ req.instrumentTitle }}</p>
                <p class="inst-price">{{ req.instrumentPrice?.toLocaleString() }}원</p>
              </div>
            </RouterLink>
          </div>

          <div class="req-details">
            <div class="req-meta">
              <span class="buyer-name">🏪 {{ req.sellerNickname }}</span>
              <span class="req-date">{{ formatDate(req.createdAt) }}</span>
              <span class="status-badge" :class="req.status.toLowerCase()">{{ statusLabel(req.status) }}</span>
            </div>
            <p v-if="req.message" class="req-message">"{{ req.message }}"</p>
          </div>

          <!-- 구매자 액션 버튼 -->
          <div class="req-actions">
            <button v-if="req.status === 'REQUESTED'" class="btn btn-cancel" @click="handleCancel(req)">
              취소
            </button>
            <span v-else class="done-label">{{ statusLabel(req.status) }}</span>
          </div>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <div v-if="sentTotalPages > 1" class="pagination">
        <button v-for="p in sentTotalPages" :key="p" :class="{ active: p - 1 === sentPage }"
          @click="loadSent(p - 1)">{{ p }}</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { tradesApi } from '@/api/trades'

const route = useRoute()

// 탭: 'received' or 'sent' — URL 경로 기반으로 초기값 결정
const tab = ref(route.path.includes('/sent') ? 'sent' : 'received')

// 받은 요청
const receivedList = ref([])
const receivedPage = ref(0)
const receivedTotalPages = ref(0)
const totalReceived = ref(0)
const receivedLoading = ref(false)

// 보낸 요청
const sentList = ref([])
const sentPage = ref(0)
const sentTotalPages = ref(0)
const totalSent = ref(0)
const sentLoading = ref(false)

async function loadReceived(page = 0) {
  receivedLoading.value = true
  try {
    const res = await tradesApi.getReceivedRequests({ page, size: 10 })
    const data = res.data.data
    receivedList.value = data.content
    receivedPage.value = data.number
    receivedTotalPages.value = data.totalPages
    totalReceived.value = data.totalElements
  } catch (e) {
    console.error(e)
  } finally {
    receivedLoading.value = false
  }
}

async function loadSent(page = 0) {
  sentLoading.value = true
  try {
    const res = await tradesApi.getSentRequests({ page, size: 10 })
    const data = res.data.data
    sentList.value = data.content
    sentPage.value = data.number
    sentTotalPages.value = data.totalPages
    totalSent.value = data.totalElements
  } catch (e) {
    console.error(e)
  } finally {
    sentLoading.value = false
  }
}

async function handleAccept(req) {
  if (!confirm(`'${req.buyerNickname}'님의 거래 요청을 수락하시겠습니까?`)) return
  try {
    await tradesApi.accept(req.id)
    await loadReceived(receivedPage.value)
  } catch (e) {
    alert(e.response?.data?.message || '수락에 실패했습니다.')
  }
}

async function handleReject(req) {
  if (!confirm(`'${req.buyerNickname}'님의 거래 요청을 거절하시겠습니까?`)) return
  try {
    await tradesApi.reject(req.id)
    await loadReceived(receivedPage.value)
  } catch (e) {
    alert(e.response?.data?.message || '거절에 실패했습니다.')
  }
}

async function handleComplete(req) {
  if (!confirm('거래를 완료 처리하시겠습니까? 상품이 "판매 완료" 상태로 변경됩니다.')) return
  try {
    await tradesApi.complete(req.id)
    await loadReceived(receivedPage.value)
  } catch (e) {
    alert(e.response?.data?.message || '거래 완료 처리에 실패했습니다.')
  }
}

async function handleCancel(req) {
  if (!confirm('거래 요청을 취소하시겠습니까?')) return
  try {
    await tradesApi.cancel(req.id)
    await loadSent(sentPage.value)
  } catch (e) {
    alert(e.response?.data?.message || '취소에 실패했습니다.')
  }
}

function statusLabel(status) {
  return {
    REQUESTED: '요청중',
    ACCEPTED: '수락됨',
    REJECTED: '거절됨',
    COMPLETED: '거래완료',
    CANCELLED: '취소됨',
  }[status] || status
}

function formatDate(dt) {
  if (!dt) return ''
  return new Date(dt).toLocaleDateString('ko-KR')
}

// 탭 전환 시 데이터 로드
watch(tab, (newTab) => {
  if (newTab === 'received' && receivedList.value.length === 0) loadReceived()
  if (newTab === 'sent' && sentList.value.length === 0) loadSent()
})

onMounted(() => {
  loadReceived()
  loadSent()
})
</script>

<style scoped>
.trades-wrap { max-width: 760px; margin: 0 auto; }
h1 { font-size: 1.4rem; margin-bottom: 20px; }

.tabs { display: flex; gap: 4px; border-bottom: 2px solid #eee; margin-bottom: 24px; }
.tabs button { padding: 10px 20px; border: none; background: none; cursor: pointer; font-size: 0.95rem; color: #888; border-bottom: 2px solid transparent; margin-bottom: -2px; transition: all 0.15s; }
.tabs button.active { color: #1a73e8; border-bottom-color: #1a73e8; font-weight: 600; }
.count { font-size: 0.85rem; color: #1a73e8; }

.loading, .empty { text-align: center; padding: 60px; color: #aaa; }

.request-list { display: flex; flex-direction: column; gap: 14px; }

.request-card {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 18px;
  display: grid;
  grid-template-columns: auto 1fr auto;
  gap: 16px;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}

.inst-link { display: flex; align-items: center; gap: 12px; color: #333; }
.thumb { width: 70px; height: 70px; object-fit: cover; border-radius: 8px; flex-shrink: 0; }
.thumb-placeholder { width: 70px; height: 70px; background: #f0f0f0; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 1.6rem; flex-shrink: 0; }
.inst-title { font-weight: 600; font-size: 0.95rem; margin-bottom: 4px; }
.inst-price { color: #1a73e8; font-weight: 700; font-size: 0.9rem; }

.req-meta { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; margin-bottom: 6px; }
.buyer-name { font-size: 0.88rem; color: #555; }
.req-date { font-size: 0.82rem; color: #bbb; }
.req-message { font-size: 0.88rem; color: #666; font-style: italic; }

.status-badge { padding: 3px 10px; border-radius: 20px; font-size: 0.78rem; font-weight: 600; }
.status-badge.requested { background: #e8f0fe; color: #1a73e8; }
.status-badge.accepted { background: #e8f5e9; color: #2e7d32; }
.status-badge.rejected { background: #fbe9e7; color: #bf360c; }
.status-badge.completed { background: #ede7f6; color: #4527a0; }
.status-badge.cancelled { background: #f5f5f5; color: #9e9e9e; }

.req-actions { display: flex; flex-direction: column; gap: 8px; min-width: 90px; }
.btn { padding: 8px 14px; border: none; border-radius: 8px; font-size: 0.85rem; cursor: pointer; font-weight: 600; white-space: nowrap; }
.btn-accept { background: #e8f5e9; color: #2e7d32; }
.btn-accept:hover { background: #c8e6c9; }
.btn-reject { background: #fbe9e7; color: #c62828; }
.btn-reject:hover { background: #ffcdd2; }
.btn-complete { background: #ede7f6; color: #4527a0; }
.btn-complete:hover { background: #d1c4e9; }
.btn-cancel { background: #fff3e0; color: #e65100; }
.btn-cancel:hover { background: #ffe0b2; }
.done-label { font-size: 0.82rem; color: #aaa; text-align: center; }

.pagination { display: flex; gap: 6px; justify-content: center; margin-top: 24px; }
.pagination button { width: 36px; height: 36px; border: 1px solid #ddd; border-radius: 8px; background: #fff; cursor: pointer; font-size: 0.88rem; }
.pagination button.active { background: #1a73e8; color: #fff; border-color: #1a73e8; }

@media (max-width: 600px) {
  .request-card { grid-template-columns: 1fr; }
  .req-actions { flex-direction: row; }
}
</style>
