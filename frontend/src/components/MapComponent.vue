<template>
  <div class="relative w-full bg-white rounded-lg shadow overflow-hidden">
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

const mapContainer = ref(null)
const mapInstance = ref(null)
const selectedMapType = ref('roadmap')

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
    center: new window.kakao.maps.LatLng(33.450701, 126.570667),
    level: 3,
  }
  mapInstance.value = new window.kakao.maps.Map(mapContainer.value, options)
  setMapType(selectedMapType.value)
}

function setMapType(type) {
  if (!mapInstance.value) return
  // const MapTypeId = window.kakao.maps.MapTypeId
  // const mapTypeId = type === 'roadmap' ? MapTypeId.ROADMAP : MapTypeId.HYBRID
  // mapInstance.value.setMapTypeId(mapTypeId)
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
