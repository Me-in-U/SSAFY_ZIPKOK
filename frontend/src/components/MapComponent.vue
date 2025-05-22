<template>
  <div class="relative w-full rounded-lg shadow-lg overflow-hidden">
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
import { useRoute } from 'vue-router'
import axios from 'axios'
import sido from '@/assets/geojson/sido.json'
import gwangju from '@/assets/geojson/29_Gwangju.json'
import sigungu from '@/assets/geojson/sigungu.json'
import emd from '@/assets/geojson/emd.json'
import * as turf from '@turf/turf'

// emit props
const route = useRoute()
const emit = defineEmits(['select-property'])
const props = defineProps({
  properties: Array, // 화면에 보여줄 주택 리스트
  searchResults: Array, // 검색 결과 리스트
  searchQuery: String, // 검색어
  favoriteSeqs: Array, // 즐겨찾기 aptSeq 리스트
  showBase: Boolean, // 기본 마커 토글
  showFavorite: Boolean, // 즐겨찾기 마커 토글
  showSearch: Boolean, // 검색 결과 토글
})
const { searchResults, favoriteSeqs, showBase, showFavorite, showSearch } = toRefs(props)

// refs
const mapContainer = ref(null)
const mapInstance = ref(null)
const baseMarkers = ref([])
const baseOverlays = ref([])
const favoriteMarkers = ref([])
const favoriteOverlays = ref([])
const searchMarkers = ref([])
const searchOverlays = ref([])
const selectedMapType = ref('roadmap')
const sidoPolygons = ref([])
const sigunguPolygons = ref([])
const emdPolygons = ref([])
const infoOverlay = ref(null)

// onMounted
onMounted(loadKakaoMap)

// 모든 watcher 앞에 정의
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

function loadKakaoMap() {
  console.log('[카카오맵 로드]')
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
  // 첫 렌더
  drawByZoom(mapInstance.value.getLevel())

  // base 마커 불러오기
  fetchBase()
  window.kakao.maps.event.addListener(mapInstance.value, 'idle', fetchBase)

  // 줌이 바뀔 때마다
  window.kakao.maps.event.addListener(mapInstance.value, 'zoom_changed', () => {
    drawByZoom(mapInstance.value.getLevel())
    // console.log('[줌 변경]', mapInstance.value.getLevel())
  })

  // 3) 팬·드래그(=idle) 이후에도 다시 그리기
  window.kakao.maps.event.addListener(mapInstance.value, 'idle', () =>
    drawByZoom(mapInstance.value.getLevel()),
  )

  // 맵이 준비되자마자 즐겨찾기 로드
  loadFavorites(favoriteSeqs.value)

  // 현재 위치 가져오기
  getMyLocation()

  infoOverlay.value = new window.kakao.maps.CustomOverlay({
    yAnchor: 1.3,
    zIndex: 999,
  })
  infoOverlay.value.setMap(mapInstance.value)
}

function getMyLocation() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      (position) => {
        const { latitude, longitude } = position.coords
        const userLatLng = new window.kakao.maps.LatLng(latitude, longitude)

        // 1) 사용자 위치 마커
        const html = `
          <div class="my-loc-overlay">
            <div class="pulse"></div>
            <div class="dot"></div>
          </div>
        `
        const overlay = new window.kakao.maps.CustomOverlay({
          position: userLatLng,
          content: html,
          yAnchor: 0.5,
          xAnchor: 0.5,
          zIndex: 10,
        })
        overlay.setMap(mapInstance.value)
        mapInstance.value.setCenter(userLatLng)
        console.log('[현재 위치] 위도:', latitude, '경도:', longitude)
      },
      (err) => {
        console.warn('[위치 정보 오류]:', err)
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0,
      },
    )
  }
}

// geojson 들을 한데 모아 놓고
const allGeoJSON = { sido, gwangju, sigungu, emd }

// 각 geojson type 별로 feature별 bbox 생성
const featureBBoxes = {}
for (const [key, geojson] of Object.entries(allGeoJSON)) {
  featureBBoxes[key] = geojson.features.map((feat) => turf.bbox(feat)) // [minX,minY,maxX,maxY]
}

function inViewport(bbox, bounds) {
  const [minLng, minLat, maxLng, maxLat] = bbox
  return !(
    maxLat < bounds.getSouthWest().getLat() ||
    minLat > bounds.getNorthEast().getLat() ||
    maxLng < bounds.getSouthWest().getLng() ||
    minLng > bounds.getNorthEast().getLng()
  )
}

function drawByZoom(level) {
  // clear old
  sidoPolygons.value.forEach((p) => p.setMap(null))
  sigunguPolygons.value.forEach((p) => p.setMap(null))
  emdPolygons.value.forEach((p) => p.setMap(null))
  sidoPolygons.value = []
  sigunguPolygons.value = []
  emdPolygons.value = []

  const bounds = mapInstance.value.getBounds()
  if (level >= 10) {
    // 시도 + 광주
    ;[
      ['sido', { strokeColor: '#888', fillColor: '#ee7777', zIndex: 0 }],
      [
        'gwangju',
        { strokeColor: '#888', fillColor: 'rgba(0, 0, 0, 0.0)', zIndex: 2, fillOpacity: 0 },
      ],
    ].forEach(([key, style]) => {
      allGeoJSON[key].features.forEach((feat, i) => {
        if (!inViewport(featureBBoxes[key][i], bounds)) return
        drawSingleFeature(feat, style, 'sido')
      })
    })
  } else if (level >= 8) {
    // 시도 + 광주
    ;[
      [
        'sido',
        {
          strokeColor: '#ff2222',
          fillColor: 'rgba(0, 0, 0, 0.0)',
          zIndex: 0,
          fillOpacity: 0,
          strokeWeight: 2.5,
        },
      ],
      [
        'gwangju',
        {
          strokeColor: '#ff2222',
          fillColor: 'rgba(0, 0, 0, 0.0)',
          zIndex: 2,
          fillOpacity: 0,
          strokeWeight: 2.5,
        },
      ],
    ].forEach(([key, style]) => {
      allGeoJSON[key].features.forEach((feat, i) => {
        if (!inViewport(featureBBoxes[key][i], bounds)) return
        drawSingleFeature(feat, style, 'sido')
      })
    })
    // 시군구
    allGeoJSON.sigungu.features.forEach((feat, i) => {
      if (!inViewport(featureBBoxes.sigungu[i], bounds)) return
      drawSingleFeature(
        feat,
        { strokeColor: '#888', fillColor: '#ee7777', zIndex: 5, fillOpacity: 0.05 },
        'sigungu',
      )
    })
  } else {
    // 시군구
    allGeoJSON.sigungu.features.forEach((feat, i) => {
      if (!inViewport(featureBBoxes.sigungu[i], bounds)) return
      drawSingleFeature(
        feat,
        {
          strokeColor: '#ff2222',
          fillColor: '#ee7777',
          zIndex: 6,
          fillOpacity: 0.001,
          strokeWeight: 2.5,
        },
        'sigungu',
      )
    })
    // EMD (읍·면·동)
    allGeoJSON.emd.features.forEach((feat, i) => {
      if (!inViewport(featureBBoxes.emd[i], bounds)) return
      drawSingleFeature(
        feat,
        {
          strokeColor: '#888',
          fillColor: '#ee7777',
          zIndex: 10,
          fillOpacity: 0.001,
          strokeWeight: 1.5,
        },
        'emd',
      )
    })
  }
}

function drawSingleFeature(feat, style, where) {
  const coords = feat.geometry.coordinates
  const rings = feat.geometry.type === 'Polygon' ? [coords] : coords
  rings.forEach((pg) =>
    pg.forEach((ring) => {
      const path = ring.map(([lng, lat]) => new window.kakao.maps.LatLng(lat, lng))
      const orig = style.fillOpacity ?? 0.1
      const hover = Math.min(orig + 0.2, 1)
      const poly = new window.kakao.maps.Polygon({
        map: mapInstance.value,
        path,
        strokeWeight: style.strokeWeight ?? 2,
        strokeColor: style.strokeColor,
        strokeOpacity: 0.8,
        fillColor: style.fillColor,
        fillOpacity: orig,
        zIndex: style.zIndex,
      })
      window.kakao.maps.event.addListener(poly, 'mouseover', () =>
        poly.setOptions({ fillOpacity: hover }),
      )
      window.kakao.maps.event.addListener(poly, 'mouseout', () =>
        poly.setOptions({ fillOpacity: orig }),
      )
      window.kakao.maps.event.addListener(poly, 'click', () => {
        // 클릭된 폴리곤 전체 경계 생성
        const bounds = new window.kakao.maps.LatLngBounds()
        path.forEach((pt) => bounds.extend(pt))

        // SW, NE 가져와서 중간 좌표 계산
        const sw = bounds.getSouthWest()
        const ne = bounds.getNorthEast()
        const center = new window.kakao.maps.LatLng(
          (sw.getLat() + ne.getLat()) / 2,
          (sw.getLng() + ne.getLng()) / 2,
        )

        // 3) 부드럽게 이동
        mapInstance.value.panTo(center)
      })

      // 마우스 포인터 위에 이름 오버레이
      window.kakao.maps.event.addListener(poly, 'mousemove', (mouseEvent) => {
        let name = ''
        if (where === 'sido') name = feat.properties.CTP_KOR_NM
        else if (where === 'sigungu') name = feat.properties.SIG_KOR_NM
        else if (where === 'emd') name = feat.properties.EMD_KOR_NM

        infoOverlay.value.setContent(
          `<div style="padding:4px 8px; background:rgba(0,0,0,0.6); color:white; border-radius:4px; font-size:12px;">
              ${name}
            </div>`,
        )

        // 2) 지도에 붙이고 위치 갱신
        infoOverlay.value.setMap(mapInstance.value)
        infoOverlay.value.setPosition(mouseEvent.latLng)
      })

      window.kakao.maps.event.addListener(poly, 'mouseout', () => {
        infoOverlay.value.setMap(null)
      })

      if (where === 'sido') sidoPolygons.value.push(poly)
      else if (where === 'sigungu') sigunguPolygons.value.push(poly)
      else if (where === 'emd') emdPolygons.value.push(poly)
    }),
  )
}

// 기본 매물
function fetchBase() {
  if (mapInstance.value.getLevel() > 5 || showSearch.value) {
    clearMarkers(baseMarkers.value, baseOverlays.value)
    return
  }
  // console.log('[기본 매물] 마커 추가')
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
  console.log('[즐겨찾기] 마커 추가')
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
    console.log('[검색 결과] 변경:', raw)
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
      console.log('[검색 결과] 반영 완료')
    })
    if (!bounds.isEmpty()) mapInstance.value.setBounds(bounds)
    updateVisibility()
  },
  { immediate: true },
)
watch(
  searchResults,
  (houses) => {
    clearMarkers(searchMarkers.value, searchOverlays.value)
    if (!houses || houses.length === 0) return updateVisibility()

    const bounds = new window.kakao.maps.LatLngBounds()
    const geocoder = new window.kakao.maps.services.Geocoder()

    houses.forEach((h) => {
      // 위도/경도가 있으면 바로 사용
      if (h.latitude && h.longitude) {
        const pos = new window.kakao.maps.LatLng(parseFloat(h.latitude), parseFloat(h.longitude))
        createMarker(pos, h)
        bounds.extend(pos)

        // 그렇지 않으면 도로명 주소로 지오코딩
      } else if (h.roadAddress) {
        geocoder.addressSearch(h.roadAddress, (results, status) => {
          if (status === window.kakao.maps.services.Status.OK) {
            const r = results[0]
            const pos = new window.kakao.maps.LatLng(r.y, r.x)
            createMarker(pos, h)
            bounds.extend(pos)
            mapInstance.value.setBounds(bounds)
          }
        })
      }
    })

    if (!bounds.isEmpty()) {
      mapInstance.value.setBounds(bounds)
    }

    updateVisibility()
  },
  { immediate: true },
)

// 즐겨찾기 매물 클릭시 지도 이동 (URL detail 쿼리 감지 → batch API 호출 → panToCoords)
watch(
  () => route.query.detail,
  async (aptSeq) => {
    if (!aptSeq || !mapInstance.value) return
    try {
      const { data: list } = await axios.get('https://api.ssafy.blog/api/v1/house/batch', {
        params: { seqs: aptSeq },
      })
      console.log('단일 매물 좌표 조회', list)
      const house = Array.isArray(list) ? list[0] : null
      if (house) panToCoords(house)
    } catch (err) {
      console.error('단일 매물 좌표 조회 실패', err)
    }
  },
  { immediate: true },
)

// 지도 이동 헬퍼
function panToCoords({ latitude, longitude }) {
  if (!latitude || !longitude || !mapInstance.value) return
  const lat = parseFloat(latitude)
  const lng = parseFloat(longitude)
  console.log('[지도 이동] 좌표:', lat, lng)
  const pos = new window.kakao.maps.LatLng(lat, lng)
  mapInstance.value.setCenter(pos)
  mapInstance.value.setLevel(5)
}

// 검색 결과 마커 클릭 시
function createMarker(position, house) {
  const m = new window.kakao.maps.Marker({ position, map: mapInstance.value, zIndex: 3 })
  const ov = new window.kakao.maps.CustomOverlay({
    position,
    content: `<div style="padding:2px 4px; background:rgba(255,245,235,0.9);
                     border:3px solid #a0d2f0; border-radius:4px; font-size:12px; color:#000">
                ${house.aptNm}
              </div>`,
    yAnchor: 2,
    zIndex: 3,
  })
  m.setMap(mapInstance.value)
  ov.setMap(mapInstance.value)
  window.kakao.maps.event.addListener(m, 'click', () => emit('select-property', house))
  searchMarkers.value.push(m)
  searchOverlays.value.push(ov)
}

// 토글만 바뀔 때 updateVisibility
watch([showBase, showSearch, showFavorite], updateVisibility)

// 지도 타입 변경
function setMapType(type) {
  const { MapTypeId } = window.kakao.maps
  mapInstance.value.setMapTypeId(type === 'roadmap' ? MapTypeId.ROADMAP : MapTypeId.SKYVIEW)
  selectedMapType.value = type
  console.log('[지도 타입 변경]:', type)
}

// 확대
function zoomIn() {
  mapInstance.value.setLevel(mapInstance.value.getLevel() - 1)
  console.log('[확대]:', mapInstance.value.getLevel())
}

// 축소
function zoomOut() {
  mapInstance.value.setLevel(mapInstance.value.getLevel() + 1)
  console.log('[축소]:', mapInstance.value.getLevel())
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
    console.log('[주소 기반 검색]:', address)
  },
  panToCoords: ({ latitude, longitude }) => {
    if (!latitude || !longitude) return
    const c = new window.kakao.maps.LatLng(parseFloat(latitude), parseFloat(longitude))
    mapInstance.value.panTo(c)
    mapInstance.value.setLevel(5)
    console.log('[좌표 기반 검색]:', latitude, longitude)
  },
})
</script>

<style>
html,
body {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
}
/* CustomOverlay content */
.my-loc-overlay {
  position: relative;
  width: 20px;
  height: 20px;
}

.my-loc-overlay .dot {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 12px;
  height: 12px;
  margin: -6px 0 0 -6px;
  background: #00bb73;
  border: 2px solid white;
  border-radius: 50%;
  z-index: 2;
}

.my-loc-overlay .pulse {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 12px;
  height: 12px;
  margin: -6px 0 0 -6px;
  border-radius: 50%;
  background: rgba(0, 255, 76, 0.3);
  animation: pulse 1.5s ease-out infinite;
  z-index: 1;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 0.8;
  }
  100% {
    transform: scale(8);
    opacity: 0;
  }
}
</style>
