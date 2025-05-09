const sido = document.querySelector("#sido");
const gugun = document.querySelector("#gugun");
const dong = document.querySelector("#dong");

const updateSido = async function () {
	try {
		const json = await getFetch("https://api.vworld.kr/ned/data/admCodeList", {
			format: "json",
			numOfRows: 100,
			key: key_vworld,
			domain: "localhost",
		});
		const infos = json.admVOList.admVOList;
		infos.forEach((item) => {
			(item.key = item.admCode), (item.label = item.lowestAdmCodeNm);
		});
		updateSelect(sido, "시도", infos);
		updateSelect(gugun, "구군");
		updateSelect(dong, "읍면동");
	} catch (error) {
		console.log(error);
	}
};

sido.addEventListener("change", async function () {
	try {
		const json = await getFetch("https://api.vworld.kr/ned/data/admSiList", {
			admCode: sido.value,
			format: "json",
			numOfRows: 100,
			key: key_vworld,
			domain: "localhost",
		});
		const infos = json.admVOList.admVOList;
		infos.forEach((item) => {
			(item.key = item.admCode), (item.label = item.lowestAdmCodeNm);
		});
		updateSelect(gugun, "구군", infos);
		updateSelect(dong, "읍면동");
	} catch (error) {
		console.log(error);
	}
});

gugun.addEventListener("change", async function () {
	try {
		const json = await getFetch("https://api.vworld.kr/ned/data/admDongList", {
			admCode: gugun.value,
			format: "json",
			numOfRows: 100,
			key: key_vworld,
			domain: "localhost",
		});
		const infos = json.admVOList.admVOList;
		infos.forEach((item) => {
			(item.key = item.admCode), (item.label = item.lowestAdmCodeNm);
		});

		updateSelect(dong, "읍면동", infos);
	} catch (error) {
		console.log(error);
	}
});

dong.addEventListener("change", async function () {
	const sidoLabel = sido.selectedOptions[0].text;
	const gugunLabel = gugun.selectedOptions[0].text;
	const dongLabel = dong.selectedOptions[0].text;
	const address = `${sidoLabel} ${gugunLabel} ${dongLabel}`;
	console.log("최종 address: " + address);
	updateMap([{ address: address, utmk: await getCoords(address), label: dongLabel }]);
});

document.querySelector("#btn_apt_search").addEventListener("click", async () => {
	const LAWD_CD = gugun.value; // 현재의 API는 5자리만 사용 - 결국 동 기준으로는 처리할 수 없음
	const DEAL_YMD = document.querySelector("#dealYmd").value;
	console.log(LAWD_CD, DEAL_YMD);

	const data = await getFetch(
		"https://apis.data.go.kr/1613000/RTMSDataSvcAptTrade/getRTMSDataSvcAptTrade",
		{
			LAWD_CD: LAWD_CD,
			DEAL_YMD: DEAL_YMD.replace("-", ""),
			serviceKey: key_data, // 일반 인증키
		},
		true
	);
	const xml = new DOMParser().parseFromString(data, "text/xml");

	const items = xml.getElementsByTagName("item");

	const infos = [];
	for (let item of items) {
		const dong = item.getElementsByTagName("umdNm")[0].textContent.trim();
		const jibun = item.getElementsByTagName("jibun")[0].textContent.trim();
		const aptNm = item.getElementsByTagName("aptNm")[0].textContent.trim();
		const buildYear = item.getElementsByTagName("buildYear")[0].textContent.trim();
		const dealAmount = item.getElementsByTagName("dealAmount")[0].textContent.trim();
		const floor = item.getElementsByTagName("floor")[0].textContent.trim();
		const buyerGbn = item.getElementsByTagName("buyerGbn")[0].textContent.trim();
		// 필요한 경우 다른 정보도 추가 가능 (예: cdealDay, estateAgentSggNm 등)

		const address = `${getLabel(sido)} ${getLabel(gugun)} ${dong} ${jibun} ${aptNm}`;
		infos.push({
			address: address,
			utmk: await getCoords(address),
			label: aptNm,
			buildYear: buildYear,
			dealAmount: dealAmount,
			floor: floor,
			buyerGbn: buyerGbn,
		});
	}
	updateMap(infos);
});
