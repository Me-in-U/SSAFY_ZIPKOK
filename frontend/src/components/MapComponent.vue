<template>
  <div class="relative w-full rounded-lg shadow overflow-hidden">
    <!-- Kakao Maps -->
    <div ref="mapContainer" id="map" class="w-full h-full relative overflow-hidden z-0"></div>

    <!-- 지도 타입 컨트롤 -->
    <div
      class="absolute top-4 right-4 z-10 flex rounded border border-gray-400 text-xs font-medium overflow-hidden"
    >
      <span
        :class="[
          'w-[65px] h-[30px] flex items-center justify-center cursor-pointer transition',
          selectedMapType === 'roadmap'
            ? 'bg-[#425470] text-white'
            : 'bg-white text-gray-800 hover:bg-gray-100',
        ]"
        @click="setMapType('roadmap')"
      >
        지도
      </span>
      <span
        :class="[
          'w-[65px] h-[30px] flex items-center justify-center cursor-pointer transition',
          selectedMapType === 'skyview'
            ? 'bg-[#425470] text-white'
            : 'bg-white text-gray-800 hover:bg-gray-100',
        ]"
        @click="setMapType('skyview')"
      >
        스카이뷰
      </span>
    </div>

    <!-- 확대/축소 컨트롤 -->
    <div
      class="absolute top-[50px] right-4 z-10 flex flex-col w-9 h-[80px] bg-gray-100 rounded border border-gray-400 overflow-hidden"
    >
      <span
        @click="zoomIn"
        class="w-full h-1/2 flex items-center justify-center border-b border-gray-300 cursor-pointer hover:bg-gray-50"
      >
        <img
          src="https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/ico_plus.png"
          alt="확대"
          class="w-4 h-4"
        />
      </span>
      <span
        @click="zoomOut"
        class="w-full h-1/2 flex items-center justify-center cursor-pointer hover:bg-gray-50"
      >
        <img
          src="https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/ico_minus.png"
          alt="축소"
          class="w-4 h-4"
        />
      </span>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted, watch, toRefs } from 'vue'
import axios from 'axios'

// 부모 이벤트
const emit = defineEmits(['select-property'])

// props 정의 및 분해
const props = defineProps({
  properties: Array,
  searchResults: Array,
  showSearch: Boolean,
})
const { searchResults, showSearch } = toRefs(props)

const mapContainer = ref(null)
const mapInstance = ref(null)
const baseMarkers = ref([])
const baseOverlays = ref([])
const searchMarkers = ref([])
const searchOverlays = ref([])
// 새로 추가: 배치 조회된 상세 결과를 담을 곳
const detailedResults = ref([]) // HouseInfo[]
onMounted(loadKakaoMap)

function loadKakaoMap() {
  if (window.kakao?.maps) return initMap()
  const s = document.createElement('script')
  s.src =
    'https://dapi.kakao.com/v2/maps/sdk.js?appkey=622cf4d5b80a1ab21c638844092e7856&autoload=false&libraries=services'
  s.async = true
  document.head.appendChild(s)
  s.onload = () => window.kakao.maps.load(initMap)
}

function initMap() {
  mapInstance.value = new window.kakao.maps.Map(mapContainer.value, {
    center: new window.kakao.maps.LatLng(37.54336, 127.01908),
    level: 5,
  })
  fetchBase()
  window.kakao.maps.event.addListener(mapInstance.value, 'idle', fetchBase)
}

// 기본 매물 로드
function fetchBase() {
  if (mapInstance.value.getLevel() > 5 || showSearch.value) {
    hideAll(baseMarkers.value, baseOverlays.value)
    return
  }
  const b = mapInstance.value.getBounds()
  axios
    .get('https://api.ssafy.blog/api/v1/house/search', {
      params: {
        minLat: b.getSouthWest().getLat(),
        maxLat: b.getNorthEast().getLat(),
        minLng: b.getSouthWest().getLng(),
        maxLng: b.getNorthEast().getLng(),
      },
    })
    .then((res) => {
      hideAll(baseMarkers.value, baseOverlays.value)
      const list = Array.isArray(res.data.houses) ? res.data.houses : res.data
      console.log('[Base Result]', list)
      list.forEach((h) => {
        const pos = new window.kakao.maps.LatLng(h.latitude, h.longitude)
        // 마커
        const m = new window.kakao.maps.Marker({ position: pos, map: mapInstance.value })
        // 텍스트 오버레이
        const ov = new window.kakao.maps.CustomOverlay({
          position: pos,
          content: `<div style="
          padding:2px 4px;
          background:rgba(255,255,255,0.8);
          border:1px solid #777;
          border-radius:4px;
          font-size:12px;
          white-space:nowrap;
        ">${h.aptNm}</div>`,
          yAnchor: 2,
        })
        ov.setMap(mapInstance.value)
        // 클릭 이벤트
        window.kakao.maps.event.addListener(m, 'click', () => emit('select-property', h))
        baseMarkers.value.push(m)
        baseOverlays.value.push(ov)
      })
    })
}

// 1) searchResults가 바뀌면
watch(
  searchResults,
  async (raw) => {
    console.log('[Search Result]', raw)
    // 1-1) 모두 지우고
    clearAll(searchMarkers.value, searchOverlays.value)

    if (!raw || raw.length === 0) {
      detailedResults.value = []
      return
    }

    let list
    // 1-2) 만약 첫 번째 엘리먼트가 문자열(apt_seq)이면, 백엔드에 배치 조회
    if (typeof raw[0] === 'string') {
      const seqs = raw.join(',')
      const res = await axios.get('https://api.ssafy.blog/api/v1/house/batch', {
        params: { seqs },
      })
      list = res.data // HouseInfo[]
    } else {
      // 이미 HouseInfo 객체 배열이 넘어온 경우
      list = raw
    }
    console.log('[Search Result]', list)
    detailedResults.value = list

    // 2) detailedResults 로 동일하게 bounds 맞추고 마커 생성
    const bounds = new window.kakao.maps.LatLngBounds()
    list.forEach((h) => {
      const lat = parseFloat(h.latitude)
      const lng = parseFloat(h.longitude)
      if (isNaN(lat) || isNaN(lng)) return
      const pos = new window.kakao.maps.LatLng(lat, lng)

      const m = new window.kakao.maps.Marker({
        position: pos,
        map: mapInstance.value,
        title: h.aptNm,
      })
      const ov = new window.kakao.maps.CustomOverlay({
        position: pos,
        content: `<div style="
        padding:2px 4px;
        background:rgba(255,255,255,0.8);
        border:1px solid #777;
        border-radius:4px;
        font-size:12px;
        white-space:nowrap;
      ">${h.aptNm}</div>`,
        yAnchor: 2,
      })
      ov.setMap(mapInstance.value)
      window.kakao.maps.event.addListener(m, 'click', () => emit('select-property', h))

      searchMarkers.value.push(m)
      searchOverlays.value.push(ov)
      bounds.extend(pos)
    })

    if (!bounds.isEmpty()) {
      mapInstance.value.setBounds(bounds)
    }
  },
  { immediate: true },
)

// 검색 토글 시
watch(showSearch, (visible) => {
  if (visible) {
    hideMarkersOnly(baseMarkers.value, baseOverlays.value)
    showMarkersOnly(searchMarkers.value, searchOverlays.value)
  } else {
    hideMarkersOnly(searchMarkers.value, searchOverlays.value)
    fetchBase()
  }
})

// 헬퍼: 마커+오버레이 숨기기/보이기
function hideAll(markers, overlays) {
  markers.forEach((m) => m.setMap(null))
  overlays.forEach((o) => o.setMap(null))
  markers.splice(0)
  overlays.splice(0)
}
// 기존 헬퍼: 완전 제거(검색 결과 업데이트 시 사용)
function clearAll(markers, overlays) {
  markers.forEach((m) => m.setMap(null))
  overlays.forEach((o) => o.setMap(null))
  markers.splice(0)
  overlays.splice(0)
}

// 헬퍼: 마커·오버레이만 숨기고 배열은 유지
function hideMarkersOnly(markers, overlays) {
  markers.forEach((m) => m.setMap(null))
  overlays.forEach((o) => o.setMap(null))
}

// 헬퍼: 마커·오버레이만 보이기
function showMarkersOnly(markers, overlays) {
  markers.forEach((m) => m.setMap(mapInstance.value))
  overlays.forEach((o) => o.setMap(mapInstance.value))
}

function zoomIn() {
  mapInstance.value.setLevel(mapInstance.value.getLevel() - 1)
}
function zoomOut() {
  mapInstance.value.setLevel(mapInstance.value.getLevel() + 1)
}
function setMapType(type) {
  const { MapTypeId } = window.kakao.maps
  mapInstance.value.setMapTypeId(type === 'roadmap' ? MapTypeId.ROADMAP : MapTypeId.SKYVIEW)
}
defineExpose({
  panToAddress: (address) => {
    new window.kakao.maps.services.Geocoder().addressSearch(address, (r, s) => {
      if (s === window.kakao.maps.services.Status.OK) {
        const c = new window.kakao.maps.LatLng(r[0].y, r[0].x)
        mapInstance.value.panTo(c)
        mapInstance.value.setLevel(5)
      }
    })
  },
})
</script>

<style scoped>
html,
body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}
</style>
