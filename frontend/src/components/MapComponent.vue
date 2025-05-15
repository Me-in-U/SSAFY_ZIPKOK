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
import { ref, onMounted } from 'vue'
import axios from 'axios'

// 부모로 보낼 이벤트 정의
const emit = defineEmits(['select-property'])

const mapContainer = ref(null)
const mapInstance = ref(null)
const selectedMapType = ref('roadmap')

// 화면에 표시할 마커 리스트
const markers = ref([])

onMounted(() => {
  loadKakaoMap()
})

function loadKakaoMap() {
  if (window.kakao?.maps) {
    initMap()
    return
  }

  const script = document.createElement('script')
  script.src =
    'https://dapi.kakao.com/v2/maps/sdk.js?appkey=622cf4d5b80a1ab21c638844092e7856&autoload=false'
  script.async = true
  document.head.appendChild(script)

  script.onload = () => {
    window.kakao.maps.load(() => {
      initMap()
    })
  }
}

function initMap() {
  const options = {
    center: new window.kakao.maps.LatLng(37.543361625522714, 127.01908556679024),
    level: 5,
  }
  mapInstance.value = new window.kakao.maps.Map(mapContainer.value, options)
  setMapType(selectedMapType.value)

  fetchByBounds()
  // 1) idle 이벤트 등록: 범위 요청
  window.kakao.maps.event.addListener(mapInstance.value, 'idle', fetchByBounds)
}

// 2) bounds 계산 & 서버 요청
function fetchByBounds() {
  const level = mapInstance.value.getLevel()
  if (level > 5) {
    hideMarkers()
    return
  }

  const bounds = mapInstance.value.getBounds()
  const sw = bounds.getSouthWest()
  const ne = bounds.getNorthEast()

  const params = {
    minLat: sw.getLat(),
    maxLat: ne.getLat(),
    minLng: sw.getLng(),
    maxLng: ne.getLng(),
  }

  // TODO: 배포시 URL 변경
  // https://api.ssafy.blog/api/v1/house/search
  axios
    .get('http://localhost:8080/api/v1/house/search', { params })
    .then((res) => {
      console.log('응답 전체:', res.data)
      // 실제 데이터 리스트가 있는 필드명(houses, list 등)을 확인하세요.
      const list = Array.isArray(res.data)
        ? res.data
        : Array.isArray(res.data.houses)
          ? res.data.houses
          : Array.isArray(res.data.list)
            ? res.data.list
            : []

      // 기존 마커 제거
      hideMarkers()

      // 새 마커 + 텍스트(CustomOverlay) 그리기
      list.forEach((house) => {
        const position = new window.kakao.maps.LatLng(house.latitude, house.longitude)

        // 1) 기본 마커
        const marker = new window.kakao.maps.Marker({
          position,
          clickable: true,
        })
        marker.setMap(mapInstance.value)

        // 2) 텍스트 오버레이
        const overlay = new window.kakao.maps.CustomOverlay({
          position,
          content: `<div style="
            padding:2px 4px;
            background:rgba(255,255,255,0.8);
            border:1px solid #777;
            border-radius:4px;
            font-size:12px;
            white-space:nowrap;
          ">${house.aptNm}</div>`,
          yAnchor: 2, // 마커 아이콘 위쪽에 위치
        })
        overlay.setMap(mapInstance.value)

        // 클릭 시 사이드바 열기
        window.kakao.maps.event.addListener(marker, 'click', () => {
          emit('select-property', house)
        })

        // 상태 관리용 배열에 함께 저장
        markers.value.push({ marker, overlay })
      })
    })
    .catch((err) => console.error(err))
}

// 마커와 오버레이를 모두 지도에서 제거하고 배열까지 비우는 함수
function hideMarkers() {
  markers.value.forEach(({ marker, overlay }) => {
    marker.setMap(null)
    overlay.setMap(null)
  })
  markers.value = []
}

function setMapType(type) {
  if (!mapInstance.value) return
  const { MapTypeId } = window.kakao.maps

  if (type === 'roadmap') {
    mapInstance.value.setMapTypeId(MapTypeId.ROADMAP)
  } else {
    mapInstance.value.setMapTypeId(MapTypeId.SKYVIEW)
  }

  selectedMapType.value = type
}

function zoomIn() {
  if (mapInstance.value) {
    mapInstance.value.setLevel(mapInstance.value.getLevel() - 1)
  }
}

function zoomOut() {
  if (mapInstance.value) {
    mapInstance.value.setLevel(mapInstance.value.getLevel() + 1)
  }
}
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
