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
  properties: Array, // 화면에 보여줄 주택 리스트
  searchResults: Array, // 검색 결과 리스트
  favoriteSeqs: Array, // 즐겨찾기 aptSeq 리스트
  showBase: Boolean, // 기본 마커 토글
  showFavorite: Boolean, // 즐겨찾기 마커 토글
  showSearch: Boolean, // 검색 결과 토글
})
const { searchResults, favoriteSeqs, showBase, showFavorite, showSearch } = toRefs(props)

const mapContainer = ref(null)
const mapInstance = ref(null)
const baseMarkers = ref([])
const baseOverlays = ref([])
const favoriteMarkers = ref([])
const favoriteOverlays = ref([])
const searchMarkers = ref([])
const searchOverlays = ref([])
const selectedMapType = ref('roadmap')

// 1) 모든 watcher 앞에 정의
function updateVisibility() {
  // 기본 마커
  baseMarkers.value.forEach((m) => m.setMap(showBase.value ? mapInstance.value : null))
  baseOverlays.value.forEach((o) => o.setMap(showBase.value ? mapInstance.value : null))
  // 검색
  searchMarkers.value.forEach((m) => m.setMap(showSearch.value ? mapInstance.value : null))
  searchOverlays.value.forEach((o) => o.setMap(showSearch.value ? mapInstance.value : null))
  // 즐겨찾기
  favoriteMarkers.value.forEach((m) => m.setMap(showFavorite.value ? mapInstance.value : null))
  favoriteOverlays.value.forEach((o) => o.setMap(showFavorite.value ? mapInstance.value : null))
}

function clearMarkers(markers, overlays) {
  markers.forEach((m) => m.setMap(null))
  overlays.forEach((o) => o.setMap(null))
  markers.splice(0)
  overlays.splice(0)
}

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

// 지도 초기화 & 사용자 위치
function initMap() {
  mapInstance.value = new window.kakao.maps.Map(mapContainer.value, {
    center: new window.kakao.maps.LatLng(37.54336, 127.01908),
    level: 5,
  })

  // base 마커 불러오기
  fetchBase()
  window.kakao.maps.event.addListener(mapInstance.value, 'idle', fetchBase)
  // 맵이 준비되자마자 즐겨찾기 로드
  loadFavorites(favoriteSeqs.value)
  // 현재 위치 가져오기
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords
        const userLatLng = new window.kakao.maps.LatLng(latitude, longitude)

        // 1) 사용자 위치 마커
        new window.kakao.maps.Marker({
          position: userLatLng,
          map: mapInstance.value,
          title: '현재 위치',
          // 원한다면 커스텀 아이콘 지정 가능
        })

        // 2) (선택) 지도 센터를 사용자 위치로 이동
        mapInstance.value.setCenter(userLatLng)
      },
      (err) => {
        console.warn('위치 정보를 가져올 수 없습니다.', err)
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0,
      },
    )
  }
}

// 기본 매물
function fetchBase() {
  if (mapInstance.value.getLevel() > 5 || showSearch.value) {
    clearMarkers(baseMarkers.value, baseOverlays.value)
    return
  }
  const bounds = mapInstance.value.getBounds()
  axios
    .get('https://api.ssafy.blog/api/v1/house/search', {
      params: {
        minLat: bounds.getSouthWest().getLat(),
        maxLat: bounds.getNorthEast().getLat(),
        minLng: bounds.getSouthWest().getLng(),
        maxLng: bounds.getNorthEast().getLng(),
      },
    })
    .then((res) => {
      clearMarkers(baseMarkers.value, baseOverlays.value)
      const list = Array.isArray(res.data.houses) ? res.data.houses : res.data
      list.forEach((h) => {
        const pos = new window.kakao.maps.LatLng(h.latitude, h.longitude)
        const m = new window.kakao.maps.Marker({ position: pos, map: mapInstance.value, zIndex: 1 })
        const ov = new window.kakao.maps.CustomOverlay({
          position: pos,
          content: `<div style="padding:2px 4px; background:rgba(255,255,255,0.8); border:1px solid #777; border-radius:4px; font-size:12px;">${h.aptNm}</div>`,
          yAnchor: 2,
          zIndex: 1,
        })
        ov.setMap(mapInstance.value)
        window.kakao.maps.event.addListener(m, 'click', () => emit('select-property', h))
        baseMarkers.value.push(m)
        baseOverlays.value.push(ov)
      })
      updateVisibility()
    })
}

// 즐겨찾기 watcher
async function loadFavorites(seqs) {
  if (!mapInstance.value) return
  clearMarkers(favoriteMarkers.value, favoriteOverlays.value)
  if (!seqs || seqs.length === 0) {
    console.log('[즐겨찾기] 없음')
    return updateVisibility()
  }
  const { data: list } = await axios.get('https://api.ssafy.blog/api/v1/house/batch', {
    params: { seqs: seqs.join(',') },
  })
  list.forEach((h) => {
    const pos = new window.kakao.maps.LatLng(h.latitude, h.longitude)
    const m = new window.kakao.maps.Marker({
      position: pos,
      map: mapInstance.value,
      icon: { url: '/favorite-icon.png', size: new window.kakao.maps.Size(24, 24), zIndex: 2 },
    })
    const ov = new window.kakao.maps.CustomOverlay({
      position: pos,
      content: `<div style="padding:2px 4px; background:rgba(255,245,235,0.9); border:3px solid #e52200; border-radius:4px; font-size:12px; color:#000000">
                  ${h.aptNm}
                </div>`,
      yAnchor: 2,
      zIndex: 2,
    })
    ov.setMap(mapInstance.value)
    window.kakao.maps.event.addListener(m, 'click', () => emit('select-property', h))
    favoriteMarkers.value.push(m)
    favoriteOverlays.value.push(ov)
  })
  updateVisibility()
}
watch(favoriteSeqs, loadFavorites, { deep: true })

// 검색 결과 watcher
watch(
  searchResults,
  async (raw) => {
    clearMarkers(searchMarkers.value, searchOverlays.value)
    if (!raw || raw.length === 0) {
      return updateVisibility()
    }
    // 배치 조회 or 바로 활용
    const list =
      typeof raw[0] === 'string'
        ? (
            await axios.get('https://api.ssafy.blog/api/v1/house/batch', {
              params: { seqs: raw.join(',') },
            })
          ).data
        : raw
    // 지도 표시
    const bounds = new window.kakao.maps.LatLngBounds()
    list.forEach((h) => {
      const lat = parseFloat(h.latitude),
        lng = parseFloat(h.longitude)
      if (isNaN(lat) || isNaN(lng)) return
      const pos = new window.kakao.maps.LatLng(lat, lng)
      const m = new window.kakao.maps.Marker({ position: pos, map: mapInstance.value, zIndex: 3 })
      const ov = new window.kakao.maps.CustomOverlay({
        position: pos,
        content: `
          <div style="
            padding:2px 4px;
            background:rgba(255,245,235,0.9);
            border:3px solid #a0d2f0;
            border-radius:4px;
            font-size:12px;
            color:#000000
          ">
            ${h.aptNm}
          </div>
        `,
        yAnchor: 2,
        zIndex: 3,
      })
      ov.setMap(mapInstance.value)
      window.kakao.maps.event.addListener(m, 'click', () => emit('select-property', h))
      searchMarkers.value.push(m)
      searchOverlays.value.push(ov)
      bounds.extend(pos)
    })
    if (!bounds.isEmpty()) mapInstance.value.setBounds(bounds)
    updateVisibility()
  },
  { immediate: true },
)

// 5) 토글만 바뀔 때
watch([showBase, showSearch, showFavorite], updateVisibility)

// 지도 타입 변경
function setMapType(type) {
  const { MapTypeId } = window.kakao.maps
  mapInstance.value.setMapTypeId(type === 'roadmap' ? MapTypeId.ROADMAP : MapTypeId.SKYVIEW)
  selectedMapType.value = type
}

// 확대
function zoomIn() {
  mapInstance.value.setLevel(mapInstance.value.getLevel() - 1)
}

// 축소
function zoomOut() {
  mapInstance.value.setLevel(mapInstance.value.getLevel() + 1)
}

// 주소기반 검색 후 지도 이동
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
