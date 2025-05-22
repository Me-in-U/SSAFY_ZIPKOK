import requests

apiurl = "https://api.vworld.kr/req/address?"
params = {
    "service": "address",
    "request": "getcoord",
    "crs": "epsg:4326",
    "address": "사상구 덕포동 789",
    "format": "json",
    "type": "PARCEL",
    "key": "C8B7CD55-53F3-3E33-9F6D-D977EB3CE170",
}
response = requests.get(apiurl, params=params)
if response.status_code == 200:
    print(response.json())
