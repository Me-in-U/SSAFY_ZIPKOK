let accessToken;
const map = sop.map("map");
// marker 목록
const markers = [];
// 경계 목록
const bounds = [];

// access token 가져오기
const getAccessToken = async () => {
	try {
		const json = await getFetch("https://sgisapi.kostat.go.kr/OpenAPI3/auth/authentication.json", {
			consumer_key: apiKeys.sgisServiceId, // 서비스 id
			consumer_secret: apiKeys.sgisSecurity, // 보안 key
		});
		accessToken = json.result.accessToken;
		console.log(accessToken);
	} catch (e) {
		console.log(e);
	}
};

// 주소를 UTM-K좌표로 변환해서 반환: - json의 errCd ==-401에서 access token 확보!!
const getCoords = async (address) => {
	try {
		console.log(address);
		const json = await getFetch("https://sgisapi.kostat.go.kr/OpenAPI3/addr/geocode.json", {
			accessToken: accessToken,
			address: address,
			resultcount: 1,
		});
		if (json.errCd === -401) {
			await getAccessToken();
			return await getCoords(address);
		}
		return json.result.resultdata[0];
	} catch (e) {
		console.log(e);
	}
};
const infoContainer = document.getElementById("apartment-info");
const updateMap = (infos) => {
	infoContainer.innerHTML = "";
	resetMarker();
	console.log(infos);

	try {
		infos.forEach((info, index) => {
			// 마커 생성 및 지도에 추가
			const marker = sop.marker([info.utmk.x, info.utmk.y]);
			marker.addTo(map).bindInfoWindow(info.label);
			markers.push(marker);
			bounds.push([info.utmk.x, info.utmk.y]);

			// 카드 생성 (Bootstrap col-md-4: 한 행에 3개 배치)
			const colDiv = document.createElement("div");
			colDiv.className = "col-md-4 mb-3";
			colDiv.innerHTML = `
			<div class="card">
			  <div class="card-body">
				<h3 class="card-title">${info.label}</h3>
				<p class="card-text">${info.address}</p>
				<p class="card-text">건축년도: ${info.buildYear}</p>
				<p class="card-text">거래금액: ${info.dealAmount}</p>
				<p class="card-text">층: ${info.floor}</p>
				<p class="card-text">매수자: ${info.buyerGbn}</p>
			  </div>
			</div>
		  `;
			// 카드 클릭 시 해당 info의 좌표로 지도 이동하고, 마커의 정보창을 연다.
			colDiv.addEventListener("click", () => {
				const currentZoom = map.getZoom();
				map.setView(sop.utmk(info.utmk.x, info.utmk.y), currentZoom);
				marker.openInfoWindow();
			});

			infoContainer.appendChild(colDiv);
		});

		// 경계를 기준으로 map을 중앙에 위치하도록 함
		if (bounds.length > 1) {
			map.setView(map._getBoundsCenterZoom(bounds).center, map._getBoundsCenterZoom(bounds).zoom);
		} else {
			map.setView(map._getBoundsCenterZoom(bounds).center, 9);
		}
	} catch (e) {
		console.log(e);
	}
};

// 마커와 경계 초기화
const resetMarker = () => {
	markers.forEach((item) => item.remove());
	bounds.length = 0;
};
